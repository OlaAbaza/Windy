package com.example.windy.ui.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.windy.R
import com.example.windy.databinding.FavDialogBinding
import com.example.windy.databinding.FavoriteFragmentBinding
import com.example.windy.extensions.action
import com.example.windy.extensions.showCheckNetworkDialog
import com.example.windy.extensions.showSnackbar
import com.example.windy.extensions.toast
import com.example.windy.models.domain.WeatherConditions
import com.example.windy.network.WeatherApiFilter
import com.example.windy.ui.adapter.DailyListAdapter
import com.example.windy.ui.adapter.FavoriteListAdapter
import com.example.windy.ui.adapter.HourlyListAdapter
import com.example.windy.ui.viewModel.FavoriteViewModel
import com.example.windy.util.Constant
import com.example.windy.util.SharedPreferenceUtil
import com.example.windy.util.isConnected
import com.example.windy.util.swipeToDeleteFunction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    @Inject
    lateinit var sharedPreferenceUtil: SharedPreferenceUtil

    private lateinit var favoriteAdapter: FavoriteListAdapter

    private lateinit var binding: FavoriteFragmentBinding

    private val viewModel: FavoriteViewModel by viewModels()
    private val itemTouchHelper by lazy {
        swipeToDeleteFunction({ position ->
            val item = favoriteAdapter.currentList[position]
            viewModel.deleteWeatherItem(item.timezone)
            showSnackBar(item, position)

        }, { position ->
            val item = favoriteAdapter.currentList[position]

            if (isDefaultWeather(item.timezone)) {
                context?.toast(getString(R.string.delete_default_msg))
            }
            isDefaultWeather(item.timezone)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FavoriteFragmentBinding.inflate(inflater)

        val arguments = FavoriteFragmentArgs.fromBundle(requireArguments())
        if (arguments.lat != 0f) {
            if (context?.let { context -> isConnected(context) } == true) {
                viewModel.getWeatherData(
                    arguments.lat,
                    arguments.lon,
                    sharedPreferenceUtil.getLanguage()
                        ?: WeatherApiFilter.ENGLISH.value,
                    sharedPreferenceUtil.getTempUnit()
                        ?: WeatherApiFilter.IMPERIAL.value
                )
            }
        }

        favoriteAdapter = FavoriteListAdapter(FavoriteListAdapter.FavoriteListener {
            viewModel.displayWeatherDetails(it)
        })

        binding.apply {
            lifecycleOwner = this@FavoriteFragment
            favoriteViewModel = viewModel
            itemTouchHelper.attachToRecyclerView(rvFavList)
            rvFavList.adapter = favoriteAdapter
            addBtn.setOnClickListener {
                checkInternetConnection {
                    navigateToMapScreen()
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.navigateToSelectedProperty.collectLatest {
                it?.let {
                    showWeatherDetailsDialog(it)
                    viewModel.doneNavigating()
                }
            }
        }
        return binding.root
    }


    private fun showWeatherDetailsDialog(weatherConditions: WeatherConditions) {
        val dialog = context?.let { Dialog(it) }
        dialog?.setCancelable(false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val lp = WindowManager.LayoutParams()
        // change dialog size
        lp.copyFrom(dialog?.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        // init views
        val bindingDialog = FavDialogBinding.inflate(layoutInflater)
        dialog?.setContentView(bindingDialog.root)
        bindingDialog.apply {
            rvHourly.adapter = HourlyListAdapter()
            rvDaily.adapter = DailyListAdapter()
            layoutFavItem.weatherConditions = weatherConditions
            this.weatherConditions = weatherConditions
            closeBtn.setOnClickListener {
                dialog?.dismiss()
            }
        }

        dialog?.show()
        dialog?.window?.attributes = lp
    }

    private fun showSnackBar(weatherConditions: WeatherConditions, position: Int) {
        binding.favLayout.showSnackbar(R.string.deleted) {
            action(R.string.undo, {
                viewModel.getWeatherData(
                    (weatherConditions.lat).toFloat(),
                    (weatherConditions.lon).toFloat(),
                    sharedPreferenceUtil.getLanguage()
                        ?: WeatherApiFilter.ENGLISH.value,
                    sharedPreferenceUtil.getTempUnit()
                        ?: WeatherApiFilter.IMPERIAL.value
                )
                binding.rvFavList.scrollToPosition(position)
            })
        }
    }

    private fun navigateToMapScreen() {
        this.findNavController()
            .navigate(
                FavoriteFragmentDirections.actionFavoriteFragmentToMapsFragment(
                    Constant.FAVORITE
                )
            )
    }

    private fun checkInternetConnection(onSuccess: () -> Unit) {
        if (context?.let { isConnected(it) } == true)
            onSuccess.invoke()
        else
            requireActivity().showCheckNetworkDialog()
    }

    private fun isDefaultWeather(timeZone: String) = sharedPreferenceUtil.getTimeZone() == timeZone

}