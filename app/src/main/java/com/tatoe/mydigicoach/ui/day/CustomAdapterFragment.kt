package com.tatoe.mydigicoach.ui.day

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.tatoe.mydigicoach.DataViewModel
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.util.BlockListAdapter
import com.tatoe.mydigicoach.ui.util.ClickListenerRecyclerView
import com.tatoe.mydigicoach.ui.util.DayContentAdapter
import com.tatoe.mydigicoach.ui.util.ExerciseListAdapter
import kotlinx.android.synthetic.main.fragment_adapter_container.*
import kotlinx.android.synthetic.main.fragment_adapter_container.view.*
import kotlinx.android.synthetic.main.fragment_day_view.view.*
import timber.log.Timber

class CustomAdapterFragment : Fragment() {

    private lateinit var fragmentView: View

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var exerciseRecyclerView: RecyclerView
    private lateinit var blockRecyclerView: RecyclerView
//    private lateinit var textView: TextView
    var adapterType = -1

    private var adapterBlocks: BlockListAdapter?=null
    private var adapterExercises: ExerciseListAdapter? = null

    private var allBlocks: ArrayList<Block> = arrayListOf()
    private var allExercises: ArrayList<Exercise> = arrayListOf()

    var contentUpdated = false

//    private lateinit var model: DataViewModel


    companion object {

        const val BUNDLE_ADAPTER_TYPE_KEY = "adapter_type"
        const val BLOCK_TYPE_ADAPTER = 0
        const val EXERCISE_TYPE_ADAPTER = 1

        fun newInstance(adapterType: Int): CustomAdapterFragment {
            var customAdapterFragment = CustomAdapterFragment()

            customAdapterFragment.arguments = Bundle().apply {
                putInt(BUNDLE_ADAPTER_TYPE_KEY, adapterType)
            }

            return customAdapterFragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        arguments?.getInt(BUNDLE_ADAPTER_TYPE_KEY)?.let {
            adapterType = it
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        model = activity?.run {
//            ViewModelProviders.of(this)[DataViewModel::class.java]
//        } ?: throw Exception("Invalid Activity")
//
//        model.allBlocks.observe(this, Observer { blocks ->
//            blocks?.let {
//                Timber.d("custom fragment observed all blocks: $blocks")
//            }
//        })
//
//        model.allExercises.observe(this, Observer { exes ->
//            exes?.let {
//                Timber.d("custom fragment observed all exercises: $exes")
//            }
//        })


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        fragmentView = inflater.inflate(R.layout.fragment_adapter_container, container, false)
//        textView=fragmentView.text_view
        mRecyclerView = fragmentView.RecyclerView

        when (adapterType) {
            BLOCK_TYPE_ADAPTER -> prepareBlockAdapter()
            EXERCISE_TYPE_ADAPTER -> prepareExerciseAdapter()
        }

//        prepareAdapter()

        return fragmentView
    }

    private fun prepareBlockAdapter() {
//        textView.text="IM BLOCK ADAPTER"
//
        adapterBlocks = BlockListAdapter(activity!!)
        mRecyclerView.adapter = adapterBlocks
        mRecyclerView.layoutManager = LinearLayoutManager(activity!!)

//        updateBlockAdapterContent()
//        Timber.d("prepare adapter called so adapterBlocks is null: ${adapterBlocks==null}")


    }

    private fun prepareExerciseAdapter() {
//        textView.text="IM EXERCISE ADAPTER"
//
        adapterExercises = ExerciseListAdapter(activity!!)
        mRecyclerView.adapter = adapterExercises
        mRecyclerView.layoutManager=LinearLayoutManager(activity!!)
    }

    fun addListenerToBlockAdapter(listener: ClickListenerRecyclerView) {
        adapterBlocks?.setListener(listener)
    }

    fun addListenerToExerciseAdapter(listener: ClickListenerRecyclerView) {
        adapterExercises?.setListener(listener)
    }

    fun updateBlockAdapterContent(blocks:List<Block>) {
//        Timber.d("blocks from fetchblocks: ${(activity as DayCreator).fetchBlocks()}")
//        Timber.d("is adapterBlocks null: ${adapterBlocks==null}")

        adapterBlocks?.setBlocks(blocks)
    }

    fun updateExerciseAdapterContent(exercises: List<Exercise>) {
        adapterExercises?.setExercises(exercises)

    }

//
//    interface ExerciseAdapterInterface {
//        fun itemSelected(position:Int)
//    }

}
