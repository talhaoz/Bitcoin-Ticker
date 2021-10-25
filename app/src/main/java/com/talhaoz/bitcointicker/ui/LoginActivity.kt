package com.talhaoz.bitcointicker.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.talhaoz.bitcointicker.R
import kotlinx.android.synthetic.main.login_activity.*

class LoginActivity : AppCompatActivity()
{

    private lateinit var mAuth : FirebaseAuth

    override fun onStart() {
        super.onStart()

        val currentUser = mAuth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {

        if(currentUser!=null)
        {
            startActivity(Intent(this,MainActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
            finish()
        }
        //else
           // Toast.makeText(baseContext, "Unable to login !", Toast.LENGTH_SHORT).show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        mAuth = FirebaseAuth.getInstance()
        val currUser = mAuth.currentUser


        registerButton.setOnClickListener{
            signUpUser()
        }

        loginButton.setOnClickListener {
            loginUser()
        }

    }

    private fun loginUser() {
        if(!checkTexts())
            return

        mAuth.signInWithEmailAndPassword(userNameEditText.text.toString(), passwordEditText.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "Login successfull!",
                        Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,MainActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
                    finish()
                } else {
                    Toast.makeText(baseContext, "Email or password is invalid!",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkTexts() : Boolean
    {
        if (userNameEditText.text.toString().isEmpty()) {
            userNameEditText.error = "Please enter an email"
            userNameEditText.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(userNameEditText.text.toString()).matches()) {
            userNameEditText.error = "Please enter valid email"
            userNameEditText.requestFocus()
            return false
        }

        if (passwordEditText.text.length < 8) {
            passwordEditText.error = "Please enter 8 character at least"
            passwordEditText.requestFocus()
            return false
        }

        if (passwordEditText.text.toString().isEmpty()) {
            passwordEditText.error = "Please enter password"
            passwordEditText.requestFocus()
            return false
        }
        return true
    }

    private fun signUpUser() {

        if(!checkTexts())
            return

        mAuth.createUserWithEmailAndPassword(userNameEditText.text.toString(), passwordEditText.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "Sign Up successfull!",
                        Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,MainActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
                    finish()
                } else {
                    Toast.makeText(baseContext, "Sign Up failed !",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}