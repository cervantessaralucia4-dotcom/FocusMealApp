package com.saracervantes.focusmeal.data.model

import com.google.firebase.firestore.DocumentId

data class Plan(
    @DocumentId val id: String = "",
    val name: String = "",
    val description: String = "",
    val caloriesPerDay: Int = 0,
    val dietType: String = ""
)
