package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun EditarReservaScreen(navController: NavController, rut: String) {
    var reservas by remember { mutableStateOf<List<ReservaResponse>>(emptyList()) }
    var selectedReserva by remember { mutableStateOf<ReservaResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
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
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Error al obtener reservas: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    if (isLoading) {
        CircularProgressIndicator()
    } else if (errorMessage != null) {
        Text(errorMessage ?: "Error desconocido")
    } else if (selectedReserva != null) {
        val reserva = selectedReserva!!

        // Variables para los campos editables
        var nombre by remember { mutableStateOf(reserva.nombre) }
        var carrera by remember { mutableStateOf(reserva.carrera) }
        var cancha by remember { mutableStateOf(reserva.cancha) }
        var duracion by remember { mutableStateOf(reserva.duracion) }
        var mes by remember { mutableStateOf(reserva.mes) }
        var dia by remember { mutableStateOf(reserva.dia) }

        Column(modifier = Modifier.padding(16.dp)) {
            TextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = carrera,
                onValueChange = { carrera = it },
                label = { Text("Carrera") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = cancha,
                onValueChange = { cancha = it },
                label = { Text("Cancha") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = duracion,
                onValueChange = { duracion = it },
                label = { Text("Duración") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = mes,
                onValueChange = { mes = it },
                label = { Text("Mes") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = dia,
                onValueChange = { dia = it },
                label = { Text("Día") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch {
                        try {
                            val updatedReserva = ReservaRequest(
                                nombre = nombre,
                                rut = reserva.rut,
                                carrera = carrera,
                                cancha = cancha,
                                duracion = duracion,
                                mes = mes,
                                dia = dia
                            )
                            // Llamada PUT para actualizar la reserva
                            RetrofitInstance.api.updateReserva(reserva.id, updatedReserva)
                            navController.popBackStack() // Navegar de vuelta después de la actualización
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
    } else {
        Text("No se encontró ninguna reserva para editar")
    }
}
