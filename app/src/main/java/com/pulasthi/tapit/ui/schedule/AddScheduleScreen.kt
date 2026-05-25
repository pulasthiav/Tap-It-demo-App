package com.pulasthi.tapit.ui.schedule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pulasthi.tapit.ui.components.ColumnLabel
import com.pulasthi.tapit.ui.components.TapItTextField
import com.pulasthi.tapit.ui.theme.TapItBluePrimary
import com.pulasthi.tapit.ui.theme.TapItError
import com.pulasthi.tapit.ui.theme.TapItLinkBlue
import com.pulasthi.tapit.ui.theme.TapItTextPrimary
import com.pulasthi.tapit.ui.theme.TapItTextSecondary
import com.pulasthi.tapit.ui.theme.TapItWhite
import com.pulasthi.tapit.viewmodel.ScheduleFrequency
import com.pulasthi.tapit.viewmodel.ScheduleRecipientOption
import com.pulasthi.tapit.viewmodel.ScheduleStatus
import com.pulasthi.tapit.viewmodel.ScheduleViewModel
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScheduleScreen(
    scheduleId: String?,
    onBack: () -> Unit,
    viewModel: ScheduleViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(scheduleId) {
        if (scheduleId != null) {
            viewModel.startEditForm(scheduleId)
        } else {
            viewModel.startAddForm()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigateBackAfterSave.collect { onBack() }
    }

    if (showDatePicker) {
        val initialMillis = uiState.nextDueDateMillis ?: System.currentTimeMillis()
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let(viewModel::onNextDueDateSelected)
                        showDatePicker = false
                    },
                ) {
                    Text("OK", color = TapItLinkBlue)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = TapItTextSecondary)
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    ScheduleFlowScaffold(
        title = uiState.formTitle,
        onBack = onBack,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
        ) {
            ColumnLabel(text = "Biller / Recipient")
            uiState.recipientOptions.forEach { option ->
                RecipientOptionRow(
                    option = option,
                    selected = uiState.selectedRecipientId == option.id,
                    onClick = { viewModel.onRecipientSelected(option.id) },
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            uiState.recipientError?.let { FormError(it) }

            Spacer(modifier = Modifier.height(12.dp))

            ColumnLabel(text = "Frequency")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FrequencyChip(
                    label = "Weekly",
                    selected = uiState.frequency == ScheduleFrequency.Weekly,
                    onClick = { viewModel.onFrequencySelected(ScheduleFrequency.Weekly) },
                )
                FrequencyChip(
                    label = "Monthly",
                    selected = uiState.frequency == ScheduleFrequency.Monthly,
                    onClick = { viewModel.onFrequencySelected(ScheduleFrequency.Monthly) },
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            ColumnLabel(text = "Amount (LKR)")
            TapItTextField(
                value = uiState.amount,
                onValueChange = viewModel::onAmountChange,
                placeholder = "LKR",
                keyboardType = KeyboardType.Decimal,
            )
            uiState.amountError?.let { FormError(it) }

            Spacer(modifier = Modifier.height(12.dp))

            ColumnLabel(text = "Next Payment Date")
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = TapItLinkBlue,
                )
                Text(
                    text = uiState.nextDueDateMillis?.let { millis ->
                        ScheduleViewModel.displayDateFormat.format(Date(millis))
                    } ?: "Select date",
                    color = TapItTextPrimary,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
            uiState.dateError?.let { FormError(it) }

            if (uiState.isEditMode) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = if (uiState.status == ScheduleStatus.Active) "Active" else "Paused",
                        color = TapItTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Switch(
                        checked = uiState.status == ScheduleStatus.Active,
                        onCheckedChange = { active ->
                            viewModel.onStatusChanged(
                                if (active) ScheduleStatus.Active else ScheduleStatus.Paused,
                            )
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = viewModel::onSaveSchedule,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TapItBluePrimary,
                    contentColor = TapItWhite,
                ),
            ) {
                Text(
                    text = if (uiState.isEditMode) "Save Changes" else "Save Schedule",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun RecipientOptionRow(
    option: ScheduleRecipientOption,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) TapItBluePrimary.copy(alpha = 0.12f) else TapItWhite,
        border = if (selected) {
            androidx.compose.foundation.BorderStroke(2.dp, TapItBluePrimary)
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, TapItTextSecondary.copy(alpha = 0.2f))
        },
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = option.label,
                color = TapItTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
            if (option.subtitle.isNotBlank()) {
                Text(
                    text = option.subtitle,
                    color = TapItTextSecondary,
                    fontSize = 12.sp,
                )
            }
        }
    }
}

@Composable
private fun FrequencyChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = TapItBluePrimary,
            selectedLabelColor = TapItWhite,
        ),
    )
}

@Composable
private fun FormError(message: String) {
    Text(
        text = message,
        color = TapItError,
        fontSize = 12.sp,
        modifier = Modifier.padding(top = 4.dp),
    )
}
