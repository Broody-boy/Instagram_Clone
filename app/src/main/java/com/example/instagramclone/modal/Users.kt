package com.example.instagramclone.modal

data class Users (
    val userid: String? = "",
    val username: String? = "",
    val profile_image: String? = "",
    val followers: Int? = 0,
    val following: Int? = 0,
)