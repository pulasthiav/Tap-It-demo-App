package com.pulasthi.tapit.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pulasthi.tapit.ui.settings.components.DeleteAccountDialog
import com.pulasthi.tapit.ui.settings.components.SettingsScaffold
import com.pulasthi.tapit.ui.theme.TapItDeleteRed
import com.pulasthi.tapit.ui.theme.TapItSettingsMenuCard
import com.pulasthi.tapit.ui.theme.TapItTextPrimary
import com.pulasthi.tapit.ui.theme.TapItWhite
import com.pulasthi.tapit.viewmodel.SecurityMenuItem
import com.pulasthi.tapit.viewmodel.SecurityViewModel

@Composable
fun SecurityScreen(
    onBack: () -> Unit,
    onNavigateTo: (String) -> Unit,
    onNavigateToDeletePin: () -> Unit,
    viewModel: SecurityViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigateTo.collect { route -> onNavigateTo(route) }
    }
    LaunchedEffect(Unit) {
        viewModel.navigateToDeletePin.collect { onNavigateToDeletePin() }
    }

    SettingsScaffold(title = "Security", onBack = onBack) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
        ) {
            uiState.menuItems.filter { !it.isDeleteAccount }.forEach { item ->
                SecurityMenuRow(
                    item = item,
                    onClick = { viewModel.onMenuItemClick(item) },
                )
            }
            val deleteItem = uiState.menuItems.first { it.isDeleteAccount }
            SecurityMenuRow(
                item = deleteItem,
                onClick = { viewModel.onMenuItemClick(deleteItem) },
                isDeleteStyle = true,
            )
        }
    }

    if (uiState.showDeleteDialog) {
        DeleteAccountDialog(
            onDismiss = viewModel::onDismissDeleteDialog,
            onConfirm = viewModel::onConfirmDeleteAccount,
        )
    }
}

@Composable
private fun SecurityMenuRow(
    item: SecurityMenuItem,
    onClick: () -> Unit,
    isDeleteStyle: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .background(
                color = if (isDeleteStyle) TapItDeleteRed.copy(alpha = 0.12f) else TapItSettingsMenuCard,
                shape = RoundedCornerShape(14.dp),
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = if (isDeleteStyle) TapItDeleteRed else TapItTextPrimary,
            modifier = Modifier.size(28.dp),
        )
        Text(
            text = item.label,
            color = if (isDeleteStyle) TapItDeleteRed else TapItTextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 16.dp),
        )
    }
}
