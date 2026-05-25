package com.pulasthi.tapit.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class GovPaymentCard(
    val id: String,
    val holderName: String,
    val maskedNumber: String,
)

data class GovPayUiState(
    val amount: String = "",
    val mobileNumber: String = "",
    val remarks: String = "",
    val cards: List<GovPaymentCard> = emptyList(),
    val selectedCardIndex: Int = 0,
    val isProcessingPayment: Boolean = false,
) {
    val selectedCard: GovPaymentCard?
        get() = cards.getOrNull(selectedCardIndex)

    val isFinesFormValid: Boolean =
        amount.toDoubleOrNull() != null &&
            amount.toDoubleOrNull()!! > 0 &&
            mobileNumber.filter { it.isDigit() }.length >= 9

    val formattedAmount: String
        get() = amount.toDoubleOrNull()?.let { "%.2f".format(it) } ?: amount
}

class GovPayViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GovPayUiState(cards = defaultCards))
    val uiState = _uiState.asStateFlow()

    private val _navigateToFinesForm = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToFinesForm = _navigateToFinesForm.asSharedFlow()

    private val _navigateToPayFines = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToPayFines = _navigateToPayFines.asSharedFlow()

    private val _navigateToSuccess = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToSuccess = _navigateToSuccess.asSharedFlow()

    fun onFinesPayClick() {
        _navigateToFinesForm.tryEmit(Unit)
    }

    fun onAmountChange(value: String) {
        val filtered = value.filter { it.isDigit() || it == '.' }
        _uiState.update { it.copy(amount = filtered) }
    }

    fun onMobileNumberChange(value: String) {
        _uiState.update { it.copy(mobileNumber = value.filter { c -> c.isDigit() }) }
    }

    fun onRemarksChange(value: String) {
        _uiState.update { it.copy(remarks = value) }
    }

    fun onCardSelected(index: Int) {
        _uiState.update { it.copy(selectedCardIndex = index) }
    }

    fun onFinesFormNext() {
        if (!_uiState.value.isFinesFormValid) return
        _navigateToPayFines.tryEmit(Unit)
    }

    fun onPayClick() {
        if (_uiState.value.isProcessingPayment) return
        _uiState.update { it.copy(isProcessingPayment = true) }
        // Hook up Gov Pay payment use-case here.
        _uiState.update { it.copy(isProcessingPayment = false) }
        _navigateToSuccess.tryEmit(Unit)
    }

    fun resetFlow() {
        _uiState.value = GovPayUiState(cards = defaultCards)
    }

    companion object {
        private val defaultCards = listOf(
            GovPaymentCard(
                id = "1",
                holderName = "R.M.K.T.Rathnayaka",
                maskedNumber = "9865 2343 **** ****",
            ),
            GovPaymentCard(
                id = "2",
                holderName = "W.M.P.A.Bandara",
                maskedNumber = "1536 1154 **** ****",
            ),
        )
    }
}
