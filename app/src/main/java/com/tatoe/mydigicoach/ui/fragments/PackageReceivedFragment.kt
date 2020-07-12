package com.tatoe.mydigicoach.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.network.DayPackage
import com.tatoe.mydigicoach.network.ExercisePackage
import com.tatoe.mydigicoach.network.FriendRequestPackage
import com.tatoe.mydigicoach.network.TransferPackage
import kotlinx.android.synthetic.main.fragment_incoming_requests_screen.*
import kotlinx.android.synthetic.main.fragment_incoming_requests_screen.cancel_btn
import kotlinx.android.synthetic.main.item_holder_friends.view.friend_username_textview
import kotlinx.android.synthetic.main.item_holder_request_package.view.*

class PackageReceivedFragment: Fragment() {

    private lateinit var requestReceiverAdapter: MyCustomReceivedRequestsAdapter
    private lateinit var recyclerView: RecyclerView

    var packageReceiverInterface :OnPackageReceivedInterface? = null
    var transferPackages = listOf<TransferPackage>()

    var transferPackageType:Int? = null

    companion object {

        const val BUNDLE_RECEIVED_PACKAGES_KEY = "received_packages_object"

        const val TRANSFER_PACKAGE_NOT_VALUE = -1
        const val TRANSFER_PACKAGE_EXERCISE = 0
        const val TRANSFER_PACKAGE_DAY = 1
        const val TRANSFER_PACKAGE_FRIEND = 2

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
        return inflater.inflate(R.layout.fragment_incoming_requests_screen, container, false)
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
        if (transferPackages.isNotEmpty()) {
            if (transferPackages[0] is ExercisePackage) {
                transferPackageType=TRANSFER_PACKAGE_EXERCISE
                return
            }
            if (transferPackages[0] is DayPackage) {
                transferPackageType= TRANSFER_PACKAGE_DAY
                return
            }
            if (transferPackages[0] is FriendRequestPackage) {
                transferPackageType= TRANSFER_PACKAGE_FRIEND
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView=requests_displayer_recycler
        requestReceiverAdapter=MyCustomReceivedRequestsAdapter(activity!!)
        requestReceiverAdapter.setContent(transferPackages)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        cancel_btn.setOnClickListener {
            packageReceiverInterface?.onBottomCancelSelected()
        }
    }

    inner class MyCustomReceivedRequestsAdapter(context: Context) :
        RecyclerView.Adapter<MyReceivedPackageViewHolder>() {

        var transferPackages = listOf<TransferPackage>()

        private val inflater: LayoutInflater = LayoutInflater.from(context)


        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            super.onAttachedToRecyclerView(recyclerView)
            if (transferPackageType==null) {
                throw Error("Package is not readable, not an exercise day or friend")
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyReceivedPackageViewHolder {
            val itemView = inflater.inflate(R.layout.item_holder_request_package, parent, false)
            return MyReceivedPackageViewHolder(itemView)
        }

        override fun getItemCount(): Int {
            return transferPackages.size
        }

        override fun onBindViewHolder(holder: MyReceivedPackageViewHolder, position: Int) {
            holder.usernameTextView.text = transferPackages[position].mSender

            var packageTitle=""
            if (transferPackageType==TRANSFER_PACKAGE_EXERCISE){
                packageTitle=(transferPackages[position] as ExercisePackage).firestoreExercise!!.mName
            }
            if (transferPackageType== TRANSFER_PACKAGE_DAY){
                packageTitle=(transferPackages[position] as DayPackage).firestoreDay!!.mDayId
            }
            if (transferPackageType== TRANSFER_PACKAGE_FRIEND){
                holder.packageNameTextView.visibility=View.GONE
            } else {
                holder.packageNameTextView.text=packageTitle
            }

            holder.acceptImageView.setOnClickListener {
                packageReceiverInterface?.onPackageAccepted(transferPackages[position], transferPackageType!!)
            }
            holder.rejectImageView.setOnClickListener {
                packageReceiverInterface?.onPackageRejected(transferPackages[position], transferPackageType!!)
            }
        }

        fun setContent(mTransferPackages: List<TransferPackage>) {
            transferPackages=mTransferPackages
            notifyDataSetChanged()
        }

    }

    class MyReceivedPackageViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var usernameTextView = v.friend_username_textview
        var packageNameTextView=v.package_name_textview
        var acceptImageView=v.accept_image_view
        var rejectImageView=v.reject_image_view
    }


    interface OnPackageReceivedInterface {
        fun onPackageAccepted(transferPackage: TransferPackage, packageType:Int)
        fun onPackageRejected(transferPackage: TransferPackage, packageType:Int)
        fun onBottomCancelSelected()
    }
}