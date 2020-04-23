package com.tatoe.mydigicoach.ui.exercise

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.ui.util.ExerciseListAdapter
import kotlinx.android.synthetic.main.activity_exercise_viewer.*
import timber.log.Timber
import com.tatoe.mydigicoach.ui.util.ClickListenerRecyclerView as ClickListenerRecyclerView
import com.tatoe.mydigicoach.*
import com.tatoe.mydigicoach.Utils.setProgressDialog
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.network.ExercisePackage
import com.tatoe.mydigicoach.network.MyCustomFirestoreExercise
import com.tatoe.mydigicoach.ui.HomeScreen
import com.tatoe.mydigicoach.ui.util.DataHolder
import com.tatoe.mydigicoach.viewmodels.ExerciseViewerViewModel
import com.tatoe.mydigicoach.viewmodels.MyExerciseViewerViewModelFactory


class ExerciseViewer : AppCompatActivity() {
    private lateinit var exerciseViewerViewModel: ExerciseViewerViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExerciseListAdapter

    private lateinit var goToCreatorListener: ClickListenerRecyclerView
    private lateinit var itemSelectorListener: ClickListenerRecyclerView
    private var selectedIndexes = arrayListOf<Int>()

    private lateinit var allExercises: List<Exercise>

//    private var db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_viewer)
        title = "Exercise Viewer"

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        home_button.setOnClickListener {
            startActivity(Intent(this, HomeScreen::class.java))
        }

        social_button.setOnClickListener {
            var receivedExercises = DataHolder.receivedExercises
            var title = "New Exercises"
            var text = "You have not received any new exercises"
            var dialogPositiveNegativeHandler: DialogPositiveNegativeHandler? = null

            if (receivedExercises.isNotEmpty()) {
                val exePackage = receivedExercises[0]
                text="Import ${exePackage.firestoreExercise!!.mName} from your friend ${exePackage.mSender}"
                dialogPositiveNegativeHandler = object :DialogPositiveNegativeHandler {
                    override fun onPositiveButton(inputText:String) {
                        super.onPositiveButton(inputText)
                        exerciseViewerViewModel.insertExercise(exePackage.firestoreExercise.toExercise())
                        exerciseViewerViewModel.updateTransferExercise(exePackage,ExercisePackage.STATE_SAVED)
                        //update state in firestore to SAVED
                    }

                    override fun onNegativeButton() {
                        super.onNegativeButton()
                        exerciseViewerViewModel.updateTransferExercise(exePackage,ExercisePackage.STATE_REJECTED)
                    }

            }

            }
            Utils.getInfoDialogView(this,title,text,dialogPositiveNegativeHandler)
        }

        recyclerView = recyclerview as RecyclerView
//        exportBtn.visibility = View.GONE
        initAdapterListeners()

        adapter = ExerciseListAdapter(this)
        adapter.setOnClickInterface(goToCreatorListener)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        exerciseViewerViewModel = ViewModelProviders.of(this, MyExerciseViewerViewModelFactory(application)).get(ExerciseViewerViewModel::class.java)

        exerciseViewerViewModel.allExercises.observe(this, Observer { exercises ->
            exercises?.let {
//                Timber.d("I WANNA SEE THIS: $exercises")

                if (it.isEmpty()) {
                    ifEmptyText.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    ifEmptyText.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    adapter.setExercises(it)
                    allExercises = it
                }
            }
        })


        val dialog = setProgressDialog(this, "Talking with cloud...")

        exerciseViewerViewModel.getIsLoading().observe(this, Observer { isLoading ->
            if (isLoading){
                dialog.show()

            } else {
                dialog.hide()
            }
        })

        addExerciseBtn.setOnClickListener {
            Timber.d("Exercise Viewer --> Exercise creator")

            val intent = Intent(this, ExerciseCreator::class.java)
            intent.putExtra(ExerciseCreator.OBJECT_ACTION, ExerciseCreator.OBJECT_NEW)
            startActivity(intent)

        }

//        getButton.setOnClickListener {
//
//
//            //if this works - think of exercises/blocks/days how to get references to exercises ( forget blocks)
//            Utils.getInfoDialogView(this,title.toString(),"Replace for your cloud exercises?",object:
//                DialogPositiveNegativeHandler {
//                override fun onPositiveButton(editTextText:String) {
//                    super.onPositiveButton(editTextText)
//                    exerciseViewerViewModel.getExercisesFromFirestore()
//                }
//            })
//        }
//
//        postButton.setOnClickListener {
//            //post stuff to firestore
//            Utils.getInfoDialogView(this,title.toString(),"Make this your cloud exercises?",object:
//                DialogPositiveNegativeHandler {
//
//                override fun onPositiveButton(editTextText:String) {
//                    super.onPositiveButton(editTextText)
//                    exerciseViewerViewModel.postExercisesToFirestore(allExercises)
//                }
//            })
//        }
    }

    private fun initAdapterListeners() {
        goToCreatorListener = object : ClickListenerRecyclerView {
            override fun onClick(view: View, position: Int) {
                super.onClick(view, position)

                val intent = Intent(this@ExerciseViewer, ExerciseCreator::class.java)
                intent.putExtra(ExerciseCreator.OBJECT_ACTION, ExerciseCreator.OBJECT_VIEW)
                updateUpdatingExercise(position)

                startActivity(intent)

            }
        }

//        itemSelectorListener = object : ClickListenerRecyclerView {
//            override fun onClick(view: View, position: Int) {
//                super.onClick(view, position)
//                Timber.d("$position was clicked, selected before: $selectedIndexes")
//
//                if (!selectedIndexes.contains(position)) {
//                    view.alpha = 0.5f
//                    selectedIndexes.add(position)
//                } else {
//
//                    val iterator = selectedIndexes.iterator()
//                    while (iterator.hasNext()) {
//                        val y = iterator.next()
//                        if (y == position) {
//                            view.alpha = 1.0f
//                            iterator.remove()
//                            break
//                        }
//                    }
//
//                }
//            }
//        }
    }
//    private fun updateAdapterListener(newListener: ClickListenerRecyclerView) {
//        adapter = ExerciseListAdapter(this)
//        adapter.setOnClickInterface(newListener)
//        recyclerView.adapter = adapter
//        adapter.setExercises(allExercises)
//    }

    private fun updateUpdatingExercise(position: Int) {

        var clickedExercise = exerciseViewerViewModel.allExercises.value?.get(position)

        if (clickedExercise != null) {
            DataHolder.activeExerciseHolder = clickedExercise
        } else {
            Timber.d("upsy error")
        }

    }


}
