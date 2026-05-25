package com.pulasthi.tapit.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CreateAccountUiState(
    val countryCode: String = "+94",
    val mobileNumber: String = "",
    val email: String = "",
    val privacyPolicyAccepted: Boolean = false,
    val termsAccepted: Boolean = false,
    val isLoading: Boolean = false,
) {
    val isDoneEnabled: Boolean =
        mobileNumber.length >= 9 &&
            email.contains("@") &&
            email.contains(".") &&
            privacyPolicyAccepted &&
            termsAccepted
}

class CreateAccountViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CreateAccountUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigateToCreatePin = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToCreatePin = _navigateToCreatePin.asSharedFlow()

    fun onMobileNumberChange(value: String) {
        _uiState.update { it.copy(mobileNumber = value.filter { c -> c.isDigit() }) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value.trim()) }
    }

    fun onPrivacyPolicyToggle(accepted: Boolean) {
        _uiState.update { it.copy(privacyPolicyAccepted = accepted) }
    }

    fun onTermsToggle(accepted: Boolean) {
        _uiState.update { it.copy(termsAccepted = accepted) }
    }

    fun onDoneClick() {
        if (!_uiState.value.isDoneEnabled) return
        _uiState.update { it.copy(isLoading = true) }
        // Hook up registration use-case here.
        _uiState.update { it.copy(isLoading = false) }
        _navigateToCreatePin.tryEmit(Unit)
    }
}
