package com.habiba.newsapp

import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class forgetpassword : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var etEmail: EditText
    private lateinit var btnReset: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_forgetpassword)


            mAuth = FirebaseAuth.getInstance()
            val btnReset = findViewById<Button>(R.id.btnReset)
            val etEmail = findViewById<EditText>(R.id.edtForgotPasswordEmail)

            btnReset.setOnClickListener {
                val email = etEmail.text.toString().trim()

                if (email.isEmpty()) {
                    Toast.makeText(this, "Please enter your email", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Reset email sent. Check your inbox!", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun resetPassword() {
        val email = etEmail.text.toString().trim()

        if (email.isEmpty()) {
            etEmail.error = "Please enter your email"
            etEmail.requestFocus()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Please enter a valid email"
            etEmail.requestFocus()
            return
        }

        // إخفاء لوحة المفاتيح
        hideKeyboard()

        // عرض الـ progressBar أثناء الإرسال
        progressBar.visibility = ProgressBar.VISIBLE
        btnReset.isEnabled = false

        mAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                progressBar.visibility = ProgressBar.GONE
                btnReset.isEnabled = true

                if (task.isSuccessful) {
                    Toast.makeText(this, "Reset email sent. Check your inbox!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(etEmail.windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}