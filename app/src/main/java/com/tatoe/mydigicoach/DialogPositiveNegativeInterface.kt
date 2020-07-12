package com.tatoe.mydigicoach

interface DialogPositiveNegativeInterface {

    //if you dont want to use the edit text then no need, can be used for dialog boxes with and without an edit text
    fun onPositiveButton(inputText:String = "") {
    }

    fun onNegativeButton() {
    }

}