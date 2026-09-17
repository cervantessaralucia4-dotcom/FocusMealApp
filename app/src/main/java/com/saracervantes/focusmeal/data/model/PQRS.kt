package com.saracervantes.focusmeal.data.model

import com.google.firebase.firestore.DocumentId

data class PQRS(
    @DocumentId val id: String = "",
    val userId: String = "",
    val type: String = "",
    val subject: String = "",
    val message: String = "",
    val date: Long = System.currentTimeMillis(),
    val status: String = "Open"
) {
    companion object {
        const val TYPE_QUESTION = "Pregunta"
        const val TYPE_COMPLAINT = "Queja"
        const val TYPE_RECLAIM = "Reclamo"
        const val TYPE_SUGGESTION = "Sugerencia"
        const val STATUS_OPEN = "Open"
        const val STATUS_RESOLVED = "Resolved"
        const val STATUS_IN_PROGRESS = "In Progress"
    }
}
