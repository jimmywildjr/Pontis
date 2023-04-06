package com.example.pontis.firestore

import android.app.Activity
import android.util.Log
import android.widget.Toast
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import com.example.pontis.activities.*
import com.example.pontis.models.FollowItem
import com.example.pontis.models.Opportunity
import com.example.pontis.models.User
import com.example.pontis.utils.Constants
import com.example.pontis.utils.GlideLoader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()
    //used in signupactivty to create user document in firestore
    fun registerUser(activity: SignUpActivity, userInfo: User) {
        // the "users" is the name of the collection. If the collection is already created (in database) then it will not create the same collection"
        mFireStore.collection(Constants.USERS)
            // id for document is the user id
            .document(userInfo.id)
            //merge the data if the user details already exist
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {

            }
    }

    fun getCurrentUserID(): String {
        // an instance of currentUser using firebase auth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // variable to assign the currentUserId if it is not null or else it will be blank
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun getUserDetails(activity: Activity) {
        //pass collection name where we want to retrieve data
        mFireStore.collection(Constants.USERS)
            // document id to get the fields of user
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())
                //convert document snapshot to user data model object
                val user = document.toObject(User::class.java)

                val sharedPreferences =
                    activity.getSharedPreferences(
                        Constants.PONTIS_PREFERENCES,
                        //makes sure data is only accessible within the app
                        Context.MODE_PRIVATE
                    )
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(
                    // key value pair
                    Constants.LOGGED_IN_USERNAME,
                    "${user?.firstName} ${user?.lastName}"
                )
                editor.apply()
                when (activity) {
                    is SignInActivity -> {
                        if (user != null) {
                            activity.userLoggedInSuccess(user)
                        }
                    }
                }

            }
            .addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, "error whilst getting users details.", e)
            }

    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection((Constants.USERS)).document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                when (activity) {
                    is OnboardingActivity -> {
                        activity.userProfileUpdateSuccess()
                    }
                }

            }
            .addOnFailureListener { e ->
                Log.e(
                    activity.javaClass.simpleName, "error while updating user details", e
                )
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String) {
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "." + Constants.getFileExtension(
                activity,
                imageFileURI
            )
        )
        sRef.putFile(imageFileURI!!)
            //image upload is successful
            .addOnSuccessListener { taskSnapshot ->
                Log.e(
                    "Firebase Image URL", taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                    Log.e("Downloadable Image URL", uri.toString())
                    when (activity) {
                        is OnboardingActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }
                        is AddOpportunityActivity ->{
                            activity.imageUploadSuccess(uri.toString())
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }

    fun uploadOpportunityDetails(activity: AddOpportunityActivity, opportunityInfo: Opportunity){
        mFireStore.collection(Constants.OPPORTUNITY)
            .document()
            .set(opportunityInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.opportunityUploadSuccess()
            }
            .addOnFailureListener { e->
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while uploading the opportunity details",
                    e
                )
            }
    }
    //function to get opportunities
    fun getOpportunityList(fragment: Fragment){
        mFireStore.collection(Constants.OPPORTUNITY)
            .get()
            .addOnSuccessListener { document ->
                Log.e("Opportunity List", document.documents.toString())
                val opportunityList: ArrayList<Opportunity> = ArrayList()
                for (i in document.documents){
                    val opportunity = i.toObject(Opportunity::class.java)
                    opportunity!!.opportunity_id = i.id

                    opportunityList.add(opportunity)
                }
                when(fragment){
                    is DiscoverFragment ->{
                        fragment.successOpportunityListFromFireStore(opportunityList)
                    }
                }
            }
    }
    //gets opportunity details
    fun getOpportunityDetails(activity: OpportunityDetailsActivity, opportunityId: String){
        mFireStore.collection(Constants.OPPORTUNITY)
            .document(opportunityId)
            .get()
            .addOnSuccessListener {document ->
                Log.e(activity.javaClass.simpleName,document.toString())
                //create opportunity object
                val opportunity = document.toObject(Opportunity::class.java)
                if (opportunity != null) {
                    activity.opportunityDetailsSuccess(opportunity)
                }
            }
            .addOnFailureListener{e->
                Log.e(activity.javaClass.simpleName, "error while getting opportunity details",e)
            }

    }
    fun addFollowItems(activity: OpportunityDetailsActivity, addToFollow: FollowItem){
        mFireStore.collection(Constants.FOLLOW_ITEMS)
            .document()
                //merges
            .set(addToFollow, SetOptions.merge())
            .addOnSuccessListener {
                activity.addToFollowListSuccess()
            }
            .addOnFailureListener{e->
                Log.e(activity.javaClass.simpleName, "error creating follow item",e)
            }
    }
    fun getFollowList(activity: Activity){
        //gets followed opportities for the user who is signed in
        mFireStore.collection(Constants.FOLLOW_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener {document->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                val list:ArrayList<FollowItem> = ArrayList()
                for (i in document.documents){
                    val followItem = i.toObject(FollowItem::class.java)!!
                    followItem.id = i.id
                    list.add(followItem)
                }
                when (activity){
                    is FollowListActivity ->{
                        activity.successFollowItemList(list)
                    }
                }
            }
            .addOnFailureListener {e->
                Log.e(activity.javaClass.simpleName, "Error while getting the cart list items", e)
            }
    }
    //called in the adapter to remove the follow
    fun removeFollow(context: Context, item_id: String){
        mFireStore.collection(Constants.FOLLOW_ITEMS)
            .document(item_id)
            .delete()
            .addOnSuccessListener {
                when(context){
                    is FollowListActivity ->{
                        context.followRemovedSuccess()
                    }
                }
            }
            .addOnFailureListener {

            }
    }
}