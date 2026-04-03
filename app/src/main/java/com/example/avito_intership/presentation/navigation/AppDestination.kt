package com.example.avito_intership.presentation.navigation

sealed class AppDestination(val route: String, val title: String) {
    data object Splash : AppDestination("splash", "Загрузка")
    data object Auth : AppDestination("auth", "Вход")
    data object Register : AppDestination("register", "Регистрация")
    data object ChatList : AppDestination("chat_list", "Чаты")
    data object Chat : AppDestination("chat/{chatId}", "Чат") {
        fun createRoute(chatId: String): String = "chat/$chatId"
    }
    data object Profile : AppDestination("profile", "Профиль")
}
