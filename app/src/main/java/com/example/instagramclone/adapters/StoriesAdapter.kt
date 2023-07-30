package com.example.instagramclone.adapters

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagramclone.R
import com.example.instagramclone.activities.StoryViewPopupActivity
import com.example.instagramclone.databinding.ItemStoryBinding
import com.example.instagramclone.modal.Story

class StoriesAdapter : RecyclerView.Adapter<StoryViewHolder>() {

    var storylist = listOf<Story>()
    lateinit var binding : ItemStoryBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return storylist.size
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val currentStory = storylist[position]
        Glide.with(holder.itemView.context).load(currentStory.imageposter).into(holder.image)
        holder.tvusername.text = currentStory.username

        holder.itemView.setOnClickListener {
            holder.image.borderColor = Color.parseColor("#9f9f9e")
            val intent = Intent(holder.itemView.context, StoryViewPopupActivity::class.java)
            intent.putExtra("links", ArrayList(currentStory.stories_array))
            intent.putExtra("profimg", currentStory.imageposter)
            intent.putExtra("usrnm", currentStory.username)
            holder.itemView.context.startActivity(intent)
        }
    }

    fun setStories(list: List<Story>) {
        val diffResult = DiffUtil.calculateDiff(StoryDiffCallback(storylist, list))
        storylist = list
            for(x in storylist){Log.d("entire", "$x")}
        diffResult.dispatchUpdatesTo(this)
    }
}

class StoryViewHolder(val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
    val image = binding.imgStoryPosterProfilePic
    val tvusername: TextView = binding.tvStoryPosterUserName
}

class StoryDiffCallback(
    private val oldList : List<Story>,
    private val newList : List<Story>
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