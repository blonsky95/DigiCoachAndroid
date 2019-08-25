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
import com.tatoe.mydigicoach.ui.util.ExerciseListAdapter
import kotlinx.android.synthetic.main.activity_exercise_viewer.*
import timber.log.Timber
import com.tatoe.mydigicoach.ui.util.ClickListenerRecyclerView as ClickListenerRecyclerView
import android.widget.Toast
import com.tatoe.mydigicoach.ui.util.DataHolder


class ExerciseViewer : AppCompatActivity() {
    private lateinit var dataViewModel: DataViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExerciseListAdapter


    private val exerciseCreatorAcitivtyRequestCode = 1

    //navigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.tatoe.mydigicoach.R.layout.activity_exercise_viewer)
        title = "Exercise Viewer"

        recyclerView = recyclerview as RecyclerView

        val myListener = object : ClickListenerRecyclerView {
            override fun onClick(view: View, position: Int) {
                super.onClick(view, position)

                Toast.makeText(this@ExerciseViewer, "$position was clicked", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@ExerciseViewer, ExerciseCreator::class.java)
                intent.putExtra(ExerciseCreator.EXERCISE_ACTION, ExerciseCreator.EXERCISE_UPDATE)
                updateUpdatingExercise(position)

                startActivityForResult(intent, exerciseCreatorAcitivtyRequestCode)

            }
        }

        adapter = ExerciseListAdapter(this, myListener)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        dataViewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)

        dataViewModel.allExercises.observe(this, Observer { exercises ->
            exercises?.let {
                Timber.d("PTG all exercises observer triggered: ${exercises.toString()}")

                if (it.isEmpty()) {
                    ifEmptyText.visibility=View.VISIBLE
                    recyclerView.visibility=View.GONE
                } else {
                    ifEmptyText.visibility=View.GONE
                    recyclerView.visibility=View.VISIBLE
                    adapter.setExercises(it)
                }
            }
        })

        addExerciseBtn.setOnClickListener {
            Timber.d("Exercise Viewer --> Exercise creator")

            val intent = Intent(this, ExerciseCreator::class.java)
            intent.putExtra(ExerciseCreator.EXERCISE_ACTION, ExerciseCreator.EXERCISE_NEW)
            startActivityForResult(intent, exerciseCreatorAcitivtyRequestCode)

        }
    }

    private fun updateUpdatingExercise(position: Int) {

        var clickedExercise = dataViewModel.allExercises.value?.get(position)

        if (clickedExercise != null) {
            DataHolder.activeExerciseHolder = clickedExercise
        } else {
            Timber.d("upsy error")
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == exerciseCreatorAcitivtyRequestCode && resultCode == ExerciseCreator.EXERCISE_NEW_RESULT_CODE) {

            val newExercise = DataHolder.newExerciseHolder
            dataViewModel.insertExercise(newExercise)

//            val actionNotification = Snackbar.make(recyclerView, "Exercise added", Snackbar.LENGTH_LONG)
//            actionNotification.show()
        }
        if (requestCode == exerciseCreatorAcitivtyRequestCode && resultCode == ExerciseCreator.EXERCISE_UPDATE_RESULT_CODE) {

            val updatedExercise = DataHolder.activeExerciseHolder
            Timber.d("PTG exercise trying to be updated: ${updatedExercise.name} ${updatedExercise.description}")
            dataViewModel.updateExercise(updatedExercise)

//            val actionNotification = Snackbar.make(recyclerView, "Exercise updated", Snackbar.LENGTH_LONG)
//            actionNotification.show()
        }
        if (requestCode == exerciseCreatorAcitivtyRequestCode && resultCode == ExerciseCreator.EXERCISE_DELETE_RESULT_CODE) {

            val deleteExercise = DataHolder.activeExerciseHolder
            Timber.d("PTG exercise trying to be deleted: ${deleteExercise.name} ${deleteExercise.description}")
            dataViewModel.deleteExercise(deleteExercise)
//            val actionNotification = Snackbar.make(recyclerView, "Exercise deleted", Snackbar.LENGTH_LONG)
//            actionNotification.show()
        }
        if (requestCode == exerciseCreatorAcitivtyRequestCode && resultCode == ExerciseCreator.EXERCISE_FAIL_RESULT_CODE) {
            //accounts for user pressing back
//            val actionNotification = Snackbar.make(recyclerView, "Failure is an option", Snackbar.LENGTH_LONG)
//            actionNotification.show()
        } else {
        }
    }

}
