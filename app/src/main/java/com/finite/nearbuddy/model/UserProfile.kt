package com.finite.nearbuddy.model

import java.io.Serializable

data class UserProfile(
    val name: String,
    val dateOfBirth: String,
    val gender: String,
    val interests: Map<String, Int>,
    val profilePic: ByteArray
) : Serializable