package com.example.instagramclone.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.instagramclone.R
import com.example.instagramclone.Utils
import com.example.instagramclone.adapters.MyFeedAdapter
import com.example.instagramclone.adapters.OnDoubleTappyClickListner
import com.example.instagramclone.databinding.FragmentHomeBinding
import com.example.instagramclone.modal.Feed
import com.example.instagramclone.mvvm.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() , OnDoubleTappyClickListner {

    private lateinit var vm : ViewModel
    private lateinit var binding : FragmentHomeBinding
    private lateinit var adapter: MyFeedAdapter

    private var postid: String =""
    private var userwholinked: String =""
    private var idofpostowner: String =""




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val homeViewModel =
//            ViewModelProvider(this).get(HomeViewModel::class.java)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home ,container, false)

        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        vm = ViewModelProvider(this).get(ViewModel::class.java)
        vm = ViewModelProvider(this).get(ViewModel::class.java)
        adapter = MyFeedAdapter()
        vm.loadMyFeed()
        binding.lifecycleOwner = viewLifecycleOwner

        vm.loadMyFeed().observe(viewLifecycleOwner, Observer {

            adapter.setFeedList(it)

//            ARSHDEEP RECYCLERVIEW DALIO YAHA PE
            binding.feedRecycler.adapter = adapter
        })

        adapter.setListener(this)


    }

    override fun onDoubleTap(feed: Feed) {
        val currentuserid = Utils.getUidLoggedIn()
        val postId = feed.postid

        val firestore = FirebaseFirestore.getInstance()
        val postRef = firestore.collection("Posts").document(postid!!)

        postRef.get().addOnSuccessListener { document ->

            if (document!=null && document.exists()){

                val likes = document.getLong("likes")?.toInt() ?:0
                val likers = document.get(("likers")) as? List<String>

                if (!likers!!.isEmpty() && likers.contains(currentuserid)){
                    Toast.makeText(requireContext(),"kitni bar karega bhosdike", Toast.LENGTH_SHORT).show()
                }else{

                    postRef.update("likes", likes+1 , "likers" , FieldValue.arrayUnion(currentuserid)).addOnSuccessListener {
                        Toast.makeText(requireContext(),"kar dia like chal bhad", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        println("failed to like $it")
                    }

                }





            }
        }
    }
}