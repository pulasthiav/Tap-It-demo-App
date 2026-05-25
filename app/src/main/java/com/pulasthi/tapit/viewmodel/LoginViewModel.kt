package com.pulasthi.tapit.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginUiState(
    val countryCode: String = "+94",
    val mobileNumber: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
) {
    val isLoginEnabled: Boolean =
        mobileNumber.isNotBlank() && password.length >= 4
}

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigateToEnterPin = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToEnterPin = _navigateToEnterPin.asSharedFlow()

    fun onMobileNumberChange(value: String) {
        _uiState.update { it.copy(mobileNumber = value.filter { c -> c.isDigit() }) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onLoginClick() {
        if (!_uiState.value.isLoginEnabled) return
        _uiState.update { it.copy(isLoading = true) }
        // Hook up authentication use-case here.
        _uiState.update { it.copy(isLoading = false) }
        _navigateToEnterPin.tryEmit(Unit)
    }
}
