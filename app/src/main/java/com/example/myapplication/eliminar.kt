package com.example.myapplication

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EliminarReservaScreen(navController: NavController, rut: String, isLoggedIn: Boolean, onLogout: () -> Unit) {
    var reservas by remember { mutableStateOf<List<ReservaResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Obtener las reservas basadas en el rut del usuario
    LaunchedEffect(rut) {
        scope.launch {
            try {
                isLoading = true
                reservas = RetrofitInstance.api.getReservasByRut(rut)
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Error al obtener reservas: ${e.localizedMessage}"
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
                        navController.navigate("mainScreen")
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
                        reservas.forEach { reserva ->
                            // Tarjeta con la información de la reserva
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.Gray)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(text = "Nombre: ${reserva.nombre}", color = Color.White)
                                    Text(text = "Carrera: ${reserva.carrera}", color = Color.White)
                                    Text(text = "Cancha: ${reserva.cancha}", color = Color.White)
                                    Text(text = "Duración: ${reserva.duracion}", color = Color.White)
                                    Text(text = "Día: ${reserva.dia}", color = Color.White)
                                    Text(text = "Mes: ${reserva.mes}", color = Color.White)

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Botón para eliminar la reserva
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                try {
                                                    RetrofitInstance.api.deleteReserva(reserva.id)
                                                    // Eliminar la reserva de la lista local después de eliminarla en el servidor
                                                    reservas = reservas.filter { it.id != reserva.id }
                                                } catch (e: Exception) {
                                                    errorMessage = "Error al eliminar la reserva: ${e.localizedMessage}"
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
                        .background(Color(0xFF33D1FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Universidad Católica de Temuco, 2024", color = Color.White)
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun EliminarReservaScreenPreview() {
    MyApplicationTheme {
        EliminarReservaScreen(
            navController = rememberNavController(),
            isLoggedIn = true,
            onLogout = {},
            rut = ""
        )
    }
}


