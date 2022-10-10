package com.example.optitrip.ui.fragments.main

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.optitrip.R
import com.example.optitrip.databinding.FragmentDisplayTripBinding
import com.example.optitrip.entities.trip.Trip
import com.example.optitrip.ui.CommentBottomSheet
import com.example.optitrip.utils.Resource
import com.example.optitrip.ui.viewmodels.MainViewModel
import com.example.optitrip.ui.viewmodels.MainViewModelFactory

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import kotlin.math.roundToInt

/**
 * Display trip fragment : after the creation of a trip or after click on a trip :  the trip is displayed on a map with info about the trip.
 * It also includes lots of actions : delete edit, new trip
 *
 * @constructor Create empty Display trip fragment
 */
class DisplayTripFragment : Fragment() {

    private var polylines: MutableList<String> = mutableListOf()
    private lateinit var binding : FragmentDisplayTripBinding
    private val viewModel : MainViewModel by activityViewModels { MainViewModelFactory(requireContext()) }
    private lateinit var mMap: GoogleMap
    private lateinit var trip : Trip


    /**
     * Callback to the map is ready to initialize everything that needs the map like markers
     */
    private val callback = OnMapReadyCallback { googleMap ->

        this.mMap = googleMap
        this.initObserver()
        this.initListeners()
        if(this.polylines.isNotEmpty()) { //return to fragment with same trip
            this.linkPoints(this.polylines.toTypedArray())
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            restoreSavedPolylines(savedInstanceState)
        }

        //rewrite how the back button works
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            val origin = arguments?.getString("originFragment","")

            viewModel.resetSelectedTrip()
            viewModel.resetCreatedTripId()
            findNavController().popBackStack(R.id.homeFragment,false)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        this.binding = FragmentDisplayTripBinding.inflate(inflater,container,false)
        val origin = arguments?.getString("originFragment","")
        Log.d("ORIGIN", "$origin")
        if(origin == "home") {
            this.binding.btnMapDisplayNew.isClickable = false
            this.binding.btnMapDisplayNew.setTextColor(Color.DKGRAY)
        }
        this.initDisplay()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onSaveInstanceState(outState: Bundle) {

        outState.putInt(KEY_POLYLINES,polylines.size)
        polylines.withIndex().forEach { (i,p) -> outState.putString("$KEY_POLYLINE $i",p) }
        super.onSaveInstanceState(outState)

    }

    /**
     * Restore  saved polylines when fragment is restored
     *
     * @param savedState
     */
    private fun restoreSavedPolylines(savedState : Bundle) {
        if(savedState!=null) {
            val number = savedState.getInt(KEY_POLYLINES)
            for( i in 0 until number) {
                polylines.add( savedState.getString("$KEY_POLYLINE $i",""))
            }

        }
    }

    private fun initObserver() {


        this.viewModel.selectedTrip.observe(viewLifecycleOwner) {
            if(it.status == Resource.Status.SUCCESS && it.data!=null) {
                this.trip = it.data
                //viewModel.getTripDirection(it.data.tripId!!)
                this.displayTrip()
            }else if(it.status == Resource.Status.ERROR) {
                Toast.makeText(requireContext(),getString(R.string.trip_not_found),Toast.LENGTH_SHORT).show()
            }
        }

        this.viewModel.tripPoints.observe(viewLifecycleOwner) {
            if(it.isNotEmpty()) {
                Log.d("OBSERVER","observe link points")
                this.linkPoints(it)
            }
        }

        this.viewModel.deletedTrip.observe(viewLifecycleOwner) {
            if(it.status == Resource.Status.SUCCESS && it.data!=null) {
                viewModel.resetDeletedTrip()
                findNavController().navigate(R.id.action_displayTripFragment_to_homeFragment)
            }else if(it.status == Resource.Status.ERROR) {
                Toast.makeText(requireContext(),getString(R.string.delete_trip_error),Toast.LENGTH_SHORT).show()
                viewModel.resetDeletedTrip()
            }
        }


    }

    /**
     * Initialize buttons listeners
     *
     */
    private fun initListeners() {

        this.binding.btnMapDisplayDelete.setOnClickListener {
            viewModel.deleteTrip(this.trip.tripId!!)
        }

        this.binding.btnMapDisplayModify.setOnClickListener {
            viewModel.resetCreatedTripId()
            viewModel.setPoints(this.trip.points)
            viewModel.setEditTrip(this.trip)
            findNavController().navigate(R.id.action_displayTripFragment_to_mapsFragment)
        }

        val origin = arguments?.getString("originFragment","")
        if(origin != "home") {
            this.binding.btnMapDisplayNew.setOnClickListener {
                viewModel.resetCreatedTripId()
                viewModel.setPoints(mutableListOf())
                findNavController().navigate(R.id.action_displayTripFragment_to_mapsFragment)
            }
        }else {
            this.binding.btnMapDisplayNew.isClickable = false
            this.binding.btnMapDisplayNew.setTextColor(Color.DKGRAY)
            this.binding.btnMapDisplayNew.iconTint = ColorStateList.valueOf(Color.GRAY)

        }


        this.binding.btnMapDisplayComment.setOnClickListener {
            if(::trip.isInitialized) {
                val commentBottomSheet = CommentBottomSheet(this.trip.tripId!!)
                commentBottomSheet.show(requireActivity().supportFragmentManager, CommentBottomSheet.TAG)
            }
        }

    }

    /**
     * Format length in seconds into minutes and hours
     *
     * @param nSecond
     * @return
     */
    private fun secondFormatter(nSecond : Int) : String {
        var min = nSecond / 60
        val hour = min /60
        min %=60
        return if(hour > 0) "${hour}:${min}"
        else "${min}m"
    }

    /**
     * Display trip info like its title and lengths
     */
    private fun displayTrip() {
        this.binding.tvMapDisplayTitle.text = this.trip.tripName
        var totalTime =  this.trip.steps.sumOf { it.stepTime!! }


        var totalLength =  this.trip.steps.sumOf { it.stepLength!! } /1000.0
        if(totalLength >= 5) {
            totalLength = totalLength.roundToInt().toDouble()
        }else {
            totalLength = (totalLength*100.0).roundToInt() / 100.0
        }

        this.binding.tvMapDisplayLength.text = "$totalLength km  ${secondFormatter(totalTime)}"

        mMap.clear()
        Log.d("#points", " ${this.trip.tripName} ,  ${this.trip.points.size}")
        val markers = mutableListOf<Marker?>()
        for( (i,point) in trip.points.withIndex()) {
            val marker = mMap.addMarker(MarkerOptions().position(LatLng(point.latitude!!,point.longitude!!)).title(point.pointName))
            if(i==0 ) marker?.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            else if(i == trip.points.lastIndex) marker?.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            else marker?.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            markers.add(marker)
        }

        if(trip.points[0].placeId == trip.points[trip.points.lastIndex].placeId ) {
            markers[0]?.remove()
            markers[markers.lastIndex]?.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
        }
    }

    /**
     * Init display of the selected trip by calling the [viewModel] to get direction
     *
     */
    private fun initDisplay() {
        val origin = arguments?.getString("originFragment","")
        Log.e("DISPLAY","VM request")
        if( origin!="home" && viewModel.createdTripId.value?.status == Resource.Status.SUCCESS) {
            viewModel.createdTripId.value!!.data?.let { viewModel.getTripDirection(it) }
            viewModel.resetCreatedTripId()
        }
    }




    /**
     * Link points part of a set a polyline to show the path to follow on the map
     *
     * @param polylines a collection of polyline for each step of the trip
     */
    private fun linkPoints(polylines : Array<String>) {
        
        this.polylines = polylines.toMutableList()

        val linesOptions = PolylineOptions().width(8F).color(Color.CYAN).geodesic(true)
        //val pointsLatLnt  = markers.map { m -> LatLng(m!!.position.latitude, m.position.longitude) }.toTypedArray()
        val points : List<LatLng> = polylines.map {  PolyUtil.decode(it) }.flatten()

        Log.d("camera to","${points[0].latitude}  ${points[0].longitude}")

        points.forEach {
            linesOptions.add( it)
        }
        mMap.addPolyline(linesOptions)
        this.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom( LatLng(points[0].latitude,points[0].longitude) ,DEFAULT_ZOOM))
        this.viewModel.resetTripPoints()

    }

    companion object {
        const val DEFAULT_ZOOM  = 12F
        const val KEY_POLYLINES = "POLYLINES NUMBER"
        const val KEY_POLYLINE = "POLYNINE"
    }


}