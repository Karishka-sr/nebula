package com.nebula.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Random

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "nebula_data")

class NebulaRepository(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val notesKey = stringPreferencesKey("notes")
    private val habitsKey = stringPreferencesKey("habits")
    private val sessionsKey = stringPreferencesKey("sessions")
    private val settingsKey = stringPreferencesKey("settings")

    // ---------- Notes ----------
    val notes: Flow<List<Note>> = context.dataStore.data.map { prefs ->
        prefs[notesKey]?.let { runCatching { json.decodeFromString<List<Note>>(it) }.getOrNull() } ?: emptyList()
    }

    suspend fun addNote(title: String, body: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[notesKey]?.let { runCatching { json.decodeFromString<List<Note>>(it) }.getOrNull() } ?: emptyList()
            val now = System.currentTimeMillis()
            val newNote = Note(
                id = now,
                title = title,
                body = body,
                createdAt = now,
                updatedAt = now,
                color = NoteColors.random()
            )
            prefs[notesKey] = json.encodeToString(current + newNote)
        }
    }

    suspend fun updateNote(note: Note) {
        context.dataStore.edit { prefs ->
            val current = prefs[notesKey]?.let { runCatching { json.decodeFromString<List<Note>>(it) }.getOrNull() } ?: emptyList()
            val updated = current.map { if (it.id == note.id) note.copy(updatedAt = System.currentTimeMillis()) else it }
            prefs[notesKey] = json.encodeToString(updated)
        }
    }

    suspend fun deleteNote(id: Long) {
        context.dataStore.edit { prefs ->
            val current = prefs[notesKey]?.let { runCatching { json.decodeFromString<List<Note>>(it) }.getOrNull() } ?: emptyList()
            prefs[notesKey] = json.encodeToString(current.filter { it.id != id })
        }
    }

    // ---------- Habits ----------
    val habits: Flow<List<Habit>> = context.dataStore.data.map { prefs ->
        prefs[habitsKey]?.let { runCatching { json.decodeFromString<List<HabitStorage>>(it) }.getOrNull() }
            ?.map { h -> Habit(h.id, h.name, h.icon, h.color, h.createdAt, h.completionDates.toSet()) }
            ?: emptyList()
    }

    suspend fun addHabit(name: String, icon: String, color: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[habitsKey]?.let { runCatching { json.decodeFromString<List<HabitStorage>>(it) }.getOrNull() } ?: emptyList()
            val now = System.currentTimeMillis()
            val newHabit = HabitStorage(id = now, name = name, icon = icon, color = color, createdAt = now)
            prefs[habitsKey] = json.encodeToString(current + newHabit)
        }
    }

    suspend fun toggleHabit(id: Long, dateKey: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[habitsKey]?.let { runCatching { json.decodeFromString<List<HabitStorage>>(it) }.getOrNull() } ?: emptyList()
            val updated = current.map { h ->
                if (h.id == id) {
                    val dates = h.completionDates.toMutableList()
                    if (dateKey in dates) dates.remove(dateKey) else dates.add(dateKey)
                    h.copy(completionDates = dates.sorted())
                } else h
            }
            prefs[habitsKey] = json.encodeToString(updated)
        }
    }

    suspend fun deleteHabit(id: Long) {
        context.dataStore.edit { prefs ->
            val current = prefs[habitsKey]?.let { runCatching { json.decodeFromString<List<HabitStorage>>(it) }.getOrNull() } ?: emptyList()
            prefs[habitsKey] = json.encodeToString(current.filter { it.id != id })
        }
    }

    // ---------- Pomodoro sessions ----------
    val sessions: Flow<List<PomodoroSession>> = context.dataStore.data.map { prefs ->
        prefs[sessionsKey]?.let { runCatching { json.decodeFromString<List<PomodoroSession>>(it) }.getOrNull() } ?: emptyList()
    }

    suspend fun addSession(session: PomodoroSession) {
        context.dataStore.edit { prefs ->
            val current = prefs[sessionsKey]?.let { runCatching { json.decodeFromString<List<PomodoroSession>>(it) }.getOrNull() } ?: emptyList()
            prefs[sessionsKey] = json.encodeToString(current + session)
        }
    }

    // ---------- Settings ----------
    val settings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        prefs[settingsKey]?.let { runCatching { json.decodeFromString<AppSettings>(it) }.getOrNull() } ?: AppSettings()
    }

    suspend fun saveSettings(settings: AppSettings) {
        context.dataStore.edit { prefs ->
            prefs[settingsKey] = json.encodeToString(AppSettings.serializer(), settings)
        }
    }
}

object NoteColors {
    val all = listOf(0xFF8E24AA.toInt(), 0xFF3949AB.toInt(), 0xFF00897B.toInt(), 0xFFF4511E.toInt())
    fun random(): Int = all[Random().nextInt(all.size)]
}