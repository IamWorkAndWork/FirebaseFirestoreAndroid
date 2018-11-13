package app.practice.rndb.Adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import app.practice.rndb.Model.Comment
import app.practice.rndb.R
import java.text.SimpleDateFormat
import java.util.*

class CommentAdapter(val comments: ArrayList<Comment>, val itemClick: (Comment) -> Unit) :
    RecyclerView.Adapter<CommentAdapter.ViewHolder>() {


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0?.context).inflate(R.layout.comment_list_view, p0, false);
        return ViewHolder(view, itemClick);
    }

    override fun getItemCount(): Int {
        return comments.count();
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.bindThought(comments[pos])
    }


    inner class ViewHolder(itemView: View, val itemClick: (Comment) -> Unit) : RecyclerView.ViewHolder(itemView) {
        //
        val username = itemView?.findViewById<TextView>(R.id.commentListUsernameTxt);
        val timestamp = itemView?.findViewById<TextView>(R.id.commentListTimestampTxt);
        val commentTxt = itemView?.findViewById<TextView>(R.id.commentListCommentTxt);

        fun bindThought(comment: Comment) {
            username?.text = comment.username;
            commentTxt?.text = comment.commentTxt;

            val dateFormat = SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.getDefault());
            timestamp?.text = dateFormat.format(comment.timeStamp);

        }

    }
}