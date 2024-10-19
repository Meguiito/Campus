package com.example.myapplication

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EliminarUsuarioScreen(navController: NavController, isLoggedIn: Boolean, onLogout: () -> Unit, username: String, email: String) {
    var usuarios by remember { mutableStateOf<List<UsuarioResponse2>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Obtener la lista de usuarios
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                isLoading = true
                usuarios = RetrofitInstance.api.getAllUsers() // Llamada al endpoint para obtener usuarios
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Error al obtener usuarios: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    // Lateral Drawer
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Menú",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                NavigationDrawerItem(
                    label = { Text("Inicio") },
                    selected = false,
                    onClick = {
                        navController.navigate("adminScreen")
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Perfil") },
                    selected = false,
                    onClick = {
                        navController.navigate("perfil/$username/$email")
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Cerrar sesión") },
                    selected = false,
                    onClick = {
                        onLogout()
                        scope.launch { drawerState.close() }
                    }
                )
            }
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.uctinformatica),
                    contentDescription = "Fondo",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 60.dp, bottom = 50.dp)
                        .align(Alignment.TopStart),
                    contentScale = ContentScale.FillHeight
                )

                // Barra superior
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .align(Alignment.TopCenter)
                        .background(Color(0xFFFCC40A)),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(115.dp)
                            .offset(x = (-5).dp)
                            .padding(start = 0.dp, top = 10.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                IconButton(
                    onClick = { scope.launch { drawerState.open() } },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                }

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (errorMessage != null) {
                    Text(errorMessage ?: "Error desconocido", color = MaterialTheme.colorScheme.error)
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp, top = 80.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        usuarios.forEach { usuario ->
                            // Tarjeta con la información del usuario
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                shape = MaterialTheme.shapes.large,
                                elevation = CardDefaults.cardElevation(4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFCC40A)),
                                border = BorderStroke(2.dp, Color.White)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(text = "Username: ${usuario.id}", color = Color.Black)
                                    Text(text = "Username: ${usuario.username}", color = Color.Black)
                                    Text(text = "Email: ${usuario.email}", color = Color.Black)

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Botón para eliminar el usuario
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                try {
                                                    isLoading = true
                                                    // Llamada para eliminar por ID
                                                    RetrofitInstance.api.deleteUserById(usuario.id)
                                                    navController.navigate("eliminarUsuario")

                                                } catch (e: Exception) {
                                                    errorMessage = "Error al eliminar usuario: ${e.localizedMessage}"
                                                } finally {
                                                    isLoading = false
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                        modifier = Modifier.align(Alignment.End)
                                    ) {
                                        Text(text = "Eliminar")
                                    }
                                }
                            }
                        }
                    }
                }

                // Barra inferior
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .align(Alignment.BottomCenter)
                        .background(Color(0xFF0F0147)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "© 2024 Universidad Católica de Temuco",
                        color = Color.White,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun EliminarUsuarioScreenPreview() {
    MyApplicationTheme {
        EliminarUsuarioScreen(
            navController = rememberNavController(),
            isLoggedIn = true,
            onLogout = {},
            username = "",
            email = ""
        )
    }
}
