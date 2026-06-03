package com.example.focusgarden.ui.progress

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focusgarden.data.model.GardenPlantView
import com.example.focusgarden.model.PlantStage
import com.example.focusgarden.model.PlantStatus
import com.example.focusgarden.model.SessionStatus
import com.example.focusgarden.ui.components.CategoryGlyph
import com.example.focusgarden.ui.components.FocusBackground
import com.example.focusgarden.ui.components.FocusPlantGlyph
import com.example.focusgarden.ui.components.GlassCard
import com.example.focusgarden.ui.components.SessionStatusGlyph
import com.example.focusgarden.ui.theme.Forest
import com.example.focusgarden.ui.theme.TextMuted
import com.example.focusgarden.ui.theme.focusGardenVisuals
import com.example.focusgarden.util.DateTimeUtils
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(viewModel: ProgressViewModel) {
    val state by viewModel.uiState.collectAsState()
    var showHistory by remember { mutableStateOf(false) }
    var selectedGarden by remember { mutableStateOf<BalanceZone?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val weeklyBalanceZones = remember(state.recentSessions) { ProgressCalculator.buildBalanceZones(state.recentSessions) }
    val gardenBalanceZones = remember(state.categoryStats) { ProgressCalculator.buildAllTimeBalanceZones(state.categoryStats) }
    val streak = remember(state.recentSessions) { ProgressCalculator.calculateStreak(state.recentSessions) }
    val visuals = focusGardenVisuals()

    FocusBackground {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Прогресс",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                    HistoryIconButton(onClick = { showHistory = true })
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(9.dp)
                    ) {
                        MiniMetric(
                            value = DateTimeUtils.formatSeconds(state.totalFocusSeconds),
                            label = "Фокус",
                            modifier = Modifier.weight(1f)
                        )
                        MiniMetric(
                            value = state.completedSessions.toString(),
                            label = "Деревья",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(9.dp)
                    ) {
                        MiniMetric(
                            value = streak.toString(),
                            label = "Серия",
                            modifier = Modifier.weight(1f)
                        )
                        MiniMetric(
                            value = state.failedSessions.toString(),
                            label = "Срывы",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                GlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("7 дней", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                            Text("${state.successRate}%", color = visuals.accent, style = MaterialTheme.typography.labelLarge)
                        }
                        WeekFocusChart(points = state.weekFocus)
                    }
                }
            }

            item {
                BalanceProfile(zones = weeklyBalanceZones)
            }

            item {
                BalanceGarden(
                    zones = gardenBalanceZones,
                    plants = state.plants,
                    onZoneClick = { zone -> selectedGarden = zone }
                )
            }

            item { Spacer(Modifier.height(8.dp)) }
        }
    }

    if (showHistory) {
        ModalBottomSheet(
            onDismissRequest = { showHistory = false },
            sheetState = sheetState,
            containerColor = visuals.sheetContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 22.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text("История", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(6.dp))
                }
                if (state.recentSessions.isEmpty()) {
                    item { Text("Пока нет сессий", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f)) }
                } else {
                    items(state.recentSessions.take(12)) { session ->
                        val ok = session.status == SessionStatus.COMPLETED.name
                        HistoryRow(
                            success = ok,
                            title = session.categoryName,
                            subtitle = "${session.plannedDurationMinutes} мин · ${DateTimeUtils.formatDateTime(session.startedAt)}"
                        )
                    }
                }
                item { Spacer(Modifier.height(20.dp)) }
            }
        }
    }

    selectedGarden?.let { zone ->
        val categoryPlants = state.plants
            .filter { plant -> plant.categoryName == zone.name }
            .sortedByDescending { it.createdAt }
        ModalBottomSheet(
            onDismissRequest = { selectedGarden = null },
            sheetState = sheetState,
            containerColor = visuals.sheetContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            CategoryGardenSheet(zone = zone, plants = categoryPlants)
        }
    }
}

@Composable
private fun MiniMetric(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1)
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.68f), fontSize = 11.sp)
        }
    }
}

@Composable
private fun WeekFocusChart(points: List<WeekFocusPoint>) {
    val visuals = focusGardenVisuals()
    val safePoints = if (points.isEmpty()) {
        listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс").map { WeekFocusPoint(it, 0) }
    } else points
    val max = safePoints.maxOfOrNull { it.minutes }?.coerceAtLeast(1) ?: 1

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(132.dp)
        ) {
            val gap = size.width / (safePoints.size * 2 + 1)
            val barWidth = gap * 0.82f
            safePoints.forEachIndexed { index, point ->
                val normalized = point.minutes.toFloat() / max.toFloat()
                val barHeight = (size.height * 0.16f) + (size.height * 0.72f * normalized)
                val x = gap + index * gap * 2
                val y = size.height - barHeight
                drawRoundRect(
                    color = visuals.chartTrack,
                    topLeft = Offset(x, size.height * 0.12f),
                    size = Size(barWidth, size.height * 0.88f),
                    cornerRadius = CornerRadius(18f, 18f)
                )
                drawRoundRect(
                    color = visuals.accent.copy(alpha = if (point.minutes > 0) 0.94f else 0.20f),
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(18f, 18f)
                )
                if (point.minutes > 0) {
                    drawRoundRect(
                        color = Forest.copy(alpha = 0.20f),
                        topLeft = Offset(x + barWidth * 0.28f, y),
                        size = Size(barWidth * 0.34f, barHeight),
                        cornerRadius = CornerRadius(18f, 18f)
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            safePoints.forEach { point ->
                Text(point.label, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.64f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun BalanceProfile(zones: List<BalanceZone>) {
    val visuals = focusGardenVisuals()
    GlassCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Профиль баланса", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                Text("7 дней", color = visuals.accent.copy(alpha = 0.86f), style = MaterialTheme.typography.labelLarge)
            }
            BalanceRadar(zones = zones)
            Text(
                text = "Распределение успешного фокуса по сферам.",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.66f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun BalanceRadar(zones: List<BalanceZone>) {
    val visuals = focusGardenVisuals()
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(266.dp)
    ) {
        val count = zones.size.coerceAtLeast(5)
        val center = Offset(size.width / 2f, size.height * 0.50f)
        val radius = min(size.width, size.height) * 0.31f
        val startAngle = -PI / 2.0
        val labelRadius = radius * 1.30f
        val axisPoints = List(count) { index ->
            val angle = startAngle + 2.0 * PI * index / count
            Offset(
                x = center.x + cos(angle).toFloat() * radius,
                y = center.y + sin(angle).toFloat() * radius
            )
        }

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    visuals.accent.copy(alpha = 0.12f),
                    Forest.copy(alpha = 0.045f),
                    Color.Transparent
                ),
                center = center,
                radius = radius * 1.62f
            ),
            radius = radius * 1.62f,
            center = center
        )

        repeat(5) { levelIndex ->
            val ratio = (levelIndex + 1) / 5f
            val path = Path()
            axisPoints.forEachIndexed { i, point ->
                val p = Offset(
                    x = center.x + (point.x - center.x) * ratio,
                    y = center.y + (point.y - center.y) * ratio
                )
                if (i == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
            }
            path.close()
            drawPath(
                path = path,
                color = visuals.subtleLine.copy(alpha = if (levelIndex == 4) 0.74f else 0.38f),
                style = Stroke(width = 1.15f)
            )
        }

        axisPoints.forEach { point ->
            drawLine(
                color = visuals.subtleLine.copy(alpha = 0.58f),
                start = center,
                end = point,
                strokeWidth = 1.0f,
                cap = StrokeCap.Round
            )
        }

        val profilePath = Path()
        zones.forEachIndexed { index, zone ->
            val axis = axisPoints[index]
            val ratio = zone.score.coerceIn(0f, 1f)
            val point = Offset(
                x = center.x + (axis.x - center.x) * ratio,
                y = center.y + (axis.y - center.y) * ratio
            )
            if (index == 0) profilePath.moveTo(point.x, point.y) else profilePath.lineTo(point.x, point.y)
        }
        profilePath.close()
        drawPath(profilePath, color = visuals.accent.copy(alpha = 0.16f))
        drawPath(profilePath, color = visuals.accent.copy(alpha = 0.95f), style = Stroke(width = 2.4f, cap = StrokeCap.Round))

        zones.forEachIndexed { index, zone ->
            val axis = axisPoints[index]
            val ratio = zone.score.coerceIn(0f, 1f)
            val point = Offset(
                x = center.x + (axis.x - center.x) * ratio,
                y = center.y + (axis.y - center.y) * ratio
            )
            drawCircle(visuals.accent.copy(alpha = if (zone.minutes > 0) 0.92f else 0.22f), radius = 5.6f, center = point)
            drawCircle(Forest.copy(alpha = 0.18f), radius = 15f, center = point)
        }

        drawRadarLabels(zones, center, labelRadius, visuals.mutedText)
    }
}

private fun DrawScope.drawRadarLabels(
    zones: List<BalanceZone>,
    center: Offset,
    radius: Float,
    labelColor: Color
) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = labelColor.copy(alpha = 0.92f).toArgb()
        textAlign = Paint.Align.CENTER
        textSize = 12.sp.toPx()
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
    }
    val startAngle = -PI / 2.0
    drawIntoCanvas { canvas ->
        zones.forEachIndexed { index, zone ->
            val angle = startAngle + 2.0 * PI * index / zones.size
            val x = center.x + cos(angle).toFloat() * radius
            val y = center.y + sin(angle).toFloat() * radius + 4.dp.toPx()
            canvas.nativeCanvas.drawText(zone.name, x, y, paint)
        }
    }
}

@Composable
private fun BalanceGarden(
    zones: List<BalanceZone>,
    plants: List<GardenPlantView>,
    onZoneClick: (BalanceZone) -> Unit
) {
    val visuals = focusGardenVisuals()
    GlassCard {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Сад баланса", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                    Text(
                        text = "за всё время",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.62f),
                        fontSize = 12.sp
                    )
                }
                Text("LVL 0–5", color = visuals.accent.copy(alpha = 0.86f), style = MaterialTheme.typography.labelLarge)
            }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                zones.forEach { zone ->
                    val zonePlants = plants
                        .filter { it.categoryName == zone.name }
                        .sortedByDescending { it.createdAt }
                    BalanceZoneRow(
                        zone = zone,
                        plants = zonePlants,
                        onClick = { onZoneClick(zone) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BalanceZoneRow(
    zone: BalanceZone,
    plants: List<GardenPlantView>,
    onClick: () -> Unit
) {
    val visuals = focusGardenVisuals()
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(26.dp),
        color = visuals.chipContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 13.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(34.dp), contentAlignment = Alignment.Center) {
                CategoryGlyph(zone.name, modifier = Modifier.size(26.dp), selected = zone.minutes > 0)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(zone.name, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("LVL ${zone.level}", color = visuals.accent.copy(alpha = 0.92f), style = MaterialTheme.typography.labelLarge)
                        ChevronGlyph()
                    }
                }
                Text(
                    text = "${zone.minutes} мин · ${zone.completedSessions} деревьев · ${plants.size} всего",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.66f),
                    fontSize = 12.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BalanceGrowthStrip(level = zone.level)
                    MiniCategoryGardenPreview(plants = plants.take(5), level = zone.level)
                }
            }
        }
    }
}

@Composable
private fun MiniCategoryGardenPreview(
    plants: List<GardenPlantView>,
    level: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (plants.isEmpty()) {
            repeat(3) { index ->
                Canvas(modifier = Modifier.size(width = 10.dp, height = 18.dp)) {
                    val alpha = if (index < level) 0.22f else 0.10f
                    drawLine(
                        color = TextMuted.copy(alpha = alpha),
                        start = Offset(size.width / 2f, size.height * 0.72f),
                        end = Offset(size.width / 2f, size.height * 0.42f),
                        strokeWidth = 1.3f,
                        cap = StrokeCap.Round
                    )
                    drawCircle(
                        color = TextMuted.copy(alpha = alpha),
                        radius = 2.3f,
                        center = Offset(size.width / 2f, size.height * 0.36f)
                    )
                }
            }
        } else {
            plants.forEach { plant ->
                val grown = plant.status == PlantStatus.GROWN.name
                FocusPlantGlyph(
                    stage = if (grown) stageForPlantType(plant.plantType) else PlantStage.WITHERED_TREE,
                    modifier = Modifier.size(18.dp),
                    active = grown
                )
            }
        }
    }
}

@Composable
private fun ChevronGlyph() {
    val visuals = focusGardenVisuals()
    Canvas(modifier = Modifier.size(14.dp)) {
        drawLine(
            color = visuals.accent.copy(alpha = 0.76f),
            start = Offset(size.width * 0.36f, size.height * 0.24f),
            end = Offset(size.width * 0.64f, size.height * 0.50f),
            strokeWidth = 1.9f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = visuals.accent.copy(alpha = 0.76f),
            start = Offset(size.width * 0.64f, size.height * 0.50f),
            end = Offset(size.width * 0.36f, size.height * 0.76f),
            strokeWidth = 1.9f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun CategoryGardenSheet(
    zone: BalanceZone,
    plants: List<GardenPlantView>
) {
    val visuals = focusGardenVisuals()
    Column(
        modifier = Modifier.padding(horizontal = 22.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = visuals.selectedChipContainer,
                contentColor = visuals.accent
            ) {
                Box(contentAlignment = Alignment.Center) {
                    CategoryGlyph(zone.name, modifier = Modifier.size(29.dp), selected = true)
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(zone.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                Text(
                    text = "${zone.minutes} мин · LVL ${zone.level}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.70f),
                    fontSize = 13.sp
                )
            }
        }

        CategoryGardenField(zone = zone, plants = plants)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            MiniMetric(value = zone.completedSessions.toString(), label = "Деревья", modifier = Modifier.weight(1f))
            MiniMetric(value = plants.count { it.status == PlantStatus.WITHERED.name }.toString(), label = "Срывы", modifier = Modifier.weight(1f))
        }

        Text(
            text = "Зона развивается только от успешно завершённых фокус-сессий. Прерванные сессии сохраняются как засохшие растения.",
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.58f),
            fontSize = 12.sp
        )
        Spacer(Modifier.height(18.dp))
    }
}

@Composable
private fun CategoryGardenField(
    zone: BalanceZone,
    plants: List<GardenPlantView>
) {
    val visuals = focusGardenVisuals()
    GlassCard {
        Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Сад зоны", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                Text("${plants.size} растений", color = visuals.accent.copy(alpha = 0.84f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            if (plants.isEmpty()) {
                EmptyCategoryGarden(zone = zone)
            } else {
                val rows = plants.chunked(6)
                val maxVisibleRows = 4
                val rowHeight = 42.dp
                val rowGap = 10.dp
                val visibleGardenHeight = rowHeight * maxVisibleRows + rowGap * (maxVisibleRows - 1)

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = visibleGardenHeight),
                    verticalArrangement = Arrangement.spacedBy(rowGap)
                ) {
                    items(rows) { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            row.forEach { plant ->
                                val grown = plant.status == PlantStatus.GROWN.name
                                Box(modifier = Modifier.size(42.dp), contentAlignment = Alignment.Center) {
                                    FocusPlantGlyph(
                                        stage = if (grown) stageForPlantType(plant.plantType) else PlantStage.WITHERED_TREE,
                                        modifier = Modifier.size(34.dp),
                                        active = grown
                                    )
                                }
                            }
                            repeat(6 - row.size) { Spacer(Modifier.size(42.dp)) }
                        }
                    }
                }

                if (rows.size > maxVisibleRows) {
                    Text(
                        text = "Прокрутите сад зоны, чтобы увидеть остальные растения",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.52f),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyCategoryGarden(zone: BalanceZone) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(modifier = Modifier.size(64.dp), contentAlignment = Alignment.Center) {
            CategoryGlyph(zone.name, modifier = Modifier.size(34.dp), selected = false)
        }
        Text("Зона пока пустая", fontWeight = FontWeight.ExtraBold)
        Text("Завершите сессию в этой категории", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.62f), fontSize = 12.sp)
    }
}

@Composable
private fun BalanceGrowthStrip(level: Int) {
    val visuals = focusGardenVisuals()
    Row(
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(5) { index ->
            val active = index < level
            Canvas(modifier = Modifier.size(width = 28.dp, height = 22.dp)) {
                val color = if (active) visuals.accent.copy(alpha = 0.90f) else visuals.mutedText.copy(alpha = 0.24f)
                val secondary = if (active) visuals.accent.copy(alpha = 0.46f) else visuals.mutedText.copy(alpha = 0.12f)
                val baseY = size.height * 0.82f
                val cx = size.width * 0.50f
                drawLine(
                    color = visuals.mutedText.copy(alpha = 0.20f),
                    start = Offset(size.width * 0.18f, baseY),
                    end = Offset(size.width * 0.82f, baseY),
                    strokeWidth = 1.3f,
                    cap = StrokeCap.Round
                )
                if (active) {
                    val topY = size.height * (0.60f - index * 0.060f).coerceAtLeast(0.24f)
                    drawLine(color, Offset(cx, baseY), Offset(cx, topY), strokeWidth = 2.2f, cap = StrokeCap.Round)
                    drawOval(secondary, topLeft = Offset(cx - 10f, topY + 2f), size = Size(10f, 5f))
                    drawOval(secondary, topLeft = Offset(cx + 1f, topY + 4f), size = Size(10f, 5f))
                    if (index >= 3) drawCircle(color.copy(alpha = 0.72f), radius = 2.7f, center = Offset(cx, topY - 2f))
                } else {
                    drawCircle(color, radius = 2.7f, center = Offset(cx, baseY - 4f))
                }
            }
        }
    }
}

@Composable
private fun GardenPreview(plants: List<GardenPlantView>) {
    GlassCard {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            plants.chunked(6).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    row.forEach { plant ->
                        val grown = plant.status == PlantStatus.GROWN.name
                        Box(
                            modifier = Modifier.size(42.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            FocusPlantGlyph(
                                stage = if (grown) stageForPlantType(plant.plantType) else PlantStage.WITHERED_TREE,
                                modifier = Modifier.size(34.dp),
                                active = grown
                            )
                        }
                    }
                    repeat(6 - row.size) { Spacer(Modifier.size(42.dp)) }
                }
            }
        }
    }
}

@Composable
private fun EmptyGarden() {
    GlassCard {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FocusPlantGlyph(stage = PlantStage.SEED, modifier = Modifier.size(58.dp), active = false)
            Text("Сад пуст", fontWeight = FontWeight.ExtraBold)
            Text("Завершите первую сессию", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.64f), fontSize = 12.sp)
        }
    }
}

@Composable
private fun HistoryIconButton(onClick: () -> Unit) {
    val visuals = focusGardenVisuals()
    Surface(
        modifier = Modifier
            .size(46.dp)
            .clickable { onClick() },
        shape = CircleShape,
        color = visuals.iconButtonContainer,
        contentColor = visuals.accent
    ) {
        Canvas(modifier = Modifier.padding(12.dp)) {
            drawCircle(
                color = visuals.accent.copy(alpha = 0.92f),
                radius = size.minDimension * 0.42f,
                center = center,
                style = Stroke(width = 2.2f)
            )
            drawLine(
                color = visuals.accent,
                start = center,
                end = Offset(size.width * 0.50f, size.height * 0.23f),
                strokeWidth = 2.2f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = visuals.accent,
                start = center,
                end = Offset(size.width * 0.70f, size.height * 0.50f),
                strokeWidth = 2.2f,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun HistoryRow(
    success: Boolean,
    title: String,
    subtitle: String
) {
    val visuals = focusGardenVisuals()
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = visuals.chipContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SessionStatusGlyph(success = success, modifier = Modifier.size(28.dp))
            Column {
                Text(title, fontWeight = FontWeight.ExtraBold)
                Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.70f), fontSize = 12.sp)
            }
        }
    }
}

private fun stageForPlantType(type: String): PlantStage = when (type) {
    "RARE_TREE" -> PlantStage.FULL_TREE
    "BIG_TREE" -> PlantStage.BIG_TREE
    "TREE" -> PlantStage.SMALL_TREE
    else -> PlantStage.SPROUT
}
