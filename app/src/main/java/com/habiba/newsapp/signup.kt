package com.habiba.newsapp

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.habiba.newsapp.responce.Article
import com.habiba.newsapp.responce.favouritesData


class signup : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        if (currentUser != null && currentUser.isEmailVerified) {
            // User is logged in and verified, go directly to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()  // Close signup activity so user cannot go back to it
        }
        val tvAlreadyHaveAccount = findViewById<TextView>(R.id.tv_login)
        val btnSignUp = findViewById<Button>(R.id.btn_signup)
        val etEmail = findViewById<EditText>(R.id.et_email)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val etConfirmPassword = findViewById<EditText>(R.id.et_confirm_password)
        val logo = findViewById<ImageView>(R.id.img_logosignup)

        // Logo Animation
        val moveAnimation = ObjectAnimator.ofFloat(logo, View.TRANSLATION_Y, -250f, 0f)
        moveAnimation.duration = 1000
        moveAnimation.interpolator = LinearInterpolator()
        moveAnimation.start()

        tvAlreadyHaveAccount.setOnClickListener {
            startActivity(Intent(this, signin::class.java))
        }

        btnSignUp.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showMessage("Missing Fields", "Please enter email, password, and confirm password.")
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showMessage("Invalid Email", "Please enter a valid email format.")
                return@setOnClickListener
            }

            if (password.length < 6) {
                showMessage("Weak Password", "Password must be at least 6 characters long.")
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                showMessage("Password Mismatch", "Passwords do not match. Please try again.")
                return@setOnClickListener
            }

            // Firebase Authentication
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = mAuth.currentUser
                        user?.sendEmailVerification()
                            ?.addOnCompleteListener { verifyTask ->
                                if (verifyTask.isSuccessful) {
                                    val db = FirebaseFirestore.getInstance()
                                    val userId =
                                        FirebaseAuth.getInstance().currentUser?.uid ?: ""
                                    constants.userID = userId;

                                    val userData = hashMapOf(
                                        "articlesList" to emptyList<favouritesData>(),
                                        "userId" to userId
                                    )

                                    db.collection("Favourites")
                                        .document(userId)  // Uses the UID as the document ID
                                        .set(userData)
                                        .addOnSuccessListener {
                                            Log.d(
                                                "Firestore",
                                                "✅ Document successfully created for user: $userId"
                                            )
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e(
                                                "Firestore",
                                                " Error adding document: ${e.message}",
                                                e
                                            )

                                    }

                                    /////////////////////////////////////////////////////////////////////////
                                    showMessage("Sign Up Successful", "Please verify your email before logging in.")
                                    {
                                        startActivity(Intent(this, signin::class.java))
                                        finish()
                                    }
                                } else {
                                    showMessage("Verification Failed", "Could not send verification email: ${verifyTask.exception?.message}")
                                }
                            }
                    } else {
                        showMessage("Sign Up Failed", "Authentication failed: ${task.exception?.message}")
                    }
                }
        }
    }

    // Reusable Function for Showing a Dialog
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