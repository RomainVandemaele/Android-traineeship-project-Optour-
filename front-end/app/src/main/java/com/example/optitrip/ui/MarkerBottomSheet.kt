package com.example.optitrip.ui

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.optitrip.R
import com.google.android.gms.maps.model.Marker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * BottomSheet displaying info on the marker
 *
 * @property marker the marker
 * @property onStatusChangeListener listener on when the marker change status : start/finish point
 */
class MarkerBottomSheet(private val marker: Marker, val onStatusChangeListener : (Marker, status : String) -> Unit) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sheet_bottom_marker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val descriptionUI = view.findViewById<TextView>(R.id.tv_sheet_marker_description)
        val nameUI = view.findViewById<EditText>(R.id.et_sheet_marker_name)
        val buttonValidateUI = view.findViewById<Button>(R.id.btn_sheet_marker_validate)
        val buttonDeleteUI = view.findViewById<Button>(R.id.btn_sheet_marker_delete)

        val setStart = view.findViewById<Button>(R.id.btn_sheet_marker_start)
        val setEnd = view.findViewById<Button>(R.id.btn_sheet_marker_end)

        nameUI.text =  SpannableStringBuilder(this.marker.title)
        descriptionUI.text = this.marker.snippet


        buttonDeleteUI.setOnClickListener {
            //delete by making it invisible and then filter it to avoid callback
            this.marker.isVisible = false
            this.dismiss()
        }

        buttonValidateUI.setOnClickListener {
            this.marker.title = nameUI.text.toString()
            Toast.makeText(requireActivity(),"name changed",Toast.LENGTH_SHORT).show()
        }

        setStart.setOnClickListener { onStatusChangeListener(marker, START) }
        setEnd.setOnClickListener { onStatusChangeListener(marker, END) }

    }

    companion object {
        const val TAG = "MarkerBottomSheet"
        const val START = "S"
        const val END = "E"
    }
}
