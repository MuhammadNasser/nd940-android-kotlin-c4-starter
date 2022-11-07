package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*


// (DONE) TODO: add the map setup implementation
// (DONE) TODO: zoom to the user location after taking his permission
// (DONE) TODO: add style to the map
// (DONE) TODO: put a marker to location that the user selected

// (DONE) TODO: call this function after the user confirms on the selected location

// (DONE) TODO: When the user confirms on the selected location,
//         send back the selected location details to the view model
//         and navigate back to the previous fragment to save the reminder and add the geofence

// (DONE) TODO: Change the map type based on the user's selection.


@Suppress("PrivatePropertyName")
class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    private val TAG = SelectLocationFragment::class.java.simpleName

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var mMap: GoogleMap
    private lateinit var _pointOfInterest: PointOfInterest
    private val client by lazy { LocationServices.getFusedLocationProviderClient(requireActivity()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        binding.confirmButton.setOnClickListener {
            onLocationSelected()
        }

        return binding.root
    }

    private fun onLocationSelected() {
        _viewModel.setPointOfInterest(_pointOfInterest)
        _viewModel.navigationCommand.value = NavigationCommand.Back
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        askForPermissionAndGetCurrentLocation()
        setMapStyle()

        mMap.clear()
        mMap.setOnPoiClickListener { pointOfInterest ->
            mMap.clear()

            this._pointOfInterest = pointOfInterest
            _viewModel.setPointOfInterest(pointOfInterest)
            _viewModel.onLocationSelected()

            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                pointOfInterest.latLng.latitude,
                pointOfInterest.latLng.longitude
            )

            mMap.addMarker(
                MarkerOptions()
                    .position(
                        LatLng(
                            pointOfInterest.latLng.latitude,
                            pointOfInterest.latLng.longitude
                        )
                    )
                    .title(pointOfInterest.name)
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )
        }

        mMap.setOnMapLongClickListener { latLng ->

            mMap.clear()

            val customPoint = PointOfInterest(latLng, "place_id", "place_name")
            this._pointOfInterest = customPoint
            _viewModel.setPointOfInterest(customPoint)
            _viewModel.onLocationSelected()

            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )

            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.dropped_pin))
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
        }
    }

    private fun askForPermissionAndGetCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentUserLocation()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentUserLocation() {

        //get user's current location
        mMap.isMyLocationEnabled = true
        client.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val latlng = LatLng(it.latitude, it.longitude)
                mMap.addMarker(
                    MarkerOptions()
                        .position(latlng)
                        .title(getString(R.string.my_current_location))
                )
            }
        }
    }

    private fun setMapStyle() {
        try {

            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireActivity(),
                    R.raw.map_style
                )
            )

            if (!success) {
                Log.e(TAG, "Style parsing failed")
            }
        } catch (e: Exception) {
            Log.e(TAG, "setMapStyle error: ${e.message}")
        }
    }

    companion object {
        const val REQUEST_LOCATION_PERMISSION = 1001
    }
}