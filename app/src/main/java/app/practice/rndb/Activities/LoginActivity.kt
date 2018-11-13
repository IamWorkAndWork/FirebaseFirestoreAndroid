package app.practice.rndb.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import app.practice.rndb.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance();
    }

    fun loginLoginClicked(v: View) {
        val email = loginEmailTxt.text.toString();
        val password = loginPasswordTxt.text.toString();

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { finish() }
            .addOnFailureListener { exception ->
                Toast.makeText(this@LoginActivity, "Error Login ${exception.localizedMessage}", Toast.LENGTH_LONG)
                    .show()
            }
    }

    fun loginCreateClicked(v: View) {
        val createIntent = Intent(this, CreateUserActivity::class.java);
        startActivity(createIntent);
    }
}
