package com.example.focusgarden.ui.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focusgarden.model.PlantStage
import com.example.focusgarden.model.SessionStatus
import com.example.focusgarden.ui.components.FocusBackground
import com.example.focusgarden.ui.components.FocusPlantGlyph
import com.example.focusgarden.ui.components.GlassCard
import com.example.focusgarden.ui.theme.Mint
import com.example.focusgarden.util.DateTimeUtils

@Composable
fun ResultScreen(
    viewModel: ResultViewModel,
    onHome: () -> Unit,
    onGarden: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val session = state.session
    val success = session?.status == SessionStatus.COMPLETED.name

    FocusBackground(contentPadding = androidx.compose.foundation.layout.PaddingValues(24.dp)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.radialGradient(
                                if (success) listOf(Mint.copy(alpha = 0.34f), Color.Transparent)
                                else listOf(MaterialTheme.colorScheme.error.copy(alpha = 0.24f), Color.Transparent)
                            ),
                            shape = RoundedCornerShape(120.dp)
                        )
                        .padding(26.dp),
                    contentAlignment = Alignment.Center
                ) {
                    FocusPlantGlyph(
                        stage = if (success) PlantStage.FULL_TREE else PlantStage.WITHERED_TREE,
                        modifier = Modifier.size(128.dp),
                        active = success
                    )
                }
                Text(
                    text = if (success) "Сессия завершена" else "Сессия прервана",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.6).sp
                )
                Text(
                    text = if (success) "Растение сохранено" else "Результат учтён",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (session != null) {
                GlassCard {
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ResultRow("Категория", session.categoryName)
                        ResultRow("План", "${session.plannedDurationMinutes} мин")
                        ResultRow("Факт", DateTimeUtils.formatSeconds(session.actualDurationSeconds.toLong()))
                        ResultRow("Очки", session.points.toString())
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onHome,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) { Text("На главный экран", fontWeight = FontWeight.Bold) }
                OutlinedButton(
                    onClick = onGarden,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp)
                ) { Text("Прогресс", fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.ExtraBold)
    }
}
