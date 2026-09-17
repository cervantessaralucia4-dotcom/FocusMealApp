package com.saracervantes.focusmeal.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.saracervantes.focusmeal.data.model.Meal
import com.saracervantes.focusmeal.data.util.Resource
import kotlinx.coroutines.tasks.await

class MealRepository(private val firestore: FirebaseFirestore) {

    private val mealsCollection = firestore.collection("meals")

    suspend fun getMeals(userId: String): Resource<List<Meal>> {
        return try {
            val snapshot = mealsCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()
            val meals = snapshot.toObjects(Meal::class.java)
            Resource.Success(meals)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error fetching meals")
        }
    }

    suspend fun addMeal(meal: Meal): Resource<Boolean> {
        return try {
            mealsCollection.add(meal).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error adding meal")
        }
    }

    suspend fun updateMeal(meal: Meal): Resource<Boolean> {
        return try {
            mealsCollection.document(meal.id).set(meal).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error updating meal")
        }
    }

    suspend fun deleteMeal(mealId: String): Resource<Boolean> {
        return try {
            mealsCollection.document(mealId).delete().await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error deleting meal")
        }
    }
}
