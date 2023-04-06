package com.example.pontis.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.pontis.R
import java.io.IOException

class GlideLoader(val context: Context) {
    fun loadUserPicture(imageURI: Uri, imageView: ImageView){
        try{
            //load user image into imageview
            Glide
                .with(context)
                .load(imageURI)
                .centerCrop() //scale image
                .placeholder(R.drawable.ic_user_placeholder) //default placeholder if image fails to load
                .into(imageView)
        }catch(e: IOException){
            e.printStackTrace()
        }
    }
    fun loadOpportunityPicture(imageURI: Uri, imageView: ImageView){
        try{
            //load user image into imageview
            Glide
                .with(context)
                .load(imageURI)
                .centerCrop() //scale image
                .into(imageView)
        }catch(e: IOException){
            e.printStackTrace()
        }
    }
}