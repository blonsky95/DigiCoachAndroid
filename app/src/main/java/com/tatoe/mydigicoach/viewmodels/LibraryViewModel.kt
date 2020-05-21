package com.tatoe.mydigicoach.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.AppRepository
import com.tatoe.mydigicoach.database.AppRoomDatabase
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.network.MyCustomFirestoreTransferExercise
import com.tatoe.mydigicoach.network.MyCustomStoreExercise
import com.tatoe.mydigicoach.utils.MD5Encrypter
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.HashMap

class LibraryViewModel(var application: Application, var db: FirebaseFirestore) : ViewModel() {

    val isDoingBackgroundTask = MutableLiveData<Boolean>(true)
    val isInsertingExercises = MutableLiveData<Boolean>(false)

    private val repository: AppRepository
    val categoriesList = MutableLiveData<ArrayList<String>>(arrayListOf())
    val storeExercisesList = MutableLiveData<ArrayList<MyCustomStoreExercise>>(arrayListOf())
    val myExercises: LiveData<List<Exercise>>

    init {
        val appDB = AppRoomDatabase.getInstance(application)
        val exerciseDao = appDB.exercisesDao()
        val blockDao = appDB.blockDao()
        val dayDao = appDB.dayDao()

        repository =
            AppRepository(exerciseDao, blockDao, dayDao)

        myExercises = repository.allExercises

        getLibraryExercises()
    }

    fun getLibraryExercises() {
        isDoingBackgroundTask.postValue(true)
        var docRef = db.collection("store_exercises")

        docRef.get().addOnSuccessListener { docs ->

            var categoryList = arrayListOf<String>()
            var exercisePairList = arrayListOf<MyCustomStoreExercise>()

            for (document in docs) {
                var exerciseCategory = document["name"] as String
                categoryList.add(exerciseCategory)
                var docRef2 =
                    db.collection("store_exercises").document(document.id).collection("exercises")
                docRef2.get().addOnSuccessListener { docs2 ->
                    for (doc in docs2) {
                        exercisePairList.add(
                            MyCustomStoreExercise(doc.toObject(MyCustomFirestoreTransferExercise::class.java).toExercise(),exerciseCategory)
                        )
                    }
                    //little cheat to only trigger observers when all categories are present
                    if (exercisePairList.size == 16) {
                        Timber.d("posting to observers ")
                        categoriesList.value = categoryList
                        storeExercisesList.postValue(exercisePairList)
                        isDoingBackgroundTask.postValue(false)
                    }
                }
            }

        }.addOnFailureListener { e ->
            Timber.d("Error recovering doc: $e")
        }

    }

    fun importExercises(toImportStoreExercises: java.util.ArrayList<MyCustomStoreExercise>) = viewModelScope.launch {
        isInsertingExercises.postValue(true)
        val toImportExercises = arrayListOf<Exercise>()
        for (storeExe in toImportStoreExercises) {
            toImportExercises.add(storeExe.mExercise)
        }
        repository.insertExercises(toImportExercises.toList()).let {
            //when operation is done post that inserting is finiished --- isinserting = false
            isInsertingExercises.postValue(false)
        }

    }

    fun removeExercise(theSameExercise: Exercise) = viewModelScope.launch{
        repository.deleteExercise(theSameExercise)
    }

    fun insertExercise(newExe: Exercise) = viewModelScope.launch{
        repository.insertExercise(newExe)
    }


//    fun addBunchOfStubStoreExercises() {
//        val powerlifting = arrayListOf<Exercise>()
//        powerlifting.add(Exercise("Squats", "Full depth squats"))
//        powerlifting.add(Exercise("Bench Press", "Touch chest for max gains"))
//        powerlifting.add(
//            Exercise(
//                "Deadlift",
//                "Technique-wise make sure straight back, and engaging posterior chain when starting lift, start with legs not with back"
//            )
//        )
//
//        for (exercise in powerlifting) {
//            db.collection("store_exercises").document("powerlifting").collection("exercises")
//                .document(exercise.name)
//                .set(MyCustomFirestoreTransferExercise(exercise))
//                .addOnSuccessListener { Timber.d("DocumentSnapshot successfully written!") }
//                .addOnFailureListener { e -> Timber.d("Error writing document: $e") }
//        }
//
//
//    }

    fun addBunchOfStubStoreExercises() {

        val olympicLiftingExes = arrayListOf<Exercise>()
        var exeHashMap: HashMap<Int, HashMap<String, String>>

        exeHashMap = hashMapOf(0 to hashMapOf("Name" to "Snatch"), 1 to hashMapOf( "Description" to "Lift bar from ground, hit at hips and gain control over bar either as power snatch or normal snatch (full depth)"))
        var exe = Exercise(exeHashMap)
        exe.md5= MD5Encrypter.getMD5(exe)
        olympicLiftingExes.add(exe)
        exeHashMap = hashMapOf(0 to hashMapOf("Name" to "Hang snatch"), 1 to hashMapOf("Description" to "Lift bar from hips, gain control over bar either as power snatch or normal snatch (full depth)"))
        exe = Exercise(exeHashMap)
        exe.md5= MD5Encrypter.getMD5(exe)
        olympicLiftingExes.add(exe)
        exeHashMap = hashMapOf(0 to hashMapOf("Name" to "Clean"), 1 to hashMapOf("Description" to "Lift bar from ground, hit at hips and gain control over bar either as power clean or normal clean (full depth)"))
        exe = Exercise(exeHashMap)
        exe.md5= MD5Encrypter.getMD5(exe)
        olympicLiftingExes.add(exe)
        exeHashMap = hashMapOf(0 to hashMapOf("Name" to "Hang Clean"), 1 to hashMapOf("Description" to "Lift bar from hips, clean"))
        exe = Exercise(exeHashMap)
        exe.md5= MD5Encrypter.getMD5(exe)
        olympicLiftingExes.add(exe)
        exeHashMap = hashMapOf(0 to hashMapOf("Name" to "Clean & jerk"), 1 to hashMapOf("Description" to "Lift bar from ground, hit at hips and gain control over bar shoulders"))
        exe = Exercise(exeHashMap)
        exe.md5= MD5Encrypter.getMD5(exe)
        olympicLiftingExes.add(exe)


        val longDistRuns = arrayListOf<Exercise>()

        exeHashMap = hashMapOf(0 to hashMapOf("Name" to "3km"), 1 to hashMapOf( "Description" to "3km optimal time 11:00"))
        exe = Exercise(exeHashMap)
        exe.md5= MD5Encrypter.getMD5(exe)
        longDistRuns.add(exe)
        exeHashMap = hashMapOf(0 to hashMapOf("Name" to "5km"), 1 to hashMapOf( "Description" to "5km optimal time 20:00"))
        exe = Exercise(exeHashMap)
        exe.md5= MD5Encrypter.getMD5(exe)
        longDistRuns.add(exe)
        exeHashMap = hashMapOf(0 to hashMapOf("Name" to "10km"), 1 to hashMapOf( "Description" to "10km optimal time 45:00"))
        exe = Exercise(exeHashMap)
        exe.md5= MD5Encrypter.getMD5(exe)
        longDistRuns.add(exe)
        exeHashMap = hashMapOf(0 to hashMapOf("Name" to "Half marathon"), 1 to hashMapOf("Description" to "21.098km"))
        exe = Exercise(exeHashMap)
        exe.md5= MD5Encrypter.getMD5(exe)
        longDistRuns.add(exe)
        exeHashMap = hashMapOf(0 to hashMapOf("Name" to "Marathon"), 1 to hashMapOf( "Description" to "42.196km lol have fun"))
        exe = Exercise(exeHashMap)
        exe.md5= MD5Encrypter.getMD5(exe)
        longDistRuns.add(exe)

//        longDistRuns.add(Exercise("3km","3km optimal time 11:00"))
//        longDistRuns.add(Exercise("5km","5km optimal time 20:00"))
//        longDistRuns.add(Exercise("10km","10km optimal time 45:00"))
//        longDistRuns.add(Exercise("Half-marathon","21.098km"))
//        longDistRuns.add(Exercise("Marathon","42.196km lol have fun"))

        val sprints = arrayListOf<Exercise>()

        exeHashMap = hashMapOf(0 to hashMapOf("Name" to "30m"), 1 to hashMapOf( "Description" to "between 5 and 8 reps, recovery of 3/4 minutes minimum"))
        exe = Exercise(exeHashMap)
        exe.md5= MD5Encrypter.getMD5(exe)
        sprints.add(exe)
        exeHashMap = hashMapOf(0 to hashMapOf("Name" to "100m"), 1 to hashMapOf("Description" to "2 sets of 10 runs at 85% intensity, with 1-2 min recovery, full rest between sets"))
        exe = Exercise(exeHashMap)
        exe.md5= MD5Encrypter.getMD5(exe)
        sprints.add(exe)
        exeHashMap = hashMapOf(0 to hashMapOf("Name" to "Lactic pyramid"), 1 to hashMapOf("Description" to "100-120-150-180-150-120-100 recovery of 5-6 min"))
        exe = Exercise(exeHashMap)
        exe.md5= MD5Encrypter.getMD5(exe)
        sprints.add(exe)

//        sprints.add(Exercise("30m","between 5 and 8 reps, recovery of 3/4 minutes minimum"))
//        sprints.add(Exercise("100m","2 sets of 10 runs at 85% intensity, with 1-2 min recovery, full rest between sets"))
//        sprints.add(Exercise("Lactic pyramid","100-120-150-180-150-120-100 recovery of 5-6 min"))

        val powerlifting = arrayListOf<Exercise>()
        exeHashMap = hashMapOf(0 to hashMapOf("Name" to "Squats"), 1 to hashMapOf("Description" to "Full depth squats"))
        exe = Exercise(exeHashMap)
        exe.md5= MD5Encrypter.getMD5(exe)
        powerlifting.add(exe)
        exeHashMap = hashMapOf(0 to hashMapOf("Name" to "Bench Press"), 1 to hashMapOf("Description" to "Touch chest for max gains"))
        exe = Exercise(exeHashMap)
        exe.md5= MD5Encrypter.getMD5(exe)
        powerlifting.add(exe)
        exeHashMap = hashMapOf(0 to hashMapOf("Name" to "Deadlift"), 1 to hashMapOf( "Description" to "Technique-wise make sure straight back, and engaging posterior chain when starting lift, start with legs not with back"))
        exe = Exercise(exeHashMap)
        exe.md5= MD5Encrypter.getMD5(exe)
        powerlifting.add(exe)

//        powerlifting.add(Exercise("Squats","Full depth squats"))
//        powerlifting.add(Exercise("Bench Press","Touch chest for max gains"))
//        powerlifting.add(Exercise("Deadlift","Technique-wise make sure straight back, and engaging posterior chain when starting lift, start with legs not with back"))

        db.collection("store_exercises").document("olympic_lifting").set(hashMapOf("name" to "Olympic Lifting"))
        db.collection("store_exercises").document("long_distance_running").set(hashMapOf("name" to "Long distance"))
        db.collection("store_exercises").document("sprinting").set(hashMapOf("name" to "Sprinting"))
        db.collection("store_exercises").document("powerlifting").set(hashMapOf("name" to "Powerlifting"))


        for (exercise in olympicLiftingExes) {
            db.collection("store_exercises").document("olympic_lifting").collection("exercises").document(exercise.name)
                .set(MyCustomFirestoreTransferExercise(exercise))
                .addOnSuccessListener {
                    Timber.d("DocumentSnapshot successfully written!")
                }
                .addOnFailureListener {
                        e -> Timber.d("Error writing document: $e")
                }
        }

        for (exercise in longDistRuns) {
            db.collection("store_exercises").document("long_distance_running").collection("exercises").document(exercise.name)
                .set(MyCustomFirestoreTransferExercise(exercise))
                .addOnSuccessListener { Timber.d("DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Timber.d("Error writing document: $e") }
        }

        for (exercise in sprints) {
            db.collection("store_exercises").document("sprinting").collection("exercises").document(exercise.name)
                .set(MyCustomFirestoreTransferExercise(exercise))
                .addOnSuccessListener { Timber.d("DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Timber.d("Error writing document: $e") }
        }

        for (exercise in powerlifting) {
            db.collection("store_exercises").document("powerlifting").collection("exercises").document(exercise.name)
                .set(MyCustomFirestoreTransferExercise(exercise))
                .addOnSuccessListener { Timber.d("DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Timber.d("Error writing document: $e") }
        }


    }
}