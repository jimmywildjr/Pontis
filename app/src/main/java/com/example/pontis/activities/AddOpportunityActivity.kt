package com.example.pontis.activities

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.widget.*
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.pontis.R
import com.example.pontis.databinding.ActivitySignInBinding
import com.example.pontis.firestore.FirestoreClass
import com.example.pontis.models.Opportunity
import com.example.pontis.utils.Constants
import com.example.pontis.utils.GlideLoader
import com.google.android.material.textfield.TextInputEditText
import java.io.IOException

class AddOpportunityActivity : AppCompatActivity(), View.OnClickListener {
    private var mSelectedImageFileURI: Uri? = null
    private var mOpportunityImageURL: String = ""

    // This function is called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the layout for this activity
        setContentView(R.layout.activity_add_opportunity)

        // Find the ImageView and Button views in the layout and assign them to variables
        val iv_add_update_opportunity = findViewById<ImageView>(R.id.iv_add_update_opportunity)
        val btn_submit_add_opportunity = findViewById<Button>(R.id.btn_submit_add_opportunity)

        // Set the OnClickListener for the ImageView and Button views to be this activity
        iv_add_update_opportunity.setOnClickListener(this)
        btn_submit_add_opportunity.setOnClickListener(this)
    }

    // This function is called when the ImageView or Button views are clicked
    override fun onClick(v: View?) {
        // Check if the clicked view is not null
        if (v !=null){
            // Use a when statement to determine which view was clicked based on its ID
            when(v.id){
                R.id.iv_add_update_opportunity ->{
                    // Check if permission to read external storage has already been granted
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                        // If the permission has been granted, show an image chooser dialog
                        Constants.showImageChooser(this@AddOpportunityActivity)
                    }else{
                        // If the permission has not been granted, request the permission
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),Constants.READ_STORAGE_PERMISSION_CODE)
                    }
                }
                R.id.btn_submit_add_opportunity ->{
                    // If the submit button was clicked, validate the opportunity details and upload the logo if validation succeeds
                    if (validateOpportunityDetails()){
                        uploadOpportunityLogo()
                    }
                }
            }
        }
    }
    // Gets the result of the permissions check
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Check if the result is OK
        if (resultCode == Activity.RESULT_OK) {
            // Check if the request code is for image selection
            if (requestCode == Constants.IMAGE_REQUEST_CODE) {
                // Check if data is not null
                if (data != null) {
                    // Find the ImageView for the add/update opportunity button and set its image to an edit icon
                    val iv_add_update_opportunity =
                        findViewById<ImageView>(R.id.iv_add_update_opportunity)
                    iv_add_update_opportunity.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_vector_edit_24
                        )
                    )
                    // Get the URI for the selected image and try to load it into the ImageView for the opportunity logo using Glide
                    mSelectedImageFileURI = data.data!!
                    try {
                        val iv_opportunity_logo =
                            findViewById<ImageView>(R.id.iv_opportunity_logo)
                        GlideLoader(this).loadUserPicture(mSelectedImageFileURI!!, iv_opportunity_logo)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    // Validate user-entered opportunity details
    private fun validateOpportunityDetails(): Boolean {
        val opportunityNameEt: EditText = findViewById(R.id.opportunityNameEt)
        val opportunityDescriptionEt: EditText = findViewById(R.id.opportunityDescriptionEt)
        val opportunityLinkEt: EditText = findViewById(R.id.opportunityLinkEt)

        // Check if an image has been selected
        return when {
            mSelectedImageFileURI == null -> {
                Toast.makeText(this, "You have not selected a logo", Toast.LENGTH_LONG).show()
                false
            }

            // Check if opportunity name field is empty
            TextUtils.isEmpty(opportunityNameEt.text.toString().trim()) -> {
                Toast.makeText(this, "Opportunity name cannot be empty", Toast.LENGTH_LONG).show()
                false
            }

            // Check if opportunity description field is empty
            TextUtils.isEmpty(opportunityDescriptionEt.text.toString().trim()) -> {
                Toast.makeText(this, "Opportunity description cannot be empty", Toast.LENGTH_LONG).show()
                false
            }

            // Check if opportunity link field is empty
            TextUtils.isEmpty(opportunityLinkEt.text.toString().trim()) -> {
                Toast.makeText(this, "Opportunity link cannot be empty", Toast.LENGTH_LONG).show()
                false
            }
            else -> true
        }
    }

    // Upload the opportunity logo to cloud storage
    private fun uploadOpportunityLogo() {
        FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileURI, Constants.OPPORTUNITY_LOGO)
    }

    // Called in uploadImageToCloudStorage() function when image upload is successful
    fun imageUploadSuccess(imageURL:String) {
        mOpportunityImageURL = imageURL
        uploadProductDetails()
    }

    // Upload opportunity details to Firestore database
    private fun uploadProductDetails() {
        val username = this.getSharedPreferences(Constants.PONTIS_PREFERENCES, Context.MODE_PRIVATE).getString(Constants.LOGGED_IN_USERNAME,"")!!

        val opportunityNameEt: EditText = findViewById(R.id.opportunityNameEt)
        val opportunityDescriptionEt: EditText = findViewById(R.id.opportunityDescriptionEt)
        val opportunityLinkEt: EditText = findViewById(R.id.opportunityLinkEt)
        val schoolYearSpinner = findViewById<Spinner>(R.id.schoolyearspinner)
        val homeCitySpinner = findViewById<Spinner>(R.id.homecityspinner)
        val industrySpinner = findViewById<Spinner>(R.id.industryspinner)
        val typeSpinner = findViewById<Spinner>(R.id.typespinner)

        // Create an opportunity object with user-entered details
        val opportunity = Opportunity(
            FirestoreClass().getCurrentUserID(),
            username,
            opportunityNameEt.text.toString().trim(),
            opportunityDescriptionEt.text.toString().trim(),
            opportunityLinkEt.text.toString().trim(),
            homeCitySpinner.selectedItem.toString(),
            schoolYearSpinner.selectedItem.toString(),
            industrySpinner.selectedItem.toString(),
            typeSpinner.selectedItem.toString(),
            mOpportunityImageURL
        )

        // Upload the opportunity details to Firestore database
        FirestoreClass().uploadOpportunityDetails(this,opportunity)
    }

    // Called in uploadOpportunityDetails() function when opportunity upload is successful
    fun opportunityUploadSuccess() {
        Toast.makeText(this, "Opportunity Added", Toast.LENGTH_LONG).show()
        finish()
    }
}