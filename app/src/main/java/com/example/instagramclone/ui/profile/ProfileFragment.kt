package com.example.instagramclone.ui.profile

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Debug
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.instagramclone.R
import com.example.instagramclone.activities.Login
import com.example.instagramclone.Utils
import com.example.instagramclone.adapters.MyPostAdapter
import com.example.instagramclone.databinding.FragmentProfileBinding
import com.example.instagramclone.mvvm.ViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {


    private lateinit var binding : FragmentProfileBinding
    private lateinit var vm : ViewModel
    private lateinit var storageRef : StorageReference
    lateinit var storage : FirebaseStorage

    lateinit var profileBitmap : Bitmap
    var uriProfile : Uri? = null

    private lateinit var pd : ProgressDialog
    private lateinit var firestore : FirebaseFirestore
    private lateinit var adapter : MyPostAdapter
    private lateinit var imageView : CircleImageView

    private lateinit var editText : EditText
    lateinit var fbauth : FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm = ViewModelProvider(this).get(ViewModel::class.java)

        fbauth = FirebaseAuth.getInstance()

        binding.viewModel = vm
        binding.lifecycleOwner = viewLifecycleOwner

        pd = ProgressDialog(requireContext())

        adapter = MyPostAdapter()
        firestore = FirebaseFirestore.getInstance()

        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference


        binding.imgOptionsMenu.setOnClickListener {
            //add menu here and within that, signout
            fbauth.signOut()
            val intent = Intent(requireContext(), Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK //clear call stack
            startActivity(intent)
            requireActivity().finish() //clear current activity(The one which fragment is a part of)
        }

        binding.btnAddFriends.setOnClickListener {
            //implement followers, following fragment
        }

        binding.imgAddPost.setOnClickListener {
            //implement post activity
        }

        vm.name.observe(viewLifecycleOwner, Observer {
            binding.tvUserName.text = it!!
        })

        vm.image.observe(viewLifecycleOwner, Observer {
            Glide.with(requireContext()).load(it).into(binding.imgProfile)
            //Glide.with(requireContext()).load(it).into(BottomNavBar.imgProfile)
        })



        vm.getMyPosts().observe(viewLifecycleOwner, Observer {
            //binding.postsCountText.setText(it.size.toString())
            //adapter.setPostList(it)
        })

        //binding.imagesRecycler.adapter = adapter

        binding.btnEditProfile.setOnClickListener {
            //layout for edit profile -> custom_dialog_layout
            val customView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_dialog_layout, null)
            //image on edit profile page
            imageView = customView.findViewById<CircleImageView>(R.id.imgUserProfile)
            //username on edit profile page
            editText = customView.findViewById<EditText>(R.id.eTUserName)

            vm.name.observe(viewLifecycleOwner, Observer {
                editText.setText(it)
            })

            vm.image.observe(viewLifecycleOwner, Observer {
                Glide.with(requireContext()).load(it).into(imageView)
            })

            imageView.setOnClickListener {
                alertDialogProfile()
            }

            val dialog = AlertDialog.Builder(requireContext())
                .setTitle("Edit Profile")
                .setView(customView)
                .setPositiveButton("Done") { dialog, which ->
                    val inputText = editText.text.toString()
                    firestore.collection("Users").document(Utils.getUidLoggedIn())
                        .update("username", inputText.toString())
                    val collectionref = firestore.collection("Posts")
                    val query = collectionref.whereEqualTo("userid", Utils.getUidLoggedIn())

                    query.get().addOnSuccessListener { documents ->
                        for (document in documents) {
                            collectionref.document(document.id).update("username", inputText)
                        }
                    }
                }
                .setNegativeButton("Cancel", null)
                .create()
            dialog.show()
        }
    }

    //same for create post
    private fun alertDialogProfile() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choose profile picture")
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Take Photo" -> {
                    profileImageWithCamera()
                }options[item] == "Choose from Gallery" -> {
                profileImageFromGallery()
            }
                options[item] == "Cancel" -> dialog.dismiss()
            }
        }
        builder.show()
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun profileImageFromGallery() {
        val pickImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (pickImageIntent.resolveActivity(requireActivity().packageManager)!=null) {
            startActivityForResult(pickImageIntent, Utils.PROFILE_IMAGE_PICK)
        }
    }

    private fun profileImageWithCamera() {
        val takeImageIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takeImageIntent, Utils.PROFILE_IMAGE_CAPTURE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                Utils.PROFILE_IMAGE_CAPTURE -> {
                    val profileBitmap = data?.extras?.get("data") as Bitmap
                    uploadProfile(profileBitmap)
                }
                Utils.PROFILE_IMAGE_PICK -> {
                    val profileUri = data?.data
                    val profileBitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, profileUri)
                    uploadProfile(profileBitmap)
                }
            }
        }
    }

    private fun uploadProfile(imageBitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        profileBitmap = imageBitmap!!
        imageView.setImageBitmap(imageBitmap)
        val storagePath = storageRef.child("Profile/${Utils.getUidLoggedIn()}.jpg")
        val uploadTask = storagePath.putBytes(data)
        uploadTask.addOnSuccessListener {
            val task = it.metadata?.reference?.downloadUrl
            task.addOnSuccessListener {
                uriProfile = it
                firestore.collection("Users").document(Utils.getUidLoggedIn())
                    .update("image", uriProfile.toString())
                val collectionRef = firestore.collection("Posts")
                val query = collectionRef.whereEqualTo("userid", Utils.getUidLoggedIn())
                query.get().addOnSuccessListener { documents ->
                    for (document in documents) {
                        collectionRef.document(document.id).update("imageposter", uriProfile.toString())
                    }
                }
                vm.image.value = uriProfile.toString()
            }
            Toast.makeText(context, "Image Uploaded Successfully!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, "Image Upload Failed!", Toast.LENGTH_SHORT).show()
        }
    }
}