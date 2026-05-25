package com.pulasthi.tapit.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class DeleteAccountPinUiState(
    val pin: String = "",
)

class DeleteAccountPinViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DeleteAccountPinUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigateToDeleted = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToDeleted = _navigateToDeleted.asSharedFlow()

    fun onDigitPressed(digit: Int) {
        val current = _uiState.value.pin
        if (current.length >= PIN_LENGTH) return
        val newPin = current + digit
        _uiState.update { it.copy(pin = newPin) }
        if (newPin.length == PIN_LENGTH) {
            // Hook up PIN verification before account deletion.
            _navigateToDeleted.tryEmit(Unit)
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
