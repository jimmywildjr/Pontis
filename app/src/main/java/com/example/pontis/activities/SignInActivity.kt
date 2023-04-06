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
    //create variable for binding and firebase
    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    //create GoogleSignInClient variable
    private lateinit var googleSignInClient : GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //initialise binding and firebase
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        val context = applicationContext
        val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)

        //requests token and email
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(resId))
            .requestEmail()
            .build()

        //initialise GoogleSignInOptions
        googleSignInClient = GoogleSignIn.getClient(this , gso)

        //check if sign in with google button clicked
        findViewById<Button>(R.id.google).setOnClickListener{
            signInGoogle()
        }
        //checks if the textView saying reset password has been clicked
        binding.resetpass.setOnClickListener {
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
        }

        //checks if the textView saying to go to sign up has been clicked
        binding.textView.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        // if sign up button is clicked
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            //check if any of the fields are empty
            if (email.isNotEmpty() && pass.isNotEmpty()){
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    //check if user has verified email
                    if (it.isSuccessful) {
                        val verification = firebaseAuth.currentUser?.isEmailVerified
                        if (verification==true){
                            FirestoreClass().getUserDetails(this@SignInActivity)
                        } else{
                            Toast.makeText(this, "You have not verified your email", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this, "Fields are empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
    //this function is called by the getUserDetails when user logs in
    fun userLoggedInSuccess(user: User){
        if (user.profileCompleted == 0){
            val intent = Intent(this@SignInActivity, OnboardingActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
            startActivity(intent)
        } else{
            startActivity(Intent(this@SignInActivity, MainActivity::class.java))
        }
    }

    //calls the launcher function
    private fun signInGoogle(){
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }
    // shows the sign in options
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    //checks if it is successful and calls updateUI which loads
    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if(task.isSuccessful){
            val account : GoogleSignInAccount? = task.result
            if (account !=null){
                updateUI(account)
            }
        }else{
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }
    //signs in the user
    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener{
            if (it.isSuccessful){
                FirestoreClass().getUserDetails(this@SignInActivity)
            }else{
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }

        }
    }
}