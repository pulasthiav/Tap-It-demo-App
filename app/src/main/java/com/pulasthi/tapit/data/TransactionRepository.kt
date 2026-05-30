package com.pulasthi.tapit.data

import com.pulasthi.tapit.viewmodel.Transaction
import com.pulasthi.tapit.viewmodel.TransactionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar

/**
 * In-memory transaction store shared across payment flows and Transaction History.
 *
 * After a successful Send Money (or any payment) action, call [addTransaction] here.
 * [HistoryViewModel] collects [transactions]; the history screen updates automatically
 * without manual refresh.
 */
object TransactionRepository {

    private val _transactions = MutableStateFlow(defaultTransactions())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    fun snapshot(): List<Transaction> = _transactions.value

    fun addTransaction(transaction: Transaction) {
        _transactions.update { current ->
            (current + transaction).sortedByDescending { it.occurredAtMillis }
        }
    }

    private fun defaultTransactions(): List<Transaction> = listOf(
        Transaction(
            id = "1",
            occurredAtMillis = atDateTime(2026, Calendar.JANUARY, 15, 0, 40),
            dateTime = "Thu 15 Jan 2026, 00:40",
            name = "W.M.P.A.Bandara",
            amount = "-Rs. 15150.00",
            status = TransactionStatus.Paid,
        ),
        Transaction(
            id = "2",
            occurredAtMillis = atDateTime(2026, Calendar.JANUARY, 9, 7, 24),
            dateTime = "Fri 09 Jan 2026, 07:24",
            name = "R.M.K.T.Rathnayaka",
            amount = "-Rs. 1010.00",
            status = TransactionStatus.Paid,
        ),
        Transaction(
            id = "3",
            occurredAtMillis = atDateTime(2026, Calendar.JANUARY, 8, 1, 22),
            dateTime = "Thu 08 Jan 2026, 01:22",
            name = "W.M.P.A.Bandara",
            amount = "Rs. 4040.00",
            status = TransactionStatus.Failed,
            isDebit = false,
        ),
        Transaction(
            id = "4",
            occurredAtMillis = atDateTime(2026, Calendar.JANUARY, 8, 12, 20),
            dateTime = "Thu 08 Jan 2026, 12:20",
            name = "Water Bill",
            amount = "-Rs. 1515.00",
            status = TransactionStatus.Paid,
        ),
    )

    private fun atDateTime(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
    ): Long {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}
