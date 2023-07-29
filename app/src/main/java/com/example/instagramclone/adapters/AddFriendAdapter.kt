package com.example.instagramclone.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagramclone.Utils
import com.example.instagramclone.databinding.ItemFriendListBinding
import com.example.instagramclone.modal.Users
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AddFriendAdapter : RecyclerView.Adapter<UserHolder>() {
    var userList = listOf<Users>()
    lateinit var binding : ItemFriendListBinding
    var clickedOn: Boolean = false
    var follower_id = ""
    var following_id = ""
    var following_count: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        binding = ItemFriendListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        val user = userList[position]
        Glide.with(holder.itemView.context).load(user.profile_image).into(holder.imgProf)
        holder.tvUserName.text = user.username
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("Following").document(Utils.getUidLoggedIn())
            .addSnapshotListener {value, error ->
                if (error!=null){
                    return@addSnapshotListener
                }
                if (value!!.exists() && value!=null) {
                    val followingIds = value.get("following_id") as? List<String>
                    if (followingIds!=null) {
                        if (followingIds!!.contains(user.userid)) {
                            holder.btnFollowUnfollow.text = "Unfollow"
                        } else {
                            holder.btnFollowUnfollow.text = "Follow"
                        }
                    }

                }
        }
        holder.btnFollowUnfollow.setOnClickListener {
            if (holder.btnFollowUnfollow.text == "Follow") {
                followUser(user)
                holder.btnFollowUnfollow.text = "Unfollow"
                Toast.makeText(binding.root.context, "ABCD", Toast.LENGTH_SHORT).show()
            } else if (holder.btnFollowUnfollow.text == "Unfollow"){
                unFollowUser(user)
                holder.btnFollowUnfollow.text = "Follow"
            }
        }

    }

    fun followUser(users: Users) {
        val firestore = FirebaseFirestore.getInstance()
        val userFollowing = firestore.collection("Users").document(Utils.getUidLoggedIn())
        userFollowing.update("following", FieldValue.increment(1))
        val userToFollow = firestore.collection("Users").document(users.userid!!)
        userToFollow.update("followers", FieldValue.increment(1))

        val followingDocRef = firestore.collection("Following").document(Utils.getUidLoggedIn())
        followingDocRef.get().addOnSuccessListener {
            if (it.exists()) {
                val existingIds = it.get("following_id") as? List<String>
                val newIds = existingIds?.toMutableList() ?: mutableListOf()
                newIds.add(users.userid!!)
                followingDocRef.update(hashMapOf("following_id" to newIds) as Map<String, Any>)
            } else{
                val follow_data = hashMapOf("following_id" to listOf(users.userid!!))
                followingDocRef.set(follow_data)
            }
        }.addOnFailureListener {
            Toast.makeText(binding.root.context, "Something went wrong!", Toast.LENGTH_SHORT).show()
        }

        val followerDocRef = firestore.collection("Followers").document(users.userid!!)
        followerDocRef.get().addOnSuccessListener {
            if (it.exists()) {
                val existingIds = it.get("follower_id") as? List<String>
                val newIds = existingIds?.toMutableList() ?: mutableListOf()
                newIds.add(Utils.getUidLoggedIn())
                followerDocRef.update(hashMapOf("follower_id" to newIds) as Map<String, Any>)
            } else{
                val follow_data = hashMapOf("follower_id" to listOf(Utils.getUidLoggedIn()))
                followerDocRef.set(follow_data)
            }
        }.addOnFailureListener {
            Toast.makeText(binding.root.context, "Something went wrong!", Toast.LENGTH_SHORT).show()
        }
    }

    fun unFollowUser(users: Users) {
        val firestore = FirebaseFirestore.getInstance()
        val userFollowing = firestore.collection("Users").document(Utils.getUidLoggedIn())
        userFollowing.update("following", FieldValue.increment(-1))
        val userToFollow = firestore.collection("Users").document(users.userid!!)
        userToFollow.update("followers", FieldValue.increment(-1))

        val followingDocRef = firestore.collection("Following").document(Utils.getUidLoggedIn())
        followingDocRef.get().addOnSuccessListener {
            if (it.exists()) {
                val existingIds = it.get("following_id") as? List<String>
                val newIds = existingIds?.toMutableList() ?: mutableListOf()
                //if (users.userid!! in newIds) {
                newIds.remove(users.userid!!)
                followingDocRef.update(hashMapOf("following_id" to newIds) as Map<String, Any>)
                //}
            }
        }.addOnFailureListener {
            Toast.makeText(binding.root.context, "Something went wrong!", Toast.LENGTH_SHORT).show()
        }
        val followerDocRef = firestore.collection("Followers").document(users.userid!!)
        followerDocRef.get().addOnSuccessListener {
            if (it.exists()) {
                val existingIds = it.get("follower_id") as? List<String>
                val newIds = existingIds?.toMutableList() ?: mutableListOf()
                newIds.remove(Utils.getUidLoggedIn())
                followerDocRef.update(hashMapOf("follower_id" to newIds) as Map<String, Any>)
            }
        }.addOnFailureListener {
            Toast.makeText(binding.root.context, "Something went wrong!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun setUsersList(list: List<Users>) {
        val diffResult = DiffUtil.calculateDiff(UserDiffCallback(userList, list))
        userList = list
        diffResult.dispatchUpdatesTo(this)
    }


}

class UserHolder(val binding: ItemFriendListBinding) : RecyclerView.ViewHolder(binding.root) {
    val imgProf = binding.imgProf
    val tvUserName = binding.tvUserName
    val btnFollowUnfollow = binding.btnFollowUnfollow
}

class UserDiffCallback(
    private val oldList : List<Users>,
    private val newList : List<Users>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
