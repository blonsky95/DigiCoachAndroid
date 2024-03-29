package com.tatoe.mydigicoach.ui.exercise

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.ui.util.ExerciseListAdapter
import kotlinx.android.synthetic.main.fragment_exercise_viewer.*
import timber.log.Timber
import com.tatoe.mydigicoach.ui.util.ClickListenerRecyclerView as ClickListenerRecyclerView
import com.tatoe.mydigicoach.*
import com.tatoe.mydigicoach.Utils.setProgressDialog
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.entity.Friend
import com.tatoe.mydigicoach.network.ExercisePackage
import com.tatoe.mydigicoach.network.FirebaseListenerService
import com.tatoe.mydigicoach.network.TransferPackage
import com.tatoe.mydigicoach.ui.fragments.PackageReceivedFragment
import com.tatoe.mydigicoach.ui.util.DataHolder
import com.tatoe.mydigicoach.utils.FirestoreReceiver
import com.tatoe.mydigicoach.viewmodels.ExerciseViewModel
import com.tatoe.mydigicoach.viewmodels.MainViewModel
import com.tatoe.mydigicoach.viewmodels.MyExerciseViewModelFactory
import com.tatoe.mydigicoach.viewmodels.MyMainViewModelFactory
import kotlinx.android.synthetic.main.fragment_exercise_viewer.share_button
import kotlinx.android.synthetic.main.fragment_exercise_viewer.social_button
import kotlinx.android.synthetic.main.fragment_exercise_viewer.textOne
import kotlinx.android.synthetic.main.item_holder_exercise.view.*
import java.util.ArrayList


class ExerciseViewerFragment : Fragment(), SearchView.OnQueryTextListener {
    private lateinit var exerciseViewModel: ExerciseViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExerciseListAdapter

    private lateinit var goToCreatorListener: ClickListenerRecyclerView
    private lateinit var selectorListener: ClickListenerRecyclerView

    private var allExercises = listOf<Exercise>()
    private var filteredExes = mutableListOf<Exercise>()

    private lateinit var receivedExercises: ArrayList<ExercisePackage>
    private lateinit var mReceiver: FirestoreReceiver
    private lateinit var mService: FirebaseListenerService

    //    private lateinit var fragmentManager: FragmentManager
    var allFriends = listOf<Friend>()
    private lateinit var selectedExercises: ArrayList<Exercise>

    lateinit var dialog: AlertDialog
    var mBound = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercise_viewer, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        exerciseViewModel = ViewModelProviders.of(
            this,
            MyExerciseViewModelFactory(activity!!.application)
        ).get(
            ExerciseViewModel::
            class.java
        )

        mainViewModel =
            ViewModelProviders.of(activity!!, MyMainViewModelFactory(activity!!.application))
                .get(MainViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mReceiver = FirestoreReceiver()

        recyclerView = libraryExercisesList as RecyclerView
        initAdapterListeners()

        setUpAdapter(ExerciseListAdapter.DEFAULT_LAYOUT)

        initObservers()

        share_button.setOnClickListener {
            modifyToSelectorUI(true)
            setUpAdapter(ExerciseListAdapter.SELECTOR_LAYOUT)
        }

        dialog = setProgressDialog(activity!!, "Talking with cloud...")

        social_button.setOnClickListener {
            mainViewModel.displayPackageReceiverFragmentType.postValue(PackageReceivedFragment.TRANSFER_PACKAGE_EXERCISE)
//            mainViewModel.displayFragmentById.postValue(MainViewModel.PACKAGE_DISPLAYER)
            //update a value in view model which makes mainactivity display the received packages framgnet
        }

        search_view.setOnQueryTextListener(this)
        addExerciseLayout.setOnClickListener {
            Timber.d("Exercise Viewer --> Exercise creator")

            val intent = Intent(activity, ExerciseCreator::class.java)
            intent.putExtra(ExerciseCreator.OBJECT_ACTION, ExerciseCreator.OBJECT_NEW)
            startActivity(intent)

        }
    }

    private fun setUpAdapter(layoutType: Int = ExerciseListAdapter.DEFAULT_LAYOUT) {
        adapter = ExerciseListAdapter(activity!!)

        when (layoutType) {
            ExerciseListAdapter.DEFAULT_LAYOUT -> {
                adapter.setOnClickInterface(goToCreatorListener)
            }
            ExerciseListAdapter.SELECTOR_LAYOUT -> {
                selectedExercises = arrayListOf()
                adapter.setOnClickInterface(selectorListener)
            }
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity!!)
        adapter.setExercises(filteredExes)
    }

    private fun modifyToSelectorUI(selectorUI: Boolean) {
        if (selectorUI) {
            addExerciseLayout.visibility = View.GONE
            share_button.visibility = View.GONE
            cancel_btn.visibility = View.VISIBLE
            share_btn.visibility = View.VISIBLE
            textView4.visibility = View.VISIBLE
            textView4.text = "Tap exercises you want to send"
            share_btn.setOnClickListener {
                if (selectedExercises.isNotEmpty()) {
                    mainViewModel.exercisesToSend.postValue(selectedExercises)
                } else {
                    Toast.makeText(activity, "No exercises selected", Toast.LENGTH_SHORT).show()                }
            }

            cancel_btn.setOnClickListener {
                modifyToSelectorUI(false)
                setUpAdapter(ExerciseListAdapter.DEFAULT_LAYOUT)
            }

        } else {
            addExerciseLayout.visibility = View.VISIBLE
            share_button.visibility = View.VISIBLE
            cancel_btn.visibility = View.GONE
            share_btn.visibility = View.GONE
            textView4.visibility = View.GONE
        }

    }

    private fun initObservers() {
        mainViewModel.receivedExercisesPackages.observe(this, Observer { exePackages ->
            updateSocialButtonNumber(exePackages.size)
        })
        exerciseViewModel.allExercises.observe(this, Observer { exercises ->
            exercises?.let {
                //                Timber.d("I WANNA SEE THIS: $exercises")
                filteredExes.clear()
                if (it.isEmpty()) {
                    ifEmptyText.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    ifEmptyText.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    adapter.setExercises(it)
                    allExercises = it
                    filteredExes.addAll(allExercises)
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

        exerciseViewModel.allFriends.observe(this, Observer { friends ->
            allFriends = friends
        })
    }

    private fun updateSocialButtonNumber(number:Int) {
        if (number==0) {
            textOne.visibility = View.GONE
            return
        }
        if (number > 9) {
            textOne.visibility = View.VISIBLE
            textOne.text = "9+"
        } else {
            textOne.visibility = View.VISIBLE
            textOne.text = number.toString()
        }
    }

    private fun initAdapterListeners() {
        goToCreatorListener = object : ClickListenerRecyclerView {
            override fun onClick(view: View, position: Int) {
                super.onClick(view, position)

                val intent = Intent(activity!!, ExerciseCreator::class.java)
                intent.putExtra(ExerciseCreator.OBJECT_ACTION, ExerciseCreator.OBJECT_VIEW)
                updateUpdatingExercise(position)

                startActivity(intent)

            }
        }

        selectorListener = object : ClickListenerRecyclerView {
            override fun onClick(view: View, position: Int) {
                super.onClick(view, position)
                val clickedExe = filteredExes[position]
                if (!selectedExercises.contains(clickedExe)) {
                    view.linearLayoutExerciseHolder.setBackgroundColor(resources.getColor(R.color.lightestBlue)) //dis ting is not working
                    selectedExercises.add(clickedExe)

                } else {
                    // (0x00000000) mean fully transparent
                    view.linearLayoutExerciseHolder.setBackgroundColor(resources.getColor(R.color.white))
                    selectedExercises.remove(clickedExe)
                }
                val string = "Exercise count: ${selectedExercises.size}"
                textView4.text = string
            }
        }

    }

    private fun updateUpdatingExercise(position: Int) {
        DataHolder.activeExerciseHolder = filteredExes[position]
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(filterText: String?): Boolean {
            adapter.setExercises(getFilteredExes(filterText))

        return true
    }

    private fun getFilteredExes(filterText: String?): List<Exercise> {
        return if (filterText != null && filterText.isNotEmpty()) {
            filteredExes.clear()

            val text = filterText.toLowerCase()
            for (exe in allExercises) {
                if (exe.name.toLowerCase().contains(text)) {
                    filteredExes.add(exe)
                }
            }
            filteredExes
        } else {
            allExercises
        }
    }

}
