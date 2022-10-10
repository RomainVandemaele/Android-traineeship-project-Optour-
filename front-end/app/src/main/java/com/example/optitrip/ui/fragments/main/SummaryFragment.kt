package com.example.optitrip.ui.fragments.main

import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.optitrip.R
import com.example.optitrip.databinding.FragmentSummaryBinding
import com.example.optitrip.entities.trip.Point
import com.example.optitrip.ui.adapters.PointAdapter
import com.example.optitrip.utils.Resource
import com.example.optitrip.utils.enums.Mode
import com.example.optitrip.utils.forms.TripForm
import com.example.optitrip.ui.viewmodels.MainViewModel
import com.example.optitrip.ui.viewmodels.MainViewModelFactory

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 * Use the [SummaryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SummaryFragment() : Fragment(), AdapterView.OnItemClickListener {

    private var selectedMode: String? = null
    private lateinit var binding: FragmentSummaryBinding
    private val viewModel : MainViewModel by activityViewModels() { MainViewModelFactory(requireContext()) }

    private var points : MutableList<Point>? = null
    private lateinit var adapter : PointAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        points = viewModel.points.value?.toMutableList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.binding = FragmentSummaryBinding.inflate(inflater,container,false)
        this.binding.progressSummary.visibility = View.INVISIBLE
        if(viewModel.editTrip.value != null) {
            this.binding.etSummaryTripName.text = SpannableStringBuilder( viewModel.editTrip.value!!.tripName)
        }

        initRecyclerView()
        initListener()
        initObserver()
        initSpinner()
        return binding.root
    }

    private fun initRecyclerView() {
        adapter = PointAdapter()
        adapter.onDeleteListener = viewModel::deletePoint
        this.binding.rcSummaryPoints.adapter = adapter
        this.binding.rcSummaryPoints.layoutManager =  LinearLayoutManager(requireContext())
    }

    /**
     * Initialize view listeners like buttons click listener
     *
     */
    private fun initListener() {
        this.binding.btnSummaryCancel.setOnClickListener {
            findNavController().popBackStack()
            //this.callback.cancel()
        }

        this.binding.btnSummaryCreate.setOnClickListener {
            val name = binding.etSummaryTripName.text.toString()
            val form = TripForm(name,this.selectedMode)
            if(form.isValid) {
                //Toast.makeText(requireContext(),getString(R.string.wait),Toast.LENGTH_SHORT).show()
                viewModel.createTrip(this.points!!,name,this.selectedMode!!)
                //this.binding.progressSummary.visibility = View.VISIBLE
            }else {
                Toast.makeText(requireContext(),getString(R.string.all_fields_required),Toast.LENGTH_SHORT).show()
            }

        }
    }

    /**
     * Initialize observers
     *
     */
    private fun initObserver() {
        viewModel.points.observe(viewLifecycleOwner) {
            Log.e("observer","observe")
            this.points = it.toMutableList()
            adapter.updateData(this.points!!)
        }

        viewModel.createdTripId.observe(viewLifecycleOwner) {
            Log.e("observer","observe createdTrip")
            if(it.status == Resource.Status.ERROR) {
                Toast.makeText(requireContext(),getString(R.string.server_error),Toast.LENGTH_SHORT).show()
            }else if( it.status == Resource.Status.SUCCESS) {
                this.binding.progressSummary.visibility = View.INVISIBLE
                findNavController().navigate(R.id.action_summaryFragment_to_displayTripFragment)
                //callback.displayTrip()
            }

        }
    }

    /**
     * Initialize the spinner for choosing the mode of transport
     *
     */
    private fun initSpinner() {
        val spinValues = Mode.values().map { v -> v.mode }
        val spinAdapter = ArrayAdapter<String>(requireContext(),android.R.layout.simple_spinner_dropdown_item,spinValues)
        spinAdapter.setDropDownViewResource(androidx.transition.R.layout.support_simple_spinner_dropdown_item)

        (binding.spinnerTripMode as? AutoCompleteTextView)?.setAdapter(spinAdapter)
        (binding.spinnerTripMode as? AutoCompleteTextView)?.onItemClickListener = this
    }



    /**
     * On item click on the spinner selectionning the mode of transport
     *
     * @param parent the parent view
     * @param p1
     * @param pos the position of selected item
     * @param p3
     */
    override fun onItemClick(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
        if(parent != null) {
            this.selectedMode = parent.getItemAtPosition(pos) as String
            Log.e("MODE","$selectedMode")
        }
    }

}