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

    private var mUserDetails: User = User()
    private var mSelectedImageFileUri: Uri? = null
    private var mUserProfileImageURL:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        @Suppress("DEPRECATION")
        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)){
            mUserDetails = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS, User::class.java)!!
            } else {
                intent.getParcelableExtra<User>(Constants.EXTRA_USER_DETAILS)!!
            }
        }
        val iv_user_photo = findViewById<ImageView>(R.id.iv_user_photo)
        iv_user_photo.setOnClickListener(this@OnboardingActivity)
        val btn_savedetails = findViewById<Button>(R.id.btn_savedetails)
        btn_savedetails.setOnClickListener(this@OnboardingActivity)

    }

    override fun onClick(v: View?) {
        if (v != null){
            when (v.id){
                R.id.iv_user_photo ->{
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                        Constants.showImageChooser(this)
                    }else{
                        //request permission to be granted. These are requested in app manifest

                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),Constants.READ_STORAGE_PERMISSION_CODE)
                    }
                }
                R.id.btn_savedetails ->{
                    //check no empty fields

                    //save to cloud
                    if (mSelectedImageFileUri !=null){
                        FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUri, Constants.USER_PROFILE_IMAGE)
                    } else{
                        updateUserProfileDetails()
                    }

                }
            }
        }
    }
    fun userProfileUpdateSuccess(){
        Toast.makeText(this, "User Details Saved", Toast.LENGTH_SHORT).show()
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
            //if permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            } else{
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
                        //get the uri of the image and change with the placeholder image
                        mSelectedImageFileUri = data.data!!
                        val iv_user_photo = findViewById<ImageView>(R.id.iv_user_photo)
                        GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!, iv_user_photo)
                    } catch (e: IOException){
                        //catch any exceptions and return error message
                        e.printStackTrace()
                        Toast.makeText(this, "Image Selection Failed", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }
    //called in upload image to cloud storage function
    fun imageUploadSuccess(imageURL:String){
        mUserProfileImageURL = imageURL
        updateUserProfileDetails()
    }
    private fun updateUserProfileDetails(){
        val userHashMap = HashMap<String, Any>()
        val rb_male = findViewById<RadioButton>(R.id.rb_male)
        val gender = if (rb_male.isChecked){
            Constants.MALE
        }else{
            Constants.FEMALE
        }
        val schoolyearspinner = findViewById<Spinner>(R.id.schoolyearspinner)
        val schoolyearvalue = schoolyearspinner.selectedItem.toString()

        val homecityspinner = findViewById<Spinner>(R.id.homecityspinner)
        val homecityvalue = homecityspinner.selectedItem.toString()

        userHashMap[Constants.GENDER] = gender
        userHashMap[Constants.SCHOOLYEAR] = schoolyearvalue
        userHashMap[Constants.HOMECITY] = homecityvalue
        userHashMap[Constants.COMPLETE_PROFILE]=1
        if (mUserProfileImageURL.isNotEmpty()){
            userHashMap[Constants.IMAGE]=mUserProfileImageURL
        }

        FirestoreClass().updateUserProfileData(this, userHashMap)
    }
}