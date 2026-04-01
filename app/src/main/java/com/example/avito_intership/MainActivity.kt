package com.example.avito_intership

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.avito_intership.core.theme.ThemeMode
import com.example.avito_intership.presentation.navigation.AppNavHost
import com.example.avito_intership.presentation.root.RootViewModel
import com.example.avito_intership.presentation.theme.AiAssistantTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AiAssistantAppContent()
        }
    }
}

@Composable
private fun AiAssistantAppContent(
    viewModel: RootViewModel = hiltViewModel(),
) {
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    AiAssistantTheme(themeMode = themeMode) {
        AppNavHost()
    }
}

@Preview(showBackground = true)
@Composable
private fun AiAssistantAppContentPreview() {
    AiAssistantTheme(themeMode = ThemeMode.SYSTEM) {
        AppNavHost()
    }
}
