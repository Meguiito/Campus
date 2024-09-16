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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaScreen(navController: NavController) {
    var nombre by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var carrera by remember { mutableStateOf("") }

    // Inicializar valores de selección con el primer elemento de las listas
    var canchaSeleccionada by remember { mutableStateOf("") }
    var duracionSeleccionada by remember { mutableStateOf("") }

    // Listas de opciones
    val canchas = listOf("Cancha de Futbol", "Cancha de Basquetbol", "Gimnasio")
    val duraciones = listOf("1 Hora", "2 Horas", "3 Horas")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Barra superior con el logo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .align(Alignment.TopCenter)
                .background(Color(0xFF33D1FF)),
            contentAlignment = Alignment.CenterStart
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(105.dp)
                    .padding(start = 16.dp),
                contentScale = ContentScale.Fit
            )
        }

        // Contenido del formulario
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp, top = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Campo de Nombre y Apellido
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre y Apellido") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Campo de RUT
            OutlinedTextField(
                value = rut,
                onValueChange = { rut = it },
                label = { Text("RUT") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Campo de Carrera en Curso
            OutlinedTextField(
                value = carrera,
                onValueChange = { carrera = it },
                label = { Text("Carrera en Curso") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Selector de Cancha
            ExposedDropdownMenuBox(
                expanded = canchaSeleccionada.isNotEmpty(),
                onExpandedChange = { canchaSeleccionada = canchaSeleccionada.takeIf { it.isEmpty() } ?: "" }
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
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = canchaSeleccionada.isNotEmpty())
                    }
                )

                ExposedDropdownMenu(
                    expanded = canchaSeleccionada.isNotEmpty(),
                    onDismissRequest = { canchaSeleccionada = "" }
                ) {
                    canchas.forEach { cancha ->
                        DropdownMenuItem(
                            text = { Text(cancha) },
                            onClick = {
                                canchaSeleccionada = cancha
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Selector de Duración
            ExposedDropdownMenuBox(
                expanded = duracionSeleccionada.isNotEmpty(),
                onExpandedChange = { duracionSeleccionada = duracionSeleccionada.takeIf { it.isEmpty() } ?: "" }
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
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = duracionSeleccionada.isNotEmpty())
                    }
                )

                ExposedDropdownMenu(
                    expanded = duracionSeleccionada.isNotEmpty(),
                    onDismissRequest = { duracionSeleccionada = "" }
                ) {
                    duraciones.forEach { duracion ->
                        DropdownMenuItem(
                            text = { Text(duracion) },
                            onClick = {
                                duracionSeleccionada = duracion
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Botón para realizar la reserva
            Button(
                onClick = {
                    // Lógica para realizar la reserva
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Realizar Reserva")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReservaScreenPreview() {
    MyApplicationTheme {
        ReservaScreen(navController = rememberNavController())
    }
}
