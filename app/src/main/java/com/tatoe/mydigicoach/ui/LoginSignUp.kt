package com.tatoe.mydigicoach.ui

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.BuildConfig
import com.tatoe.mydigicoach.DialogPositiveNegativeHandler
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.Utils
import com.tatoe.mydigicoach.viewmodels.LoginSignUpViewModel
import com.tatoe.mydigicoach.viewmodels.MyLoginSignUpViewModelFactory
import kotlinx.android.synthetic.main.activity_login_screen_2.login_button
import kotlinx.android.synthetic.main.activity_login_screen_2.register_button
import kotlinx.android.synthetic.main.activity_login_signup.*
//import org.junit.experimental.results.ResultMatchers.isSuccessful
import timber.log.Timber


class LoginSignUp : AppCompatActivity() {
    private lateinit var loginButton: TextView
    private lateinit var registerButton: TextView

    private lateinit var userEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var emailEditText: EditText


    private lateinit var auth: FirebaseAuth
    private var db = FirebaseFirestore.getInstance()

    private lateinit var loginSignUpViewModel: LoginSignUpViewModel

    private lateinit var progress: ProgressBar
    private var mHasAccess = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_signup)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        loginSignUpViewModel = ViewModelProviders.of(
            this,
            MyLoginSignUpViewModelFactory(application, db)
        ).get(
            LoginSignUpViewModel::class.java
        )

        loginButton = login_button
        loginButton.setOnClickListener(logInUser)
        registerButton = register_button
        registerButton.setOnClickListener(registerNewUser)

        forgot_your_pw.setOnClickListener {
            val context = this
            Utils.getDialogViewWithEditText(
                this,
                "Reset password",
                "Type your email below",
                "Email",
                object :
                    DialogPositiveNegativeHandler {
                    override fun onPositiveButton(inputText: String) {
                        loginSignUpViewModel.resetFirebaseUserPassword(inputText, context)
//                    resetPassword(userEmail)
                    }
                })
        }

        initObservers()
        progress = progressBar_cyclic

        userEditText = username_et
        passwordEditText = password_et
        emailEditText = email_et

        var accessType = intent.getIntExtra(UserAccess.ACCESS_EXTRA, UserAccess.ACCESS_LOGIN)
        updateLayout(accessType)

//        checkPermissions()

        if (BuildConfig.DEBUG) {
            magic_btn.visibility = View.VISIBLE
            magic_btn.setOnClickListener {
                userEditText.text = SpannableStringBuilder("pablo.trescoli@gmail.com")
                passwordEditText.text = SpannableStringBuilder("123456")
            }
        }
    }

    private fun initObservers() {
        loginSignUpViewModel.isDoingBackgroundTask.observe(this, Observer {
            if (it) {
                progress.visibility = View.VISIBLE
            } else {
                progress.visibility = View.GONE
            }
        })

        loginSignUpViewModel.hasAccess.observe(this, Observer {
            attemptLogIn(it)
        })
    }


    private fun updateLayout(accessType: Int) {
        when (accessType) {
            UserAccess.ACCESS_LOGIN -> {
                emailEditText.visibility = View.GONE
                forgot_your_pw.visibility = View.VISIBLE
                loginButton.visibility = View.VISIBLE
                registerButton.visibility = View.GONE

//                loginButton.setOnClickListener(logInUser)
            }
            UserAccess.ACCESS_REGISTER -> {
                emailEditText.visibility = View.VISIBLE
                forgot_your_pw.visibility = View.GONE
                loginButton.visibility = View.GONE
                registerButton.visibility = View.VISIBLE

//                registerButton.setOnClickListener(registerNewUser)
            }

        }
    }
    private fun attemptLogIn(hasAccess:Boolean) {
        //assuming you already have permissions if you're here
        if (hasAccess) {
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
            finish()
        }
    }
//    private fun attemptLogIn(user: FirebaseUser?) {
//        //assuming you already have permissions if you're here
//        if (user != null) {
//            val intent = Intent(this, HomeScreen::class.java)
//            startActivity(intent)
//            finish()
//        }
//    }

//    private fun checkPermissions() {
//        if (ContextCompat.checkSelfPermission(
//                this,
//                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
//            )
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//            // Permission is not granted
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                PERMISSION_REQUEST_CODE
//            )
//        } else {
//            hasPermissions = true
//        }
//    }

    private val logInUser = View.OnClickListener {
//        progress.visibility = View.VISIBLE

        val username = userEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (username.isNotEmpty() && password.isNotEmpty()) {

            loginSignUpViewModel.logInUser(username, password, this)

        } else {
            progress.visibility = View.GONE
            Toast.makeText(
                baseContext, "Empty field",
                Toast.LENGTH_SHORT
            ).show()
        }


    }

    private val registerNewUser = View.OnClickListener {
        progress.visibility = View.VISIBLE

        val username = userEditText.text.toString()
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
            loginSignUpViewModel.registerNewUser(username,email,password, this)
        } else {
            progress.visibility = View.GONE
            Toast.makeText(
                baseContext, "Empty field",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
//        val currentUser = auth.currentUser
//        attemptLogIn(currentUser)
    }
}