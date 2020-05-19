package com.tatoe.mydigicoach.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.viewmodels.MyProfileViewModelFactory
import com.tatoe.mydigicoach.viewmodels.ProfileViewModel
import kotlinx.android.synthetic.main.activity_profile.*

class Profile : AppCompatActivity() {

    private lateinit var profileViewModel:ProfileViewModel
    private var db = FirebaseFirestore.getInstance()

//    private lateinit var emailTextView: TextView
//    private lateinit var usernameTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

//        emailTextView=email_value
//        usernameTextView=username_value

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)

        home_button.setOnClickListener {
            startActivity(Intent(this, HomeScreen::class.java))
            finish()
        }

        backup_button.setOnClickListener{
            //do the fragment or whatever
        }

        profileViewModel = ViewModelProviders.of(
            this,
            MyProfileViewModelFactory(db)
        ).get(
            ProfileViewModel::class.java
        )
        initObservers()
    }

    private fun initObservers() {
        profileViewModel.userEmail.observe(this, Observer {
            email_value.text=it
        })
        profileViewModel.userName.observe(this, Observer {
            username_value.text=it
        })
    }
}