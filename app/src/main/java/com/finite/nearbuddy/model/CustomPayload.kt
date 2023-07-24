package com.finite.nearbuddy.model


data class CustomPayload(
    val userProfile: UserProfile = UserProfile("", "", "", mapOf(), "".toByteArray(Charsets.UTF_8)),
    val chatMessage: ChatMessage = ChatMessage("", "")
)
