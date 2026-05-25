package com.pulasthi.tapit.ui.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pulasthi.tapit.ui.theme.TapItBluePrimary
import com.pulasthi.tapit.ui.theme.TapItGreen
import com.pulasthi.tapit.ui.theme.TapItLinkBlue
import com.pulasthi.tapit.ui.theme.TapItTextHint
import com.pulasthi.tapit.ui.theme.TapItTextPrimary
import com.pulasthi.tapit.ui.theme.TapItTextSecondary
import com.pulasthi.tapit.ui.theme.TapItWhite
import com.pulasthi.tapit.viewmodel.ScheduleStatus
import com.pulasthi.tapit.viewmodel.ScheduleViewModel
import com.pulasthi.tapit.viewmodel.ScheduledPayment

@Composable
fun ScheduledPaymentsScreen(
    onBack: () -> Unit,
    onAddSchedule: () -> Unit,
    onEditSchedule: (String) -> Unit,
    viewModel: ScheduleViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.lastTriggeredMessage) {
        uiState.lastTriggeredMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearTriggeredMessage()
        }
    }

    uiState.pendingCancel?.let { schedule ->
        CancelScheduleDialog(
            schedule = schedule,
            onConfirm = viewModel::confirmCancel,
            onDismiss = viewModel::dismissCancelDialog,
        )
    }

    ScheduleFlowScaffold(
        title = "Scheduled Payments",
        onBack = onBack,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            ) {
                if (uiState.isEmpty) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "No Previous Transactions",
                            color = TapItTextHint,
                            fontSize = 14.sp,
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(
                            items = uiState.schedules,
                            key = { it.id },
                        ) { schedule ->
                            ScheduledPaymentCard(
                                schedule = schedule,
                                onEdit = { onEditSchedule(schedule.id) },
                                onCancel = { viewModel.requestCancel(schedule) },
                                onTogglePause = { viewModel.togglePause(schedule) },
                            )
                        }
                    }
                }

                Button(
                    onClick = onAddSchedule,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TapItBluePrimary,
                        contentColor = TapItWhite,
                    ),
                ) {
                    Surface(
                        shape = CircleShape,
                        color = TapItWhite.copy(alpha = 0.2f),
                        modifier = Modifier.size(28.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = TapItWhite,
                            modifier = Modifier.padding(4.dp),
                        )
                    }
                    Text(
                        text = "Schedule Payment",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 10.dp),
                    )
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 72.dp),
            )
        }
    }
}

@Composable
private fun ScheduledPaymentCard(
    schedule: ScheduledPayment,
    onEdit: () -> Unit,
    onCancel: () -> Unit,
    onTogglePause: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = TapItWhite,
        shadowElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = schedule.recipientName,
                        color = TapItTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = schedule.billerLabel,
                        color = TapItTextSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
                StatusBadge(status = schedule.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = TapItLinkBlue,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = "Next: ${schedule.formattedNextDueDate()}",
                    color = TapItTextSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(start = 6.dp),
                )
            }

            Text(
                text = "LKR ${schedule.formattedAmount()} · ${schedule.frequency.name}",
                color = TapItTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 6.dp),
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text("Edit", color = TapItLinkBlue, fontWeight = FontWeight.SemiBold)
                }
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text("Cancel", color = TapItLinkBlue, fontWeight = FontWeight.SemiBold)
                }
            }

            TextButton(
                onClick = onTogglePause,
                modifier = Modifier.align(Alignment.End),
            ) {
                Text(
                    text = if (schedule.status == ScheduleStatus.Active) "Pause" else "Resume",
                    color = TapItLinkBlue,
                    fontSize = 13.sp,
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(status: ScheduleStatus) {
    val background = when (status) {
        ScheduleStatus.Active -> TapItGreen.copy(alpha = 0.15f)
        ScheduleStatus.Paused -> TapItTextSecondary.copy(alpha = 0.2f)
    }
    val textColor = when (status) {
        ScheduleStatus.Active -> TapItGreen
        ScheduleStatus.Paused -> TapItTextSecondary
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = background,
    ) {
        Text(
            text = status.name,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}
