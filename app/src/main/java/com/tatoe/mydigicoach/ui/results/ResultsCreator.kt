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
import com.tatoe.mydigicoach.DataViewModel
import com.tatoe.mydigicoach.ExerciseResults
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.ResultSet
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.exercise.ExerciseCreator.Companion.OBJECT_ACTION
import com.tatoe.mydigicoach.ui.exercise.ExerciseCreator.Companion.OBJECT_EDIT
import com.tatoe.mydigicoach.ui.exercise.ExerciseCreator.Companion.OBJECT_NEW
import com.tatoe.mydigicoach.ui.exercise.ExerciseCreator.Companion.OBJECT_VIEW
import com.tatoe.mydigicoach.ui.util.DataHolder
import kotlinx.android.synthetic.main.activity_exercise_creator.*
import kotlinx.android.synthetic.main.activity_results_creator.*
import kotlinx.android.synthetic.main.activity_results_creator.left_button
import kotlinx.android.synthetic.main.activity_results_creator.right_button
import kotlinx.android.synthetic.main.custom_dialog_window.view.*
import timber.log.Timber
import java.util.ArrayList

class ResultsCreator : AppCompatActivity() {

    private lateinit var rightButton: Button
    private lateinit var leftButton: Button
    private lateinit var centreButton: Button

    private lateinit var linearLayout: LinearLayout

    private lateinit var newField: String

    private var resultFieldsMap = LinkedHashMap<String, String>()
    private var resultsArrayList: ArrayList<LinkedHashMap<String, String>> = arrayListOf()


    private lateinit var dataViewModel: DataViewModel

    var menuItemRead: MenuItem? = null
    var menuItemEdit: MenuItem? = null

    lateinit var mAction: String

    var activeExercise: Exercise? = null
    private var resultDate = "unknown date"
    private var resultIndex = -1

    var NEW_FIELD_VALUE = ""

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

            resultFieldsMap = if (activeExercise?.exerciseResults!!.resultFieldsMap.isNotEmpty()) {
                activeExercise?.exerciseResults!!.resultFieldsMap
            } else {
                ExerciseResults.getGenericFields()
            }

            if (activeExercise?.exerciseResults!!.resultsArrayList.isNotEmpty()) {
                resultsArrayList = activeExercise?.exerciseResults!!.resultsArrayList
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

//            centreButton.visibility = View.VISIBLE
//            centreButton.text = "ADD FIELD"
//            centreButton.setOnClickListener(addFieldButtonListener)
            return
        } else {

            if (actionType == OBJECT_EDIT) {
                rightButton.visibility = View.VISIBLE
                rightButton.text = "UPDATE"
                rightButton.setOnClickListener(updateButtonListener)
//
//                centreButton.visibility = View.VISIBLE
//                centreButton.text = "ADD FIELD"
//                centreButton.setOnClickListener(addFieldButtonListener)

                leftButton.visibility = View.VISIBLE
                leftButton.text = "DELETE"
                leftButton.setOnClickListener(deleteButtonListener)
            }
            if (actionType == OBJECT_VIEW) {
                rightButton.visibility = View.INVISIBLE
                leftButton.visibility = View.INVISIBLE
//                centreButton.visibility = View.INVISIBLE
            }
        }
    }


//    private fun updateBodyUI(actionType: String) {
//
//        if (actionType == OBJECT_VIEW) {
//
////            Timber.d("active exercise results 3 ${activeExercise!!.results[0].sResult.toString()}")
////            Timber.d("active exercise results 4 ${activeExercise!!.results[1].sResult.toString()}")
//            result_edit_text.visibility = View.GONE
//            result_text_view.visibility = View.VISIBLE
//            result_text_view.text =
//                activeExercise!!.exerciseResults.resultsArrayList[resultIndex].sResult
//            return
//        }
//        //todo generate the layout like in ExerciseCreator
//        if (actionType == OBJECT_NEW) {
//            result_edit_text.visibility = View.VISIBLE
//            result_edit_text.hint = "How did it go?"
//            result_text_view.visibility = View.GONE
//        } else { //must be edit
//            result_edit_text.visibility = View.VISIBLE
//            result_edit_text.text =
//                SpannableStringBuilder(activeExercise!!.exerciseResults.resultsArrayList[resultIndex].sResult)
//            result_text_view.visibility = View.GONE
//        }
//    }

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

        linearLayout.removeAllViews()

//        var currentResultSet = activeExercise!!.exerciseResults.resultsArrayList[0]

        for (entry in resultFieldsMap.entries) {

            //temporary
            //****************************
//            var text = "empty"
//            text = if (resultIndex > 0) {
//                if (entry.key == ExerciseResults.NOTE_KEY) {
//                    resultsArrayList[resultIndex].sResult!!
//
//                } else {
//                    resultsArrayList[resultIndex].sPlottableResult.toString()
//                }
//            } else {
//                entry.value
//            }

            //*****************************
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
            fieldEditText.hint = resultsArrayList[resultIndex][entry.value]
            fieldEditText.text = SpannableStringBuilder(resultsArrayList[resultIndex][entry.key])

            linearLayout.addView(fieldEditText)

            var fieldInfoTextView = TextView(this)
            fieldInfoTextView.layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            fieldInfoTextView.text = resultsArrayList[resultIndex][entry.key]

            linearLayout.addView(fieldInfoTextView)
        }

//        Timber.d("Child count: ${linearLayout.childCount}")
    }

//    private val addFieldButtonListener = View.OnClickListener {
//        generateDialog()
//    }
//
//    private fun generateDialog() {
//        val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_window, null)
//        mDialogView.dialogTextTextView.visibility = View.INVISIBLE
//        mDialogView.dialogEditText.hint = "New field name"
//        mDialogView.dialogEditText.inputType = InputType.TYPE_CLASS_TEXT
//        val mBuilder = AlertDialog.Builder(this)
//            .setView(mDialogView)
//            .setTitle("Add Field")
//        val mAlertDialog = mBuilder.show()
//        mDialogView.dialogEnterBtn.setOnClickListener {
//            mAlertDialog.dismiss()
//            newField = mDialogView.dialogEditText.text.toString().trim()
//            addFieldLayout()
//        }
//        mDialogView.dialogCancelBtn.setOnClickListener {
//            mAlertDialog.dismiss()
//        }
//    }

//    private fun addFieldLayout() {
//        //todo save what was written
////        exerciseFieldsMap[newField] = ""
//
////        updateBodyUI(OBJECT_EDIT)
//
//        var fieldTitleTextView = TextView(this)
//        fieldTitleTextView.layoutParams =
//            ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//            )
//        fieldTitleTextView.text = newField
//        fieldTitleTextView.typeface = Typeface.DEFAULT_BOLD
//
//        linearLayout.addView(fieldTitleTextView)
//
//        var fieldEditText = EditText(this)
//        fieldEditText.layoutParams =
//            ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//            )
//        fieldEditText.hint = NEW_FIELD_VALUE
//        fieldEditText.text = SpannableStringBuilder(NEW_FIELD_VALUE)
//
//        linearLayout.addView(fieldEditText)
//
//        var fieldInfoTextView = TextView(this)
//        fieldInfoTextView.layoutParams =
//            ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//            )
//        fieldInfoTextView.text = NEW_FIELD_VALUE
//
//        linearLayout.addView(fieldInfoTextView)
//
//        changeVisibility(linearLayout, false)
//    }

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


        //todo essentially the results fields are being forced here but they should be user created
        //todo for now if default to basic naming then ok

//        dataViewModel.insertExercise(newExercise)

        //        var resultDate = TextView1.text.toString()
//        var resultString = SpannableStringBuilder(result_edit_text.text.trim()).toString()

//        activeExercise?.exerciseResults!!.addResult(
//            Day.dayIDtoDashSeparator(resultDate),
//            result = newResultFields[ExerciseResults.NOTE_KEY]!!,
//            plottableResult = newResultFields[ExerciseResults.PLOTTABLE_KEY]!!
//        )
        activeExercise?.exerciseResults!!.resultFieldsMap = newResultFields

        activeExercise?.exerciseResults!!.addResult(
            Day.dayIDtoDashSeparator(resultDate),
            newResultFields
        )

        if (activeExercise != null) {
            dataViewModel.updateExerciseResult(activeExercise!!)
            DataHolder.activeExerciseHolder = activeExercise
        }
//        Timber.d("after adding result exercise 3 :$activeExercise ${activeExercise?.exerciseResults!!.resultsArrayList}")
        finish() //?
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

    private val updateButtonListener = View.OnClickListener {
        var newResultFields = getFieldContents()

        activeExercise?.exerciseResults!!.resultFieldsMap = newResultFields

        activeExercise?.exerciseResults!!.resultsArrayList[resultIndex]=newResultFields

        if (activeExercise != null) {
            dataViewModel.updateExerciseResult(activeExercise!!)
            DataHolder.activeExerciseHolder = activeExercise
        }
        //todo add and update the same?
//        activeExercise!!.exerciseResults.resultsArrayList[resultIndex].sResult =
//            SpannableStringBuilder(result_edit_text.text.trim()).toString()
//        dataViewModel.updateExerciseResult(activeExercise!!)
//        DataHolder.activeExerciseHolder = activeExercise

        backToViewer()
    }

    private val deleteButtonListener = View.OnClickListener {
        activeExercise!!.exerciseResults.resultsArrayList.removeAt(resultIndex)
        dataViewModel.updateExerciseResult(activeExercise!!)
        DataHolder.activeExerciseHolder = activeExercise

        backToViewer()
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
            updateButtonUI(OBJECT_EDIT)
            updateBodyUI(OBJECT_EDIT)
            updateToolbarItemVisibility(menuItemEdit, false)
            updateToolbarItemVisibility(menuItemRead, true)
            true
        }
        R.id.action_read -> {
            updateButtonUI(OBJECT_VIEW)
            updateBodyUI(OBJECT_VIEW)
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