package com.example.pontis.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.pontis.R
import java.io.IOException

// This class uses the Glide library to load images into ImageViews for users and opportunities.
class GlideLoader(val context: Context) {

    // This function loads a user's picture into an ImageView.
    fun loadUserPicture(imageURI: Uri, imageView: ImageView){
        try{
            // Use Glide to load the image from the given URI into the ImageView, with center crop and a default placeholder.
            Glide
                .with(context)
                .load(imageURI)
                .centerCrop()
                .placeholder(R.drawable.ic_user_placeholder)
                .into(imageView)
        }catch(e: IOException){
            // If there is an error, print the stack trace.
            e.printStackTrace()
        }
    }

    // This function loads an opportunity's picture into an ImageView.
    fun loadOpportunityPicture(imageURI: Uri, imageView: ImageView){
        try{
            // Use Glide to load the image from the given URI into the ImageView, with center crop.
            Glide
                .with(context)
                .load(imageURI)
                .centerCrop()
                .into(imageView)
        }catch(e: IOException){
            // If there is an error, print the stack trace.
            e.printStackTrace()
        }
    }
}