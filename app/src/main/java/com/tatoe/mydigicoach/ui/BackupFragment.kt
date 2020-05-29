package com.tatoe.mydigicoach.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.HandleCloudActionsInterface
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.viewmodels.MyProfileFragmentViewModelFactory
import com.tatoe.mydigicoach.viewmodels.MyProfileViewModelFactory
import com.tatoe.mydigicoach.viewmodels.ProfileFragmentViewModel
import com.tatoe.mydigicoach.viewmodels.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_backup_screen.*

class BackupFragment : Fragment() {

    private lateinit var cloudActionInterface: HandleCloudActionsInterface
    private lateinit var profileFragmentViewModel: ProfileFragmentViewModel
    private var db = FirebaseFirestore.getInstance()

    //So I have the profile view model here, meaning there was no need for me to implement the interface
    //however I'd rather have the fragment specify on UI logic, and the activity on other stuff.
    //Plus, I also use some interfaces.

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_backup_screen, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //so because the attaching activity implements the interface I can use its context directly to
//        initialise the interface im using when the buttons selected!!!!!!!!
        if (context is HandleCloudActionsInterface) {
            cloudActionInterface = context
        }

        profileFragmentViewModel = ViewModelProviders.of(
            this,
            MyProfileFragmentViewModelFactory(db, activity!!.application)
        ).get(
            ProfileFragmentViewModel::
            class.java
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        lastBackupTimeObserve()

        upload_cloud_btn.setOnClickListener {
            cloudActionInterface.uploadToCloud()
        }

        download_cloud_btn.setOnClickListener {
            cloudActionInterface.downloadFromCloud()
        }
    }

    private fun lastBackupTimeObserve() {
        profileFragmentViewModel.lastUploadTime.observe(this, Observer {
            updateBackUpTimeText(it)
        })
    }

    private fun updateBackUpTimeText(text: String) {
        val string = "Last backup: $text"
        last_backup_time.text = string
    }


}