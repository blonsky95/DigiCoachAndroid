package com.tatoe.mydigicoach.ui.exercise

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
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
import com.tatoe.mydigicoach.entity.Friend
import com.tatoe.mydigicoach.network.FirebaseListenerService
import com.tatoe.mydigicoach.network.ExercisePackage
import com.tatoe.mydigicoach.network.TransferPackage
import com.tatoe.mydigicoach.ui.HomeScreen
import com.tatoe.mydigicoach.ui.fragments.ShareToFriendsFragment
import com.tatoe.mydigicoach.ui.util.DataHolder
import com.tatoe.mydigicoach.utils.FirestoreReceiver
import com.tatoe.mydigicoach.viewmodels.ExerciseViewModel
import com.tatoe.mydigicoach.viewmodels.MyExerciseViewModelFactory
import kotlinx.android.synthetic.main.activity_exercise_viewer.home_button
import kotlinx.android.synthetic.main.activity_exercise_viewer.share_button
import kotlinx.android.synthetic.main.activity_exercise_viewer.social_button
import kotlinx.android.synthetic.main.activity_exercise_viewer.textOne
import java.util.ArrayList


class ExerciseViewer : AppCompatActivity(),
    ShareToFriendsFragment.OnFriendSelectedListenerInterface {
    private lateinit var exerciseViewModel: ExerciseViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExerciseListAdapter

    private lateinit var goToCreatorListener: ClickListenerRecyclerView

    private lateinit var allExercises: List<Exercise>
    private lateinit var receivedExercises: ArrayList<ExercisePackage>
    private lateinit var mReceiver: FirestoreReceiver
    private lateinit var mService: FirebaseListenerService

    private lateinit var fragmentManager: FragmentManager
    var allFriends = listOf<Friend>()
    var selectedExercises = listOf<Exercise>()

    lateinit var dialog:AlertDialog
    var mBound = false

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
        mReceiver = FirestoreReceiver()

        recyclerView = libraryExercisesList as RecyclerView
        initAdapterListeners()

        adapter = ExerciseListAdapter(this)
        adapter.setOnClickInterface(goToCreatorListener)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        exerciseViewModel =
            ViewModelProviders.of(this, MyExerciseViewModelFactory(application))
                .get(ExerciseViewModel::class.java)

        initObservers()


        share_button.setOnClickListener {
            //todo make this analogous to MonthViewer and simple to read

            share_button.visibility = View.GONE

            addExerciseLayout.visibility = View.GONE

            cancel_btn.visibility = View.VISIBLE

            cancel_btn.setOnClickListener {
                modifyUIChangename()
            }


            share_btn.visibility = View.VISIBLE
            share_btn.setOnClickListener {
                fragmentManager = supportFragmentManager
                setUpFragment()
            }

        }

        dialog = setProgressDialog(this, "Talking with cloud...")

        addExerciseLayout.setOnClickListener {
            Timber.d("Exercise Viewer --> Exercise creator")

            val intent = Intent(this, ExerciseCreator::class.java)
            intent.putExtra(ExerciseCreator.OBJECT_ACTION, ExerciseCreator.OBJECT_NEW)
            startActivity(intent)

        }
    }

    fun modifyUIChangename() {
        //todo fix this shit
        addExerciseLayout.visibility = View.VISIBLE
        share_button.visibility = View.VISIBLE
        cancel_btn.visibility = View.GONE
        share_btn.visibility = View.GONE
    }

    private fun initObservers() {
        exerciseViewModel.allExercises.observe(this, Observer { exercises ->
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

        exerciseViewModel.getIsLoading().observe(this, Observer { isLoading ->
            if (isLoading) {
                dialog.show()

            } else {
                dialog.hide()
            }
        })

        exerciseViewModel.allFriends.observe(this, Observer {friends ->
            allFriends=friends
        })
    }

    override fun onFriendSelected(friend: Friend) {
        Toast.makeText(this, "Sending to ${friend.username}!", Toast.LENGTH_SHORT).show()
        exerciseViewModel.sendExercisesToUser(selectedExercises, friend)
        fragmentManager.popBackStack()
        modifyUIChangename()
    }

    override fun onCancelSelected() {
        fragmentManager.popBackStack()
        modifyUIChangename()
        //back to normal
    }

    private fun setUpFragment() {

        var fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.setCustomAnimations(
            R.anim.slide_in_up,
            R.anim.slide_in_down,
            R.anim.slide_out_down,
            R.anim.slide_out_up
        )

        fragmentTransaction.addToBackStack("A")
            .replace(R.id.frame_layout, ShareToFriendsFragment.newInstance(allFriends))
        fragmentTransaction.commit()

    }

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

    private fun updateSocialButtonListener() {
        social_button.setOnClickListener {
            //            var receivedExercises = DataHolder.receivedExercises
            val title = "New Exercises"
            var text = "You have not received any new exercises"
            var dialogPositiveNegativeHandler: DialogPositiveNegativeHandler? = null

            if (receivedExercises.isNotEmpty()) {
                val exePackage = receivedExercises[0]
                text =
                    "Import ${exePackage.firestoreExercise!!.mName} from your friend ${exePackage.mSender}"
                dialogPositiveNegativeHandler = object : DialogPositiveNegativeHandler {
                    override fun onPositiveButton(inputText: String) {
                        super.onPositiveButton(inputText)
                        attemptImportExercise(exePackage)
                    }

                    override fun onNegativeButton() {
                        super.onNegativeButton()
                        exerciseViewModel.updateTransferExercise(
                            exePackage,
                            TransferPackage.STATE_REJECTED
                        )
                    }
                }
            }
            Utils.getInfoDialogView(this, title, text, dialogPositiveNegativeHandler)
        }
    }

    //todo think that most of this stuff should be moved to the view model
    private fun attemptImportExercise(exePackage: ExercisePackage) {
        val exe = exePackage.firestoreExercise!!.toExercise()
        if (theSameExercise(exe) != null) {
            val title = "Overwrite"
            val text = "You already have this exercise, do you want to overwrite it?"
            val dialogPositiveNegativeHandler = object : DialogPositiveNegativeHandler {
                override fun onPositiveButton(inputText: String) {
                    super.onPositiveButton(inputText)
                    removeExercise(theSameExercise(exe)!!)
                    insertExercise(exePackage)
                }

                override fun onNegativeButton() {
                    super.onNegativeButton()
                    rejectExercisePackage(exePackage)
                }
            }
            Utils.getInfoDialogView(this, title, text, dialogPositiveNegativeHandler)
        } else {
            insertExercise(exePackage)
        }
    }


    private fun theSameExercise(exe: Exercise): Exercise? {
        for (exercise in allExercises) {
            if (exe.md5 == exercise.md5) {
                return exercise
            }
        }
        return null
    }

    private fun insertExercise(exePackage: ExercisePackage) {
        exerciseViewModel.insertExercise(exePackage.firestoreExercise!!.toExercise())
        exerciseViewModel.updateTransferExercise(
            exePackage,
            TransferPackage.STATE_SAVED
        )
        //update state in firestore to SAVED
    }

    private fun removeExercise(theSameExercise: Exercise) {
        exerciseViewModel.deleteExercise(theSameExercise)
    }

    private fun rejectExercisePackage(exePackage: ExercisePackage) {
        exerciseViewModel.updateTransferExercise(
            exePackage,
            TransferPackage.STATE_REJECTED
        )
        //update state in firestore to REJECTED
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

        var clickedExercise = exerciseViewModel.allExercises.value?.get(position)

        if (clickedExercise != null) {
            DataHolder.activeExerciseHolder = clickedExercise
        } else {
            Timber.d("upsy error")
        }

    }
}
