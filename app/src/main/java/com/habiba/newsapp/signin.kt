package com.habiba.newsapp

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth


class signin : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        if (currentUser != null && currentUser.isEmailVerified) {
            // User is logged in and verified, go directly to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()  // Close signup activity so user cannot go back to it
        }
        val btnLogin = findViewById<Button>(R.id.btn_login)
        val etEmail = findViewById<EditText>(R.id.et_email_login)
        val etPassword = findViewById<EditText>(R.id.et_password_login)
        val tvForgotPassword = findViewById<TextView>(R.id.tv_forgot_password)
        val tvSignup = findViewById<TextView>(R.id.tv_signup)
        val logo = findViewById<ImageView>(R.id.img_logosignin)

        // Logo Animation
        val moveAnimation = ObjectAnimator.ofFloat(logo, View.TRANSLATION_Y, -250f, 0f)
        moveAnimation.duration = 1000
        moveAnimation.interpolator = LinearInterpolator()
        moveAnimation.start()

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showMessage("Missing Fields", "Please enter email and password.")
                return@setOnClickListener
            }

            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = mAuth.currentUser
                        user?.reload()?.addOnCompleteListener { reloadTask ->
                            if (reloadTask.isSuccessful) {
                                if (user.isEmailVerified) {
                                    // User is verified → Enter the Home Activity
                                    showMessage("Login Successful", "Welcome!") {
                                        startActivity(Intent(this, MainActivity::class.java))
                                        finish()
                                    }
                                } else {
                                    //  User is NOT verified → Send Verification Email
                                    sendVerificationEmail(user)
                                    showMessage(
                                        "Email Not Verified",
                                        "A new verification email has been sent. Please check your email and verify your account before logging in."
                                    )
                                }
                            } else {
                                showMessage(
                                    "Error",
                                    "Could not refresh user data. Please try again."
                                )
                            }
                        }
                    } else {
                        showMessage(
                            "Login Failed",
                            "Authentication failed: ${task.exception?.message}"
                        )
                    }
                }
        }
        tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, forgetpassword::class.java))
        }

        tvSignup.setOnClickListener {
            startActivity(Intent(this, signup::class.java))
        }
    }

    // ✅ Function to Send Verification Email on Every Login Attempt
    private fun sendVerificationEmail(user: com.google.firebase.auth.FirebaseUser) {
        user.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    showMessage("Failed to Send", "Could not send verification email: ${task.exception?.message}")
                }
            }
    }

    // ✅ Reusable Function for Showing a Dialog
    private fun showMessage(title: String, message: String, onPositiveClick: (() -> Unit)? = null) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                onPositiveClick?.invoke()
            }
            .show()
    }
}