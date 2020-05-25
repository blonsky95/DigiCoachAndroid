package com.tatoe.mydigicoach.ui.results

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProviders
import com.tatoe.mydigicoach.ExerciseResults
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.exercise.ExerciseCreator.Companion.OBJECT_ACTION
import com.tatoe.mydigicoach.ui.exercise.ExerciseCreator.Companion.OBJECT_EDIT
import com.tatoe.mydigicoach.ui.exercise.ExerciseCreator.Companion.OBJECT_NEW
import com.tatoe.mydigicoach.ui.exercise.ExerciseCreator.Companion.OBJECT_VIEW
import com.tatoe.mydigicoach.ui.util.DataHolder
import com.tatoe.mydigicoach.viewmodels.MyResultsViewModelFactory
import com.tatoe.mydigicoach.viewmodels.ResultsViewModel
import kotlinx.android.synthetic.main.activity_exercise_creator.*
import kotlinx.android.synthetic.main.activity_exercise_creator.backBtn
import kotlinx.android.synthetic.main.activity_exercise_creator.toolbar_title
import kotlinx.android.synthetic.main.activity_results_creator.left_button
import kotlinx.android.synthetic.main.activity_results_creator.right_button
import kotlinx.android.synthetic.main.inflate_extrafield_edittext_layout.view.*
import kotlinx.android.synthetic.main.inflate_extrafield_textview_layout.view.*
import kotlinx.android.synthetic.main.inflate_spinner_units_selector.view.*
import kotlinx.android.synthetic.main.inflate_units_field_mins_secs.view.*
import kotlinx.android.synthetic.main.inflate_units_field_one_rm.view.*
import kotlinx.android.synthetic.main.inflate_units_field_secs.view.*
import timber.log.Timber
import java.util.ArrayList

class ResultsCreator : AppCompatActivity() {

    private val MAX_NUMBER_ENTRIES = 5
    private lateinit var rightButton: TextView
    private lateinit var leftButton: TextView

    private lateinit var linearLayout: LinearLayout

    //contains the key and the type - so string or plottable
    private var sResultFieldsTypes = HashMap<Int, HashMap<String, String>>()
    private var sResultsArrayList: ArrayList<HashMap<Int, HashMap<String, String>>> = arrayListOf()

    private lateinit var resultsViewModel: ResultsViewModel

    var menuItemRead: MenuItem? = null
    var menuItemEdit: MenuItem? = null

    lateinit var mAction: String

    var activeExercise: Exercise? = null
    private var resultDate = "unknown date"
    private var resultIndex = -1

    private var LAYOUT_TYPE_EXTRAFIELD_TV = 5
    private var LAYOUT_TYPE_EXTRAFIELD_ET = 6
    private var LAYOUT_TYPE_DATE_TV = 7
    private var LAYOUT_TYPE_NEW_FIELD_EDIT = 8
    private var LAYOUT_TYPE_NEW_FIELD_READ = 9

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

        resultsViewModel = ViewModelProviders.of(this, MyResultsViewModelFactory(application))
            .get(ResultsViewModel::class.java)

        linearLayout = exercise_properties as LinearLayout

        rightButton = right_button
        leftButton = left_button

        centre_button.visibility = View.GONE
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
            createExerciseFieldsLayout()
            updateButtonUI(mAction)
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

            val currentField = hashMapHashMap[fieldPosition]
            val firstEntry = currentField!!.entries.iterator().next()
            val fieldEntryKey = firstEntry.key //first of pair - title of entry
            val fieldEntryValue = firstEntry.value //second of pair - value of entry

            addLayout(fieldEntryKey, fieldEntryValue, getLayoutType(fieldPosition))

        }
        Timber.d("LINEAR LAYOUT CHILD COUNT: ${linearLayout.childCount}")
        addFieldBtn.setOnClickListener {
            addLayout(null, null, LAYOUT_TYPE_NEW_FIELD_EDIT)
            updateAddFieldBtnVisibility()
        }
    }

    private fun updateAddFieldBtnVisibility() {
        if (linearLayout.childCount >= MAX_NUMBER_ENTRIES) {
            addFieldBtn.visibility = View.GONE
        } else {
            addFieldBtn.visibility = View.VISIBLE
        }
    }

    private fun getLayoutType(fieldPosition: Int): Int {
        if (fieldPosition == 0 && mAction == OBJECT_NEW) {
            return LAYOUT_TYPE_DATE_TV
        }

        if (fieldPosition > 1) {
            if (mAction == OBJECT_EDIT) {
                return LAYOUT_TYPE_NEW_FIELD_EDIT
            }
            if (mAction == OBJECT_VIEW) {
                return LAYOUT_TYPE_NEW_FIELD_READ
            }

        }

        var layoutType: Int =
            if ((mAction == OBJECT_NEW || mAction == OBJECT_EDIT) && fieldPosition != 0) {
                LAYOUT_TYPE_EXTRAFIELD_ET
            } else {
                LAYOUT_TYPE_EXTRAFIELD_TV
            }

        return layoutType
    }

    private fun addLayout(fieldEntryKey: String?, fieldEntryValue: String?, layoutType: Int) {

        var fieldLayout = View(this)

        if (layoutType == LAYOUT_TYPE_NEW_FIELD_READ) {
            fieldLayout =
                layoutInflater.inflate(R.layout.inflate_extrafield_textview_layout, null)
            fieldLayout.fieldKey5.text = fieldEntryKey

            fieldLayout.fieldValueTextView5.text =
                ExerciseResults.toReadableFormat(fieldEntryValue!!, fieldEntryKey!!)
        }

        if (layoutType == LAYOUT_TYPE_NEW_FIELD_EDIT) {
            fieldLayout = layoutInflater.inflate(R.layout.inflate_spinner_units_selector, null)
            ArrayAdapter.createFromResource(
                this,
                R.array.units_array,
                android.R.layout.simple_spinner_item
            ).also { vAdapter ->
                vAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                fieldLayout.units_spinner.adapter = vAdapter
            }

            var maSpinner = fieldLayout.units_spinner
            var myWeirdSpinner = MySpinnerConfigurator(maSpinner, fieldLayout)

            //if its editing an existing, the key and value wont be null
            if (fieldEntryKey != null) {
                myWeirdSpinner.loadValues(
                    fieldEntryKey,
                    fieldEntryValue!!
                )
            }
        }

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

    private fun getFieldContents(): HashMap<Int, HashMap<String, String>> {

        var fieldsMap = java.util.HashMap<Int, java.util.HashMap<String, String>>()
        for (i in 0 until linearLayout.childCount) {

            var layout = linearLayout.getChildAt(i) as LinearLayout

            var fieldName = ""
            var fieldValue = ""

            if (i > 1) {
                if (layout.getChildAt(0) is Spinner) {
                    fieldName = (layout.getChildAt(0) as Spinner).selectedItem.toString()

                    var nextLayout =
                        (layout.getChildAt(1) as LinearLayout).getChildAt(0) as LinearLayout
                    var dash = ""
                    for (ite in 0 until (nextLayout.childCount - 1) step 2) {
                        fieldValue += "$dash${(nextLayout.getChildAt(ite) as EditText).text.trim()}"
                        dash = "-"
                    }
                    if (nextLayout.childCount == 5) {
                        fieldValue += "$dash${(nextLayout.getChildAt(4) as TextView).text.trim()}"
                    }
                }
            } else {
                if (layout.getChildAt(0) is LinearLayout) {
                    layout = layout.getChildAt(0) as LinearLayout
                }

                fieldName = (layout.getChildAt(0) as TextView).text.toString()
                fieldValue = if (i == 0) {
                    (layout.getChildAt(1) as TextView).text.trim().toString()
                } else {
                    (layout.getChildAt(1) as EditText).text.trim().toString()
                }
            }

            fieldsMap[i] = hashMapOf(fieldName to fieldValue)
        }
        return fieldsMap

    }

    inner class MySpinnerConfigurator(
        var spinner: Spinner,
        var fieldLayout: View
    ) :
        AdapterView.OnItemSelectedListener {

        lateinit var oneRmTextView: TextView
        var fieldEntryValue = ""
        var fieldEntryKey = ""
        private var oneRmReps = 0
        private var oneRmWeight = 0.toFloat()

        init {
            spinner.onItemSelectedListener = this
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        fun loadValues(sFieldEntryKey: String, sFieldEntryValue: String) {
            spinner.setSelection(
                ExerciseResults.getFieldTypePosition(
                    sFieldEntryKey,
                    this@ResultsCreator
                )
            )
            fieldEntryValue = sFieldEntryValue
            fieldEntryKey = sFieldEntryKey
        }

        private fun resetFieldValues() {
            fieldEntryValue = ""
            fieldEntryKey = ""
        }

        private fun updateOneRmValue() {
            oneRmTextView.text =
                String.format("%.1f", (100 * oneRmWeight) / (102.78 - 2.78 * oneRmReps))
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            var linearLayout = fieldLayout.units_container
            linearLayout.removeAllViews()

            //if editing spinner key, check if the spinner value is the same before resetting the values
            //basically, if changing to new field from an edit, reset the values inside this class
            if (ExerciseResults.getPositionFromKey(position, applicationContext) != fieldEntryKey) {
                resetFieldValues()
            }

            when (position) {
                0 -> {
                    var unitsView = layoutInflater.inflate(
                        R.layout.inflate_units_field_secs,
                        null
                    )
                    if (mAction == OBJECT_EDIT) {
                        unitsView.unit_value_et.text = SpannableStringBuilder(
                            ExerciseResults.toNumericFormat(
                                fieldEntryValue,
                                fieldEntryKey
                            )[0].toString()
                        )
                    }
                    linearLayout.addView(
                        unitsView
                    )
                    return
                }
                1 -> {
                    var unitsView = layoutInflater.inflate(
                        R.layout.inflate_units_field_mins_secs,
                        null
                    )
                    if (mAction == OBJECT_EDIT) {
                        var valuesArray = ExerciseResults.toNumericFormat(
                            fieldEntryValue,
                            fieldEntryKey
                        )

                        unitsView.unit_value_et_mins.text = SpannableStringBuilder(
                            valuesArray[0].toString()
                        )
                        unitsView.unit_value_et_secs.text = SpannableStringBuilder(
                            valuesArray[1].toString()
                        )
                    }
                    linearLayout.addView(
                        unitsView
                    )
                    return
                }

                2 -> {
                    var unitsView = layoutInflater.inflate(
                        R.layout.inflate_units_field_km,
                        null
                    )
                    if (mAction == OBJECT_EDIT) {
                        unitsView.unit_value_et.text = SpannableStringBuilder(
                            ExerciseResults.toNumericFormat(
                                fieldEntryValue,
                                fieldEntryKey
                            )[0].toString()
                        )
                    }
                    linearLayout.addView(
                        unitsView
                    )
                    return
                }
                3 -> {
                    var unitsView = layoutInflater.inflate(
                        R.layout.inflate_units_field_m,
                        null
                    )
                    if (mAction == OBJECT_EDIT) {
                        unitsView.unit_value_et.text = SpannableStringBuilder(
                            ExerciseResults.toNumericFormat(
                                fieldEntryValue,
                                fieldEntryKey
                            )[0].toString()
                        )
                    }
                    linearLayout.addView(
                        unitsView
                    )
                    return
                }
                4 -> {
                    var unitsView = layoutInflater.inflate(
                        R.layout.inflate_units_field_kg,
                        null
                    )
                    if (mAction == OBJECT_EDIT) {
                        unitsView.unit_value_et.text = SpannableStringBuilder(
                            ExerciseResults.toNumericFormat(
                                fieldEntryValue,
                                fieldEntryKey
                            )[0].toString()
                        )
                    }
                    linearLayout.addView(
                        unitsView
                    )
                    return
                }
                5 -> {
                    var oneRmLayout =
                        layoutInflater.inflate(R.layout.inflate_units_field_one_rm, null)

                    if (mAction == OBJECT_EDIT) {
                        oneRmLayout.unit_value_et_reps.text = SpannableStringBuilder(
                            ExerciseResults.toNumericFormat(
                                fieldEntryValue,
                                fieldEntryKey
                            )[0].toInt().toString()
                        )
                        oneRmLayout.unit_value_et_kg.text = SpannableStringBuilder(
                            ExerciseResults.toNumericFormat(
                                fieldEntryValue,
                                fieldEntryKey
                            )[1].toString()
                        )
                        oneRmLayout.one_rm_kg.text = SpannableStringBuilder(
                            ExerciseResults.toNumericFormat(
                                fieldEntryValue,
                                fieldEntryKey
                            )[2].toString()
                        )
                        oneRmReps = ExerciseResults.toNumericFormat(
                            fieldEntryValue,
                            fieldEntryKey
                        )[0].toInt()
                        oneRmWeight =
                            ExerciseResults.toNumericFormat(fieldEntryValue, fieldEntryKey)[1]
                        oneRmTextView = oneRmLayout.one_rm_kg

                        updateOneRmValue()
                    }

                    oneRmTextView = oneRmLayout.one_rm_kg


                    oneRmLayout.unit_value_et_reps.doAfterTextChanged { text ->
                        if (!text.isNullOrEmpty()) {
                            oneRmReps = text.toString().toInt()
                            updateOneRmValue()
                        }
                    }
                    oneRmLayout.unit_value_et_kg.doAfterTextChanged { text ->
                        if (!text.isNullOrEmpty()) {
                            oneRmWeight = text.toString().toFloat()
                            updateOneRmValue()
                        }
                    }
                    linearLayout.addView(oneRmLayout)
                    return
                }

            }
        }

    }

    private val addButtonListener = View.OnClickListener {

        var newResultFields = getFieldContents()

        //check if a new result field has been added
//        var resultTypes = activeExercise!!.exerciseResults
        var defaultExerciseFieldsSize = 2
        if (newResultFields.size > defaultExerciseFieldsSize) {
            for (i in defaultExerciseFieldsSize until newResultFields.size) {
                var newResultKey = newResultFields[i]!!.entries.iterator().next().key
                if (!activeExercise!!.exerciseResults.resultsTypes.contains(newResultKey)) {
                    activeExercise!!.exerciseResults.resultsTypes.add(newResultKey)
                }
            }
        }
//        activeExercise?.exerciseResults!!.setFieldsMap(sResultFieldsTypes)
        //this updates the new field skeleton of the result (if new fields per e.g.)

        activeExercise?.exerciseResults!!.addResult(
            newResultFields
        )
        //this adds the result with a date to the list of results in form of hashmaps

        if (activeExercise != null) {
            resultsViewModel.updateExerciseResult(activeExercise!!)
            DataHolder.activeExerciseHolder = activeExercise
        }
        refreshCreator()
    }

    private val updateButtonListener = View.OnClickListener {
        var newResultFields = getFieldContents()

//        activeExercise?.exerciseResults!!.setFieldsMap(sResultFieldsTypes)
        //this updates the new field skeleton of the result (if new fields per e.g.)

        var defaultExerciseFieldsSize = 2
        if (newResultFields.size - defaultExerciseFieldsSize > activeExercise!!.exerciseResults.resultsTypes.size) {
            for (i in defaultExerciseFieldsSize..newResultFields.size) {
                var newResultKey = newResultFields[i]!!.entries.iterator().next().key
                if (!activeExercise!!.exerciseResults.resultsTypes.contains(newResultKey)) {
                    activeExercise!!.exerciseResults.resultsTypes.add(newResultKey)
                }
            }
        }

        activeExercise?.exerciseResults!!.updateResult(newResultFields, resultIndex)
        //this adds the result with a date to the list of results in form of hashmaps

        if (activeExercise != null) {
            resultsViewModel.updateExerciseResult(activeExercise!!)
            DataHolder.activeExerciseHolder = activeExercise
        }

        refreshCreator()
    }

    private val deleteButtonListener = View.OnClickListener {

        activeExercise!!.exerciseResults.removeResult(resultIndex)
        resultsViewModel.updateExerciseResult(activeExercise!!)
        DataHolder.activeExerciseHolder = activeExercise
        super.onBackPressed()
//        backToViewer()
    }


    private fun updateButtonUI(actionType: String) {
        if (actionType == OBJECT_NEW) {
            addFieldBtn.visibility = View.VISIBLE
            rightButton.visibility = View.VISIBLE
            rightButton.text = "Add"
            rightButton.setOnClickListener(addButtonListener)

            leftButton.visibility = View.INVISIBLE

            return
        } else {

            if (actionType == OBJECT_EDIT) {
                updateAddFieldBtnVisibility()

                rightButton.visibility = View.VISIBLE
                rightButton.text = "Update"
                rightButton.setOnClickListener(updateButtonListener)

                leftButton.visibility = View.VISIBLE
                leftButton.text = "Delete"
                leftButton.setOnClickListener(deleteButtonListener)
            }
            if (actionType == OBJECT_VIEW) {
                addFieldBtn.visibility = View.GONE
                rightButton.visibility = View.INVISIBLE
                leftButton.visibility = View.INVISIBLE
            }
        }
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