package com.example.instagramclone.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instagramclone.adapters.AddFriendAdapter
import com.example.instagramclone.databinding.ActivityAddFriendBinding
import com.example.instagramclone.modal.Users
import com.example.instagramclone.mvvm.ViewModel

class AddFriend : AppCompatActivity() {
    private lateinit var binding: ActivityAddFriendBinding
    private lateinit var vm: ViewModel
    private lateinit var adapter : AddFriendAdapter
    private var list: List<Users> = emptyList()     //this variable 'list' will be used to hold the list of users returned by getAllUsers()
    var firstTime: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vm = ViewModelProvider(this).get(ViewModel::class.java)
        adapter = AddFriendAdapter()
        binding.recyclerUserList.adapter = adapter
        binding.recyclerUserList.layoutManager = LinearLayoutManager(this)

        vm.getAllUsers().observe(this, Observer {
            list = it                               //see description in declaration
            if(firstTime) {                         //As soon as the data is fetched for the first time,
                adapter.setUsersList(it)            //send it to the adapter of the recyclerView
                firstTime = false
            }
        })

        binding.swipeRefresh.setOnRefreshListener {
            adapter.setUsersList(list)
            binding.swipeRefresh.isRefreshing = false
        }

        binding.imgBack.setOnClickListener {
            finish()
        }

    }
}