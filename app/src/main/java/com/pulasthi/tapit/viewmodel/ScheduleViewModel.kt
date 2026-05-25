package com.pulasthi.tapit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulasthi.tapit.data.BeneficiaryRepository
import com.pulasthi.tapit.data.ScheduleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class ScheduleFrequency {
    Weekly,
    Monthly,
}

enum class ScheduleStatus {
    Active,
    Paused,
}

data class ScheduledPayment(
    val id: String,
    val recipientName: String,
    val billerLabel: String,
    val amount: Double,
    val frequency: ScheduleFrequency,
    val nextDueDateMillis: Long,
    val status: ScheduleStatus = ScheduleStatus.Active,
) {
    fun formattedAmount(): String = "%.2f".format(amount)

    fun formattedNextDueDate(): String =
        ScheduleViewModel.displayDateFormat.format(Date(nextDueDateMillis))
}

data class ScheduleRecipientOption(
    val id: String,
    val label: String,
    val subtitle: String = "",
)

data class ScheduleUiState(
    val schedules: List<ScheduledPayment> = emptyList(),
    val recipientOptions: List<ScheduleRecipientOption> = emptyList(),
    val editingScheduleId: String? = null,
    val selectedRecipientId: String? = null,
    val amount: String = "",
    val frequency: ScheduleFrequency = ScheduleFrequency.Monthly,
    val nextDueDateMillis: Long? = null,
    val status: ScheduleStatus = ScheduleStatus.Active,
    val recipientError: String? = null,
    val amountError: String? = null,
    val dateError: String? = null,
    val pendingCancel: ScheduledPayment? = null,
    val lastTriggeredMessage: String? = null,
) {
    val isEditMode: Boolean
        get() = editingScheduleId != null

    val formTitle: String
        get() = if (isEditMode) "Edit Schedule" else "Schedule Payment"

    val isEmpty: Boolean
        get() = schedules.isEmpty()
}

class ScheduleViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigateBackAfterSave = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateBackAfterSave = _navigateBackAfterSave.asSharedFlow()

    init {
        viewModelScope.launch {
            ScheduleRepository.schedules.collect { schedules ->
                _uiState.update { it.copy(schedules = schedules.sortedBy { item -> item.nextDueDateMillis }) }
            }
        }
        refreshRecipientOptions()
        startBackgroundScheduler()
    }

    fun refreshRecipientOptions() {
        viewModelScope.launch(Dispatchers.Default) {
            val beneficiaryOptions = BeneficiaryRepository.snapshot().map { beneficiary ->
                ScheduleRecipientOption(
                    id = "beneficiary_${beneficiary.id}",
                    label = beneficiary.name,
                    subtitle = "${beneficiary.bankName} · ${beneficiary.accountNumber}",
                )
            }
            val billerOptions = defaultBillers()
            _uiState.update {
                it.copy(recipientOptions = billerOptions + beneficiaryOptions)
            }
        }
    }

    fun startAddForm() {
        val defaultDate = startOfDayMillis(daysFromToday(1))
        _uiState.update {
            it.copy(
                editingScheduleId = null,
                selectedRecipientId = null,
                amount = "",
                frequency = ScheduleFrequency.Monthly,
                nextDueDateMillis = defaultDate,
                status = ScheduleStatus.Active,
                recipientError = null,
                amountError = null,
                dateError = null,
            )
        }
        refreshRecipientOptions()
    }

    fun startEditForm(scheduleId: String) {
        val schedule = ScheduleRepository.findById(scheduleId) ?: return
        val recipientId = _uiState.value.recipientOptions
            .firstOrNull { it.label == schedule.recipientName || it.label == schedule.billerLabel }
            ?.id

        _uiState.update {
            it.copy(
                editingScheduleId = schedule.id,
                selectedRecipientId = recipientId,
                amount = schedule.formattedAmount(),
                frequency = schedule.frequency,
                nextDueDateMillis = schedule.nextDueDateMillis,
                status = schedule.status,
                recipientError = null,
                amountError = null,
                dateError = null,
            )
        }
    }

    fun onRecipientSelected(optionId: String) {
        _uiState.update { it.copy(selectedRecipientId = optionId, recipientError = null) }
    }

    fun onAmountChange(value: String) {
        val filtered = value.filter { it.isDigit() || it == '.' }
        _uiState.update { it.copy(amount = filtered, amountError = null) }
    }

    fun onFrequencySelected(frequency: ScheduleFrequency) {
        _uiState.update { it.copy(frequency = frequency) }
    }

    fun onNextDueDateSelected(millis: Long) {
        _uiState.update {
            it.copy(
                nextDueDateMillis = startOfDayMillis(millis),
                dateError = null,
            )
        }
    }

    fun onStatusChanged(status: ScheduleStatus) {
        _uiState.update { it.copy(status = status) }
    }

    fun onSaveSchedule() {
        if (!validateForm()) return

        val state = _uiState.value
        val recipient = state.recipientOptions.first { it.id == state.selectedRecipientId }
        val amount = state.amount.toDoubleOrNull() ?: return
        val dueDate = state.nextDueDateMillis ?: return

        val schedule = ScheduledPayment(
            id = state.editingScheduleId ?: UUID.randomUUID().toString(),
            recipientName = recipient.label,
            billerLabel = recipient.subtitle.ifBlank { recipient.label },
            amount = amount,
            frequency = state.frequency,
            nextDueDateMillis = dueDate,
            status = state.status,
        )

        if (state.isEditMode) {
            ScheduleRepository.update(schedule)
        } else {
            ScheduleRepository.add(schedule)
        }
        _navigateBackAfterSave.tryEmit(Unit)
    }

    fun requestCancel(schedule: ScheduledPayment) {
        _uiState.update { it.copy(pendingCancel = schedule) }
    }

    fun dismissCancelDialog() {
        _uiState.update { it.copy(pendingCancel = null) }
    }

    fun confirmCancel() {
        val target = _uiState.value.pendingCancel ?: return
        ScheduleRepository.remove(target.id)
        _uiState.update { it.copy(pendingCancel = null) }
    }

    fun togglePause(schedule: ScheduledPayment) {
        val newStatus = if (schedule.status == ScheduleStatus.Active) {
            ScheduleStatus.Paused
        } else {
            ScheduleStatus.Active
        }
        ScheduleRepository.update(schedule.copy(status = newStatus))
    }

    fun clearTriggeredMessage() {
        _uiState.update { it.copy(lastTriggeredMessage = null) }
    }

    private fun validateForm(): Boolean {
        val state = _uiState.value
        val recipientError = if (state.selectedRecipientId == null) {
            "Select a biller or recipient"
        } else {
            null
        }
        val amount = state.amount.toDoubleOrNull()
        val amountError = when {
            state.amount.isBlank() -> "Enter amount"
            amount == null || amount <= 0 -> "Enter a valid amount"
            else -> null
        }
        val dateError = if (state.nextDueDateMillis == null) {
            "Select next payment date"
        } else {
            null
        }

        if (recipientError != null || amountError != null || dateError != null) {
            _uiState.update {
                it.copy(
                    recipientError = recipientError,
                    amountError = amountError,
                    dateError = dateError,
                )
            }
            return false
        }
        return true
    }

    private fun startBackgroundScheduler() {
        viewModelScope.launch(Dispatchers.Default) {
            while (isActive) {
                delay(BACKGROUND_CHECK_INTERVAL_MS)
                val message = processDueSchedules()
                if (message != null) {
                    _uiState.update { it.copy(lastTriggeredMessage = message) }
                }
            }
        }
    }

    /** Runs on [Dispatchers.Default]; returns snackbar message when work was done. */
    private fun processDueSchedules(): String? {
        val todayStart = startOfDayMillis(System.currentTimeMillis())
        val dueSchedules = ScheduleRepository.snapshot().filter { schedule ->
            schedule.status == ScheduleStatus.Active && schedule.nextDueDateMillis <= todayStart
        }

        if (dueSchedules.isEmpty()) return null

        dueSchedules.forEach { schedule ->
            val nextDate = advanceDueDate(schedule.nextDueDateMillis, schedule.frequency)
            ScheduleRepository.update(
                schedule.copy(nextDueDateMillis = nextDate),
            )
        }

        return if (dueSchedules.size == 1) {
            "Scheduled payment of LKR ${dueSchedules.first().formattedAmount()} processed for ${dueSchedules.first().recipientName}"
        } else {
            "${dueSchedules.size} scheduled payments processed"
        }
    }

    private fun advanceDueDate(currentMillis: Long, frequency: ScheduleFrequency): Long {
        val calendar = Calendar.getInstance().apply { timeInMillis = currentMillis }
        when (frequency) {
            ScheduleFrequency.Weekly -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
            ScheduleFrequency.Monthly -> calendar.add(Calendar.MONTH, 1)
        }
        return startOfDayMillis(calendar.timeInMillis)
    }

    companion object {
        const val BACKGROUND_CHECK_INTERVAL_MS = 30_000L

        val displayDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        fun defaultBillers(): List<ScheduleRecipientOption> = listOf(
            ScheduleRecipientOption("biller_electricity", "Electricity Bill", "LECO / CEB"),
            ScheduleRecipientOption("biller_water", "Water Bill", "National Water Supply"),
            ScheduleRecipientOption("biller_internet", "Internet Bill", "Dialog Broadband"),
            ScheduleRecipientOption("biller_mobile", "Mobile Postpaid", "Mobitel Postpaid"),
            ScheduleRecipientOption("biller_tv", "TV Subscription", "Dialog TV"),
            ScheduleRecipientOption("biller_insurance", "Insurance Premium", "Monthly premium"),
        )

        fun startOfDayMillis(millis: Long): Long {
            val calendar = Calendar.getInstance().apply { timeInMillis = millis }
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return calendar.timeInMillis
        }

        fun daysFromToday(days: Int): Long {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, days)
            return calendar.timeInMillis
        }
    }
}
