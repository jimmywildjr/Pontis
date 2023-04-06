package com.example.pontis.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.pontis.R
import com.example.pontis.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    //initialise binding variable
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        //load main app theme
        setTheme(R.style.Theme_Pontis)
        super.onCreate(savedInstanceState)
        //initialise binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // this code is used when we navigate back from change details acttivity
        val fragmentName = intent.getStringExtra("fragmentName")
        if (fragmentName == "SettingsFragment") {
            replaceFragment(SettingsFragment())
        } else {
            replaceFragment(HomeFragment())
        }

        //when the bottomNavigationView items are clicked it replaces the framelayout with the fragment
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
    //this is the function called to replace the fragments
    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.commit()
    }
}