package com.pulasthi.tapit.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class TransactionStatus {
    Paid,
    Failed,
    Received,
}

data class HistoryTab(
    val id: String,
    val label: String,
)

data class TransactionItem(
    val id: String,
    val dateTime: String,
    val name: String,
    val amount: String,
    val status: TransactionStatus,
    val isDebit: Boolean = true,
)

data class HistoryUiState(
    val tabs: List<HistoryTab> = listOf(
        HistoryTab("all", "All"),
        HistoryTab("received", "Received"),
        HistoryTab("paid", "Paid"),
        HistoryTab("failed", "Failed"),
    ),
    val selectedTabId: String = "all",
    val fromDate: String = "01-01-2026",
    val toDate: String = "15-01-2026",
    val allTransactions: List<TransactionItem> = emptyList(),
) {
    val filteredTransactions: List<TransactionItem>
        get() = when (selectedTabId) {
            "received" -> allTransactions.filter { it.status == TransactionStatus.Received }
            "paid" -> allTransactions.filter { it.status == TransactionStatus.Paid }
            "failed" -> allTransactions.filter { it.status == TransactionStatus.Failed }
            else -> allTransactions
        }
}

class HistoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState(allTransactions = defaultTransactions))
    val uiState = _uiState.asStateFlow()

    fun onTabSelected(tabId: String) {
        _uiState.update { it.copy(selectedTabId = tabId) }
    }

    companion object {
        private val defaultTransactions = listOf(
            TransactionItem(
                id = "1",
                dateTime = "Thu 15 Jan 2026, 00:40",
                name = "W.M.P.A.Bandara",
                amount = "-Rs. 15150.00",
                status = TransactionStatus.Paid,
            ),
            TransactionItem(
                id = "2",
                dateTime = "Fri 09 Jan 2026, 07:24",
                name = "R.M.K.T.Rathnayaka",
                amount = "-Rs. 1010.00",
                status = TransactionStatus.Paid,
            ),
            TransactionItem(
                id = "3",
                dateTime = "Thu 08 Jan 2026, 01:22",
                name = "W.M.P.A.Bandara",
                amount = "Rs. 4040.00",
                status = TransactionStatus.Failed,
                isDebit = false,
            ),
            TransactionItem(
                id = "4",
                dateTime = "Thu 08 Jan 2026, 12:20",
                name = "Water Bill",
                amount = "-Rs. 1515.00",
                status = TransactionStatus.Paid,
            ),
        )
    }
}
