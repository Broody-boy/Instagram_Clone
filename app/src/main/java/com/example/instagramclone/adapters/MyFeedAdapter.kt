package com.example.instagramclone.adapters

import android.annotation.SuppressLint
import android.text.Html
import android.text.Spanned
import android.text.format.DateUtils
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.instagramclone.R
import com.example.instagramclone.modal.Feed
import java.util.Date

class MyFeedAdapter : RecyclerView.Adapter<FeedHolder>() {

    var feedlist = listOf<Feed>()
    private var listener : OnDoubleTappyClickListner? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return FeedHolder(view)
    }

    override fun getItemCount(): Int {
    return feedlist.size
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: FeedHolder, position: Int) {
        val feed = feedlist[position]
        val boldtext  = "<b>${feed.username}</b>"
        val additional  = "${feed.caption}"
        val combinedText = boldtext + additional
        val formattedtext : Spanned = Html.fromHtml(combinedText)
        holder.usernameCaption.text = formattedtext

        val date = Date(feed.time!!.toLong()*1000)

        val instagramtimeformat = DateUtils.getRelativeDateTimeString(date.time,System.currentTimeMillis(),DateUtils.MINUTE_IN_MILLIS,DateUtils.FORMAT_ABBREV_RELATIVE)

        holder.time.setText(instagramtimeformat)
        holder.usernamePoster.setText(feed.username)


        Glide.with(holder.itemView.context).load(feed.image).into(holder.feedImage)
        Glide.with(holder.itemView.context).load(feed.imageposter).into(holder.userPosterImage)

        holder.likecount.setText("${feed.likes} Likes")

        val doubleClickGesture = GestureDetectorCompat(holder.itemView.context,object : GestureDetector.SimpleOnGestureListener(){
            override fun onDoubleTap(e: MotionEvent): Boolean {

                listener?.onDoubleTap(feed)
                return true
            }
        })

        holder.itemView.setOnTouchListener { _, event ->
            doubleClickGesture.onTouchEvent(event)
            true
        }


    }

    fun setListener(listner: OnDoubleTappyClickListner){
        this.listener = listener
    }


    fun setFeedList(list : List<Feed>){
        this.feedlist = list
    }


}


class FeedHolder(itemView : View) : ViewHolder(itemView){

    val usernamePoster : TextView = itemView.findViewById(R.id.feedtopusername)
    val usernameCaption : TextView = itemView.findViewById(R.id.feedtopusername)
    val userPosterImage : ImageView = itemView.findViewById(R.id.feedtopusername)
    val feedImage: ImageView = itemView.findViewById(R.id.feedtopusername)
    val time : TextView = itemView.findViewById(R.id.feedtopusername)
    val likecount : TextView = itemView.findViewById(R.id.feedtopusername)

}

interface OnDoubleTappyClickListner{
    fun onDoubleTap(feed : Feed)
}