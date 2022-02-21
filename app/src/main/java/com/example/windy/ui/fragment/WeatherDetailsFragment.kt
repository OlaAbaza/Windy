package com.example.windy.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.windy.R.*
import com.example.windy.databinding.WeatherDetailsLayoutBinding
import com.example.windy.extensions.makeGone
import com.example.windy.extensions.makeVisible
import com.example.windy.extensions.showCheckNetworkDialog
import com.example.windy.models.domain.Alert
import com.example.windy.models.domain.CurrentLocation
import com.example.windy.network.WeatherApiFilter
import com.example.windy.ui.adapter.DailyListAdapter
import com.example.windy.ui.adapter.HourlyListAdapter
import com.example.windy.ui.viewModel.WeatherDetailsViewModel
import com.example.windy.util.*
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.*
import javax.inject.Inject


private const val PERMISSION_ID = 42

@AndroidEntryPoint
//create a dependencies container that is attached to the fragment
class WeatherDetailsFragment : Fragment() {

    @Inject
    lateinit var sharedPreferenceUtil: SharedPreferenceUtil

    @Inject
    lateinit var hourlyListAdapter: HourlyListAdapter

    @Inject
    lateinit var dailyListAdapter: DailyListAdapter

    @Inject
    lateinit var notificationUtilsUtils: NotificationUtils

    private lateinit var binding: WeatherDetailsLayoutBinding

    private var getCurrentLocation = MutableLiveData<CurrentLocation?>()

    private val viewModel: WeatherDetailsViewModel by viewModels()


    private val mFusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this.requireActivity())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPreferenceUtil.saveIsFirstTimeLunch(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = WeatherDetailsLayoutBinding.inflate(inflater)

        startLoading()

        binding.weatherDetailsLayout.apply {
            layoutWeatherDetailsContent.apply {
                lifecycleOwner = this@WeatherDetailsFragment
                rvHourly.adapter = hourlyListAdapter
                rvDaily.adapter = dailyListAdapter
            }
            lifecycleOwner = this@WeatherDetailsFragment
            swipeRefreshLayout.setOnRefreshListener {
                getDefaultWeatherData()
            }
        }

        handleNavigation()
        addObservers()

        if (sharedPreferenceUtil.getIsFirstTimeLunch()) {
            sharedPreferenceUtil.saveIsFirstTimeLunch(false)
            getDefaultWeatherData()
        }
        if (sharedPreferenceUtil.getIsLocationNeedUpdate()) {
            getDefaultWeatherData()
            sharedPreferenceUtil.saveIsLocationNeedUpdate(false)
        }
        updateAllData()

        return binding.root
    }

    private fun startLoading() {
        binding.apply {
            loadingLayout.makeVisible()
            weatherDetailsLayout.root.makeGone()
        }

    }

    private fun stopLoading() {
        binding.apply {
            loadingLayout.visibility = View.GONE
            weatherDetailsLayout.root.visibility = View.VISIBLE
        }

    }

    private fun getDefaultWeatherData() {
        if (sharedPreferenceUtil.getIsUseDeviceLocation() && context?.let { isConnected(it) } == true
        ) {
            getLastLocation()

        } else {
            sharedPreferenceUtil.getTimeZone()?.let { viewModel.getObjByTimezone(it) }
        }
    }

    private fun handleNavigation() {
        binding.weatherDetailsLayout.apply {
            menuAlarm.setOnClickListener {
                this@WeatherDetailsFragment.findNavController().navigate(
                    WeatherDetailsFragmentDirections.actionWeatherDetailsFragmentToAlarmFragment()
                )

            }
            menuFav.setOnClickListener {
                this@WeatherDetailsFragment.findNavController().navigate(
                    WeatherDetailsFragmentDirections.actionWeatherDetailsFragmentToFavoriteFragment()
                )
            }
            menuSetting.setOnClickListener {
                checkInternetConnection {
                    this@WeatherDetailsFragment.findNavController().navigate(
                        WeatherDetailsFragmentDirections.actionWeatherDetailsFragmentToSettingsFragment()
                    )
                }

            }
            menuMap.setOnClickListener {
                checkInternetConnection {
                    this@WeatherDetailsFragment.findNavController().navigate(
                        WeatherDetailsFragmentDirections.actionWeatherDetailsFragmentToMapsFragment(
                            Constant.HOME
                        )
                    )
                }
            }
        }
    }

    private fun checkInternetConnection(onSuccess: () -> Unit) {
        if (context?.let { isConnected(it) } == true)
            onSuccess.invoke()
        else
            requireActivity().showCheckNetworkDialog()
    }

    private fun addObservers() {
        getCurrentLocation.observe(viewLifecycleOwner, { currentLocation ->
            currentLocation?.let {
                if (context?.let { context -> isConnected(context) } == true) {
                    viewModel.getWeatherData(
                        it.latitude,
                        it.longitude,
                        sharedPreferenceUtil.getLanguage() ?: WeatherApiFilter.ENGLISH.value,
                        sharedPreferenceUtil.getTempUnit() ?: WeatherApiFilter.IMPERIAL.value
                    )
                }
                getCurrentLocation.value = null
            }
        })
        lifecycleScope.launchWhenStarted {
            viewModel.getDefaultWeatherCondition.collectLatest { response ->
                when (response) {
                    is Resource.Success -> {
                        stopLoading()

                        response.data.apply item@{
                            sharedPreferenceUtil.saveTimeZone(timezone)
                            binding.weatherDetailsLayout.apply {
                                weatherConditionItem = this@item
                                layoutWeatherDetailsContent.weatherConditionItem = this@item
                                swipeRefreshLayout.isRefreshing = false
                                if (!(alerts.isNullOrEmpty()))
                                    notifyUser(alerts)
                            }
                        }
                    }
                    is Resource.Loading -> {
                        startLoading()
                    }
                    is Resource.Error -> {
                        stopLoading()
//                        response.message.let { message ->
//                            binding.progressBar.hide()
//                            context?.toast(message.toString())
//                        }
                    }
                }


            }
        }
    }

    private fun notifyUser(alert: List<Alert>) {
        notificationUtilsUtils.sendNotification(
            alert[0].event ?: "",
            alert[0].startTime + "," + alert[0].endTime +
                    "\n" + alert[0].description,
            isAlarmSound = false,
            isCancelable = true,
            notificationId = Constant.WEATHER_ALERT_NOTIFICATION_ID
        )
    }

    private fun updateAllData() {
        if (sharedPreferenceUtil.getIsDataNeedUpdate() && context?.let { isConnected(it) } == true) {
            viewModel.updateAllWeatherData(
                sharedPreferenceUtil.getLanguage() ?: WeatherApiFilter.ENGLISH.value,
                sharedPreferenceUtil.getTempUnit() ?: WeatherApiFilter.IMPERIAL.value
            )
            sharedPreferenceUtil.saveIsDataNeedUpdate(false)
        }
    }

    ////////////////////////current location///////////////////////////////
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this.requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        getCurrentLocation.value =
                            CurrentLocation(
                                location.latitude,
                                location.longitude
                            )
                    }
                }
            } else {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient?.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            getCurrentLocation.value =
                CurrentLocation(
                    mLastLocation.latitude,
                    mLastLocation.longitude
                )
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            this.requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this.requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }
}