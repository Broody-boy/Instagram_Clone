package com.example.instagramclone.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagramclone.databinding.ItemMypostBinding
import com.example.instagramclone.modal.Posts

class MyPostAdapter : RecyclerView.Adapter<PostHolder>() {

    var mypostlist = listOf<Posts>()
    lateinit var binding : ItemMypostBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        //val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mypost, parent, false)
        binding = ItemMypostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostHolder(binding)
    }

    override fun getItemCount(): Int {
        return mypostlist.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        val post = mypostlist[position]
        Glide.with(holder.itemView.context).load(post.image).into(holder.image)
    }

    fun setPostList(list: List<Posts>) {
        val diffResult = DiffUtil.calculateDiff(MyDiffCallback(mypostlist, list))
        mypostlist = list
        diffResult.dispatchUpdatesTo(this)
    }
}

class PostHolder(binding: ItemMypostBinding) : RecyclerView.ViewHolder(binding.root) {
    val image: ImageView = binding.imgMyPost
}

class MyDiffCallback(
    private val oldList : List<Posts>,
    private val newList : List<Posts>
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