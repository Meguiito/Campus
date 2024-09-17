package com.example.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaScreen(navController: NavController, isLoggedIn: Boolean, onLogout: () -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var carrera by remember { mutableStateOf("") }

    var canchaSeleccionada by remember { mutableStateOf("") }
    var duracionSeleccionada by remember { mutableStateOf("") }
    var canchas by remember { mutableStateOf(listOf<String>()) }
    var duraciones = listOf("1 Hora", "2 Horas", "3 Horas")
    var expandedCancha by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    // Carga las canchas desde la API
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp, top = 100.dp),
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
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
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
            var expandedDuracion by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedDuracion,
                onExpandedChange = { expandedDuracion = !expandedDuracion }
            ) {
                OutlinedTextField(
                    value = duracionSeleccionada,
                    onValueChange = { duracionSeleccionada = it },
                    label = { Text("Seleccionar Duración") },
                    readOnly = true,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
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

            Button(
                onClick = {
                    if (nombre.isNotEmpty() && rut.isNotEmpty() && carrera.isNotEmpty() && canchaSeleccionada.isNotEmpty() && duracionSeleccionada.isNotEmpty()) {
                        coroutineScope.launch {
                            isLoading = true
                            try {
                                val response = RetrofitInstance.api.crearReserva(
                                    ReservaRequest(
                                        nombre = nombre,
                                        rut = rut,
                                        carrera = carrera,
                                        cancha = canchaSeleccionada,
                                        duracion = duracionSeleccionada
                                    )
                                )
                                successMessage = "Reserva realizada con éxito"
                                errorMessage = null
                            } catch (e: Exception) {
                                errorMessage = "Error al realizar la reserva: ${e.localizedMessage}"
                                successMessage = null
                            } finally {
                                isLoading = false
                            }
                        }
                    } else {
                        errorMessage = "Todos los campos son obligatorios"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text(if (isLoading) "Guardando..." else "Realizar Reserva")
            }
        }
    }
}
