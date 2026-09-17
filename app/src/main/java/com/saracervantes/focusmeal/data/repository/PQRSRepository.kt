package com.saracervantes.focusmeal.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.saracervantes.focusmeal.data.model.PQRS
import com.saracervantes.focusmeal.data.util.Resource
import kotlinx.coroutines.tasks.await

class PQRSRepository(private val firestore: FirebaseFirestore) {

    private val pqrsCollection = firestore.collection("pqrs")

    suspend fun submitPQRS(record: PQRS): Resource<Boolean> {
        return try {
            pqrsCollection.add(record).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error submitting PQRS")
        }
    }

    suspend fun getUserPQRS(userId: String): Resource<List<PQRS>> {
        return try {
            val snapshot = pqrsCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()
            val list = snapshot.toObjects(PQRS::class.java)
            Resource.Success(list)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error fetching PQRS records")
        }
    }

    suspend fun updateStatus(pqrsId: String, status: String): Resource<Boolean> {
        return try {
            pqrsCollection.document(pqrsId).update("status", status).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error updating PQRS status")
        }
    }

    suspend fun deletePQRS(pqrsId: String): Resource<Boolean> {
        return try {
            pqrsCollection.document(pqrsId).delete().await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error deleting PQRS record")
        }
    }
}
