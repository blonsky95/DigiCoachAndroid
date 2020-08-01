package com.tatoe.mydigicoach.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.tatoe.mydigicoach.network.TransferPackage
import com.tatoe.mydigicoach.ui.util.DataHolder

class FirestoreReceiver : BroadcastReceiver() {

    private val mData = MutableLiveData<ArrayList<TransferPackage>>(arrayListOf())

    fun getData() :MutableLiveData<ArrayList<TransferPackage>> {
        return mData
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        mData.value = DataHolder.receivedExercises
    }
}