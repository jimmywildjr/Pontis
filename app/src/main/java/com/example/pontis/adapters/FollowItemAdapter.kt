package com.example.pontis.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pontis.R
import com.example.pontis.activities.FollowListActivity
import com.example.pontis.activities.OpportunityDetailsActivity
import com.example.pontis.firestore.FirestoreClass
import com.example.pontis.models.FollowItem
import com.example.pontis.utils.Constants
import com.example.pontis.utils.GlideLoader

open class FollowItemAdapter (
    private val context: Context,
    private var list: ArrayList<FollowItem>

):RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    //creates the list items
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FollowItemAdapter.MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_follow_layout,
                parent,
                false
            )
        )
    }
    //tells the adapter how many items to return
    override fun getItemCount(): Int {
        return list.size
    }
    //binds the list items
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder) {
            val imageUri = Uri.parse(model.image) // Convert the image URL String to a Uri
            GlideLoader(context).loadOpportunityPicture(imageUri, holder.itemView.findViewById(R.id.iv_follow_item_image))
            holder.itemView.findViewById<TextView>(R.id.tv_follow_item_name).text = model.title
            holder.itemView.findViewById<TextView>(R.id.tv_follow_item_homecity).text = model.homeCity
            holder.itemView.findViewById<TextView>(R.id.tv_follow_item_industry).text = model.industry

        }
        holder.itemView.findViewById<ImageButton>(R.id.delete_button).setOnClickListener{
            //calls the function to remove the follow passing in the id of the opportunity to be removed
            FirestoreClass().removeFollow(context, model.id)
        }
    }
    private class MyViewHolder(view: View):RecyclerView.ViewHolder(view)
}