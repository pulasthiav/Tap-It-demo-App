package com.pulasthi.tapit.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

enum class BillCategoryId(val id: String) {
    ELECTRICITY("electricity"),
    WATER("water"),
    PHONE("phone"),
    TV("tv"),
    INTERNET("internet"),
}

enum class PayBillsContentMode {
    HUB,
    PROVIDERS,
}

data class BillCategoryItem(
    val id: BillCategoryId,
    val title: String,
    val icon: ImageVector,
)

/** Brand styling for provider logo badges (no remote assets required). */
data class BillProviderBrand(
    val primaryColor: Color,
    val secondaryColor: Color,
    val logoText: String,
    val logoSubtext: String? = null,
)

data class BillProvider(
    val id: String,
    val name: String,
    val categoryId: BillCategoryId,
    val brand: BillProviderBrand,
)

sealed class PayBillsHubItem {
    data class Category(val category: BillCategoryItem) : PayBillsHubItem()
    data class Provider(val provider: BillProvider) : PayBillsHubItem()
}

enum class BillPaymentStatus {
    Idle,
    Processing,
    Success,
    Failed,
}

private val defaultBillCategories = listOf(
    BillCategoryItem(BillCategoryId.ELECTRICITY, "Electricity Bill", Icons.Default.Bolt),
    BillCategoryItem(BillCategoryId.WATER, "Water Bill", Icons.Default.WaterDrop),
    BillCategoryItem(BillCategoryId.PHONE, "Phone Bill", Icons.Default.Phone),
    BillCategoryItem(BillCategoryId.TV, "TV Bill", Icons.Default.Tv),
    BillCategoryItem(BillCategoryId.INTERNET, "Internet Bill", Icons.Default.Laptop),
)

private val allBillProviders = listOf(
    BillProvider(
        id = "ceb",
        name = "Ceylon Electricity Board",
        categoryId = BillCategoryId.ELECTRICITY,
        brand = BillProviderBrand(
            primaryColor = Color(0xFFC62828),
            secondaryColor = Color(0xFFFFC107),
            logoText = "CEB",
        ),
    ),
    BillProvider(
        id = "leco",
        name = "Lanka Electricity Co.",
        categoryId = BillCategoryId.ELECTRICITY,
        brand = BillProviderBrand(
            primaryColor = Color(0xFFFFC107),
            secondaryColor = Color(0xFF1B5E20),
            logoText = "LECO",
        ),
    ),
    BillProvider(
        id = "nwsdb",
        name = "Water Board",
        categoryId = BillCategoryId.WATER,
        brand = BillProviderBrand(
            primaryColor = Color(0xFF1565C0),
            secondaryColor = Color(0xFF42A5F5),
            logoText = "NWSDB",
            logoSubtext = "Water",
        ),
    ),
    BillProvider(
        id = "dialog_phone",
        name = "Dialog",
        categoryId = BillCategoryId.PHONE,
        brand = BillProviderBrand(
            primaryColor = Color(0xFFE53935),
            secondaryColor = Color(0xFF8E24AA),
            logoText = "Dialog",
        ),
    ),
    BillProvider(
        id = "mobitel",
        name = "Mobitel",
        categoryId = BillCategoryId.PHONE,
        brand = BillProviderBrand(
            primaryColor = Color(0xFF00897B),
            secondaryColor = Color(0xFF1565C0),
            logoText = "MOBI",
            logoSubtext = "TEL",
        ),
    ),
    BillProvider(
        id = "hutch",
        name = "Hutch",
        categoryId = BillCategoryId.PHONE,
        brand = BillProviderBrand(
            primaryColor = Color(0xFFFF6F00),
            secondaryColor = Color(0xFFE65100),
            logoText = "HUTCH",
        ),
    ),
    BillProvider(
        id = "airtel",
        name = "Airtel",
        categoryId = BillCategoryId.PHONE,
        brand = BillProviderBrand(
            primaryColor = Color(0xFFE53935),
            secondaryColor = Color(0xFFFFFFFF),
            logoText = "airtel",
        ),
    ),
    BillProvider(
        id = "peo_tv",
        name = "Peo TV",
        categoryId = BillCategoryId.TV,
        brand = BillProviderBrand(
            primaryColor = Color(0xFF1565C0),
            secondaryColor = Color(0xFF43A047),
            logoText = "PEO",
            logoSubtext = "TV",
        ),
    ),
    BillProvider(
        id = "dialog_tv",
        name = "Dialog Television",
        categoryId = BillCategoryId.TV,
        brand = BillProviderBrand(
            primaryColor = Color(0xFFE53935),
            secondaryColor = Color(0xFF6A1B9A),
            logoText = "Dialog",
            logoSubtext = "TV",
        ),
    ),
    BillProvider(
        id = "slt_fiber",
        name = "SLT Fiber",
        categoryId = BillCategoryId.INTERNET,
        brand = BillProviderBrand(
            primaryColor = Color(0xFF1565C0),
            secondaryColor = Color(0xFF00897B),
            logoText = "SLT",
            logoSubtext = "Fibre",
        ),
    ),
    BillProvider(
        id = "dialog_broadband",
        name = "Dialog Home Broadband",
        categoryId = BillCategoryId.INTERNET,
        brand = BillProviderBrand(
            primaryColor = Color(0xFFE53935),
            secondaryColor = Color(0xFFAD1457),
            logoText = "Dialog",
            logoSubtext = "BB",
        ),
    ),
)

data class PayBillsUiState(
    val contentMode: PayBillsContentMode = PayBillsContentMode.HUB,
    val categories: List<BillCategoryItem> = defaultBillCategories,
    val providers: List<BillProvider> = allBillProviders,
    val searchQuery: String = "",
    val selectedCategory: BillCategoryItem? = null,
    val selectedProvider: BillProvider? = null,
    val accountNumber: String = "",
    val amount: String = "",
    val cards: List<GovPaymentCard> = emptyList(),
    val selectedCardIndex: Int = 0,
    val accountNumberError: String? = null,
    val amountError: String? = null,
    val paymentStatus: BillPaymentStatus = BillPaymentStatus.Idle,
    val paymentErrorMessage: String? = null,
) {
    val hubListItems: List<PayBillsHubItem>
        get() {
            val query = searchQuery.trim()
            if (query.isEmpty()) {
                return categories.map { PayBillsHubItem.Category(it) }
            }
            val matchingCategories = categories.filter {
                it.title.contains(query, ignoreCase = true)
            }
            val matchingProviders = providers.filter { provider ->
                provider.name.contains(query, ignoreCase = true) ||
                    categoryTitle(provider.categoryId).contains(query, ignoreCase = true)
            }
            val items = mutableListOf<PayBillsHubItem>()
            matchingCategories.forEach { items.add(PayBillsHubItem.Category(it)) }
            matchingProviders.forEach { provider ->
                if (items.none { it is PayBillsHubItem.Provider && it.provider.id == provider.id }) {
                    items.add(PayBillsHubItem.Provider(provider))
                }
            }
            return items
        }

    val filteredProviders: List<BillProvider>
        get() {
            val category = selectedCategory ?: return emptyList()
            val query = searchQuery.trim()
            return providers.filter { it.categoryId == category.id }.filter { provider ->
                if (query.isEmpty()) return@filter true
                provider.name.contains(query, ignoreCase = true) ||
                    category.title.contains(query, ignoreCase = true)
            }
        }

    val selectedCard: GovPaymentCard?
        get() = cards.getOrNull(selectedCardIndex)

    val hubTitle: String
        get() = "Pay Bills"

    val providersScreenTitle: String
        get() = selectedCategory?.title ?: "Pay Bills"

    val paymentFormTitle: String
        get() = "Pay Your Bill"

    val serviceFee: Double
        get() = amount.toDoubleOrNull()?.let { it * 0.01 } ?: 0.0

    val totalAmount: Double
        get() = (amount.toDoubleOrNull() ?: 0.0) + serviceFee

    private fun categoryTitle(id: BillCategoryId): String =
        categories.firstOrNull { it.id == id }?.title.orEmpty()
}

class PayBillsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PayBillsUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigateToPaymentForm = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToPaymentForm = _navigateToPaymentForm.asSharedFlow()

    private val _navigateToSuccess = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToSuccess = _navigateToSuccess.asSharedFlow()

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onCategorySelected(category: BillCategoryItem) {
        _uiState.update {
            it.copy(
                contentMode = PayBillsContentMode.PROVIDERS,
                selectedCategory = category,
                selectedProvider = null,
                searchQuery = "",
            )
        }
    }

    fun onProviderSelected(provider: BillProvider) {
        val category = _uiState.value.categories.firstOrNull { it.id == provider.categoryId }
        _uiState.update {
            it.copy(
                selectedCategory = category,
                selectedProvider = provider,
                accountNumber = "",
                amount = "",
                accountNumberError = null,
                amountError = null,
                paymentStatus = BillPaymentStatus.Idle,
                paymentErrorMessage = null,
            )
        }
        refreshWalletCards()
        _navigateToPaymentForm.tryEmit(Unit)
    }

    fun onHubProviderSelected(provider: BillProvider) {
        onProviderSelected(provider)
    }

    fun onExitProviders() {
        _uiState.update {
            it.copy(
                contentMode = PayBillsContentMode.HUB,
                selectedCategory = null,
                searchQuery = "",
            )
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

    fun onAccountNumberChange(value: String) {
        _uiState.update {
            it.copy(
                accountNumber = value.filter { c -> c.isDigit() || c.isLetter() || c == '-' || c == ' ' },
                accountNumberError = null,
            )
        }
    }

    fun onAmountChange(value: String) {
        val filtered = value.filter { it.isDigit() || it == '.' }
        _uiState.update {
            it.copy(amount = filtered, amountError = null)
        }
    }

    fun onCardSelected(index: Int) {
        _uiState.update { it.copy(selectedCardIndex = index) }
    }

    fun onProcessPayment() {
        val state = _uiState.value
        if (state.paymentStatus == BillPaymentStatus.Processing) return
        if (state.selectedProvider == null) return

        val accountError = when {
            state.accountNumber.isBlank() -> "Enter account number"
            state.accountNumber.filter { it.isDigit() }.length < 5 -> "Enter a valid account number"
            else -> null
        }
        val amountError = when {
            state.amount.isBlank() -> "Enter amount"
            state.amount.toDoubleOrNull() == null || state.amount.toDoubleOrNull()!! <= 0 ->
                "Enter a valid amount"
            else -> null
        }
        val cardError = if (state.cards.isEmpty()) "Add a card in My Wallet first" else null

        if (accountError != null || amountError != null || cardError != null) {
            _uiState.update {
                it.copy(
                    accountNumberError = accountError,
                    amountError = amountError ?: cardError,
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    paymentStatus = BillPaymentStatus.Processing,
                    paymentErrorMessage = null,
                    accountNumberError = null,
                    amountError = null,
                )
            }
            delay(1_200L)
            _uiState.update { it.copy(paymentStatus = BillPaymentStatus.Success) }
            _navigateToSuccess.tryEmit(Unit)
        }
    }

    fun resetFlow() {
        _uiState.value = PayBillsUiState()
    }
}
