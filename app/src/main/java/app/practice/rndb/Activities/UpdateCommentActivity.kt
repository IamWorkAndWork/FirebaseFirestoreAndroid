package app.practice.rndb.Activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import app.practice.rndb.R
import app.practice.rndb.Utilities.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_update_comment.*

class UpdateCommentActivity : AppCompatActivity() {

    lateinit var thoughDocId: String;
    lateinit var commentDocId: String;
    lateinit var commentTxt: String;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_comment)


        thoughDocId = intent.getStringExtra(THOUGHT_DOC_ID_EXTRA);
        commentDocId = intent.getStringExtra(COMMENT_DOC_ID_EXTRA);
        commentTxt = intent.getStringExtra(COMMENT_TXT_EXTRA);

        updateCommentTxt.setText(commentTxt);

    }

    fun updateCommentClicked(v: View) {

        FirebaseFirestore.getInstance().collection(THOUGHTS_REF).document(thoughDocId)
            .collection(COMMENTS_REF).document(commentDocId)
            .update(COMMENT_TXT, updateCommentTxt.text.toString())
            .addOnSuccessListener {
                finish();
            }
            .addOnFailureListener { }

    }
}
