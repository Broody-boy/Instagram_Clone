package com.example.instagramclone

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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

        val navView: BottomNavigationView = binding.navViewBottom
        val homefragment = HomeFragment()
        val profilefragment = ProfileFragment()
        val searchfragment = SearchFragment()

        navView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> {
                    binding.toolbar.visibility = View.VISIBLE
                    setFragment(homefragment)
                }
                R.id.search -> {
                    binding.toolbar.visibility = View.GONE
                    setFragment(searchfragment)
                }
                R.id.reels -> {
                    binding.toolbar.visibility = View.GONE
                }
                R.id.profile -> {
                    binding.toolbar.visibility = View.GONE
                    setFragment(profilefragment)
                }
            }
            true
        }
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {

            replace(R.id.fragment_holder, fragment)
            commit()
        }
    }
}