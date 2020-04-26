package com.tatoe.mydigicoach.ui.exercise

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.tatoe.mydigicoach.ui.util.ExerciseListAdapter
import kotlinx.android.synthetic.main.activity_exercise_viewer.*
import timber.log.Timber
import com.tatoe.mydigicoach.ui.util.ClickListenerRecyclerView as ClickListenerRecyclerView
import com.tatoe.mydigicoach.*
import com.tatoe.mydigicoach.Utils.setProgressDialog
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.network.FirebaseListenerService
import com.tatoe.mydigicoach.network.ExercisePackage
import com.tatoe.mydigicoach.ui.HomeScreen
import com.tatoe.mydigicoach.ui.util.DataHolder
import com.tatoe.mydigicoach.utils.FirestoreReceiver
import com.tatoe.mydigicoach.viewmodels.ExerciseViewerViewModel
import com.tatoe.mydigicoach.viewmodels.MyExerciseViewerViewModelFactory
import java.util.ArrayList


class ExerciseViewer : AppCompatActivity() {
    private lateinit var exerciseViewerViewModel: ExerciseViewerViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExerciseListAdapter

    private lateinit var goToCreatorListener: ClickListenerRecyclerView
    private lateinit var itemSelectorListener: ClickListenerRecyclerView
    private var selectedIndexes = arrayListOf<Int>()

    private lateinit var allExercises: List<Exercise>
    private lateinit var receivedExercises: ArrayList<ExercisePackage>
    private lateinit var mReceiver: FirestoreReceiver
    private lateinit var mService: FirebaseListenerService

//    private var db = FirebaseFirestore.getInstance()

    //    override fun onRestart() {
//        receivedExercises = DataHolder.receivedExercises
//        super.onRestart()
//    }
    var mBound = false
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance

            val binder = service as FirebaseListenerService.LocalBinder
            mService = binder.getService()
            mBound = true
            observe()

        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    private fun observe() {
        mService.receivedExercisesLiveData.observe(this, Observer { lol ->
            receivedExercises = lol
            updateSocialButtonListener()
            updateSocialButtonNumber()
        })
    }

    override fun onStart() {
        super.onStart()
        // Bind to LocalService
        Intent(this, FirebaseListenerService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        mBound = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_viewer)
        title = "Exercise Viewer"

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        home_button.setOnClickListener {
            startActivity(Intent(this, HomeScreen::class.java))
            finish()
        }

        var firebaseUser = FirebaseAuth.getInstance().currentUser
        if (!FirebaseListenerService.isServiceRunning && firebaseUser != null) {
            startService(Intent(this, FirebaseListenerService::class.java))
        }
//        receivedExercises = DataHolder.receivedExercises
        mReceiver = FirestoreReceiver()

        recyclerView = recyclerview as RecyclerView
//        exportBtn.visibility = View.GONE
        initAdapterListeners()

        adapter = ExerciseListAdapter(this)
        adapter.setOnClickInterface(goToCreatorListener)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        exerciseViewerViewModel =
            ViewModelProviders.of(this, MyExerciseViewerViewModelFactory(application))
                .get(ExerciseViewerViewModel::class.java)

//        exerciseViewerViewModel.receivedExercises.observe(this, Observer { recExercises ->
//            receivedExercises = recExercises
//            updateSocialButtonNumber()
//            updateSocialButtonListener()
//        })

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
            if (isLoading) {
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

    private fun updateSocialButtonListener() {
        social_button.setOnClickListener {
            //            var receivedExercises = DataHolder.receivedExercises
            var title = "New Exercises"
            var text = "You have not received any new exercises"
            var dialogPositiveNegativeHandler: DialogPositiveNegativeHandler? = null

            if (receivedExercises.isNotEmpty()) {
                val exePackage = receivedExercises[0]
                text =
                    "Import ${exePackage.firestoreExercise!!.mName} from your friend ${exePackage.mSender}"
                dialogPositiveNegativeHandler = object : DialogPositiveNegativeHandler {
                    override fun onPositiveButton(inputText: String) {
                        super.onPositiveButton(inputText)
                        exerciseViewerViewModel.insertExercise(exePackage.firestoreExercise.toExercise())
                        exerciseViewerViewModel.updateTransferExercise(
                            exePackage,
                            ExercisePackage.STATE_SAVED
                        )
                        //update state in firestore to SAVED
                    }

                    override fun onNegativeButton() {
                        super.onNegativeButton()
                        exerciseViewerViewModel.updateTransferExercise(
                            exePackage,
                            ExercisePackage.STATE_REJECTED
                        )
                    }

                }

            }
            Utils.getInfoDialogView(this, title, text, dialogPositiveNegativeHandler)
        }
    }

    private fun updateSocialButtonNumber() {
        if (receivedExercises.isEmpty()) {
            textOne.visibility = View.GONE
            return
        }
        if (receivedExercises.size > 9) {
            textOne.visibility = View.VISIBLE
            textOne.text = "9+"
        } else {
            textOne.visibility = View.VISIBLE
            textOne.text = receivedExercises.size.toString()
        }
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

    }

    private fun updateUpdatingExercise(position: Int) {

        var clickedExercise = exerciseViewerViewModel.allExercises.value?.get(position)

        if (clickedExercise != null) {
            DataHolder.activeExerciseHolder = clickedExercise
        } else {
            Timber.d("upsy error")
        }

    }

}
