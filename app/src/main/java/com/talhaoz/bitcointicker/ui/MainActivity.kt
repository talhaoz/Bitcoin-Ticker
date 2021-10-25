package com.talhaoz.bitcointicker.ui


import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.talhaoz.bitcointicker.R
import com.talhaoz.bitcointicker.db.DBHelper
import com.talhaoz.bitcointicker.service.CryptoJobService
import com.talhaoz.bitcointicker.ui.fragments.FavoritesFragment
import com.talhaoz.bitcointicker.ui.fragments.SearchFragment
import com.talhaoz.bitcointicker.ui.fragments.SettingsFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var fragment: Fragment

    private val job_id=12

    lateinit var jobScheduler : JobScheduler
    lateinit var db : DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.cancel(job_id)


        // set search nav for start

        val sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(baseContext)
        val isFirstStart = sharedPreferences.getBoolean("isFirstStart",true)


        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId){

                R.id.searchNav -> { fragment =
                    SearchFragment()
                }

                R.id.favoritesNav -> { fragment =
                    FavoritesFragment()
                }

                else -> { fragment =
                    SettingsFragment()
                }
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container,fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()

            true
        }

        bottom_navigation.selectedItemId=R.id.searchNav
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        val componentName = ComponentName(this,CryptoJobService::class.java)
        val jobInfo = JobInfo.Builder(job_id,componentName)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setPersisted(true)
            .setMinimumLatency(5000)
            .setOverrideDeadline(30*1000)
            //.setPeriodic(30 *1000)
            .build()

        println("------------------- job scheduled")
        jobScheduler.schedule(jobInfo)


    }

    override fun onResume() {
        super.onResume()
        println("job canceled")
        jobScheduler.cancelAll()
        db = DBHelper(this)
        db.deleteFavoriteCoinValues()
        //val i = Intent(this, CryptoJobService::class.java)
        //stopService(i)
    }

    override fun onDestroy() {
        super.onDestroy()
        jobScheduler.cancelAll()



    }


}