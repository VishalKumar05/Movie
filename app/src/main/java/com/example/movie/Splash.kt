package com.example.movie

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity

class Splash : AppCompatActivity(){

    private var SPLASH_TIME_OUT : Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)

        Handler().postDelayed({
            var intent:Intent = Intent(this,Login::class.java)
            startActivity(intent)

            finish()
        },SPLASH_TIME_OUT)
    }

}