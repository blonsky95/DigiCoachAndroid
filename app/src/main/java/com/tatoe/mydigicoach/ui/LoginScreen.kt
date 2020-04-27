package com.tatoe.mydigicoach.ui

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tatoe.mydigicoach.BuildConfig
import com.tatoe.mydigicoach.R
import kotlinx.android.synthetic.main.activity_login_screen.*
import kotlinx.android.synthetic.main.activity_login_screen.login_button
import kotlinx.android.synthetic.main.activity_login_screen.register_button
import kotlinx.android.synthetic.main.activity_login_screen_2.*
import timber.log.Timber
import java.util.jar.Manifest

class LoginScreen : AppCompatActivity() {

    companion object {
        const val ACCESS_EXTRA="ACCESS_TYPE"
        const val ACCESS_LOGIN = 0
        const val ACCESS_REGISTER = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen_2)

        register_button.setOnClickListener {
            val intent = Intent(this, LoginScreenV2::class.java)
            intent.putExtra(ACCESS_EXTRA, ACCESS_REGISTER)
            startActivity(intent)
        }

        login_button.setOnClickListener {
            val intent = Intent(this, LoginScreenV2::class.java)
            intent.putExtra(ACCESS_EXTRA, ACCESS_LOGIN)
            startActivity(intent)
        }
    }


}
