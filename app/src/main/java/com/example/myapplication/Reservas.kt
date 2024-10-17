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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaScreen(
    navController: NavController,
    isLoggedIn: Boolean,
    onLogout: () -> Unit,
    mesSeleccionado: String,
    diaSeleccionado: String,
    username: String, email: String, rut: String
) {
    var nombre by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var carrera by remember { mutableStateOf("") }
    var canchaSeleccionada by remember { mutableStateOf("") }
    var duracionSeleccionada by remember { mutableStateOf("") }
    var canchas by remember { mutableStateOf(listOf<String>()) }
    var duraciones = listOf("10:00 a 11:30", "11:30 a 13:00", "13:00 a 14:30", "14:30 a 16:00", "16:00 a 17:30")
    var expandedCancha by remember { mutableStateOf(false) }
    var expandedDuracion by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    // Cargar las canchas desde la API
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val response = RetrofitInstance.api.getEspacios()
                canchas = response
            } catch (e: Exception) {
                errorMessage = "Error al cargar canchas: ${e.localizedMessage}"
            }
        }
    }

    // Llamar a la API para obtener los horarios ocupados
    LaunchedEffect(canchaSeleccionada, mesSeleccionado, diaSeleccionado) {
        if (canchaSeleccionada.isNotEmpty() && mesSeleccionado.isNotEmpty() && diaSeleccionado.isNotEmpty()) {
            coroutineScope.launch {
                try {
                    val response = RetrofitInstance.api.getDisponibilidad(
                        cancha = canchaSeleccionada,
                        mes = mesSeleccionado,
                        dia = diaSeleccionado
                    )
                    val horariosOcupados = response.horarios_ocupados

                    // Filtrar las duraciones disponibles
                    duraciones = listOf("10:00 a 11:30", "11:30 a 13:00", "13:00 a 14:30", "14:30 a 16:00", "16:00 a 17:30")
                        .filter { duracion -> !horariosOcupados.contains(duracion) }

                } catch (e: Exception) {
                    errorMessage = "Error al cargar la disponibilidad: ${e.localizedMessage}"
                }
            }
        }
    }

    // Lateral Drawer
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            if (isLoggedIn) {
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
                            scope.launch { drawerState.close() }
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Perfil") },
                        selected = false,
                        onClick = {
                            navController.navigate("perfil/$username/$email/$rut")
                            coroutineScope.launch { drawerState.close() }
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
            }
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                // Fondo de la sección entre las barras
                Image(
                    painter = painterResource(id = R.drawable.uctinformatica),
                    contentDescription = "Fondo",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 60.dp, bottom = 50.dp)
                        .align(Alignment.TopStart),
                    contentScale = ContentScale.FillHeight
                )

                // Barra superior con el logo
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

                // Icono para abrir el Drawer
                IconButton(
                    onClick = { scope.launch { drawerState.open() } },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                }

                // Formulario de reserva
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp, top = 80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Mensajes de error o éxito
                    if (errorMessage != null) {
                        Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                    }
                    if (successMessage != null) {
                        Text(text = successMessage!!, color = Color.Green)
                    }

                    // Campos del formulario
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre y Apellido") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = rut,
                        onValueChange = { rut = it },
                        label = { Text("RUT") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = carrera,
                        onValueChange = { carrera = it },
                        label = { Text("Carrera en Curso") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Campos para seleccionar día y mes
                    OutlinedTextField(
                        value = diaSeleccionado.toString(),
                        onValueChange = { /* No permitir cambios directos, solo mostrar */ },
                        label = { Text("Día de Reserva") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = mesSeleccionado.toString(),
                        onValueChange = { /* No permitir cambios directos, solo mostrar */ },
                        label = { Text("Mes de Reserva") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true
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
                            }
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
                            }
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

                    // Botón para enviar el formulario
                    Button(
                        onClick = {
                            isLoading = true
                            coroutineScope.launch {
                                try {
                                    RetrofitInstance.api.crearReserva(
                                        ReservaRequest(
                                            nombre = nombre,
                                            rut = rut,
                                            carrera = carrera,
                                            cancha = canchaSeleccionada,
                                            duracion = duracionSeleccionada,
                                            mes = mesSeleccionado,
                                            dia = diaSeleccionado
                                        )
                                    )
                                    successMessage = "Reserva creada exitosamente."
                                } catch (e: Exception) {
                                    errorMessage = "Error esa hora ya esta ocupada"
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Text("Enviar Reserva")
                        }
                    }
                }
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
fun ReservaScreenPreview() {
    MyApplicationTheme {
        val navController = rememberNavController()
        ReservaScreen(
            navController = navController,
            isLoggedIn = true,
            onLogout = {},
            mesSeleccionado = "Octubre",
            diaSeleccionado = "16",
            username = "",
            rut="",
            email = ""
        )
    }
}
