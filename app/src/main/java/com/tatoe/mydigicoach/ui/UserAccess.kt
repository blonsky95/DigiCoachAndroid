package com.tatoe.mydigicoach.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tatoe.mydigicoach.R
import kotlinx.android.synthetic.main.activity_login_screen.login_button
import kotlinx.android.synthetic.main.activity_login_screen.register_button

class UserAccess : AppCompatActivity() {

    companion object {
        const val ACCESS_EXTRA="ACCESS_TYPE"
        const val ACCESS_LOGIN = 0
        const val ACCESS_REGISTER = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen_2)

        register_button.setOnClickListener {
            val intent = Intent(this, LoginSignUp::class.java)
            intent.putExtra(ACCESS_EXTRA, ACCESS_REGISTER)
            startActivity(intent)
        }

        login_button.setOnClickListener {
            val intent = Intent(this, LoginSignUp::class.java)
            intent.putExtra(ACCESS_EXTRA, ACCESS_LOGIN)
            startActivity(intent)
        }
    }


}
