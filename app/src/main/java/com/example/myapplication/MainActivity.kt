package com.example.myapplication

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()

                // Estado de inicio de sesión
                var isLoggedIn by remember { mutableStateOf(false) }

                // Función de logout
                val onLogout = {
                    isLoggedIn = false
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }

                NavHost(navController = navController, startDestination = if (isLoggedIn) "mainScreen" else "login") {
                    composable("login") {
                        LoginForm(navController)
                        LaunchedEffect(navController) {
                            navController.currentBackStackEntryFlow.collect { backStackEntry ->
                                if (backStackEntry.destination.route == "mainScreen") {
                                    isLoggedIn = true
                                }
                            }
                        }
                    }
                    composable("register") { RegisterScreen(navController) }
                    composable("mainScreen") {
                        MainScreen(
                            navController = navController,
                            isLoggedIn = isLoggedIn,
                            onLogout = onLogout
                        )
                    }
                    composable("reserva") {
                        ReservaScreen(
                            navController = navController,
                            isLoggedIn = isLoggedIn,
                            onLogout = onLogout
                        )
                    }
                    // Añadir la nueva pantalla del calendario
                    composable("calendario") {
                        CalendarScreen(
                            navController = navController,
                            isLoggedIn = isLoggedIn,
                            onLogout = onLogout
                        )
                    }
                }
            }
        }
    }
}
