package com.nebula.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nebula.app.data.AppSettings
import com.nebula.app.data.Habit
import com.nebula.app.data.NebulaRepository
import com.nebula.app.data.Note
import com.nebula.app.data.PomodoroSession
import com.nebula.app.data.Quotes
import com.nebula.app.data.dayKey
import com.nebula.app.ui.animations.AnimatedNumber
import com.nebula.app.ui.animations.RevealIn
import com.nebula.app.ui.animations.nebulaSpring
import com.nebula.app.ui.animations.rememberRevealed
import com.nebula.app.ui.navigation.Routes
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

const val WATER_GOAL = 8

private val MoodOptions = listOf("😞", "😕", "😐", "🙂", "😄")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    repository: NebulaRepository,
    onOpenNotes: () -> Unit,
    onOpenStats: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenTasks: () -> Unit
) {
    val settings by repository.settings.collectAsState(initial = AppSettings())
    val notes by repository.notes.collectAsState(initial = emptyList())
    val habits by repository.habits.collectAsState(initial = emptyList())
    val sessions by repository.sessions.collectAsState(initial = emptyList())
    val tasks by repository.tasks.collectAsState(initial = emptyList())
    val water by repository.water.collectAsState(initial = emptyList())
    val moods by repository.moods.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    val todayKey = dayKey(LocalDate.now())
    val todayWater = water.find { it.dateKey == todayKey }?.glasses ?: 0
    val activeTasks = tasks.count { !it.done }

    val todayLetter = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE", Locale("ru")))
        .replaceFirstChar { it.titlecase(Locale("ru")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Привет, ${settings.userName}! 👋", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(todayLetter, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                actions = {
                    IconButton(onClick = { onOpenSettings() }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Настройки")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onOpenNotes() }) {
                Icon(Icons.Filled.Add, contentDescription = "Новая заметка")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // Цитата дня
            val revealedQuote = rememberRevealed(0)
            RevealIn(visible = revealedQuote) { QuoteOfTheDayCard() }

            Spacer(Modifier.height(16.dp))

            // Focus progress card
            val todayFocus = sessions.count {
                it.type == "FOCUS" && it.completedAt >= LocalDate.now().atStartOfDay()
                    .atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
            }
            val revealedFocus = rememberRevealed(90)
            RevealIn(visible = revealedFocus) {
                FocusCard(todayFocus = todayFocus, goal = settings.dailyFocusGoal)
            }

            Spacer(Modifier.height(16.dp))

            // Quick stats row
            val revealedStats = rememberRevealed(180)
            RevealIn(visible = revealedStats) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatTile(
                        modifier = Modifier.weight(1f),
                        label = "Заметки",
                        value = notes.size,
                        color = MaterialTheme.colorScheme.primary,
                        onClick = onOpenNotes
                    )
                    StatTile(
                        modifier = Modifier.weight(1f),
                        label = "Привычки",
                        value = habits.size,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    StatTile(
                        modifier = Modifier.weight(1f),
                        label = "Фокус",
                        value = sessions.size,
                        color = MaterialTheme.colorScheme.secondary,
                        onClick = onOpenStats
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Задачи сегодня
            val revealedTasks = rememberRevealed(270)
            RevealIn(visible = revealedTasks) {
                SectionHeader("Задачи", onClick = { onOpenTasks() })
            }
            Spacer(Modifier.height(8.dp))
            if (tasks.isEmpty()) {
                RevealIn(visible = revealedTasks) { EmptyHint("Активных задач нет — добавь пару ✍️") }
            } else {
                RevealIn(visible = revealedTasks) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenTasks() },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                if (activeTasks > 0) "В работе: $activeTasks задач"
                                else "Все задачи выполнены! 🎉",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(8.dp))
                            tasks.filter { !it.done }.take(3).forEach { task ->
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 3.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (task.color != 0) Color(task.color) else MaterialTheme.colorScheme.primary)
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Text(task.title, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Вода и настроение
            val revealedWater = rememberRevealed(360)
            RevealIn(visible = revealedWater) {
                WaterCard(glasses = todayWater, onPlus = {
                    scope.launch { repository.incrementWater(todayKey, 1) }
                }, onMinus = {
                    scope.launch { repository.incrementWater(todayKey, -1) }
                })
            }

            Spacer(Modifier.height(16.dp))

            val revealedMood = rememberRevealed(450)
            RevealIn(visible = revealedMood) {
                MoodCard(currentMood = moods.find { it.dateKey == todayKey }?.mood ?: "") { mood ->
                    scope.launch { repository.setMood(todayKey, mood) }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Habits today
            val revealedHabits = rememberRevealed(540)
            RevealIn(visible = revealedHabits) {
                SectionHeader("Привычки на сегодня", onClick = { onOpenStats() })
                Spacer(Modifier.height(8.dp))
                if (habits.isEmpty()) {
                    EmptyHint("Добавь привычки во вкладке «Привычки»")
                } else {
                    habits.forEach { habit ->
                        HabitTodayRow(habit, done = todayKey in habit.completionDates)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            val revealedNotes = rememberRevealed(630)
            RevealIn(visible = revealedNotes) {
                SectionHeader("Последние заметки", onClick = { onOpenNotes() })
                Spacer(Modifier.height(8.dp))
                if (notes.isEmpty()) {
                    EmptyHint("Создай первую заметку кнопкой +")
                } else {
                    notes.sortedByDescending { it.updatedAt }.take(3).forEach { note ->
                        NoteCard(note)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun QuoteOfTheDayCard() {
    val quote = Quotes.quoteOfTheDay()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("💡", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.width(8.dp))
                Text("Цитата дня", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                quote.text,
                style = MaterialTheme.typography.bodyLarge,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "— ${quote.author}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun WaterCard(glasses: Int, onPlus: () -> Unit, onMinus: () -> Unit) {
    val progress = (glasses.toFloat() / WATER_GOAL).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(progress, nebulaSpring, label = "waterProgress")
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.WaterDrop, contentDescription = null, tint = Color(0xFF29B6F6))
                Spacer(Modifier.width(8.dp))
                Text("Вода сегодня", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                Text("$glasses / $WATER_GOAL", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = Color(0xFF29B6F6),
                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onMinus, modifier = Modifier.weight(1f)) {
                    Text("− 1")
                }
                Button(onClick = onPlus, modifier = Modifier.weight(1f)) {
                    Text("+ 1 стакан")
                }
            }
        }
    }
}

@Composable
private fun MoodCard(currentMood: String, onSelect: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🎭", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.width(8.dp))
                Text("Как настроение?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                if (currentMood.isNotEmpty()) {
                    Text("Сегодня: $currentMood", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MoodOptions.forEach { mood ->
                    val selected = mood == currentMood || (currentMood.isEmpty() && mood == "😐")
                    val emojiScale by animateFloatAsState(
                        targetValue = if (selected) 1.3f else 1f,
                        animationSpec = nebulaSpring,
                        label = "moodScale"
                    )
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(CircleShape)
                            .background(if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface)
                            .clickable { onSelect(mood) },
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .graphicsLayer {
                                    scaleX = emojiScale
                                    scaleY = emojiScale
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(mood, fontSize = 26.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        IconButton(onClick = onClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
    }
}

@Composable
fun EmptyHint(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Text(
            text,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FocusCard(todayFocus: Int, goal: Int) {
    val progress = if (goal > 0) (todayFocus.toFloat() / goal).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(progress, nebulaSpring, label = "focusProgress")
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f)
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Прогресс фокуса",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    "$todayFocus из $goal сессий",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    if (progress >= 1f) "Цель дня достигнута! 🎉" else "До цели осталось ${goal - todayFocus}",
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun StatTile(modifier: Modifier = Modifier, label: String, value: Int, color: Color, onClick: (() -> Unit)? = null) {
    val clickModifier = if (onClick != null) Modifier.clickable { onClick() } else Modifier
    Card(
        modifier = modifier.then(clickModifier),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            AnimatedNumber(target = value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = color)
            Spacer(Modifier.height(4.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun HabitTodayRow(habit: Habit, done: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(if (done) Color(habit.color) else Color(habit.color).copy(alpha = 0.3f))
            )
            Spacer(Modifier.width(12.dp))
            Text(habit.name, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            Text(
                if (done) "✓" else "•",
                color = if (done) Color(habit.color) else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun NoteCard(note: Note) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (note.color != 0) Color(note.color).copy(alpha = 0.18f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(note.title.ifBlank { "Без названия" }, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(
                note.body.ifBlank { "Пустая заметка" },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
        }
    }
}