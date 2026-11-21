package com.nebula.app.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material.icons.filled.SmokeFree
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector

data class NebulaMood(
    val key: String,
    val label: String,
    val icon: ImageVector
)

/**
 * Каталог настоящих векторных Material-иконок.
 * Заменяет прежние «иконки-эмодзи»: привычки и настроение теперь хранят
 * символьные ключи и рендерятся Material Icons.
 */
object NebulaIcons {

    // ---------- Привычки ----------
    val habitEntries: List<Pair<String, ImageVector>> = listOf(
        "water" to Icons.Filled.WaterDrop,
        "run" to Icons.Filled.DirectionsRun,
        "book" to Icons.Filled.MenuBook,
        "zen" to Icons.Filled.SelfImprovement,
        "sleep" to Icons.Filled.Bedtime,
        "food" to Icons.Filled.Restaurant,
        "gym" to Icons.Filled.FitnessCenter,
        "write" to Icons.Filled.EditNote,
        "target" to Icons.Filled.Adjust,
        "sun" to Icons.Filled.WbSunny,
        "nosmoke" to Icons.Filled.SmokeFree
    )

    // Совместимость со старыми данными, где иконкой была эмодзи-строка
    private val legacyHabitIcons: Map<String, ImageVector> = mapOf(
        "💧" to Icons.Filled.WaterDrop,
        "🏃" to Icons.Filled.DirectionsRun,
        "📖" to Icons.Filled.MenuBook,
        "🧘" to Icons.Filled.SelfImprovement,
        "😴" to Icons.Filled.Bedtime,
        "🥗" to Icons.Filled.Restaurant,
        "💪" to Icons.Filled.FitnessCenter,
        "✍️" to Icons.Filled.EditNote,
        "🎯" to Icons.Filled.Adjust,
        "🌅" to Icons.Filled.WbSunny,
        "🚭" to Icons.Filled.SmokeFree,
        "🦷" to Icons.Filled.CheckCircle
    )

    fun habitIcon(key: String): ImageVector =
        habitEntries.firstOrNull { it.first == key }?.second
            ?: legacyHabitIcons[key]
            ?: Icons.Filled.CheckCircle

    // ---------- Настроение ----------
    val moods: List<NebulaMood> = listOf(
        NebulaMood("BAD", "Плохо", Icons.Filled.SentimentVeryDissatisfied),
        NebulaMood("MEH", "Грустно", Icons.Filled.SentimentDissatisfied),
        NebulaMood("NEUTRAL", "Норм", Icons.Filled.SentimentNeutral),
        NebulaMood("GOOD", "Хорошо", Icons.Filled.SentimentSatisfied),
        NebulaMood("GREAT", "Отлично", Icons.Filled.SentimentVerySatisfied)
    )

    private val legacyMoodKeys: Map<String, String> = mapOf(
        "😞" to "BAD", "😕" to "MEH", "😐" to "NEUTRAL", "🙂" to "GOOD", "😄" to "GREAT"
    )

    /** Приводит сохранённое значение настроения к ключу каталога. */
    fun normalizeMood(stored: String?): String {
        val s = stored.orEmpty().trim().ifEmpty { return "NEUTRAL" }
        legacyMoodKeys[s]?.let { return it }
        if (moods.any { it.key == s }) return s
        return "NEUTRAL"
    }

    fun moodIcon(stored: String?): ImageVector =
        moods.first { it.key == normalizeMood(stored) }.icon

    fun moodLabel(stored: String?): String =
        moods.first { it.key == normalizeMood(stored) }.label
}