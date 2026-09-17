package com.saracervantes.focusmeal.data.model

import com.google.firebase.firestore.DocumentId

data class ChatMessage(
    @DocumentId val id: String = "",
    val conversationId: String = "",
    val senderId: String = "",
    val text: String = "",
    val date: Long = System.currentTimeMillis()
)
