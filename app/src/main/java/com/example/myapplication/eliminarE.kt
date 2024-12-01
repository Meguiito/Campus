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
import androidx.compose.ui.graphics.Brush

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EliminarCanchaScreen(navController: NavController, isLoggedIn: Boolean, onLogout: () -> Unit, username: String, email: String) {
    var canchas by remember { mutableStateOf<List<CanchaResponse2>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Obtener la lista de canchas
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                isLoading = true
                canchas = RetrofitInstance.api.getAllcanchas() // Llamada al endpoint para obtener canchas
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Error al obtener canchas: ${e.localizedMessage}"
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
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF2559A8), // Color de inicio del gradiente
                                Color(0xFFFFFFFF)  // Color de fin (blanco)
                            )
                        )
                    )
            ) {


                // Barra superior
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .align(Alignment.TopCenter)
                        .background(Color(0xFF2559A8)), // Color de la barra superior
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
                    Icon(Icons.Default.Menu, contentDescription = "Abrir menú", tint = Color(0xFF2559A8)) // Ícono en el color de la paleta
                }

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF2559A8) // Indicador de carga en color de la paleta
                    )
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
                        canchas.forEach { cancha ->
                            // Tarjeta con la información de la cancha
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                shape = MaterialTheme.shapes.large,
                                elevation = CardDefaults.cardElevation(4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)), // Tarjeta en un color distintivo
                                border = BorderStroke(2.dp, Color(0xFF2559A8)) // Borde de la tarjeta
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(text = "Nombre: ${cancha.nombre}", color = Color.Black)
                                    Text(text = "Tipo: ${cancha.tipo}", color = Color.Black)

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Botón para eliminar la cancha
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                try {
                                                    isLoading = true
                                                    // Llamada para eliminar por nombre
                                                    RetrofitInstance.api.deleteEspacioByNombre(cancha.nombre)
                                                    // Recargar la lista de canchas tras la eliminación
                                                    canchas = RetrofitInstance.api.getAllcanchas()
                                                } catch (e: Exception) {
                                                    errorMessage = "Error al eliminar cancha: ${e.localizedMessage}"
                                                } finally {
                                                    isLoading = false
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2559A8)), // Botón en color de la paleta
                                        modifier = Modifier.align(Alignment.End)
                                    ) {
                                        Text(text = "Eliminar", color = Color.White)
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
                        .background(Color(0xFF000000)),
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
fun EliminarCanchaScreenPreview() {
    MyApplicationTheme {
        EliminarCanchaScreen(
            navController = rememberNavController(),
            isLoggedIn = true,
            onLogout = {},
            username = "",
            email = ""
        )
    }
}
