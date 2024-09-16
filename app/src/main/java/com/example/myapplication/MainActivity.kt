package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // Create a NavController
                val navController = rememberNavController()

                // State to manage login status
                var isLoggedIn by remember { mutableStateOf(false) }

                // Set up the NavHost
                NavHost(navController = navController, startDestination = if (isLoggedIn) "mainScreen" else "login") {
                    composable("login") {
                        LoginForm(navController)
                        // Update the login status on successful login
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
                            onLogout = {
                                isLoggedIn = false
                                navController.navigate("login") {
                                    // Clear the back stack to prevent returning to the previous screen
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("reserva") { ReservaScreen(navController) }
                }
            }
        }
    }
}
