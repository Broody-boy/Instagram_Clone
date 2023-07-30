package com.example.instagramclone.modal

data class Story (
    val userid: String? = "",
    val username: String? = "",
    val imageposter: String? = "",
    val stories_array: List<String> = emptyList(),
    val time: Long? = null,
)