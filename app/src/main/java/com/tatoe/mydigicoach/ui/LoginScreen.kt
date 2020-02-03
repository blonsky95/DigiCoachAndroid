package com.tatoe.mydigicoach.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.tatoe.mydigicoach.R
import kotlinx.android.synthetic.main.activity_login_screen.*

class LoginScreen : AppCompatActivity() {

    lateinit var loginButton:TextView
    lateinit var userEditText: EditText
    lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)

        loginButton=login_button
        loginButton.setOnClickListener(checkValidUser)

        userEditText=username_field
        passwordEditText=password_field

    }

    private val checkValidUser = View.OnClickListener {
        if (isUserValid(userEditText.text.toString(),passwordEditText.text.toString())) {
            var intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun isUserValid(username: String, password: String): Boolean {
        return true
    }
}
