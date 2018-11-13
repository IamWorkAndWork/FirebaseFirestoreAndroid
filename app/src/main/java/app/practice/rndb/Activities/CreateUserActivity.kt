package app.practice.rndb.Activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import app.practice.rndb.Utilities.DATE_CREATED
import app.practice.rndb.R
import app.practice.rndb.Utilities.USERNAME
import app.practice.rndb.Utilities.USERS_REF
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_create_user.*

class CreateUserActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)

        auth = FirebaseAuth.getInstance();
    }

    fun createCreateClicked(v: View) {

        val email = createEmailTxt.text.toString();
        val password = createPasswordTxt.text.toString();
        val username = createUsernameTxt.text.toString()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val changeRequest = UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build();
                it.user.updateProfile(changeRequest)
                    .addOnFailureListener { exception ->
                        Log.e("create", "error could not update display name ${exception.localizedMessage}")
                    }

                val data = hashMapOf<String, Any>();
                data.put(USERNAME, username);
                data.put(DATE_CREATED, FieldValue.serverTimestamp())

                FirebaseFirestore.getInstance().collection(USERS_REF).document(it.user.uid)
                    .set(data)
                    .addOnSuccessListener {
                        finish()
                    }
                    .addOnFailureListener { exception ->
                        Log.e("create", "error could not add user document ${exception.localizedMessage}")
                    }
            }
            .addOnFailureListener { exception ->
                Log.e("create", "error could not create user ${exception.localizedMessage}")
            }
    }

    fun createCancelClicked(v: View) {
        finish()
    }


}
