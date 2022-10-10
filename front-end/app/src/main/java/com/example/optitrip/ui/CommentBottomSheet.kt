package com.example.optitrip.ui

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.optitrip.R
import com.example.optitrip.entities.Comment
import com.example.optitrip.entities.CommentDB
import com.example.optitrip.ui.adapters.CommentAdapter
import com.example.optitrip.utils.Resource
import com.example.optitrip.utils.SharedPreference
import com.example.optitrip.utils.hideSoftKeyboard
import com.example.optitrip.ui.viewmodels.MainViewModel
import com.example.optitrip.ui.viewmodels.MainViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * BottomSheet displaying the comment below a trip in [DisplayTripFragment]
 *
 * @property tripId the id of the trip
 * @property adapter adapter to the recylcerview of commentq
 * @property viewModel to get the comments
 */
class CommentBottomSheet(val tripId : Int) : BottomSheetDialogFragment() {


    private lateinit var adapter: CommentAdapter
    private val viewModel : MainViewModel by activityViewModels() { MainViewModelFactory(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.sheet_bottom_comments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        this.initRecyclerview(view)
        this.initListeners(view)
        this.initObservers()
    }

    private fun initRecyclerview(view : View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.rc_sheet_bottom_comments)
        this.adapter = CommentAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewModel.getCommentsByTrip(this.tripId)
    }

    /**
     * initialize buttons listeners like the one to add  a comment
     *
     * @param view
     */
    private fun initListeners(view : View) {

        val commentEditTextLayout = view.findViewById<TextInputLayout>(R.id.et_sheet_bottom_new_comment_layout)
        val commentEditText = view.findViewById<TextInputEditText>(R.id.et_sheet_bottom_new_comment)
        val addButton = view.findViewById<Button>(R.id.btn_sheet_bottom_comments_add)

        //listeners for entering a comment
        val keyListener: (view: View, key: Int, event: KeyEvent) -> Boolean = { _, k, e ->
            if (e.action == KeyEvent.ACTION_DOWN && k == KeyEvent.KEYCODE_ENTER) {
                //hideSoftKeyboard(requireActivity()) TODO  focus nullpointer

                val query = commentEditText.text.toString()
                if(query.isNotEmpty()) {
                    val clientId = PreferenceManager.getDefaultSharedPreferences(requireContext()).getInt(SharedPreference.CLIENT_ID.key,0)
                    val username = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString(SharedPreference.USER_USERNAME.key,"")

                    if(clientId != 0 && username!!.isNotEmpty() ) {
                        viewModel.uploadComment(CommentDB(tripId,clientId,query), username)
                        commentEditTextLayout.visibility = View.INVISIBLE
                    }
                }else {
                    Toast.makeText(requireContext(),getString(R.string.comment_not_empty),Toast.LENGTH_SHORT).show()
                }
                true
            }
            false
        }


        commentEditText.setOnKeyListener(keyListener)
        commentEditTextLayout.visibility = View.INVISIBLE

        //display editText only when ad button is clicked
        addButton.setOnClickListener {
            commentEditTextLayout.visibility = View.VISIBLE
        }

    }

    /**
     * Init observers for when a new comment is created
     *
     */
    private fun initObservers() {
        this.viewModel.comments.observe(viewLifecycleOwner) {
            if(it.status == Resource.Status.SUCCESS && it.data!=null) {
                if(it.data.size > 0) {
                    this.adapter.updateData(it.data.toList())
                }else {
                    this.adapter.updateData(listOf( Comment("No comments yet","")))
                }
            }else if(it.status == Resource.Status.ERROR) {
                this.adapter.updateData(listOf( Comment("No comments yet","")))
            }
        }
    }

    companion object {
        const val TAG = "CommentBottomSheet"
    }
}