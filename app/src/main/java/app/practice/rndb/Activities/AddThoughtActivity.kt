package app.practice.rndb.Activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import app.practice.rndb.R
import app.practice.rndb.Utilities.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_thought.*

class AddThoughtActivity : AppCompatActivity() {

    var selectedCategory = FUNNY;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_thought)

        defaultSelect();
    }

    fun defaultSelect() {
        if (selectedCategory == FUNNY) {
            addFunnyBtn.isChecked = true;
            return;
        }
        addSeriousBtn.isChecked = false;
        addCrazyBtn.isChecked = false;
        selectedCategory = FUNNY;
    }

    fun addFunnyClicked(v: View) {
        defaultSelect();
    }

    fun addSeriousClicked(v: View) {
        if (selectedCategory == SERIOUS) {
            addSeriousBtn.isChecked = true;
            return;
        }
        addFunnyBtn.isChecked = false;
        addCrazyBtn.isChecked = false;
        selectedCategory = SERIOUS;
    }

    fun addCrazyClicked(v: View) {
        if (selectedCategory == CRAZY) {
            addCrazyBtn.isChecked = true;
            return;
        }
        addFunnyBtn.isChecked = false;
        addSeriousBtn.isChecked = false;
        selectedCategory = CRAZY;
    }

    fun addPostClicked(v: View) {

        val data = HashMap<String, Any>();
        data.put(CATEGORY, selectedCategory);
        data.put(NUM_COMMENTS, 0);
        data.put(NUM_LIKES, 0);
        data.put(THOUGHT_TXT, addThoughtTxt.text.toString());
        data.put(TIMESTAMTP, FieldValue.serverTimestamp());
        data.put(USERNAME, FirebaseAuth.getInstance().currentUser?.displayName.toString());

        FirebaseFirestore.getInstance().collection(THOUGHTS_REF)
            .add(data)
            .addOnSuccessListener {
                finish();
            }
            .addOnFailureListener {
                Log.e("AddThought", "error add : $it")
            }


    }

}
