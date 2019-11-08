package com.tatoe.mydigicoach.ui.util

import android.view.View

interface ClickListenerRecyclerView {

    fun onClick(view:View,position:Int) {

    }

    fun onClick(view: View,position: Int,actionId:Int){

    }

    fun onLongClick(view: View, position: Int) {

    }

}