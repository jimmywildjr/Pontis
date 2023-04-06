package com.example.pontis.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.example.pontis.R
import com.example.pontis.databinding.ActivityChangeDetailsBinding
import com.google.firebase.auth.FirebaseAuth

class ChangeDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangeDetailsBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the binding by inflating the layout
        binding = ActivityChangeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the FirebaseAuth instance
        firebaseAuth = FirebaseAuth.getInstance()

        // Set up the click listener for the textView that allows the user to go back to the previous screen
        binding.textView.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("fragmentName", "SettingsFragment")
            startActivity(intent)
        }

        // Set up the click listener for the "Update Details" button
        binding.updateemail.setOnClickListener {
            val user = firebaseAuth.currentUser
            val email = binding.emailEt.text.toString()

            // Check if the email field is not empty
            if (email.isNotEmpty()) {

                // Update the user's email address and add a listener to check if it was successful
                user?.updateEmail(email)?.addOnCompleteListener {

                    // Check if the email address update was successful
                    if (it.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Update successful",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Send a verification email to the user's new email address
                        firebaseAuth.currentUser?.sendEmailVerification()
                            ?.addOnSuccessListener {
                                Toast.makeText(this, "Please Verify Email", Toast.LENGTH_SHORT)
                                    .show()

                                // After the verification email has been sent, navigate the user to the sign-in screen
                                val intent = Intent(this, SignInActivity::class.java)
                                startActivity(intent)
                            }
                            ?.addOnFailureListener{
                                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                            }

                    } else {
                        // If the email address update was not successful, display the error message
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // If the email field is empty, display a message to the user
                Toast.makeText(this, "Email is empty", Toast.LENGTH_SHORT).show()
            }
        }

        // Set up the click listener for the "Update Password" button
        binding.updatepassword.setOnClickListener {
            val user = firebaseAuth.currentUser
            val pass = binding.passET.text.toString()

            // Check if the password field is not empty
            if (pass.isNotEmpty()) {

                // Update the user's password and add a listener to check if it was successful
                user?.updatePassword(pass)?.addOnCompleteListener {

                    // Check if the password update was successful
                    if (it.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Update successful",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // If the password update was not successful, display the error message
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // If the password field is empty, display a message to the user
                Toast.makeText(this, "Password is empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
}