package com.tatoe.mydigicoach.network

import com.tatoe.mydigicoach.entity.Exercise

class MyCustomFirestoreTransferExercise(exercise: Exercise) {

    //empty constructor needed to deserialize object and have it in Firestore
    constructor() : this(Exercise("No name", "No description"))

    var mName: String = exercise.name
    var mDesc: String = exercise.description

    var fieldsMap: HashMap<String, HashMap<String, String>> = exercise.fieldsHashMap

    var resultsFieldsMap: HashMap<String, HashMap<String, String>> =exercise.exerciseResults.resultFieldsMap

    var resultsArrayList: ArrayList<HashMap<String, HashMap<String, String>>> =exercise.exerciseResults.resultsArrayList

    fun toExercise(): Exercise {
        var exercise = Exercise(Exercise.stringMapToIntMap(fieldsMap))
        exercise.exerciseResults.resultFieldsMap = resultsFieldsMap
        exercise.exerciseResults.resultsArrayList = resultsArrayList
        return exercise
    }

//    private fun pairToHashMap(fieldsHashMap: HashMap<String, Pair<String, String>>): HashMap<String, HashMap<String, String>> {
//        var map = HashMap<String, HashMap<String, String>>()
//        for (entry in fieldsHashMap) {
//            var hashmap = hashMapOf<String, String>()
//            hashmap["first"] = entry.value.first
//            hashmap["second"] = entry.value.second
//            map[entry.key] = hashmap
//        }
//        return map
//    }
//
//    private fun hashMapToPair(fieldsHashMap: HashMap<String, HashMap<String, String>>): HashMap<String, Pair<String, String>> {
//        var map = HashMap<String, Pair<String, String>>()
//        for (entry in fieldsHashMap) {
//            var pair = Pair(entry.value["first"]!!,entry.value["second"]!!)
//            map[entry.key] = pair
//        }
//        return map
//    }

//    private fun arrayListPairToHashMap(arrayListFieldsHashMap: ArrayList<HashMap<String, Pair<String, String>>>): ArrayList<HashMap<String, HashMap<String, String>>> {
//        var arraylist = arrayListOf<HashMap<String, HashMap<String, String>>>()
//        for (item in arrayListFieldsHashMap) {
//            arraylist.add(pairToHashMap(item))
//        }
//        return arraylist
//    }
//
//    private fun arrayListHashMapToPair(arrayListFieldsHashMap: ArrayList<HashMap<String, HashMap<String, String>>>): ArrayList<HashMap<String, Pair<String, String>>> {
//        var arraylist = arrayListOf<HashMap<String, Pair<String, String>>>()
//        for (item in arrayListFieldsHashMap) {
//            arraylist.add(hashMapToPair(item))
//        }
//        return arraylist
//    }
}
