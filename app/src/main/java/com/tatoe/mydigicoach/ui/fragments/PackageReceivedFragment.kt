package com.tatoe.mydigicoach.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Friend
import com.tatoe.mydigicoach.network.TransferPackage
import kotlinx.android.synthetic.main.fragment_friends_screen.view.*
import kotlinx.android.synthetic.main.fragment_share_to_friends_screen.*
import java.lang.ClassCastException

class PackageReceivedFragment: Fragment() {

    var packageReceiverInterface :OnPackageReceivedInterface? = null
    var transferPackages = listOf<TransferPackage>()

    companion object {

        const val BUNDLE_RECEIVED_PACKAGES_KEY = "received_packages_object"

        fun newInstance(
            receivedPackages: List<TransferPackage>
        ): PackageReceivedFragment {
            val packageReceivedFragment = PackageReceivedFragment()

            packageReceivedFragment.arguments = Bundle().apply {
                putString(BUNDLE_RECEIVED_PACKAGES_KEY, Gson().toJson(receivedPackages))
            }

            return packageReceivedFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_share_to_friends_screen, container, false)
        //todo change this to my other layout
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        packageReceiverInterface = context as? OnPackageReceivedInterface
        if (packageReceiverInterface == null) {
            throw ClassCastException("$context must implement OnFriendSelectedListenerInterface")
        }

        arguments?.getString(BUNDLE_RECEIVED_PACKAGES_KEY)?.let {
            transferPackages = Gson().fromJson(it, object : TypeToken<List<TransferPackage>>() {}.type)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //todo do all the UI stuff

    }

    //todo create the adapter - use the sharetofriends adapter as reference


    interface OnPackageReceivedInterface {
        fun onPackageAccepted(transferPackage: TransferPackage)
        fun onPackageRejected(transferPackage: TransferPackage)
    }
}