package com.example.pontis.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.pontis.R
import com.example.pontis.databinding.ActivityChangeDetailsBinding
import com.example.pontis.databinding.ActivityOpportunityDetailsBinding
import com.example.pontis.firestore.FirestoreClass
import com.example.pontis.models.FollowItem
import com.example.pontis.models.Opportunity
import com.example.pontis.utils.Constants
import com.example.pontis.utils.GlideLoader

class OpportunityDetailsActivity : AppCompatActivity(), View.OnClickListener {
    // Declaring private variables
    private lateinit var binding: ActivityOpportunityDetailsBinding
    private var mOpportunityId: String = ""
    private lateinit var mOpportunityDetails: Opportunity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the activity layout using view binding
        binding = ActivityOpportunityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Set the click listener for the "Follow" button
        binding.btnFollow.setOnClickListener(this)
        // Retrieve the Opportunity ID passed as an extra in the intent
        if(intent.hasExtra(Constants.EXTRA_OPPORTUNITY_ID)){
            mOpportunityId = intent.getStringExtra(Constants.EXTRA_OPPORTUNITY_ID)!!
        }
        // Fetch the opportunity details from Firestore
        getOpportunityDetails()
    }

    // Fetch the opportunity details from Firestore
    private fun getOpportunityDetails(){
        FirestoreClass().getOpportunityDetails(this, mOpportunityId)
    }

    // Callback method called when opportunity details are successfully retrieved
    fun opportunityDetailsSuccess(opportunity: Opportunity){
        // Store the opportunity details in a member variable
        mOpportunityDetails = opportunity
        // Load the opportunity image into the ImageView using Glide
        val imageUri = Uri.parse(opportunity.image)
        GlideLoader(this@OpportunityDetailsActivity).loadOpportunityPicture(
            imageUri,
            binding.ivOpportunityImage
        )
        // Set the opportunity details in the appropriate TextViews
        binding.tvOpportunityTitle.text = opportunity.title
        binding.tvOpportunitySchoolYear.text = opportunity.schoolYear
        binding.tvOpportunityHomeCity.text = opportunity.homeCity
        binding.tvOpportunityType.text = opportunity.type
        binding.tvOpportunityIndustry.text = opportunity.industry
        binding.tvOpportunityDescription.text = opportunity.description
        binding.tvOpportunityLink.text = opportunity.link
    }

    // Add the opportunity to the user's follow list
    private fun addToFollowList(){
        val followItem = FollowItem(
            FirestoreClass().getCurrentUserID(),
            mOpportunityId,
            mOpportunityDetails.title,
            mOpportunityDetails.schoolYear,
            mOpportunityDetails.homeCity,
            mOpportunityDetails.industry,
            mOpportunityDetails.type,
            mOpportunityDetails.image,
        )
        FirestoreClass().addFollowItems(this, followItem)
    }

    // Callback method called when the opportunity is successfully added to the user's follow list
    fun addToFollowListSuccess(){
        Toast.makeText(this, "Followed Opportunity", Toast.LENGTH_LONG).show()
    }

    // Handle clicks on views in the activity
    override fun onClick(v: View?) {
        if(v!=null){
            when(v.id){
                R.id.btn_follow ->{
                    addToFollowList()
                }
            }
        }
    }
}