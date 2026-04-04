package com.example.avito_intership.presentation.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.SettingsBrightness
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.avito_intership.core.theme.ThemeMode
import com.example.avito_intership.presentation.components.ScreenScaffold

@Composable
fun ProfileRoute(
    onNavigateBack: () -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                ProfileEffect.LoggedOut -> onLoggedOut()
                is ProfileEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    ScreenScaffold(
        title = "Профиль",
        snackbarHost = { SnackbarHost(snackbarHostState) },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Назад",
                )
            }
        },
    ) {
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    tonalElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(84.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PersonOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(40.dp),
                            )
                        }

                        Text(
                            text = uiState.displayName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                        )

                        uiState.email?.let {
                            InfoRow(label = "Email", value = it)
                        }
                        InfoRow(
                            label = "Телефон",
                            value = uiState.phone ?: "Не указан",
                        )
                        InfoRow(
                            label = "Токены",
                            value = uiState.tokens.toString(),
                        )
                    }
                }

                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    tonalElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = "Тема приложения",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            ThemeChip(
                                label = "Система",
                                selected = uiState.themeMode == ThemeMode.SYSTEM,
                                icon = Icons.Outlined.SettingsBrightness,
                                onClick = { viewModel.onAction(ProfileAction.ThemeSelected(ThemeMode.SYSTEM)) },
                            )
                            ThemeChip(
                                label = "Светлая",
                                selected = uiState.themeMode == ThemeMode.LIGHT,
                                icon = Icons.Outlined.LightMode,
                                onClick = { viewModel.onAction(ProfileAction.ThemeSelected(ThemeMode.LIGHT)) },
                            )
                            ThemeChip(
                                label = "Тёмная",
                                selected = uiState.themeMode == ThemeMode.DARK,
                                icon = Icons.Outlined.DarkMode,
                                onClick = { viewModel.onAction(ProfileAction.ThemeSelected(ThemeMode.DARK)) },
                            )
                        }
                    }
                }

                Button(
                    onClick = { viewModel.onAction(ProfileAction.LogoutClicked) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Logout,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                    Text(text = "Выйти из аккаунта")
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun ThemeChip(
    label: String,
    selected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
) {
    AssistChip(
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
            )
        },
        colors = if (selected) {
            AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                leadingIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        } else {
            AssistChipDefaults.assistChipColors()
        },
    )
}
