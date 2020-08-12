package com.tatoe.mydigicoach.ui

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.Utils
import com.tatoe.mydigicoach.ui.fragments.PackageReceivedFragment
import com.tatoe.mydigicoach.viewmodels.*
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel
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
        mainViewModel =
            ViewModelProviders.of(activity!!, MyMainViewModelFactory(activity!!.application))
                .get(MainViewModel::class.java)

        dialog = Utils.setProgressDialog(activity!!, "Talking with cloud...")

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        backup_button.setOnClickListener {
            mainViewModel.displayFragmentById.postValue(MainViewModel.BACKUP_FRAGMENT)
        }

        friends_requests_button.setOnClickListener {
            mainViewModel.displayPackageReceiverFragmentType.postValue(PackageReceivedFragment.TRANSFER_PACKAGE_FRIEND)
        }

        friends_display_btn.setOnClickListener {
            mainViewModel.displayFragmentById.postValue(MainViewModel.FRIEND_DISPLAYER)
        }

        log_out_btn.setOnClickListener {
            logOut()
        }

        initObservers()
    }

    private fun logOut() {
        profileViewModel.closeDbInstance()
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(activity, UserAccess::class.java)
        startActivity(intent)
        activity!!.finish()
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

        mainViewModel.receivedFriendRequestsPackages.observe(this, Observer { exePackages ->
            updateSocialButtonNumber(exePackages.size)
        })

//        profileViewModel.receivedRequestsNumber.observe(this, Observer {
//            updateFriendRequestsNum(it)
//        })
    }

    private fun updateSocialButtonNumber(numberRequests: Int) {
        if (numberRequests>0) {
            friend_requests_number_f.visibility= View.VISIBLE
            friend_requests_number_f.text=numberRequests.toString()
        } else {
            friend_requests_number_f.visibility= View.GONE
        }
    }
}