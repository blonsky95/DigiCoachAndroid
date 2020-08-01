package com.tatoe.mydigicoach.ui

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import androidx.annotation.DimenRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.*
import com.tatoe.mydigicoach.entity.Day
//import com.google.firebase.iid.FirebaseInstanceId
import com.tatoe.mydigicoach.ui.calendar.DayCreator
import com.tatoe.mydigicoach.ui.calendar.MonthViewer
import com.tatoe.mydigicoach.ui.calendar.WeekViewer
import com.tatoe.mydigicoach.ui.exercise.ExerciseViewer
import com.tatoe.mydigicoach.viewmodels.HomeViewModel
import com.tatoe.mydigicoach.viewmodels.MyHomeViewModelFactory
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.item_holder_home_slider.view.*
import timber.log.Timber

class HomeScreen : AppCompatActivity() {

    private var firebaseUser: FirebaseUser? = null
    private var db = FirebaseFirestore.getInstance()

    private lateinit var homeViewModel: HomeViewModel
    private var dayToday: Day? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        homeViewModel = ViewModelProviders.of(
            this,
            MyHomeViewModelFactory(db, application)
        ).get(
            HomeViewModel::class.java
        )

        firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser != null) {
            homeViewModel.saveUserToDataholder()

        }

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)

//        val viewpager = viewpager
        viewpager2.adapter = CustomPagerAdapter(this)
        viewpager2.offscreenPageLimit = 1

        val nextItemVisiblePx = resources.getDimension(R.dimen.viewpager_next_item_visible)
        val currentItemHorizontalMarginPx =
            resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
        val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
            page.translationX = -pageTranslationX * position
            // Next line scales the item's height. You can remove it if you don't want this effect
//            page.scaleY = 1 - (0.1f * kotlin.math.abs(position))
            // If you want a fading effect uncomment the next line:
            // page.alpha = 0.25f + (1 - abs(position))
        }
        viewpager2.setPageTransformer(pageTransformer)

        val itemDecoration = HorizontalMarginItemDecoration(
            this,
            R.dimen.viewpager_current_item_horizontal_margin
        )
        viewpager2.addItemDecoration(itemDecoration)

//        recyclerViewExercises = dayExercisesRecyclerView as RecyclerView

        initObservers()
    }

    class CustomPagerAdapter(homeScreen: HomeScreen) :
        RecyclerView.Adapter<CustomPagerAdapter.MyViewHolder>() {

        private val inflater: LayoutInflater = LayoutInflater.from(homeScreen)
        private val context = homeScreen

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = inflater.inflate(R.layout.item_holder_home_slider, parent, false)
            return MyViewHolder(itemView)
        }

        override fun getItemCount(): Int {
            return 4
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

            var classVar: Class<*>? = null
            when (position) {
                0 -> {
                    holder.name.text = "Exercises"
                    classVar = ExerciseViewer::class.java
                }
                1 -> {
                    holder.name.text = "Calendar"
                    classVar = MonthViewer::class.java
                }
                2 -> {
                    holder.name.text = "Store"
                    classVar = Library::class.java
                }
                3 -> {
                    holder.name.text = "Profile"
                    classVar = Profile::class.java

                }
            }
            holder.container.setOnClickListener {
                if (classVar!=null){
                    context.startActivity(Intent(context, classVar))
                }
            }
        }

        class MyViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            var background = v.category_image
            var name = v.category_name
            var container = v.item_container
        }

    }

    class HorizontalMarginItemDecoration(context: Context, @DimenRes horizontalMarginInDp: Int) :
        RecyclerView.ItemDecoration() {

        private val horizontalMarginInPx: Int =
            context.resources.getDimension(horizontalMarginInDp).toInt()

        override fun getItemOffsets(
            outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
        ) {
            outRect.right = horizontalMarginInPx
            outRect.left = horizontalMarginInPx
        }

    }

    private fun initObservers() {
        homeViewModel.dayToday.observe(this, androidx.lifecycle.Observer { day ->
            dayToday = day
            var isDayEmpty =
                dayToday == null || dayToday?.exercises!!.isEmpty()
            updateUI(isDayEmpty)
        })

    }

    private fun updateUI(isDayEmpty: Boolean) {
        if (isDayEmpty) {
            todayDiaryText.text = "Click here to add today's training!"
            todayDairyImage.setOnClickListener {
                val intent = Intent(this, DayCreator::class.java)
                intent.putExtra(DayCreator.DAY_ID, Day.dateToDayID(Day.getTodayDate()))
                startActivity(intent)
            }
        } else {

            var todayText = ""
            val itemMax = 3
            var i = 0
            for (exercise in dayToday!!.exercises) {
                if (i != itemMax) {
                    todayText += "- ${exercise.name}\n"
                    i++
                } else {
                    todayText += "- Tap here for full training"
                    break
                }
            }
            todayDiaryText.text = todayText
            todayDairyImage.setOnClickListener {
                val intent = Intent(this, WeekViewer::class.java)
                intent.putExtra(MonthViewer.DAY_ID_KEY, Day.dateToDayID(Day.getTodayDate()))
                startActivity(intent)
            }
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.home_toolbar_menu, menu)
//        return super.onCreateOptionsMenu(menu)
//    }

//    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
//
//        R.id.action_logout -> {
////            homeViewModel.closeDbInstance()
////            FirebaseAuth.getInstance().signOut()
////            val intent = Intent(this, UserAccess::class.java)
////            startActivity(intent)
////            finish()
//            true
//        }
//
//        R.id.upload_store_exercises -> {
//            //uncomment this to be able to add stub exercises to store
////        loginSignUpViewModel.addBunchOfStubStoreExercises()
//            true
//        }
//
//        else -> {
//            super.onOptionsItemSelected(item)
//        }
//    }

}
