package com.tatoe.mydigicoach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore

class MyHomeScreenViewModelFactory(var firebaseFirestore: FirebaseFirestore) : ViewModelProvider.Factory {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeScreenViewModel(firebaseFirestore) as T
    }
}