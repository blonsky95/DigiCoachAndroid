package com.tatoe.mydigicoach.ui.util

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.R

class DayExerciseViewHolder(v: View) :
    RecyclerView.ViewHolder(v) {

    var expanded: Boolean = false

    val mainLinearLayout: LinearLayout? = v.findViewById(R.id.mainLinearLayout)
    val exerciseTextView: TextView? = v.findViewById(R.id.exercise_name)
    val questionBtn: ImageView? = v.findViewById(R.id.questionBtn)
    val collapsibleLinearLayout: LinearLayout? = v.findViewById(R.id.collapsibleLinearLayout)
    val exerciseResultButton: TextView? = v.findViewById(R.id.resultsBtn)
    //    val bottomBtnsLayout:ConstraintLayout = v.findViewById(R.id.bottomBtnsLinearLayout)
    val bottomBtnsLayout: RelativeLayout = v.findViewById(R.id.bottomBtnsLinearLayout)

    val exerciseDoneButton: TextView? = v.findViewById(R.id.doneBtn)

    fun toggleExpand(
        questionBtnVisiblity: Boolean = true,
        exerciseDoneBtnVisibility: Boolean = true
    ) {
        if (!expanded) {
            collapsibleLinearLayout!!.visibility = View.VISIBLE
            bottomBtnsLayout.visibility = View.VISIBLE

            //if you are viewing all the results of an exercise no need to have a question btn to go to exercise
            if (questionBtnVisiblity) {
                questionBtn!!.visibility = View.VISIBLE
            }
            if (exerciseDoneBtnVisibility) {
                exerciseDoneButton!!.visibility = View.VISIBLE
            }
            expanded = true
        } else {
            collapsibleLinearLayout!!.visibility = View.GONE
            bottomBtnsLayout.visibility = View.GONE
            questionBtn!!.visibility = View.GONE

            expanded = false
        }
    }

}