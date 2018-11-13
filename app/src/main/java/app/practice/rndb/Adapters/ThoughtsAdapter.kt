package app.practice.rndb.Adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import app.practice.rndb.Model.Thought
import app.practice.rndb.R
import app.practice.rndb.Utilities.NUM_LIKES
import app.practice.rndb.Utilities.THOUGHTS_REF
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ThoughtsAdapter(val thoughts: ArrayList<Thought>, val itemClick: (Thought) -> Unit) :
    RecyclerView.Adapter<ThoughtsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0?.context).inflate(R.layout.thought_list_view, p0, false);
        return ViewHolder(view, itemClick);
    }

    override fun getItemCount(): Int {
        return thoughts.count();
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.bindThought(thoughts[pos])
    }


    inner class ViewHolder(itemView: View, val itemClick: (Thought) -> Unit) : RecyclerView.ViewHolder(itemView) {

        val username = itemView?.findViewById<TextView>(R.id.listviewUsernameTxt);
        val timestamp = itemView?.findViewById<TextView>(R.id.listviewTimeStampTxt);
        val thoughtTxt = itemView?.findViewById<TextView>(R.id.listviewThoughtTxt);
        val numLikes = itemView?.findViewById<TextView>(R.id.listviewNumLikesLbl);
        val likesImage = itemView?.findViewById<ImageView>(R.id.listviewLikeImg);
        val listviewDeleteBtn = itemView?.findViewById<Button>(R.id.listviewDeleteBtn);
        val numComments = itemView?.findViewById<TextView>(R.id.listviewNumCommentTxt);

        fun bindThought(thought: Thought) {
            username?.text = thought.username;
            thoughtTxt?.text = thought.thoughtTxt;
            numLikes?.text = thought.numLikes.toString() + " , ${thought.category}";
            numComments?.text = thought.numComments.toString();

            val dateFormat = SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.getDefault());
            timestamp?.text = dateFormat.format(thought.timestamp);

            likesImage?.setOnClickListener {
                FirebaseFirestore.getInstance().collection(THOUGHTS_REF)
                    .document(thought.documentId)
                    .update(NUM_LIKES, thought.numLikes + 1);
            }

            listviewDeleteBtn?.setOnClickListener {
                FirebaseFirestore.getInstance().collection(THOUGHTS_REF)
                    .document(thought.documentId)
                    .delete();
            }

            itemView?.setOnClickListener {
                itemClick(thought);
            }

        }

    }
}