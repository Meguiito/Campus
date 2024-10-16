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
import androidx.compose.ui.text.font.Font
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
fun EditarReservaScreen(navController: NavController, rut: String, isLoggedIn: Boolean, onLogout: () -> Unit) {
    var reservas by remember { mutableStateOf<List<ReservaResponse>>(emptyList()) }
    var selectedReserva by remember { mutableStateOf<ReservaResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var canchas by remember { mutableStateOf(listOf<String>()) }
    var expandedCancha by remember { mutableStateOf(false) }
    var expandedDuracion by remember { mutableStateOf(false) }
    var duracionSeleccionada by remember { mutableStateOf("") }
    val duraciones = listOf("10:00 a 11:30", "11:30 a 13:00", "13:00 a 14:30", "14:30 a 16:00", "16:00 a 17:30")
    val scope = rememberCoroutineScope()

    // Obtener las reservas basadas en el rut del usuario
    LaunchedEffect(rut) {
        scope.launch {
            try {
                isLoading = true
                reservas = RetrofitInstance.api.getReservasByRut(rut)
                if (reservas.isNotEmpty()) {
                    selectedReserva = reservas.first() // Selecciona la primera reserva para editar
                }
                canchas = RetrofitInstance.api.getEspacios() // Obtener las canchas
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
                } else if (selectedReserva != null) {
                    val reserva = selectedReserva!!

                    // Variables para los campos editables
                    var nombre by remember { mutableStateOf(reserva.nombre) }
                    var carrera by remember { mutableStateOf(reserva.carrera) }
                    var canchaSeleccionada by remember { mutableStateOf(reserva.cancha) }
                    var mes by remember { mutableStateOf(reserva.mes) }
                    var dia by remember { mutableStateOf(reserva.dia) }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp, top = 80.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre y Apellido") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedLabelColor = Color.White,
                                unfocusedLabelColor = Color.White,
                                cursorColor = Color.White,
                                containerColor = Color.Gray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = carrera,
                            onValueChange = { carrera = it },
                            label = { Text("Carrera en Curso") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedLabelColor = Color.White,
                                unfocusedLabelColor = Color.White,
                                cursorColor = Color.White,
                                containerColor = Color.Gray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = dia.toString(),
                            onValueChange = { /* No permitir cambios directos */ },
                            label = { Text("Día de Reserva") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedLabelColor = Color.White,
                                unfocusedLabelColor = Color.White,
                                cursorColor = Color.White,
                                containerColor = Color.Gray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = mes.toString(),
                            onValueChange = { /* No permitir cambios directos */ },
                            label = { Text("Mes de Reserva") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedLabelColor = Color.White,
                                unfocusedLabelColor = Color.White,
                                cursorColor = Color.White,
                                containerColor = Color.Gray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Selector de Cancha
                        ExposedDropdownMenuBox(
                            expanded = expandedCancha,
                            onExpandedChange = { expandedCancha = !expandedCancha }
                        ) {
                            OutlinedTextField(
                                value = canchaSeleccionada,
                                onValueChange = { canchaSeleccionada = it },
                                label = { Text("Seleccionar Cancha") },
                                readOnly = true,
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCancha)
                                },
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedLabelColor = Color.White,
                                    unfocusedLabelColor = Color.White,
                                    cursorColor = Color.White,
                                    containerColor = Color.Gray,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = expandedCancha,
                                onDismissRequest = { expandedCancha = false }
                            ) {
                                canchas.forEach { cancha ->
                                    DropdownMenuItem(
                                        text = { Text(cancha) },
                                        onClick = {
                                            canchaSeleccionada = cancha
                                            expandedCancha = false
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // Selector de Duración
                        ExposedDropdownMenuBox(
                            expanded = expandedDuracion,
                            onExpandedChange = { expandedDuracion = !expandedDuracion }
                        ) {
                            OutlinedTextField(
                                value = duracionSeleccionada,
                                onValueChange = { duracionSeleccionada = it },
                                label = { Text("Seleccionar Duración") },
                                readOnly = true,
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDuracion)
                                },
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedLabelColor = Color.White,
                                    unfocusedLabelColor = Color.White,
                                    cursorColor = Color.White,
                                    containerColor = Color.Gray,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = expandedDuracion,
                                onDismissRequest = { expandedDuracion = false }
                            ) {
                                duraciones.forEach { duracion ->
                                    DropdownMenuItem(
                                        text = { Text(duracion) },
                                        onClick = {
                                            duracionSeleccionada = duracion
                                            expandedDuracion = false
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                scope.launch {
                                    try {
                                        val updatedReserva = ReservaRequest(
                                            nombre = nombre,
                                            rut = reserva.rut,
                                            carrera = carrera,
                                            cancha = canchaSeleccionada,
                                            duracion = duracionSeleccionada,
                                            mes = mes,
                                            dia = dia
                                        )
                                        RetrofitInstance.api.updateReserva(reserva.id, updatedReserva)
                                        navController.popBackStack()
                                    } catch (e: Exception) {
                                        errorMessage = "Error al actualizar la reserva: ${e.localizedMessage}"
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Actualizar")
                        }
                    }
                }

                // Barra inferior
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .align(Alignment.BottomCenter)
                        .background(Color(0xCC2B2B2B)),
                    contentAlignment = Alignment.Center
                )
                {
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
fun EditarReservaScreenPreview() {
    MyApplicationTheme {
        EditarReservaScreen(
            navController = rememberNavController(),
            isLoggedIn = true,
            onLogout = {},
            rut = ""
        )
    }
}
