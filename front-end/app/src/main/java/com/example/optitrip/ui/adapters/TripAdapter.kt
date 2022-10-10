package com.example.optitrip.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.optitrip.R
import com.example.optitrip.entities.TripBasic
import com.example.optitrip.entities.trip.Trip

/**
 * Builder for [TripAdapter]
 *
 * @param block an extension function to build thea instance
 * @receiver
 * @return
 */
fun TripAdapter(block: TripAdapter.() -> Unit) : TripAdapter {
    val instance = TripAdapter()
    instance.block()
    return instance
}

/**
 * Trip adapter for recyclerView of [Trip]
 *
 * @constructor Create empty Trip adapter
 * @property trips the list of trips
 * @property onClickListener the listener for a click on [TripViewHolder]
 */
class TripAdapter : RecyclerView.Adapter<TripAdapter.TripViewHolder>() {

    private var trips : List<TripBasic> = listOf()
    var onClickListener: ((TripBasic) -> Unit)? = null

    /**
     * View holder to display a trip
     *
     * @constructor
     *
     * @param view
     */
    inner class TripViewHolder(view : View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val titleTextView : TextView = view.findViewById(R.id.tv_card_trip_title)
        val orgDestTextView : TextView = view.findViewById(R.id.tv_card_trip_origin_dest)

        init {
            view.setOnClickListener(this::onClick)
        }

        override fun onClick(p0: View?) {
            val item = trips[layoutPosition]
            onClickListener.let {
                if (it != null)  it(item)
            }
        }

    }

    /**
     * The new ViewHolder will be used to display items of the adapter using
     * [.onBindViewHolder]. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary [View.findViewById] calls.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return A new ViewHolder that holds a View of the given view type.
     * @see .getItemViewType
     * @see .onBindViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_trip_layout,parent,false)
        return TripViewHolder(view)
    }

    /**
     * Format the trip to get info of origin and destination city of the trip
     *
     * @param trip
     * @return
     */
    private fun getOriginToDest(trip : TripBasic) : String{
        var text = ""
        text += trip.startAddress.split("\n")[1].split(" ")[1] + " --> "
        text += trip.endAddress.split("\n")[1].split(" ")[1]
        return text
    }

    /**
     * On bind view holder links the trip at [position] to a [TripViewHolder]
     *
     * @param holder  [TripViewHolder] of [trips] at [position]
     * @param position the postion in [trips]
     */
    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        holder.titleTextView.text = trips[position].tripName
        holder.orgDestTextView.text = getOriginToDest(trips[position])

    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int = trips.size

    /**
     * Update data refreshes the list displayed
     *
     * @param trips the new list of trips
     */
    fun updateData(trips: List<TripBasic>) {
        this.trips = trips
        notifyDataSetChanged()
    }
}