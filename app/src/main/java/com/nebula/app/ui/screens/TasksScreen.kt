package com.nebula.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.nebula.app.data.NebulaRepository
import com.nebula.app.data.TaskItem
import com.nebula.app.ui.animations.nebulaSpring
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(repository: NebulaRepository) {
    val tasks by repository.tasks.collectAsState(initial = emptyList())
    val scope = remember { CoroutineScope(Dispatchers.IO) }
    var showCreate by remember { mutableStateOf(false) }
    var filter by rememberSaveable { mutableStateOf("ALL") }

    val visible = when (filter) {
        "ACTIVE" -> tasks.filter { !it.done }
        "DONE" -> tasks.filter { it.done }
        else -> tasks
    }
    val activeCount = tasks.count { !it.done }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Задачи", style = MaterialTheme.typography.titleLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        Text(
                            if (tasks.isEmpty()) "Спланируй свой день"
                            else "Осталось: $activeCount из ${tasks.size}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            val fabRotation by animateFloatAsState(
                targetValue = if (showCreate) 135f else 0f,
                animationSpec = nebulaSpring,
                label = "fabRotation"
            )
            val fabScale by animateFloatAsState(
                targetValue = if (showCreate) 0.7f else 1f,
                animationSpec = nebulaSpring,
                label = "fabScale"
            )
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Новая задача",
                    modifier = Modifier
                        .rotate(fabRotation)
                        .graphicsLayer {
                            scaleX = fabScale
                            scaleY = fabScale
                        }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("ALL" to "Все", "ACTIVE" to "Активные", "DONE" to "Готово").forEach { (key, label) ->
                    FilterChip(
                        selected = filter == key,
                        onClick = { filter = key },
                        label = { Text(label) }
                    )
                }
            }

            if (visible.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("✅", fontSize = MaterialTheme.typography.displayMedium.fontSize)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        when {
                            tasks.isEmpty() -> "Пока нет задач"
                            filter == "DONE" -> "Выполненных задач нет"
                            else -> "Нет активных задач"
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Нажми + чтобы добавить задачу",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(visible.sortedBy { it.done }.sortedByDescending { it.important }, key = { it.id }) { task ->
                        TaskRow(
                            task = task,
                            onToggle = { scope.launch { repository.toggleTask(task.id) } },
                            onDelete = { scope.launch { repository.deleteTask(task.id) } },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }

    if (showCreate) {
        TaskCreateDialog(
            onDismiss = { showCreate = false },
            onCreate = { title, important ->
                scope.launch { repository.addTask(title.trim(), important) }
                showCreate = false
            }
        )
    }
}

@Composable
private fun TaskRow(task: TaskItem, onToggle: () -> Unit, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    val titleColor by animateColorAsState(
        targetValue = if (task.done) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
        label = "taskTitleColor"
    )
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.done,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = if (task.color != 0) Color(task.color) else MaterialTheme.colorScheme.primary
                )
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = if (task.done) TextDecoration.LineThrough else null,
                    color = titleColor
                )
                if (task.important) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Flag,
                            contentDescription = null,
                            tint = Color(0xFFE53935),
                            modifier = Modifier.height(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Важно",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFE53935)
                        )
                    }
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Удалить", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun TaskCreateDialog(
    onDismiss: () -> Unit,
    onCreate: (String, Boolean) -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var important by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новая задача") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Что нужно сделать?") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Flag, contentDescription = null, tint = Color(0xFFE53935))
                    Spacer(Modifier.width(8.dp))
                    Text("Важная задача", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                    Switch(checked = important, onCheckedChange = { important = it })
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (title.isNotBlank()) onCreate(title, important) },
                enabled = title.isNotBlank()
            ) {
                Text("Добавить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}