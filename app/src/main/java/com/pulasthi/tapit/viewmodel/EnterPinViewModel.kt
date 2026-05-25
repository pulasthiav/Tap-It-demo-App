package com.pulasthi.tapit.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class EnterPinUiState(
    val pin: String = "",
    val isLoading: Boolean = false,
)

class EnterPinViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EnterPinUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigateToDashboard = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToDashboard = _navigateToDashboard.asSharedFlow()

    fun onDigitPressed(digit: Int) {
        val current = _uiState.value.pin
        if (current.length >= PIN_LENGTH || _uiState.value.isLoading) return
        val newPin = current + digit
        _uiState.update { it.copy(pin = newPin) }
        if (newPin.length == PIN_LENGTH) {
            submitPin(newPin)
        }
    }

    fun onBackspace() {
        val current = _uiState.value.pin
        if (current.isEmpty() || _uiState.value.isLoading) return
        _uiState.update { it.copy(pin = current.dropLast(1)) }
    }

    private fun submitPin(pin: String) {
        _uiState.update { it.copy(isLoading = true) }
        // Hook up PIN verification use-case here.
        _uiState.update { it.copy(isLoading = false) }
        _navigateToDashboard.tryEmit(Unit)
    }

    companion object {
        private const val PIN_LENGTH = 4
    }
}
