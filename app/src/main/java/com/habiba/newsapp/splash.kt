package com.habiba.newsapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
//import com.google.firebase.auth.FirebaseAuth
import com.habiba.newsapp.R


class splash : AppCompatActivity() {
    //private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        //  mAuth = FirebaseAuth.getInstance()
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, onboarding::class.java)
            startActivity(intent)
            finish() // Close MainActivity after transitioning
        }, 2000)
    }

}