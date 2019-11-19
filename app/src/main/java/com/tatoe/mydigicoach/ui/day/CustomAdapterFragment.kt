package com.tatoe.mydigicoach.ui.day

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Block
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.ui.util.BlockListAdapter
import com.tatoe.mydigicoach.ui.util.ClickListenerRecyclerView
import com.tatoe.mydigicoach.ui.util.ExerciseListAdapter
import kotlinx.android.synthetic.main.fragment_adapter_container.view.*
import timber.log.Timber

class CustomAdapterFragment : Fragment() {

    private lateinit var fragmentView: View

    private lateinit var mRecyclerView: RecyclerView
    var adapterType = -1

    private var adapterBlocks: BlockListAdapter?=null
    private var adapterExercises: ExerciseListAdapter? = null

    var contentUpdated = false

    internal lateinit var callback: CustomAdapterEventsListener

    companion object {

        const val BUNDLE_ADAPTER_TYPE_KEY = "adapter_type"
        const val BLOCK_TYPE_ADAPTER = 0
        const val EXERCISE_TYPE_ADAPTER = 1
        const val BLOCK_DELETE_TYPE_ADAPTER = 2
        const val EXERCISE_DELETE_TYPE_ADAPTER = 3

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

    fun setCustomAdapterEventsListenerInterface(callback: CustomAdapterEventsListener){
        this.callback=callback
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        fragmentView = inflater.inflate(R.layout.fragment_adapter_container, container, false)
        mRecyclerView = fragmentView.RecyclerView

        Timber.d("whats my type $adapterType")

        when (adapterType) {
            BLOCK_TYPE_ADAPTER -> prepareBlockAdapter(blockSelectorListener,false)
            EXERCISE_TYPE_ADAPTER -> prepareExerciseAdapter(exerciseSelectorListener,false)
            BLOCK_DELETE_TYPE_ADAPTER -> prepareBlockAdapter(blockDeleterListener,true)
            EXERCISE_DELETE_TYPE_ADAPTER -> prepareExerciseAdapter(exerciseDeleterListener,true)
        }

        return fragmentView
    }

//    private fun prepareAdapter (adapter:RecyclerView.Adapter<EditableItemViewHolder>,listener:ClickListenerRecyclerView, hasDelete: Boolean) {
//        var mAdapter = adapter
//        mRecyclerView.adapter=mAdapter
//        mRecyclerView.layoutManager = LinearLayoutManager(activity!!)
//    }

    private fun prepareBlockAdapter(listener:ClickListenerRecyclerView,hasDelete:Boolean) {
        adapterBlocks = BlockListAdapter(activity!!,hasDelete)
        mRecyclerView.adapter = adapterBlocks
        mRecyclerView.layoutManager = LinearLayoutManager(activity!!)
        adapterBlocks?.setOnClickInterface(listener)
    }

    private fun prepareExerciseAdapter(listener:ClickListenerRecyclerView,hasDelete:Boolean) {
        adapterExercises = ExerciseListAdapter(activity!!,hasDelete)
        mRecyclerView.adapter = adapterExercises
        mRecyclerView.layoutManager=LinearLayoutManager(activity!!)
        adapterExercises?.setOnClickInterface(listener)
    }

    fun updateBlockAdapterContent(blocks:List<Block>) {
        Timber.d("update blocks from fragment $blocks")
        adapterBlocks?.setBlocks(blocks)
    }

    fun updateExerciseAdapterContent(exercises: List<Exercise>) {
        Timber.d("update exercises from fragment $exercises")
        adapterExercises?.setExercises(exercises)
    }

    interface CustomAdapterEventsListener {
        fun itemSelected(adapterType: Int, position:Int, deletingItem:Boolean = false)
    }

    private val blockSelectorListener = object : ClickListenerRecyclerView {
        override fun onClick(view: View, position: Int) {
            super.onClick(view, position)
            callback.itemSelected(BLOCK_TYPE_ADAPTER,position)
//            Timber.d("block creator exercise list after addition - $currentDayBlocks")
        }
    }

    private val exerciseSelectorListener = object : ClickListenerRecyclerView {
        override fun onClick(view: View, position: Int) {
            super.onClick(view, position)
            callback.itemSelected(EXERCISE_TYPE_ADAPTER,position)
//            Timber.d("block creator exercise list after addition - $currentDayBlcks")
        }
    }
    private val blockDeleterListener = object : ClickListenerRecyclerView {
        override fun onClick(view: View, position: Int) {
            super.onClick(view, position)
            callback.itemSelected(BLOCK_TYPE_ADAPTER,position,true)
//            Timber.d("block creator exercise list after addition - $currentDayBlocks")
        }
    }

    private val exerciseDeleterListener = object : ClickListenerRecyclerView {
        override fun onClick(view: View, position: Int) {
            super.onClick(view, position)
            callback.itemSelected(EXERCISE_TYPE_ADAPTER,position,true)
//            Timber.d("block creator exercise list after addition - $currentDayBlcks")
        }
    }

}
