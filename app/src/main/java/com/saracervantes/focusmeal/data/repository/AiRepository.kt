package com.saracervantes.focusmeal.data.repository

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.saracervantes.focusmeal.data.util.Resource
import com.saracervantes.focusmeal.data.util.Secrets
import org.json.JSONObject

class AiRepository {

    private val visionModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = Secrets.GEMINI_API_KEY
    )

    suspend fun analyzeFoodImage(bitmap: Bitmap): Resource<FoodAnalysisResult> {
        return try {
            val prompt = """
                Analiza esta imagen de comida y devuelve ÚNICAMENTE un objeto JSON válido con la siguiente estructura, sin formato markdown ni texto adicional:
                {
                  "name": "Nombre de la comida",
                  "calories": 0,
                  "proteins": 0.0,
                  "carbs": 0.0,
                  "fats": 0.0
                }
                Si no hay comida en la foto, devuelve valores en 0 y nombre 'Desconocido'.
            """.trimIndent()

            val response = visionModel.generateContent(
                content {
                    image(bitmap)
                    text(prompt)
                }
            )
            
            val jsonText = response.text?.replace("```json", "")?.replace("```", "")?.trim()
            if (jsonText != null) {
                val jsonObject = JSONObject(jsonText)
                val result = FoodAnalysisResult(
                    name = jsonObject.optString("name", "Desconocido"),
                    calories = jsonObject.optInt("calories", 0),
                    proteins = jsonObject.optDouble("proteins", 0.0).toFloat(),
                    carbs = jsonObject.optDouble("carbs", 0.0).toFloat(),
                    fats = jsonObject.optDouble("fats", 0.0).toFloat()
                )
                Resource.Success(result)
            } else {
                Resource.Error("No se pudo analizar la imagen.")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error conectando con Gemini AI")
        }
    }
    suspend fun generateDietPlan(weight: Float, age: Int, goal: String, dietType: String): Resource<PlanGenerationResult> {
        return try {
            val prompt = """
                Eres un nutricionista experto. Crea un plan nutricional estructurado en formato JSON, sin markdown, para un usuario de $age años, con $weight kg, cuyo objetivo es "$goal" y prefiere una dieta "$dietType". 
                El JSON debe tener esta estructura:
                {
                  "name": "Nombre creativo del plan",
                  "description": "Descripción detallada del plan en 1-2 oraciones",
                  "caloriesPerDay": 2000
                }
            """.trimIndent()

            val textModel = GenerativeModel(
                modelName = "gemini-2.5-flash",
                apiKey = Secrets.GEMINI_API_KEY
            )

            val response = textModel.generateContent(prompt)
            val jsonText = response.text?.replace("```json", "")?.replace("```", "")?.trim()
            if (jsonText != null) {
                val jsonObject = JSONObject(jsonText)
                val result = PlanGenerationResult(
                    name = jsonObject.optString("name", "Plan Personalizado IA"),
                    description = jsonObject.optString("description", "Descripción generada por IA"),
                    caloriesPerDay = jsonObject.optInt("caloriesPerDay", 2000)
                )
                Resource.Success(result)
            } else {
                Resource.Error("No se pudo generar el plan.")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error conectando con Gemini AI")
        }
    }
}

data class PlanGenerationResult(
    val name: String,
    val description: String,
    val caloriesPerDay: Int
)

data class FoodAnalysisResult(
    val name: String,
    val calories: Int,
    val proteins: Float,
    val carbs: Float,
    val fats: Float
)
