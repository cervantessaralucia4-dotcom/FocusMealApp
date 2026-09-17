package com.saracervantes.focusmeal.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.saracervantes.focusmeal.data.repository.*

object AppModule {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    val authRepository: AuthRepository by lazy {
        AuthRepository(auth, firestore)
    }

    val userRepository: UserRepository by lazy {
        UserRepository(firestore)
    }

    val mealRepository: MealRepository by lazy {
        MealRepository(firestore)
    }

    val planRepository: PlanRepository by lazy {
        PlanRepository(firestore)
    }

    val progressRepository: ProgressRepository by lazy {
        ProgressRepository(firestore)
    }

    val chatRepository: ChatRepository by lazy {
        ChatRepository(firestore)
    }

    val pqrsRepository: PQRSRepository by lazy {
        PQRSRepository(firestore)
    }
}
