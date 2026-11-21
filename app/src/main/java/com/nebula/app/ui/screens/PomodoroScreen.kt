package com.nebula.app.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nebula.app.data.NebulaRepository
import com.nebula.app.data.PomodoroSession
import com.nebula.app.ui.animations.RevealIn
import com.nebula.app.ui.animations.rememberRevealed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale

private const val FOCUS_MIN = 25
private const val SHORT_BREAK_MIN = 5
private const val LONG_BREAK_MIN = 15

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroScreen(repository: NebulaRepository) {
    val sessions by repository.sessions.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    var mode by remember { mutableStateOf("FOCUS") }
    var totalSeconds by remember { mutableLongStateOf(FOCUS_MIN * 60L) }
    var remaining by remember { mutableLongStateOf(FOCUS_MIN * 60L) }
    var isRunning by remember { mutableStateOf(false) }
    var completedFocusToday by remember { mutableIntStateOf(0) }

    val todayStart = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    completedFocusToday = sessions.count { it.type == "FOCUS" && it.completedAt >= todayStart }

    LaunchedEffect(isRunning, remaining) {
        while (isRunning && remaining > 0) {
            delay(1000)
            remaining--
        }
        if (remaining == 0L && isRunning) {
            isRunning = false
            // mark session complete
            val type = mode
            val minutes = (totalSeconds / 60).toInt()
            scope.launch {
                repository.addSession(
                    PomodoroSession(
                        id = System.currentTimeMillis(),
                        type = type,
                        durationMinutes = minutes,
                        completedAt = System.currentTimeMillis(),
                        taskLabel = ""
                    )
                )
            }
            when (mode) {
                "FOCUS" -> {
                    mode = "SHORT_BREAK"
                    totalSeconds = SHORT_BREAK_MIN * 60L
                    remaining = SHORT_BREAK_MIN * 60L
                }
                "SHORT_BREAK" -> {
                    mode = "FOCUS"
                    totalSeconds = FOCUS_MIN * 60L
                    remaining = FOCUS_MIN * 60L
                }
            }
        }
    }

    val progress = if (totalSeconds > 0L) remaining.toFloat() / totalSeconds else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(500),
        label = "timerProgress"
    )

    val pulseTransition = rememberInfiniteTransition(label = "timerPulse")
    val pulseScale by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(750), RepeatMode.Reverse),
        label = "pulseScale"
    )

    fun selectMode(newMode: String) {
        mode = newMode
        isRunning = false
        totalSeconds = when (newMode) {
            "FOCUS" -> FOCUS_MIN * 60L
            "SHORT_BREAK" -> SHORT_BREAK_MIN * 60L
            else -> LONG_BREAK_MIN * 60L
        }
        remaining = totalSeconds
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Фокус", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(
                            "Помодоро сегодня: $completedFocusToday",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("FOCUS" to "Фокус", "SHORT_BREAK" to "Короткий", "LONG_BREAK" to "Длинный").forEach { (key, label) ->
                    FilterChip(
                        selected = mode == key,
                        onClick = { selectMode(key) },
                        label = { Text(label) }
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // Timer ring
            val trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            val progressColor = if (mode == "FOCUS") MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.tertiary
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(260.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 14.dp.toPx()
                    drawArc(
                        color = trackColor,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = Size(size.width - strokeWidth, size.height - strokeWidth),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = progressColor,
                        startAngle = -90f,
                        sweepAngle = 360f * animatedProgress,
                        useCenter = false,
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = Size(size.width - strokeWidth, size.height - strokeWidth),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        formatTime(remaining),
                        fontSize = 52.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.graphicsLayer {
                            scaleX = if (isRunning) pulseScale else 1f
                            scaleY = if (isRunning) pulseScale else 1f
                        }
                    )
                    Text(
                        when (mode) {
                            "FOCUS" -> "Сессия фокуса"
                            "SHORT_BREAK" -> "Короткий перерыв"
                            else -> "Длинный перерыв"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { isRunning = !isRunning },
                    modifier = Modifier.height(52.dp)
                ) {
                    Crossfade(targetState = isRunning, animationSpec = tween(280), label = "playIcon") { running ->
                        if (running) {
                            Icon(Icons.Filled.Stop, contentDescription = null)
                        } else {
                            Icon(Icons.Filled.PlayArrow, contentDescription = null)
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(if (isRunning) "Стоп" else "Старт", style = MaterialTheme.typography.titleMedium)
                }
                IconButton(onClick = { selectMode(mode) }) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Сбросить")
                }
            }

            Spacer(Modifier.height(28.dp))

            Text("Последние сессии", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            val recent = sessions.sortedByDescending { it.completedAt }.take(5)
            if (recent.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Text(
                        "Завершённых сессий пока нет — начни первую!",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                recent.forEachIndexed { index, session ->
                    RevealIn(visible = rememberRevealed(index * 60L)) {
                        SessionRow(session)
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SessionRow(session: PomodoroSession) {
    val typeName = when (session.type) {
        "FOCUS" -> "🧘 Фокус"
        "SHORT_BREAK" -> "☕ Перерыв"
        else -> "🌙 Длинный"
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(typeName, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            Text(
                "${session.durationMinutes} мин",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatTime(totalSeconds: Long): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%02d:%02d", minutes, seconds)
}