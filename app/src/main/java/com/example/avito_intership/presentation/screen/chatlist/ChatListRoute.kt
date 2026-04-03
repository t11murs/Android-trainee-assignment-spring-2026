package com.example.avito_intership.presentation.screen.chatlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.avito_intership.presentation.components.ScreenScaffold

@Composable
fun ChatListRoute(
    onOpenChat: (String) -> Unit,
    onOpenProfile: () -> Unit,
    onLoggedOut: () -> Unit,
    contentViewModel: ChatListViewModel = hiltViewModel(),
    sessionViewModel: ChatListSessionViewModel = hiltViewModel(),
) {
    val uiState by contentViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val chats = contentViewModel.chats.collectAsLazyPagingItems()

    LaunchedEffect(Unit) {
        contentViewModel.effects.collect { effect ->
            when (effect) {
                is ChatListEffect.OpenChat -> onOpenChat(effect.chatId)
                is ChatListEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    LaunchedEffect(Unit) {
        sessionViewModel.effects.collect { effect ->
            when (effect) {
                ChatListSessionEffect.LoggedOut -> onLoggedOut()
                is ChatListSessionEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    ScreenScaffold(
        title = "Чаты",
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBarActions = {
            TextButton(onClick = sessionViewModel::logout) {
                Text(text = "Выйти")
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = uiState.query,
                onValueChange = contentViewModel::onQueryChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Поиск чатов") },
                singleLine = true,
            )

            FloatingActionButton(
                onClick = contentViewModel::createChat,
                modifier = Modifier.align(Alignment.End),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Новый чат",
                )
            }

            when {
                chats.loadState.refresh is LoadState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                chats.itemCount == 0 -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = if (uiState.query.isBlank()) "Пока нет чатов" else "Ничего не найдено",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 12.dp),
                        )
                        Text(
                            text = if (uiState.query.isBlank()) {
                                "Создайте первый чат кнопкой сверху"
                            } else {
                                "Попробуйте изменить запрос"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(
                            count = chats.itemCount,
                            key = chats.itemKey { it.id },
                        ) { index ->
                            val item = chats[index] ?: return@items
                            ChatCard(
                                item = item,
                                onClick = { contentViewModel.openChat(item.id) },
                            )
                        }
                    }
                }
            }

            TextButton(
                onClick = onOpenProfile,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Icon(
                    imageVector = Icons.Outlined.PersonOutline,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp),
                )
                Text(text = "Профиль")
            }
        }
    }
}

@Composable
private fun ChatCard(
    item: ChatListItemUi,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
