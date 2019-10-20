package com.tatoe.mydigicoach.ui.exercise

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.tatoe.mydigicoach.DataViewModel
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.util.DataHolder
import kotlinx.android.synthetic.main.activity_exercise_creator.*
import kotlinx.android.synthetic.main.custom_dialog_window.view.*
import timber.log.Timber

class ExerciseCreator : AppCompatActivity() {

    private lateinit var exerciseName: String
    private lateinit var exerciseDesc: String

    private lateinit var nameEditText: EditText
    private lateinit var descEditText: EditText
    private lateinit var nameTextView: TextView
    private lateinit var descTextView: TextView

    private lateinit var linearLayout: LinearLayout

    private lateinit var newField: String

    private lateinit var rightButton: Button
    private lateinit var leftButton: Button
    private lateinit var centreButton: Button

    private lateinit var dataViewModel: DataViewModel

    private var updatingExercise: Exercise? = null

    var menuItemRead: MenuItem? = null
    var menuItemEdit: MenuItem? = null

    lateinit var mAction: String

    companion object {
        var EXERCISE_ACTION = "exercise_action"
        var EXERCISE_NEW = "exercise_new"
        var EXERCISE_EDIT = "exercise_edit"
        var EXERCISE_VIEW = "exercise_view"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_creator)
        title = "Exercise Creator"

        setSupportActionBar(findViewById(R.id.my_toolbar))

        dataViewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)

        nameEditText = EditText1
        descEditText = EditText2
        nameTextView = TextView1
        descTextView = TextView2

        linearLayout = exercise_properties as LinearLayout

        rightButton = right_button
        leftButton = left_button
        centreButton = centre_button

        if (intent.hasExtra(EXERCISE_ACTION)) { //can only reach this with an intent extra
            mAction = intent.getStringExtra(EXERCISE_ACTION)
            if (mAction == EXERCISE_EDIT || mAction == EXERCISE_VIEW) {
//                if (DataHolder.activeExerciseHolder != null) {
                    updatingExercise = DataHolder.activeExerciseHolder
//                }
            }
            updateButtonUI(mAction)
            updateBodyUI(mAction)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.exercise_creator_toolbar, menu)
        menuItemEdit = menu?.findItem(R.id.action_edit)
        menuItemRead = menu?.findItem(R.id.action_read)
        when (mAction) {
            EXERCISE_EDIT -> {
                updateToolbarItemVisibility(menuItemEdit, false)
                updateToolbarItemVisibility(menuItemRead, true)
            }
            EXERCISE_VIEW -> {
                updateToolbarItemVisibility(menuItemEdit, true)
                updateToolbarItemVisibility(menuItemRead, false)
            }
            EXERCISE_NEW -> {
                updateToolbarItemVisibility(menuItemEdit, false)
                updateToolbarItemVisibility(menuItemRead, false)
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.action_back -> {
            super.onBackPressed()
            true
        }
        R.id.action_edit -> {
            updateButtonUI(EXERCISE_EDIT)
            updateBodyUI(EXERCISE_EDIT)
            updateToolbarItemVisibility(menuItemEdit, false)
            updateToolbarItemVisibility(menuItemRead, true)
            true
        }
        R.id.action_read -> {
            updateButtonUI(EXERCISE_VIEW)
            updateBodyUI(EXERCISE_VIEW)
            updateToolbarItemVisibility(menuItemEdit, true)
            updateToolbarItemVisibility(menuItemRead, false)
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    private fun updateToolbarItemVisibility(menuItem: MenuItem?, isVisible: Boolean) {
        menuItem?.isVisible = isVisible
    }


    private fun updateButtonUI(actionType: String) {
        if (actionType == EXERCISE_NEW) {
            rightButton.visibility = View.VISIBLE
            rightButton.text = "ADD"
            rightButton.setOnClickListener(addButtonListener)

            centreButton.visibility = View.VISIBLE
            leftButton.visibility = View.VISIBLE
            centreButton.setOnClickListener(addFieldButtonListener)

            return
        } else {

            if (actionType == EXERCISE_EDIT) {
                rightButton.visibility = View.VISIBLE
                rightButton.text = "UPDATE"
                rightButton.setOnClickListener(updateButtonListener)

                centreButton.visibility = View.VISIBLE
                centreButton.text = "ADD FIELD"
                centreButton.setOnClickListener(addFieldButtonListener)

                leftButton.visibility = View.VISIBLE
                leftButton.text = "DELETE"
                leftButton.setOnClickListener(deleteButtonListener)
            }
            if (actionType == EXERCISE_VIEW) {
                rightButton.visibility = View.VISIBLE
                rightButton.text = "HISTORY"
                rightButton.setOnClickListener(historyButtonListener)

                centreButton.visibility = View.INVISIBLE

                leftButton.visibility = View.INVISIBLE
            }
        }
    }

    private fun updateBodyUI(actionType: String) {

        //try examining childs of layout and changing visibility of edit texts and of text views



        if (actionType == EXERCISE_NEW) {
            changeVisibility(linearLayout,false)
//            isRead=View.VISIBLE
//            nameTextView.visibility = View.GONE
//            descTextView.visibility = View.GONE
//
//            nameEditText.visibility = View.VISIBLE
//            descEditText.visibility = View.VISIBLE
        }
        if (actionType == EXERCISE_EDIT) {
            changeVisibility(linearLayout,false)
//            isRead=false
//            nameTextView.visibility = View.GONE
//            descTextView.visibility = View.GONE
//
//            nameEditText.visibility = View.VISIBLE
//            nameEditText.text = SpannableStringBuilder(DataHolder.activeExerciseHolder?.name)
//            descEditText.visibility = View.VISIBLE
//            descEditText.text = SpannableStringBuilder(DataHolder.activeExerciseHolder?.description)

        }
        if (actionType == EXERCISE_VIEW) {
            changeVisibility(linearLayout,true)

//            isRead=true
//            nameEditText.visibility = View.GONE
//            descEditText.visibility = View.GONE
//
//            nameTextView.visibility = View.VISIBLE
//            nameTextView.text = SpannableStringBuilder(DataHolder.activeExerciseHolder?.name)
//            descTextView.visibility = View.VISIBLE
//            descTextView.text = SpannableStringBuilder(DataHolder.activeExerciseHolder?.description)
        }


    }

    private fun changeVisibility(layout:LinearLayout, isRead:Boolean){

        //todo have to import the text of the fields from the updating exercise in form of array
        //todo add a method in Exercise that returns an array with [Name, desc, extra1, extra2...]
        // get this array and fill views correspondingly
        var editTextVisibility = View.VISIBLE
        var textViewVisibility = View.GONE

        if (isRead) {
            editTextVisibility=View.GONE
            textViewVisibility=View.VISIBLE
        }
        for (index in 0 until layout.childCount) {
            //Here I could change checking the view type to checking if index is 0,1,2 like Im doing
            //with title fields, in case its not edit texts or to improve performance.
            var childView = layout.getChildAt(index)
            if (isFieldTitle(index)) {
                childView.visibility=View.VISIBLE
                continue
            }
            if (childView is EditText) {
                childView.text=SpannableStringBuilder("im an edit text")
                childView.visibility=editTextVisibility

            }
            if (childView is TextView) {
                childView.text="im a text view"
                childView.visibility=textViewVisibility
            }
        }
    }

    private fun isFieldTitle(index: Int): Boolean {
        //fields go in 3s, so childs 0,3,6,9... are alwasy titles
        return (index+3)%3==0
    }

    private val addButtonListener = View.OnClickListener {
        exerciseName = nameEditText.text.trim().toString()
        exerciseDesc = descEditText.text.trim().toString()

        var newExercise = Exercise(exerciseName, exerciseDesc)
        dataViewModel.insertExercise(newExercise)
        backToViewer()
    }

    private val updateButtonListener = View.OnClickListener {
        exerciseName = nameEditText.text.trim().toString()
        exerciseDesc = descEditText.text.trim().toString()

        updatingExercise!!.name = exerciseName
        updatingExercise!!.description = exerciseDesc
        dataViewModel.updateExercise(updatingExercise!!)

        backToViewer()
    }

    private val deleteButtonListener = View.OnClickListener {
        dataViewModel.deleteExercise(updatingExercise!!)

        backToViewer()
    }

    private val addFieldButtonListener = View.OnClickListener {
        generateDialog()
    }

    private fun generateDialog() {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_window, null)
        mDialogView.dialogTextTextView.visibility = View.INVISIBLE
        mDialogView.dialogEditText.hint = "New field name"
        mDialogView.dialogEditText.inputType = InputType.TYPE_CLASS_TEXT
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("Add Field")
        //show dialog
        val mAlertDialog = mBuilder.show()
        //login button click of custom layout
        mDialogView.dialogEnterBtn.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()
            newField = mDialogView.dialogEditText.text.toString().trim()
            addFieldLayout()
        }
        //cancel button click of custom layout
        mDialogView.dialogCancelBtn.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()
        }
    }

    private fun addFieldLayout() {
        //todo ok add the layout
    }

    private val historyButtonListener = View.OnClickListener {
        val intent = Intent(this, ExerciseResults::class.java)

//        DataHolder.activeExerciseHolder=updatingExercise
        intent.putExtra(ExerciseResults.RESULTS_ACTION, ExerciseResults.RESULTS_VIEW)
        intent.putExtra(ExerciseResults.RESULTS_EXE_ID, updatingExercise!!.exerciseId)

        startActivity(intent)
    }

    private fun backToViewer() {
        val intent = Intent(this, ExerciseViewer::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }


}
