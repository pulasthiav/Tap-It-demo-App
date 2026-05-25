package com.pulasthi.tapit.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulasthi.tapit.data.PaymentCardRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReloadOperatorBrand(
    val primaryColor: Color,
    val secondaryColor: Color,
    val logoText: String,
)

data class ReloadOperator(
    val id: String,
    val name: String,
    val prefixes: List<String>,
    val brand: ReloadOperatorBrand,
)

enum class ReloadType {
    DATA,
    VOICE,
}

enum class ReloadAmountOption {
    CUSTOM,
    RS_50,
    RS_100,
}

enum class ReloadStatus {
    Idle,
    Processing,
    Success,
}

data class ReloadUiState(
    val operators: List<ReloadOperator> = emptyList(),
    /** Current [HorizontalPager] page; each page shows up to [ReloadViewModel.OPERATORS_PER_PAGE] operators. */
    val selectedOperatorPage: Int = 0,
    val selectedOperatorIndex: Int = 0,
    val mobileNumber: String = "",
    val reloadType: ReloadType = ReloadType.DATA,
    val amountOption: ReloadAmountOption = ReloadAmountOption.RS_100,
    val customAmount: String = "",
    val remarks: String = "",
    val cards: List<GovPaymentCard> = emptyList(),
    val selectedCardIndex: Int = 0,
    val mobileError: String? = null,
    val amountError: String? = null,
    val reloadStatus: ReloadStatus = ReloadStatus.Idle,
) {
    val operatorPageCount: Int
        get() = if (operators.isEmpty()) {
            1
        } else {
            (operators.size + ReloadViewModel.OPERATORS_PER_PAGE - 1) / ReloadViewModel.OPERATORS_PER_PAGE
        }

    val selectedOperator: ReloadOperator
        get() = operators.getOrElse(selectedOperatorIndex.coerceIn(operators.indices)) {
            operators.first()
        }

    val resolvedAmount: Double?
        get() = when (amountOption) {
            ReloadAmountOption.CUSTOM -> customAmount.toDoubleOrNull()
            ReloadAmountOption.RS_50 -> 50.0
            ReloadAmountOption.RS_100 -> 100.0
        }

    val formattedAmount: String
        get() = resolvedAmount?.let { "%.2f".format(it) } ?: "0.00"

    val isHomeStepValid: Boolean
        get() = ReloadViewModel.isValidMobileForOperator(mobileNumber, selectedOperator) &&
            resolvedAmount != null && resolvedAmount!! > 0

    val isFormStepValid: Boolean
        get() = isHomeStepValid && cards.isNotEmpty()
}

class ReloadViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        ReloadUiState(operators = buildDefaultOperators()),
    )
    val uiState = _uiState.asStateFlow()

    private val _navigateToPaymentForm = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToPaymentForm = _navigateToPaymentForm.asSharedFlow()

    private val _navigateToConfirm = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToConfirm = _navigateToConfirm.asSharedFlow()

    private val _navigateToSuccess = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToSuccess = _navigateToSuccess.asSharedFlow()

    /**
     * Called when the pager [currentPage] changes (swipe) or when a dot is tapped (after scroll).
     * Updates [selectedOperatorPage] and the active operator index immediately.
     */
    fun onOperatorPageChanged(page: Int) {
        val state = _uiState.value
        if (state.operators.isEmpty()) return
        val safePage = page.coerceIn(0, state.operatorPageCount - 1)
        val pageStartIndex = safePage * OPERATORS_PER_PAGE
        val pageEndIndex = (pageStartIndex + OPERATORS_PER_PAGE - 1).coerceAtMost(state.operators.lastIndex)
        val indexOnPage = if (state.selectedOperatorIndex / OPERATORS_PER_PAGE == safePage) {
            state.selectedOperatorIndex.coerceIn(pageStartIndex, pageEndIndex)
        } else {
            pageStartIndex
        }
        if (safePage == state.selectedOperatorPage && indexOnPage == state.selectedOperatorIndex) {
            return
        }
        _uiState.update {
            it.copy(
                selectedOperatorPage = safePage,
                selectedOperatorIndex = indexOnPage,
                mobileError = null,
            )
        }
    }

    fun onOperatorSelected(index: Int) {
        _uiState.update {
            val safeIndex = index.coerceIn(0, it.operators.lastIndex)
            it.copy(
                selectedOperatorIndex = safeIndex,
                selectedOperatorPage = safeIndex / OPERATORS_PER_PAGE,
                mobileError = null,
            )
        }
    }

    fun onMobileNumberChange(value: String) {
        val digits = value.filter { it.isDigit() }.take(10)
        _uiState.update {
            it.copy(mobileNumber = digits, mobileError = null)
        }
    }

    fun onReloadTypeSelected(type: ReloadType) {
        _uiState.update { it.copy(reloadType = type) }
    }

    fun onAmountOptionSelected(option: ReloadAmountOption) {
        _uiState.update {
            it.copy(amountOption = option, amountError = null)
        }
    }

    fun onCustomAmountChange(value: String) {
        val filtered = value.filter { it.isDigit() || it == '.' }
        _uiState.update {
            it.copy(
                customAmount = filtered,
                amountOption = ReloadAmountOption.CUSTOM,
                amountError = null,
            )
        }
    }

    fun onRemarksChange(value: String) {
        _uiState.update { it.copy(remarks = value) }
    }

    fun onCardSelected(index: Int) {
        _uiState.update { it.copy(selectedCardIndex = index) }
    }

    fun onHomeNext() {
        val state = _uiState.value
        val mobileError = validateMobile(state.mobileNumber, state.selectedOperator)
        val amountError = validateAmount(state.resolvedAmount)

        if (mobileError != null || amountError != null) {
            _uiState.update {
                it.copy(mobileError = mobileError, amountError = amountError)
            }
            return
        }

        refreshWalletCards()
        _navigateToPaymentForm.tryEmit(Unit)
    }

    fun onPaymentFormNext() {
        val state = _uiState.value
        if (state.cards.isEmpty()) {
            _uiState.update { it.copy(amountError = "Add a card in My Wallet first") }
            return
        }
        if (!state.isFormStepValid) {
            onHomeNext()
            return
        }
        _navigateToConfirm.tryEmit(Unit)
    }

    fun onConfirmPay() {
        val state = _uiState.value
        if (state.reloadStatus == ReloadStatus.Processing) return
        if (!state.isFormStepValid) return

        viewModelScope.launch {
            _uiState.update { it.copy(reloadStatus = ReloadStatus.Processing) }
            delay(1_200L)
            _uiState.update { it.copy(reloadStatus = ReloadStatus.Success) }
            _navigateToSuccess.tryEmit(Unit)
        }
    }

    fun refreshWalletCards() {
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

    fun resetFlow() {
        _uiState.value = ReloadUiState(operators = buildDefaultOperators())
    }

    companion object {
        const val OPERATORS_PER_PAGE = 2

        fun buildDefaultOperators(): List<ReloadOperator> = listOf(
            ReloadOperator(
                id = "dialog",
                name = "Dialog",
                prefixes = listOf("077"),
                brand = ReloadOperatorBrand(
                    primaryColor = Color(0xFFE53935),
                    secondaryColor = Color(0xFF8E24AA),
                    logoText = "Dialog",
                ),
            ),
            ReloadOperator(
                id = "mobitel",
                name = "Mobitel",
                prefixes = listOf("071", "070"),
                brand = ReloadOperatorBrand(
                    primaryColor = Color(0xFF00897B),
                    secondaryColor = Color(0xFF1565C0),
                    logoText = "MOBI",
                ),
            ),
            ReloadOperator(
                id = "hutch",
                name = "Hutch",
                prefixes = listOf("078"),
                brand = ReloadOperatorBrand(
                    primaryColor = Color(0xFFFF6F00),
                    secondaryColor = Color(0xFFE65100),
                    logoText = "HUTCH",
                ),
            ),
            ReloadOperator(
                id = "airtel",
                name = "Airtel",
                prefixes = listOf("075"),
                brand = ReloadOperatorBrand(
                    primaryColor = Color(0xFFE53935),
                    secondaryColor = Color(0xFFFFFFFF),
                    logoText = "airtel",
                ),
            ),
            ReloadOperator(
                id = "slt_mobitel",
                name = "SLT Mobitel",
                prefixes = listOf("076"),
                brand = ReloadOperatorBrand(
                    primaryColor = Color(0xFF1565C0),
                    secondaryColor = Color(0xFF43A047),
                    logoText = "SLT",
                ),
            ),
        )

        internal fun normalizeMobile(number: String): String {
            val digits = number.filter { it.isDigit() }
            return when {
                digits.startsWith("94") && digits.length >= 11 -> digits.drop(2).take(9)
                digits.startsWith("0") -> digits.drop(1).take(9)
                else -> digits.take(9)
            }
        }

        internal fun isValidMobileForOperator(number: String, operator: ReloadOperator): Boolean {
            val normalized = normalizeMobile(number)
            if (normalized.length != 9) return false
            return operator.prefixes.any { prefix ->
                val carrierPrefix = prefix.trimStart('0')
                normalized.startsWith(carrierPrefix)
            }
        }

        fun validateMobile(number: String, operator: ReloadOperator): String? {
            return when {
                number.isBlank() -> "Enter mobile number"
                !isValidMobileForOperator(number, operator) ->
                    "Enter a valid ${operator.name} number (${operator.prefixes.joinToString(", ")} prefix)"
                else -> null
            }
        }

        fun validateAmount(amount: Double?): String? {
            return when {
                amount == null || amount <= 0 -> "Select or enter a valid amount"
                amount > 50_000 -> "Amount exceeds maximum reload limit"
                else -> null
            }
        }
    }
}
