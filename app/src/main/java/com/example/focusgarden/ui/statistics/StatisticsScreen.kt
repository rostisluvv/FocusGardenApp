package com.example.focusgarden.ui.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.focusgarden.model.SessionStatus
import com.example.focusgarden.ui.components.FocusBackground
import com.example.focusgarden.ui.components.GlassCard
import com.example.focusgarden.ui.components.ScreenHeader
import com.example.focusgarden.ui.components.StatisticCard
import com.example.focusgarden.util.DateTimeUtils

@Composable
fun StatisticsScreen(viewModel: StatisticsViewModel) {
    val state by viewModel.uiState.collectAsState()

    FocusBackground {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                ScreenHeader(
                    title = "Статистика",
                    subtitle = "Коротко о прогрессе"
                )
            }
            item {
                StatisticCard("Общее время фокусировки", DateTimeUtils.formatSeconds(state.totalFocusSeconds), icon = "⏱️")
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    StatisticCard("Успешно", state.completedSessions.toString(), icon = "✅", modifier = Modifier.weight(1f))
                    StatisticCard("Прервано", state.failedSessions.toString(), icon = "🥀", modifier = Modifier.weight(1f))
                }
            }
            item {
                GlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Успешность", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${state.successRate}%", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.ExtraBold)
                        LinearProgressIndicator(
                            progress = { state.successRate.coerceIn(0, 100) / 100f },
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
            item {
                Text("По категориям", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
            }
            if (state.categoryStats.isEmpty()) {
                item { Text("Нет данных по категориям", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            } else {
                items(state.categoryStats) { item ->
                    GlassCard {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("${item.categoryIcon} ${item.categoryName}", fontWeight = FontWeight.ExtraBold)
                            Text("${DateTimeUtils.formatSeconds(item.totalFocusSeconds)} · ${item.completedSessions}/${item.failedSessions}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            item {
                Text("Последние сессии", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
            }
            if (state.recentSessions.isEmpty()) {
                item { Text("История пока пустая", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            } else {
                items(state.recentSessions) { session ->
                    GlassCard {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            val ok = session.status == SessionStatus.COMPLETED.name
                            Text(
                                text = "${if (ok) "✅" else "🥀"} ${session.categoryIcon} ${session.categoryName}",
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text("${session.plannedDurationMinutes} мин · ${DateTimeUtils.formatDateTime(session.startedAt)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
