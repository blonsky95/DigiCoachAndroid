package com.tatoe.mydigicoach.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.tatoe.mydigicoach.R
import com.tatoe.mydigicoach.Utils
import com.tatoe.mydigicoach.entity.Day
import com.tatoe.mydigicoach.entity.Exercise
import com.tatoe.mydigicoach.entity.Friend
import com.tatoe.mydigicoach.network.*
import com.tatoe.mydigicoach.ui.calendar.MonthViewerFragment
import com.tatoe.mydigicoach.ui.exercise.ExerciseViewerFragment
import com.tatoe.mydigicoach.ui.fragments.FriendsDisplayerFragment
import com.tatoe.mydigicoach.ui.fragments.PackageReceivedFragment
import com.tatoe.mydigicoach.ui.fragments.ShareToFriendsFragment
import com.tatoe.mydigicoach.viewmodels.MainViewModel
import com.tatoe.mydigicoach.viewmodels.MyMainViewModelFactory
import kotlinx.android.synthetic.main.activity_parent_of_fragments.*

class HomeActivity : AppCompatActivity(), ShareToFriendsFragment.OnFriendSelectedListenerInterface,
    PackageReceivedFragment.OnPackageReceivedInterface {

    lateinit var mainViewModel: MainViewModel

    private var allFriends = listOf<Friend>()
    private var allExercises = listOf<Exercise>()
    private var allDays = listOf<Day>()

    private var toSendExes: List<Exercise>? = null
    private var toSendDays: List<Day>? = null

    private var packagesReceived: List<TransferPackage>? = null

    private var exercisePackagesReceived: ArrayList<ExercisePackage>? = null
    private var dayPackagesReceived: ArrayList<DayPackage>? = null
    private var friendPackagesReceived: ArrayList<FriendRequestPackage>? = null

//    private var overwriteExerciseDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parent_of_fragments)

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
        mainViewModel.displayFragmentById.observe(this, Observer {
//            if (it == MainViewModel.REMOVE_VERTICAL_FRAGMENT) {
//                supportFragmentManager.popBackStack()
//            }
            if (it != MainViewModel.NO_FRAGMENT) {
                displayFragment(it)
            }
        })

        mainViewModel.allFriends.observe(this, Observer { it ->
            allFriends = it
        })
        mainViewModel.allExercises.observe(this, Observer { it ->
            allExercises = it
        })
        mainViewModel.allDays.observe(this, Observer { it ->
            allDays = it
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

        mainViewModel.adapterTransferPackages.observe(this, Observer {
            //if fragment not there then just returns null and doesnt do shit
            (supportFragmentManager.findFragmentById(R.id.half_fragment_container) as PackageReceivedFragment?)?.updateAdapterContent(
                it
            )
        })

        mainViewModel.displayPackageReceiverFragmentType.observe(this, Observer {
            if (it == MainViewModel.REMOVE_VERTICAL_FRAGMENT) {
                mainViewModel.displayFragmentById.postValue(MainViewModel.REMOVE_VERTICAL_FRAGMENT)
            }
            if (it != PackageReceivedFragment.TRANSFER_PACKAGE_NOT_VALUE) {
                packagesReceived = when (it) {
                    PackageReceivedFragment.TRANSFER_PACKAGE_EXERCISE -> exercisePackagesReceived
                    PackageReceivedFragment.TRANSFER_PACKAGE_DAY -> dayPackagesReceived
                    PackageReceivedFragment.TRANSFER_PACKAGE_FRIEND -> friendPackagesReceived
                    else -> listOf()
                }
                mainViewModel.displayFragmentById.postValue(MainViewModel.PACKAGE_DISPLAYER)
            }
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
//            else {
//                Toast.makeText(this,"No exercises selected", Toast.LENGTH_SHORT).show()
//            }
        })

        mainViewModel.dialogBoxBundle.observe(this, Observer {
            if (it != null) {
                Utils.getInfoDialogView(this, it.mTitle, it.mText, it.mPositiveNegativeInterface)
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
//        if (supportFragmentManager.fragments.size > 2) {
//            supportFragmentManager.popBackStack()
//        }
        val verticalFragment = supportFragmentManager.findFragmentById(R.id.half_fragment_container)
        if (verticalFragment != null) {
            supportFragmentManager.popBackStack()
            return
        }

        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_in_up,R.anim.slide_in_down,R.anim.slide_in_up,R.anim.slide_in_down)
        //todo add animation of swiping up/down
        when (fragmentId) {
            MainViewModel.FRIEND_SHARER -> {
                transaction.replace(
                    R.id.half_fragment_container,
                    ShareToFriendsFragment.newInstance(allFriends)
                )
                transaction.addToBackStack("friends_sharer")
            }

            MainViewModel.PACKAGE_DISPLAYER -> {
                transaction.replace(
                    R.id.half_fragment_container,
                    PackageReceivedFragment.newInstance(packagesReceived!!)
                )
                transaction.addToBackStack("package_displayer")
            }

            MainViewModel.FRIEND_DISPLAYER -> {
                transaction.replace(
                    R.id.half_fragment_container,
                    FriendsDisplayerFragment()
                )
                transaction.addToBackStack("friends_displayer")
            }
        }

        transaction.commit()

    }

    private fun navigateToFragment(fragment: Fragment) {
        val verticalFragment = supportFragmentManager.findFragmentById(R.id.half_fragment_container)
        if (verticalFragment != null) {
            supportFragmentManager.popBackStack()
        }

        val transaction = supportFragmentManager.beginTransaction()
        //todo add animation of swiping left/right

        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()

    }

    override fun onFriendSelected(friend: Friend) {

        if (toSendExes != null) {
            mainViewModel.sendExercisesToFriend(toSendExes!!, friend)
            toSendExes= listOf()
        }
        if (toSendDays != null) {
            mainViewModel.sendDaysToFriend(toSendDays!!, friend)
            toSendDays= listOf()
        }

        Toast.makeText(this, "Sent to ${friend.username}", Toast.LENGTH_SHORT).show()
    }

    override fun onCancelSelected() {
        super.onBackPressed()
    }

    override fun onPackageAccepted(transferPackage: TransferPackage, packageType: Int) {

        when (packageType) {
            PackageReceivedFragment.TRANSFER_PACKAGE_EXERCISE -> {
                val exercisePackage = transferPackage as ExercisePackage
                if (mainViewModel.exerciseAlreadyInDb(exercisePackage, allExercises)) {
                    mainViewModel.promptOverwriteExerciseDialog(exercisePackage, allExercises)
                } else {
                    mainViewModel.insertExercise(exercisePackage.firestoreExercise!!.toExercise())
                    mainViewModel.updateExerciseAdapterContentAfterAction(exercisePackage)
                    mainViewModel.updateTransferPackage(transferPackage, TransferPackage.STATE_SAVED)
                }
            }

            PackageReceivedFragment.TRANSFER_PACKAGE_DAY -> {
                val dayPackage = transferPackage as DayPackage
                //following methods updates adapter and packages livedata
                mainViewModel.attemptImportDay(
                    dayPackage,
                    allExercises,
                    allDays
                )
            }
            PackageReceivedFragment.TRANSFER_PACKAGE_FRIEND -> {
                val friendRequestPackage = transferPackage as FriendRequestPackage
                //following methods updates adapter and packages livedata
                mainViewModel.attemptAddFriend(
                    friendRequestPackage, allFriends
                )
            }
        }
    }

    override fun onPackageRejected(transferPackage: TransferPackage, packageType: Int) {
        when (packageType) {
            PackageReceivedFragment.TRANSFER_PACKAGE_EXERCISE -> {
                mainViewModel.updateExerciseAdapterContentAfterAction(transferPackage)
                mainViewModel.updateTransferPackage(transferPackage, TransferPackage.STATE_REJECTED)
            }
            PackageReceivedFragment.TRANSFER_PACKAGE_DAY -> {
                mainViewModel.updateDayAdapterContentAfterAction(transferPackage)
                mainViewModel.updateTransferPackage(transferPackage, TransferPackage.STATE_REJECTED)
            }
            PackageReceivedFragment.TRANSFER_PACKAGE_FRIEND -> {
                mainViewModel.updateFriendAdapterContentAfterAction(transferPackage)
                val friendRequestPackage = transferPackage as FriendRequestPackage
                mainViewModel.rejectFriendRequest(friendRequestPackage)
            }
        }
    }

    override fun onBottomCancelSelected() {
        super.onBackPressed()
    }
}