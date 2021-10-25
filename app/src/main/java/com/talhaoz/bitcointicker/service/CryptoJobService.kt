package com.talhaoz.bitcointicker.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Looper

import com.talhaoz.bitcointicker.R
import com.talhaoz.bitcointicker.api.RetrofitClient
import com.talhaoz.bitcointicker.db.DBHelper

import com.talhaoz.bitcointicker.models.FavoriteCoinsModel
import com.talhaoz.bitcointicker.models.FavoriteCoinsModelItem
import com.talhaoz.bitcointicker.ui.LoginActivity
import com.talhaoz.bitcointicker.viewmodel.CryptoViewModel
import com.talhaoz.martianstalker.util.Constants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import java.lang.Runnable

class CryptoJobService : JobService() {

    private lateinit var job : Job
    lateinit var handler : Handler
    var compositeDisposable = CompositeDisposable()
    var coinsList = ArrayList<FavoriteCoinsModelItem>()
    var oldValuesList = ArrayList<FavoriteCoinsModelItem>()
    lateinit var db : DBHelper
    lateinit var sharedPreferences: SharedPreferences

    lateinit var notificationManager : NotificationManager
    lateinit var notificationChannel : NotificationChannel
    lateinit var builder : Notification.Builder
    private val channelId = "com.talhaoz.bitcointicker.notification"
    private val description = "."

    override fun onStopJob(params: JobParameters?): Boolean {
        job.cancel()
        //compositeDisposable.dispose()
        //handler.removeCallbacksAndMessages(null)
        println("job stopped")
        return true
    }

    override fun onStartJob(params: JobParameters?): Boolean
    {
        val retrofitRes = RetrofitClient.loadData()
        db = DBHelper(this)
        sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        val currStr=sharedPreferences.getString("currency_str","usd")
        job = CoroutineScope(Dispatchers.Default).launch {

            /*handler = Handler(Looper.getMainLooper())
            handler.postDelayed(object : Runnable {
                override fun run() { */
                    // call for api
                    compositeDisposable.add(
                        retrofitRes.getFavoriteCoinsDetails(
                            vs_currency = if(currStr!=null) currStr else "usd",
                            ids = db.readFavoriteCoins()
                        )
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(object : DisposableSingleObserver<FavoriteCoinsModel>(){
                                override fun onSuccess( it : FavoriteCoinsModel) {
                                    coinsList.clear()
                                    coinsList.addAll(it)

                                    oldValuesList=db.readFavoriteCoinsValues()

                                    if(!oldValuesList.isEmpty()) {
                                        //val diff = coinsList.filterNot { coinsList -> oldValuesList.any { coinsList.current_price==it.current_price} }
                                        for (i in 0 until coinsList.size) {
                                            if(!coinsList.get(i).current_price.equals(oldValuesList.get(i).current_price))
                                                pushNotification(coinsList.get(i).name + " için değerler değişti!", i)
                                        }
                                    }

                                    db.insertFavoriteCoinsValues(coinsList)


                                }

                                override fun onError(e: Throwable) {

                                    println("ERROR while fetching data!")
                                    e.printStackTrace()

                                }

                            })
                    )
                    jobFinished(params,true)
                    /*handler.postDelayed(this, (30 *1000).toLong())
                }
            }, 0) */
        }
        return true
    }

    private fun pushNotification(contentText: String, id : Int)
    {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(this, LoginActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_UPDATE_CURRENT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId,description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this,channelId)
                .setContentTitle("Bitcoin Ticker")
                .setAutoCancel(true)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_coins)
                .setContentIntent(pendingIntent)
        }else{

            builder = Notification.Builder(this)
                .setContentTitle("Bitcoin Ticker")
                .setAutoCancel(true)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_coins)
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(id,builder.build())

    }


}