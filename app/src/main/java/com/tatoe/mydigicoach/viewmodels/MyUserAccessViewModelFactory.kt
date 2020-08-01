package com.tatoe.mydigicoach.viewmodels

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore

class MyUserAccessViewModelFactory(var application: Application, var firebaseFirestore: FirebaseFirestore, var prefs:SharedPreferences) : ViewModelProvider.Factory {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UserAccessViewModel(
            application,firebaseFirestore, prefs
        ) as T
    }
}