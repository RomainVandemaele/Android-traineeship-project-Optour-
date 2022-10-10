package com.example.optitrip.ui.fragments.main

import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.optitrip.R
import com.example.optitrip.databinding.FragmentScheduleBinding
import com.example.optitrip.entities.TripBasic
import com.example.optitrip.entities.trip.Trip
import com.example.optitrip.ui.adapters.PointScheduleAdapter
import com.example.optitrip.utils.Resource
import com.example.optitrip.utils.ScheduleMaker
import com.example.optitrip.ui.viewmodels.MainViewModel
import com.example.optitrip.ui.viewmodels.MainViewModelFactory
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date


/**
 * A simple [Fragment] subclass that represent the schedule part of the app.
 * It allows a user to transform one of its trip into a schedule.
 * By entering how much time passed at each point
 * and export to the calendar app as ics
 *
 */
class ScheduleFragment : Fragment(), AdapterView.OnItemClickListener {

    private lateinit var trips: Array<TripBasic>
    private var trip: Trip? = null
    private lateinit var adapter: PointScheduleAdapter
    private var startTime: LocalDateTime? = null
    private lateinit var binding : FragmentScheduleBinding
    private val viewModel : MainViewModel by activityViewModels() { MainViewModelFactory(requireContext())  }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        this.binding = FragmentScheduleBinding.inflate(inflater,container,false)
        binding.tvScheduleChoosenDate.visibility  = View.INVISIBLE
        // Inflate the layout for this fragment

        getTrips()
        //initSpinners()
        initDatePicker()
        initRecyclerView()
        initObserver()
        initListeners()
        return this.binding.root
    }



    /**
    * Function calling view model to get the trips of the logged user
    */
    private fun getTrips() {
        val clientId = PreferenceManager.getDefaultSharedPreferences(requireContext()).getInt("clientId",0)
        if(clientId != 0) {
            viewModel.getTripsByClient(clientId)
        }else {
            Toast.makeText(requireContext(),getString(R.string.server_error_trips), Toast.LENGTH_SHORT).show()
        }

    }

    private fun initRecyclerView() {
        val recyclerview = this.binding.rcSchedulePoints
        this.adapter = PointScheduleAdapter(requireContext())
        recyclerview.adapter = adapter
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun initObserver() {
        this.viewModel.selectedScheduleTrip.observe(viewLifecycleOwner) {
            if(it.status == Resource.Status.SUCCESS && it.data!=null) {
                this.trip = it.data
                //this.binding.tvScheduleTripName.text = it.data.tripName
                this.adapter.updateData(it.data.points.toTypedArray())
            }else if (it.status == Resource.Status.ERROR) {
                Toast.makeText(requireContext(),getString(R.string.trip_selected_not_found),Toast.LENGTH_SHORT).show()
            }
        }

        this.viewModel.tripsClient.observe(viewLifecycleOwner) {
            if(it.status == Resource.Status.SUCCESS && it.data!=null) {
                this.trips = it.data
                val tripsAdapter = ArrayAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,trips.map { t -> t.tripName })
                tripsAdapter.setDropDownViewResource(androidx.transition.R.layout.support_simple_spinner_dropdown_item)
                (this.binding.spinnerScheduleTrip as? AutoCompleteTextView)?.setAdapter(tripsAdapter)
                (this.binding.spinnerScheduleTrip as? AutoCompleteTextView)?.onItemClickListener = this
                tripsAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun initDatePicker() {

        this.binding.btnScheduleChooseDay.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setCalendarConstraints(
                    CalendarConstraints.Builder()
                        .setValidator(DateValidatorPointForward.now())
                        .build()
                )
                .build()

            datePicker.addOnPositiveButtonClickListener {

                val dateFormat = SimpleDateFormat("dd/MM/yyyy")
                val newDate = dateFormat.format( Date( it))
                val dateInfos = newDate.split("/")
                val startDate =LocalDate.of(dateInfos[2].toInt(),dateInfos[1].toInt(),dateInfos[0].toInt())

                val timePicker = MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(12)
                    .setMinute(0)
                    .setTitleText(getString(R.string.select_start_time))
                    .build()

                timePicker.addOnPositiveButtonClickListener {
                    val time = "${timePicker.hour}:${timePicker.minute}"
                    binding.tvScheduleChoosenDate.visibility  = View.VISIBLE
                    binding.tvScheduleChoosenDate.text = "$newDate $time"
                    this.startTime = startDate.atTime(timePicker.hour,timePicker.minute)
                }

                timePicker.show(requireActivity().supportFragmentManager,"TIME")
            }

            datePicker.show(requireActivity().supportFragmentManager ,"ok")

        }

    }

    /**
     * Initialize listeners
     *
     */
    private fun initListeners() {
        this.binding.btnScheduleCreate.setOnClickListener {

            if(trip == null) {
                Toast.makeText(requireContext(),getString(R.string.select_trip_msg),Toast.LENGTH_SHORT).show()
            }else {
                val N = this.trip?.points!!.size

                if (this.startTime != null && this.adapter.pointTypes.filter { it!="" }.size == N && this.adapter.pointsDuration.filter { it!=-1 }.size == N) {
                    Toast.makeText(requireContext(),"form valid",Toast.LENGTH_SHORT).show()
                    val maker = ScheduleMaker(trip!!, startTime!!,adapter.pointsDuration,adapter.pointTypes,requireContext())
                    maker.generateICS("${trip!!.tripName}.ics")

                }else {
                    Toast.makeText(requireContext(),getString(R.string.all_fields_required),Toast.LENGTH_SHORT).show()
                }

            }

        }
    }




    /**
     * On item click on the spinner for the trip selction
     *
     * @param parent
     * @param p1
     * @param pos
     * @param p3
     */
    override fun onItemClick(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
        if(parent != null) {
            val selectedTripName = parent.getItemAtPosition(pos) as String
            viewModel.getTripById(this.trips[pos].tripId)
        }
    }
}