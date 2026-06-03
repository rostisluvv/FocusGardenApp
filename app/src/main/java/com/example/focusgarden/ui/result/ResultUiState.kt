package com.example.focusgarden.ui.result

import com.example.focusgarden.data.model.FocusSessionView

data class ResultUiState(
    val session: FocusSessionView? = null,
    val isLoading: Boolean = true
)
