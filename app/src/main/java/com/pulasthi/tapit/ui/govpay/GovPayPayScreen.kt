package com.pulasthi.tapit.ui.govpay

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pulasthi.tapit.ui.components.DashboardBackground
import com.pulasthi.tapit.ui.govpay.components.PaymentCardView
import com.pulasthi.tapit.ui.theme.TapItBannerPurple
import com.pulasthi.tapit.ui.theme.TapItTextPrimary
import com.pulasthi.tapit.ui.theme.TapItTextSecondary
import com.pulasthi.tapit.ui.theme.TapItWhite
import com.pulasthi.tapit.viewmodel.GovPayViewModel

@Composable
fun GovPayPayScreen(
    onNavigateToSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: GovPayViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigateToSuccess.collect { onNavigateToSuccess() }
    }

    DashboardBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TapItWhite,
                    )
                }
                Text(
                    text = "Pay Fines",
                    color = TapItWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }

            Text(
                text = "From",
                color = TapItWhite,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            )

            uiState.selectedCard?.let { card ->
                PaymentCardView(card = card)
            }

            Spacer(modifier = Modifier.height(32.dp))

            PaymentDetailRow(label = "Amount", value = uiState.formattedAmount)
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = TapItTextSecondary.copy(alpha = 0.4f),
            )
            PaymentDetailRow(label = "Mobile No", value = uiState.mobileNumber)

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = viewModel::onPayClick,
                enabled = !uiState.isProcessingPayment,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(bottom = 32.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TapItBannerPurple,
                    contentColor = TapItWhite,
                ),
            ) {
                if (uiState.isProcessingPayment) {
                    CircularProgressIndicator(
                        color = TapItWhite,
                        modifier = Modifier.height(24.dp),
                    )
                } else {
                    Text(text = "Pay", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun PaymentDetailRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = TapItTextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            color = TapItTextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
