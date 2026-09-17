package com.saracervantes.focusmeal.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.saracervantes.focusmeal.data.model.User
import com.saracervantes.focusmeal.data.util.Resource
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    val currentUser get() = auth.currentUser

    suspend fun signIn(email: String, pass: String): Resource<Boolean> {
        return try {
            auth.signInWithEmailAndPassword(email, pass).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown Error")
        }
    }

    suspend fun signUp(email: String, pass: String, name: String, age: Int = 0, gender: String = "", dietType: String = ""): Resource<Boolean> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            val userId = result.user?.uid ?: throw Exception("User creation failed")
            
            val user = User(id = userId, name = name, email = email, age = age, gender = gender, dietType = dietType)
            firestore.collection("users").document(userId).set(user).await()
            
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown Error")
        }
    }

    suspend fun signOut() {
        auth.signOut()
    }
}
