package com.saracervantes.focusmeal.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.saracervantes.focusmeal.data.model.Plan
import com.saracervantes.focusmeal.data.util.Resource
import kotlinx.coroutines.tasks.await

class PlanRepository(private val firestore: FirebaseFirestore) {

    private val plansCollection = firestore.collection("plans")

    suspend fun getPlans(): Resource<List<Plan>> {
        return try {
            val snapshot = plansCollection.get().await()
            val plans = snapshot.toObjects(Plan::class.java)
            Resource.Success(plans)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error fetching plans")
        }
    }

    suspend fun addPlan(plan: Plan): Resource<Boolean> {
        return try {
            plansCollection.add(plan).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error adding plan")
        }
    }

    suspend fun updatePlan(plan: Plan): Resource<Boolean> {
        return try {
            plansCollection.document(plan.id).set(plan).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error updating plan")
        }
    }

    suspend fun deletePlan(planId: String): Resource<Boolean> {
        return try {
            plansCollection.document(planId).delete().await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error deleting plan")
        }
    }
}
