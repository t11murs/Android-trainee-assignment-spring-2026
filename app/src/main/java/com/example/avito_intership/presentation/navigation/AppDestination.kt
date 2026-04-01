package com.example.avito_intership.presentation.navigation

sealed class AppDestination(val route: String, val title: String) {
    data object Splash : AppDestination("splash", "Splash")
    data object Auth : AppDestination("auth", "Sign in")
    data object Register : AppDestination("register", "Create account")
    data object ChatList : AppDestination("chat_list", "Chats")
    data object Chat : AppDestination("chat", "Chat")
    data object Profile : AppDestination("profile", "Profile")
}
