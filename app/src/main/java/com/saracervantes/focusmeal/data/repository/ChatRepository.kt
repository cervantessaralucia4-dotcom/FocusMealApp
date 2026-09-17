package com.saracervantes.focusmeal.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.saracervantes.focusmeal.data.model.ChatMessage
import com.saracervantes.focusmeal.data.util.Resource
import kotlinx.coroutines.tasks.await

class ChatRepository(private val firestore: FirebaseFirestore) {

    private val chatCollection = firestore.collection("messagesChat")

    suspend fun getMessages(conversationId: String): Resource<List<ChatMessage>> {
        return try {
            val snapshot = chatCollection
                .whereEqualTo("conversationId", conversationId)
                .orderBy("date", Query.Direction.ASCENDING)
                .get()
                .await()
            val messages = snapshot.toObjects(ChatMessage::class.java)
            Resource.Success(messages)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error fetching messages")
        }
    }

    suspend fun sendMessage(message: ChatMessage): Resource<Boolean> {
        return try {
            chatCollection.add(message).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error sending message")
        }
    }

    suspend fun updateMessage(message: ChatMessage): Resource<Boolean> {
        return try {
            chatCollection.document(message.id).set(message).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error updating message")
        }
    }

    suspend fun deleteMessage(messageId: String): Resource<Boolean> {
        return try {
            chatCollection.document(messageId).delete().await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error deleting message")
        }
    }
}
