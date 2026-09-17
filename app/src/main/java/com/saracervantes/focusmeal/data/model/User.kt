package com.saracervantes.focusmeal.data.model

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId val id: String = "",
    val name: String = "",
    val email: String = "",
    val age: Int = 0,
    val gender: String = "",
    val weight: Float = 0f,
    val height: Float = 0f,
    val goal: String = "",
    val dietType: String = "",
    val role: String = "user",
    val isPremium: Boolean = false,
    val registrationDate: Long = System.currentTimeMillis(),
    val profileImageUrl: String? = null
) {
    companion object {
        const val GOAL_LOSE_WEIGHT = "Bajar de peso"
        const val GOAL_MAINTAIN_WEIGHT = "Mantener peso"
        const val GOAL_GAIN_MASS = "Aumentar masa"
        const val DIET_GENERAL = "General"
        const val DIET_VEGETARIAN = "Vegetariana"
        const val DIET_KETO = "Keto"
        const val DIET_LOW_CARB = "Baja en carbohidratos"
        const val DIET_HIGH_PROTEIN = "Alta en proteínas"
        const val ROLE_USER = "usuario"
        const val ROLE_NUTRITIONIST = "nutricionista"
        const val ROLE_ADMIN = "admin"

        val GENDER_OPTIONS = listOf("Masculino", "Femenino", "Otro", "Prefiero no decirlo")
        val DIET_OPTIONS = listOf(DIET_GENERAL, DIET_VEGETARIAN, DIET_KETO, DIET_LOW_CARB, DIET_HIGH_PROTEIN)
    }
}
