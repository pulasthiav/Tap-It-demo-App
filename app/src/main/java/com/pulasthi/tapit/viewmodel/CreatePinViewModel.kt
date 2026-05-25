package com.pulasthi.tapit.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CreatePinUiState(
    val pin: String = "",
)

class CreatePinViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePinUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigateToReenterPin = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val navigateToReenterPin = _navigateToReenterPin.asSharedFlow()

    fun onDigitPressed(digit: Int) {
        val current = _uiState.value.pin
        if (current.length >= PIN_LENGTH) return
        val newPin = current + digit
        _uiState.update { it.copy(pin = newPin) }
        if (newPin.length == PIN_LENGTH) {
            _navigateToReenterPin.tryEmit(newPin)
        }
    }

    fun onBackspace() {
        val current = _uiState.value.pin
        if (current.isEmpty()) return
        _uiState.update { it.copy(pin = current.dropLast(1)) }
    }

    companion object {
        private const val PIN_LENGTH = 4
    }
}
