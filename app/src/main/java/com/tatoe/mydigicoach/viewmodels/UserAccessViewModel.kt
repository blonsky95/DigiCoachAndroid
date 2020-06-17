package com.tatoe.mydigicoach.viewmodels

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.ui.util.DataHolder
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber
import java.util.HashMap

class UserAccessViewModel(
    var application: Application,
    var db: FirebaseFirestore,
    var prefs: SharedPreferences
) : ViewModel() {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    val hasAccess = MutableLiveData<Boolean>(false)
    val isDoingBackgroundTask = MutableLiveData<Boolean>(false)
    private val viewModelJob = SupervisorJob()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {
        if (auth.currentUser != null) {
            getLocalUserDataToDataholder()
        }
    }

    /**
     * Only call when firebase user is logged in.
     * Checks if the user has its username, email and doc id credentials stored
     * in shared preferences. If it doesn't it makes a firebase db query and fetches them
     * It then loads them to DataHolder object to make them accessible throughout app
     */
    private fun getLocalUserDataToDataholder() {
        isDoingBackgroundTask.value = true
        val userEmail = auth.currentUser!!.email
        if (prefs.getString("${userEmail}_username", "unknown") != "unknown") {
            DataHolder.userName = prefs.getString("${userEmail}_username", "uknown_username")!!
            DataHolder.userDocId = prefs.getString("${userEmail}_docid", "unknown_doc_id")!!
//            DataHolder.userEmail = prefs.getString("user_email", "uknown_email")!!
            isDoingBackgroundTask.value = false
            hasAccess.value = true
        } else {
            val docRef1 = db.collection("users").whereEqualTo("email", auth.currentUser!!.email)
            docRef1.get().addOnSuccessListener { docs ->
                if (!docs.isEmpty) {
                    putUserPreferences(
                        auth.currentUser!!.email!!, docs.documents[0]["username"].toString(),
                        docs.documents[0].id
                    )
                    getLocalUserDataToDataholder()
                }
            }.addOnFailureListener {
                isDoingBackgroundTask.value = false
                hasAccess.value = false
            }
        }
    }

    @SuppressLint("ApplySharedPref")
    fun putUserPreferences(email: String, username: String, userDocId: String) {
        val editor = prefs.edit()
        editor.putString("${email}_username", username)
        editor.putString("${email}_docid", userDocId)
//        editor.putString("user_email", userDocId)
        editor.commit()
    }

    fun resetFirebaseUserPassword(userEmail: String, context: Context) {
        auth.sendPasswordResetEmail(userEmail)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.d("Email sent.")
                    Toast.makeText(context, "Email sent to $userEmail", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun logInUser(username: String, password: String, activity: Activity) {
        isDoingBackgroundTask.value = true
        val docRef = db.collection("users").whereEqualTo("username", username)
        docRef.get().addOnSuccessListener { docs ->
            if (docs.isEmpty) {

                val toastMsg: String =
                    if (com.tatoe.mydigicoach.Utils.isConnectedToInternet(application)) {
                        "User doesn't exist"
                    } else {
                        "There is no Internet connection, connect to Internet to login"
                    }
                isDoingBackgroundTask.value = false

                Toast.makeText(
                    activity, toastMsg,
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                val doc = docs.documents[0]
                val userEmail = doc["email"].toString()

                auth.signInWithEmailAndPassword(
                    userEmail,
                    password
                )
                    .addOnCompleteListener(activity) { task ->
                        isDoingBackgroundTask.value = false
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Timber.d("signInWithEmail:success")
                            val user = auth.currentUser
                            Toast.makeText(
                                activity, " Welcome back ${user?.email}",
                                Toast.LENGTH_SHORT
                            ).show()
                            getLocalUserDataToDataholder()
                        } else {
                            // If sign in fails, display a message to the user.
//                            isDoingBackgroundTask.value = false
                            Timber.w("signInWithEmail:failure exception: ${task.exception}")
                            Toast.makeText(
                                activity, "Authentication login failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                            hasAccess.value = false
                        }
                    }
            }
        }

            .addOnFailureListener { e ->
                isDoingBackgroundTask.value = false

                Timber.d("signInWithEmail:failure: $e")
                Toast.makeText(
                    activity, "signInWithEmail:failure: $e",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    fun registerNewUser(username: String, email: String, password: String, activity: Activity) {
        isDoingBackgroundTask.value = true

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
                            isDoingBackgroundTask.value = false

                            Timber.d("createUserWithEmail:success")
                            Toast.makeText(
                                activity, "New user registered! Welcome: $username",
                                Toast.LENGTH_SHORT
                            ).show()
                            getLocalUserDataToDataholder()
//                            hasAccess.value = true

                        }
                        .addOnFailureListener { e ->
                            isDoingBackgroundTask.value = false

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
                    hasAccess.value = false

                }
            }
    }


}