package com.tatoe.mydigicoach.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.DialogPositiveNegativeHandler
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.Utils
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.network.ExercisePackage
import com.tatoe.mydigicoach.network.MyCustomStoreExercise
import com.tatoe.mydigicoach.viewmodels.LibraryViewModel
import com.tatoe.mydigicoach.viewmodels.MyLibraryViewModelFactory
import kotlinx.android.synthetic.main.activity_library.*
import kotlinx.android.synthetic.main.dialog_window_library_filter.view.*
import kotlinx.android.synthetic.main.item_holder_exercise_library.view.*
import kotlinx.android.synthetic.main.item_holder_filter_library.view.*
import timber.log.Timber

class Library : AppCompatActivity(), SearchView.OnQueryTextListener {
    private lateinit var myExercisesAdapter: MyCustomExercisesAdapter
    private lateinit var libraryViewModel: LibraryViewModel

    private var allMyCustomStoreExercises = arrayListOf<MyCustomStoreExercise>()
    private var allMyExercises = listOf<Exercise>()
    var filterCategories = arrayListOf<Pair<String, Boolean>>()
    var toImportExercises = arrayListOf<MyCustomStoreExercise>()

    private var db = FirebaseFirestore.getInstance()

    private var allExesLoaded = false
    private var storeExercisesLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)

        libraryViewModel = ViewModelProviders.of(
            this,
            MyLibraryViewModelFactory(application, db)
        ).get(
            LibraryViewModel::class.java
        )

        myExercisesAdapter = MyCustomExercisesAdapter(this)
        search_view.isIconified = true

        libraryExercisesList.layoutManager = LinearLayoutManager(this)
        libraryExercisesList.adapter = myExercisesAdapter

        addToTrainingBtn.setOnClickListener {
            toImportExercises = myExercisesAdapter.checkedExercises
            Timber.d("exercises to import ${toImportExercises.size}")
            attemptImportExercises()

        }

        initObservers()
    }

    private fun attemptImportExercises() {
        var toRemoveExes= arrayListOf<MyCustomStoreExercise>()
        for (exe in toImportExercises) {
            if (theSameExercise(exe.mExercise) != null) {
                val title = "Overwrite"
                val text = "You already have this exercise, do you want to overwrite it?"
                val dialogPositiveNegativeHandler = object : DialogPositiveNegativeHandler {
                    override fun onPositiveButton(inputText: String) {
                        super.onPositiveButton(inputText)
                        removeExercise(theSameExercise(exe.mExercise)!!)
                        insertExercise(exe.mExercise)
                    }

                }
                Utils.getInfoDialogView(this, title, text, dialogPositiveNegativeHandler)
                toRemoveExes.add(exe)
//                toImportExercises.remove(exe)
            }
        }
        for (removableEx in toRemoveExes) {
            toImportExercises.remove(removableEx)
        }
        if (toImportExercises.isNotEmpty()) {
            libraryViewModel.importExercises(toImportExercises)
            allExesLoaded = false
        }
    }

    private fun insertExercise(mExercise: Exercise) {
        libraryViewModel.insertExercise(mExercise)
    }

    private fun removeExercise(theSameExercise: Exercise) {
        libraryViewModel.removeExercise(theSameExercise)
    }

    private fun theSameExercise(exe: Exercise): Exercise? {
        for (exercise in allMyExercises) {
            if (exe.md5 == exercise.md5) {
                return exercise
            }
        }
        return null
    }

    lateinit var mAlertDialog: AlertDialog

    private fun showFilterDialog() {

        val mDialogView =
            LayoutInflater.from(this).inflate(R.layout.dialog_window_library_filter, null)
        var customCategoriesAdapter = MyCustomCategoriesAdapter(this)
        mDialogView.libraryCategoriesList.layoutManager = LinearLayoutManager(this)
        mDialogView.libraryCategoriesList.adapter = customCategoriesAdapter
        mDialogView.filter_btn.setOnClickListener {

            filterByCategory(customCategoriesAdapter.checkedCategories)
        }
        mDialogView.clear_btn.setOnClickListener {
            customCategoriesAdapter.clearFilters()
        }


        val mBuilder = AlertDialog.Builder(this).setView(mDialogView)
        mBuilder.setCancelable(true)
        mAlertDialog = mBuilder.show()
    }

    private fun filterByCategory(checkedCategories: java.util.ArrayList<String>) {
        Timber.d("Filter by:  and size is ${checkedCategories.size}")
        active_categories.visibility = View.GONE

        var filteredExes = arrayListOf<MyCustomStoreExercise>()
        for (myCustomStoreExercise in allMyCustomStoreExercises) {
            if (checkedCategories.isEmpty()) {
                filteredExes.add(myCustomStoreExercise)
            } else {
                var catsString = ""
                var i = 0
                for (categ in checkedCategories) {
                    i++
                    catsString += categ
                    if (i < checkedCategories.size) {
                        catsString += "  -  "
                    }
                    if (myCustomStoreExercise.mCategory == categ) {
                        filteredExes.add(myCustomStoreExercise)
                        continue
                    }
                }
                active_categories.visibility = View.VISIBLE
                active_categories.text = catsString
            }

        }
        mAlertDialog.dismiss()
        myExercisesAdapter.setContent(filteredExes)
    }

    private fun initObservers() {
        libraryViewModel.isDoingBackgroundTask.observe(this, Observer {
            Timber.d("is doing background task: $it ")
            if (it) {
                progressBar_cyclic.visibility = View.VISIBLE
            } else {
                progressBar_cyclic.visibility = View.GONE
            }
        })

        var isInserting = false

        libraryViewModel.isInsertingExercises.observe(this, Observer {
            if (isInserting && !it) {
                progressBar_cyclic.visibility = View.GONE
                Toast.makeText(this, "Exercises imported", Toast.LENGTH_SHORT).show()
                val allExercises = arrayListOf<MyCustomStoreExercise>()
                for (exe in allMyCustomStoreExercises) {
                    allExercises.add(exe)
                }
                myExercisesAdapter = MyCustomExercisesAdapter(this)
                libraryExercisesList.adapter = myExercisesAdapter

                myExercisesAdapter.setContent(allExercises)
            } else {
                isInserting = true
                progressBar_cyclic.visibility = View.VISIBLE
            }
        })

        libraryViewModel.categoriesList.observe(this, Observer {
            //            allCategories = it
            for (cat in it) {
                filterCategories.add(Pair(cat, false))
            }
            toolbar_filter.setOnClickListener {
                showFilterDialog()
            }
        })

        libraryViewModel.myExercises.observe(this, Observer {
            Timber.d("got my info of exercises: ${it.size} ")
            allMyExercises = it
            allExesLoaded = true
            if (storeExercisesLoaded) {
                setContentToAdapter()
            }
        })

        libraryViewModel.storeExercisesList.observe(this, Observer {
            Timber.d("got my info of store exercise: ${it.size} ")

            allMyCustomStoreExercises = it
            for (exe in allMyCustomStoreExercises) {
                for (myExe in allMyExercises) {
                    if (myExe.md5==exe.mExercise.md5) {
                        exe.isOwned = true
                    }
                }
            }

            storeExercisesLoaded = true
            if (allExesLoaded) {
                setContentToAdapter()
            } else {
            }

            search_view.setOnQueryTextListener(this)
        })
    }

    private fun setContentToAdapter() {
        for (exe in allMyCustomStoreExercises) {
            for (myExe in allMyExercises) {
                if (myExe.md5==exe.mExercise.md5) {
                    exe.isOwned = true
                }
            }
        }
        myExercisesAdapter.setContent(allMyCustomStoreExercises)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        myExercisesAdapter.filter(newText)
        return true
    }

    //is an inner class because I want to acces the categoriesfilter variable which says which filters there is and if they are active (Pair<String, Boolean>)
    inner class MyCustomCategoriesAdapter(
        context: Context
    ) :
        RecyclerView.Adapter<MyCategoryViewHolder>() {
        private val inflater: LayoutInflater = LayoutInflater.from(context)
        var checkedCategories = buildCheckedCats()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCategoryViewHolder {
            val itemView = inflater.inflate(R.layout.item_holder_filter_library, parent, false)
            return MyCategoryViewHolder(itemView)
        }

        override fun getItemCount(): Int {
            return filterCategories.size
        }

        override fun onBindViewHolder(holder: MyCategoryViewHolder, position: Int) {
            var categoryName = filterCategories[position]
            holder.name.text = categoryName.first
            holder.checkBox.isChecked = categoryName.second
            holder.checkBox.setOnClickListener {
                if (holder.checkBox.isChecked && !checkedCategories.contains(categoryName.first)) {
                    checkedCategories.add(categoryName.first)
                    filterCategories[position] = Pair(categoryName.first, true)
                } else {
                    checkedCategories.remove(categoryName.first)
                    filterCategories[position] = Pair(categoryName.first, false)
                }
            }
        }

        fun clearFilters() {
            for (i in 0 until filterCategories.size) {
                filterCategories[i] = Pair(filterCategories[i].first, false)
            }
            checkedCategories = buildCheckedCats()

            notifyDataSetChanged()
        }

        private fun buildCheckedCats(): ArrayList<String> {
            var array = arrayListOf<String>()
            for (cat in filterCategories) {
                if (cat.second) {
                    array.add(cat.first)
                }
            }
            return array
        }


    }

    class MyCustomExercisesAdapter(context: Context) :
        RecyclerView.Adapter<MyExerciseViewHolder>() {


        private val inflater: LayoutInflater = LayoutInflater.from(context)

        var currentExesInAdapter: ArrayList<MyCustomStoreExercise> = arrayListOf()
        var initialExesInAdapter: ArrayList<MyCustomStoreExercise> = arrayListOf()
        var checkedExercises: ArrayList<MyCustomStoreExercise> = arrayListOf()
//        var allMyExercises = arrayListOf<Exercise>()

        fun setContent(exes: ArrayList<MyCustomStoreExercise>) {
            initialExesInAdapter = exes
            currentExesInAdapter = exes
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyExerciseViewHolder {
            val itemView = inflater.inflate(R.layout.item_holder_exercise_library, parent, false)
            return MyExerciseViewHolder(itemView)
        }

        override fun getItemCount(): Int {
            return currentExesInAdapter.size
        }

        override fun onBindViewHolder(holderExercise: MyExerciseViewHolder, position: Int) {
//            Timber.d("Checked exercises: ${checkedExercises} is $position checked: ${holderExercise.checkBox.isChecked}")
            var myCustomStoreExercise = currentExesInAdapter[position]
            val nameDisplay = if (myCustomStoreExercise.isOwned) {
                "${myCustomStoreExercise.mExercise.name} (OWNED)"
            } else {
                myCustomStoreExercise.mExercise.name
            }

            holderExercise.name.text = nameDisplay
            holderExercise.descriptionText.text = myCustomStoreExercise.mExercise.description
            holderExercise.name.setOnClickListener {
                holderExercise.toggleExpand()
            }

            holderExercise.checkBox.isChecked = checkedExercises.contains(myCustomStoreExercise)
//            holderExercise.checkBox.setOnCheckedChangeListener { _, isChecked ->
//                if (isChecked && !checkedExercises.contains(myCustomStoreExercise)) {
//                    checkedExercises.add(myCustomStoreExercise)
//                } else {
//                    checkedExercises.remove(myCustomStoreExercise)
//
//                }
//            }
            holderExercise.checkBox.setOnClickListener {
                if (holderExercise.checkBox.isChecked && !checkedExercises.contains(
                        myCustomStoreExercise
                    )
                ) {
                    checkedExercises.add(myCustomStoreExercise)
                } else {
                    checkedExercises.remove(myCustomStoreExercise)
                }
            }
        }

        fun filter(newText: String?) {
            var filteredExes = arrayListOf<MyCustomStoreExercise>()
            Timber.d("Filtering text: $newText")
            if (newText != null) {
                if (newText.isEmpty()) {
                    filteredExes = initialExesInAdapter
                } else {
                    for (exe in initialExesInAdapter) {
                        if (exe.mExercise.name.toLowerCase().contains(newText.toLowerCase())) {
                            filteredExes.add(exe)
                        }
                    }
                }
                currentExesInAdapter = filteredExes

                notifyDataSetChanged()
            }
        }

    }

    class MyExerciseViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var expanded: Boolean = false

        var name = v.titleTextExerciseHolder
        var collapsibleLinearLayout = v.collapsibleLinearLayout
        var descriptionText = v.descriptionTextExerciseHolder
        var checkBox = v.checkboxLeftExerciseHolder

        fun toggleExpand() {
            if (!expanded) {
                collapsibleLinearLayout!!.visibility = View.VISIBLE
                expanded = true
            } else {
                collapsibleLinearLayout!!.visibility = View.GONE
                expanded = false
            }
        }
    }

    class MyCategoryViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        var name = v.titleTextCategoryHolder
        var checkBox = v.checkBox2


    }

}