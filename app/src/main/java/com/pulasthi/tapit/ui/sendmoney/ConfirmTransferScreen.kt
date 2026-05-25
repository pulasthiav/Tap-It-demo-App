package com.pulasthi.tapit.ui.sendmoney

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import com.pulasthi.tapit.ui.components.DashboardBackground
import com.pulasthi.tapit.ui.govpay.components.PaymentCardView
import com.pulasthi.tapit.ui.theme.TapItBluePrimary
import com.pulasthi.tapit.ui.theme.TapItReloadConfirmPurple
import com.pulasthi.tapit.ui.theme.TapItTextPrimary
import com.pulasthi.tapit.ui.theme.TapItTextSecondary
import com.pulasthi.tapit.ui.theme.TapItWhite
import com.pulasthi.tapit.viewmodel.SendMoneyTransferStatus
import com.pulasthi.tapit.viewmodel.SendMoneyViewModel

@Composable
fun ConfirmTransferScreen(
    onBack: () -> Unit,
    onNavigateToSuccess: () -> Unit,
    viewModel: SendMoneyViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val card = uiState.cards.getOrNull(uiState.selectedCardIndex)
    val recipient = uiState.activeRecipient

    LaunchedEffect(Unit) {
        viewModel.navigateToSuccess.collect { onNavigateToSuccess() }
    }

    DashboardBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp),
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TapItWhite,
                )
            }

            Text(
                text = "Confirm Transfer",
                color = TapItWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            )

            Text(
                text = "From",
                color = TapItWhite,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            if (card != null) {
                PaymentCardView(
                    card = card,
                    modifier = Modifier.padding(bottom = 20.dp),
                )
            }

            Text(
                text = "To",
                color = TapItWhite,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                color = TapItWhite,
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        modifier = Modifier.size(44.dp),
                        shape = CircleShape,
                        color = TapItBluePrimary.copy(alpha = 0.15f),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = TapItBluePrimary,
                            modifier = Modifier
                                .padding(10.dp)
                                .size(24.dp),
                        )
                    }
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text(
                            text = recipient.name,
                            color = TapItTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = recipient.bankName,
                            color = TapItTextSecondary,
                            fontSize = 13.sp,
                        )
                        Text(
                            text = recipient.accountNumber,
                            color = TapItTextSecondary,
                            fontSize = 13.sp,
                        )
                    }
                }
            }

            SummaryRow(label = "Amount", value = uiState.formattedAmount)
            SummaryRow(label = "Service Fee (1%)", value = uiState.formattedServiceFee)
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = TapItWhite.copy(alpha = 0.35f),
            )
            SummaryRow(label = "Total", value = uiState.formattedTotal)

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = viewModel::onConfirmTransfer,
                enabled = uiState.transferStatus != SendMoneyTransferStatus.Processing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TapItReloadConfirmPurple,
                    contentColor = TapItWhite,
                ),
            ) {
                if (uiState.transferStatus == SendMoneyTransferStatus.Processing) {
                    CircularProgressIndicator(
                        color = TapItWhite,
                        modifier = Modifier.height(22.dp),
                    )
                } else {
                    Text("Confirm Payment", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = TapItWhite,
            fontSize = 15.sp,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            color = TapItWhite,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
