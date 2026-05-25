package com.pulasthi.tapit.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pulasthi.tapit.ui.settings.components.SettingsScaffold
import com.pulasthi.tapit.ui.theme.TapItSettingsMenuCard
import com.pulasthi.tapit.ui.theme.TapItTextPrimary
import com.pulasthi.tapit.viewmodel.LanguageViewModel

@Composable
fun LanguageScreen(
    onBack: () -> Unit,
    viewModel: LanguageViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScaffold(title = "Select Your Language", onBack = onBack) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp),
        ) {
            uiState.options.forEachIndexed { index, option ->
                val selected = option.id == uiState.selectedId
                Text(
                    text = option.label,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (selected) TapItSettingsMenuCard.copy(alpha = 0.9f) else TapItSettingsMenuCard,
                            RoundedCornerShape(28.dp),
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { viewModel.onLanguageSelected(option.id) },
                        )
                        .padding(vertical = 18.dp, horizontal = 24.dp),
                    color = TapItTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                if (index < uiState.options.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = TapItTextPrimary.copy(alpha = 0.2f),
                    )
                }
            }
        }
    }
}
