package com.pulasthi.tapit.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class QrCodeType {
    Personal,
    Payment,
}

data class MyQrUiState(
    val selectedType: QrCodeType = QrCodeType.Personal,
    val personalQrValue: String = "tapit://pay/personal/pulasthi",
    val paymentQrValue: String = "tapit://pay/merchant/pulasthi",
    val instruction: String = "Other TapIt users can scan this QR Code to send money to your bank account.",
)

class MyQrViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MyQrUiState())
    val uiState = _uiState.asStateFlow()

    fun onQrTypeSelected(type: QrCodeType) {
        _uiState.update { it.copy(selectedType = type) }
    }
}
