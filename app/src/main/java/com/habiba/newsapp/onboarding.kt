package com.habiba.newsapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class onboarding : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        if (currentUser != null && currentUser.isEmailVerified) {
            // User is logged in and verified, go directly to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()  // Close signup activity so user cannot go back to it
        }

        val skipButton = findViewById<TextView>(R.id.tvSkip)

        skipButton.setOnClickListener {
            val intent = Intent(this, signup::class.java)
            startActivity(intent)
            finish()
        }
    }
}