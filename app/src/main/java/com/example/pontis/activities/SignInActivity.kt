package com.example.pontis.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.pontis.R
import com.example.pontis.databinding.ActivitySignInBinding
import com.example.pontis.firestore.FirestoreClass
import com.example.pontis.models.User
import com.example.pontis.utils.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class SignInActivity : AppCompatActivity() {
    // create variable for binding and firebase
    private lateinit var binding: ActivitySignInBinding // view binding for activity_sign_in.xml
    private lateinit var firebaseAuth: FirebaseAuth // instance of FirebaseAuth

    // create GoogleSignInClient variable
    private lateinit var googleSignInClient: GoogleSignInClient // instance of GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // initialize binding and firebase
        binding = ActivitySignInBinding.inflate(layoutInflater) // initialize view binding
        setContentView(binding.root) // set the content view of the activity

        firebaseAuth = FirebaseAuth.getInstance() // get an instance of FirebaseAuth

        val context = applicationContext
        // get the default_web_client_id from the resources
        val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)

        // create a GoogleSignInOptions object to configure the sign-in request
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(resId)) // set the request ID token
            .requestEmail() // request user's email address
            .build()

        // initialize GoogleSignInClient with the GoogleSignInOptions
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // check if sign in with google button clicked
        findViewById<Button>(R.id.google).setOnClickListener {
            signInGoogle() // call the function to start the sign-in flow
        }

        // check if the textView saying reset password has been clicked
        binding.resetpass.setOnClickListener {
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent) // start the ResetPasswordActivity
        }

        // check if the textView saying to go to sign up has been clicked
        binding.textView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent) // start the SignUpActivity
        }

        // if sign up button is clicked
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            // check if any of the fields are empty
            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    // check if user has verified email
                    if (it.isSuccessful) {
                        val verification = firebaseAuth.currentUser?.isEmailVerified
                        if (verification == true) {
                            FirestoreClass().getUserDetails(this@SignInActivity) // get user details from Firestore
                        } else {
                            Toast.makeText(this, "You have not verified your email", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Fields are empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // this function is called by the getUserDetails when user logs in
    fun userLoggedInSuccess(user: User) {
        if (user.profileCompleted == 0) {
            val intent = Intent(this@SignInActivity, OnboardingActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
            startActivity(intent) // start the OnboardingActivity if user profile is not complete
        } else {
            startActivity(Intent(this@SignInActivity, MainActivity::class.java))
        }
    }

    // calls the launcher function
    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent) // start the sign-in flow
    }
    // This code shows the sign-in options
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        // If the result code is OK, sign in using the Google account selected
        if (result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    // This function checks if the sign-in process was successful, and if so, calls updateUI to sign in the user
    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if(task.isSuccessful){
            val account : GoogleSignInAccount? = task.result
            if (account !=null){
                updateUI(account)
            }
        }else{
            // If there was an error, display a toast with the error message
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    // This function signs in the user using Firebase Authentication
    private fun updateUI(account: GoogleSignInAccount) {
        // Get the ID token for the user's Google account
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        // Sign in using the ID token
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener{
            if (it.isSuccessful){
                // If sign-in was successful, get the user's details
                FirestoreClass().getUserDetails(this@SignInActivity)
            }else{
                // If there was an error, display a toast with the error message
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}