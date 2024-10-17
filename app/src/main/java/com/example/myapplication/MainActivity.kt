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
                var isAdmin by remember { mutableStateOf(false) }

                // Función de logout
                val onLogout = {
                    isLoggedIn = false
                    isAdmin = false
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }

                // Variables para almacenar información del usuario
                var username by remember { mutableStateOf("") }
                var email by remember { mutableStateOf("") }
                var rut by remember { mutableStateOf("") }

                // Definir el destino de inicio según el estado de sesión y tipo de usuario
                NavHost(
                    navController = navController,
                    startDestination = if (isLoggedIn) {
                        if (isAdmin) "adminScreen" else "mainScreen"
                    } else "login"
                ) {
                    // Pantalla de login
                    composable("login") {
                        LoginForm(navController) { user, userEmail, userRut ->
                            isLoggedIn = true
                            username = user
                            email = userEmail
                            rut = userRut

                            // Verificar si el usuario es administrador
                            if (userEmail == "admin@uct.cl") {
                                isAdmin = true
                                navController.navigate("adminScreen")
                            } else {
                                isAdmin = false
                                navController.navigate("mainScreen")
                            }
                        }
                    }

                    // Pantalla de registro
                    composable("register") { RegisterScreen(navController) }

                    // Pantalla principal de usuario regular
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

                    // Pantalla de reservas
                    composable("reserva/{mes}/{dia}") { backStackEntry ->
                        val mes = backStackEntry.arguments?.getString("mes") ?: "1"
                        val dia = backStackEntry.arguments?.getString("dia") ?: "1"
                        ReservaScreen(
                            navController = navController,
                            isLoggedIn = isLoggedIn,
                            onLogout = onLogout,
                            mesSeleccionado = mes,
                            diaSeleccionado = dia,
                            username = username,
                            email = email,
                            rut = rut
                        )
                    }

                    // Pantalla de calendario
                    composable("calendario") {
                        CalendarScreen(
                            navController = navController,
                            isLoggedIn = isLoggedIn,
                            onLogout = onLogout,
                            username = username,
                            email = email,
                            rut = rut
                        )
                    }

                    // Pantalla de administrador
                    composable("adminScreen") {
                        AdminScreen(
                            navController = navController,
                            username = username,
                            email = email,
                            rut = rut,
                            onLogout = onLogout
                        )
                    }

                    // Pantalla de perfil de usuario
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

                    // Pantalla de edición de reserva
                    composable("editarReserva") {
                        EditarReservaScreen(
                            navController = navController,
                            rut = rut,
                            isLoggedIn = isLoggedIn,
                            onLogout = onLogout,
                            email = email,
                            username = username
                        )
                    }

                    // Pantalla de eliminación de reserva
                    composable("eliminarReserva") {
                        EliminarReservaScreen(
                            navController = navController,
                            rut = rut,
                            isLoggedIn = isLoggedIn,
                            onLogout = onLogout,
                            username=username,
                            email=email
                        )
                    }
                }
            }
        }
    }
}
