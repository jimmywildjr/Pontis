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
        //initialise binding and firebase
        binding = ActivityChangeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        //checks if the textView saying go back has been clicked
        binding.textView.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("fragmentName", "SettingsFragment")
            startActivity(intent)
        }
        //check if update details button been clicked
        binding.updateemail.setOnClickListener {
            val user = firebaseAuth.currentUser
            val email = binding.emailEt.text.toString()

            //need if statements to check each of the fields and
            if (email.isNotEmpty()) {
                user?.updateEmail(email)?.addOnCompleteListener {
                    //check if it has been successful
                    if (it.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Update successful",
                            Toast.LENGTH_SHORT
                        ).show()
                        //sends email verification
                        firebaseAuth.currentUser?.sendEmailVerification()
                            ?.addOnSuccessListener {
                                Toast.makeText(this, "Please Verify Email", Toast.LENGTH_SHORT)
                                    .show()
                                val intent = Intent(this, SignInActivity::class.java)
                                startActivity(intent)
                            }
                            ?.addOnFailureListener{
                                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Email is empty", Toast.LENGTH_SHORT).show()
            }
        }
        //check if update passwordbutton been clicked and updates password if it is not empty
        binding.updatepassword.setOnClickListener {

            val user = firebaseAuth.currentUser
            val pass = binding.passET.text.toString()
            if (pass.isNotEmpty()) {
                user?.updatePassword(pass)?.addOnCompleteListener {
                    //check if it has been successful
                    if (it.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Update successful",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Password is empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
}