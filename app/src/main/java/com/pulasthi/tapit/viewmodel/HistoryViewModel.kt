package com.pulasthi.tapit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulasthi.tapit.data.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private fun historyStartOfDayMillis(millis: Long): Long {
    return Calendar.getInstance().apply {
        timeInMillis = millis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

private fun historyEndOfDayMillis(millis: Long): Long {
    return Calendar.getInstance().apply {
        timeInMillis = historyStartOfDayMillis(millis)
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.timeInMillis
}

private val defaultHistoryStartDateMillis: Long =
    Calendar.getInstance().apply {
        set(Calendar.YEAR, 2026)
        set(Calendar.MONTH, Calendar.JANUARY)
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

private val defaultHistoryEndDateMillis: Long =
    historyEndOfDayMillis(
        Calendar.getInstance().apply {
            set(Calendar.YEAR, 2026)
            set(Calendar.MONTH, Calendar.JANUARY)
            set(Calendar.DAY_OF_MONTH, 15)
        }.timeInMillis,
    )

enum class TransactionStatus {
    Paid,
    Failed,
    Received,
}

data class HistoryTab(
    val id: String,
    val label: String,
)

data class Transaction(
    val id: String,
    val occurredAtMillis: Long,
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
    val startDateMillis: Long = defaultHistoryStartDateMillis,
    val endDateMillis: Long = defaultHistoryEndDateMillis,
    val allTransactions: List<Transaction> = emptyList(),
) {
    val fromDate: String
        get() = HistoryViewModel.displayDateFormat.format(Date(startDateMillis))

    val toDate: String
        get() = HistoryViewModel.displayDateFormat.format(Date(endDateMillis))

    val filteredTransactions: List<Transaction>
        get() {
            val rangeStart = HistoryViewModel.startOfDayMillis(startDateMillis)
            val rangeEnd = HistoryViewModel.endOfDayMillis(endDateMillis)
            return allTransactions
                .filter { it.occurredAtMillis in rangeStart..rangeEnd }
                .filter { transaction ->
                    when (selectedTabId) {
                        "received" -> transaction.status == TransactionStatus.Received
                        "paid" -> transaction.status == TransactionStatus.Paid
                        "failed" -> transaction.status == TransactionStatus.Failed
                        else -> true
                    }
                }
        }
}

class HistoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Observes the shared repository so new entries (e.g. after Send Money) appear instantly.
        viewModelScope.launch {
            TransactionRepository.transactions.collect { transactions ->
                _uiState.update { it.copy(allTransactions = transactions) }
            }
        }
    }

    fun onTabSelected(tabId: String) {
        _uiState.update { it.copy(selectedTabId = tabId) }
    }

    fun onStartDateSelected(millis: Long) {
        val normalizedStart = startOfDayMillis(millis)
        _uiState.update { state ->
            val adjustedEnd = if (normalizedStart > state.endDateMillis) {
                endOfDayMillis(normalizedStart)
            } else {
                state.endDateMillis
            }
            state.copy(
                startDateMillis = normalizedStart,
                endDateMillis = adjustedEnd,
            )
        }
    }

    fun onEndDateSelected(millis: Long) {
        val normalizedEnd = endOfDayMillis(millis)
        _uiState.update { state ->
            val adjustedStart = if (normalizedEnd < state.startDateMillis) {
                startOfDayMillis(normalizedEnd)
            } else {
                state.startDateMillis
            }
            state.copy(
                startDateMillis = adjustedStart,
                endDateMillis = normalizedEnd,
            )
        }
    }

    companion object {
        val displayDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        fun startOfDayMillis(millis: Long): Long {
            return Calendar.getInstance().apply {
                timeInMillis = millis
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }

        fun endOfDayMillis(millis: Long): Long {
            return Calendar.getInstance().apply {
                timeInMillis = startOfDayMillis(millis)
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis
        }
    }
}
