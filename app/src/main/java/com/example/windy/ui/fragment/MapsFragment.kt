package com.example.windy.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.windy.R
import com.example.windy.databinding.FragmentMapsBinding
import com.example.windy.extensions.makeGone
import com.example.windy.extensions.makeVisible
import com.example.windy.extensions.toast
import com.example.windy.util.Constant
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.net.MalformedURLException
import java.net.URL

class MapsFragment : Fragment() {

    private var googleMap: GoogleMap? = null
    private lateinit var binding: FragmentMapsBinding
    private var parentScreenName: String? = null
    private var tileOverlayTransparent: TileOverlay? = null
    private var tileLayer = "clouds"
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        this.googleMap = googleMap
        googleMap.setOnMapClickListener { point ->
            latitude = point.latitude
            longitude = point.longitude
            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(point))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(point))
        }
        showTileOverlay()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        val arguments = MapsFragmentArgs.fromBundle(requireArguments())
        parentScreenName = arguments.screenName
        mapFragment?.getMapAsync(callback)
        handleViewsVisibility()
        binding.confirmBtn.setOnClickListener {
            if (latitude == 0.0) {
                context?.toast(getString(R.string.choose_place_msg))
            } else {
                if (parentScreenName == Constant.FAVORITE) {
                    this.findNavController().navigate(
                        MapsFragmentDirections.actionMapsFragmentToFavoriteFragment()
                            .apply {
                                lat = latitude.toFloat()
                                lon = longitude.toFloat()
                            })

                } else if (parentScreenName == Constant.SETTINGS) {
                    this.findNavController().navigate(
                        MapsFragmentDirections.actionMapsFragmentToSettingsFragment()
                            .apply {
                                lat = latitude.toFloat()
                                lon = longitude.toFloat()
                            })
                }
            }
        }

        binding.tileTypeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    tileLayer = parent?.getItemAtPosition(position).toString()
                    if (tileLayer.isNotEmpty()) {
                        tileOverlayTransparent?.remove()
                        showTileOverlay()
                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
    }

    private fun handleViewsVisibility() {
        if (parentScreenName == Constant.HOME) {
            binding.confirmBtn.makeGone()
            binding.tileTypeSpinner.makeVisible()
        } else {
            binding.confirmBtn.makeVisible()
            binding.tileTypeSpinner.makeGone()
        }
    }

    private fun showTileOverlay() {
        if (parentScreenName == Constant.HOME) {
            googleMap?.mapType = GoogleMap.MAP_TYPE_HYBRID
            val tileProvider: TileProvider = object : UrlTileProvider(256, 256) {
                @Synchronized
                override fun getTileUrl(x: Int, y: Int, zoom: Int): URL? {
                    val url =
                        "https://tile.openweathermap.org/map/$tileLayer" + "_new/$zoom/$x/$y.png?appid=31cceaa80d19afe5ea2ec0f5b270311b"
                    return try {
                        URL(url)
                    } catch (e: MalformedURLException) {
                        throw AssertionError(e)
                    }
                }

            }
            tileOverlayTransparent =
                googleMap?.addTileOverlay(TileOverlayOptions().tileProvider(tileProvider))

        }
    }

}