package com.example.instagramclone

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.instagramclone.activities.NewPostStory
import com.example.instagramclone.databinding.ActivityMainBinding
import com.example.instagramclone.ui.home.HomeFragment
import com.example.instagramclone.ui.profile.ProfileFragment
import com.example.instagramclone.ui.explore.ExploreFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navViewBottom
        val homefragment = HomeFragment()
        val profilefragment = ProfileFragment()
        val explorefragment = ExploreFragment()

        setFragment(homefragment)

        navView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> {
                    binding.toolbar.visibility = View.VISIBLE
                    setFragment(homefragment)
                }
                R.id.explore -> {
                    binding.toolbar.visibility = View.GONE
                    setFragment(explorefragment)
                }
                R.id.plus -> {
                    val intent = Intent(this, NewPostStory::class.java)
                    startActivity(intent)
                }
                R.id.reels -> {
                    Toast.makeText(this, "No reels here yet",Toast.LENGTH_SHORT).show()
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