package com.pulasthi.tapit.ui.govpay

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulasthi.tapit.ui.components.FlowScreenScaffold
import com.pulasthi.tapit.ui.theme.TapItBannerPurple
import com.pulasthi.tapit.ui.theme.TapItDeleteRed
import com.pulasthi.tapit.ui.theme.TapItTextPrimary
import com.pulasthi.tapit.viewmodel.GovPayViewModel

@Composable
fun GovPayScreen(
    onNavigateToFinesForm: () -> Unit,
    onBack: () -> Unit,
    viewModel: GovPayViewModel,
) {
    LaunchedEffect(Unit) {
        viewModel.navigateToFinesForm.collect { onNavigateToFinesForm() }
    }

    FlowScreenScaffold(
        title = "",
        showBack = true,
        onBack = onBack,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            GovPayHeaderTitle()

            Text(
                text = "Sri Lanka Police",
                color = TapItTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp, bottom = 32.dp),
            )

            FinesPayMenuItem(onClick = viewModel::onFinesPayClick)
        }
    }
}

@Composable
private fun GovPayHeaderTitle() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Gov Pay",
            color = TapItTextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "( Fines )",
            color = TapItTextPrimary,
            fontSize = 15.sp,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}

@Composable
private fun FinesPayMenuItem(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = TapItBannerPurple.copy(alpha = 0.25f),
            modifier = Modifier.size(72.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                tint = TapItDeleteRed,
                modifier = Modifier
                    .padding(16.dp)
                    .size(40.dp),
            )
        }
        Text(
            text = "Fines Pay",
            color = TapItTextPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 20.dp),
        )
    }
}
