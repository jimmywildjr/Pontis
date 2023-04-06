package com.example.pontis.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.pontis.R
import com.example.pontis.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // initialise binding variable
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // load main app theme
        setTheme(R.style.Theme_Pontis)
        super.onCreate(savedInstanceState)
        // initialise binding with the view using the binding object
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // this code is used when we navigate back from change details activity
        // if "fragmentName" extra is found, replace the current fragment with SettingsFragment
        // otherwise, replace with HomeFragment
        val fragmentName = intent.getStringExtra("fragmentName")
        if (fragmentName == "SettingsFragment") {
            replaceFragment(SettingsFragment())
        } else {
            replaceFragment(HomeFragment())
        }

        // when the bottomNavigationView items are clicked, it replaces the frameLayout with the fragment
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> replaceFragment(HomeFragment())
                R.id.discover -> replaceFragment(DiscoverFragment())
                R.id.resources -> replaceFragment(ResourcesFragment())
                R.id.news -> replaceFragment(NewsFragment())
                R.id.settings -> replaceFragment(SettingsFragment())
                else ->{
                }
            }
            true
        }
    }
    // this is the function called to replace the fragments
    // it takes in a fragment object and replaces the current fragment with it
    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.commit()
    }
}