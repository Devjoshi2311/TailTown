package com.tailtown.pawcare.ui.inbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tailtown.pawcare.data.repository.InboxRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InboxViewModel @Inject constructor(private val inboxRepository: InboxRepository) : ViewModel() {

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()
    private val _selectedFilter = MutableStateFlow(0)  // 0=All 1=Vets 2=Orders
    val selectedFilter: StateFlow<Int> = _selectedFilter.asStateFlow()

    val filteredConversations: StateFlow<List<Conversation>> = combine(
        _conversations, _selectedFilter,
    ) { convos, filter ->
        when (filter) {
            1 -> convos.filter { it.type == ConversationType.VET }
            2 -> convos.filter { it.type == ConversationType.ORDER }
            else -> convos
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _messages = MutableStateFlow(sampleMessages)
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private var loaded = false

    // Was eagerly fetched at NavGraph root regardless of whether Inbox was ever opened —
    // now triggered lazily from the Inbox screen itself (see NavGraph.kt).
    fun ensureLoaded() {
        if (loaded) return
        loaded = true
        loadConversations()
    }

    private fun loadConversations() {
        viewModelScope.launch {
            try {
                _conversations.value = inboxRepository.getConversations()
            } catch (_: Exception) {
                _conversations.value = emptyList()
            }
        }
    }

    fun setFilter(index: Int) { _selectedFilter.value = index }

    fun sendMessage(conversationId: String, text: String) {
        viewModelScope.launch {
            try {
                val msg = inboxRepository.sendMessage(conversationId, text)
                _messages.update { it + msg }
            } catch (_: Exception) {
                // message failed to send — UI stays unchanged
            }
        }
    }

    fun markRead(conversationId: String) {
        _conversations.update { list ->
            list.map { if (it.id == conversationId) it.copy(unreadCount = 0) else it }
        }
    }
}
