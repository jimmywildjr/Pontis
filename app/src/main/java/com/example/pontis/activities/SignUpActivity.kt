package com.example.pontis.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.pontis.R
import com.example.pontis.databinding.ActivitySignUpBinding
import com.example.pontis.firestore.FirestoreClass
import com.example.pontis.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class SignUpActivity : AppCompatActivity() {
    // create variable for binding and Firebase
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient : GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialize binding and Firebase
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)
        firebaseAuth = FirebaseAuth.getInstance()

        // retrieve client ID for Google Sign-In
        val context = applicationContext
        val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)

        // configure Google Sign-In options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(resId))
            .requestEmail()
            .build()

        // create Google Sign-In client
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // set click listener for Google Sign-In button
        findViewById<Button>(R.id.google).setOnClickListener {
            signInGoogle()
        }

        // set click listener for "Already have an account?" text
        binding.textView.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        // set click listener for sign up button
        binding.button.setOnClickListener {
            val firstname = binding.firstnameEt.text.toString()
            val lastname = binding.lastnameEt.text.toString()
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmpass = binding.confirmPassEt.text.toString()

            // check if any of the fields are empty
            if (firstname.isNotEmpty() && lastname.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty() && confirmpass.isNotEmpty()) {

                // check if passwords match
                if (pass == confirmpass) {

                    // create user account with Firebase Authentication
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {

                            // if account creation is successful, create User object and call register function
                            val firebaseUser: FirebaseUser = it.result!!.user!!
                            val user = User(
                                firebaseUser.uid,
                                firstname,
                                lastname,
                                email,
                            )
                            FirestoreClass().registerUser(this, user)

                            // send email verification to user
                            firebaseAuth.currentUser?.sendEmailVerification()?.addOnSuccessListener {
                                Toast.makeText(this, "Please Verify Email", Toast.LENGTH_SHORT)
                                    .show()
                                val intent = Intent(this, SignInActivity::class.java)
                                startActivity(intent)
                            }?.addOnFailureListener{
                                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Fields are empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // function to start Google Sign-In flow
    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    // handle results of Google Sign-In flow
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    // This function handles the results of the Google sign-in process
    private fun handleResults(task: Task<GoogleSignInAccount>) {
        // Check if the task was successful
        if(task.isSuccessful){
            // Get the account information from the result
            val account : GoogleSignInAccount? = task.result
            // If the account exists, update the UI with the account information
            if (account !=null){
                updateUI(account)
            }
        } else {
            // If the task was not successful, show an error message
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    // This function is called when the activity starts
    override fun onStart() {
        super.onStart()
        // Check if the user is already logged in
        if (firebaseAuth.currentUser != null){
            // If the user is already logged in, automatically load the main activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    // This function updates the UI with the user's account information
    private fun updateUI(account: GoogleSignInAccount) {
        // Get the user's credentials
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        // Sign in with the user's credentials
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener{
            // Check if the sign-in was successful
            if (it.isSuccessful){
                // If the sign-in was successful, create a new user object
                val firebaseUser: FirebaseUser = it.result!!.user!!
                val user = User (
                    firebaseUser.uid,
                    account.givenName.toString(),
                    account.familyName.toString(),
                    account.email.toString(),
                )
                // Register the user with Firestore
                FirestoreClass().registerUser(this,user)
                // Get the user details from Firestore
                FirestoreClass().getUserDetails(this@SignUpActivity)
                // Load the main activity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                // If the sign-in was not successful, show an error message
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}