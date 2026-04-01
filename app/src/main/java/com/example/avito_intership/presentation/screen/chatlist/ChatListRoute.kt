package com.example.avito_intership.presentation.screen.chatlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.avito_intership.presentation.components.ScreenScaffold

@Composable
fun ChatListRoute(
    onOpenChat: () -> Unit,
    onOpenProfile: () -> Unit,
) {
    ScreenScaffold(title = "Chats") {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search chats") },
                enabled = false,
            )
            Text(
                text = "No chats yet",
                modifier = Modifier.padding(top = 8.dp),
            )
            Button(onClick = onOpenChat) {
                Text(text = "New chat")
            }
            TextButton(onClick = onOpenProfile) { Text(text = "Profile") }
            OutlinedButton(onClick = onOpenChat) { Text(text = "Open sample chat") }
        }
    }
}
