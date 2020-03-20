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
import com.tatoe.mydigicoach.viewmodels.DataViewModel
import com.tatoe.mydigicoach.DialogPositiveNegativeHandler
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.Utils
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.results.ResultsCreator
import com.tatoe.mydigicoach.ui.results.ResultsViewer
import com.tatoe.mydigicoach.ui.util.DataHolder
import kotlinx.android.synthetic.main.activity_exercise_creator.*
import kotlinx.android.synthetic.main.custom_dialog_window.view.*
import timber.log.Timber
import java.util.HashMap

class ExerciseCreator : AppCompatActivity() {

    private lateinit var linearLayout: LinearLayout

    private lateinit var newField: String

    private lateinit var rightButton: Button
    private lateinit var leftButton: Button
    private lateinit var centreButton: Button

    private lateinit var dataViewModel: DataViewModel

    private var activeExercise: Exercise? = null
    private var exerciseFieldsMap = HashMap<Int, Pair<String,String>>()


    var menuItemRead: MenuItem? = null
    var menuItemEdit: MenuItem? = null

    lateinit var mAction: String

    var NEW_FIELD_VALUE = ""

    companion object {
        var OBJECT_ACTION = "exercise_action"
        var OBJECT_NEW = "exercise_new"
        var OBJECT_EDIT = "exercise_edit"
        var OBJECT_VIEW = "exercise_view"
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
                exerciseFieldsMap[0]=Pair("Name","")

//                exerciseFieldsMap[1]!!["Description"] = ""
                exerciseFieldsMap[1]=Pair("Description","")
            }
            updateBodyUI(mAction)
            updateButtonUI(mAction)

        }
    }

    private fun updateButtonUI(actionType: String) {
        if (actionType == OBJECT_NEW) {
            rightButton.visibility = View.VISIBLE
            rightButton.text = "ADD"
            rightButton.setOnClickListener(addButtonListener)

            centreButton.visibility = View.VISIBLE
            leftButton.visibility = View.VISIBLE
            centreButton.setOnClickListener(addFieldButtonListener)

            return
        } else {

            if (actionType == OBJECT_EDIT) {
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
            if (actionType == OBJECT_VIEW) {
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
        var linkedHashMap = Exercise.pairHashMapToLinked(exerciseFieldsMap)

        for (entry in linkedHashMap.entries) {
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

//        Timber.d("Child count: ${linearLayout.childCount}")
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

    private fun getFieldContents(): HashMap<Int,Pair<String,String>> {

        var fieldsMap = LinkedHashMap<String, String>()
        for (i in 0 until linearLayout.childCount step 3) {
            Timber.d("child at $i is ${linearLayout.getChildAt(i)}")
            var keyString = (linearLayout.getChildAt(i) as TextView).text.toString()
            fieldsMap[keyString] =
                (linearLayout.getChildAt(i + 1) as EditText).text.trim().toString()
        }
        return Exercise.linkedToPairHashMap(fieldsMap)
    }

    private val addButtonListener = View.OnClickListener {

        var newExerciseFields = getFieldContents()

//        var newExercise = Exercise(newExerciseFields["Name"]!!, newExerciseFields["Description"]!!)
        var newExercise = Exercise(newExerciseFields)

        newExercise.fieldsHashMap = newExerciseFields

        dataViewModel.insertExercise(newExercise)
        backToViewer()
    }
    private val updateButtonListener = View.OnClickListener {

        var updatingExerciseFields = getFieldContents()

        activeExercise!!.name = updatingExerciseFields[0]!!.second
//        activeExercise!!.name = updatingExerciseFields["Name"]!!
        activeExercise!!.description = updatingExerciseFields[1]!!.second
//        activeExercise!!.description = updatingExerciseFields["Description"]!!
        activeExercise!!.fieldsHashMap = updatingExerciseFields

        dataViewModel.updateExercise(activeExercise!!)

        backToViewer()
    }

    private val deleteButtonListener = View.OnClickListener {
        Utils.getInfoDialogView(this,title.toString(),"Are you sure you want to delete this exercise?",object:
            DialogPositiveNegativeHandler {

            override fun onPositiveButton(editTextText:String) {
                super.onPositiveButton(editTextText)
                dataViewModel.deleteExercise(activeExercise!!)
                backToViewer()
            }
        })

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
        exerciseFieldsMap[exerciseFieldsMap.size] = Pair("","")

//        updateBodyUI(OBJECT_EDIT)

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

        DataHolder.activeExerciseHolder=activeExercise
        intent.putExtra(OBJECT_ACTION, OBJECT_VIEW)
        intent.putExtra(ResultsCreator.RESULTS_EXE_ID, activeExercise!!.exerciseId)

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
