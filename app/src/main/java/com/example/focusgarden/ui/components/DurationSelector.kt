package com.example.focusgarden.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focusgarden.model.PlantStage
import com.example.focusgarden.ui.theme.Forest
import com.example.focusgarden.ui.theme.Mint
import com.example.focusgarden.ui.theme.focusGardenVisuals

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DurationSelector(
    selectedMinutes: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Длительность"
) {
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val visuals = focusGardenVisuals()

    GlassCard(
        modifier = modifier.clickable { showSheet = true }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = "$selectedMinutes минут",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.linearGradient(listOf(Forest, Mint, visuals.accent)),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text("⌄", color = Color(0xFF06120F), fontWeight = FontWeight.Bold, fontSize = 22.sp)
            }
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = visuals.sheetContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
            dragHandle = {
                Surface(
                    modifier = Modifier.padding(top = 10.dp, bottom = 8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
                    shape = RoundedCornerShape(50)
                ) {
                    Box(modifier = Modifier.padding(horizontal = 22.dp, vertical = 3.dp))
                }
            }
        ) {
            DurationBottomSheetContent(
                selectedMinutes = selectedMinutes,
                onSelected = {
                    onSelected(it)
                    showSheet = false
                }
            )
        }
    }
}

@Composable
private fun DurationBottomSheetContent(
    selectedMinutes: Int,
    onSelected: (Int) -> Unit
) {
    val items = listOf(
        DurationOption(10, "Быстро", PlantStage.SPROUT),
        DurationOption(25, "Классика", PlantStage.SMALL_TREE),
        DurationOption(45, "Глубоко", PlantStage.BIG_TREE),
        DurationOption(60, "Долго", PlantStage.FULL_TREE)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Время фокуса",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold
        )
        items.forEach { item ->
            val selected = selectedMinutes == item.minutes
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelected(item.minutes) },
                shape = RoundedCornerShape(26.dp),
                color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    FocusPlantGlyph(stage = item.stage, modifier = Modifier.size(34.dp), active = true)
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${item.minutes} мин", fontWeight = FontWeight.ExtraBold)
                        Text(item.title, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    }
                    Text(if (selected) "✓" else "", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

private data class DurationOption(
    val minutes: Int,
    val title: String,
    val stage: PlantStage
)
