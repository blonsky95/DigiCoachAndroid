package com.tatoe.mydigicoach.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import androidx.room.Database
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.database.DataSource
import kotlinx.android.synthetic.main.activity_exercise_lab.*

class ExerciseLab : AppCompatActivity() {

    lateinit var exerciseName:String
    lateinit var exerciseDesc:String
    lateinit var exerciseDB:DataSource.AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_lab)

        exerciseDB = DataSource.AppDatabase(this)

        floatingActionButton.setOnClickListener {
            exerciseName=EditText1.text.trim().toString()
            exerciseDesc=editText2.text.trim().toString()

            //todo use a coroutine to store the exercise
            exerciseDB.ExercisesDao().addExercise(DataSource.ExerciseEntity(4,exerciseName,exerciseDesc))
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
