package com.example.instagramclone.ui.profile

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.instagramclone.R
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

        vm = ViewModelProvider(this).get(ViewModel::class.java)

        fbauth = FirebaseAuth.getInstance()

        binding.viewModel = vm
        binding.lifecycleOwner = viewLifecycleOwner

        pd = ProgressDialog(requireContext())

        adapter = MyPostAdapter()
        firestore = FirebaseFirestore.getInstance()

        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference


        binding.imgOptionsMenu.setOnClickListener {
            //add menu here and within that, signout
            fbauth.signOut()
            val intent = Intent(requireContext(), Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK //clear call stack
            startActivity(intent)
            requireActivity().finish()                                                      //clear current activity(The one which fragment is a part of)
        }

        binding.imgAddFriends.setOnClickListener {
            //implement followers, following fragment
        }

        binding.imgAddPost.setOnClickListener {
            //implement post activity
        }

        vm.name.observe(viewLifecycleOwner, Observer {
            binding.tvUserName.text = it!!
        })

        vm.image.observe(viewLifecycleOwner, Observer {
            Glide.with(requireContext()).load(it).into(binding.imgProfile)
            //Glide.with(requireContext()).load(it).into(BottomNavBar.imgProfile)
        })

        vm.getMyPost().observe(viewLifecycleOwner, Observer {
            //binding.postsCountText.setText(it.size.toString())
            //adapter.setPostList(it)
        })

        //binding.imagesRecycler.adapter = adapter
    }

}