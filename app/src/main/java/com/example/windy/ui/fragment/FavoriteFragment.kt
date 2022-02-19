package com.example.windy.ui.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.windy.R
import com.example.windy.adapter.DailyListAdapter
import com.example.windy.adapter.FavoriteListAdapter
import com.example.windy.adapter.HourlyListAdapter
import com.example.windy.database.WeatherDatabase
import com.example.windy.databinding.FavDialogBinding
import com.example.windy.databinding.FavoriteFragmentBinding
import com.example.windy.domain.WeatherConditions
import com.example.windy.network.WeatherApiFilter
import com.example.windy.util.Constant
import com.example.windy.util.SharedPreferenceUtil
import com.example.windy.util.isConnected
import com.example.windy.ui.viewModel.FavoriteViewModel
import com.google.android.material.snackbar.Snackbar

class FavoriteFragment : Fragment() {

    private lateinit var favoriteAdapter: FavoriteListAdapter
    private lateinit var binding: FavoriteFragmentBinding
    private val viewModel by lazy {
        val application = requireNotNull(activity).application
        val weatherDatabase = WeatherDatabase.getInstance(application)
        ViewModelProvider(this, FavoriteViewModel.Factory(weatherDatabase)).get(
            FavoriteViewModel::class.java
        )
    }
    private val sharedPreferenceUtil by lazy {
        context?.run { SharedPreferenceUtil(this) }
    }
    private val itemTouchHelper by lazy {
        val itemTouchHelperCallback =
            object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun getSwipeDirs(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ): Int {
                    val position = viewHolder.adapterPosition
                    val item = favoriteAdapter.currentList[position]
                    if (isDefaultWeather(item.timezone)) {
                        Toast.makeText(
                            context,
                            getString(R.string.delete_default_msg),
                            Toast.LENGTH_SHORT
                        ).show()
                        return 0
                    }
                    return super.getSwipeDirs(recyclerView, viewHolder)
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val item = favoriteAdapter.currentList[position]
                    viewModel.deleteWeatherItem(item.timezone)
                    showSnackBar(item, position)

                }

            }
        ItemTouchHelper(itemTouchHelperCallback)
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
                    sharedPreferenceUtil?.getLanguage()
                        ?: WeatherApiFilter.ENGLISH.value,
                    sharedPreferenceUtil?.getTempUnit()
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

        viewModel.navigateToSelectedProperty.observe(viewLifecycleOwner, {
            it?.let {
                showWeatherDetailsDialog(it)
                viewModel.doneNavigating()
            }
        })

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
        Snackbar.make(
            binding.favLayout,
            getString(R.string.deleted),
            Snackbar.LENGTH_LONG
        ).apply {
            setAction(getString(R.string.undo)) {
                viewModel.getWeatherData(
                    (weatherConditions.lat).toFloat(),
                    (weatherConditions.lon).toFloat(),
                    sharedPreferenceUtil?.getLanguage()
                        ?: WeatherApiFilter.ENGLISH.value,
                    sharedPreferenceUtil?.getTempUnit()
                        ?: WeatherApiFilter.IMPERIAL.value
                )
                binding.rvFavList.scrollToPosition(position)
            }
            setTextColor(Color.parseColor(R.color.white.toString()))
            setActionTextColor(Color.parseColor(R.color.purple_200.toString()))
            setBackgroundTint(Color.parseColor(R.color.gray.toString()))
            duration.minus(1)
        }.show()
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
            showCheckNetworkDialog()
    }

    private fun isDefaultWeather(timeZone: String) = sharedPreferenceUtil?.getTimeZone() == timeZone


    private fun showCheckNetworkDialog() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setMessage(getString(R.string.check_internet))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.connect)) { dialog, _ ->
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))

                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                requireActivity().finish()
                dialog.dismiss()
            }
            .show()
    }
}