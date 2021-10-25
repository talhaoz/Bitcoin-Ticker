package com.talhaoz.bitcointicker.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.talhaoz.bitcointicker.R
import com.talhaoz.bitcointicker.adapter.RecyclerViewAdapter
import com.talhaoz.bitcointicker.db.DBHelper
import com.talhaoz.bitcointicker.models.CryptoAllCoinsModelItem
import com.talhaoz.bitcointicker.viewmodel.CryptoViewModel
import kotlinx.android.synthetic.main.fragment_favorites.view.*


class FavoritesFragment : Fragment(R.layout.fragment_favorites){

    private lateinit var viewModel: CryptoViewModel
    lateinit var coinsAdapter: RecyclerViewAdapter

    lateinit var db : DBHelper
    lateinit var sharedPreferences: SharedPreferences
    var isFirstStart = true
    lateinit var editor : SharedPreferences.Editor

    val fireStoreDB = Firebase.firestore
    var favoriteCoins = hashMapOf<String,String>()
    private var items: ArrayList<CryptoAllCoinsModelItem> = arrayListOf()

    private var mAuth = FirebaseAuth.getInstance()
    val currentUser = mAuth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = DBHelper(context)
        sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        editor =  sharedPreferences.edit()

        isFirstStart = sharedPreferences.getBoolean("isFirstStart",true)




    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_favorites, container, false)

        viewModel = ViewModelProvider(this).get(CryptoViewModel::class.java)

        getFireBaseData()

        // recycler view set
        coinsAdapter = RecyclerViewAdapter()
        view.favoritesRecyclerView.adapter=coinsAdapter
        view.favoritesRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)




        return view
    }

    private fun getFireBaseData() {
        if(currentUser==null)
            return

        fireStoreDB.collection("bitcoinTicker").document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->

                if (document != null && document.data!=null) {
                    favoriteCoins.clear()
                    favoriteCoins.putAll(document.data as HashMap<String, String>)

                    getFavoriteCoinsDB(favoriteCoins)

                }
                else
                    println("ERROR Document is nullllllll! ")

            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents.", exception)
            }

    }

    private fun getFavoriteCoinsDB(favoriteCoins: HashMap<String, String>)
    {
        items.clear()
        for(item  in favoriteCoins)
        {
            items.add(db.getCoinByID(item.value))
        }

        coinsAdapter.addAll(items)
    }

    override fun onResume() {
        super.onResume()
        getFireBaseData()
    }


}