package com.tatoe.mydigicoach.ui

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.R
import kotlinx.android.synthetic.main.activity_login_screen.login_button
import kotlinx.android.synthetic.main.activity_login_screen.register_button

class UserAccess : AppCompatActivity() {

    companion object {
        const val ACCESS_EXTRA="ACCESS_TYPE"
        const val ACCESS_LOGIN = 0
        const val ACCESS_REGISTER = 1
    }

    private var hasPermissions = false
    private val PERMISSION_REQUEST_CODE = 123

    private var auth = FirebaseAuth.getInstance()
//    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen_2)

        checkPermissions()

        attemptLogIn(auth.currentUser)

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

    private fun attemptLogIn(user: FirebaseUser?) {
        if (user != null) {
            if (hasPermissions) {
                val intent = Intent(this, HomeScreen::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun checkPermissions() {
        //todo add more info in to why i need access to storage
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        } else {
            hasPermissions = true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                hasPermissions =
                    (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                return
            }

            else -> {
                // Ignore all other requests.
            }
        }
    }


}
