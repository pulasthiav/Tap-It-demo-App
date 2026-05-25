package com.pulasthi.tapit.ui.wallet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pulasthi.tapit.ui.components.DashboardBackground
import com.pulasthi.tapit.ui.components.FlowScreenScaffold
import com.pulasthi.tapit.ui.theme.TapItTextPrimary
import com.pulasthi.tapit.ui.theme.TapItTextSecondary
import com.pulasthi.tapit.ui.wallet.components.RemoveCardDialog
import com.pulasthi.tapit.ui.wallet.components.WalletCardView
import com.pulasthi.tapit.ui.wallet.components.WalletTealButton
import com.pulasthi.tapit.viewmodel.AccountViewModel

@Composable
fun RemoveCardScreen(
    onBack: () -> Unit,
    onCardsRemoved: () -> Unit,
    viewModel: AccountViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
                text = "Select your card to remove",
                color = TapItTextSecondary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
            )

            uiState.cards.forEach { card ->
                val selected = card.id in uiState.removeSelection
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        checked = selected,
                        onCheckedChange = { viewModel.onToggleRemoveSelection(card.id) },
                    )
                    WalletCardView(
                        card = card,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                    )
                }
            }

            WalletTealButton(
                text = "Remove Your Card Here",
                onClick = viewModel::onRequestRemoveConfirm,
                enabled = uiState.hasRemoveSelection,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
            )
        }
        }

        if (uiState.showRemoveConfirmDialog) {
            DashboardBackground(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    RemoveCardDialog(
                        onDismiss = viewModel::onDismissRemoveDialog,
                        onConfirm = {
                            viewModel.onConfirmRemoveCards()
                            onCardsRemoved()
                        },
                    )
                }
            }
        }
    }
}
