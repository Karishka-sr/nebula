package com.nebula.app.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nebula.app.data.Habit
import com.nebula.app.data.NebulaRepository
import com.nebula.app.data.dayKey
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

private val HabitColors = listOf(
    0xFF7C4DFF.toInt(), 0xFFFF80AB.toInt(), 0xFF00BFA5.toInt(), 0xFFFF6E40.toInt(),
    0xFF40C4FF.toInt(), 0xFFFFD740.toInt(), 0xFF69F0AE.toInt(), 0xFFB388FF.toInt()
)

private val HabitIcons = listOf("💧", "🏃", "📖", "🧘", "😴", "🥗", "💪", "✍️", "🎯", "🌅", "🦷", "🚭")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsScreen(repository: NebulaRepository) {
    val habits by repository.habits.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var showCreate by remember { mutableStateOf(false) }

    val today = LocalDate.now()
    val todayKey = dayKey(today)
    val todayCount = habits.count { todayKey in it.completionDates }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Привычки", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(
                            if (habits.isEmpty()) "Добавь свои привычки"
                            else "Выполнено сегодня: $todayCount из ${habits.size}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Новая привычка")
            }
        }
    ) { padding ->
        if (habits.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("🎯", fontSize = MaterialTheme.typography.displayMedium.fontSize)
                Spacer(Modifier.height(8.dp))
                Text("Нет привычек", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Добавь первую привычку кнопкой +",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(habits, key = { it.id }) { habit ->
                    HabitCard(
                        habit = habit,
                        todayKey = todayKey,
                        weekDates = (6L downTo 0L).map { today.minusDays(it) },
                        onToggle = { scope.launch { repository.toggleHabit(habit.id, todayKey) } },
                        onDelete = { scope.launch { repository.deleteHabit(habit.id) } }
                    )
                }
            }
        }
    }

    if (showCreate) {
        HabitCreateDialog(
            onDismiss = { showCreate = false },
            onCreate = { name, icon, color ->
                scope.launch { repository.addHabit(name, icon, color) }
                showCreate = false
            }
        )
    }
}

@Composable
fun HabitCard(
    habit: Habit,
    todayKey: String,
    weekDates: List<LocalDate>,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val color = Color(habit.color)
    val streak = currentStreak(habit, LocalDate.now())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(color.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(habit.icon, fontSize = 22.sp)
                }
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(habit.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, maxLines = 2)
                    Text("Серия: $streak 🔥", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Filled.Delete, contentDescription = "Удалить", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(Modifier.height(14.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                weekDates.forEach { date ->
                    val done = dayKey(date) in habit.completionDates
                    val isToday = date == LocalDate.now()
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(
                                if (done) color.copy(alpha = if (isToday) 1f else 0.7f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            date.dayOfMonth.toString(),
                            fontSize = 11.sp,
                            color = if (done) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

fun currentStreak(habit: Habit, date: LocalDate): Int {
    var streak = 0
    var day = date
    if (dayKey(day) !in habit.completionDates && streak == 0) {
        // don't break streak if today not yet done; check yesterday
        day = date.minusDays(1)
    }
    while (dayKey(day) in habit.completionDates) {
        streak++
        day = day.minusDays(1)
    }
    return streak
}

@Composable
fun HabitCreateDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, Int) -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var icon by remember { mutableStateOf(HabitIcons.first()) }
    var color by remember { mutableIntStateOf(HabitColors.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новая привычка") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Название привычки") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                Text("Иконка", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(HabitIcons) { i ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    if (i == icon) Color(color).copy(alpha = 0.3f) else Color.Transparent,
                                    CircleShape
                                )
                                .clickable { icon = i },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(i, fontSize = 20.sp)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text("Цвет", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(HabitColors) { c ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(c), CircleShape)
                                .clickable { color = c }
                                .then(
                                    if (c == color) Modifier.padding(4.dp) else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (c == color) {
                                Box(
                                    Modifier
                                        .size(10.dp)
                                        .background(Color.White, CircleShape)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onCreate(name.trim(), icon, color) },
                enabled = name.isNotBlank()
            ) {
                Text("Создать")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}