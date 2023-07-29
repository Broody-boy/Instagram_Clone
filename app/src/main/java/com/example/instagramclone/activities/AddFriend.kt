package com.example.instagramclone.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instagramclone.adapters.AddFriendAdapter
import com.example.instagramclone.adapters.OnFriendClicked
import com.example.instagramclone.databinding.ActivityAddFriendBinding
import com.example.instagramclone.modal.Users
import com.example.instagramclone.mvvm.ViewModel

class AddFriend : AppCompatActivity(), OnFriendClicked {
    lateinit var binding: ActivityAddFriendBinding
    lateinit var vm: ViewModel
    private lateinit var adapter : AddFriendAdapter
    private var list: List<Users> = emptyList()
    var firstTime: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFriendBinding.inflate(layoutInflater)
        vm = ViewModelProvider(this).get(ViewModel::class.java)
        setContentView(binding.root)
        adapter = AddFriendAdapter()
        vm.getAllUsers().observe(this, Observer {
            list = it
            if(firstTime) {
                adapter.setUsersList(it)
                firstTime = false
            }
        })
        binding.swipeRefresh.setOnRefreshListener {
            adapter.setUsersList(list)
            binding.swipeRefresh.isRefreshing = false
        }
        binding.recyclerUserList.adapter = adapter
        binding.recyclerUserList.layoutManager = LinearLayoutManager(this)

        binding.imgPrevious.setOnClickListener {
            finish()
        }

        adapter.setListener(this)
    }

    override fun onFriendListener(position: Int, users: Users) {
        //implement something
    }
}