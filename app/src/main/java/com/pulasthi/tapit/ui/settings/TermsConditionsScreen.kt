package com.pulasthi.tapit.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulasthi.tapit.ui.settings.components.SettingsScaffold
import com.pulasthi.tapit.ui.theme.TapItInputBackground
import com.pulasthi.tapit.ui.theme.TapItTermsAccent
import com.pulasthi.tapit.ui.theme.TapItTextPrimary

@Composable
fun TermsConditionsScreen(onBack: () -> Unit) {
    SettingsScaffold(title = "TERMS & CONDITIONS", onBack = onBack) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = TapItInputBackground,
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
            ) {
                Text(
                    text = "TERMS & CONDITIONS OF USE — TAP IT",
                    color = TapItTermsAccent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp),
                )
                termsSections.forEach { section ->
                    Text(
                        text = section,
                        color = TapItTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp),
                    )
                }
            }
        }
    }
}

private val termsSections = listOf(
    "1. General: Users must be 18+, Sri Lankan residents with a valid NIC and bank account.",
    "2. Security: Keep your PIN and OTP confidential. Report lost devices immediately.",
    "3. Transactions: All transfers are final and non-reversible. Verify account numbers before paying.",
    "4. Prohibited Use: Illegal activities including money laundering and hacking may result in account bans.",
    "5. Data & Privacy: KYC data is collected in compliance with Central Bank of Sri Lanka regulations.",
    "6. Liability: Tap-It is not liable for service downtime or user errors such as wrong account numbers.",
    "7. Contact: Reach us via the In-App Help Center or email support@tapit.lk.",
)
