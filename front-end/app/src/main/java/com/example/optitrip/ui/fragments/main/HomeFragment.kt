package com.example.optitrip.ui.fragments.main

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.optitrip.R
import com.example.optitrip.databinding.FragmentHomeBinding
import com.example.optitrip.entities.TripBasic
import com.example.optitrip.ui.adapters.TripAdapter
import com.example.optitrip.utils.Resource
import com.example.optitrip.utils.hideSoftKeyboard
import com.example.optitrip.ui.viewmodels.MainViewModel
import com.example.optitrip.ui.viewmodels.MainViewModelFactory


/**
 * A simple [Fragment] subclass that is [HomeFragment]. Home screen for the app.. it displays a list of trips either from the user or from all users
 *
 * @property homeTrip it is the list of trips of the user
 * @property adapter to the [RecyclerView]
 * @property viewModel the VM used to access and modify data
 *
 */
class HomeFragment : Fragment() {

    private var homeTrip: Array<TripBasic> = arrayOf()

    private lateinit var adapter: TripAdapter
    private lateinit var binding : FragmentHomeBinding
    private val viewModel : MainViewModel by activityViewModels() { MainViewModelFactory(requireContext()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.binding = FragmentHomeBinding.inflate(inflater,container,false)

        //initialize everything needed
        this.getTrips()
        this.initRecyclerView()
        this.initListeners()
        this.initObservers()
        return binding.root
    }

    /**
     * Function : Initialize listeners for search bar
     *
     */
    private fun initListeners() {
        val listener: (view: View, key: Int, event: KeyEvent) -> Boolean = { v, k, e ->
            if (e.action == KeyEvent.ACTION_DOWN && k == KeyEvent.KEYCODE_ENTER) {
                hideSoftKeyboard(requireActivity())
                val query = this.binding.etTripSearch.text.toString()
                if(query.isEmpty()) {
                    adapter.updateData(this.homeTrip.toList())
                }else this.getTripsSearch(query)
                true
            }
            false
        }
        this.binding.etTripSearch.setOnKeyListener(listener)
    }

    /**
     * Initialize observers for [viewModel] [LiveData] ans reacts accordingly
     *
     */
    private fun initObservers() {
        this.viewModel.tripsClient.observe(viewLifecycleOwner) {
            if(it.status == Resource.Status.SUCCESS) {
                Log.d("HOME","GOT request trips")
                this.adapter.updateData(it.data!!.toList())
                this.homeTrip = it.data!!
            }else if(it.status == Resource.Status.ERROR) {
                Toast.makeText(requireContext(),getString(R.string.trips_not_found),Toast.LENGTH_SHORT).show()
            }
        }

        this.viewModel.tripsSearch.observe(viewLifecycleOwner) {
            if(it.status == Resource.Status.SUCCESS) {
                this.adapter.updateData(it.data!!.toList())
            }else if(it.status == Resource.Status.ERROR) {
                Toast.makeText(requireContext(),getString(R.string.trips_search_not_found),Toast.LENGTH_SHORT).show()
            }
        }
    }


    /**
     * Initialize recycler view for the trips
     *
     */
    private fun initRecyclerView() {
        this.adapter = TripAdapter {
            onClickListener = { goToDetail(it) }
        }
        this.binding.rcHomeTrips.adapter = this.adapter
        this.binding.rcHomeTrips.layoutManager = LinearLayoutManager(requireContext())
    }


    /**
     * Go to [displayTripFragment] to display selected trip
     *
     * @param trip the selected trip
     */
    private fun goToDetail(trip : TripBasic) {
        viewModel.getTripDirection(trip.tripId)
        val bundle = bundleOf("origin" to "HOME")
        findNavController().navigate(R.id.action_homeFragment_to_displayTripFragment,bundle)
    }

    /**
     * Function calling view model to get the trips of the logged user
     *
     */
    private fun getTrips() {
        val clientId = PreferenceManager.getDefaultSharedPreferences(requireContext()).getInt("clientId",0)
        if(clientId != 0) {
            Log.d("HOME","launch request trips")
            viewModel.getTripsByClient(clientId)
        }else {
            Toast.makeText(requireContext(),getString(R.string.server_error_trips),Toast.LENGTH_SHORT).show()
        }

    }

    /**
     * Function calling view model to get the trips from search
     *
     */
    private fun getTripsSearch(query : String) {
        viewModel.getTripBySearch(query)
    }


}