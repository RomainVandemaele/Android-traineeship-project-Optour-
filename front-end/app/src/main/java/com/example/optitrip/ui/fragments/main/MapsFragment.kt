package com.example.optitrip.ui.fragments.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.optitrip.R
import com.example.optitrip.databinding.FragmentMapsBinding
import com.example.optitrip.entities.Position
import com.example.optitrip.entities.mapSearch.SearchMapResult
import com.example.optitrip.entities.reverseGeocoding.GeoCodingResult
import com.example.optitrip.entities.trip.Point
import com.example.optitrip.ui.MarkerBottomSheet
import com.example.optitrip.utils.Resource
import com.example.optitrip.utils.hideSoftKeyboard
import com.example.optitrip.ui.viewmodels.MainViewModel
import com.example.optitrip.ui.viewmodels.MainViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


/**
 * [Fragment] [MapsFragment] that represents a map that is the first step inta creating a route and then a schedule
 * It allows markers creation/removal
 *
 * @property mMap the map itself
 * @property locationPermissionGranted indicates if the permission was granted or not
 * @property fusedLocationProviderClient used to find the phone's location
 * @property lastKnownLocation save the last user location
 *
 * @property markers set of markers on the map that represents the point of the trip
 * @property startMarker the marker that start the trip
 * @property endMarker the marker that end the trip can be the same as [startMarker]
 */
class MapsFragment() :
    Fragment() ,
    GoogleMap.OnMarkerClickListener
{

    private lateinit var mMap: GoogleMap
    private var locationPermissionGranted: Boolean = false
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lastKnownLocation: Location? = null

    private lateinit var binding: FragmentMapsBinding
    private val viewModel: MainViewModel by activityViewModels() { MainViewModelFactory(requireContext()) }

    private var markers = mutableListOf<Marker?>()

    private var startMarker : Marker? = null
    private var endMarker : Marker? = null


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
        mMap = googleMap
        mMap.setMinZoomPreference(DEFAULT_MIN_ZOOM)
        val pos = LatLng(DEFAULT_LOCATION.latitude, DEFAULT_LOCATION.longitude)

        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos))
        mMap.moveCamera(CameraUpdateFactory.zoomBy(DEFAULT_ZOOM))
        initListeners()
        initObservers()
        this.fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this.requireActivity())
        this.getLocationPermission()

        if(this.viewModel.points.value!=null) {
            Log.d("RESTORE","viewModels")
            this.putMarkers()
        }
//        else if(this.pos.isNotEmpty()) {
//            Log.d("RESTORE","POS")
//            this.restoreSavedMarkers()
//        }

        Log.d("RESTORE"," markers : ${this.markers.size}")
    }

    /**
     * Initialize observers to react to viewModel chenge
     *
     */
    private fun initObservers() {
        viewModel.geoCoding.observe(viewLifecycleOwner) {
            if(it.status == Resource.Status.SUCCESS && it.data!=null ) {
                this.addMarkerSearch(it.data)
            }else if(it.status == Resource.Status.ERROR) {
                Toast.makeText(requireContext(), getString(R.string.place_not_found), Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.mapSearch.observe(viewLifecycleOwner) {
            if(it.status == Resource.Status.SUCCESS && it.data!=null ) {
                this.centerToSearch(it.data)
            }else if(it.status == Resource.Status.ERROR) {
                Toast.makeText(requireContext(), getString(R.string.place_not_found), Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Restore saved markers for when the user return to the fragment
     *
     */
    private fun putMarkers() {
        val points = this.viewModel.points.value
        this.markers.forEach { it!!.remove() }
        this.markers.clear()
        points!!.forEach {
            val marker =  mMap.addMarker(
                MarkerOptions()
                    .title(it.pointName)
                    .snippet(it.address)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .position(LatLng(it.latitude!!,it.longitude!!)))
            if(marker!=null) {
                marker.tag = it.placeId
                this.markers.add(marker)
            }
        }
        this.startMarker = null
        if(viewModel.tspStartIndex!=-1 && viewModel.tspStartIndex <= markers.lastIndex ) {
            startMarker = markers[viewModel.tspStartIndex]
            this.updateStartEndMarkers(startMarker!!,MarkerBottomSheet.START)
        }
        this.endMarker = null
        if(viewModel.tspEndIndex!=-1 && viewModel.tspEndIndex <= markers.lastIndex) {
            endMarker = markers[viewModel.tspEndIndex]
            this.updateStartEndMarkers(endMarker!!,MarkerBottomSheet.END)
        }

        if(this.markers.isNotEmpty()) mMap.moveCamera(CameraUpdateFactory.newLatLng(this.markers[0]!!.position))

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //MapsInitializer.initialize(requireContext(), MapsInitializer.Renderer.LATEST, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.e("pos", this.markers.size.toString())
        this.binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }



    override fun onSaveInstanceState(outState: Bundle) {
        Log.e("pos", "save")
        val points = markers.map { m -> Point(null,m?.title,m?.position?.latitude,m?.position?.longitude,null,m?.snippet,m?.tag as String) }.toMutableList()
        viewModel.setPoints(points)
        super.onSaveInstanceState(outState)
    }




    /**
     * Separate function to group all listeners of all types : buttons, search bar,...
     *
     */
    private fun initListeners() {

        mMap.setOnMarkerClickListener(this)
        binding.fabMapDone.setOnClickListener {
            val markers = this.markers.filter { it!!.isVisible }
            if(markers.size >= 2) {
                markers.forEach {
                    Log.d("TAG",it?.tag.toString())
                }
                val points = markers.map { m -> Point(null,m?.title,m?.position?.latitude,m?.position?.longitude,null,m?.snippet,m?.tag as String) }.toMutableList()
                viewModel.setPoints(points)
                viewModel.setMarkerIndex(startMarker,endMarker)
                findNavController().navigate(R.id.action_mapsFragment_to_summaryFragment)
            }else {
                Toast.makeText(requireContext(),getString(R.string.min_two_markers),Toast.LENGTH_SHORT).show()
            }
            Log.d("MAP", "click fab")

        }

        val listener: (view: View, key: Int, event: KeyEvent) -> Boolean = { v, k, e ->
            if (e.action == KeyEvent.ACTION_DOWN && k == KeyEvent.KEYCODE_ENTER) {
                hideSoftKeyboard(requireActivity())
                val place = this.binding.etMapSearch.text.toString()
                viewModel.searchMap(place)
                true
            }
            false
        }
        this.binding.etMapSearch.setOnKeyListener(listener)


        this.mMap.setOnMapLongClickListener {
            Toast.makeText(requireContext(),getString(R.string.lookin_point),Toast.LENGTH_SHORT).show()
            viewModel.findInfo(it)
        }

    }

    /**
     * Center the map to the result of a search
     *
     * @param result the result of the search
     */
    private fun centerToSearch(result: SearchMapResult) {
        val lat = result.candidates[0].geometry?.location?.lat
        val long = result.candidates[0].geometry?.location?.lng
        Log.e("PLACE", "center $lat $long")
        if (lat != null && long != null) {
            this.mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lat, long)))
        }

    }


    /**
     * Callback api call to get address from coordinates
     *
     * @param result
     */
    private fun addMarkerSearch(result: GeoCodingResult?) {
        if (result != null && result.results.isNotEmpty()) {

            val point = result.results[0].geometry!!.location!!
            val title = "Point " + (this.markers.size+1).toString()
            val snippet = result.results[0].addressComponents.map { c -> c.shortName }
                .dropLastWhile { it!!.length >= 3 }.toMutableList()
            snippet.add(2, "\n")
            val placeId = result.results[0].placeId


            val marker: Marker? = this.mMap.addMarker(
                MarkerOptions().position(LatLng(point.lat!!, point.lng!!)).title(title)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .snippet(snippet.joinToString(" "))
            )

            if (marker != null) {
                marker.tag = placeId
                //this.positions[marker] = result
                this.markers.add(marker)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentPosition() {
        if (locationPermissionGranted) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                //Log.e("CURRENT POS", "success ${it.latitude} ${it.longitude}")
                lastKnownLocation = it
                this.centerToPos()
            }.addOnFailureListener {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(LatLng(DEFAULT_LOCATION.latitude,
                    DEFAULT_LOCATION.longitude)))
                Log.e("CURRENT POS", "failure")
            }
        }else {
            mMap.animateCamera(CameraUpdateFactory.newLatLng(LatLng(DEFAULT_LOCATION.latitude,
                DEFAULT_LOCATION.longitude)))
        }
    }

    /**
     * Center map to position of the user
     *
     */
    private fun centerToPos() {
        if (lastKnownLocation != null) {
            mMap.moveCamera(
                CameraUpdateFactory.newLatLng(
                    LatLng(
                        lastKnownLocation!!.latitude,
                        lastKnownLocation!!.longitude
                    )
                )
            )
        } else {
            val pos = LatLng(DEFAULT_LOCATION.latitude, DEFAULT_LOCATION.longitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(pos))
        }

    }

    @SuppressLint("MissingPermission")
    private fun updateLocationUI() {
        if (mMap == null) return

        try {
            mMap.setMinZoomPreference(2.5F)
            if (locationPermissionGranted) {
                mMap.isMyLocationEnabled = true
                mMap.uiSettings.isMyLocationButtonEnabled = true
            } else {
                mMap.isMyLocationEnabled = false
                mMap.uiSettings.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("EXCEPTION: %s", e.message, e)
        }
    }


    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            this.locationPermissionGranted = true
            this.updateLocationUI()
            this.getCurrentPosition()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                    this.updateLocationUI()
                    this.getCurrentPosition()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    /**
     * On marker click listener that display bottom sheet
     *
     * @param marker the marker clicked on
     * @return
     */
    override fun onMarkerClick(marker: Marker): Boolean {
        val bottomSheet = MarkerBottomSheet(marker,this::updateStartEndMarkers)
        bottomSheet.show(requireActivity().supportFragmentManager, MarkerBottomSheet.TAG)
        return true
    }


    /**
     * Companion
     *
     * @constructor Create empty Companion
     */
    companion object {
        const val KEY_LOCATION = "location"
        const val POSITIONS_NUMBER = "pos number"
        const val POSITION = "position"
        val DEFAULT_LOCATION = LatLng(50.8163647,4.4070792)
        const val DEFAULT_ZOOM = 7.5F
        const val DEFAULT_MIN_ZOOM = 10.5F
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101
        const val MIN_POINT = 2
        const val MAX_POINT = 10
    }

    private fun updateStartEndMarkers(marker: Marker, status : String) {
        val oldStart = this.startMarker
        val oldEnd = this.endMarker
        startMarker = if(status == MarkerBottomSheet.START) marker else startMarker
        endMarker = if(status == MarkerBottomSheet.END) marker else endMarker

        oldStart?.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        oldEnd?.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))

        if(startMarker === endMarker) {
            startMarker?.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
        }else {
            startMarker?.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            endMarker?.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        }

    }


}



