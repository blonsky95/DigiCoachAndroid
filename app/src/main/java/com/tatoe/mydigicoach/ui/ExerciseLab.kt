package com.tatoe.mydigicoach.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.database.AppDatabase
import kotlinx.android.synthetic.main.activity_exercise_lab.*

class ExerciseLab : AppCompatActivity() {

    lateinit var exerciseName:String
    lateinit var exerciseDesc:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_lab)


        floatingActionButton.setOnClickListener {
            exerciseName=EditText1.text.trim().toString()
            exerciseDesc=editText2.text.trim().toString()

            //todo use a coroutine to store the exercise
            //todo use another DAO method to check if it has been updated
            //todo check for autoincrement and why am i providing it as a parameter
            //todo add navigation so can circulate through app
//            GlobalScope.launch {
//                db.todoDao().insertAll(TodoEntry("Title", "Content"))
//                data = db.todoDao().getAll()
//
//                data?.forEach {
//                    println(it)
//                }
//            }
        }
    }
}
