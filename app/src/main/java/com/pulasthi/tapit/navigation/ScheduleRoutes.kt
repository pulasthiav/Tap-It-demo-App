package com.pulasthi.tapit.navigation

object ScheduleRoutes {
    const val GRAPH = "schedule"
    const val LIST = "schedule_list"
    const val ADD = "schedule_add"
    const val EDIT = "schedule_edit/{scheduleId}"

    const val ARG_SCHEDULE_ID = "scheduleId"

    fun editSchedule(scheduleId: String): String = "schedule_edit/$scheduleId"
}
