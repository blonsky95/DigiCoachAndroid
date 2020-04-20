package com.tatoe.mydigicoach.ui.results

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.SpannableStringBuilder
import android.view.*
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
import kotlinx.android.synthetic.main.inflate_extrafield_edittext_layout.view.*
import kotlinx.android.synthetic.main.inflate_extrafield_textview_layout.view.*
import kotlinx.android.synthetic.main.plottable_dialog_window.view.*
import timber.log.Timber
import java.util.ArrayList

class ResultsCreator : AppCompatActivity() {

    private lateinit var rightButton: TextView
    private lateinit var leftButton: TextView
    private lateinit var centreButton: TextView

    private lateinit var linearLayout: LinearLayout

    //contains the key and the type - so string or plottable
    private var sResultFieldsTypes = HashMap<Int, HashMap<String, String>>()
    private var sResultsArrayList: ArrayList<HashMap<Int, HashMap<String, String>>> = arrayListOf()


    private lateinit var dataViewModel: DataViewModel

    var menuItemRead: MenuItem? = null
    var menuItemEdit: MenuItem? = null

    lateinit var mAction: String

    var activeExercise: Exercise? = null
    private var resultDate = "unknown date"
    private var resultIndex = -1

    var NEW_FIELD_VALUE = ""

    private var LAYOUT_TYPE_TITLE_TV = 1
    private var LAYOUT_TYPE_TITLE_ET = 2
    private var LAYOUT_TYPE_DESCRIPTION_TV = 3
    private var LAYOUT_TYPE_DESCRIPTION_ET = 4
    private var LAYOUT_TYPE_EXTRAFIELD_TV = 5
    private var LAYOUT_TYPE_EXTRAFIELD_ET = 6
    private var LAYOUT_TYPE_DATE_TV = 7

    companion object {
        var RESULTS_DATE = "results_date"
        var RESULTS_EXE_ID = "results_exe_id"
        var RESULT_INDEX = "result_index"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_creator)
        toolbar_title.text = "Result"
        backBtn.setOnClickListener {
            super.onBackPressed()
        }

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar!!.setDisplayShowTitleEnabled(false)

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

            sResultFieldsTypes =
                if (activeExercise?.exerciseResults!!.getFieldsMap().isNotEmpty()) {
                    activeExercise?.exerciseResults!!.getFieldsMap()
                } else {
                    ExerciseResults.getGenericFields()
                }

            if (activeExercise?.exerciseResults!!.getArrayListOfResults().isNotEmpty()) {
                sResultsArrayList = activeExercise?.exerciseResults!!.getArrayListOfResults()
            }

            Timber.d("ACTIVE EXERCISE IN CREATOR ${activeExercise!!}")

            createExerciseFieldsLayout()
            updateButtonUI(mAction)

        }
    }

    private fun updateButtonUI(actionType: String) {
        if (actionType == OBJECT_NEW) {
            rightButton.visibility = View.VISIBLE
            rightButton.text = "Add"
            rightButton.setOnClickListener(addButtonListener)

            leftButton.visibility = View.INVISIBLE

            centreButton.visibility = View.VISIBLE
            centreButton.text = "Add Field"
            centreButton.setOnClickListener(addFieldButtonListener)
            return
        } else {

            if (actionType == OBJECT_EDIT) {
                rightButton.visibility = View.VISIBLE
                rightButton.text = "Update"
                rightButton.setOnClickListener(updateButtonListener)
//
                centreButton.visibility = View.VISIBLE
                centreButton.text = "Add Field"
                centreButton.setOnClickListener(addFieldButtonListener)

                leftButton.visibility = View.VISIBLE
                leftButton.text = "Delete"
                leftButton.setOnClickListener(deleteButtonListener)
            }
            if (actionType == OBJECT_VIEW) {
                rightButton.visibility = View.INVISIBLE
                leftButton.visibility = View.INVISIBLE
                centreButton.visibility = View.INVISIBLE
            }
        }
    }

    private fun createExerciseFieldsLayout() {

        linearLayout.removeAllViews()

        val hashMapHashMap =
            if (mAction == OBJECT_NEW) {
                sResultFieldsTypes
            } else {
                sResultsArrayList[resultIndex]
            }
        for (fieldPosition in 0 until hashMapHashMap.size) {

            var currentField = hashMapHashMap[fieldPosition]

            var firstEntry = currentField!!.entries.iterator().next()

            var fieldEntryKey = firstEntry.key //first of pair - title of entry
            var fieldEntryValue = firstEntry.value //second of pair - value of entry
//            var entryHintString = "Type here"

            addLayout(fieldEntryKey, fieldEntryValue, getLayoutType(fieldPosition))

        }
    }

    private fun getLayoutType(fieldPosition: Int): Int {
        if (fieldPosition == 0 && mAction== OBJECT_NEW) {
            return LAYOUT_TYPE_DATE_TV
        }

        var layoutType: Int = if ((mAction == OBJECT_NEW || mAction == OBJECT_EDIT)&&fieldPosition!=0) {
            LAYOUT_TYPE_EXTRAFIELD_ET
        } else {
            LAYOUT_TYPE_EXTRAFIELD_TV
        }



        Timber.d("LAYOUT TYPE: $layoutType")
        return layoutType
    }

    private fun addLayout(fieldEntryKey: String, fieldEntryValue: String, layoutType: Int) {

        var fieldLayout = View(this)

        if (layoutType == LAYOUT_TYPE_DATE_TV) {
            fieldLayout =
                layoutInflater.inflate(R.layout.inflate_extrafield_textview_layout, null)
            fieldLayout.fieldKey5.text = fieldEntryKey

            fieldLayout.fieldValueTextView5.text = Day.dayIDtoDashSeparator(resultDate)
        }

        if (layoutType == LAYOUT_TYPE_EXTRAFIELD_TV) {
            fieldLayout =
                layoutInflater.inflate(R.layout.inflate_extrafield_textview_layout, null)
            fieldLayout.fieldKey5.text = fieldEntryKey

            fieldLayout.fieldValueTextView5.text = fieldEntryValue
        }
        if (layoutType == LAYOUT_TYPE_EXTRAFIELD_ET) {
            fieldLayout =
                layoutInflater.inflate(R.layout.inflate_extrafield_edittext_layout, null)
            fieldLayout.fieldKey6.text = fieldEntryKey

            val editText = fieldLayout.fieldValueEditText6
            if (mAction == OBJECT_NEW) {
                editText.hint = "New field"
            } else {
                editText.text =
                    SpannableStringBuilder(fieldEntryValue)
            }
        }

        linearLayout.addView(fieldLayout)

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
        var newFieldValue = "String"
        if (isPlottable) {
            newFieldValue = ExerciseResults.PLOTTABLE_VALUE
        }
        addLayout(fieldName, newFieldValue, LAYOUT_TYPE_EXTRAFIELD_ET)

    }

    private val addButtonListener = View.OnClickListener {

        var newResultFields = getFieldContents()

        activeExercise?.exerciseResults!!.setFieldsMap(sResultFieldsTypes)
        //this updates the new field skeleton of the result (if new fields per e.g.)

        activeExercise?.exerciseResults!!.addResult(
            newResultFields
        )
        //this adds the result with a date to the list of results in form of hashmaps

        if (activeExercise != null) {
            dataViewModel.updateExerciseResult(activeExercise!!)
            DataHolder.activeExerciseHolder = activeExercise
        }
        refreshCreator()
    }

    private val updateButtonListener = View.OnClickListener {
        var newResultFields = getFieldContents()

        activeExercise?.exerciseResults!!.setFieldsMap(sResultFieldsTypes)
        //this updates the new field skeleton of the result (if new fields per e.g.)

        activeExercise?.exerciseResults!!.updateResult(newResultFields, resultIndex)
        //this adds the result with a date to the list of results in form of hashmaps

        if (activeExercise != null) {
            dataViewModel.updateExerciseResult(activeExercise!!)
            DataHolder.activeExerciseHolder = activeExercise
        }

        refreshCreator()
    }

    private val deleteButtonListener = View.OnClickListener {
        //        activeExercise!!.exerciseResults.getArrayListOfResults().removeAt(resultIndex)
        activeExercise!!.exerciseResults.removeResult(resultIndex)
        dataViewModel.updateExerciseResult(activeExercise!!)
        DataHolder.activeExerciseHolder = activeExercise

        backToViewer()
    }

    private fun getFieldContents(): HashMap<Int, HashMap<String, String>> {

        var fieldsMap = java.util.HashMap<Int, java.util.HashMap<String, String>>()
        for (i in 0 until linearLayout.childCount) {
//            Timber.d("child at $i is ${linearLayout.getChildAt(i)}")
            var layout = linearLayout.getChildAt(i) as LinearLayout

            //extra fields have a layout inside the layout - check the respective inflate files
            if (layout.getChildAt(0) is LinearLayout) {
                layout = layout.getChildAt(0) as LinearLayout
            }

            var fieldName = (layout.getChildAt(0) as TextView).text.toString()
            var fieldValue = ""
            fieldValue = if (i==0){
                (layout.getChildAt(1) as TextView).text.trim().toString()
            } else {
                (layout.getChildAt(1) as EditText).text.trim().toString()
            }
            fieldsMap[i] = hashMapOf(fieldName to fieldValue)
        }
        return fieldsMap

    }

    private fun refreshCreator() {
        val intent = Intent(this, ResultsCreator::class.java)
        intent.putExtra(OBJECT_ACTION, OBJECT_VIEW)
        if (resultIndex == -1) {
            resultIndex = activeExercise!!.exerciseResults.getResultPosition(resultDate)
        }
        intent.putExtra(RESULT_INDEX, resultIndex)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        DataHolder.activeExerciseHolder = activeExercise
        startActivity(intent)
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

        R.id.action_edit -> {
            mAction = OBJECT_EDIT
            updateButtonUI(mAction)
            createExerciseFieldsLayout()
            updateToolbarItemVisibility(menuItemEdit, false)
            updateToolbarItemVisibility(menuItemRead, false)
            true
        }
        R.id.action_read -> {
            mAction = OBJECT_VIEW
            updateButtonUI(mAction)
            createExerciseFieldsLayout()
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