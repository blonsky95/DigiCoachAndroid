package com.tatoe.mydigicoach.ui.exercise

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.text.SpannableStringBuilder
import android.view.*
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
import com.tatoe.mydigicoach.ui.results.ResultsViewer
import com.tatoe.mydigicoach.ui.util.DataHolder
import kotlinx.android.synthetic.main.activity_exercise_creator.*
import kotlinx.android.synthetic.main.custom_dialog_window.view.*
import timber.log.Timber

class ExerciseCreator : AppCompatActivity() {

    private lateinit var linearLayout: LinearLayout

    private lateinit var newField: String

    private lateinit var rightButton: Button
    private lateinit var leftButton: Button
    private lateinit var centreButton: Button

    private lateinit var dataViewModel: DataViewModel

    private var updatingExercise: Exercise? = null
    private var exerciseFieldsMap = LinkedHashMap<String, String>()


    var menuItemRead: MenuItem? = null
    var menuItemEdit: MenuItem? = null

    lateinit var mAction: String

    var NEW_FIELD_VALUE = ""

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
        linearLayout = exercise_properties as LinearLayout

        rightButton = right_button
        leftButton = left_button
        centreButton = centre_button

        if (intent.hasExtra(EXERCISE_ACTION)) { //can only reach this with an intent extra
            mAction = intent.getStringExtra(EXERCISE_ACTION)
            if (mAction == EXERCISE_EDIT || mAction == EXERCISE_VIEW) {
//                if (DataHolder.activeExerciseHolder != null) {
                updatingExercise = DataHolder.activeExerciseHolder
                if (updatingExercise != null) {
                    exerciseFieldsMap = updatingExercise!!.getFieldsMap()
                }
//                }
            } else {
                exerciseFieldsMap["Name"] = ""
                exerciseFieldsMap["Description"] = ""
            }
            updateBodyUI(mAction)
            updateButtonUI(mAction)

        }
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

        createExerciseFieldsLayout()

        if (actionType == EXERCISE_NEW) {
            changeVisibility(linearLayout, false)
        }
        if (actionType == EXERCISE_EDIT) {
            changeVisibility(linearLayout, false)
        }
        if (actionType == EXERCISE_VIEW) {
            changeVisibility(linearLayout, true)
        }
    }

    private fun createExerciseFieldsLayout() {

        linearLayout.removeAllViews()

        for (entry in exerciseFieldsMap.entries) {
            var fieldTitleTextView = TextView(this)
            fieldTitleTextView.layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            fieldTitleTextView.text = entry.key
            fieldTitleTextView.typeface = Typeface.DEFAULT_BOLD

            linearLayout.addView(fieldTitleTextView)

            var fieldEditText = EditText(this)
            fieldEditText.layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            fieldEditText.hint = entry.value
            fieldEditText.text=SpannableStringBuilder(entry.value)

            linearLayout.addView(fieldEditText)

            var fieldInfoTextView = TextView(this)
            fieldInfoTextView.layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            fieldInfoTextView.text = entry.value

            linearLayout.addView(fieldInfoTextView)
        }

        Timber.d("Child count: ${linearLayout.childCount}")
    }

    private fun changeVisibility(layout: LinearLayout, isRead: Boolean) {

        var editTextVisibility = View.VISIBLE
        var textViewVisibility = View.GONE

        if (isRead) {
            editTextVisibility = View.GONE
            textViewVisibility = View.VISIBLE
        }

        for (index in 0 until layout.childCount) {
            //Here I could change checking the view type to checking if index is 0,1,2 like Im doing
            //with title fieldsHashMap, in case its not edit texts or to improve performance.
            val childView = layout.getChildAt(index)
            if (isFieldTitle(index)) {
                childView.visibility = View.VISIBLE
                continue
            }
            if (childView is EditText) {
                Timber.d("visibility of child $index changed to $editTextVisibility")

                childView.visibility = editTextVisibility
                continue

            }
            if (childView is TextView) {
                Timber.d("visibility of child $index changed to $textViewVisibility")

                childView.visibility = textViewVisibility
            }
        }
    }

    private fun isFieldTitle(index: Int): Boolean {
        //fieldsHashMap go in 3s, so childs 0,3,6,9... are alwasy titles
        return (index + 3) % 3 == 0
    }

    private fun getFieldContents(): LinkedHashMap<String, String> {

        var fieldsMap = LinkedHashMap<String, String>()
        for (i in 0 until linearLayout.childCount step 3) {
            Timber.d("child at $i is ${linearLayout.getChildAt(i)}")
            var keyString = (linearLayout.getChildAt(i) as TextView).text.toString()
            fieldsMap[keyString] =
                (linearLayout.getChildAt(i + 1) as EditText).text.trim().toString()
        }
        return fieldsMap
    }

    private val addButtonListener = View.OnClickListener {

        var newExerciseFields = getFieldContents()

        var newExercise = Exercise(newExerciseFields["Name"]!!, newExerciseFields["Description"]!!)
        newExercise.fieldsHashMap = newExerciseFields

        dataViewModel.insertExercise(newExercise)
        backToViewer()
    }
    private val updateButtonListener = View.OnClickListener {

        var updatingExerciseFields = getFieldContents()

        updatingExercise!!.name = updatingExerciseFields["Name"]!!
        updatingExercise!!.description = updatingExerciseFields["Description"]!!
        updatingExercise!!.fieldsHashMap = updatingExerciseFields

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
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("Add Field")
        val mAlertDialog = mBuilder.show()
        mDialogView.dialogEnterBtn.setOnClickListener {
            mAlertDialog.dismiss()
            newField = mDialogView.dialogEditText.text.toString().trim()
            addFieldLayout()
        }
        mDialogView.dialogCancelBtn.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

    private fun addFieldLayout() {
        //todo save what was written
        exerciseFieldsMap[newField] = ""

//        updateBodyUI(EXERCISE_EDIT)

        var fieldTitleTextView = TextView(this)
        fieldTitleTextView.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        fieldTitleTextView.text = newField
        fieldTitleTextView.typeface = Typeface.DEFAULT_BOLD

        linearLayout.addView(fieldTitleTextView)

        var fieldEditText = EditText(this)
        fieldEditText.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        fieldEditText.hint = NEW_FIELD_VALUE
        fieldEditText.text=SpannableStringBuilder(NEW_FIELD_VALUE)

        linearLayout.addView(fieldEditText)

        var fieldInfoTextView = TextView(this)
        fieldInfoTextView.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        fieldInfoTextView.text = NEW_FIELD_VALUE

        linearLayout.addView(fieldInfoTextView)

        changeVisibility(linearLayout,false)
    }

    private val historyButtonListener = View.OnClickListener {
        val intent = Intent(this, ResultsViewer::class.java)

//        DataHolder.activeExerciseHolder=updatingExercise
        intent.putExtra(ResultsViewer.RESULTS_ACTION, ResultsViewer.RESULTS_VIEW)
        intent.putExtra(ResultsViewer.RESULTS_EXE_ID, updatingExercise!!.exerciseId)

        startActivity(intent)
    }

    private fun backToViewer() {
        val intent = Intent(this, ExerciseViewer::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
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


}
