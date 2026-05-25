package com.pulasthi.tapit.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ReEnterPinUiState(
    val pin: String = "",
    val expectedPin: String = "",
    val errorMessage: String? = null,
)

class ReEnterPinViewModel(
    expectedPin: String,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReEnterPinUiState(expectedPin = expectedPin))
    val uiState = _uiState.asStateFlow()

    private val _navigateToSuccess = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToSuccess = _navigateToSuccess.asSharedFlow()

    fun onDigitPressed(digit: Int) {
        val current = _uiState.value.pin
        if (current.length >= PIN_LENGTH) return
        val newPin = current + digit
        _uiState.update { it.copy(pin = newPin, errorMessage = null) }
        if (newPin.length == PIN_LENGTH) {
            verifyAndNavigate(newPin)
        }
    }

    fun onBackspace() {
        val current = _uiState.value.pin
        if (current.isEmpty()) return
        _uiState.update { it.copy(pin = current.dropLast(1), errorMessage = null) }
    }

    private fun verifyAndNavigate(pin: String) {
        if (pin == _uiState.value.expectedPin) {
            _navigateToSuccess.tryEmit(Unit)
        } else {
            _uiState.update {
                it.copy(
                    pin = "",
                    errorMessage = "PIN codes do not match. Please try again.",
                )
            }
        }
    }

    companion object {
        private const val PIN_LENGTH = 4
    }
}
