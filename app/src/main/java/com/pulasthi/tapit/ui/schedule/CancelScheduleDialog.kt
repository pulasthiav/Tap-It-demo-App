package com.pulasthi.tapit.ui.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.pulasthi.tapit.ui.theme.TapItTextPrimary
import com.pulasthi.tapit.ui.theme.TapItTextSecondary
import com.pulasthi.tapit.ui.theme.TapItWhite
import com.pulasthi.tapit.viewmodel.ScheduledPayment

@Composable
fun CancelScheduleDialog(
    schedule: ScheduledPayment,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = TapItWhite,
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Cancel scheduled payment?",
                    color = TapItTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = schedule.recipientName,
                    color = TapItTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 12.dp),
                )
                Text(
                    text = "LKR ${schedule.formattedAmount()} · ${schedule.frequency.name}",
                    color = TapItTextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 20.dp),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TapItTextSecondary.copy(alpha = 0.2f),
                            contentColor = TapItTextPrimary,
                        ),
                    ) {
                        Text("Yes, cancel", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TapItTextSecondary.copy(alpha = 0.2f),
                            contentColor = TapItTextPrimary,
                        ),
                    ) {
                        Text("No", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
