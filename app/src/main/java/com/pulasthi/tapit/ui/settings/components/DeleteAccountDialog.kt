package com.pulasthi.tapit.ui.settings.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.pulasthi.tapit.ui.theme.TapItDeleteDialogBg
import com.pulasthi.tapit.ui.theme.TapItDeleteRed
import com.pulasthi.tapit.ui.theme.TapItWhite

@Composable
fun DeleteAccountDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = TapItDeleteDialogBg,
            border = BorderStroke(1.dp, TapItDeleteRed.copy(alpha = 0.3f)),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Are You Sure want to delete\nTap-It Account",
                    color = TapItDeleteRed,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
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
                            containerColor = TapItWhite,
                            contentColor = TapItDeleteRed,
                        ),
                        border = BorderStroke(1.dp, TapItDeleteRed.copy(alpha = 0.4f)),
                    ) {
                        Text("Yes", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TapItWhite,
                            contentColor = TapItDeleteRed,
                        ),
                        border = BorderStroke(1.dp, TapItDeleteRed.copy(alpha = 0.4f)),
                    ) {
                        Text("No", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
