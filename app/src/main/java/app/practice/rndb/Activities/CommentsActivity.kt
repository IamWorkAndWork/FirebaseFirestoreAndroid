package app.practice.rndb.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import app.practice.rndb.Adapters.CommentAdapter
import app.practice.rndb.Model.Comment
import app.practice.rndb.R
import app.practice.rndb.Utilities.*
import app.practice.rndb.interfaces.CommentOptionsClickListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_comments.*
import java.util.*

class CommentsActivity : AppCompatActivity(), CommentOptionsClickListener {


    lateinit var thoughtDocumentId: String;
    val comments = arrayListOf<Comment>();
    lateinit var commentsAdapter: CommentAdapter;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        thoughtDocumentId = intent.getStringExtra(DOCUMENT_KEY);

        commentsAdapter = CommentAdapter(comments, this) { comment ->
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
                        val documentId = document.id;
                        val userId = data[USER_ID] as String;

                        val newComment = Comment(name, timestamp, commentTxt, documentId, userId);
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
            data.put(USER_ID, FirebaseAuth.getInstance().currentUser?.uid.toString());

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

    override fun optionsMenuClicked(comment: Comment) {

        val builder = AlertDialog.Builder(this);
        val dialogView = layoutInflater.inflate(R.layout.options_menu, null);
        val deleteBtn = dialogView.findViewById<Button>(R.id.optionDeleteBtn);
        val editBtn = dialogView.findViewById<Button>(R.id.optionEditBtn);

        builder?.setView(dialogView)
            .setNegativeButton("Cancel") { dialogInterface, i ->
            }

        val ad = builder.show();

        deleteBtn.setOnClickListener {


            //            thoughtRef.delete()
//                .addOnSuccessListener {
//                    ad.dismiss();
//                }
//                .addOnFailureListener {
//
//                }

            val commentRef = FirebaseFirestore.getInstance().collection(THOUGHTS_REF).document(thoughtDocumentId)
                .collection(COMMENTS_REF).document(comment.documentId);
            val thoughtRef = FirebaseFirestore.getInstance().collection(THOUGHTS_REF).document(thoughtDocumentId)

            FirebaseFirestore.getInstance().runTransaction {

                val thought = it.get(thoughtRef);
                val numComments = thought.getLong(NUM_COMMENTS)!! - 1;
                it?.update(thoughtRef, NUM_COMMENTS, numComments);

                it.delete(commentRef);


            }.addOnSuccessListener {
                ad.dismiss()
            }
                .addOnFailureListener { }

        }

        editBtn.setOnClickListener {

            val updateIntent = Intent(this, UpdateCommentActivity::class.java);
            updateIntent.putExtra(THOUGHT_DOC_ID_EXTRA, thoughtDocumentId);
            updateIntent.putExtra(COMMENT_DOC_ID_EXTRA, comment.documentId);
            updateIntent.putExtra(COMMENT_TXT_EXTRA, comment.commentTxt);
            ad.dismiss();
            startActivity(updateIntent)

        }
    }


}
