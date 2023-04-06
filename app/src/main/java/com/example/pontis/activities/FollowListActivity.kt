package com.example.pontis.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pontis.R
import com.example.pontis.adapters.FollowItemAdapter
import com.example.pontis.adapters.MyOpportunityListAdapter
import com.example.pontis.firestore.FirestoreClass
import com.example.pontis.models.FollowItem
import com.google.firebase.auth.FirebaseAuth

class FollowListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_follow_list)
    }

    // Function to handle success callback of FirestoreClass.getFollowList()
    fun successFollowItemList(followList: ArrayList<FollowItem>) {
        if (followList.size > 0) {
            // Get the RecyclerView object from the activity_follow_list.xml file
            val rv_follow_items_list = findViewById<RecyclerView>(R.id.rv_follow_items_list)
            // Set the layout manager for the RecyclerView
            rv_follow_items_list.layoutManager = LinearLayoutManager(this@FollowListActivity)
            rv_follow_items_list.setHasFixedSize(true)

            // Create a new FollowItemAdapter with the followList data and set it as the adapter for the RecyclerView
            val followItemAdapter = FollowItemAdapter(this@FollowListActivity, followList)
            rv_follow_items_list.adapter = followItemAdapter
        }
    }

    // Function to get the list of items the user is following and display it in the RecyclerView
    private fun getFollowItemList() {
        FirestoreClass().getFollowList(this@FollowListActivity)
    }

    // Function to refresh the RecyclerView when the activity is resumed
    override fun onResume() {
        super.onResume()
        getFollowItemList()
    }

    // Function called when the removeFollow function in FirestoreClass is successful
    fun followRemovedSuccess() {
        // Display a toast indicating that the item has been unfollowed
        Toast.makeText(this@FollowListActivity, "Unfollowed", Toast.LENGTH_SHORT).show()
        // Refresh the RecyclerView to remove the unfollowed item
        getFollowItemList()
    }
}