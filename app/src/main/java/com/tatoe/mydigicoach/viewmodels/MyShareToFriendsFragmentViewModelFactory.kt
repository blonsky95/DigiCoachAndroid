package com.tatoe.mydigicoach.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore

class MyShareToFriendsFragmentViewModelFactory(
    var firebaseFirestore: FirebaseFirestore,
    var application: Application
) : ViewModelProvider.Factory {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ShareToFriendsFragmentViewModel(
            firebaseFirestore, application
        ) as T
    }
}