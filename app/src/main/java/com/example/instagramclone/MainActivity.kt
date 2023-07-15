package com.example.instagramclone

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.instagramclone.databinding.ActivityMainBinding
import com.example.instagramclone.ui.home.HomeFragment
import com.example.instagramclone.ui.profile.ProfileFragment
import com.example.instagramclone.ui.search.SearchFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val homefragment = HomeFragment()
        val profilefragment = ProfileFragment()
        val searchfragment = SearchFragment()

        navView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> {
                    setFragment(homefragment)
                }
                R.id.search -> {
                    setFragment(searchfragment)
                }
                R.id.profile -> {
                    setFragment(profilefragment)
                }
            }
            true
        }
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {

            replace(R.id.nav_host_fragment_activity_main, fragment)
            commit()
        }
    }
}