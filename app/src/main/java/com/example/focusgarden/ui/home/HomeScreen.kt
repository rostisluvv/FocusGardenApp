package com.example.focusgarden.ui.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focusgarden.data.entity.CategoryEntity
import com.example.focusgarden.model.PlantStage
import com.example.focusgarden.ui.components.CategoryGlyph
import com.example.focusgarden.ui.components.FocusBackground
import com.example.focusgarden.ui.components.PlantView
import com.example.focusgarden.ui.theme.Forest
import com.example.focusgarden.ui.theme.Mint
import com.example.focusgarden.ui.theme.Pollen
import com.example.focusgarden.ui.theme.focusGardenVisuals

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenFocus: (Long) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var showTimeSheet by remember { mutableStateOf(false) }
    var showCategorySheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val selectedCategory = state.categories.firstOrNull { it.id == state.selectedCategoryId }
    val visuals = focusGardenVisuals()

    LaunchedEffect(Unit) {
        viewModel.navigationEvents.collect { onOpenFocus(it) }
    }

    FocusBackground(contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.height(2.dp))

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                PlantView(
                    stage = PlantStage.SPROUT,
                    progress = 0.18f,
                    modifier = Modifier.fillMaxWidth(),
                    showLabel = false,
                    size = 248
                )

                Text(
                    text = "%02d:00".format(state.selectedDurationMinutes),
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(36.dp))
                        .clickable { showTimeSheet = true }
                        .padding(horizontal = 24.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = visuals.primaryText
                )

                Surface(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .clickable { showCategorySheet = true },
                    shape = RoundedCornerShape(28.dp),
                    color = visuals.chipContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 11.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CategoryGlyph(
                            name = selectedCategory?.name ?: "Разум",
                            selected = true,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = selectedCategory?.name ?: "Фокус",
                            style = MaterialTheme.typography.labelLarge,
                            color = visuals.primaryText
                        )
                    }
                }
            }

            StartFocusIconButton(
                enabled = state.selectedCategoryId != null,
                onClick = viewModel::startSession
            )
        }
    }

    if (showTimeSheet) {
        ModalBottomSheet(
            onDismissRequest = { showTimeSheet = false },
            sheetState = sheetState,
            containerColor = visuals.sheetContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Время", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                listOf(10, 25, 45, 60).forEach { minutes ->
                    TimeSheetOption(
                        minutes = minutes,
                        selected = state.selectedDurationMinutes == minutes,
                        onClick = {
                            viewModel.selectDuration(minutes)
                            showTimeSheet = false
                        }
                    )
                }
                Spacer(Modifier.height(18.dp))
            }
        }
    }

    if (showCategorySheet) {
        ModalBottomSheet(
            onDismissRequest = { showCategorySheet = false },
            sheetState = sheetState,
            containerColor = visuals.sheetContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Категория", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                state.categories.forEach { category ->
                    CategorySheetOption(
                        category = category,
                        selected = state.selectedCategoryId == category.id,
                        onClick = {
                            viewModel.selectCategory(category.id)
                            showCategorySheet = false
                        }
                    )
                }
                Spacer(Modifier.height(18.dp))
            }
        }
    }
}

@Composable
private fun TimeSheetOption(
    minutes: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    val visuals = focusGardenVisuals()
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = if (selected) visuals.selectedChipContainer else visuals.chipContainer,
        contentColor = if (selected) visuals.accent else MaterialTheme.colorScheme.onSurface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("$minutes мин", fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
            if (selected) SelectionDot()
        }
    }
}

@Composable
private fun CategorySheetOption(
    category: CategoryEntity,
    selected: Boolean,
    onClick: () -> Unit
) {
    val visuals = focusGardenVisuals()
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = if (selected) visuals.selectedChipContainer else visuals.chipContainer,
        contentColor = if (selected) visuals.accent else MaterialTheme.colorScheme.onSurface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 15.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryGlyph(name = category.name, selected = selected, modifier = Modifier.size(28.dp))
            Text(
                text = category.name,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp
            )
            if (selected) SelectionDot()
        }
    }
}

@Composable
private fun SelectionDot() {
    val visuals = focusGardenVisuals()
    Canvas(modifier = Modifier.size(18.dp)) {
        drawCircle(visuals.accent.copy(alpha = 0.20f), radius = size.minDimension * 0.48f, center = center)
        drawCircle(visuals.accent, radius = size.minDimension * 0.20f, center = center)
    }
}

@Composable
private fun StartFocusIconButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    val visuals = focusGardenVisuals()
    val transition = rememberInfiniteTransition(label = "play_pulse")
    val pulse by transition.animateFloat(
        initialValue = 0.82f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(132.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(128.dp)) {
            val active = if (enabled) 1f else 0.35f
            drawCircle(
                color = visuals.accent.copy(alpha = 0.10f * active),
                radius = size.minDimension * 0.46f * pulse,
                center = center,
                style = Stroke(width = 2.2f)
            )
            drawCircle(
                color = Forest.copy(alpha = 0.10f * active),
                radius = size.minDimension * 0.35f * (1.18f - (pulse - 0.82f)),
                center = center,
                style = Stroke(width = 1.6f)
            )
        }
        Box(
            modifier = Modifier
                .size(92.dp)
                .graphicsLayer {
                    shadowElevation = if (enabled) 38f else 0f
                    shape = CircleShape
                    clip = true
                }
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        if (enabled) listOf(Forest, Mint, visuals.accent) else visuals.disabledButtonGradient
                    )
                )
                .clickable(enabled = enabled) { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(36.dp)) {
                val play = Path().apply {
                    moveTo(size.width * 0.34f, size.height * 0.18f)
                    lineTo(size.width * 0.34f, size.height * 0.82f)
                    lineTo(size.width * 0.82f, size.height * 0.50f)
                    close()
                }
                drawPath(play, color = Color(0xFF03110D))
            }
            if (enabled) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    drawCircle(
                        color = Pollen.copy(alpha = 0.18f),
                        radius = size.minDimension * 0.48f,
                        center = Offset(size.width * 0.38f, size.height * 0.28f)
                    )
                }
            }
        }
    }
}
