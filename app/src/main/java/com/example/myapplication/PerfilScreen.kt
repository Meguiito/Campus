package com.example.myapplication

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    navController: NavController,
    isLoggedIn: Boolean,
    onLogout: () -> Unit,
    username: String,
    email: String,
    direccion: String,
    carrera: String,
    rut: String,
    imageBase64: String?
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var updatedImageBase64 by remember { mutableStateOf(imageBase64) }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Decodificar la imagen de perfil en Base64
    val bitmap = updatedImageBase64?.let {
        val imageBytes = Base64.decode(it, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(text = "Menú", fontSize = 20.sp, modifier = Modifier.padding(16.dp))
                Spacer(modifier = Modifier.height(16.dp))
                NavigationDrawerItem(
                    label = { Text("Inicio") },
                    selected = false,
                    onClick = {
                        navController.navigate("mainScreen")
                        coroutineScope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Cerrar sesión") },
                    selected = false,
                    onClick = {
                        onLogout()
                        coroutineScope.launch { drawerState.close() }
                    }
                )
            }
        },
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(colors = listOf(Color(0xFF1565C0), Color(0xFF42A5F5))))
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Imagen de perfil circular con botón
                    Box(contentAlignment = Alignment.TopEnd) {
                        Box(
                            modifier = Modifier
                                .size(130.dp)
                                .clip(CircleShape)
                                .border(4.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            bitmap?.let {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = "Imagen de Perfil",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(130.dp).clip(CircleShape)
                                )
                            } ?: Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Icono de Perfil",
                                tint = Color.Gray,
                                modifier = Modifier.size(130.dp)
                            )
                        }
                        IconButton(
                            onClick = {
                                imageUri?.let {
                                    val inputStream = context.contentResolver.openInputStream(it)
                                    val byteArray = inputStream?.readBytes()
                                    val newImageBase64 = byteArray?.let { bytes ->
                                        Base64.encodeToString(bytes, Base64.DEFAULT)
                                    }
                                    updatedImageBase64 = newImageBase64
                                    // Llamada al backend
                                    coroutineScope.launch {
                                        try {
                                            val imageRequest = ImageRequest(email = email, image = newImageBase64 ?: "")
                                            val response = RetrofitInstance.api.uploadProfileImage(imageRequest)
                                            if (response.message.isNotEmpty()) {
                                                // Notifica éxito
                                            }
                                        } catch (e: Exception) {
                                            // Maneja el error
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar Imagen",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = username,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(text = email, fontSize = 16.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(40.dp))


                    ProfileOptionButton("Cambiar imagen de perfil", Icons.Default.Edit) {
                        launcher.launch("image/*")
                    }

                    // Cambiar imagen al seleccionar nueva imagen
                    imageUri?.let {
                        val inputStream = context.contentResolver.openInputStream(it)
                        val byteArray = inputStream?.readBytes()
                        val newImageBase64 = byteArray?.let { bytes ->
                            Base64.encodeToString(bytes, Base64.DEFAULT)
                        }
                        updatedImageBase64 = newImageBase64
                        // Aquí puedes hacer una llamada a tu backend para actualizar la imagen del perfil
                    }
                    ProfileOptionButton("Mi información", Icons.Default.Person) {
                        navController.navigate("informacion/$username/$email/$rut/$carrera/$direccion")
                    }
                    ProfileOptionButton("Mis reservas", Icons.Default.List) {
                        navController.navigate("editarReserva")
                    }
                    ProfileOptionButton("Cerrar sesión", Icons.Default.ExitToApp, onClick = { onLogout() })
                }
            }
        }
    )
}


@Composable
fun ProfileOptionButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .padding(vertical = 8.dp)
            .height(60.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        elevation = ButtonDefaults.buttonElevation(8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color(0xFFfcc40d))
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                color = Color(0xFFfcc40d),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PerfilScreenPreview() {
    MyApplicationTheme {
        PerfilScreen(
            navController = rememberNavController(),
            email = "",
            username = "",
            rut = "",
            isLoggedIn = true,
            onLogout = {},
            imageBase64 = null,
            carrera = "",
            direccion = ""
        )
    }
}
