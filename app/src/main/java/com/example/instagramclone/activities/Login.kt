package com.example.instagramclone.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.instagramclone.MainActivity
import com.example.instagramclone.R
import com.example.instagramclone.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var pd: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        auth = FirebaseAuth.getInstance()

        binding.btnCreateNewAccountRedirect.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        if (auth.currentUser!=null) {
            startActivity(Intent(this, MainActivity::class.java))
        }

        pd = ProgressDialog(this)

        binding.btnLogIn.setOnClickListener {
            if (binding.etEmailNumberUserName.text.isNotEmpty() && binding.etPassword.text.isNotEmpty()) {
                val email = binding.etEmailNumberUserName.text.toString()
                val password = binding.etPassword.text.toString()

                signIn(email, password)
            }
            else if (binding.etEmailNumberUserName.text.isEmpty() || binding.etPassword.text.isEmpty()) {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signIn(email: String, password: String) {
        pd.show()
        pd.setMessage("Signing In")
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { SignInTask->
            if (SignInTask.isSuccessful) {
                startActivity(Intent(this, MainActivity::class.java))
                Toast.makeText(this, "Sign In successful!", Toast.LENGTH_SHORT).show()
                pd.dismiss()
            }
        }.addOnFailureListener {
            pd.dismiss()
            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            return@addOnFailureListener
        }
    }
}