package com.saracervantes.focusmeal.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.saracervantes.focusmeal.data.model.Progress
import com.saracervantes.focusmeal.data.util.Resource
import kotlinx.coroutines.tasks.await

class ProgressRepository(private val firestore: FirebaseFirestore) {

    private val progressCollection = firestore.collection("progress")

    suspend fun getUserProgress(userId: String): Resource<List<Progress>> {
        return try {
            val snapshot = progressCollection
                .whereEqualTo("userId", userId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()
            val progressList = snapshot.toObjects(Progress::class.java)
            Resource.Success(progressList)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error fetching progress")
        }
    }

    suspend fun addProgressRecord(record: Progress): Resource<Boolean> {
        return try {
            progressCollection.add(record).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error adding progress record")
        }
    }

    suspend fun updateProgress(record: Progress): Resource<Boolean> {
        return try {
            progressCollection.document(record.id).set(record).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error updating progress record")
        }
    }

    suspend fun deleteProgress(progressId: String): Resource<Boolean> {
        return try {
            progressCollection.document(progressId).delete().await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error deleting progress record")
        }
    }
}
