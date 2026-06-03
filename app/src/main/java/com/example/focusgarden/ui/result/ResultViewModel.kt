package com.example.focusgarden.ui.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusgarden.data.repository.FocusSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResultViewModel(
    private val sessionId: Long,
    private val repository: FocusSessionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ResultUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = ResultUiState(session = repository.getSession(sessionId), isLoading = false)
        }
    }
}
