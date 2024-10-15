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

                // Variables para almacenar información del usuario
                var username by remember { mutableStateOf("") }
                var email by remember { mutableStateOf("") }
                var rut by remember { mutableStateOf("") }

                NavHost(navController = navController, startDestination = if (isLoggedIn) "mainScreen" else "login") {
                    composable("login") {
                        LoginForm(navController) { user, userEmail, userRut ->
                            isLoggedIn = true
                            username = user
                            email = userEmail
                            rut = userRut // Asegúrate de que esta variable reciba el RUT
                            navController.navigate("mainScreen")
                        }
                    }
                    composable("register") { RegisterScreen(navController) }
                    composable("mainScreen") {
                        MainScreen(
                            navController = navController,
                            isLoggedIn = isLoggedIn,
                            onLogout = onLogout,
                            username = username,
                            email = email,
                            rut = rut
                        )
                    }
                    composable("reserva/{mes}/{dia}") { backStackEntry ->
                        val mes = backStackEntry.arguments?.getString("mes") ?: "1"
                        val dia = backStackEntry.arguments?.getString("dia") ?: "1"
                        ReservaScreen(
                            navController = navController,
                            isLoggedIn = isLoggedIn,
                            onLogout = onLogout,
                            mesSeleccionado = mes,
                            diaSeleccionado = dia
                        )
                    }
                    composable("calendario") {
                        CalendarScreen(
                            navController = navController,
                            isLoggedIn = isLoggedIn,
                            onLogout = onLogout
                        )
                    }
                    // Nueva pantalla para el perfil del usuario
                    composable("perfil/{username}/{email}/{rut}") { backStackEntry ->
                        val username = backStackEntry.arguments?.getString("username") ?: ""
                        val email = backStackEntry.arguments?.getString("email") ?: ""
                        val rut = backStackEntry.arguments?.getString("rut") ?: ""
                        PerfilScreen(
                            username = username,
                            email = email,
                            rut = rut,
                            isLoggedIn = isLoggedIn,
                            onLogout = onLogout,
                            navController = navController
                        )
                    }
                    composable("editarReserva") {
                        EditarReservaScreen(navController = navController, rut = rut,isLoggedIn = isLoggedIn,
                            onLogout = onLogout)
                    }
                }
            }
        }
    }
}
