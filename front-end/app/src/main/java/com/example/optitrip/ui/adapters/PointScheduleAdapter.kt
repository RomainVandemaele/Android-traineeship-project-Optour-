package com.example.optitrip.ui.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.optitrip.R
import com.example.optitrip.entities.trip.Point
import com.example.optitrip.ui.adapters.TripAdapter.TripViewHolder

/**
 * Point schedule adapter for  recycler view of [Point] in [ScheduleFragment]
 *
 * @property context the fragment context
 * @property points the list of points to display
 * @property pointsDuration the list of selected durations
 * @property pointTypes the list of selected types
 * @property types the types that the user can choose
 * @constructor Create empty Point schedule adapter
 */
class PointScheduleAdapter(val context : Context) : RecyclerView.Adapter<PointScheduleAdapter.PointScheduleViewHolder>() {

    /**
     * Point schedule view holder displaying one point in [ScheduleFragment]
     *
     * @constructor instantiate the view
     *
     * @param view
     * @property nameTextView the point name
     * @property durationEditText the edit text indicating duration of the stop
     * @property spinnerType spinner to choose type of stop
     */
    inner class PointScheduleViewHolder(view : View) : RecyclerView.ViewHolder(view),
        AdapterView.OnItemClickListener {

        val nameTextView  : TextView = view.findViewById<TextView>(R.id.tv_item_point_schedule_name)
        val durationEditText  : EditText = view.findViewById<EditText>(R.id.et_item_point_schedule_duration)
        val spinnerType: AutoCompleteTextView = view.findViewById<AutoCompleteTextView>(R.id.spinner_schedule_point_type)

        init {
            spinnerType.onItemClickListener = this
        }

        override fun onItemClick(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
            val selection = parent?.getItemAtPosition(pos) as String?
            if( selection != null) pointTypes[layoutPosition] = selection
            Log.d("SELECT", "$layoutPosition $selection")
        }
    }

    private var points : Array<Point> = arrayOf()
    var pointsDuration : Array<Int> = arrayOf()
    var pointTypes : Array<String> = arrayOf()
    private val types = arrayOf("BREAK", "VISIT", "EAT", "STORE")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointScheduleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_point_schedule_layout,parent,false)
        return PointScheduleViewHolder(view)
    }

    /**
     * On bind view holder links the trip at [position] to a [PointScheduleViewHolder]
     *
     * @param holder  [PointScheduleViewHolder] of [points] at [position]
     * @param position the position in [points]
     */
    override fun onBindViewHolder(holder: PointScheduleViewHolder, position: Int) {
        val item = this.points[position]
        holder.nameTextView.text = item.pointName
        initSpinner(holder)

        holder.durationEditText.addTextChangedListener {
            try {
                if(!it.isNullOrBlank()) pointsDuration[position] = it.toString().toInt()
                else pointsDuration[position] = -1
            }catch (e : Exception) {
                Toast.makeText(context,context.getString(R.string.wrong_format_duration),Toast.LENGTH_SHORT).show()
            }

        }

    }

    override fun getItemCount(): Int = this.points.size

    /**
     * Init spinner to choose type of trip stop
     *
     * @param holder the [PointScheduleViewHolder] for the point
     */
    private fun initSpinner(holder: PointScheduleViewHolder) {
        val adapter = ArrayAdapter<String>(context , androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,types)
        adapter.setDropDownViewResource(androidx.transition.R.layout.support_simple_spinner_dropdown_item)
        holder.spinnerType.setAdapter(adapter)


    }

    /**
     *  Update data refreshes the data displayed
     *
     * @param points the new list of points
     */
    fun updateData( points : Array<Point>) {
        this.points = points
        pointsDuration = Array<Int>(points.size) {-1}
        pointTypes = Array<String>(points.size) {""}
        this.notifyDataSetChanged()
    }




}