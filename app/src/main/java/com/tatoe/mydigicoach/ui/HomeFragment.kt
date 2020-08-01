package com.tatoe.mydigicoach.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.ui.calendar.DayCreator
import com.tatoe.mydigicoach.ui.calendar.MonthViewer
import com.tatoe.mydigicoach.ui.calendar.WeekViewer
import com.tatoe.mydigicoach.viewmodels.*
import kotlinx.android.synthetic.main.activity_home.*

class HomeFragment : Fragment() {

    private var firebaseUser: FirebaseUser? = null
    private var db = FirebaseFirestore.getInstance()

    private lateinit var homeViewModel: HomeViewModel
    private var dayToday: Day? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        homeViewModel = ViewModelProviders.of(
            this,
            MyHomeViewModelFactory(db, activity!!.application)
        ).get(
            HomeViewModel::
            class.java
        )

        firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser != null) {
            homeViewModel.saveUserToDataholder()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
    }

    private fun initObservers() {
        homeViewModel.dayToday.observe(this, androidx.lifecycle.Observer { day ->
            dayToday = day
            var isDayEmpty =
                dayToday == null || dayToday?.exercises!!.isEmpty()
            updateTodayTrainingText(isDayEmpty)
        })

    }

    private fun updateTodayTrainingText(isDayEmpty: Boolean) {
        if (isDayEmpty) {
            todayDiaryText.text = "Click here to add today's training!"
            todayDairyImage.setOnClickListener {
                val intent = Intent(activity, DayCreator::class.java)
                intent.putExtra(DayCreator.DAY_ID, Day.dateToDayID(Day.getTodayDate()))
                startActivity(intent)
            }
        } else {

            var todayText = ""
            val itemMax = 5
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
                val intent = Intent(activity, WeekViewer::class.java)
                intent.putExtra(MonthViewer.DAY_ID_KEY, Day.dateToDayID(Day.getTodayDate()))
                startActivity(intent)
            }
        }
    }
}