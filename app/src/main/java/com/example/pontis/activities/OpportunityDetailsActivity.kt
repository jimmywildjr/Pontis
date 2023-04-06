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
    private lateinit var binding: ActivityOpportunityDetailsBinding
    private var mOpportunityId: String = ""
    private lateinit var mOpportunityDetails: Opportunity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //initialise binding and firebase
        binding = ActivityOpportunityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnFollow.setOnClickListener(this)
        //gets the extra information from the intent to load
        if(intent.hasExtra(Constants.EXTRA_OPPORTUNITY_ID)){
            mOpportunityId = intent.getStringExtra(Constants.EXTRA_OPPORTUNITY_ID)!!
        }
        getOpportunityDetails()
    }
    private fun getOpportunityDetails(){
        FirestoreClass().getOpportunityDetails(this, mOpportunityId)
    }

    //on success loads the image and parameter
    fun opportunityDetailsSuccess(opportunity: Opportunity){
        mOpportunityDetails = opportunity
        val imageUri = Uri.parse(opportunity.image)
        GlideLoader(this@OpportunityDetailsActivity).loadOpportunityPicture(
            imageUri,
            binding.ivOpportunityImage
        )
        binding.tvOpportunityTitle.text = opportunity.title
        binding.tvOpportunitySchoolYear.text = opportunity.schoolYear
        binding.tvOpportunityHomeCity.text = opportunity.homeCity
        binding.tvOpportunityType.text = opportunity.type
        binding.tvOpportunityIndustry.text = opportunity.industry
        binding.tvOpportunityDescription.text = opportunity.description
        binding.tvOpportunityLink.text = opportunity.link
    }
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
    fun addToFollowListSuccess(){
        Toast.makeText(this, "Followed Opportunity", Toast.LENGTH_LONG).show()
    }

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