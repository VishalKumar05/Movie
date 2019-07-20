package com.example.movie

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
//import jdk.nashorn.internal.runtime.ECMAException.getException
import com.google.firebase.auth.FirebaseUser
import org.junit.experimental.results.ResultMatchers.isSuccessful
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task
import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import android.R.attr.password
import android.content.Intent
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.content_main.*


class SignUp: AppCompatActivity() {

    //private var username : EditText? = null
    private var email : EditText? = null
    private var password : EditText? = null
    private var registerButton : Button? = null
    private lateinit var mAuth : FirebaseAuth
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_layout)
        initViews()

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        registerButton?.setOnClickListener(View.OnClickListener {
            performNewUserRegistration()
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.METHOD, performNewUserRegistration().toString())
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)
        })
    }

    private fun performNewUserRegistration() {
        //var user_name:String = username?.text.toString()
        val email_address:String = email?.text.toString().trim()
        val pass:String = password?.text.toString().trim()

        progress_bar?.visibility = View.VISIBLE

        /*if (TextUtils.isEmpty(user_name)){
            Toast.makeText(this,"Please Enter Username",Toast.LENGTH_SHORT).show()
        }*/

        if (TextUtils.isEmpty(email_address)){
            Toast.makeText(this,"Please Enter Valid Email",Toast.LENGTH_SHORT).show()
        }

        else if (TextUtils.isEmpty(pass)){
            Toast.makeText(this,"Please Enter Password",Toast.LENGTH_SHORT).show()
        }

        else if (pass.length < 5){
            Toast.makeText(this,"Password too short",Toast.LENGTH_SHORT).show()
        }

        else{
            mAuth.createUserWithEmailAndPassword(email_address, pass)
                .addOnCompleteListener(this) { task ->
                    progress_bar?.visibility = View.GONE
                    if (task.isSuccessful) {
                        val intent = Intent(this,MainActivity::class.java)
                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        Toast.makeText(applicationContext,"Registration Successfull",Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(applicationContext,"Authentication Failed",Toast.LENGTH_SHORT).show()
                    }

                }
        }

    }

    private fun initViews() {
        //username = findViewById(R.id.username)
        email = findViewById(R.id.email_address)
        password = findViewById(R.id.password)
        registerButton = findViewById<Button>(R.id.register_button)
    }

}