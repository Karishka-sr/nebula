package com.nebula.app.data

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class Note(
    val id: Long,
    val title: String,
    val body: String,
    val createdAt: Long,
    val updatedAt: Long,
    val color: Int = 0
)

@Serializable
data class Habit(
    val id: Long,
    val name: String,
    val icon: String,
    val color: Int,
    val createdAt: Long,
    val completionDates: Set<String> = emptySet()
)

@Serializable
data class HabitStorage(
    val id: Long,
    val name: String,
    val icon: String,
    val color: Int,
    val createdAt: Long,
    val completionDates: List<String> = emptyList()
)

@Serializable
data class PomodoroSession(
    val id: Long,
    val type: String,           // FOCUS / SHORT_BREAK / LONG_BREAK
    val durationMinutes: Int,
    val completedAt: Long,
    val taskLabel: String = ""
)

@Serializable
data class TaskItem(
    val id: Long,
    val title: String,
    val done: Boolean = false,
    val important: Boolean = false,
    val color: Int = 0,
    val createdAt: Long = 0L
)

@Serializable
data class WaterRecord(
    val dateKey: String,
    val glasses: Int = 0
)

@Serializable
data class MoodRecord(
    val dateKey: String,
    val mood: String = "NEUTRAL"
)

@Serializable
data class AppSettings(
    val userName: String = "Мастер времени",
    val dailyFocusGoal: Int = 8,
    val themeMode: String = "SYSTEM",  // LIGHT / DARK / SYSTEM
    val dynamicColor: Boolean = true,  // Material You (динимический цвет)
    val fontFamily: String = "default" // default / inter / montserrat / playfair / comfortaa / space
)

data class DayStreak(
    val date: LocalDate,
    val count: Int
)

val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
val dateKeyFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

fun Long.toLocalDateTime(): LocalDateTime =
    LocalDateTime.ofEpochSecond(
        this / 1000,
        0,
        java.time.ZoneOffset.systemDefault().rules.getOffset(java.time.Instant.ofEpochMilli(this))
    )

fun dayKey(date: LocalDate): String = date.format(dateKeyFormatter)