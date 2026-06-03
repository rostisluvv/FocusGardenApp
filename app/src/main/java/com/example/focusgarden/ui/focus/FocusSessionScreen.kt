package com.example.focusgarden.ui.focus

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalContext
import com.example.focusgarden.ui.components.FocusBackground
import com.example.focusgarden.ui.components.GreenPixelRain
import com.example.focusgarden.ui.components.CategoryGlyph
import com.example.focusgarden.ui.components.PlantView
import com.example.focusgarden.ui.theme.focusGardenVisuals
import com.example.focusgarden.util.DateTimeUtils
import com.example.focusgarden.util.SessionFeedback

@Composable
fun FocusSessionScreen(
    viewModel: FocusSessionViewModel,
    onOpenResult: (Long) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val latestState by rememberUpdatedState(state)
    val visuals = focusGardenVisuals()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is FocusEvent.NavigateResult -> {
                    SessionFeedback.playSessionFinished(
                        context = context,
                        status = latestState.status,
                        soundEnabled = latestState.soundEnabled,
                        vibrationEnabled = latestState.vibrationEnabled
                    )
                    onOpenResult(event.sessionId)
                }
                FocusEvent.NavigateBack -> onBack()
            }
        }
    }

    DisposableEffect(lifecycleOwner, state.strictModeEnabled) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                viewModel.failByBackgroundIfStrictMode()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    FocusBackground(contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp)) {
        GreenPixelRain(modifier = Modifier.matchParentSize(), intensity = 92)
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = visuals.chipContainer,
                    contentColor = visuals.primaryText
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CategoryGlyph(
                            name = state.categoryName,
                            selected = true,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = state.categoryName.ifBlank { "Фокус" },
                            style = MaterialTheme.typography.labelLarge,
                            color = visuals.primaryText,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
                StopButton(onClick = viewModel::showCancelDialog)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = DateTimeUtils.formatTimer(state.remainingSeconds),
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = visuals.primaryText
                )
                PlantView(
                    stage = state.plantStage,
                    progress = state.progress,
                    showLabel = false,
                    size = 252
                )
            }

            Text(
                text = if (state.strictModeEnabled) "STRICT" else "FOCUS",
                color = visuals.accent.copy(alpha = 0.70f),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }

    if (state.isCancelDialogVisible) {
        AlertDialog(
            onDismissRequest = viewModel::hideCancelDialog,
            shape = RoundedCornerShape(30.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("Прервать?", fontWeight = FontWeight.ExtraBold) },
            text = { Text("Сессия сохранится как сорванная, растение засохнет.") },
            confirmButton = {
                Button(
                    onClick = viewModel::cancelSession,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Прервать") }
            },
            dismissButton = {
                TextButton(onClick = viewModel::hideCancelDialog) { Text("Остаться") }
            }
        )
    }
}

@Composable
private fun StopButton(onClick: () -> Unit) {
    val visuals = focusGardenVisuals()
    Surface(
        modifier = Modifier
            .size(44.dp)
            .clickable { onClick() },
        shape = CircleShape,
        color = visuals.iconButtonContainer,
        contentColor = visuals.iconButtonContent
    ) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(20.dp)) {
                drawCircle(
                    color = visuals.iconButtonContent.copy(alpha = 0.20f),
                    radius = size.minDimension * 0.48f,
                    center = center,
                    style = Stroke(width = 1.2f)
                )
                drawLine(
                    color = visuals.iconButtonContent,
                    start = Offset(size.width * 0.32f, size.height * 0.32f),
                    end = Offset(size.width * 0.68f, size.height * 0.68f),
                    strokeWidth = 2.6f
                )
                drawLine(
                    color = visuals.iconButtonContent,
                    start = Offset(size.width * 0.68f, size.height * 0.32f),
                    end = Offset(size.width * 0.32f, size.height * 0.68f),
                    strokeWidth = 2.6f
                )
            }
        }
    }
}
