package com.example.movie

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import android.R.attr.password
import android.util.Log
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.content_main.*


class Login : AppCompatActivity(),GoogleApiClient.OnConnectionFailedListener {

    private var email : EditText? = null
    private var password : EditText? = null
    private var signInButton : Button? = null
    private var registerButton : Button? = null
    private var googleLoginButton : SignInButton? = null
    private lateinit var mAuth : FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var mAuthListener:FirebaseAuth.AuthStateListener

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }

    override fun onStart() {
        super.onStart()
        //mAuth.addAuthStateListener(mAuthListener)
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        /*if (mAuthListener != null){
            FirebaseAuth.getInstance().signOut();
        }*/
        mAuth.addAuthStateListener(mAuthListener);
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)

        initViews()

        registerButton?.setOnClickListener(View.OnClickListener {
            val intent = Intent(this,SignUp::class.java)
            startActivity(intent)
        })

        signInButton?.setOnClickListener(View.OnClickListener {
            performSignIn()
        })

        googleLoginButton?.setOnClickListener(View.OnClickListener {
            performLoginUsingGoogle()
        })

        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            // Get signedIn user
            val user = firebaseAuth.currentUser

            //if user is signed in, we call a helper method to save the user details to Firebase
            if (user != null) {
                // User is signed in
                // you could place other firebase code
                //logic to save the user details to Firebase
                Log.d("TAG", "onAuthStateChanged:signed_in:" + user.uid)
            } else {
                // User is signed out
                Log.d("TAG", "onAuthStateChanged:signed_out")
            }
        }

        mAuthListener = FirebaseAuth.AuthStateListener(object : FirebaseAuth.AuthStateListener, (FirebaseAuth) -> Unit {
            override fun invoke(p1: FirebaseAuth) {
            }

            override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
                val user = firebaseAuth.getCurrentUser()
                if (user != null){
                    val intent = Intent(applicationContext,MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }

        })

    }

    private fun initViews() {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        email = findViewById(R.id.email_address)
        password = findViewById(R.id.password_address)
        registerButton = findViewById(R.id.register_button)
        signInButton = findViewById(R.id.signin_button)
        googleLoginButton = findViewById<SignInButton>(R.id.google_sign_in_button)
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

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.d(TAG, "Google sign in failed", e)
                // ...
            }
        }else{
            Toast.makeText(applicationContext,"Something went wrong",Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "SignInWithCredential:Success")
                    val user = mAuth.currentUser
                    updateUI(user)
                    gotoMainActivity()
                    Toast.makeText(applicationContext,"Gmail Login Successfull",Toast.LENGTH_SHORT).show()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    //Snackbar.make(main_layout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                    Toast.makeText(applicationContext,"Authentication Failed",Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun gotoMainActivity() {
        val intent = Intent(this,MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun updateUI(user: FirebaseUser?) {
        //progress_bar?.visibility = View.GONE
    }

    private fun performLoginUsingGoogle() {
        signIn()
    }

    override fun onResume() {
        super.onResume()
        Log.d("1","onResume")
        if (!TextUtils.isEmpty(email.toString())){
            email!!.text.clear()
        }
        if (!TextUtils.isEmpty(password.toString())){
            password!!.text.clear()
        }
    }
}