package com.tatoe.mydigicoach.ui.exercise

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.tatoe.mydigicoach.DialogPositiveNegativeHandler
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.Utils
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.results.ResultsCreator
import com.tatoe.mydigicoach.ui.results.ResultsViewer
import com.tatoe.mydigicoach.ui.util.DataHolder
import com.tatoe.mydigicoach.viewmodels.ExerciseViewModel
import kotlinx.android.synthetic.main.activity_exercise_creator.*
import kotlinx.android.synthetic.main.inflate_description_edittext_layout.view.*
import kotlinx.android.synthetic.main.inflate_description_textview_layout.view.*
import kotlinx.android.synthetic.main.inflate_extrafield_edittext_layout.view.*
import kotlinx.android.synthetic.main.inflate_extrafield_textview_layout.view.*
import kotlinx.android.synthetic.main.inflate_title_edittext_layout.view.*
import kotlinx.android.synthetic.main.inflate_title_textview_layout.view.*
import timber.log.Timber
import java.util.HashMap

class ExerciseCreator : AppCompatActivity() {

    private lateinit var linearLayout: LinearLayout

    private lateinit var newField: String

    private lateinit var rightButton: TextView
    private lateinit var leftButton: TextView
    private lateinit var centreButton: TextView

    private lateinit var exerciseViewModel: ExerciseViewModel

    private var activeExercise: Exercise? = null
    private var exerciseFieldsMap = HashMap<Int, HashMap<String, String>>()


    var menuItemRead: MenuItem? = null
    var menuItemEdit: MenuItem? = null

    lateinit var mAction: String

    var NEW_FIELD_VALUE = ""

    private var LAYOUT_TYPE_TITLE_TV = 1
    private var LAYOUT_TYPE_TITLE_ET = 2
    private var LAYOUT_TYPE_DESCRIPTION_TV = 3
    private var LAYOUT_TYPE_DESCRIPTION_ET = 4
    private var LAYOUT_TYPE_EXTRAFIELD_TV = 5
    private var LAYOUT_TYPE_EXTRAFIELD_ET = 6

    companion object {
        var OBJECT_ACTION = "exercise_action"
        var OBJECT_NEW = "exercise_new"
        var OBJECT_EDIT = "exercise_edit"
        var OBJECT_VIEW = "exercise_view"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_creator)
        addFieldBtn.visibility=View.GONE
        title = "Exercise Creator"

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)
        backBtn.setOnClickListener {
            super.onBackPressed()
        }

        exerciseViewModel = ViewModelProviders.of(this).get(ExerciseViewModel::class.java)
        linearLayout = exercise_properties as LinearLayout

        rightButton = right_button
        leftButton = left_button
        centreButton = centre_button

        if (intent.hasExtra(OBJECT_ACTION)) { //can only reach this with an intent extra
            mAction = intent.getStringExtra(OBJECT_ACTION)
            if (mAction == OBJECT_EDIT || mAction == OBJECT_VIEW) {
//                if (DataHolder.activeExerciseHolder != null) {
                activeExercise = DataHolder.activeExerciseHolder
                Timber.d("about to open $activeExercise")

                if (activeExercise != null) {
                    exerciseFieldsMap = activeExercise!!.getFieldsMap()
                    Timber.d("field map $exerciseFieldsMap")

                }
//                }
            } else {
//                exerciseFieldsMap[0]!!["Name"] = ""

                exerciseFieldsMap[0] = hashMapOf("Name" to "")
//                exerciseFieldsMap[1]!!["Description"] = ""
                exerciseFieldsMap[1] = hashMapOf("Description" to "")
            }
            createExerciseFieldsLayout()
            updateButtonUI(mAction)

        }
    }

    private fun updateButtonUI(actionType: String) {
        if (actionType == OBJECT_NEW) {
            rightButton.visibility = View.VISIBLE
            rightButton.text = "Save"
            rightButton.setOnClickListener(addButtonListener)

            centreButton.visibility = View.VISIBLE
            leftButton.visibility = View.VISIBLE
            centreButton.setOnClickListener(addFieldButtonListener)

            return
        } else {

            if (actionType == OBJECT_EDIT) {
                rightButton.visibility = View.VISIBLE
                rightButton.text = "Save"
                rightButton.setOnClickListener(updateButtonListener)

                centreButton.visibility = View.VISIBLE
                centreButton.text = "Add Field"
                centreButton.setOnClickListener(addFieldButtonListener)

                leftButton.visibility = View.VISIBLE
                leftButton.text = "Delete"
                leftButton.setOnClickListener(deleteButtonListener)
            }
            if (actionType == OBJECT_VIEW) {
                rightButton.visibility = View.VISIBLE
                rightButton.text = "Results"
                rightButton.setOnClickListener(historyButtonListener)

                centreButton.visibility = View.INVISIBLE

                leftButton.visibility = View.VISIBLE
                leftButton.text = "Send"
                leftButton.setOnClickListener(sendToUserListener)
            }
        }
    }

    private fun updateBodyUI() {
        createExerciseFieldsLayout()

    }

    private fun createExerciseFieldsLayout() {
        linearLayout.removeAllViews()
        var hashMapHashMap = exerciseFieldsMap

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
        var layoutType: Int
        if (fieldPosition == 0) {
            layoutType = if (mAction == OBJECT_NEW || mAction == OBJECT_EDIT) {
                LAYOUT_TYPE_TITLE_ET
            } else {
                LAYOUT_TYPE_TITLE_TV
            }

        } else {
            layoutType = if (fieldPosition == 1) {
                if (mAction == OBJECT_NEW || mAction == OBJECT_EDIT) {
                    LAYOUT_TYPE_DESCRIPTION_ET
                } else {
                    LAYOUT_TYPE_DESCRIPTION_TV
                }
            } else {
                if (mAction == OBJECT_NEW || mAction == OBJECT_EDIT) {
                    LAYOUT_TYPE_EXTRAFIELD_ET
                } else {
                    LAYOUT_TYPE_EXTRAFIELD_TV
                }
            }
        }
        Timber.d("LAYOUT TYPE: $layoutType")
        return layoutType
    }

    private fun addLayout(fieldEntryKey: String, fieldEntryValue: String, layoutType: Int) {

        var fieldLayout = View(this)

        if (layoutType == LAYOUT_TYPE_TITLE_TV) {
            fieldLayout =
                layoutInflater.inflate(R.layout.inflate_title_textview_layout, null)
            fieldLayout.fieldKey1.text = fieldEntryKey
            fieldLayout.fieldValueTextView1.text = fieldEntryValue

        }
        if (layoutType == LAYOUT_TYPE_TITLE_ET) {
            fieldLayout =
                layoutInflater.inflate(R.layout.inflate_title_edittext_layout, null)
            fieldLayout.fieldKey2.text = fieldEntryKey

            var editText = fieldLayout.fieldValueEditText2
            if (mAction == OBJECT_NEW) {
                editText.hint = "e.g. Squats"
            } else {
                editText.text =
                    SpannableStringBuilder(fieldEntryValue)
            }
        }
        if (layoutType == LAYOUT_TYPE_DESCRIPTION_TV) {
            fieldLayout =
                layoutInflater.inflate(R.layout.inflate_description_textview_layout, null)
            fieldLayout.fieldKey3.text = fieldEntryKey

            fieldLayout.fieldValueTextView3.text = fieldEntryValue
        }
        if (layoutType == LAYOUT_TYPE_DESCRIPTION_ET) {
            fieldLayout =
                layoutInflater.inflate(R.layout.inflate_description_edittext_layout, null)
            fieldLayout.fieldKey4.text = fieldEntryKey

            val editText = fieldLayout.fieldValueEditText4
            if (mAction == OBJECT_NEW) {
                editText.hint = "Describe here your exercise"
            } else {
                editText.text =
                    SpannableStringBuilder(fieldEntryValue)
            }
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

        var fieldsMap = HashMap<Int, HashMap<String, String>>()
        for (i in 0 until linearLayout.childCount) {
//            Timber.d("child at $i is ${linearLayout.getChildAt(i)}")
            var layout = linearLayout.getChildAt(i) as LinearLayout

            //extra fields have a layout inside the layout - check the respective inflate files
            if (i > 1 && layout.getChildAt(0) is LinearLayout) {
                layout = layout.getChildAt(0) as LinearLayout
            }

            var fieldName = (layout.getChildAt(0) as TextView).text.toString()
            var fieldValue = (layout.getChildAt(1) as EditText).text.trim().toString()
            fieldsMap[i] = hashMapOf(fieldName to fieldValue)
        }
        return fieldsMap
    }

    private val addButtonListener = View.OnClickListener {

        var newExerciseFields = getFieldContents()
        var newExercise = Exercise(newExerciseFields)

        newExercise.setFieldsMap(newExerciseFields)

        exerciseViewModel.insertExercise(newExercise)
//        backToViewer()
        activeExercise=newExercise //new exercise are not fetched from SQLite from creator
        Toast.makeText(this,"${activeExercise?.name} has been added",Toast.LENGTH_SHORT).show()
        refreshCreator()
    }
    private val updateButtonListener = View.OnClickListener {

        var updatingExerciseFields = getFieldContents()

        activeExercise!!.name = updatingExerciseFields[0]!!["Name"]!!
        activeExercise!!.description = updatingExerciseFields[1]!!["Description"]!!
        activeExercise!!.setFieldsMap(updatingExerciseFields)

        exerciseViewModel.updateExercise(activeExercise!!)
        Toast.makeText(this,"${activeExercise?.name} has been updated",Toast.LENGTH_SHORT).show()
//        backToViewer()
        refreshCreator()
    }

    private val deleteButtonListener = View.OnClickListener {
        Utils.getInfoDialogView(
            this,
            title.toString(),
            "Are you sure you want to delete this exercise?",
            object :
                DialogPositiveNegativeHandler {

                override fun onPositiveButton(inputText: String) {
                    super.onPositiveButton(inputText)
                    exerciseViewModel.deleteExercise(activeExercise!!)
                    Toast.makeText(applicationContext,"${activeExercise?.name} has been deleted",Toast.LENGTH_SHORT).show()
                    backToViewer()
                }
            })

    }

    private val addFieldButtonListener = View.OnClickListener {
        generateDialog()
    }

    private fun generateDialog() {

        Utils.getDialogViewWithEditText(this, "Add Field", null, "Name of new field",
            object : DialogPositiveNegativeHandler {
                override fun onPositiveButton(inputText: String) {
                    addNewFieldLayout(inputText)
                }

            })
    }

    private fun addNewFieldLayout(newFieldKey: String) {
        exerciseFieldsMap[exerciseFieldsMap.size] = hashMapOf(newFieldKey to "")

        var fieldEntryKey = newFieldKey //first of pair - title of entry
        var fieldEntryValue = NEW_FIELD_VALUE //second of pair - value of entry

        addLayout(fieldEntryKey, fieldEntryValue, LAYOUT_TYPE_EXTRAFIELD_ET)

    }

    private val historyButtonListener = View.OnClickListener {
        val intent = Intent(this, ResultsViewer::class.java)

        DataHolder.activeExerciseHolder = activeExercise
        intent.putExtra(OBJECT_ACTION, OBJECT_VIEW)
        intent.putExtra(ResultsCreator.RESULTS_EXE_ID, activeExercise!!.exerciseId)

        startActivity(intent)
    }

    private val sendToUserListener = View.OnClickListener {
        Utils.getDialogViewWithEditText(this, "Send to User", null, "Username",
            object : DialogPositiveNegativeHandler {
                override fun onPositiveButton(username: String) {
                    exerciseViewModel.sendExerciseToUser(activeExercise, username)
                }

            })
    }

    private fun backToViewer() {
        val intent = Intent(this, ExerciseViewer::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    private fun refreshCreator() {
        val intent = Intent(this, ExerciseCreator::class.java)
        intent.putExtra(OBJECT_ACTION, OBJECT_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        DataHolder.activeExerciseHolder = activeExercise
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.exercise_creator_toolbar, menu)
        menuItemEdit = menu?.findItem(R.id.action_edit)
        menuItemRead = menu?.findItem(R.id.action_read)
        when (mAction) {
            OBJECT_EDIT -> {
                updateToolbarItemVisibility(menuItemEdit, false)
                updateToolbarItemVisibility(menuItemRead, false)
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
            updateBodyUI()
            updateToolbarItemVisibility(menuItemEdit, false)
            updateToolbarItemVisibility(menuItemRead, false)
            true
        }
        R.id.action_read -> {
            mAction = OBJECT_VIEW
            updateButtonUI(mAction)
            updateBodyUI()
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
