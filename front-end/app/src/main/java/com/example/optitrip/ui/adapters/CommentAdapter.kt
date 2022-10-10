package com.example.optitrip.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.optitrip.R
import com.example.optitrip.entities.Comment
import com.example.optitrip.entities.TripBasic

/**
 * Builder for Comment adapter for recycler view of [Comment]
 *
 * @param block
 * @receiver
 * @return a new instance of the adapter
 */
fun CommentAdapter(block: CommentAdapter.() -> Unit) : CommentAdapter {
    val instance = CommentAdapter()
    instance.block()
    return instance
}

/**
 * Comment Comment adapter for recycler view of [Comment]
 *
 * @constructor Create empty Comment adapter
 * @property comments list of comments to display
 */
class CommentAdapter : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    /**
     * Comment view holder contains the view for one comment
     *
     * @constructor instantiate the view
     *
     * @param view the view
     * @property usernameTextView  textView displaying the username of the comment's user
     * @property commentTextView textView displaying the comment itself
     */
    inner class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val usernameTextView: TextView = view.findViewById(R.id.tv_comment_username)
        val commentTextView: TextView = view.findViewById(R.id.tv_comment_comment)
    }

    private var comments: List<Comment> = listOf()


    /**
     * On create view holder Create one instance of the [CommentViewHolder]
     *
     * @param parent
     * @param viewType
     * @return
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment_layout, parent, false)
        return CommentViewHolder(view)
    }

    /**
     * On bind view holder links the data from [comments] at [position] to a [CommentViewHolder]
     *
     * @param holder
     * @param position
     */
    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.usernameTextView.text = comment.username
        holder.commentTextView.text = comment.comment
    }

    override fun getItemCount(): Int = comments.size

    /**
     * Update data refreshes the data displayed
     *
     * @param comments the new list of  comments
     */
    fun updateData(comments: List<Comment>) {
        this.comments = comments
        notifyDataSetChanged()
    }

}