package com.example.myapplication

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformacionScreen(
    navController: NavController,
    isLoggedIn: Boolean,
    onLogout: () -> Unit,
    username: String,
    email: String,
    direccion: String,
    carrera: String,
    rut: String,
    imageBase64: String? // Imagen opcional
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }

    // Decodificar imagen base64 si está disponible
    val bitmap = imageBase64?.let {
        val imageBytes = Base64.decode(it, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    // Fondo con diseño redondeado
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Fondo blanco para el 65% restante
    ) {
        // Parte superior azul con borde redondeado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp) // Aproximadamente el 35% de la pantalla
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1565C0), // Azul más oscuro
                            Color(0xFF42A5F5)  // Azul más claro
                        )
                    ),
                    shape = RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp)
                )
        ) {
            // Botón de volver en la esquina superior izquierda
            IconButton(
                onClick = { navController.popBackStack() }, // Acción para volver a la página anterior
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Imagen del perfil
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Imagen de Perfil",
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Nombre y email
            Text(
                text = username,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = email,
                fontSize = 14.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botones de acciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ActionButton(icon = Icons.Default.Settings) // Tuerca para Settings
                ActionButton(icon = Icons.Default.Home) // Casa para Home
                ActionButton(icon = Icons.Default.ExitToApp) // Cuadrado con flecha para Logout
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campos de información con líneas divisorias
            InfoField(label = "Carrera:", value = carrera)
            Divider(color = Color.Gray, thickness = 1.dp)
            InfoField(label = "Nombre de usuario:", value = username)
            Divider(color = Color.Gray, thickness = 1.dp)
            InfoField(label = "Rut:", value = rut)
            Divider(color = Color.Gray, thickness = 1.dp)
            InfoField(label = "Direccion:", value = direccion)

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Editar Perfil
            Button(
                onClick = { /* Acción de editar perfil */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFCC40A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .clip(RoundedCornerShape(50.dp))
            ) {
                Text("EDIT PROFILE", color = Color.White)
            }
        }
    }
}

@Composable
fun InfoField(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color(0xFF1565C0), // Mismo azul que el fondo
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            color = Color.Black, // Color negro para los valores
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun ActionButton(icon: ImageVector) {
    Button(
        onClick = { /* Acción del botón */ },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFfcc40d)),
        modifier = Modifier
            .clip(CircleShape)
            .size(50.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InformacionScreenPreview() {
    MyApplicationTheme {
        InformacionScreen(
            navController = rememberNavController(),
            username = "Jack Willson",
            email = "jackwillson@gmail.com",
            isLoggedIn = true,
            onLogout = {},
            rut = "",
            imageBase64 = null,
            carrera = "",
            direccion = ""// Imagen opcional
        )
    }
}
