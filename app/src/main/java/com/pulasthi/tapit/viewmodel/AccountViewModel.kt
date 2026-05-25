package com.pulasthi.tapit.viewmodel

import androidx.lifecycle.ViewModel
import com.pulasthi.tapit.data.PaymentCardRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

enum class CardBrand {
    MASTERCARD,
    VISA,
}

data class SavedCard(
    val id: String,
    val holderName: String,
    val maskedNumber: String,
    val brand: CardBrand = CardBrand.MASTERCARD,
)

data class AddCardFormState(
    val holderName: String = "",
    val cardNumber: String = "",
    val expiry: String = "",
    val cvv: String = "",
    val holderNameError: String? = null,
    val cardNumberError: String? = null,
    val expiryError: String? = null,
    val cvvError: String? = null,
) {
    val previewHolderName: String
        get() = holderName.ifBlank { "Cardholder Name" }

    val previewCardNumber: String
        get() {
            val digits = cardNumber.filter { it.isDigit() }
            if (digits.isEmpty()) return "Card Number xxxx xxxx"
            val grouped = digits.chunked(4).joinToString(" ")
            return if (digits.length < 16) grouped else grouped.take(19)
        }

    val previewExpiry: String
        get() = expiry.ifBlank { "12/26" }

}

data class AccountUiState(
    val cards: List<SavedCard> = emptyList(),
    val addCardForm: AddCardFormState = AddCardFormState(),
    val removeSelection: Set<String> = emptySet(),
    val showRemoveConfirmDialog: Boolean = false,
    val snackbarMessage: String? = null,
) {
    val hasRemoveSelection: Boolean
        get() = removeSelection.isNotEmpty()
}

class AccountViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AccountUiState(cards = PaymentCardRepository.snapshot()))
    val uiState = _uiState.asStateFlow()

    private val _navigateToAddCard = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToAddCard = _navigateToAddCard.asSharedFlow()

    private val _navigateToAddSuccess = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToAddSuccess = _navigateToAddSuccess.asSharedFlow()

    private val _navigateToRemoveSelect = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToRemoveSelect = _navigateToRemoveSelect.asSharedFlow()

    fun onAddCardClick() {
        clearAddCardForm()
        _navigateToAddCard.tryEmit(Unit)
    }

    fun onRemoveCardClick() {
        _uiState.update { it.copy(removeSelection = emptySet()) }
        _navigateToRemoveSelect.tryEmit(Unit)
    }

    fun onHolderNameChange(value: String) {
        _uiState.update {
            it.copy(
                addCardForm = it.addCardForm.copy(
                    holderName = value,
                    holderNameError = null,
                ),
            )
        }
    }

    fun onCardNumberChange(value: String) {
        val digits = value.filter { it.isDigit() }.take(16)
        val formatted = digits.chunked(4).joinToString(" ")
        _uiState.update {
            it.copy(
                addCardForm = it.addCardForm.copy(
                    cardNumber = formatted,
                    cardNumberError = null,
                ),
            )
        }
    }

    fun onExpiryChange(value: String) {
        val digits = value.filter { it.isDigit() }.take(4)
        val formatted = when {
            digits.length <= 2 -> digits
            else -> "${digits.take(2)}/${digits.drop(2)}"
        }
        _uiState.update {
            it.copy(
                addCardForm = it.addCardForm.copy(
                    expiry = formatted,
                    expiryError = null,
                ),
            )
        }
    }

    fun onCvvChange(value: String) {
        val filtered = value.filter { it.isDigit() }.take(4)
        _uiState.update {
            it.copy(
                addCardForm = it.addCardForm.copy(
                    cvv = filtered,
                    cvvError = null,
                ),
            )
        }
    }

    fun onSubmitAddCard() {
        val form = _uiState.value.addCardForm
        val holderError = if (form.holderName.isBlank()) "Enter cardholder name" else null
        val numberError = when {
            form.cardNumber.filter { it.isDigit() }.isEmpty() -> "Enter card number"
            !Companion.isValidCardNumber(form.cardNumber) -> "Enter a valid card number"
            else -> null
        }
        val expiryError = when {
            form.expiry.isBlank() -> "Enter expiry date"
            !Companion.isValidExpiry(form.expiry) -> "Enter a valid expiry (MM/YY)"
            else -> null
        }
        val cvvError = when {
            form.cvv.isBlank() -> "Enter CVV"
            !Companion.isValidCvv(form.cvv) -> "Enter a valid CVV"
            else -> null
        }

        if (holderError != null || numberError != null || expiryError != null || cvvError != null) {
            _uiState.update {
                it.copy(
                    addCardForm = form.copy(
                        holderNameError = holderError,
                        cardNumberError = numberError,
                        expiryError = expiryError,
                        cvvError = cvvError,
                    ),
                )
            }
            return
        }

        val digits = form.cardNumber.filter { it.isDigit() }
        val newCard = SavedCard(
            id = UUID.randomUUID().toString(),
            holderName = form.holderName.trim(),
            maskedNumber = maskCardNumber(digits),
            brand = detectBrand(digits),
        )
        PaymentCardRepository.addCard(newCard)
        _uiState.update {
            it.copy(
                cards = PaymentCardRepository.snapshot(),
                addCardForm = AddCardFormState(),
            )
        }
        _navigateToAddSuccess.tryEmit(Unit)
    }

    fun onToggleRemoveSelection(cardId: String) {
        _uiState.update { state ->
            val next = state.removeSelection.toMutableSet()
            if (cardId in next) next.remove(cardId) else next.add(cardId)
            state.copy(removeSelection = next)
        }
    }

    fun onRequestRemoveConfirm() {
        if (!_uiState.value.hasRemoveSelection) return
        _uiState.update { it.copy(showRemoveConfirmDialog = true) }
    }

    fun onDismissRemoveDialog() {
        _uiState.update { it.copy(showRemoveConfirmDialog = false) }
    }

    fun onConfirmRemoveCards() {
        val ids = _uiState.value.removeSelection
        if (ids.isEmpty()) return
        PaymentCardRepository.removeCards(ids)
        _uiState.update {
            it.copy(
                cards = PaymentCardRepository.snapshot(),
                removeSelection = emptySet(),
                showRemoveConfirmDialog = false,
                snackbarMessage = "Remove Card Successfully",
            )
        }
    }

    fun onSnackbarShown() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun clearAddCardForm() {
        _uiState.update { it.copy(addCardForm = AddCardFormState()) }
    }

    companion object {
        fun maskCardNumber(digits: String): String {
            val first = digits.take(8).chunked(4).joinToString(" ")
            return "$first **** ****".trim()
        }

        fun detectBrand(digits: String): CardBrand {
            return when (digits.firstOrNull()) {
                '4' -> CardBrand.VISA
                else -> CardBrand.MASTERCARD
            }
        }

        internal fun isValidCardNumber(number: String): Boolean {
            val digits = number.filter { it.isDigit() }
            if (digits.length !in 13..16) return false
            var sum = 0
            var alternate = false
            for (i in digits.indices.reversed()) {
                var n = digits[i].digitToInt()
                if (alternate) {
                    n *= 2
                    if (n > 9) n -= 9
                }
                sum += n
                alternate = !alternate
            }
            return sum % 10 == 0
        }

        internal fun isValidExpiry(expiry: String): Boolean {
            val parts = expiry.split("/")
            if (parts.size != 2) return false
            val month = parts[0].toIntOrNull() ?: return false
            val year = parts[1].toIntOrNull() ?: return false
            if (month !in 1..12) return false
            if (parts[1].length != 2) return false
            val cal = java.util.Calendar.getInstance()
            val currentYear = cal.get(java.util.Calendar.YEAR) % 100
            val currentMonth = cal.get(java.util.Calendar.MONTH) + 1
            return year > currentYear || (year == currentYear && month >= currentMonth)
        }

        internal fun isValidCvv(cvv: String): Boolean {
            return cvv.length in 3..4 && cvv.all { it.isDigit() }
        }
    }
}
