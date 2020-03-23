package com.tatoe.mydigicoach.ui.results

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
import com.tatoe.mydigicoach.viewmodels.DataViewModel
import com.tatoe.mydigicoach.ExerciseResults
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.exercise.ExerciseCreator.Companion.OBJECT_ACTION
import com.tatoe.mydigicoach.ui.exercise.ExerciseCreator.Companion.OBJECT_EDIT
import com.tatoe.mydigicoach.ui.exercise.ExerciseCreator.Companion.OBJECT_NEW
import com.tatoe.mydigicoach.ui.exercise.ExerciseCreator.Companion.OBJECT_VIEW
import com.tatoe.mydigicoach.ui.util.DataHolder
import kotlinx.android.synthetic.main.activity_exercise_creator.*
import kotlinx.android.synthetic.main.activity_results_creator.left_button
import kotlinx.android.synthetic.main.activity_results_creator.right_button
import kotlinx.android.synthetic.main.plottable_dialog_window.view.*
import timber.log.Timber
import java.util.ArrayList

class ResultsCreator : AppCompatActivity() {

    private lateinit var rightButton: Button
    private lateinit var leftButton: Button
    private lateinit var centreButton: Button

    private lateinit var linearLayout: LinearLayout

    private var sResultFieldsMap = HashMap<Int, Pair<String, String>>()
    private var sResultsArrayList: ArrayList<HashMap<Int, Pair<String, String>>> = arrayListOf()


    private lateinit var dataViewModel: DataViewModel

    var menuItemRead: MenuItem? = null
    var menuItemEdit: MenuItem? = null

    lateinit var mAction: String

    var activeExercise: Exercise? = null
    private var resultDate = "unknown date"
    private var resultIndex = -1

    companion object {
        var RESULTS_DATE = "results_date"
        var RESULTS_EXE_ID = "results_exe_id"
        var RESULT_INDEX = "result_index"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_creator)
        title = "Exercise Result"


        setSupportActionBar(findViewById(R.id.my_toolbar))

        dataViewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)
        linearLayout = exercise_properties as LinearLayout

        rightButton = right_button
        leftButton = left_button
        centreButton = centre_button
        centreButton.visibility = View.INVISIBLE

        if (intent.hasExtra(OBJECT_ACTION)) { //can only reach this with an intent extra
            mAction = intent.getStringExtra(OBJECT_ACTION)

            if (intent.hasExtra(RESULTS_DATE)) {
                resultDate = intent.getStringExtra(RESULTS_DATE)
            }

            if (intent.hasExtra(RESULT_INDEX)) {
                resultIndex = intent.getIntExtra(RESULT_INDEX, -1)
            }

            activeExercise = DataHolder.activeExerciseHolder

            sResultFieldsMap = if (activeExercise?.exerciseResults!!.getFieldsMap().isNotEmpty()) {
                activeExercise?.exerciseResults!!.getFieldsMap()
            } else {
                ExerciseResults.getGenericFields()
            }

            if (activeExercise?.exerciseResults!!.getArrayListOfResults().isNotEmpty()) {
                sResultsArrayList = activeExercise?.exerciseResults!!.getArrayListOfResults()
            }

            Timber.d("ACTIVE EXERCISE IN CREATOR ${activeExercise!!}")

//            result_title_text_view.text = activeExercise!!.name
//            Timber.d("ACTION RECEIVED AT RESULTS CREATOR: $mAction $resultDate result index: $resultIndex")

            updateBodyUI(mAction)
            updateButtonUI(mAction)

        }
    }

    private fun updateButtonUI(actionType: String) {
        if (actionType == OBJECT_NEW) {
            rightButton.visibility = View.VISIBLE
            rightButton.text = "ADD"
            rightButton.setOnClickListener(addButtonListener)

            leftButton.visibility = View.INVISIBLE

            centreButton.visibility = View.VISIBLE
            centreButton.text = "ADD FIELD"
            centreButton.setOnClickListener(addFieldButtonListener)
            return
        } else {

            if (actionType == OBJECT_EDIT) {
                rightButton.visibility = View.VISIBLE
                rightButton.text = "UPDATE"
                rightButton.setOnClickListener(updateButtonListener)
//
                centreButton.visibility = View.VISIBLE
                centreButton.text = "ADD FIELD"
                centreButton.setOnClickListener(addFieldButtonListener)

                leftButton.visibility = View.VISIBLE
                leftButton.text = "DELETE"
                leftButton.setOnClickListener(deleteButtonListener)
            }
            if (actionType == OBJECT_VIEW) {
                rightButton.visibility = View.INVISIBLE
                leftButton.visibility = View.INVISIBLE
                centreButton.visibility = View.INVISIBLE
            }
        }
    }

    private fun updateBodyUI(actionType: String) {

        //try examining childs of layout and changing visibility of edit texts and of text views

        createExerciseFieldsLayout()

        if (actionType == OBJECT_NEW) {
            changeVisibility(linearLayout, false)
        }
        if (actionType == OBJECT_EDIT) {
            changeVisibility(linearLayout, false)
        }
        if (actionType == OBJECT_VIEW) {
            changeVisibility(linearLayout, true)
        }
    }

    private fun createExerciseFieldsLayout() {

        //todo change input of edit texts - if it si a plottable make it only numbers
        linearLayout.removeAllViews()

        var uiFieldsValues = sResultFieldsMap

        Timber.d(" MY TIMBER uiFieldValues: ${uiFieldsValues.values}")

//        for (entry in uiFieldsValues.entries) {
        for (i in 1 until uiFieldsValues.size) {

//            if (entry.key == ExerciseResults.DATE_KEY) {
//                continue
//            }
            //the current hashmap with the int for order and the key and value for this field
            var currentField = uiFieldsValues[i]

            var fieldEntryKey = currentField!!.first //first of pair - title of entry
            var fieldEntryValue = "" //second of pair - value of entry
            var entryHintString = "Type here"
            if (mAction != OBJECT_NEW) {
//                currentField = sResultsArrayList[resultIndex][i]
                sResultsArrayList[resultIndex][i]?.let {
                    fieldEntryKey = it.first
                    fieldEntryValue = it.second
//                entryValueTextString = "whats going on here"
//                }
                }
            }

            var fieldTitleTextView = TextView(this)
            fieldTitleTextView.layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            fieldTitleTextView.text = fieldEntryKey
            fieldTitleTextView.typeface = Typeface.DEFAULT_BOLD

            var fieldEditText = EditText(this)
            fieldEditText.layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            fieldEditText.hint = entryHintString
            fieldEditText.text = SpannableStringBuilder(fieldEntryValue)


            var fieldInfoTextView = TextView(this)
            fieldInfoTextView.layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            fieldInfoTextView.text = fieldEntryValue

            if (currentField.second == ExerciseResults.PLOTTABLE_VALUE) {
                fieldEditText.inputType = InputType.TYPE_CLASS_NUMBER
                fieldEditText.setBackgroundColor(resources.getColor(R.color.lightBlue))
                fieldInfoTextView.setBackgroundColor(resources.getColor(R.color.lightBlue))
                fieldTitleTextView.setBackgroundColor(resources.getColor(R.color.lightBlue))
            }

            linearLayout.addView(fieldTitleTextView)
            linearLayout.addView(fieldEditText)
            linearLayout.addView(fieldInfoTextView)
        }
    }


    private val addFieldButtonListener = View.OnClickListener {
        generateDialog()
    }

    private fun generateDialog() {
        val mDialogView =
            LayoutInflater.from(this).inflate(R.layout.plottable_dialog_window, null)
        mDialogView.dialogTextTextView.visibility = View.GONE
        mDialogView.dialogEditText.hint = "New field name"
        mDialogView.dialogEditText.inputType = InputType.TYPE_CLASS_TEXT
//        mDialogView.checkBox.text="Measurable parameter"
//        mDialogView.checkBox.isChecked=false
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("Add Field")
        val mAlertDialog = mBuilder.show()
        mDialogView.dialogEnterBtn.setOnClickListener {
            mAlertDialog.dismiss()
            addFieldLayout(
                mDialogView.dialogEditText.text.toString().trim(),
                mDialogView.checkBox.isChecked
            )
        }
        mDialogView.dialogCancelBtn.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

    private fun addFieldLayout(fieldName: String, isPlottable: Boolean) {
        //todo save what was written
//        exerciseFieldsMap[newField] = ""

//        updateBodyUI(OBJECT_EDIT)
        var newFieldValue = "String"
        if (isPlottable) {
            newFieldValue = ExerciseResults.PLOTTABLE_VALUE
        }
//        sResultFieldsMap[fieldName] = newFieldValue
        sResultFieldsMap[sResultFieldsMap.size] = Pair(fieldName, newFieldValue)
        updateBodyUI(OBJECT_EDIT)

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
//                Timber.d("visibility of child $index changed to $editTextVisibility")

                childView.visibility = editTextVisibility
                continue

            }
            if (childView is TextView) {
//                Timber.d("visibility of child $index changed to $textViewVisibility")

                childView.visibility = textViewVisibility
            }
        }
    }

    private fun isFieldTitle(index: Int): Boolean {
        //fieldsHashMap go in 3s, so childs 0,3,6,9... are alwasy titles
        return (index + 3) % 3 == 0
    }


    private val addButtonListener = View.OnClickListener {

        var newResultFields = getFieldContents()

        //todo when field added, modify directly the structure of sResultsFieldsMap
        activeExercise?.exerciseResults!!.setFieldsMap(sResultFieldsMap)
        //this updates the new field skeleton of the result (if new fields per e.g.)

        activeExercise?.exerciseResults!!.addResult(
            newResultFields
        )
        //this adds the result with a date to the list of results in form of hashmaps

        if (activeExercise != null) {
            dataViewModel.updateExerciseResult(activeExercise!!)
            DataHolder.activeExerciseHolder = activeExercise
        }
        finish() //?
    }

    private val updateButtonListener = View.OnClickListener {
        var newResultFields = getFieldContents()

        activeExercise?.exerciseResults!!.setFieldsMap(sResultFieldsMap)
        //this updates the new field skeleton of the result (if new fields per e.g.)

        activeExercise?.exerciseResults!!.updateResult(newResultFields, resultIndex)
        //this adds the result with a date to the list of results in form of hashmaps

        if (activeExercise != null) {
            dataViewModel.updateExerciseResult(activeExercise!!)
            DataHolder.activeExerciseHolder = activeExercise
        }

        backToViewer()
    }

    private val deleteButtonListener = View.OnClickListener {
//        activeExercise!!.exerciseResults.getArrayListOfResults().removeAt(resultIndex)
        activeExercise!!.exerciseResults.removeResult(resultIndex)
        dataViewModel.updateExerciseResult(activeExercise!!)
        DataHolder.activeExerciseHolder = activeExercise

        backToViewer()
    }

    private fun getFieldContents(): HashMap<Int, Pair<String, String>> {

        var fieldsMap = HashMap<Int, Pair<String, String>>()
        if (mAction== OBJECT_NEW) {
            fieldsMap[0] = Pair(ExerciseResults.DATE_KEY,Day.dayIDtoDashSeparator(resultDate))
        } else {
            fieldsMap[0] =Pair(ExerciseResults.DATE_KEY,activeExercise!!.exerciseResults.getResultDate(resultIndex))
        }
        for (i in 0 until linearLayout.childCount/3) {
//            var keyString = (linearLayout.getChildAt(i) as TextView).text.toString()

            var fieldName = (linearLayout.getChildAt(3*i) as TextView).text.toString()
            var fieldValue = (linearLayout.getChildAt(3*i + 1) as EditText).text.trim().toString()

            fieldsMap[i+1] =   Pair(fieldName,fieldValue)
        }
        return fieldsMap
    }

    private fun backToViewer() {
        val intent = Intent(this, ResultsViewer::class.java)
        intent.putExtra(RESULTS_EXE_ID, activeExercise!!.exerciseId)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.exercise_creator_toolbar, menu)
        menuItemEdit = menu?.findItem(R.id.action_edit)
        menuItemRead = menu?.findItem(R.id.action_read)
        when (mAction) {
            OBJECT_EDIT -> {
                updateToolbarItemVisibility(menuItemEdit, false)
                updateToolbarItemVisibility(menuItemRead, true)
            }
            OBJECT_VIEW -> {
                updateToolbarItemVisibility(menuItemEdit, true)
                updateToolbarItemVisibility(menuItemRead, false)
            }
            OBJECT_NEW -> {
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
            mAction= OBJECT_EDIT
            updateButtonUI(mAction)
            updateBodyUI(mAction)
            updateToolbarItemVisibility(menuItemEdit, false)
            updateToolbarItemVisibility(menuItemRead, true)
            true
        }
        R.id.action_read -> {
            mAction= OBJECT_VIEW
            updateButtonUI(mAction)
            updateBodyUI(mAction)
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