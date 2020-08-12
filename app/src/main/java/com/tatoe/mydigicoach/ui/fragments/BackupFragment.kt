package com.tatoe.mydigicoach.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.firestore.FirebaseFirestore
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.viewmodels.MyBackupFragmentViewModelFactory
import com.tatoe.mydigicoach.viewmodels.BackupFragmentViewModel
import kotlinx.android.synthetic.main.fragment_backup_screen.*

class BackupFragment : Fragment() {

    private lateinit var cloudActionInterface: HandleCloudActionsInterface
    private lateinit var backupFragmentViewModel: BackupFragmentViewModel
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
//        use the interface implemented in the parent activity when the buttons selected!!!!!!!!
        if (context is HandleCloudActionsInterface) {
            cloudActionInterface = context
        }

        backupFragmentViewModel = ViewModelProviders.of(
            this,
            MyBackupFragmentViewModelFactory(db, activity!!.application)
        ).get(
            BackupFragmentViewModel::
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
        backupFragmentViewModel.lastUploadTime.observe(this, Observer {
            updateBackUpTimeText(it)
        })
    }

    private fun updateBackUpTimeText(text: String) {
        val string = "Last backup: $text"
        last_backup_time.text = string
    }

    interface HandleCloudActionsInterface {
        fun uploadToCloud() {
        }

        fun downloadFromCloud() {
        }
    }


}