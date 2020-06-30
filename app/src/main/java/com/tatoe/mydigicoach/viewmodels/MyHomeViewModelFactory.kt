package com.tatoe.mydigicoach.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore

class MyHomeViewModelFactory(var application: Application, var firebaseFirestore: FirebaseFirestore) : ViewModelProvider.Factory {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(
            application,firebaseFirestore
        ) as T
    }
}