package com.example.instagramclone.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.instagramclone.R
import com.example.instagramclone.Utils
import com.example.instagramclone.databinding.ActivityNewPostBinding
import com.example.instagramclone.mvvm.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import jp.wasabeef.blurry.Blurry
import java.io.ByteArrayOutputStream
import java.util.UUID

class NewPost : AppCompatActivity() {

    private lateinit var binding : ActivityNewPostBinding
    private lateinit var pd: ProgressDialog
    private lateinit var vm : ViewModel
    private lateinit var storageRef: StorageReference
    private lateinit var storage: FirebaseStorage
    private var uri: Uri? = null
    private lateinit var firestore : FirebaseFirestore

    private lateinit var bitmap: Bitmap
    var postid : String = ""
    var imageUserPoster: String = ""
    var nameUserPoster: String = ""

    //On start of activity, freshActivityOpened is true. Upon the first dismissal of the dialog box, set to false as next time the dialog box will surely have been opened once.
    var freshActivityOpened = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout for this fragment
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_post)

        //On start of activity, show image choosing options
        showImageSelectionOptionDialog()

        vm = ViewModelProvider(this).get(ViewModel::class.java)
        postid = UUID.randomUUID().toString()
        binding.lifecycleOwner = this
        pd = ProgressDialog(this)
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        vm.name.observe(this, Observer {
            nameUserPoster = it!!
        })

        vm.image.observe(this, Observer {
            imageUserPoster = it!!
            Glide.with(this).load(it).into(binding.imgProfileUser)
        })

        binding.imgImageToPost.setOnClickListener {
            showImageSelectionOptionDialog()
        }

        binding.btnPost.setOnClickListener {
            var capti = binding.etCaption.text.toString()
            firestore.collection("Posts").document(postid).update("caption", capti)
            finish()
        }
    }

    private fun showImageSelectionOptionDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.custom_dialog_select_image_options)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        dialog.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
            if(freshActivityOpened)
                finish()    //In starting itself, if person presses cancel, return back to previous activity
        }

        dialog.findViewById<LinearLayout>(R.id.layoutTakePicture).setOnClickListener {
            takePhotoWithCamera()
            dialog.dismiss()
        }

        dialog.findViewById<ConstraintLayout>(R.id.layoutSelectFromGallery).setOnClickListener {
            pickImageFromGallery()
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            freshActivityOpened = false  //See description at declaration
        }

        dialog.show()
    }


    @SuppressLint("QueryPermissionsNeeded")
    private fun pickImageFromGallery() {
        val pickPictureIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (pickPictureIntent.resolveActivity(this.packageManager) != null) {
            startActivityForResult(pickPictureIntent, Utils.REQUEST_IMAGE_PICK)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun takePhotoWithCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, Utils.REQUEST_IMAGE_CAPTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Utils.REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    uploadImageToFirebaseStorage(imageBitmap)
                }
                Utils.REQUEST_IMAGE_PICK -> {
                    val imageUri = data?.data
                    val imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                    uploadImageToFirebaseStorage(imageBitmap)
                }
            }
        }
    }

    private fun uploadImageToFirebaseStorage(imageBitmap: Bitmap?) {
        val baos = ByteArrayOutputStream()
        imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        bitmap = imageBitmap!!
        binding.imgImageToPost.setImageBitmap(imageBitmap)
        Blurry.with(this).from(bitmap).into(binding.imgBackBlur)
        val storagePath = storageRef.child("Photos/${UUID.randomUUID()}.jpg")
        val uploadTask = storagePath.putBytes(data)

        uploadTask.addOnSuccessListener {
            val task = it.metadata?.reference?.downloadUrl
            task?.addOnSuccessListener {
                uri = it
                postImage(uri)
            }
            Toast.makeText(this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to upload image!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun postImage(uri: Uri?) {
        val likers = ArrayList<String>() // Create an empty ArrayList as the initial value

        val hashMap = hashMapOf<Any, Any>("image" to uri.toString(), "postid" to postid, "userid" to Utils.getUidLoggedIn(), "likers" to likers,
            "time" to Utils.getTime(), "caption" to "default", "likes" to 0,
            "username" to nameUserPoster, "imageposter" to imageUserPoster)

        firestore.collection("Posts").document(postid).set(hashMap)
    }
}