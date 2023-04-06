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
    fun successFollowItemList(followList: ArrayList<FollowItem>){
        if (followList.size > 0){
            val rv_follow_items_list = findViewById<RecyclerView>(R.id.rv_follow_items_list)
            rv_follow_items_list.layoutManager = LinearLayoutManager(this@FollowListActivity)
            rv_follow_items_list.setHasFixedSize(true)

            val followItemAdapter = FollowItemAdapter(this@FollowListActivity, followList)
            rv_follow_items_list.adapter = followItemAdapter
        }
    }
    private fun getFollowItemList(){
        FirestoreClass().getFollowList(this@FollowListActivity)
    }

    override fun onResume() {
        super.onResume()
        getFollowItemList()
    }
    //called once the removeFollow function in firestore class is successful
    fun followRemovedSuccess(){
        Toast.makeText(this@FollowListActivity, "Unfollowed", Toast.LENGTH_SHORT).show()
        //reloads list with the unfollowed item removed
        getFollowItemList()
    }


}