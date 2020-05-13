package com.tatoe.mydigicoach.ui.util

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.ExerciseResults
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.exercise.ExerciseCreator
import com.tatoe.mydigicoach.ui.results.ResultsCreator
import kotlinx.android.synthetic.main.inflate_results_collapsible_textview_layout.view.*

class ResultListAdapter(var context: Context) : RecyclerView.Adapter<DayExerciseViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var sResults = arrayListOf<HashMap<Int, HashMap<String, String>>>()
    private var sExercise: Exercise? = null
    private var listenerRecyclerView: ClickListenerRecyclerView? = null


    override fun getItemCount(): Int {
        return sResults.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayExerciseViewHolder {
        var itemView =
            inflater.inflate(R.layout.item_holder_day_exercise, parent, false)
        return DayExerciseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DayExerciseViewHolder, position: Int) {
        val bindingResult = sResults[position]
        updateExerciseLayout(holder, bindingResult)
    }

    private fun updateExerciseLayout(
        holder: DayExerciseViewHolder,
        bindingResult: java.util.HashMap<Int, java.util.HashMap<String, String>>
    ) {
        holder.mainLinearLayout!!.setBackgroundColor(context.resources.getColor(R.color.lightGreen))
        loadResultsLayout(holder.collapsibleLinearLayout, bindingResult)

        holder.exerciseTextView!!.text = bindingResult[0]!![ExerciseResults.DATE_KEY]
        holder.exerciseTextView.setOnClickListener {
            holder.toggleExpand(false)
        }

        holder.exerciseResultButton!!.setOnClickListener {
            //            Toast.makeText(context, "result pressed", Toast.LENGTH_SHORT).show()
            goToResult(sResults.indexOf(bindingResult))
        }

    }

    private fun goToResult(resultIndex: Int) {

        var intent = Intent(context, ResultsCreator::class.java)

        intent.putExtra(ExerciseCreator.OBJECT_ACTION, ExerciseCreator.OBJECT_VIEW)
        intent.putExtra(
            ResultsCreator.RESULT_INDEX, resultIndex
        )
        ContextCompat.startActivity(context, intent, null)
    }

    private fun loadResultsLayout(
        collapsibleLinearLayout: LinearLayout?,
        bindingResult: java.util.HashMap<Int, java.util.HashMap<String, String>>
    ) {

        for (i in 1 until bindingResult.size) {
            var fieldLayout =
                inflater.inflate(R.layout.inflate_results_collapsible_textview_layout, null)
            //the reason I use iterator().next() is because when I load resultsmap[i]
            //even though I have a hashmap of size 1, it is still a hashmap of undefined size (and order, thats why i use ints), so to
            //iterate through the entries and get the first one I use that, otherwise could get entries
            val resultKey= bindingResult[i]!!.iterator().next().key
            val resultValue = bindingResult[i]!!.iterator().next().value
            fieldLayout.fieldKey7.text = resultKey
            fieldLayout.fieldValueTextView8.text = ExerciseResults.toReadableFormat(resultValue, resultKey)
            collapsibleLinearLayout?.addView(fieldLayout)
        }
    }


    internal fun setContent(exercise: Exercise?) {
        sExercise = exercise
        this.sResults = exercise?.exerciseResults!!.getArrayListOfResults()
        notifyDataSetChanged()
    }

    fun setOnClickInterface(listener: ClickListenerRecyclerView) {
        this.listenerRecyclerView = listener
    }
}