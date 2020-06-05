package com.tatoe.mydigicoach.ui

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.DialogPositiveNegativeHandler
import com.tatoe.mydigicoach.HandleCloudActionsInterface
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.Utils
import com.tatoe.mydigicoach.ui.fragments.BackupFragment
import com.tatoe.mydigicoach.ui.fragments.FriendsFragment
import com.tatoe.mydigicoach.viewmodels.MyProfileViewModelFactory
import com.tatoe.mydigicoach.viewmodels.ProfileViewModel
import kotlinx.android.synthetic.main.activity_profile.*

class Profile : AppCompatActivity(), HandleCloudActionsInterface {

    private lateinit var profileViewModel: ProfileViewModel
    private var db = FirebaseFirestore.getInstance()
    private lateinit var fragmentManager: FragmentManager
    private lateinit var backupFragment: Fragment
    private lateinit var friendsFragment: Fragment
    private lateinit var fragmentTransaction: FragmentTransaction
    private var isFragmentOpen = false

    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)

        home_button.setOnClickListener {
            startActivity(Intent(this, HomeScreen::class.java))
            finish()
        }

        fragmentManager = supportFragmentManager
        backupFragment = BackupFragment()
        friendsFragment = FriendsFragment()

        backup_button.setOnClickListener {
            setUpFragment(backupFragment)
        }

        friends_button.setOnClickListener {
            setUpFragment(friendsFragment)
        }

        profileViewModel = ViewModelProviders.of(
            this,
            MyProfileViewModelFactory(db, application)
        ).get(
            ProfileViewModel::
            class.java
        )

        dialog = Utils.setProgressDialog(this, "Talking with cloud...")

        initObservers()
    }

    private fun setUpFragment(fragment: Fragment) {
        if (!isFragmentOpen) {

            fragmentTransaction = fragmentManager.beginTransaction()

            fragmentTransaction.setCustomAnimations(
                R.anim.slide_in_up,
                R.anim.slide_in_down,
                R.anim.slide_out_down,
                R.anim.slide_out_up
            )

            fragmentTransaction.addToBackStack("A").replace(R.id.frame_layout, fragment)
            fragmentTransaction.commit()

            isFragmentOpen = true
        } else {
            fragmentManager.popBackStack()
            isFragmentOpen = false
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (isFragmentOpen) {
            isFragmentOpen=false
        }
    }

    private fun initObservers() {
        profileViewModel.userEmail.observe(this, Observer {
            email_value.text = it
        })
        profileViewModel.userName.observe(this, Observer {
            username_value.text = it
        })

        profileViewModel.getIsLoading().observe(this, Observer { isLoading ->
            if (isLoading) {
                dialog.show()

            } else {
                dialog.hide()
            }
        })

        profileViewModel.receivedRequestsNumber.observe(this, Observer {
            updateFriendRequestsNum(it)
        })
    }

    private fun updateFriendRequestsNum(numberRequests: Int) {
        if (numberRequests>0) {
            friend_requests_number.visibility= View.VISIBLE
            friend_requests_number.text=numberRequests.toString()
        } else {
            friend_requests_number.visibility= View.GONE
        }
    }

    override fun uploadToCloud() {
        Utils.getInfoDialogView(
            this,
            dialogText = "Upload exercises and days to the cloud?",
            dialogPositiveNegativeHandler = object : DialogPositiveNegativeHandler {
                override fun onPositiveButton(inputText: String) {
                    super.onPositiveButton(inputText)
                    profileViewModel.uploadBackup()
                }
            })
    }

    override fun downloadFromCloud() {
        Utils.getInfoDialogView(
            this,
            dialogText = "Downloading backup will replace your current data, are you sure?",
            dialogPositiveNegativeHandler = object : DialogPositiveNegativeHandler {
                override fun onPositiveButton(inputText: String) {
                    super.onPositiveButton(inputText)
                    profileViewModel.downloadBackup()
                }
            })
    }


}