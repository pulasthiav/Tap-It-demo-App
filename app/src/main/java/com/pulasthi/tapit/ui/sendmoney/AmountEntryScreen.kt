package com.pulasthi.tapit.ui.sendmoney

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pulasthi.tapit.ui.components.ColumnLabel
import com.pulasthi.tapit.ui.components.TapItTextField
import com.pulasthi.tapit.ui.govpay.components.PaymentCardCarousel
import com.pulasthi.tapit.ui.theme.TapItBluePrimary
import com.pulasthi.tapit.ui.theme.TapItLinkBlue
import com.pulasthi.tapit.ui.theme.TapItError
import com.pulasthi.tapit.ui.theme.TapItTextPrimary
import com.pulasthi.tapit.ui.theme.TapItTextSecondary
import com.pulasthi.tapit.ui.theme.TapItWhite
import com.pulasthi.tapit.viewmodel.SendMoneyViewModel

@Composable
fun AmountEntryScreen(
    onBack: () -> Unit,
    onNavigateToConfirm: () -> Unit,
    viewModel: SendMoneyViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refreshWalletCards()
    }

    LaunchedEffect(Unit) {
        viewModel.navigateToConfirm.collect { onNavigateToConfirm() }
    }

    SendMoneyScaffold(
        title = "Transfer Amount",
        onBack = onBack,
        headerExtra = {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "From",
                    color = TapItWhite,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                if (uiState.cards.isNotEmpty()) {
                    PaymentCardCarousel(
                        cards = uiState.cards,
                        selectedIndex = uiState.selectedCardIndex,
                        onCardSelected = viewModel::onCardSelected,
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
        ) {
            TextButton(onClick = onBack) {
                Text(
                    text = "Change recipient",
                    color = TapItLinkBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Text(
                text = "To: ${uiState.activeRecipient.name}",
                color = TapItTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "${uiState.activeRecipient.bankName} · ${uiState.activeRecipient.accountNumber}",
                color = TapItTextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
            )

            ColumnLabel(text = "Amount (LKR)")
            TapItTextField(
                value = uiState.amount,
                onValueChange = viewModel::onAmountChange,
                placeholder = "LKR",
                keyboardType = KeyboardType.Decimal,
            )
            uiState.amountError?.let {
                Text(
                    text = it,
                    color = TapItError,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = TapItWhite,
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Payment breakdown",
                        color = TapItTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    BreakdownRow("Amount", uiState.formattedAmount)
                    BreakdownRow("Service Fee (1%)", uiState.formattedServiceFee)
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = TapItTextSecondary.copy(alpha = 0.25f),
                    )
                    BreakdownRow(
                        label = "Total debit",
                        value = uiState.formattedTotal,
                        emphasized = true,
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = viewModel::onAmountNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TapItBluePrimary,
                    contentColor = TapItWhite,
                ),
            ) {
                Text("Continue", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun BreakdownRow(
    label: String,
    value: String,
    emphasized: Boolean = false,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = TapItTextSecondary,
            fontSize = if (emphasized) 15.sp else 14.sp,
            fontWeight = if (emphasized) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            color = TapItTextPrimary,
            fontSize = if (emphasized) 16.sp else 14.sp,
            fontWeight = if (emphasized) FontWeight.Bold else FontWeight.SemiBold,
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
}
