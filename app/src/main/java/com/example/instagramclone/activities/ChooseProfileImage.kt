package com.example.instagramclone.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.instagramclone.databinding.ActivityChooseProfileImageBinding

class ChooseProfileImage : AppCompatActivity() {

    private lateinit var binding: ActivityChooseProfileImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseProfileImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}