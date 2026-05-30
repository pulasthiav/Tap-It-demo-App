package com.pulasthi.tapit.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pulasthi.tapit.ui.components.FlowScreenScaffold
import com.pulasthi.tapit.ui.theme.TapItBluePrimary
import com.pulasthi.tapit.ui.theme.TapItGreen
import com.pulasthi.tapit.ui.theme.TapItTextPrimary
import com.pulasthi.tapit.ui.theme.TapItTextSecondary
import com.pulasthi.tapit.ui.theme.TapItWhite
import com.pulasthi.tapit.ui.theme.TapItError
import com.pulasthi.tapit.ui.theme.TapItLinkBlue
import com.pulasthi.tapit.viewmodel.HistoryTab
import com.pulasthi.tapit.viewmodel.HistoryViewModel
import com.pulasthi.tapit.viewmodel.Transaction
import com.pulasthi.tapit.viewmodel.TransactionStatus

/** Transaction History tab screen (also referred to as TransactionHistoryScreen). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var datePickerTarget by remember { mutableStateOf<DatePickerTarget?>(null) }

    datePickerTarget?.let { target ->
        val initialMillis = when (target) {
            DatePickerTarget.Start -> uiState.startDateMillis
            DatePickerTarget.End -> uiState.endDateMillis
        }
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

        DatePickerDialog(
            onDismissRequest = { datePickerTarget = null },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            when (target) {
                                DatePickerTarget.Start -> viewModel.onStartDateSelected(millis)
                                DatePickerTarget.End -> viewModel.onEndDateSelected(millis)
                            }
                        }
                        datePickerTarget = null
                    },
                ) {
                    Text("OK", color = TapItLinkBlue)
                }
            },
            dismissButton = {
                TextButton(onClick = { datePickerTarget = null }) {
                    Text("Cancel", color = TapItTextSecondary)
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    FlowScreenScaffold(title = "Transaction History") {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 88.dp),
        ) {
            HistoryTabBar(
                tabs = uiState.tabs,
                selectedTabId = uiState.selectedTabId,
                onTabSelected = viewModel::onTabSelected,
            )

            HistoryDateRangeRow(
                fromDate = uiState.fromDate,
                toDate = uiState.toDate,
                onFromDateClick = { datePickerTarget = DatePickerTarget.Start },
                onToDateClick = { datePickerTarget = DatePickerTarget.End },
            )

            // filteredTransactions is derived in ViewModel state and recomposes on tab/date/repository changes.
            if (uiState.filteredTransactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "No Previous Transactions",
                        color = TapItTextSecondary,
                        fontSize = 15.sp,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 16.dp,
                        vertical = 8.dp,
                    ),
                ) {
                    items(uiState.filteredTransactions, key = { it.id }) { item ->
                        TransactionRow(item = item)
                        HorizontalDivider(color = TapItTextSecondary.copy(alpha = 0.2f))
                    }
                }
            }
        }
    }
}

private enum class DatePickerTarget {
    Start,
    End,
}

@Composable
private fun HistoryTabBar(
    tabs: List<HistoryTab>,
    selectedTabId: String,
    onTabSelected: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TapItBluePrimary)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        tabs.forEach { tab ->
            val selected = tab.id == selectedTabId
            Text(
                text = tab.label,
                color = TapItWhite,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        color = if (selected) {
                            TapItBluePrimary.copy(alpha = 0.85f)
                        } else {
                            TapItBluePrimary.copy(alpha = 0.35f)
                        },
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onTabSelected(tab.id) },
                    )
                    .padding(vertical = 10.dp),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun HistoryDateRangeRow(
    fromDate: String,
    toDate: String,
    onFromDateClick: () -> Unit,
    onToDateClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        DateField(date = fromDate, onClick = onFromDateClick)
        Text(text = "To", color = TapItTextPrimary, fontSize = 14.sp)
        DateField(date = toDate, onClick = onToDateClick)
    }
}

@Composable
private fun DateField(
    date: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .background(TapItWhite, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = date,
            color = TapItTextPrimary,
            fontSize = 13.sp,
            modifier = Modifier.padding(end = 8.dp),
        )
        Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = null,
            tint = TapItTextSecondary,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun TransactionRow(item: Transaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    when (item.status) {
                        TransactionStatus.Failed -> TapItError
                        TransactionStatus.Paid -> TapItGreen
                        TransactionStatus.Received -> TapItGreen
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (item.status == TransactionStatus.Failed) {
                    Icons.Default.Close
                } else {
                    Icons.Default.Check
                },
                contentDescription = null,
                tint = TapItWhite,
                modifier = Modifier.size(22.dp),
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
        ) {
            Text(text = item.dateTime, color = TapItTextSecondary, fontSize = 12.sp)
            Text(
                text = item.name,
                color = TapItTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = item.amount,
                color = TapItTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
            StatusBadge(status = item.status)
        }
    }
}

@Composable
private fun StatusBadge(status: TransactionStatus) {
    val (label, bg, fg) = when (status) {
        TransactionStatus.Paid -> Triple("PAID", TapItGreen.copy(alpha = 0.25f), TapItGreen)
        TransactionStatus.Failed -> Triple("FAILED", TapItError, TapItError)
        TransactionStatus.Received -> Triple("RECEIVED", TapItGreen.copy(alpha = 0.25f), TapItGreen)
    }
    Text(
        text = label,
        color = fg,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .padding(top = 4.dp)
            .background(bg, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp),
    )
}
