package com.example.instagramclone.mvvm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instagramclone.Utils
import com.example.instagramclone.modal.Posts
import com.example.instagramclone.modal.Story
import com.example.instagramclone.modal.Users
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewModel : ViewModel() {
    val name = MutableLiveData<String>()
    val username = MutableLiveData<String>()
    val image = MutableLiveData<String>()
    val followers = MutableLiveData<String>()
    val following = MutableLiveData<String>()

    init {
        getCurrentUser()
    }

    fun getCurrentUser() = viewModelScope.launch(Dispatchers.IO) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("Users").document(Utils.getUidLoggedIn())
            .addSnapshotListener {value, error ->
            if (error!=null) {
                return@addSnapshotListener
            }
            if (value!!.exists() && value!=null) {
                val users = value.toObject(Users::class.java)
                name.value = users!!.name!!
                username.value = users!!.username!!
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
                        val postList = snapshot?.documents?.mapNotNull {    //mapNotNull is used to convert the list of Firestore documents into a list of Posts objects.
                            it.toObject(Posts::class.java)                                  //Here, mapNotNull is a higher-order function applied to the list of documents.
                        }                                                                   //For each document in the list, the lambda function inside mapNotNull is executed.
                            //show latest post
                            ?.sortedByDescending { it.time
                            }
                        posts.postValue(postList!!) //Set the value of posts to the postList. This is done using postValue() instead of directly setting the value to ensure it happens on the main (UI) thread, as LiveData is usually used for UI observation.
                    }
            } catch (e: Exception) {
                // handle exception
            }
        }
        return posts
    }

    //get all users except the current user
    fun getAllUsers() : LiveData<List<Users>>{
        val users = MutableLiveData<List<Users>>()
        val firestore = FirebaseFirestore.getInstance()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                firestore.collection("Users").whereNotEqualTo("userid", Utils.getUidLoggedIn())
                    .addSnapshotListener {snapshot, exception ->
                        if (exception!=null) {
                            // Handle the exception here
                            return@addSnapshotListener
                        }
                        val userList = snapshot?.documents?.mapNotNull {    //mapNotNull is used to convert the list of Firestore documents into a list of Users objects.
                            it.toObject(Users::class.java)                                  //Here, mapNotNull is a higher-order function applied to the list of documents.
                        }                                                                   //For each document in the list, the lambda function inside mapNotNull is executed.
                        users.postValue(userList!!) //Set the value of users to the userList. This is done using postValue() instead of directly setting the value to ensure it happens on the main (UI) thread, as LiveData is usually used for UI observation.
                    }
            } catch (e: Exception) {
                // handle exception
            }
        }
        return users
    }

    fun loadStories(): LiveData<List<Story>> {
        val firestore = FirebaseFirestore.getInstance()
        val stories = MutableLiveData<List<Story>>()

        viewModelScope.launch(Dispatchers.IO) {
            getThePeopleIFollow { list ->
                try{
                    firestore.collection("Stories").whereIn("userid", list).addSnapshotListener { value, error ->
                        if (error != null) {
                            return@addSnapshotListener
                        }

                        val story = mutableListOf<Story>()
                        value?.documents?.forEach { documentSnapshot ->
                            val pModal = documentSnapshot.toObject(Story::class.java)
                            pModal?.let {
                                story.add(it)
                            }
                        }

                        val sortedStory = story.sortedByDescending { it.time }
                        stories.postValue(sortedStory)
                    }
                }catch (e:Exception){
                }
            }
        }
        return stories
    }

    // get the ids of those who I follow:
    fun getThePeopleIFollow(callback: (List<String>) -> Unit) {
        val firestore = FirebaseFirestore.getInstance()

        val ifollowlist = mutableListOf<String>()
        ifollowlist.add(Utils.getUidLoggedIn())

        firestore.collection("Follow").document(Utils.getUidLoggedIn()).get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val followingIds = documentSnapshot.get("following_id") as? List<String>
                val updatedList = followingIds?.toMutableList() ?: mutableListOf()

                ifollowlist.addAll(updatedList)

                Log.d("ListOfPeopleIfollow", ifollowlist.toString())
                callback(ifollowlist)
            } else {
                callback(ifollowlist)
            }
        }
    }
}