package com.example.pontis.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {
    const val USERS: String = "users"
    const val PONTIS_PREFERENCES: String = "PontisPref"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"
    const val EXTRA_USER_DETAILS: String = "extra_user_details"
    const val READ_STORAGE_PERMISSION_CODE = 2
    const val IMAGE_REQUEST_CODE = 1
    const val MALE:String="Male"
    const val FEMALE:String="Female"
    const val GENDER:String="gender"
    const val SCHOOLYEAR:String="schoolYear"
    const val HOMECITY:String="homeCity"
    const val IMAGE:String="image"
    const val USER_PROFILE_IMAGE:String="User_Profile_Image"
    const val COMPLETE_PROFILE:String="profileCompleted"
    const val USER_ID: String = "user_id"

    const val OPPORTUNITY_LOGO: String = "Opportunity_Logo"
    const val OPPORTUNITY: String = "opportunity"

    const val EXTRA_OPPORTUNITY_ID: String="extra_opportunity_id"

    const val FOLLOW_ITEMS: String = "follow_items"

    fun showImageChooser(activity:Activity){
        // intent for launching image selection of phone storage
        val galleryIntent  = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        //laumches the image selection of phone storage
        activity.startActivityForResult(galleryIntent, IMAGE_REQUEST_CODE)
    }
    //gets extension of file
    fun getFileExtension(activity: Activity, uri: Uri?):String?{
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType((activity.contentResolver.getType(uri!!)))
    }

}