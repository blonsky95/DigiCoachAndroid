package com.tatoe.mydigicoach.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.iid.FirebaseInstanceId
import com.tatoe.mydigicoach.BuildConfig
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.ui.block.BlockViewer
import com.tatoe.mydigicoach.ui.day.DayViewer
import com.tatoe.mydigicoach.ui.exercise.ExerciseViewer
import kotlinx.android.synthetic.main.activity_home.*
import timber.log.Timber
import java.util.*

class HomeScreen : AppCompatActivity() {

    private var firebaseUser:FirebaseUser? = null
    private lateinit var db:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        title = "Home"
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
        }

        firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            welcome_text.text = firebaseUser!!.email
        } else {
            // No user is signed in
        }

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        calendar_button.setOnClickListener {
            var intent = Intent(this, DayViewer::class.java)
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

        db = FirebaseFirestore.getInstance()

        val user1 = hashMapOf(
            "username" to "${firebaseUser?.displayName}",
            "email" to "${firebaseUser?.email}",
            "last_contact" to Calendar.getInstance().time.toString()
        ) as HashMap<String, Any>

// Add a new document with a generated ID

//        addUser(user1)

        val user2 = hashMapOf(
            "username" to "${firebaseUser?.displayName}",
            "email" to "${firebaseUser?.email}",
            "last_contact" to Calendar.getInstance().time.toString(),
            "android_version" to android.os.Build.VERSION.SDK_INT
        )

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
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Timber.d("DocumentSnapshot added with ID: ${documentReference.id} and path: ${documentReference.path}")
            }
            .addOnFailureListener { e ->
                Timber.d( "Error adding document: $e")
            }
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
