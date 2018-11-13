package app.practice.rndb.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import app.practice.rndb.Adapters.ThoughtsAdapter
import app.practice.rndb.Model.Thought
import app.practice.rndb.R
import app.practice.rndb.Utilities.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {
    var db: FirebaseFirestore? = null;
    lateinit var thoughtsAdapter: ThoughtsAdapter;
    val thoughts = arrayListOf<Thought>();

    var selectedCategory = FUNNY;

    val thoughtsCollectionsRef = FirebaseFirestore.getInstance().collection(THOUGHTS_REF);
    lateinit var thoughtsListener: ListenerRegistration;
    lateinit var auth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        db = FirebaseFirestore.getInstance();

        auth = FirebaseAuth.getInstance();

        fabAdd.setOnClickListener {
            val intent = Intent(this@MainActivity, AddThoughtActivity::class.java);
            startActivity(intent);
        }

        thoughtsAdapter = ThoughtsAdapter(thoughts) { thought ->

            val commentIntent = Intent(this@MainActivity, CommentsActivity::class.java);
            commentIntent.putExtra(DOCUMENT_KEY, thought.documentId);
            startActivity(commentIntent);

        };

        val lm = LinearLayoutManager(this);
        thoughtListView.apply {
            adapter = thoughtsAdapter;
            layoutManager = lm
            setHasFixedSize(true)
        }


//        thoughtListView.also {
//            it.adapter = thoughtsAdapter;
//            it.layoutManager = LinearLayoutManager(this);
//            it.setHasFixedSize(true)
//        }


//        thoughtsCollectionsRef.get()
//            .addOnSuccessListener { snapshot ->
//
//
//            }.addOnFailureListener { exception ->
//                Log.e("mainActivity", "error = $exception")
//            }


    }

    fun updateUI() {
        if (auth.currentUser == null) {
            mainCrazyBtn.isEnabled = false;
            mainPopularBtn.isEnabled = false;
            mainFunnyBtn.isEnabled = false;
            mainSeriousBtn.isEnabled = false;
            fabAdd.isEnabled = false;
            thoughts.clear();
            thoughtsAdapter.notifyDataSetChanged();
        } else {
            mainCrazyBtn.isEnabled = true;
            mainPopularBtn.isEnabled = true;
            mainFunnyBtn.isEnabled = true;
            mainSeriousBtn.isEnabled = true;
            fabAdd.isEnabled = true;

            val username = auth.currentUser?.displayName
            val email = auth.currentUser?.email;
            Toast.makeText(this, "Welcome ${username}", Toast.LENGTH_LONG).show()


            setListener()

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu, menu);
        return true;

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_login) {
            if (auth.currentUser == null) {
                val loginIntent = Intent(this, LoginActivity::class.java);
                startActivity(loginIntent);
            } else {
                auth.signOut();
                updateUI();
            }
            return true;
        }
        return false;
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val menuItem = menu?.getItem(0);
        if (auth.currentUser == null) {
            //no user
            menuItem?.title = "Login"
        } else {
            menuItem?.title = "Logout"
        }
        return super.onPrepareOptionsMenu(menu);
    }

    override fun onResume() {
        super.onResume()

        updateUI()

    }

    fun setListener() {

        thoughts.clear()
        thoughtsAdapter.notifyDataSetChanged();


        if (selectedCategory == POPULAR) {

            thoughtsListener = thoughtsCollectionsRef
                .orderBy(NUM_LIKES, Query.Direction.DESCENDING)
                .addSnapshotListener(this) { snapshot, exception ->

                    if (exception != null) {
                        Log.e("mainActivity", "error listener = $exception")
                    }

                    parseData(snapshot!!)

                }

        } else {
            thoughtsListener = thoughtsCollectionsRef
                .orderBy(TIMESTAMTP, Query.Direction.DESCENDING)
                .whereEqualTo(CATEGORY, selectedCategory)
                .addSnapshotListener(this) { snapshot, exception ->

                    if (exception != null) {
                        Log.e("mainActivity", "error listener = $exception")
                    }

                    parseData(snapshot!!)

                }
        }


    }


    fun parseData(snapshot: QuerySnapshot) {
        if (snapshot != null) {

            thoughts.clear()

            for (document in snapshot.documents) {

                val data = document.data!!;

                val name = data[USERNAME] as String;
                val timestamp = data[TIMESTAMTP] as Date;
                val thoughtTxt = data[THOUGHT_TXT] as String;
                val numLikes = data[NUM_LIKES] as Long;
                val numComments = data[NUM_COMMENTS] as Long;
                val documentId = document.id;
                val category = document[CATEGORY] as String;

                val newThought =
                    Thought(
                        name,
                        timestamp,
                        thoughtTxt,
                        numLikes.toInt(),
                        numComments.toInt(),
                        category,
                        documentId
                    );
                thoughts.add(newThought);

            }

            thoughtsAdapter.notifyDataSetChanged();
        }
    }

    fun mainFunnyClicked(v: View) {
        if (selectedCategory == FUNNY) {
            mainFunnyBtn.isChecked = true;
            return;
        }
        mainSeriousBtn.isChecked = false;
        mainCrazyBtn.isChecked = false;
        mainPopularBtn.isChecked = false;
        selectedCategory = FUNNY;

        thoughtsListener.remove();
        setListener()
    }

    fun mainSeriousClicked(v: View) {
        if (selectedCategory == SERIOUS) {
            mainSeriousBtn.isChecked = true;
            return;
        }
        mainFunnyBtn.isChecked = false;
        mainCrazyBtn.isChecked = false;
        mainPopularBtn.isChecked = false;
        selectedCategory = SERIOUS;

        thoughtsListener.remove();
        setListener()
    }

    fun mainCrazyClicked(v: View) {
        if (selectedCategory == CRAZY) {
            mainCrazyBtn.isChecked = true;
            return;
        }
        mainFunnyBtn.isChecked = false;
        mainSeriousBtn.isChecked = false;
        mainPopularBtn.isChecked = false;
        selectedCategory = CRAZY;

        thoughtsListener.remove();
        setListener()
    }

    fun mainPopularClicked(v: View) {
        if (selectedCategory == POPULAR) {
            mainPopularBtn.isChecked = true;
            return;
        }
        mainFunnyBtn.isChecked = false;
        mainSeriousBtn.isChecked = false;
        mainCrazyBtn.isChecked = false;
        selectedCategory = POPULAR;

        thoughtsListener.remove();
        setListener()
    }

}
