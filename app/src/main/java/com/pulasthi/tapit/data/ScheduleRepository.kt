package com.pulasthi.tapit.data

import com.pulasthi.tapit.viewmodel.ScheduleFrequency
import com.pulasthi.tapit.viewmodel.ScheduleStatus
import com.pulasthi.tapit.viewmodel.ScheduledPayment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object ScheduleRepository {

    private val _schedules = MutableStateFlow<List<ScheduledPayment>>(emptyList())
    val schedules: StateFlow<List<ScheduledPayment>> = _schedules.asStateFlow()

    fun snapshot(): List<ScheduledPayment> = _schedules.value

    fun add(schedule: ScheduledPayment) {
        _schedules.update { it + schedule }
    }

    fun update(schedule: ScheduledPayment) {
        _schedules.update { list ->
            list.map { if (it.id == schedule.id) schedule else it }
        }
    }

    fun remove(id: String) {
        _schedules.update { list -> list.filter { it.id != id } }
    }

    fun findById(id: String): ScheduledPayment? = _schedules.value.find { it.id == id }
}
