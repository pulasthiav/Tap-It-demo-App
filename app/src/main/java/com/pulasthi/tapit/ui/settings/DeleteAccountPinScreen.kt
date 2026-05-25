package com.pulasthi.tapit.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pulasthi.tapit.ui.auth.PinEntryScreen
import com.pulasthi.tapit.viewmodel.DeleteAccountPinViewModel

@Composable
fun DeleteAccountPinScreen(
    onNavigateToAccountDeleted: () -> Unit,
    viewModel: DeleteAccountPinViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigateToDeleted.collect { onNavigateToAccountDeleted() }
    }

    PinEntryScreen(
        title = "Enter PIN Code",
        pinLength = uiState.pin.length,
        onDigitClick = viewModel::onDigitPressed,
        onBackspaceClick = viewModel::onBackspace,
    )
}
