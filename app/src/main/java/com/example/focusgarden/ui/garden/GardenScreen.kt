package com.example.focusgarden.ui.garden

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focusgarden.model.PlantStatus
import com.example.focusgarden.ui.components.FocusBackground
import com.example.focusgarden.ui.components.GlassCard
import com.example.focusgarden.ui.components.ScreenHeader
import com.example.focusgarden.util.DateTimeUtils

@Composable
fun GardenScreen(viewModel: GardenViewModel) {
    val state by viewModel.uiState.collectAsState()

    FocusBackground {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            ScreenHeader(
                title = "Сад",
                subtitle = "Твой след фокуса"
            )

            if (state.plants.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("🌱", fontSize = 92.sp)
                    Text("Пока нет растений", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                    Text("Заверши первую фокус-сессию", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.plants) { plant ->
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val grown = plant.status == PlantStatus.GROWN.name
                                Text(if (grown) plantEmoji(plant.plantType) else "🥀", fontSize = 54.sp)
                                Text("${plant.categoryIcon} ${plant.plannedDurationMinutes} мин", fontWeight = FontWeight.ExtraBold)
                                Text(
                                    DateTimeUtils.formatDateTime(plant.createdAt),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun plantEmoji(type: String): String = when (type) {
    "RARE_TREE" -> "🌲"
    "BIG_TREE" -> "🌳"
    "TREE" -> "🌴"
    else -> "🌱"
}
