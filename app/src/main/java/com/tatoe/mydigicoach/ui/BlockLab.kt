package com.tatoe.mydigicoach.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.tatoe.mydigicoach.DataViewModel
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.util.ExerciseListAdapter
import kotlinx.android.synthetic.main.activity_block_lab.*
import timber.log.Timber

class BlockLab : AppCompatActivity() {
    private lateinit var dataViewModel: DataViewModel
    private lateinit var recyclerView: RecyclerView

    private val exerciseLabAcitivtyRequestCode = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block_lab)
        title = "Block Lab"

        recyclerView = recyclerview as RecyclerView

        var myClickListener = MyClickListener()
        val adapter = ExerciseListAdapter(this, myClickListener)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        dataViewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)

        dataViewModel.allExercises.observe(this, Observer { exercises ->
            exercises?.let { adapter.setExercises(it) }
            Timber.d("all exercises: ${exercises.toString()}")
        })

        // add the delete as well

        button4.setOnClickListener {
            Timber.d("block lab --> Exercise lab")

            val intent = Intent(this, ExerciseLab::class.java)
            intent.putExtra(ExerciseLab.EXERCISE_ACTION, ExerciseLab.EXERCISE_NEW)
            startActivityForResult(intent, exerciseLabAcitivtyRequestCode)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == exerciseLabAcitivtyRequestCode && resultCode == ExerciseLab.EXERCISE_NEW_RESULT_CODE) {
            intentData?.let { data ->
                val exercise = Exercise(
                    data.getStringExtra(ExerciseLab.EXERCISE_NAME_KEY),
                    data.getStringExtra(ExerciseLab.EXERCISE_DESCRIPTION_KEY)
                )
                dataViewModel.insert(exercise)

            }
            val mySnackbar = Snackbar.make(recyclerView, "Exercise added", Snackbar.LENGTH_LONG)
            mySnackbar.show()
        }
        if (requestCode == exerciseLabAcitivtyRequestCode && resultCode == ExerciseLab.EXERCISE_UPDATE_RESULT_CODE) {
            intentData?.let { data ->
                val exercise = Exercise(
                    data.getStringExtra(ExerciseLab.EXERCISE_NAME_KEY),
                    data.getStringExtra(ExerciseLab.EXERCISE_DESCRIPTION_KEY)
                )
                dataViewModel.update(exercise, data.getStringExtra(ExerciseLab.EXERCISE_OLD_NAME_KEY))

            }
            val mySnackbar = Snackbar.make(recyclerView, "Exercise updated", Snackbar.LENGTH_LONG)
            mySnackbar.show()
        }
        if (requestCode == exerciseLabAcitivtyRequestCode && resultCode == ExerciseLab.EXERCISE_FAIL_RESULT_CODE) {
            //accounts for user pressing back
            val mySnackbar = Snackbar.make(recyclerView, "Failure is an option", Snackbar.LENGTH_LONG)
            mySnackbar.show()
        } else {
        }
    }

    inner class MyClickListener : View.OnClickListener, View.OnLongClickListener {
        //todo change name of MyClickListener

        lateinit var currentExercise: Exercise

        fun setExercise(exercise: Exercise) {
            currentExercise = exercise
        }

        override fun onClick(v: View?) {
            val intent = Intent(this@BlockLab, ExerciseLab::class.java)
            intent.putExtra(ExerciseLab.EXERCISE_ACTION, ExerciseLab.EXERCISE_UPDATE)
            intent.putExtra(ExerciseLab.EXERCISE_NAME_KEY, currentExercise.name)
            intent.putExtra(ExerciseLab.EXERCISE_DESCRIPTION_KEY, currentExercise.description)
            Timber.d("on click list item - View currentExercise: ${currentExercise.name}")
            startActivityForResult(intent, exerciseLabAcitivtyRequestCode)
        }

        override fun onLongClick(v: View?): Boolean {
            Timber.d("on long click list item - View currentExercise: ${currentExercise.name}")
            return true
        }

    }
}
