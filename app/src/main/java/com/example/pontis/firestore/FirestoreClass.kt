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
    // create a new instance of Firebase Firestore
    private val mFireStore = FirebaseFirestore.getInstance()

    // function to register user in Firebase Firestore
    fun registerUser(activity: SignUpActivity, userInfo: User) {
        // the "users" is the name of the collection. If the collection is already created (in database) then it will not create the same collection
        mFireStore.collection(Constants.USERS)
            // set the document id to the user id
            .document(userInfo.id)
            // merge the data if the user details already exist
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                // handle success case here
            }
    }

    // function to get the current user's ID
    fun getCurrentUserID(): String {
        // get an instance of the current user using Firebase Auth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // variable to assign the currentUserId if it is not null or else it will be blank
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    // function to get user details from Firebase Firestore
    fun getUserDetails(activity: Activity) {
        // pass collection name where we want to retrieve data
        mFireStore.collection(Constants.USERS)
            // document id to get the fields of user
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())
                // convert document snapshot to user data model object
                val user = document.toObject(User::class.java)

                // create a new instance of shared preferences
                val sharedPreferences = activity.getSharedPreferences(
                    Constants.PONTIS_PREFERENCES,
                    // makes sure data is only accessible within the app
                    Context.MODE_PRIVATE
                )
                // create a new editor object
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                // set the logged-in username key-value pair
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user?.firstName} ${user?.lastName}"
                )
                // apply the changes to the shared preferences
                editor.apply()
                // handle success case depending on the activity
                when (activity) {
                    is SignInActivity -> {
                        if (user != null) {
                            activity.userLoggedInSuccess(user)
                        }
                    }
                }

            }
            .addOnFailureListener { e ->
                // handle failure case here
                Log.e(activity.javaClass.simpleName, "error whilst getting users details.", e)
            }
    }

    // Update user profile data in Firestore
    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        // Update document with the current user's ID
        mFireStore.collection(Constants.USERS).document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                // Success case for updating user profile
                when (activity) {
                    is OnboardingActivity -> {
                        activity.userProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                // Failure case for updating user profile
                Log.e(
                    activity.javaClass.simpleName,
                    "error while updating user details",
                    e
                )
            }
    }

    // Upload image to Cloud Storage and return the download URL
    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String) {
        // Create a reference to the Firebase storage bucket location
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "." + Constants.getFileExtension(
                activity,
                imageFileURI
            )
        )
        sRef.putFile(imageFileURI!!)
            .addOnSuccessListener { taskSnapshot ->
                // Image upload is successful
                Log.e(
                    "Firebase Image URL", taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                    Log.e("Downloadable Image URL", uri.toString())
                    // Success case for uploading image to Cloud Storage
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
                // Failure case for uploading image to Cloud Storage
                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }

    // Uploads opportunity details to Firestore database
    fun uploadOpportunityDetails(activity: AddOpportunityActivity, opportunityInfo: Opportunity) {
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

    // Gets list of opportunities from Firestore database
    fun getOpportunityList(fragment: Fragment) {
        mFireStore.collection(Constants.OPPORTUNITY)
            .get()
            .addOnSuccessListener { document ->
                Log.e("Opportunity List", document.documents.toString())
                val opportunityList: ArrayList<Opportunity> = ArrayList()
                for (i in document.documents) {
                    val opportunity = i.toObject(Opportunity::class.java)
                    opportunity!!.opportunity_id = i.id
                    opportunityList.add(opportunity)
                }
                when(fragment) {
                    is DiscoverFragment -> {
                        fragment.successOpportunityListFromFireStore(opportunityList)
                    }
                }
            }
    }

    // Gets opportunity details from Firestore database
    fun getOpportunityDetails(activity: OpportunityDetailsActivity, opportunityId: String) {
        mFireStore.collection(Constants.OPPORTUNITY)
            .document(opportunityId)
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.toString())
                // Create opportunity object
                val opportunity = document.toObject(Opportunity::class.java)
                if (opportunity != null) {
                    activity.opportunityDetailsSuccess(opportunity)
                }
            }
            .addOnFailureListener { e->
                Log.e(activity.javaClass.simpleName, "Error while getting opportunity details", e)
            }
    }

    // This function adds a FollowItem to the Firestore collection.
    fun addFollowItems(activity: OpportunityDetailsActivity, addToFollow: FollowItem){
        // Get the Firestore collection for follow items and add the FollowItem with merge options.
        mFireStore.collection(Constants.FOLLOW_ITEMS)
            .document()
            .set(addToFollow, SetOptions.merge())
            .addOnSuccessListener {
                // If the addition was successful, call the "addToFollowListSuccess" function in the activity.
                activity.addToFollowListSuccess()
            }
            .addOnFailureListener{e->
                // If the addition was not successful, log an error.
                Log.e(activity.javaClass.simpleName, "error creating follow item",e)
            }
    }

    // This function retrieves the follow list for the current user.
    fun getFollowList(activity: Activity){
        // Get the Firestore collection for follow items where the user ID matches the current user's ID.
        mFireStore.collection(Constants.FOLLOW_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener {document->
                // If the retrieval was successful, log the documents and create a list of FollowItems.
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                val list:ArrayList<FollowItem> = ArrayList()
                for (i in document.documents){
                    val followItem = i.toObject(FollowItem::class.java)!!
                    followItem.id = i.id
                    list.add(followItem)
                }
                // Depending on the activity that called this function, call the appropriate success function with the list.
                when (activity){
                    is FollowListActivity ->{
                        activity.successFollowItemList(list)
                    }
                }
            }
            .addOnFailureListener {e->
                // If the retrieval was not successful, log an error.
                Log.e(activity.javaClass.simpleName, "Error while getting the cart list items", e)
            }
    }

    // This function removes a follow from the Firestore collection.
    fun removeFollow(context: Context, item_id: String){
        // Get the Firestore collection for follow items and delete the item with the given ID.
        mFireStore.collection(Constants.FOLLOW_ITEMS)
            .document(item_id)
            .delete()
            .addOnSuccessListener {
                // If the deletion was successful, call the appropriate success function based on the context.
                when(context){
                    is FollowListActivity ->{
                        context.followRemovedSuccess()
                    }
                }
            }
            .addOnFailureListener {
                // If the deletion was not successful, do nothing.
            }
    }
}