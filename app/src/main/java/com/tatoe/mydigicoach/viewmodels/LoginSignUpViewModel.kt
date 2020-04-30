package com.tatoe.mydigicoach.viewmodels

import android.app.Activity
import android.app.Application
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.AppRepository
import com.tatoe.mydigicoach.database.AppRoomDatabase
import com.tatoe.mydigicoach.entity.Day
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.HashMap

class LoginSignUpViewModel(var application: Application, var db: FirebaseFirestore) : ViewModel() {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    val dayToday: LiveData<Day>
    val hasAccess = MutableLiveData<Boolean>(false)
    val isDoingBackgroundTask = MutableLiveData<Boolean>(false)
    private val repository: AppRepository

    init {
        val appDB = AppRoomDatabase.getInstance(application)
        val exerciseDao = appDB.exercisesDao()
        val blockDao = appDB.blockDao()
        val dayDao = appDB.dayDao()

        repository =
            AppRepository(exerciseDao, blockDao, dayDao)

        dayToday = repository.dayToday
        if (auth.currentUser!=null){
            hasAccess.value=true
        }
    }

    fun resetFirebaseUserPassword(userEmail: String, context:Context) {
            auth.sendPasswordResetEmail(userEmail)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Timber.d("Email sent.")
                        Toast.makeText(context,"Email sent to $userEmail", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    fun logInUser(username: String, password: String, activity: Activity) {
        isDoingBackgroundTask.value=true
        val docRef = db.collection("users").whereEqualTo("username", username)
        docRef.get().addOnSuccessListener { docs ->
            if (docs.isEmpty) {
                isDoingBackgroundTask.value=false

                Timber.d("signInWithEmail:failure: unexistent")
                Toast.makeText(
                    activity, " User doesn't exist",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                var doc = docs.documents[0]
                val userEmail = doc["email"].toString()

                auth.signInWithEmailAndPassword(
                    userEmail,
                    password
                )
                    .addOnCompleteListener(activity) { task ->
                        isDoingBackgroundTask.value=false
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Timber.d("signInWithEmail:success")
                            val user = auth.currentUser
                            Toast.makeText(
                                activity, " Welcome back ${user?.email}",
                                Toast.LENGTH_SHORT
                            ).show()
                            hasAccess.value=true
                        } else {
                            // If sign in fails, display a message to the user.
                            isDoingBackgroundTask.value=false
                            Timber.w("signInWithEmail:failure exception: ${task.exception}")
                            Toast.makeText(
                                activity, "Authentication login failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                            hasAccess.value=false
                        }
                    }
            }
        }

            .addOnFailureListener { e ->
                isDoingBackgroundTask.value=false

                Timber.d("signInWithEmail:failure: $e")
                Toast.makeText(
                    activity, " Weird error",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    fun registerNewUser(username: String, email: String, password: String, activity : Activity) {
        isDoingBackgroundTask.value=true

        auth.createUserWithEmailAndPassword(
            email, password
        )
            .addOnCompleteListener(activity) { task ->

                if (task.isSuccessful) {
                    val userMap = HashMap<String, String>()
                    userMap["username"] = username
                    userMap["email"] = email
                    db.collection("users").document(auth.currentUser!!.uid).set(userMap)
                        .addOnSuccessListener {
                            isDoingBackgroundTask.value=false

                            Timber.d("createUserWithEmail:success")
                            Toast.makeText(
                                activity, "New user registered! Welcome: $username",
                                Toast.LENGTH_SHORT
                            ).show()
                            hasAccess.value=true

                        }
                        .addOnFailureListener { e ->
                            isDoingBackgroundTask.value=false

                            Timber.d("error registering: $e")
                            Toast.makeText(
                                activity, "Registration failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    Timber.w("createUserWithEmail:failure exception: ${task.exception}")
                    Toast.makeText(
                        activity, "Authentication register failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    hasAccess.value=false

                }
            }
    }
}