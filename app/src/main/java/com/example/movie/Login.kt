package com.example.movie

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.login_layout.*
//import jdk.nashorn.internal.runtime.ECMAException.getException
import com.google.firebase.auth.FirebaseUser
import org.junit.experimental.results.ResultMatchers.isSuccessful
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import android.R.attr.password
import kotlinx.android.synthetic.main.content_main.*


class Login : AppCompatActivity() {

    private var email : EditText? = null
    private var password : EditText? = null
    private var signInButton : Button? = null
    private var registerButton : Button? = null
    private var continueWithGoogleButton : Button? = null
    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        email = findViewById(R.id.email_address)
        password = findViewById(R.id.password_address)
        registerButton = findViewById(R.id.register_button)
        signInButton = findViewById(R.id.signin_button)
        continueWithGoogleButton = findViewById<Button>(R.id.google_button)

        registerButton?.setOnClickListener(View.OnClickListener {
            val intent = Intent(this,SignUp::class.java)
            startActivity(intent)
        })

        signInButton?.setOnClickListener(View.OnClickListener {
            performSignIn()
        })

        continueWithGoogleButton?.setOnClickListener(View.OnClickListener {
            performLoginUsingGoogle()
        })

    }

    private fun performSignIn() {
        val email_address:String = email?.text.toString().trim()
        val pass:String = password?.text.toString().trim()

        progress_bar?.visibility = View.VISIBLE

        if (TextUtils.isEmpty(email_address)){
            Toast.makeText(this,"Please Enter Valid Email", Toast.LENGTH_SHORT).show()
        }

        else if (TextUtils.isEmpty(pass)){
            Toast.makeText(this,"Please Enter Password", Toast.LENGTH_SHORT).show()
        }

        else if (pass.length < 5){
            Toast.makeText(this,"Password too short",Toast.LENGTH_SHORT).show()
        }

        else{
            mAuth.signInWithEmailAndPassword(email_address, pass)
                .addOnCompleteListener(this) { task ->
                    progress_bar?.visibility = View.GONE
                    if (task.isSuccessful) {
                        val intent = Intent(this,MainActivity::class.java)
                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        Toast.makeText(applicationContext,"Login Successfull",Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(applicationContext,"Login Failed or User not available",Toast.LENGTH_SHORT).show()
                    }

                }
        }

    }

    private fun performLoginUsingGoogle() {
        
    }


}