package com.nebula.app.ui.animations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.delay

/** Пружина с приятным «отскоком» для UI-элементов. */
val nebulaSpring = spring<Float>(dampingRatio = 0.5f, stiffness = 550f)

/** Каскадное появление: растворение + мягкий сдвиг снизу. */
fun cascadeEnter(delayMillis: Int = 0): EnterTransition =
    fadeIn(tween(420, delayMillis, FastOutSlowInEasing)) +
        slideInVertically(tween(420, delayMillis, FastOutSlowInEasing)) { it / 4 }

fun cascadeExit(): ExitTransition =
    fadeOut(tween(240)) + slideOutVertically(tween(240)) { -it / 8 }

/** Возвращает true после задержки — удобно для каскадных появлений. */
@Composable
fun rememberRevealed(delayMillis: Long = 0): Boolean {
    var revealed by remember { mutableStateOf(delayMillis == 0L) }
    LaunchedEffect(Unit) {
        if (delayMillis > 0) delay(delayMillis)
        revealed = true
    }
    return revealed
}

/** Обёртка над AnimatedVisibility с каскадным появлением по умолчанию. */
@Composable
fun RevealIn(
    visible: Boolean,
    modifier: Modifier = Modifier,
    enter: EnterTransition = cascadeEnter(),
    content: @Composable () -> Unit
) {
    AnimatedVisibility(visible = visible, modifier = modifier, enter = enter, exit = cascadeExit()) { content() }
}

/** Число, которое эффектно «досчитывается» до целевого значения. */
@Composable
fun AnimatedNumber(
    target: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.headlineMedium,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = Color.Unspecified,
    durationMillis: Int = 800
) {
    var revealed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { revealed = true }
    val value by animateIntAsState(
        targetValue = if (revealed) target else 0,
        animationSpec = tween(durationMillis, easing = FastOutSlowInEasing),
        label = "AnimatedNumber"
    )
    Text(text = value.toString(), modifier = modifier, style = style, fontWeight = fontWeight, color = color)
}