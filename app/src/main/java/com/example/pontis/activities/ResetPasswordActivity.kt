package com.example.pontis.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.pontis.databinding.ActivityResetPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ResetPasswordActivity : AppCompatActivity() {

    // declare view binding and firebase authentication variables
    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //inflate the view using the view binding and set the content view
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initialize firebase authentication instance
        firebaseAuth = FirebaseAuth.getInstance()

        // set a click listener on the "go back" TextView to go back to sign-in screen
        binding.goback.setOnClickListener{
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        // set a click listener on the "send reset password email" button to send reset email
        binding.resetemail.setOnClickListener {
            // get email entered by user
            val email = binding.emailEt.text.toString()

            // check if email is not empty
            if (email.isNotEmpty()) {
                // send password reset email using firebase authentication
                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener {
                    // check if password reset email has been sent successfully
                    if (it.isSuccessful) {
                        // go back to sign-in screen and display success message
                        val intent = Intent(this, SignInActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(
                            this,
                            "Password Reset Email has been successfully sent",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // display error message if there was an error while sending reset email
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // display error message if email field is empty
                Toast.makeText(this, "Email is empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
}