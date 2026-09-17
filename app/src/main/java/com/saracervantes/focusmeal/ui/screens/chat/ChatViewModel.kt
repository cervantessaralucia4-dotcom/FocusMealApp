package com.saracervantes.focusmeal.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saracervantes.focusmeal.data.model.ChatMessage
import com.saracervantes.focusmeal.data.repository.AuthRepository
import com.saracervantes.focusmeal.data.repository.ChatRepository
import com.saracervantes.focusmeal.data.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    data class ChatUiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val messages: List<ChatMessage> = emptyList(),
        val newMessage: String = "",
        val conversationId: String = ""
    )

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun setConversationId(id: String) {
        _uiState.update { it.copy(conversationId = id) }
        loadMessages(id)
    }

    fun loadMessages(conversationId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, conversationId = conversationId) }
            when (val result = chatRepository.getMessages(conversationId)) {
                is Resource.Success -> _uiState.update { it.copy(isLoading = false, messages = result.data ?: emptyList()) }
                is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                is Resource.Loading -> Unit
            }
        }
    }

    fun updateNewMessage(text: String) {
        _uiState.update { it.copy(newMessage = text) }
    }

    fun sendMessage() {
        viewModelScope.launch {
            val current = _uiState.value
            if (current.newMessage.isBlank()) return@launch
            val userId = authRepository.currentUser?.uid ?: ""
            val message = ChatMessage(
                conversationId = current.conversationId,
                senderId = userId,
                text = current.newMessage
            )
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = chatRepository.sendMessage(message)) {
                is Resource.Success -> {
                    loadMessages(current.conversationId)
                    _uiState.update { it.copy(newMessage = "") }
                }
                is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                is Resource.Loading -> Unit
            }
        }
    }
}
