package com.saracervantes.focusmeal.data.model

import com.google.firebase.firestore.DocumentId

data class Meal(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val category: String = "",
    val calories: Int = 0,
    val proteins: Float = 0f,
    val carbs: Float = 0f,
    val fats: Float = 0f,
    val date: Long = System.currentTimeMillis(),
    val imageUrl: String = ""
)
