package com.saracervantes.focusmeal.data.model

import com.google.firebase.firestore.DocumentId

data class Progress(
    @DocumentId val id: String = "",
    val userId: String = "",
    val date: Long = System.currentTimeMillis(),
    val weight: Float = 0f,
    val caloriesConsumed: Int = 0
)
