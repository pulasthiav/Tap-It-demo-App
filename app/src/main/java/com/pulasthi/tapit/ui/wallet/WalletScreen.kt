package com.pulasthi.tapit.ui.wallet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pulasthi.tapit.ui.components.FlowScreenScaffold
import com.pulasthi.tapit.ui.theme.TapItTextPrimary
import com.pulasthi.tapit.ui.wallet.components.CardActionSnackbar
import com.pulasthi.tapit.ui.wallet.components.WalletCardView
import com.pulasthi.tapit.ui.wallet.components.WalletTealButton
import com.pulasthi.tapit.viewmodel.AccountViewModel
import kotlinx.coroutines.delay

@Composable
fun WalletScreen(
    onBack: () -> Unit,
    onNavigateToAddCard: () -> Unit,
    onNavigateToRemoveSelect: () -> Unit,
    viewModel: AccountViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigateToAddCard.collect { onNavigateToAddCard() }
    }
    LaunchedEffect(Unit) {
        viewModel.navigateToRemoveSelect.collect { onNavigateToRemoveSelect() }
    }

    val snackbarMessage = uiState.snackbarMessage
    if (snackbarMessage != null) {
        LaunchedEffect(snackbarMessage) {
            delay(2_500L)
            viewModel.onSnackbarShown()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        FlowScreenScaffold(
            title = "My Cards and Account",
            showBack = true,
            onBack = onBack,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            ) {
                Text(
                    text = "Bank Accounts",
                    color = TapItTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                )

                uiState.cards.forEach { card ->
                    WalletCardView(
                        card = card,
                        modifier = Modifier.padding(bottom = 16.dp),
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                WalletTealButton(
                    text = "Add Your Card Here",
                    onClick = viewModel::onAddCardClick,
                    modifier = Modifier.padding(top = 8.dp),
                )
                WalletTealButton(
                    text = "Remove Your Card Here",
                    onClick = viewModel::onRemoveCardClick,
                    modifier = Modifier.padding(top = 12.dp, bottom = 16.dp),
                )
            }
        }

        if (snackbarMessage != null) {
            CardActionSnackbar(
                message = snackbarMessage,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}
