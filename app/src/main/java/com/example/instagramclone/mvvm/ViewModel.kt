package com.example.instagramclone.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instagramclone.Utils
import com.example.instagramclone.modal.Posts
import com.example.instagramclone.modal.Users
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewModel : ViewModel() {
    val name = MutableLiveData<String>()
    val image = MutableLiveData<String>()
    val followers = MutableLiveData<String>()
    val following = MutableLiveData<String>()

    init {
        getCurrentUser()
    }

    fun getCurrentUser() = viewModelScope.launch(Dispatchers.IO) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("Users").document(Utils.getUidLoggedIn()).addSnapshotListener {value, error ->
            if (error!=null) {
                return@addSnapshotListener
            }
            if (value!!.exists() && value!=null) {
                val users = value.toObject(Users::class.java)
                name.value = users!!.username!!
                image.value = users!!.profile_image!!
                followers.value = users!!.followers!!.toString()
                following.value = users!!.following!!.toString()
            }
        }
    }

    fun getMyPosts() : LiveData<List<Posts>> {
        val posts = MutableLiveData<List<Posts>>()
        val firestore = FirebaseFirestore.getInstance()
        viewModelScope.launch(Dispatchers.IO) {
            try{
                firestore.collection("Posts").whereEqualTo("userid", Utils.getUidLoggedIn())
                    .addSnapshotListener {snapshot, exception ->
                        if (exception!=null) {
                            // Handle the exception here
                            return@addSnapshotListener
                        }
                        val postList = snapshot?.documents?.mapNotNull {
                            it.toObject(Posts::class.java)
                        }
                            //show latest post
                            ?.sortedByDescending { it.time
                            }
                        posts.postValue(postList!!) // Switch back to the main thread
                    }
            } catch (e: Exception) {
                // Handle any exceptions that occur during the Firestore operation
            }
        }
        return posts
    }
}