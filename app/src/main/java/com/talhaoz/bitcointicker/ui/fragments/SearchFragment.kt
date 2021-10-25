package com.talhaoz.bitcointicker.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.talhaoz.bitcointicker.R
import com.talhaoz.bitcointicker.adapter.RecyclerViewAdapter
import com.talhaoz.bitcointicker.db.DBHelper
import com.talhaoz.bitcointicker.models.CryptoAllCoinsModelItem
import com.talhaoz.bitcointicker.viewmodel.CryptoViewModel
import kotlinx.android.synthetic.main.fragment_search.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var viewModel: CryptoViewModel
    lateinit var coinsAdapter: RecyclerViewAdapter

    lateinit var db : DBHelper
    lateinit var sharedPreferences: SharedPreferences
    var isFirstStart = true
    lateinit var editor : SharedPreferences.Editor

    private var isItResume = false

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
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        viewModel = ViewModelProvider(this).get(CryptoViewModel::class.java)

        if(!isItResume)
            viewModel.getData(null) // call for api

        // recycler view set
        coinsAdapter = RecyclerViewAdapter()
        view.mainRecyclerView.adapter=coinsAdapter
        view.mainRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)


        observeData(view)
        view.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                view.searchView.isFocusable=false
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {

                val coinsList : ArrayList<CryptoAllCoinsModelItem>?
                if(!newText.equals(""))
                    coinsList = db.readData(newText)
                else
                    coinsList=null
                coinsAdapter.addAll(coinsList)
                return true
            }
        })

        // to dismiss keyboard on start
        view.searchView.isFocusable=false

        // Inflate the layout for this fragment
        return view
    }

    override fun onResume() {
        super.onResume()
        isItResume=true
    }

    private fun observeData(view : View)
    {
        var dataB = db.readData(null)
        var dialog : AlertDialog
        viewModel.coinsList.observe(viewLifecycleOwner, Observer { data ->
            data?.let {
                if(isFirstStart)
                {

                    GlobalScope.launch(Dispatchers.Default) {

                        withContext(Dispatchers.Main)
                        {

                            var dialogB = AlertDialog.Builder(view.context)
                            val dialogView = LayoutInflater.from(context).inflate(
                                R.layout.loading_layout,null)
                            dialogB.setView(dialogView)

                            dialog  =dialogB.create()
                            // Set other dialog properties
                            dialog.setCancelable(false)
                            dialog.show()


                        }

                        db.insertData(data)
                        editor.putBoolean("isFirstStart",false).apply()

                        withContext(Dispatchers.Main)
                        {
                            dialog.dismiss()
                            Toast.makeText(context,"${data.size} coin added successfully!",Toast.LENGTH_LONG).show()
                            //view.mainProgressBar.visibility = View.INVISIBLE

                        }
                    }

                }
                else
                {
                    //db.deleteAllData()

                    if(dataB.size !=data.size)
                    {

                        GlobalScope.launch(Dispatchers.Default) {

                            withContext(Dispatchers.Main)
                            {

                                var dialogB = AlertDialog.Builder(view.context)
                                val dialogView = LayoutInflater.from(context).inflate(
                                    R.layout.loading_layout,null)
                                dialogB.setView(dialogView)

                                dialog  =dialogB.create()
                                // Set other dialog properties
                                dialog.setCancelable(false)
                                dialog.show()


                            }

                            val oldData = db.readData(null)
                            oldData.addAll(data)

                            val unInsertedElements = oldData.groupBy { it.id }
                                .filter { it.value.size == 1 }
                                .flatMap { it.value }

                            val newList = arrayListOf<CryptoAllCoinsModelItem>()
                            newList.addAll(unInsertedElements)
                            db.insertData(newList)

                            withContext(Dispatchers.Main)
                            {
                                dialog.dismiss()
                                Toast.makeText(context,"${newList.size} coin added successfully!",Toast.LENGTH_LONG).show()
                                //view.mainProgressBar.visibility = View.INVISIBLE

                            }
                        }



                    }
                    else
                    {
                        println("Coins are up to date!")
                    }

                }
            }
        })

    }



}