package com.tatoe.mydigicoach.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.*
import com.tatoe.mydigicoach.entity.Day
//import com.google.firebase.iid.FirebaseInstanceId
import com.tatoe.mydigicoach.ui.block.BlockViewer
import com.tatoe.mydigicoach.ui.calendar.CustomAdapterFragment
import com.tatoe.mydigicoach.ui.calendar.DayCreator
import com.tatoe.mydigicoach.ui.calendar.MonthViewer
import com.tatoe.mydigicoach.ui.exercise.ExerciseViewer
import com.tatoe.mydigicoach.ui.util.DataHolder
import com.tatoe.mydigicoach.ui.util.DayExercisesListAdapter
import com.tatoe.mydigicoach.viewmodels.HomeScreenViewModel
import com.tatoe.mydigicoach.viewmodels.MyHomeScreenViewModelFactory
import kotlinx.android.synthetic.main.activity_home.*
import timber.log.Timber
import java.util.*

class HomeScreen : AppCompatActivity() {

    private var firebaseUser: FirebaseUser? = null
    private var db = FirebaseFirestore.getInstance()

    private lateinit var homeScreenViewModel: HomeScreenViewModel
    private var dayToday: Day? = null
    private lateinit var recyclerViewExercises: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        title = "Home"
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
        }

//        dataViewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)
        homeScreenViewModel = ViewModelProviders.of(
            this,
            MyHomeScreenViewModelFactory(application, db)
        ).get(
            HomeScreenViewModel::class.java
        )

        firebaseUser = FirebaseAuth.getInstance().currentUser

//        if (!DatabaseListener.isServiceRunning && firebaseUser!=null) {
//            startService(Intent(this, DatabaseListener::class.java))
//        }
        if (firebaseUser != null) {
            welcome_text.text = firebaseUser!!.email
            DataHolder.userEmail = firebaseUser!!.email
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

        recyclerViewExercises = dayExercisesRecyclerView as RecyclerView

        //todo test this
        log_off.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            var intent = Intent(this, UserAccess::class.java)
//            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TOP
//            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK
//            intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
//            onBackPressed()
            finish()
        }

        initObservers()


//        val user1 = hashMapOf(
//            "username" to "${firebaseUser?.displayName}",
//            "email" to "${firebaseUser?.email}",
//            "last_contact" to Calendar.getInstance().time.toString()
//        ) as HashMap<String, Any>
//
//        homeScreenViewModel.checkInUserFirestore(user1)

//        viewData()

    }

    private fun initObservers() {
        homeScreenViewModel.dayToday.observe(this, androidx.lifecycle.Observer { day ->
            dayToday = day
            var isDayEmpty =
                dayToday == null || dayToday?.exercises!!.isEmpty()
            updateUI(isDayEmpty)
        })

    }

    private fun updateUI(isDayEmpty: Boolean) {
        if (isDayEmpty) {
            ifEmptyTodaytext.visibility = View.VISIBLE
            recyclerViewExercises.visibility = View.GONE
            ifEmptyTodaytext.setOnClickListener {
                val intent = Intent(this, DayCreator::class.java)
                intent.putExtra(DayCreator.DAY_ID, Day.dateToDayID(Day.getTodayDate()))
                startActivity(intent)
            }
        } else {
            ifEmptyTodaytext.visibility = View.GONE
            recyclerViewExercises.visibility = View.VISIBLE
            val dayContentAdapterExercises =
                DayExercisesListAdapter(
                    this,
                    dayToday!!.dayId,
                    CustomAdapterFragment.EXERCISE_TYPE_ADAPTER
                )
            recyclerViewExercises.adapter = dayContentAdapterExercises
            recyclerViewExercises.layoutManager = LinearLayoutManager(this)
            dayContentAdapterExercises.setContent(dayToday)
        }
    }

//    private fun initObservers() {
//        homeScreenViewModel.allDays.observe(this, androidx.lifecycle.Observer { days ->
//            days?.let {
//
//            }
//        })
//    }

//    private fun viewData() {
//        db.collection("users")
//            .get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    Timber.d( "${document.id} => ${document.data} ********** email: ${document["email"]}")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Timber.d( "Error getting documents: $exception")
//            }
//    }

//    private fun addUser(user: HashMap<String, Any>) {
//        //todo run this in non UI thread
//        if (!userInCollection(user["email"] as String)) {
//            db.collection("users").document(user["email"] as String)
//                .set(user)
//                .addOnSuccessListener {
//                    Timber.d("DocumentSnapshot added!")
//                }
//                .addOnFailureListener { e ->
//                    Timber.d("Error adding document: $e")
//                }
//        }
//        Timber.d("we gucci no need to add")
//
//    }
//
//    private fun userInCollection(userEmail: String): Boolean {
//        // check if in users there is document
//        val docRef : DocumentReference = db.collection("users").document(userEmail)
////        Timber.d("is docref succesfull: ${docRef.get().result?.exists()}")
//
//        return docRef.get().isSuccessful
//
//    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_toolbar_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.action_logout -> {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, UserAccess::class.java)
            startActivity(intent)
            true
        }


        else -> {
            super.onOptionsItemSelected(item)
        }
    }

}
