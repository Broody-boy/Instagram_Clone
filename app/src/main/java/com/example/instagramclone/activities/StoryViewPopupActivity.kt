package com.example.instagramclone.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.example.instagramclone.R
import com.example.instagramclone.databinding.ActivityStoryViewPopupBinding

class StoryViewPopupActivity : AppCompatActivity() {

    private lateinit var binding : ActivityStoryViewPopupBinding
    private var currstoryidx = 0
    private lateinit var storyLinksList : ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_view_popup)

        binding = ActivityStoryViewPopupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val list = intent.getSerializableExtra("links") as? ArrayList<String>
        storyLinksList = ArrayList(list)

        val wdw = this.window
        wdw.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        wdw.statusBarColor = resources.getColor(R.color.black)

        setStoryandWait()
    }

    private fun setStoryandWait() {

        Glide.with(this).load(storyLinksList!![currstoryidx]).into(binding.imgStoryContent)
        currstoryidx++

        object : CountDownTimer(7000, 10) {
            override fun onTick(millisUntilFinished: Long) {
                binding.pbTime.progress = ((7000-millisUntilFinished) * 100 / 7000).toInt()
                //Toast.makeText(applicationContext, "$millisUntilFinished", Toast.LENGTH_SHORT).show()
            }

            override fun onFinish() {
                //Toast.makeText(applicationContext, "done", Toast.LENGTH_SHORT).show()
                if(currstoryidx < storyLinksList.size)
                    setStoryandWait()
                else
                    finish()
            }
        }.start()
    }
}