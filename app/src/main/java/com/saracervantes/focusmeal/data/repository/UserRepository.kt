package com.saracervantes.focusmeal.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.saracervantes.focusmeal.data.model.User
import com.saracervantes.focusmeal.data.util.Resource
import kotlinx.coroutines.tasks.await

class UserRepository(private val firestore: FirebaseFirestore) {

    private val usersCollection = firestore.collection("users")

    suspend fun createUser(user: User): Resource<Boolean> {
        return try {
            usersCollection.document(user.id).set(user).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error creating user")
        }
    }

    suspend fun getUser(userId: String): Resource<User?> {
        return try {
            val snapshot = usersCollection.document(userId).get().await()
            val user = snapshot.toObject(User::class.java)
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error fetching user")
        }
    }

    suspend fun getAllUsers(): Resource<List<User>> {
        return try {
            val snapshot = usersCollection.get().await()
            val users = snapshot.toObjects(User::class.java)
            Resource.Success(users)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error fetching users")
        }
    }

    suspend fun deleteUser(userId: String): Resource<Boolean> {
        return try {
            usersCollection.document(userId).delete().await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error deleting user")
        }
    }

    suspend fun updateUser(user: User): Resource<Boolean> {
        return try {
            usersCollection.document(user.id).set(user).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error updating user")
        }
    }
}
