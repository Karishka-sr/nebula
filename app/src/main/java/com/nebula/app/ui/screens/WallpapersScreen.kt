package com.nebula.app.ui.screens

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val wallpaperCount = 13

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallpapersScreen() {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Обои Nebula", style = MaterialTheme.typography.titleLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        Text("Нажми, чтобы установить на рабочий стол", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items((1..wallpaperCount).toList()) { index ->
                WallpaperCard(
                    index = index,
                    onClick = {
                        setWallpaper(context, index) {
                            scope.launch { snackbarHostState.showSnackbar("Обои установлены 🌌") }
                        }
                    },
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}

@Composable
private fun WallpaperCard(index: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }
    LaunchedEffect(index) {
        bitmap = withContext(Dispatchers.IO) {
            decodeWallpaper(context, index, maxSide = 720)?.asImageBitmap()
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(9f / 16f)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Crossfade(targetState = bitmap, animationSpec = tween(450), label = "wallpaperLoad") { bmp ->
            if (bmp != null) {
                Image(
                    bitmap = bmp,
                    contentDescription = "Обои $index",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

private fun decodeWallpaper(context: Context, index: Int, maxSide: Int): Bitmap? {
    return try {
        context.assets.open("wallpapers/wallpaper_$index.png").use { stream ->
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeStream(stream, null, bounds)
            var sample = 1
            val maxDim = maxOf(bounds.outWidth, bounds.outHeight)
            while (maxDim / sample > maxSide) sample *= 2
            context.assets.open("wallpapers/wallpaper_$index.png").use { stream2 ->
                BitmapFactory.decodeStream(stream2, null, BitmapFactory.Options().apply { inSampleSize = sample })
            }
        }
    } catch (e: Exception) {
        null
    }
}

private fun setWallpaper(context: Context, index: Int, onDone: () -> Unit) {
    Thread {
        try {
            val bitmap = decodeWallpaper(context, index, maxSide = 2880) ?: return@Thread
            val wallpaperManager = WallpaperManager.getInstance(context.applicationContext)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                wallpaperManager.setBitmap(bitmap)
            } else {
                @Suppress("DEPRECATION")
                wallpaperManager.setBitmap(bitmap)
            }
            onDone()
        } catch (_: Exception) {
        }
    }.start()
}