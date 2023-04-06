package com.example.pontis.activities

import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.*
import com.example.pontis.R
import com.example.pontis.firestore.FirestoreClass
import com.example.pontis.models.User
import com.example.pontis.utils.Constants
import com.example.pontis.utils.GlideLoader
import java.io.IOException


class OnboardingActivity : AppCompatActivity(), View.OnClickListener {

    // Declare variables for user details, selected image file URI, and user profile image URL
    private var mUserDetails: User = User()
    private var mSelectedImageFileUri: Uri? = null
    private var mUserProfileImageURL:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        // Check if intent has extra user details, and set mUserDetails accordingly
        @Suppress("DEPRECATION")
        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)){
            mUserDetails = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS, User::class.java)!!
            } else {
                intent.getParcelableExtra<User>(Constants.EXTRA_USER_DETAILS)!!
            }
        }

        // Find views for user photo and save details button, and set click listeners
        val iv_user_photo = findViewById<ImageView>(R.id.iv_user_photo)
        iv_user_photo.setOnClickListener(this@OnboardingActivity)
        val btn_savedetails = findViewById<Button>(R.id.btn_savedetails)
        btn_savedetails.setOnClickListener(this@OnboardingActivity)
    }

    override fun onClick(v: View?) {
        if (v != null){
            when (v.id){
                R.id.iv_user_photo ->{
                    // Check if read external storage permission is granted, and show image chooser if it is
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                        Constants.showImageChooser(this)
                    }else{
                        // Request permission to be granted. These are requested in app manifest.
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),Constants.READ_STORAGE_PERMISSION_CODE)
                    }
                }
                R.id.btn_savedetails ->{
                    // Check if no fields are empty
                    // Save user profile to cloud
                    if (mSelectedImageFileUri !=null){
                        FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUri, Constants.USER_PROFILE_IMAGE)
                    } else{
                        updateUserProfileDetails()
                    }
                }
            }
        }
    }

    // Function called when user profile update is successful
    fun userProfileUpdateSuccess(){
        Toast.makeText(this, "User Details Saved", Toast.LENGTH_SHORT).show()
        // Start MainActivity and finish OnboardingActivity
        startActivity(Intent(this@OnboardingActivity, MainActivity::class.java))
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            // Check if storage permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                // Show the image chooser to select an image from the gallery
                Constants.showImageChooser(this)
            } else{
                // Show a toast message if permission is denied
                Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode== Constants.IMAGE_REQUEST_CODE){
                if (data != null){
                    try{
                        // Get the URI of the selected image and load it into the image view
                        mSelectedImageFileUri = data.data!!
                        val iv_user_photo = findViewById<ImageView>(R.id.iv_user_photo)
                        GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!, iv_user_photo)
                    } catch (e: IOException){
                        // Catch any exceptions and show an error message
                        e.printStackTrace()
                        Toast.makeText(this, "Image Selection Failed", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }

    // Called after an image has been uploaded to cloud storage
    fun imageUploadSuccess(imageURL:String){
        // Save the image URL and update user profile details
        mUserProfileImageURL = imageURL
        updateUserProfileDetails()
    }

    private fun updateUserProfileDetails(){
        // Create a hashmap to store user profile data
        val userHashMap = HashMap<String, Any>()
        // Get user's gender and school year from the UI
        val rb_male = findViewById<RadioButton>(R.id.rb_male)
        val gender = if (rb_male.isChecked){
            Constants.MALE
        }else{
            Constants.FEMALE
        }
        val schoolyearspinner = findViewById<Spinner>(R.id.schoolyearspinner)
        val schoolyearvalue = schoolyearspinner.selectedItem.toString()

        // Get user's home city from the UI
        val homecityspinner = findViewById<Spinner>(R.id.homecityspinner)
        val homecityvalue = homecityspinner.selectedItem.toString()

        // Add user profile data to the hashmap
        userHashMap[Constants.GENDER] = gender
        userHashMap[Constants.SCHOOLYEAR] = schoolyearvalue
        userHashMap[Constants.HOMECITY] = homecityvalue
        userHashMap[Constants.COMPLETE_PROFILE]=1
        if (mUserProfileImageURL.isNotEmpty()){
            userHashMap[Constants.IMAGE]=mUserProfileImageURL
        }

        // Update user profile data in Firestore database
        FirestoreClass().updateUserProfileData(this, userHashMap)
    }
}