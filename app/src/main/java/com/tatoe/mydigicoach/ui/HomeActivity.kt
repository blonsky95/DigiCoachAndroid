package com.tatoe.mydigicoach.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.entity.Friend
import com.tatoe.mydigicoach.network.DayPackage
import com.tatoe.mydigicoach.network.ExercisePackage
import com.tatoe.mydigicoach.network.FriendRequestPackage
import com.tatoe.mydigicoach.network.TransferPackage
import com.tatoe.mydigicoach.ui.calendar.MonthViewerFragment
import com.tatoe.mydigicoach.ui.exercise.ExerciseViewerFragment
import com.tatoe.mydigicoach.ui.fragments.PackageReceivedFragment
import com.tatoe.mydigicoach.ui.fragments.ShareToFriendsFragment
import com.tatoe.mydigicoach.viewmodels.MainViewModel
import com.tatoe.mydigicoach.viewmodels.MyMainViewModelFactory
import kotlinx.android.synthetic.main.parent_of_fragments_activity.*

class HomeActivity : AppCompatActivity(), ShareToFriendsFragment.OnFriendSelectedListenerInterface,
    PackageReceivedFragment.OnPackageReceivedInterface {

    lateinit var mainViewModel: MainViewModel
    private var allFriends = listOf<Friend>()

    private var toSendExes: List<Exercise>? = null
    private var toSendDays: List<Day>? = null

    private var packagesReceived: List<TransferPackage>? = null

    private var exercisePackagesReceived: List<ExercisePackage>? = null
    private var dayPackagesReceived: List<DayPackage>? = null
    private var friendPackagesReceived: List<FriendRequestPackage>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.parent_of_fragments_activity)

        mainViewModel = ViewModelProviders.of(this, MyMainViewModelFactory(application))
            .get(MainViewModel::class.java)

        setUpBottomNav()
        bottom_navigation.selectedItemId = R.id.page_home

        initObservers()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.stopSnapshotListeners()
    }

    private fun initObservers() {
//        mainViewModel.displayFragmentById.observe(this, Observer {
//            if (it != MainViewModel.NO_FRAGMENT) {
//                displayFragment(it)
//            }
//        })
        mainViewModel.allFriends.observe(this, Observer { it ->
            allFriends = it
        })


        mainViewModel.receivedExercisesPackages.observe(this, Observer {
            exercisePackagesReceived = it
        })
        mainViewModel.receivedDaysPackages.observe(this, Observer {
            dayPackagesReceived = it
        })
        mainViewModel.receivedFriendRequestsPackages.observe(this, Observer {
            friendPackagesReceived = it
        })

        mainViewModel.displayFragmentTriggerAndType.observe(this, Observer {
            packagesReceived = when (it) {
                PackageReceivedFragment.TRANSFER_PACKAGE_EXERCISE -> exercisePackagesReceived
                PackageReceivedFragment.TRANSFER_PACKAGE_DAY -> dayPackagesReceived
                PackageReceivedFragment.TRANSFER_PACKAGE_FRIEND -> friendPackagesReceived
                else -> listOf()
            }
            displayFragment(MainViewModel.PACKAGE_DISPLAYER)
        })

        mainViewModel.daysToSend.observe(this, Observer { it ->
            if (it.isNotEmpty()) {
                toSendDays = it
                displayFragment(MainViewModel.FRIEND_SHARER)
            }
        })

        mainViewModel.exercisesToSend.observe(this, Observer { it ->
            if (it.isNotEmpty()) {
                toSendExes = it
                displayFragment(MainViewModel.FRIEND_SHARER)
            }
        })
    }

    private fun setUpBottomNav() {
        bottom_navigation.setOnNavigationItemSelectedListener {
            val i: Int
            when (it.itemId) {
                R.id.page_home -> {
                    navigateToFragment(HomeFragment())
                    true
                }
                R.id.page_exes -> {
                    navigateToFragment(ExerciseViewerFragment())
                    true
                }
                R.id.page_calendar -> {
                    navigateToFragment(MonthViewerFragment())
                    true
                }
                R.id.page_store -> {
                    navigateToFragment(LibraryFragment())
                    true
                }
                R.id.page_profile -> {
                    navigateToFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
        bottom_navigation.setOnNavigationItemReselectedListener {
            val i: Int
            when (it.itemId) {
                R.id.page_home -> {
                    navigateToFragment(HomeFragment())
                }
                R.id.page_exes -> {
                    navigateToFragment(ExerciseViewerFragment())
                }
                R.id.page_calendar -> {
                    navigateToFragment(MonthViewerFragment())
                }
                R.id.page_store -> {
                    navigateToFragment(LibraryFragment())
                }
                R.id.page_profile -> {
                    navigateToFragment(ProfileFragment())
                }
            }
        }
    }

    private fun displayFragment(fragmentId: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        //todo add animation of swiping up/down
        when (fragmentId) {
            MainViewModel.FRIEND_SHARER -> {
                transaction.replace(
                    R.id.half_fragment_container,
                    ShareToFriendsFragment.newInstance(allFriends)
                )
                transaction.addToBackStack("friends")
            }

            //change this for the package displayer fragment - create it - do ticket
            MainViewModel.PACKAGE_DISPLAYER -> {
                transaction.replace(
                    R.id.half_fragment_container,
                    PackageReceivedFragment.newInstance(packagesReceived!!)
                )
                transaction.addToBackStack("friends")
            }
        }

        transaction.commit()

    }

    private fun navigateToFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        //todo add animation of swiping left/right

        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)

        transaction.commit()
    }

    override fun onFriendSelected(friend: Friend) {
        //test if this works
        //then test receiving
        //add a stub friend
        if (toSendExes != null) {
            mainViewModel.sendExercisesToFriend(toSendExes!!, friend)
        }
        if (toSendDays != null) {
            mainViewModel.sendDaysToFriend(toSendDays!!, friend)
        }

    }

    override fun onCancelSelected() {
        super.onBackPressed()
    }

    override fun onPackageAccepted(transferPackage: TransferPackage, packageType: Int) {
        when (packageType) {
            PackageReceivedFragment.TRANSFER_PACKAGE_EXERCISE -> {
                mainViewModel.attemptImportExercise((transferPackage as ExercisePackage).firestoreExercise!!.toExercise())
            }
            PackageReceivedFragment.TRANSFER_PACKAGE_DAY -> {
                mainViewModel.insertDay((transferPackage as DayPackage).firestoreDay!!.toDay())
            }
            PackageReceivedFragment.TRANSFER_PACKAGE_FRIEND -> {
                val friendRequestPackage = transferPackage as FriendRequestPackage
                mainViewModel.insertFriend(
                    Friend(
                        friendRequestPackage.mSender!!,
                        friendRequestPackage.senderDocId
                    )
                )
            }
        }
    }

    override fun onPackageRejected(transferPackage: TransferPackage, packageType: Int) {
        //update the package
    }

    override fun onBottomCancelSelected() {
        super.onBackPressed()
    }
}