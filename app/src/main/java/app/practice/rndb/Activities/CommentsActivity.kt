package app.practice.rndb.Activities

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import app.practice.rndb.Adapters.CommentAdapter
import app.practice.rndb.Model.Comment
import app.practice.rndb.R
import app.practice.rndb.Utilities.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_comments.*
import java.util.*

class CommentsActivity : AppCompatActivity() {

    lateinit var thoughtDocumentId: String;
    val comments = arrayListOf<Comment>();
    lateinit var commentsAdapter: CommentAdapter;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        thoughtDocumentId = intent.getStringExtra(DOCUMENT_KEY);

        commentsAdapter = CommentAdapter(comments) { comment ->
        }
        commentListView.apply {
            adapter = commentsAdapter;
            layoutManager = LinearLayoutManager(this@CommentsActivity);
            setHasFixedSize(true);
        }

        FirebaseFirestore.getInstance().collection(THOUGHTS_REF).document(thoughtDocumentId)
            .collection(COMMENTS_REF)
            .orderBy(TIMESTAMTP, Query.Direction.DESCENDING)
            .addSnapshotListener() { snapshot, exception ->

                if (exception != null) {
                    Log.e("error", "coulf not retrive comments ${exception.localizedMessage}")
                }

                if (snapshot != null) {
                    comments.clear();

                    for (document in snapshot.documents) {
                        val data = document.data!!;
                        val name = data[USERNAME] as String;
                        val timestamp = data[TIMESTAMTP] as Date;
                        val commentTxt = data[COMMENT_TXT] as String;

                        val newComment = Comment(name, timestamp, commentTxt);
                        comments.add(newComment)
                    }

                    commentsAdapter.notifyDataSetChanged();
                }

            }

    }

    fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager;
        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0);
        }
    }

    fun addCommentClicked(view: View) {
        val commentTxt = enterCommentTxt.text.toString();
        val thoughtRef = FirebaseFirestore.getInstance().collection(THOUGHTS_REF).document(thoughtDocumentId)

        FirebaseFirestore.getInstance().runTransaction { transaction ->

            val thought = transaction.get(thoughtRef);
            val numComments = thought?.getLong(NUM_COMMENTS)!! + 1;
            transaction.update(thoughtRef, NUM_COMMENTS, numComments);

            val newCommentRef = FirebaseFirestore.getInstance().collection(THOUGHTS_REF)
                .document(thoughtDocumentId)
                .collection(COMMENTS_REF)
                .document()

            val data = hashMapOf<String, Any>();
            data.put(COMMENT_TXT, commentTxt);
            data.put(TIMESTAMTP, FieldValue.serverTimestamp());
            data.put(USERNAME, FirebaseAuth.getInstance().currentUser?.displayName.toString());

            transaction.set(newCommentRef, data);


        }
            .addOnSuccessListener {
                enterCommentTxt.setText("");
                hideKeyboard();
            }
            .addOnFailureListener { exception ->
                Log.e("error", "cpulf not add comment ${exception.localizedMessage}")
            }
    }


}
