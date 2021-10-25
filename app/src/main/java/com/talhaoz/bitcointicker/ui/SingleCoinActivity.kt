package com.talhaoz.bitcointicker.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.provider.Settings
import android.provider.Settings.*
import android.provider.Settings.Secure.ANDROID_ID
import android.text.Editable
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.talhaoz.bitcointicker.R
import com.talhaoz.bitcointicker.db.DBHelper
import com.talhaoz.bitcointicker.viewmodel.CryptoViewModel
import com.talhaoz.martianstalker.util.Constants.Companion.REFRESH_TIME
import io.reactivex.plugins.RxJavaPlugins
import kotlinx.android.synthetic.main.single_coin_activity.*
import java.text.DecimalFormat

class SingleCoinActivity: AppCompatActivity()
{
    private lateinit var viewModel: CryptoViewModel

    lateinit var handler : Handler
    lateinit var coinId : String
    val fireStoreDB = Firebase.firestore
    var favoriteCoins = hashMapOf<String,String>()
    private var isFirstStart=true

    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor : SharedPreferences.Editor

    private var mAuth = FirebaseAuth.getInstance()
    val currentUser = mAuth.currentUser

    lateinit var db : DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.single_coin_activity)


        db = DBHelper(this)
        sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        editor =  sharedPreferences.edit()


        // to make description scrollable
        description.movementMethod= ScrollingMovementMethod()

        var coinId = intent.getStringExtra("coinId")


        getFireBaseData(coinId)

        viewModel = ViewModelProvider(this).get(CryptoViewModel::class.java)


        handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {
                println("new data Arriveed  Single Coin!!!!!!!!!!!!!!!")
                viewModel.getData(coinId) // call for api
                handler.postDelayed(this, (REFRESH_TIME*1000).toLong())//1 sec delay
            }
        }, 0)

        observeData()

        refreshInterval.setText(REFRESH_TIME.toString())

        refreshInterval.doAfterTextChanged { it: Editable? ->

            var t =it.toString().toIntOrNull()
            if(t!=null) {

                REFRESH_TIME = t

                Toast.makeText(this,"Refresh interval set as $t sec!",Toast.LENGTH_SHORT).show()
            }
        }

        addFavButton.setOnClickListener( View.OnClickListener {

            if(favoriteCoins.containsKey(coinId)) {
                removeCoin(coinId)
                addFavButton.setImageResource(R.drawable.ic_star)
                Toast.makeText(this,"Coin removed from favorites!",Toast.LENGTH_SHORT).show()
            }
            else
            {
                addToCloudFireStore(coinId)
                addFavButton.setImageResource(R.drawable.ic_star_full)
                Toast.makeText(this,"Coin added to favorites!",Toast.LENGTH_SHORT).show()
            }
        })


    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
    }

    private fun removeCoin(coinId: String?)
    {
        if(currentUser==null)
            return

        val docRef = fireStoreDB.collection("bitcoinTicker").document(currentUser.uid)

        var updates : HashMap<String,Any>
        if(coinId!=null) {// Remove the 'capital' field from the document
            updates = hashMapOf(
                coinId to FieldValue.delete()
            )
            docRef.update(updates).addOnCompleteListener { }
        }

        getFireBaseData(coinId)

    }

    private fun getFireBaseData(coinIdP : String?) {

        if(currentUser==null)
            return

        fireStoreDB.collection("bitcoinTicker").document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->

                if (document != null && document.data!=null) {
                    favoriteCoins.clear()
                    favoriteCoins.putAll(document.data as HashMap<String, String>)

                    var list = favoriteCoins.keys.toList() as ArrayList<String>
                    db.insertFavoriteCoins(list)

                    if(favoriteCoins.containsKey(coinIdP))
                        addFavButton.setImageResource(R.drawable.ic_star_full)
                }
                else
                    println("ERROR Document is nullllllll! ")

            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents.", exception)
            }
    }


    private fun addToCloudFireStore(coinId : String?) {

        if(currentUser==null)
            return

        if(coinId!=null)
        {
            favoriteCoins.put(coinId, coinId)

            val docRef = fireStoreDB.collection("bitcoinTicker").document(currentUser.uid)
            docRef.set(favoriteCoins)
                .addOnSuccessListener { documentReference ->
                    Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference}")
                }
                .addOnFailureListener { e ->
                    Log.w("TAG", "Error adding document", e)
                }

            getFireBaseData(coinId)
        }


        //fs.collection("coinIds").document(androidId).set("firstname", user)


    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)

    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    private fun observeData() {
        viewModel.singleCoin.observe(this, Observer { data ->
            data?.let {

                val currencyStr=sharedPreferences.getString("currency_str","usd")
                val df = DecimalFormat("#.##")

                when(currencyStr)
                {
                    "usd" -> {
                        coinPrice.text =it.market_data.current_price.usd_curr + " USD"
                        val str = it.market_data.price_change_percentage_1h_in_currency.usd_curr
                        if(str!=null)
                            oneHourChange.text=df.format(str.toDouble())+ " %"
                    }
                    "try" -> {
                        coinPrice.text =it.market_data.current_price.try_curr + " TRY"
                        val str = it.market_data.price_change_percentage_1h_in_currency.try_curr
                        if(str!=null)
                            oneHourChange.text=df.format(str.toDouble())+ " %"
                    }
                    "eur" -> {
                        coinPrice.text =it.market_data.current_price.eur_curr + " EUR"
                        val str = it.market_data.price_change_percentage_1h_in_currency.eur_curr
                        if(str!=null)
                            oneHourChange.text=df.format(str.toDouble()) + " %"
                    }
                    else -> {
                            coinPrice.text =it.market_data.current_price.gbp_curr + " TRY"
                            val str = it.market_data.price_change_percentage_1h_in_currency.gbp_curr
                            if(str!=null)
                                oneHourChange.text=df.format(str.toDouble()) + " %"
                    }
                }

                coinId=it.id
                Picasso.get().load(it.image.large).into(coinImage)


                coinName.text= "${it.name} (${it.symbol.toUpperCase()})"

                hashingAlgorithm.text=it.hashing_algorithm
                description.text=it.description.en
                val str1h = it.market_data.price_change_percentage_24h
                if(str1h!=null)
                    oneDayChange.text=df.format(str1h.toDouble()) + " %"
                val str1w = it.market_data.price_change_percentage_7d
                if(str1w!=null)
                oneWeekChange.text=df.format(str1w.toDouble()) + " %"

            }})
    }


}