package com.example.windy.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.windy.R.*
import com.example.windy.adapter.DailyListAdapter
import com.example.windy.adapter.HourlyListAdapter
import com.example.windy.database.WeatherDatabase
import com.example.windy.databinding.WeatherDetailsLayoutBinding
import com.example.windy.domain.CurrentLocation
import com.example.windy.network.WeatherApiFilter
import com.example.windy.ui.viewModel.WeatherDetailsViewModel
import com.example.windy.util.Constant
import com.example.windy.util.SharedPreferenceUtil
import com.example.windy.util.isConnected
import com.google.android.gms.location.*
import java.util.*


private const val PERMISSION_ID = 42

class WeatherDetailsFragment : Fragment() {

    private val viewModel by lazy {
        val application = requireNotNull(activity).application
        val weatherDatabase = WeatherDatabase.getInstance(application)
        ViewModelProvider(this, WeatherDetailsViewModel.Factory(weatherDatabase)).get(
            WeatherDetailsViewModel::class.java
        )
    }
    private lateinit var binding: WeatherDetailsLayoutBinding
    private val sharedPreferenceUtil by lazy {
        context?.run { SharedPreferenceUtil(this) }
    }
    private var getCurrentLocation = MutableLiveData<CurrentLocation?>()
    private val mFusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this.requireActivity())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPreferenceUtil?.saveIsFirstTimeLunch(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val hourlyListAdapter = HourlyListAdapter()
        val dailyListAdapter = DailyListAdapter()

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

        if (sharedPreferenceUtil?.getIsFirstTimeLunch() == true) {
            sharedPreferenceUtil?.saveIsFirstTimeLunch(false)
            getDefaultWeatherData()
        }
        if (sharedPreferenceUtil?.getIsLocationNeedUpdate() == true) {
            getDefaultWeatherData()
            sharedPreferenceUtil?.saveIsLocationNeedUpdate(false)
        }
        updateAllData()

        return binding.root
    }

    private fun startLoading() {
        binding.apply {
            loadingLayout.visibility = View.VISIBLE
            weatherDetailsLayout.root.visibility = View.GONE
        }

    }

    private fun stopLoading() {
        binding.apply {
            loadingLayout.visibility = View.GONE
            weatherDetailsLayout.root.visibility = View.VISIBLE
        }

    }

    private fun getDefaultWeatherData() {
        if (sharedPreferenceUtil?.getIsUseDeviceLocation() == true
            && context?.let { isConnected(it) } == true
        ) {
            getLastLocation()

        } else {
            sharedPreferenceUtil?.getTimeZone()?.let { viewModel.getObjByTimezone(it) }
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
            showCheckNetworkDialog()
    }


    private fun showCheckNetworkDialog() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setMessage(getString(string.check_internet))
            .setCancelable(false)
            .setPositiveButton(getString(string.connect)) { dialog, _ ->
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))

                dialog.dismiss()
            }
            .setNegativeButton(getString(string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun addObservers() {
        getCurrentLocation.observe(viewLifecycleOwner, { currentLocation ->
            currentLocation?.let {
                if (context?.let { context -> isConnected(context) } == true) {
                    viewModel.getWeatherData(
                        it.latitude,
                        it.longitude,
                        sharedPreferenceUtil?.getLanguage() ?: WeatherApiFilter.ENGLISH.value,
                        sharedPreferenceUtil?.getTempUnit() ?: WeatherApiFilter.IMPERIAL.value
                    )
                }
                getCurrentLocation.value = null
            }
        })
        viewModel.getDefaultWeatherCondition.observe(viewLifecycleOwner, { item ->
            item?.let {
                sharedPreferenceUtil?.saveTimeZone(it.timezone)
                binding.weatherDetailsLayout.apply {
                    weatherConditionItem = it
                    layoutWeatherDetailsContent.weatherConditionItem = it
                    swipeRefreshLayout.isRefreshing = false
                }
                stopLoading()

            }
        })
    }

    private fun updateAllData() {
        if (sharedPreferenceUtil?.getIsDataNeedUpdate() == true && context?.let { isConnected(it) } == true) {
            viewModel.updateAllWeatherData(
                sharedPreferenceUtil?.getLanguage() ?: WeatherApiFilter.ENGLISH.value,
                sharedPreferenceUtil?.getTempUnit() ?: WeatherApiFilter.IMPERIAL.value
            )
            // activity?.recreate()
            sharedPreferenceUtil?.saveIsDataNeedUpdate(false)
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
////////////////////////////////////////////////////////

}