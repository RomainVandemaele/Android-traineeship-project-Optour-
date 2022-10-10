package com.example.optitrip.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.optitrip.R
import com.example.optitrip.entities.trip.Point
import com.example.optitrip.ui.fragments.main.MapsFragment

/**
 * Point adapter to display a list of [Point] in a recyclerView
 *
 * @constructor Create empty Point adapter
 * @property points the list of points
 * @property onClickListener the listener when a click event on the [PointViewHolder]
 * @property onDeleteListener the listener when delete Button of [PointViewHolder] is clicked
 */
class PointAdapter : RecyclerView.Adapter<PointAdapter.PointViewHolder>() {

    private var points: List<Point> = mutableListOf()

    var onClickListener : ((it: Point) -> Unit )? = null
    var onDeleteListener : ((it: Int) -> Unit )? = null


    /**
     * Point view holder the view to display one point
     *
     *
     *
     * @param view
     * @property title the point name
     * @property description the point adress
     * @property descriptionView the view containing [description]
     * @property deleteButton the button to delete the point
     */
    inner class PointViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val title : TextView by lazy { view.findViewById(R.id.et_summary_trip_name) }
        val description : TextView by lazy { view.findViewById(R.id.et_summary_trip_description) }
        val descriptionView : RelativeLayout by lazy { view.findViewById(R.id.expanded_point_view) }
        val deleteButton : Button by lazy { view.findViewById(R.id.btn_point_delete)}

        init {
            view.setOnClickListener( this::onClick )
        }

        override fun onClick(p0: View?) {
            if(descriptionView.isVisible) descriptionView.visibility = View.GONE
            else descriptionView.visibility = View.VISIBLE
        }
    }

    /**
     * On create view holder instantiates a [PointViewHolder] from the corresponding layout
     *
     * @param parent
     * @param viewType
     * @return an instance of [PointViewHolder]
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_point_layout, parent, false)
        return  PointViewHolder(view)
    }



    override fun onBindViewHolder(holder: PointViewHolder, position: Int) {
        holder.title.text = points[position].pointName
        holder.description.text = points[position].address
        holder.descriptionView.visibility = View.GONE

        if(this.itemCount > MapsFragment.MIN_POINT) holder.deleteButton.setOnClickListener { this.onDeleteListener?.invoke(position) }
        else holder.deleteButton.visibility = View.INVISIBLE


    }

    override fun getItemCount(): Int = points.size

    /**
     *  Update data refreshes the data displayed
     *
     * @param points the new lists of points
     */
    fun updateData(points: List<Point>) {
        this.points = points
        this.notifyDataSetChanged()
    }

}

