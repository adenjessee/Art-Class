
package com.example.artclass

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.artclass.fragments.HomeFragment
import com.example.artclass.fragments.SettingsFragment
import com.example.artclass.fragments.PhotosFragment
import com.example.artclass.fragments.ScoreboardFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth.getInstance

class MainActivity : AppCompatActivity(){

    // navigation menu fragments
    private val homeFragment = HomeFragment()
    private val photosFragment = PhotosFragment()
    private val scoreboardFragment = ScoreboardFragment()
    private val settingsFragment = SettingsFragment()

    override fun onCreate(savedInstanceState: Bundle?){

        // the bar at the top is not needed
        supportActionBar?.hide()

        // lets make the activity screen the login page
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // preventing anything bad from happening with login
        verifyUserIsLoggedIn()

        // navigation menu assign the fragments
        makeCurrentFragment(homeFragment)

        // make the bottom navigation
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.ic_home->makeCurrentFragment(homeFragment)
                R.id.ic_photos->makeCurrentFragment(photosFragment)
                R.id.ic_leaderboard->makeCurrentFragment(scoreboardFragment)
                R.id.ic_options->makeCurrentFragment(settingsFragment)
            }
            true
        }
    }

    // assign the current fragment to fragment passed in
    private fun makeCurrentFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fl_wrapper, fragment)
        transaction.commit()
    }

    // lets make sure the user is actually signed in to be looking at the login page
    private fun verifyUserIsLoggedIn() {
        val uid = getInstance().uid
        if (uid == null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}