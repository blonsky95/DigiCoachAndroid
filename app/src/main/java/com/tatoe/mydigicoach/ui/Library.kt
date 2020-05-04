package com.tatoe.mydigicoach.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.DialogPositiveNegativeHandler
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.viewmodels.LibraryViewModel
import com.tatoe.mydigicoach.viewmodels.MyLibraryViewModelFactory
import kotlinx.android.synthetic.main.activity_library.*
import kotlinx.android.synthetic.main.dialog_window_library_filter.view.*
import kotlinx.android.synthetic.main.item_holder_exercise_library.view.*
import kotlinx.android.synthetic.main.item_holder_filter_library.view.*
import timber.log.Timber

class Library : AppCompatActivity(), SearchView.OnQueryTextListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var myExercisesAdapter: MyCustomExercisesAdapter
    private lateinit var libraryViewModel: LibraryViewModel

    private var allExercisePairs = arrayListOf<Pair<String, Exercise>>()
    private var allCategories = arrayListOf<String>()

    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        setSupportActionBar(findViewById(R.id.my_toolbar))
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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

        initObservers()
    }

    lateinit var mAlertDialog: AlertDialog

    private fun showFilterDialog() {

//        var dialogPositiveNegativeHandler: DialogPositiveNegativeHandler? = null

        val mDialogView =
            LayoutInflater.from(this).inflate(R.layout.dialog_window_library_filter, null)
        var customCategoriesAdapter = MyCustomCategoriesAdapter(this, allCategories)
        mDialogView.libraryCategoriesList.layoutManager = LinearLayoutManager(this)
        mDialogView.libraryCategoriesList.adapter = customCategoriesAdapter
        mDialogView.filter_btn.setOnClickListener {

            filterByCategory(customCategoriesAdapter.checkedCategories)
        }
        mDialogView.clear_btn.setOnClickListener {
            //            customCategoriesAdapter= MyCustomCategoriesAdapter(this, allCategories)
        }


        val mBuilder = AlertDialog.Builder(this).setView(mDialogView)
//        mBuilder.setP
        mBuilder.setCancelable(true)
        mAlertDialog = mBuilder.show()
    }

    private fun filterByCategory(checkedCategories: java.util.ArrayList<String>) {
        Timber.d("Filter by:  and size is ${checkedCategories.size}")
        active_categories.visibility=View.GONE

        var filteredExes = arrayListOf<Exercise>()
        for (exePair in allExercisePairs) {
            if (checkedCategories.isEmpty()) {
                filteredExes.add(exePair.second)
            } else {
                var catsString=""
                var i = 0
                for (categ in checkedCategories) {
                    i++
                    catsString+= categ
                    if (i<checkedCategories.size){
                        catsString+="  -  "
                    }
                    if (exePair.first == categ) {
                        filteredExes.add(exePair.second)
                        continue
                    }
                }
                active_categories.visibility=View.VISIBLE
                active_categories.text=catsString
            }

        }
        mAlertDialog.dismiss()
        myExercisesAdapter.setContent(filteredExes)
    }

    private fun initObservers() {
        libraryViewModel.isDoingBackgroundTask.observe(this, Observer {
            Timber.d("is doing background task: $it ")
            if (it) {
                progressBar_cyclic.visibility=View.VISIBLE
            } else {
                progressBar_cyclic.visibility=View.GONE
            }
        })

        libraryViewModel.categoriesList.observe(this, Observer {
            allCategories = it
            toolbar_filter.setOnClickListener {
                showFilterDialog()
            }
        })
        libraryViewModel.exercisesPairsList.observe(this, Observer {
            Timber.d("got my info of exercisePairslist: ${it.size} ")

            allExercisePairs = it
            val allExercises = arrayListOf<Exercise>()
            for (exe in allExercisePairs) {
                allExercises.add(exe.second)
            }
            myExercisesAdapter.setContent(allExercises)
            search_view.setOnQueryTextListener(this)
        })
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        myExercisesAdapter.filter(newText)
        return true
    }

    class MyCustomCategoriesAdapter(
        context: Context,
        var allCategories: java.util.ArrayList<String>
    ) :
        RecyclerView.Adapter<MyCategoryViewHolder>() {

        private val inflater: LayoutInflater = LayoutInflater.from(context)
        val checkedCategories = arrayListOf<String>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCategoryViewHolder {
            val itemView = inflater.inflate(R.layout.item_holder_filter_library, parent, false)
            return MyCategoryViewHolder(itemView)
        }

        override fun getItemCount(): Int {
            return allCategories.size
        }

        override fun onBindViewHolder(holder: MyCategoryViewHolder, position: Int) {
            var categoryName = allCategories[position]
            holder.name.text = categoryName
            holder.checkBox.setOnClickListener {
                if (holder.checkBox.isChecked && !checkedCategories.contains(categoryName)) {
                    checkedCategories.add(categoryName)
                } else {
                    checkedCategories.remove(categoryName)
                }
            }
        }

    }

    class MyCustomExercisesAdapter(context: Context) :
        RecyclerView.Adapter<MyExerciseViewHolder>() {

        //todo offline copy of

        private val inflater: LayoutInflater = LayoutInflater.from(context)

        var currentExesInAdapter: ArrayList<Exercise> = arrayListOf()
        var initialExesInAdapter: ArrayList<Exercise> = arrayListOf()

        fun setContent(exes: ArrayList<Exercise>) {
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
            holderExercise.name.text = currentExesInAdapter[position].name
        }

        fun filter(newText: String?) {
            var filteredExes = arrayListOf<Exercise>()
            Timber.d("Filtering text: $newText")
            if (newText != null) {
                if (newText.isEmpty()) {
                    filteredExes = initialExesInAdapter
                } else {
                    for (exe in initialExesInAdapter) {
                        if (exe.name.toLowerCase().contains(newText.toLowerCase())) {
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
        var name = v.titleTextExerciseHolder
    }

    class MyCategoryViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var name = v.titleTextCategoryHolder
        var checkBox = v.checkBox2
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.library_toolbar_menu, menu)
//
//        return super.onCreateOptionsMenu(menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
//
//        android.R.id.home -> {
//            onBackPressed()
//            true
//        }
//
//        else -> {
//            super.onOptionsItemSelected(item)
//        }
//    }


}