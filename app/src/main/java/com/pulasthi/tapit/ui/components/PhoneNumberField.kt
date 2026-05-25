package com.pulasthi.tapit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulasthi.tapit.ui.theme.TapItInputBackground
import com.pulasthi.tapit.ui.theme.TapItTextPrimary

@Composable
fun PhoneNumberField(
    countryCode: String,
    mobileNumber: String,
    onMobileNumberChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ColumnLabel(text = "Mobile Number")
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(72.dp)
                .height(56.dp)
                .background(TapItInputBackground, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = countryCode,
                color = TapItTextPrimary,
                fontSize = 14.sp,
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        TapItTextField(
            value = mobileNumber,
            onValueChange = onMobileNumberChange,
            placeholder = "Enter Mobile Number",
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
fun ColumnLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        color = TapItTextPrimary,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier.padding(bottom = 8.dp),
    )
}
