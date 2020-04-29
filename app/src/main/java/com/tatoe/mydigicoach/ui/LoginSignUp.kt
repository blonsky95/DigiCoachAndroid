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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.BuildConfig
import com.tatoe.mydigicoach.DialogPositiveNegativeHandler
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.Utils
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

    private lateinit var progress: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_signup)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        loginButton = login_button
        loginButton.setOnClickListener(logInUser)
        registerButton = register_button
        registerButton.setOnClickListener(registerNewUser)

        forgot_your_pw.setOnClickListener {
            Utils.getDialogViewWithEditText(this,"Reset password", "Type your email below","Email",object :
                DialogPositiveNegativeHandler {
                override fun onPositiveButton(userEmail: String) {
                    resetPassword(userEmail)
                }

            })

        }

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

    private fun resetPassword(userEmail: String) {
        auth.sendPasswordResetEmail(userEmail)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.d("Email sent.")
                    Toast.makeText(this,"Email sent to $userEmail", Toast.LENGTH_SHORT).show()
                }
            }
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

    private fun attemptLogIn(user: FirebaseUser?) {
        //assuming you already have permissions if you're here
        if (user != null) {
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
            finish()
        }
    }

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
        progress.visibility = View.VISIBLE

        val username = userEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (username.isNotEmpty() && password.isNotEmpty()) {

            val docRef = db.collection("users").whereEqualTo("username", username)
            docRef.get().addOnSuccessListener { docs ->
                if (docs.isEmpty) {
                    progress.visibility = View.GONE

                    Timber.d("signInWithEmail:failure: unexistent")
                    Toast.makeText(
                        baseContext, " User doesn't exist",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    var doc = docs.documents[0]
                    val userEmail = doc["email"].toString()

                    auth.signInWithEmailAndPassword(
                        userEmail,
                        password
                    )
                        .addOnCompleteListener(this) { task ->
                            progress.visibility = View.GONE
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Timber.d("signInWithEmail:success")
                                val user = auth.currentUser
                                Toast.makeText(
                                    baseContext, " Welcome back ${user?.email}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                attemptLogIn(user)
                            } else {
                                // If sign in fails, display a message to the user.
                                progress.visibility = View.GONE
                                Timber.w("signInWithEmail:failure exception: ${task.exception}")
                                Toast.makeText(
                                    baseContext, "Authentication login failed.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                attemptLogIn(null)
                            }
                        }
                }
            }

                .addOnFailureListener { e ->
                    progress.visibility = View.GONE

                    Timber.d("signInWithEmail:failure: $e")
                    Toast.makeText(
                        baseContext, " Weird error",
                        Toast.LENGTH_SHORT
                    ).show()
                }


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
            auth.createUserWithEmailAndPassword(
                email, password
            )
                .addOnCompleteListener(this) { task ->


                    if (task.isSuccessful) {

                        val userMap = HashMap<String, String>()
                        userMap["username"] = username
                        userMap["email"] = email
                        db.collection("users").document(auth.currentUser!!.uid).set(userMap)
                            .addOnSuccessListener {
                                progress.visibility = View.GONE

                                Timber.d("createUserWithEmail:success")
                                val user = auth.currentUser
                                Toast.makeText(
                                    baseContext, "New user registered! Welcome: $username",
                                    Toast.LENGTH_SHORT
                                ).show()
                                attemptLogIn(user)

                            }
                            .addOnFailureListener { e ->
                                progress.visibility = View.GONE

                                Timber.d("error registering: $e")
                                Toast.makeText(
                                    baseContext, "Registration failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        Timber.w("createUserWithEmail:failure exception: ${task.exception}")
                        Toast.makeText(
                            baseContext, "Authentication register failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                        attemptLogIn(null)
                    }
                }
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
        val currentUser = auth.currentUser
        attemptLogIn(currentUser)
    }
}