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
    private lateinit var auth: FirebaseAuth
    private lateinit var pd: ProgressDialog

    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)

        setContentView(binding.root)


        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        pd = ProgressDialog(this)

        binding.btnSignUp.setOnClickListener {

            if(binding.etName.text.isNotEmpty() && binding.etEmailNumber.text.isNotEmpty() && binding.etPassword.text.isNotEmpty()){
                val name = binding.etName.text.toString()
                val email = binding.etEmailNumber.text.toString()
                val password = binding.etPassword.text.toString()
                signup(name,email,password)
            }
            else if (binding.etEmailNumber.text.isEmpty() || binding.etPassword.text.isEmpty() || binding.etName.text.isEmpty()) {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
            }
        }



        binding.tvLogInRedirect.setOnClickListener {
            finish()
        }
    }

    private fun signup(name: String, email: String, password: String) {


        pd.show()
        pd.setMessage("Signing Up")

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {

            if (it.isSuccessful){





                val user = auth.currentUser
                val hashMap = hashMapOf("userid" to user!!.uid,
                    "image" to "https://cdn2.iconfinder.com/data/icons/instagram-outline/19/11-512.png",
                    "email" to email,
                    "followers" to 0,
                    "following" to 8)

                firestore.collection("Users").document (user.uid).set(hashMap)

                pd.dismiss()
                Toast.makeText(this, "Sign In successful!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))

            }

        }.addOnFailureListener {
            pd.dismiss()
            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            return@addOnFailureListener
        }
    }




    }
