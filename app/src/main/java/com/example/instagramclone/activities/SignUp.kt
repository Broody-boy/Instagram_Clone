package com.example.instagramclone.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.instagramclone.MainActivity
import com.example.instagramclone.R
import com.example.instagramclone.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.btnSignUp.setOnClickListener {

            if(binding.etName.text.isNotEmpty() && binding.etEmailNumber.text.isNotEmpty() && binding.etPassword.text.isNotEmpty()){
                val name = binding.etName.text.toString()
                val email = binding.etEmailNumber.text.toString()
                val password = binding.etPassword.text.toString()
                val intent = Intent(this, UserName::class.java)
                intent.putExtra("name", name)
                intent.putExtra("email", email)
                intent.putExtra("password", password)
                startActivity(intent)
            }
            else if (binding.etEmailNumber.text.isEmpty() || binding.etPassword.text.isEmpty() || binding.etName.text.isEmpty()) {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvLogInRedirect.setOnClickListener {
            finish()
        }
    }






    }
