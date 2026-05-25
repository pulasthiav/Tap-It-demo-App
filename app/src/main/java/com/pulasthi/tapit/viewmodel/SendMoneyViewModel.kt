package com.pulasthi.tapit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulasthi.tapit.data.BeneficiaryRepository
import com.pulasthi.tapit.data.PaymentCardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

data class TransferBeneficiary(
    val id: String,
    val name: String,
    val bankName: String,
    val accountNumber: String,
    val branchName: String = "",
    val mobileNumber: String = "",
)

enum class SendMoneyTransferStatus {
    Idle,
    Processing,
    Success,
}

data class SendMoneyUiState(
    val selectedBeneficiaryId: String? = null,
    val recipientName: String = "",
    val recipientBank: String = "",
    val recipientAccount: String = "",
    val recipientMobile: String = "",
    val remarks: String = "",
    val sendSmsToReceiver: Boolean = false,
    val saveAsFavourite: Boolean = false,
    val amount: String = "",
    val cards: List<GovPaymentCard> = emptyList(),
    val selectedCardIndex: Int = 0,
    val recipientNameError: String? = null,
    val recipientBankError: String? = null,
    val recipientAccountError: String? = null,
    val amountError: String? = null,
    val transferStatus: SendMoneyTransferStatus = SendMoneyTransferStatus.Idle,
) {
    val hasSelectedBeneficiary: Boolean
        get() = selectedBeneficiaryId != null &&
            recipientName.isNotBlank() &&
            recipientBank.isNotBlank() &&
            recipientAccount.isNotBlank()

    val activeRecipient: TransferBeneficiary
        get() = TransferBeneficiary(
            id = selectedBeneficiaryId ?: "manual",
            name = recipientName.trim(),
            bankName = recipientBank.trim(),
            accountNumber = recipientAccount.trim(),
            branchName = "",
            mobileNumber = recipientMobile.trim(),
        )

    val transferAmount: Double?
        get() = amount.toDoubleOrNull()

    val serviceFee: Double
        get() = transferAmount?.let { it * SendMoneyViewModel.SERVICE_FEE_RATE } ?: 0.0

    val totalDebit: Double
        get() = (transferAmount ?: 0.0) + serviceFee

    val formattedAmount: String
        get() = transferAmount?.let { "%.2f".format(it) } ?: "0.00"

    val formattedServiceFee: String
        get() = "%.2f".format(serviceFee)

    val formattedTotal: String
        get() = "%.2f".format(totalDebit)

    val isRecipientStepValid: Boolean
        get() = recipientName.isNotBlank() &&
            recipientBank.isNotBlank() &&
            recipientAccount.filter { it.isDigit() }.length >= 5
}

class SendMoneyViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SendMoneyUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigateToAmount = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToAmount = _navigateToAmount.asSharedFlow()

    private val _navigateToConfirm = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToConfirm = _navigateToConfirm.asSharedFlow()

    private val _navigateToSuccess = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToSuccess = _navigateToSuccess.asSharedFlow()

    fun onBeneficiarySelected(beneficiary: TransferBeneficiary) {
        _uiState.update {
            it.copy(
                selectedBeneficiaryId = beneficiary.id,
                recipientName = beneficiary.name,
                recipientBank = beneficiary.bankName,
                recipientAccount = beneficiary.accountNumber,
                recipientMobile = beneficiary.mobileNumber,
                recipientNameError = null,
                recipientBankError = null,
                recipientAccountError = null,
            )
        }
    }

    fun clearRecipientForManualEntry() {
        _uiState.update {
            it.copy(
                selectedBeneficiaryId = null,
                recipientName = "",
                recipientBank = "",
                recipientAccount = "",
                recipientMobile = "",
                remarks = "",
                sendSmsToReceiver = false,
                saveAsFavourite = false,
                recipientNameError = null,
                recipientBankError = null,
                recipientAccountError = null,
            )
        }
    }

    fun onRecipientNameChange(value: String) {
        _uiState.update {
            it.copy(
                recipientName = value,
                selectedBeneficiaryId = null,
                recipientNameError = null,
            )
        }
    }

    fun onRecipientBankChange(value: String) {
        _uiState.update {
            it.copy(recipientBank = value, selectedBeneficiaryId = null, recipientBankError = null)
        }
    }

    fun onRecipientAccountChange(value: String) {
        val filtered = value.filter { it.isDigit() }
        _uiState.update {
            it.copy(
                recipientAccount = filtered,
                selectedBeneficiaryId = null,
                recipientAccountError = null,
            )
        }
    }

    fun onRecipientMobileChange(value: String) {
        val digits = value.filter { it.isDigit() }.take(10)
        _uiState.update {
            it.copy(recipientMobile = digits, selectedBeneficiaryId = null)
        }
    }

    fun onRemarksChange(value: String) {
        _uiState.update { it.copy(remarks = value) }
    }

    fun onSendSmsChanged(checked: Boolean) {
        _uiState.update { it.copy(sendSmsToReceiver = checked) }
    }

    fun onSaveAsFavouriteChanged(checked: Boolean) {
        _uiState.update { it.copy(saveAsFavourite = checked) }
    }

    fun onAmountChange(value: String) {
        val filtered = value.filter { it.isDigit() || it == '.' }
        _uiState.update { it.copy(amount = filtered, amountError = null) }
    }

    fun onCardSelected(index: Int) {
        _uiState.update { it.copy(selectedCardIndex = index) }
    }

    fun onRecipientNext() {
        val state = _uiState.value
        val nameError = if (state.recipientName.isBlank()) "Enter recipient name" else null
        val bankError = if (state.recipientBank.isBlank()) "Enter bank name" else null
        val accountError = when {
            state.recipientAccount.isBlank() -> "Enter account number"
            state.recipientAccount.filter { it.isDigit() }.length < 5 -> "Enter a valid account number"
            else -> null
        }

        if (nameError != null || bankError != null || accountError != null) {
            _uiState.update {
                it.copy(
                    recipientNameError = nameError,
                    recipientBankError = bankError,
                    recipientAccountError = accountError,
                )
            }
            return
        }

        refreshWalletCards()
        _navigateToAmount.tryEmit(Unit)
    }

    fun onAmountNext() {
        val amount = _uiState.value.transferAmount
        if (amount == null || amount <= 0) {
            _uiState.update { it.copy(amountError = "Enter a valid amount") }
            return
        }
        if (_uiState.value.cards.isEmpty()) {
            _uiState.update { it.copy(amountError = "Add a card in My Wallet first") }
            return
        }
        if (!_uiState.value.isRecipientStepValid) {
            _uiState.update { it.copy(amountError = "Select or enter a recipient first") }
            return
        }
        _navigateToConfirm.tryEmit(Unit)
    }

    fun onConfirmTransfer() {
        val state = _uiState.value
        if (state.transferStatus == SendMoneyTransferStatus.Processing) return
        if (!state.isRecipientStepValid || state.transferAmount == null || state.cards.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(transferStatus = SendMoneyTransferStatus.Processing) }
            delay(1_200L)
            if (state.saveAsFavourite && state.selectedBeneficiaryId == null) {
                BeneficiaryRepository.addBeneficiary(
                    TransferBeneficiary(
                        id = UUID.randomUUID().toString(),
                        name = state.recipientName.trim(),
                        bankName = state.recipientBank.trim(),
                        accountNumber = state.recipientAccount.trim(),
                        mobileNumber = state.recipientMobile.trim(),
                    ),
                )
            }
            _uiState.update { it.copy(transferStatus = SendMoneyTransferStatus.Success) }
            _navigateToSuccess.tryEmit(Unit)
        }
    }

    fun refreshWalletCards() {
        viewModelScope.launch(Dispatchers.Default) {
            val walletCards = PaymentCardRepository.snapshot().map { saved ->
                GovPaymentCard(
                    id = saved.id,
                    holderName = saved.holderName,
                    maskedNumber = saved.maskedNumber,
                )
            }
            _uiState.update {
                it.copy(
                    cards = walletCards,
                    selectedCardIndex = 0.coerceAtMost((walletCards.size - 1).coerceAtLeast(0)),
                )
            }
        }
    }

    fun resetFlow() {
        _uiState.value = SendMoneyUiState()
    }

    companion object {
        const val SERVICE_FEE_RATE = 0.01
    }
}
