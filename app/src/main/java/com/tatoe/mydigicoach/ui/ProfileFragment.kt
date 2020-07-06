package com.tatoe.mydigicoach.ui

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.HandleCloudActionsInterface
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.Utils
import com.tatoe.mydigicoach.ui.fragments.BackupFragment
import com.tatoe.mydigicoach.ui.fragments.FriendsFragment
import com.tatoe.mydigicoach.viewmodels.BackupFragmentViewModel
import com.tatoe.mydigicoach.viewmodels.MyBackupFragmentViewModelFactory
import com.tatoe.mydigicoach.viewmodels.MyProfileViewModelFactory
import com.tatoe.mydigicoach.viewmodels.ProfileViewModel
import kotlinx.android.synthetic.main.activity_month_viewer.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.backup_button
import kotlinx.android.synthetic.main.activity_profile.friends_button
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private var db = FirebaseFirestore.getInstance()

    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        profileViewModel = ViewModelProviders.of(
            this,
            MyProfileViewModelFactory(db, activity!!.application)
        ).get(
            ProfileViewModel::
            class.java
        )

        dialog = Utils.setProgressDialog(activity!!, "Talking with cloud...")

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        backup_button.setOnClickListener {
//            setUpFragment(backupFragment)
        }

        friends_button.setOnClickListener {
//            setUpFragment(friendsFragment)
            //todo sort this one out
        }

        initObservers()
    }

    private fun initObservers() {
        profileViewModel.userEmail.observe(this, Observer {
            email_value_f.text = it
        })
        profileViewModel.userName.observe(this, Observer {
            username_value_f.text = it
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
            friend_requests_number_f.visibility= View.VISIBLE
            friend_requests_number_f.text=numberRequests.toString()
        } else {
            friend_requests_number_f.visibility= View.GONE
        }
    }
}