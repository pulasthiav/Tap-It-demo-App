package com.pulasthi.tapit.navigation

import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.pulasthi.tapit.ui.schedule.AddScheduleScreen
import com.pulasthi.tapit.ui.schedule.ScheduledPaymentsScreen
import com.pulasthi.tapit.viewmodel.ScheduleViewModel

fun NavGraphBuilder.scheduleNavGraph(
    navController: NavHostController,
    onExitFlow: () -> Unit,
) {
    navigation(
        route = ScheduleRoutes.GRAPH,
        startDestination = ScheduleRoutes.LIST,
    ) {
        composable(ScheduleRoutes.LIST) { entry ->
            val viewModel: ScheduleViewModel = viewModel(
                remember(entry) {
                    navController.getBackStackEntry(ScheduleRoutes.GRAPH)
                },
            )
            ScheduledPaymentsScreen(
                viewModel = viewModel,
                onBack = onExitFlow,
                onAddSchedule = {
                    navController.navigate(ScheduleRoutes.ADD)
                },
                onEditSchedule = { scheduleId ->
                    navController.navigate(ScheduleRoutes.editSchedule(scheduleId))
                },
            )
        }

        composable(ScheduleRoutes.ADD) { entry ->
            val viewModel: ScheduleViewModel = viewModel(
                remember(entry) {
                    navController.getBackStackEntry(ScheduleRoutes.GRAPH)
                },
            )
            AddScheduleScreen(
                scheduleId = null,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = ScheduleRoutes.EDIT,
            arguments = listOf(
                navArgument(ScheduleRoutes.ARG_SCHEDULE_ID) { type = NavType.StringType },
            ),
        ) { entry ->
            val viewModel: ScheduleViewModel = viewModel(
                remember(entry) {
                    navController.getBackStackEntry(ScheduleRoutes.GRAPH)
                },
            )
            val scheduleId = entry.arguments?.getString(ScheduleRoutes.ARG_SCHEDULE_ID)
            AddScheduleScreen(
                scheduleId = scheduleId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
