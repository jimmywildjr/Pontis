package com.example.pontis.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import com.example.pontis.R
import com.example.pontis.utils.Constants

// Define a fragment to show on home screen
class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable options menu in this fragment
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_home, container, false)

        // Get the username from shared preferences
        val sharedPreferences = requireContext().getSharedPreferences(Constants.PONTIS_PREFERENCES, Context.MODE_PRIVATE)
        val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME,"")!!

        // Set the greeting text with the username
        val tvHome: TextView = view.findViewById(R.id.tv_home)
        tvHome.text = "Hello $username"

        return view
    }

    // Create options menu in the fragment
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            R.id.action_favourite -> {
                // Open FollowListActivity when the user clicks on the Favorite icon
                startActivity(Intent(activity, FollowListActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}