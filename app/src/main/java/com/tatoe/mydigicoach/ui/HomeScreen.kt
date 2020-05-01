package com.tatoe.mydigicoach.ui

import android.content.Intent
import android.os.Bundle
import android.view.*
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
import com.tatoe.mydigicoach.ui.calendar.CustomAdapterFragment
import com.tatoe.mydigicoach.ui.calendar.DayCreator
import com.tatoe.mydigicoach.ui.util.DayExercisesListAdapter
import com.tatoe.mydigicoach.viewmodels.LoginSignUpViewModel
import com.tatoe.mydigicoach.viewmodels.MyLoginSignUpViewModelFactory
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home_2.*
import kotlinx.android.synthetic.main.item_holder_home_slider.view.*
import timber.log.Timber

class HomeScreen : AppCompatActivity() {

    private var firebaseUser: FirebaseUser? = null
    private var db = FirebaseFirestore.getInstance()

    private lateinit var loginSignUpViewModel: LoginSignUpViewModel
    private var dayToday: Day? = null
    private lateinit var recyclerViewExercises: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_2)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        loginSignUpViewModel = ViewModelProviders.of(
            this,
            MyLoginSignUpViewModelFactory(application, db)
        ).get(
            LoginSignUpViewModel::class.java
        )

        firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser != null) {
           loginSignUpViewModel.saveUserToDataholder()

        }

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)

//        val viewpager = viewpager
        viewpager.adapter=CustomPagerAdapter(this)

//        calendar_button.setOnClickListener {
//            var intent = Intent(this, MonthViewer::class.java)
//            startActivity(intent)
//        }
//
//        block_button.setOnClickListener {
//            var intent = Intent(this, BlockViewer::class.java)
//            startActivity(intent)
//        }
//
//        exercise_button.setOnClickListener {
//            var intent = Intent(this, ExerciseViewer::class.java)
//            startActivity(intent)
//        }
//
//        library_button.setOnClickListener {
//            var intent = Intent(this, Library::class.java)
//            startActivity(intent)
//        }

//        recyclerViewExercises = dayExercisesRecyclerView as RecyclerView

//        initObservers()

    }


    class CustomPagerAdapter(homeScreen: HomeScreen) : RecyclerView.Adapter<CustomPagerAdapter.MyViewHolder>() {

        private val inflater: LayoutInflater = LayoutInflater.from(homeScreen)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = inflater.inflate(R.layout.item_holder_home_slider, parent, false)
            return MyViewHolder(itemView)
        }

        override fun getItemCount(): Int {
            return 4
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            when (position) {
                0 -> holder.name.text="EXERCISES"
                1 -> holder.name.text="CALENDAR"
                2 -> holder.name.text="LIBRARY"
                3 -> holder.name.text="PROFILE"
            }
        }

        class MyViewHolder(v:View) : RecyclerView.ViewHolder(v){
            var background = v.category_image
            var name = v.category_name
        }

    }

    private fun initObservers() {
        loginSignUpViewModel.dayToday.observe(this, androidx.lifecycle.Observer { day ->
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.action_logout -> {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, UserAccess::class.java)
            startActivity(intent)
            finish()
            true
        }


        else -> {
            super.onOptionsItemSelected(item)
        }
    }

}
