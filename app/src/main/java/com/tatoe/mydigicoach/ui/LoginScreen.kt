package com.tatoe.mydigicoach.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tatoe.mydigicoach.R
import kotlinx.android.synthetic.main.activity_login_screen.*
import timber.log.Timber

class LoginScreen : AppCompatActivity() {

    private lateinit var loginButton: TextView
    private lateinit var registerButton: TextView

    private lateinit var userEditText: EditText
    private lateinit var passwordEditText: EditText

    private lateinit var auth: FirebaseAuth
    private lateinit var progress: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        loginButton = login_button
        loginButton.setOnClickListener(checkValidUser)
        registerButton = register_button
        registerButton.setOnClickListener(registerNewUser)

        progress = progressBar_cyclic

        userEditText = username_field
        passwordEditText = password_field

    }

    private val checkValidUser = View.OnClickListener {
        progress.visibility = View.VISIBLE

        if (userEditText.text.toString().isNotEmpty() && passwordEditText.text.toString().isNotEmpty()) {
            auth.signInWithEmailAndPassword(
                userEditText.text.toString(),
                passwordEditText.text.toString()
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
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Timber.w("signInWithEmail:failure exception: ${task.exception}")
                        Toast.makeText(
                            baseContext, "Authentication login failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                        updateUI(null)
                    }

                    // ...
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

        if (userEditText.text.toString().isNotEmpty() && passwordEditText.text.toString().isNotEmpty()) {

            auth.createUserWithEmailAndPassword(
                userEditText.text.toString(),
                passwordEditText.text.toString()
            )
                .addOnCompleteListener(this) { task ->
                    progress.visibility = View.GONE

                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Timber.d("createUserWithEmail:success")
                        val user = auth.currentUser
                        Toast.makeText(
                            baseContext, "New user registered! Welcome: ${user?.email}",
                            Toast.LENGTH_SHORT
                        ).show()
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Timber.w("createUserWithEmail:failure exception: ${task.exception}")
                        Toast.makeText(
                            baseContext, "Authentication register failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                        updateUI(null)
                    }

                    // ...
                }
        } else {
            progress.visibility = View.GONE
            Toast.makeText(
                baseContext, "Empty field",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
            finish()
        }
    }

//    private fun isUserValid(username: String, password: String): Boolean {
//        return true
//    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        //todo when is this current user reset to null???
        updateUI(currentUser)
    }
}
