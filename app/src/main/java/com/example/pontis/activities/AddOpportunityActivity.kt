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
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.widget.*
import androidx.core.app.ActivityCompat
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_opportunity)

        val iv_add_update_opportunity = findViewById<ImageView>(R.id.iv_add_update_opportunity)
        val btn_submit_add_opportunity = findViewById<Button>(R.id.btn_submit_add_opportunity)
        iv_add_update_opportunity.setOnClickListener(this)
        btn_submit_add_opportunity.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v !=null){
            when(v.id){
                R.id.iv_add_update_opportunity ->{
                    //checks if permission has already been granted
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                        Constants.showImageChooser(this@AddOpportunityActivity)
                    }else{
                        //requests that permissions to be granted
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),Constants.READ_STORAGE_PERMISSION_CODE)

                    }
                }
                R.id.btn_submit_add_opportunity ->{
                    if (validateOpportunityDetails()){
                        uploadOpportunityLogo()

                    }
                }
            }
        }
    }
    //checking permissions
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
    //gets the result of the permisions check
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode== Constants.IMAGE_REQUEST_CODE){
                if (data != null){
                    val iv_add_update_opportunity = findViewById<ImageView>(R.id.iv_add_update_opportunity)

                    iv_add_update_opportunity.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_vector_edit_24))
                    mSelectedImageFileURI = data.data!!
                    try{
                        val iv_opportunity_logo = findViewById<ImageView>(R.id.iv_opportunity_logo)
                        GlideLoader(this).loadUserPicture(mSelectedImageFileURI!!, iv_opportunity_logo)
                    } catch(e: IOException){
                        e.printStackTrace()
                    }
                }
            }
        }
    }
    private fun validateOpportunityDetails(): Boolean {
        val opportunityNameEt: EditText = findViewById(R.id.opportunityNameEt)
        val opportunityDescriptionEt: EditText = findViewById(R.id.opportunityDescriptionEt)
        val opportunityLinkEt: EditText = findViewById(R.id.opportunityLinkEt)
        return when {
            mSelectedImageFileURI == null -> {
                Toast.makeText(this, "You have not selected a logo", Toast.LENGTH_LONG).show()
                false
            }
            TextUtils.isEmpty(opportunityNameEt.text.toString().trim()) -> {
                Toast.makeText(this, "Opportunity name cannot be empty", Toast.LENGTH_LONG).show()
                false
            }
            TextUtils.isEmpty(opportunityDescriptionEt.text.toString().trim()) -> {
                Toast.makeText(this, "Opportunity description cannot be empty", Toast.LENGTH_LONG).show()
                false
            }
            TextUtils.isEmpty(opportunityLinkEt.text.toString().trim()) -> {
                Toast.makeText(this, "Opportunity link cannot be empty", Toast.LENGTH_LONG).show()
                false
            }
            else -> true
        }
    }
    private fun uploadOpportunityLogo(){
        FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileURI, Constants.OPPORTUNITY_LOGO)
    }

    //called in upload image to cloud storage function
    fun imageUploadSuccess(imageURL:String){
        mOpportunityImageURL = imageURL
        uploadProductDetails()
    }

    private fun uploadProductDetails(){
        val username = this.getSharedPreferences(Constants.PONTIS_PREFERENCES, Context.MODE_PRIVATE).getString(Constants.LOGGED_IN_USERNAME,"")!!
        val opportunityNameEt: EditText = findViewById(R.id.opportunityNameEt)
        val opportunityDescriptionEt: EditText = findViewById(R.id.opportunityDescriptionEt)
        val opportunityLinkEt: EditText = findViewById(R.id.opportunityLinkEt)
        val schoolYearSpinner = findViewById<Spinner>(R.id.schoolyearspinner)
        val homeCitySpinner = findViewById<Spinner>(R.id.homecityspinner)
        val industrySpinner = findViewById<Spinner>(R.id.industryspinner)
        val typeSpinner = findViewById<Spinner>(R.id.typespinner)

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
        FirestoreClass().uploadOpportunityDetails(this,opportunity)
    }
    fun opportunityUploadSuccess(){
        Toast.makeText(this, "Opportunity Added", Toast.LENGTH_LONG).show()
        finish()
    }
}