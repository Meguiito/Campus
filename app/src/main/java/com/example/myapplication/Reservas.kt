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
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun ReservaScreen() {
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

            // Selector de Cancha o Gimnasio
            ExposedDropdownMenuBoxField(
                label = "Seleccionar Cancha o Gimnasio",
                options = canchas,
                selectedOption = canchaSeleccionada,
                onOptionSelected = { canchaSeleccionada = it }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Selector de Duración
            ExposedDropdownMenuBoxField(
                label = "Duración",
                options = duraciones,
                selectedOption = duracionSeleccionada,
                onOptionSelected = { duracionSeleccionada = it }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Botón para enviar
            Button(
                onClick = { /* TODO: Implementar funcionalidad de reserva */ },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF33D1FF)
                )
            ) {
                Text(text = "Confirmar Reserva", color = Color.Black)
            }
        }

        // Barra inferior
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .align(Alignment.BottomCenter)
                .background(Color.Gray)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenuBoxField(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(selectedOption) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = { },
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier
                .menuAnchor() // Importante para posicionar el menú correctamente
                .fillMaxWidth(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        selectedText = option
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReservaScreenPreview() {
    MyApplicationTheme {
        ReservaScreen()
    }
}
