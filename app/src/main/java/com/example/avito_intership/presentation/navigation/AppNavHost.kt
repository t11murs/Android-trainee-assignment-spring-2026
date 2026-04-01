package com.example.avito_intership.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.avito_intership.presentation.screen.auth.AuthRoute
import com.example.avito_intership.presentation.screen.auth.RegisterRoute
import com.example.avito_intership.presentation.screen.chat.ChatRoute
import com.example.avito_intership.presentation.screen.chatlist.ChatListRoute
import com.example.avito_intership.presentation.screen.profile.ProfileRoute
import com.example.avito_intership.presentation.screen.splash.SplashRoute

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppDestination.Splash.route,
        modifier = modifier,
    ) {
        composable(AppDestination.Splash.route) {
            SplashRoute(
                onOpenAuth = { navController.navigate(AppDestination.Auth.route) },
                onOpenChatList = { navController.navigate(AppDestination.ChatList.route) },
            )
        }
        composable(AppDestination.Auth.route) {
            AuthRoute(
                onOpenRegister = { navController.navigate(AppDestination.Register.route) },
                onOpenChatList = {
                    navController.navigate(AppDestination.ChatList.route) {
                        popUpTo(AppDestination.Splash.route) { inclusive = true }
                    }
                },
            )
        }
        composable(AppDestination.Register.route) {
            RegisterRoute(
                onNavigateBack = { navController.popBackStack() },
                onRegistered = {
                    navController.navigate(AppDestination.ChatList.route) {
                        popUpTo(AppDestination.Auth.route) { inclusive = true }
                    }
                },
            )
        }
        composable(AppDestination.ChatList.route) {
            ChatListRoute(
                onOpenChat = { navController.navigate(AppDestination.Chat.route) },
                onOpenProfile = { navController.navigate(AppDestination.Profile.route) },
            )
        }
        composable(AppDestination.Chat.route) {
            ChatRoute(onNavigateBack = { navController.popBackStack() })
        }
        composable(AppDestination.Profile.route) {
            ProfileRoute(onNavigateBack = { navController.popBackStack() })
        }
    }
}
