package com.tatoe.mydigicoach.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.tatoe.mydigicoach.DataViewModel
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Exercise
import kotlinx.android.synthetic.main.activity_exercise_lab.*

class ExerciseLab : AppCompatActivity() {

    lateinit var exerciseName:String
    lateinit var exerciseDesc:String
    private lateinit var dataViewModel:DataViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_lab)

        dataViewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)


        floatingActionButton.setOnClickListener {
            exerciseName=EditText1.text.trim().toString()
            exerciseDesc=editText2.text.trim().toString()

            var newExercise = Exercise(exerciseName,exerciseDesc)
            dataViewModel.insert(newExercise)



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
