package com.pulasthi.tapit.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LanguageOption(
    val id: String,
    val label: String,
)

data class LanguageUiState(
    val options: List<LanguageOption> = listOf(
        LanguageOption("en", "English"),
        LanguageOption("si", "Sinhala"),
        LanguageOption("ta", "தமிழ்"),
    ),
    val selectedId: String = "en",
)

class LanguageViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LanguageUiState())
    val uiState = _uiState.asStateFlow()

    fun onLanguageSelected(id: String) {
        _uiState.update { it.copy(selectedId = id) }
    }
}
