package com.example.instagramclone.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.instagramclone.MainActivity
import com.example.instagramclone.R
import com.example.instagramclone.databinding.ActivitySignUpBinding
import com.example.instagramclone.databinding.ActivityUserNameBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.sign

class UserName : AppCompatActivity() {

    private lateinit var binding: ActivityUserNameBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var pd: ProgressDialog
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserNameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.etUserName.setText("Anonymous_user")

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        pd = ProgressDialog(this)

        binding.btnNext.setOnClickListener {
            if (binding.etUserName.text.isNotEmpty()) {
                if (binding.etUserName.text.toString().contains(" ")){
                    binding.inputLayoutUserName.error = "Username cannot contain spaces."
                }
                else {
                    val name = intent.getStringExtra("name")!!
                    val email = intent.getStringExtra("email")!!
                    val password = intent.getStringExtra("password")!!
                    signup(name, email, password)
                }
            }
            else {
                binding.inputLayoutUserName.error = "Choose a username to continue."
            }
        }
    }

    private fun signup(name: String, email: String, password: String) {
        pd.show()
        pd.setMessage("Signing Up")

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            if (it.isSuccessful){
                val user = auth.currentUser
                val hashMap = hashMapOf("userid" to user!!.uid,
                    "name" to name,
                    "email" to email,
                    "username" to binding.etUserName.text.toString(),
                    "profile_image" to "https://cdn2.iconfinder.com/data/icons/instagram-outline/19/11-512.png",
                    "followers" to 0,
                    "following" to 0,
                    "posts_count" to 0)

                firestore.collection("Users").document (user.uid).set(hashMap)

                pd.dismiss()
                Toast.makeText(this, "Sign Up successful!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()

            }
        }.addOnFailureListener {
            pd.dismiss()
            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            return@addOnFailureListener
        }


    }

}