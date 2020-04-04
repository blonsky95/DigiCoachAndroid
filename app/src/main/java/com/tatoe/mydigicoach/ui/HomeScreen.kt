package com.tatoe.mydigicoach.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.*
//import com.google.firebase.iid.FirebaseInstanceId
import com.tatoe.mydigicoach.ui.block.BlockViewer
import com.tatoe.mydigicoach.ui.calendar.MonthViewer
import com.tatoe.mydigicoach.ui.exercise.ExerciseViewer
import com.tatoe.mydigicoach.ui.util.DataHolder
import com.tatoe.mydigicoach.viewmodels.HomeScreenViewModel
import com.tatoe.mydigicoach.viewmodels.MyHomeScreenViewModelFactory
import kotlinx.android.synthetic.main.activity_home.*
import timber.log.Timber
import java.util.*

class HomeScreen : AppCompatActivity() {

    private var firebaseUser:FirebaseUser? = null
    private var db = FirebaseFirestore.getInstance()


    private lateinit var homeScreenViewModel: HomeScreenViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        title = "Home"
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
        }

//        dataViewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)
        homeScreenViewModel = ViewModelProviders.of(this,
            MyHomeScreenViewModelFactory(db)
        ).get(
            HomeScreenViewModel::class.java)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            welcome_text.text = firebaseUser!!.email
            DataHolder.userEmail=firebaseUser!!.email
        } else {
            // No user is signed in
        }

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        calendar_button.setOnClickListener {
            var intent = Intent(this, MonthViewer::class.java)
            startActivity(intent)
        }

        block_button.setOnClickListener {
            var intent = Intent(this, BlockViewer::class.java)
            startActivity(intent)
        }

        exercise_button.setOnClickListener {
            var intent = Intent(this, ExerciseViewer::class.java)
            startActivity(intent)
        }

        library_button.setOnClickListener {
            var intent = Intent(this, Library::class.java)
            startActivity(intent)
        }


        val user1 = hashMapOf(
            "username" to "${firebaseUser?.displayName}",
            "email" to "${firebaseUser?.email}",
            "last_contact" to Calendar.getInstance().time.toString()
        ) as HashMap<String, Any>

// Add a new document with a generated ID

//        addUser(user1)
        homeScreenViewModel.checkInUserFirestore(user1)

//        val user2 = hashMapOf(
//            "username" to "${firebaseUser?.displayName}",
//            "email" to "${firebaseUser?.email}",
//            "last_contact" to Calendar.getInstance().time.toString(),
//            "android_version" to android.os.Build.VERSION.SDK_INT
//        )

//        addUser(user2)

//        FirebaseInstanceId.getInstance().instanceId
//            .addOnCompleteListener(OnCompleteListener { task ->
//                if (!task.isSuccessful) {
//                    Timber.d("getInstanceId failed ${task.exception}")
//                    return@OnCompleteListener
//                }
//
//                // Get new Instance ID token
//                val token = task.result?.token
//
//                // Log and toast
////                val msg = getString(R.string.msg_token_fmt, token)
//                Timber.d(token.toString())
//                Toast.makeText(baseContext, token.toString(), Toast.LENGTH_SHORT).show()
//            })

        viewData()

    }

    private fun viewData() {
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Timber.d( "${document.id} => ${document.data} ********** email: ${document["email"]}")
                }
            }
            .addOnFailureListener { exception ->
                Timber.d( "Error getting documents: $exception")
            }
    }

    private fun addUser(user: HashMap<String, Any>) {
        //todo run this in non UI thread
        if (!userInCollection(user["email"] as String)) {
            db.collection("users").document(user["email"] as String)
                .set(user)
                .addOnSuccessListener {
                    Timber.d("DocumentSnapshot added!")
                }
                .addOnFailureListener { e ->
                    Timber.d("Error adding document: $e")
                }
        }
        Timber.d("we gucci no need to add")

    }

    private fun userInCollection(userEmail: String): Boolean {
        // check if in users there is document
        val docRef : DocumentReference = db.collection("users").document(userEmail)
//        Timber.d("is docref succesfull: ${docRef.get().result?.exists()}")

        return docRef.get().isSuccessful

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_toolbar_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.action_logout -> {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginScreen::class.java)
            startActivity(intent)
            true
        }


        else -> {
            super.onOptionsItemSelected(item)
        }
    }

}
