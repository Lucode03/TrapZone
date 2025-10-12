package com.example.trapzoneapp.classes

data class UserProfile(
    val name: String,
    val surname: String,
    val avatarUrl: String,
    val points: Int,
    val numTraps: Int,
    val numObjects: Int
)