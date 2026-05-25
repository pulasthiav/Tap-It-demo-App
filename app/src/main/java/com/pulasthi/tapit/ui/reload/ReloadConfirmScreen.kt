package com.pulasthi.tapit.ui.reload

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pulasthi.tapit.ui.components.DashboardBackground
import com.pulasthi.tapit.ui.govpay.components.PaymentCardView
import com.pulasthi.tapit.ui.theme.TapItReloadConfirmPurple
import com.pulasthi.tapit.ui.theme.TapItTextPrimary
import com.pulasthi.tapit.ui.theme.TapItWhite
import com.pulasthi.tapit.viewmodel.ReloadStatus
import com.pulasthi.tapit.viewmodel.ReloadViewModel

@Composable
fun ReloadConfirmScreen(
    onBack: () -> Unit,
    onNavigateToSuccess: () -> Unit,
    viewModel: ReloadViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val card = uiState.cards.getOrNull(uiState.selectedCardIndex)

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
                text = "Pay Bill",
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
                    modifier = Modifier.padding(bottom = 24.dp),
                )
            }

            SummaryRow(label = "Amount", value = uiState.formattedAmount)
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = TapItWhite.copy(alpha = 0.35f),
            )
            SummaryRow(
                label = "Mobile No",
                value = formatDisplayMobile(uiState.mobileNumber),
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = viewModel::onConfirmPay,
                enabled = uiState.reloadStatus != ReloadStatus.Processing,
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
                if (uiState.reloadStatus == ReloadStatus.Processing) {
                    CircularProgressIndicator(
                        color = TapItWhite,
                        modifier = Modifier.height(22.dp),
                    )
                } else {
                    Text("Pay", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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

private fun formatDisplayMobile(number: String): String {
    val digits = number.filter { it.isDigit() }
    return when {
        digits.length == 9 -> "0$digits"
        digits.startsWith("0") -> digits
        else -> number
    }
}
