package com.example.focusgarden.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun StatisticCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    icon: String = "✦"
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column {
            Text(text = icon, fontSize = 22.sp)
            Text(text = title, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.4).sp
            )
        }
    }
}
