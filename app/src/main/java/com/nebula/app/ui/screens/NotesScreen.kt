package com.nebula.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nebula.app.data.NebulaRepository
import com.nebula.app.data.Note
import com.nebula.app.data.dateFormatter
import com.nebula.app.data.toLocalDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(repository: NebulaRepository) {
    val notes by repository.notes.collectAsState(initial = emptyList())
    val scope = remember { CoroutineScope(Dispatchers.IO) }
    var editingNote by remember { mutableStateOf<Note?>(null) }
    var showCreate by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Заметки", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("${notes.size} шт.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            val rotation by animateFloatAsState(
                targetValue = if (showCreate) 135f else 0f,
                label = "notesFabRotation"
            )
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Новая заметка",
                    modifier = Modifier.rotate(rotation)
                )
            }
        }
    ) { padding ->
        if (notes.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("📝", fontSize = MaterialTheme.typography.displayMedium.fontSize)
                Spacer(Modifier.height(8.dp))
                Text("Пока пусто", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Нажми + чтобы создать заметку",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notes.sortedByDescending { it.updatedAt }, key = { it.id }) { note ->
                    NoteListItem(
                        note = note,
                        onClick = { editingNote = note },
                        onDelete = { scope.launch { repository.deleteNote(note.id) } },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }

    if (showCreate) {
        NoteEditorDialog(
            title = "Новая заметка",
            initialTitle = "",
            initialBody = "",
            onDismiss = { showCreate = false },
            onSave = { t, b ->
                scope.launch { repository.addNote(t, b) }
                showCreate = false
            }
        )
    }

    editingNote?.let { note ->
        NoteEditorDialog(
            title = "Редактировать",
            initialTitle = note.title,
            initialBody = note.body,
            onDismiss = { editingNote = null },
            onSave = { t, b ->
                scope.launch { repository.updateNote(note.copy(title = t, body = b)) }
                editingNote = null
            }
        )
    }
}

@Composable
fun NoteListItem(note: Note, onClick: () -> Unit, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (note.color != 0) Color(note.color).copy(alpha = 0.15f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    note.title.ifBlank { "Без названия" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (note.body.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        note.body,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    note.updatedAt.toLocalDateTime().format(dateFormatter),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Удалить", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun NoteEditorDialog(
    title: String,
    initialTitle: String,
    initialBody: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var noteTitle by rememberSaveable { mutableStateOf(initialTitle) }
    var noteBody by rememberSaveable { mutableStateOf(initialBody) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = noteTitle,
                    onValueChange = { noteTitle = it },
                    label = { Text("Заголовок") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = noteBody,
                    onValueChange = { noteBody = it },
                    label = { Text("Текст заметки") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(noteTitle.trim(), noteBody.trim()) }) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}