package com.nebula.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.nebula.app.data.AppSettings
import com.nebula.app.ui.navigation.NebulaNavHost
import com.nebula.app.ui.theme.NebulaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = (application as NebulaApplication).repository
        enableEdgeToEdge()
        setContent {
            val settings by repository.settings.collectAsState(initial = AppSettings())
            val darkTheme = when (settings.themeMode) {
                "LIGHT" -> false
                "DARK" -> true
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }
            NebulaTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold { innerPadding ->
                        NebulaNavHost(
                            modifier = Modifier.padding(innerPadding),
                            repository = repository
                        )
                    }
                }
            }
        }
    }
}