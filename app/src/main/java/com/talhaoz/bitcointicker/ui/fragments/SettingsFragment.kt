package com.talhaoz.bitcointicker.ui.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.talhaoz.bitcointicker.R
import com.talhaoz.bitcointicker.ui.LoginActivity
import com.talhaoz.bitcointicker.ui.MainActivity
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*


class SettingsFragment : Fragment(R.layout.fragment_settings) {

    val currencySymbol   = hashMapOf("try" to 1,"usd" to 2,"eur" to 3,"gbp" to 4)
    val currencySymbolVal   = hashMapOf(1 to "try",2 to "usd",3 to "eur",4 to "gbp")

    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor : SharedPreferences.Editor

    private var mAuth = FirebaseAuth.getInstance()
    val currentUser = mAuth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        editor =  sharedPreferences.edit()
        val currencyStr=sharedPreferences.getString("currency_str","usd")

        // set the selected currency radio button
        var radioButton : RadioButton
        val radioButtonIndex=currencySymbol.get(currencyStr)
        if(radioButtonIndex!=null) {
            radioButton = view.radioGroup.getChildAt(radioButtonIndex) as RadioButton
            radioButton.isChecked=true
        }
        val email = currentUser?.email
        view.signedInUserText.text="Signed in as $email"

        view.radioGroup.setOnCheckedChangeListener { group, checkedId ->

            val checkedIndex =  group.indexOfChild(group.findViewById(checkedId))
            println("----------------------------   " + checkedIndex)
            val selectedCurr=currencySymbolVal.get(checkedIndex)
            editor.putString("currency_str",selectedCurr).apply()

            Toast.makeText(context,"${(group.getChildAt(checkedIndex) as RadioButton).text}  seçildi!",Toast.LENGTH_SHORT).show();

        }

        view.logOutButton.setOnClickListener {
            mAuth.signOut()
            startActivity(Intent(activity, LoginActivity::class.java))
            //activity?.finish()
        }

        return view
    }



}