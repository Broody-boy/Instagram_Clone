package com.example.instagramclone.ui.profile

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.instagramclone.R
import com.example.instagramclone.activities.AddFriend
import com.example.instagramclone.activities.Login
import com.example.instagramclone.adapters.MyPostAdapter
import com.example.instagramclone.databinding.FragmentProfileBinding
import com.example.instagramclone.mvvm.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView

class ProfileFragment : Fragment() {


    private lateinit var binding : FragmentProfileBinding
    private lateinit var vm : ViewModel
    private lateinit var storageRef : StorageReference
    lateinit var storage : FirebaseStorage

    lateinit var profileBitmap : Bitmap
    var uriProfile : Uri? = null

    private lateinit var pd : ProgressDialog
    private lateinit var firestore : FirebaseFirestore
    private lateinit var adapter : MyPostAdapter
    private lateinit var imageView : CircleImageView

    private lateinit var editText : EditText
    lateinit var fbauth : FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        fbauth = FirebaseAuth.getInstance()

        vm = ViewModelProvider(this).get(ViewModel::class.java)
        binding.viewModel = vm

        adapter = MyPostAdapter()
        binding.recyclerMyPosts.adapter = adapter
        binding.recyclerMyPosts.layoutManager = GridLayoutManager(requireContext(), 3)

        vm.username.observe(viewLifecycleOwner, Observer {
            binding.tvUserName.text = it!!
        })

        vm.name.observe(viewLifecycleOwner, Observer {
            binding.tvName.text = it!!
        })

        vm.image.observe(viewLifecycleOwner, Observer {
            Glide.with(requireContext()).load(it).into(binding.imgProfile)
            //Glide.with(requireContext()).load(it).into(BottomNavBar.imgProfile)
        })

        vm.followers.observe(viewLifecycleOwner, Observer {
            binding.tvFollowerCount.text = it!!
        })

        vm.following.observe(viewLifecycleOwner, Observer {
            binding.tvFollowingCount.text = it!!
        })

        vm.getMyPosts().observe(viewLifecycleOwner, Observer {
            binding.tvPostCount.text = it.size.toString()
            adapter.setPostList(it)
        })

        binding.imgOptionsMenu.setOnClickListener {
            //add menu here and within that, signout
            fbauth.signOut()
            val intent = Intent(requireContext(), Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK //clear call stack
            startActivity(intent)
            requireActivity().finish() //clear current activity(The one which fragment is a part of)
        }

        binding.imgAddPost.setOnClickListener {
            //implement post activity
        }

        binding.imgProfile.setOnClickListener {
            //implement story
        }

        binding.btnEditProfile.setOnClickListener {
            //Implement
        }

        binding.btnShareProfile.setOnClickListener {
            //Implement
        }

        binding.btnAddFriends.setOnClickListener {
            val intent = Intent(requireContext(), AddFriend::class.java)
            startActivity(intent)
        }
    }
}