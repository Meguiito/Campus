package com.example.myapplication

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    isLoggedIn: Boolean,
    onLogout: () -> Unit,
    username: String,
    email: String,
    imageBase64: String?,
    rut: String
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }

    // Estructura de la pantalla
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Parte superior azul con borde redondeado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1565C0),
                            Color(0xFF42A5F5)
                        )
                    ),
                    shape = RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp)
                )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Top
            ) {
                TopBarSection(imageBase64, drawerState, coroutineScope, username, email, rut, navController)
                Spacer(modifier = Modifier.height(16.dp))
                WelcomeSection(username)

                Spacer(modifier = Modifier.height(16.dp))

                // Carrusel del clima
                WeatherCarousel()
            }
        }

        val drawerState = rememberDrawerState(DrawerValue.Closed)
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
                                coroutineScope.launch { drawerState.close() }
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
                                coroutineScope.launch { drawerState.close() }
                            }
                        )
                    }
                }
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 380.dp)
                        .background(Color.White)
                ) {
                    Spacer(modifier = Modifier.height(5.dp))
                    ButtonSection(navController)
                    Spacer(modifier = Modifier.height(20.dp))
                    RecommendedSection()

                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        )
    }
}

@Composable
fun TopBarSection(
    imageBase64: String?,
    drawerState: DrawerState,
    coroutineScope: CoroutineScope,
    username: String,
    email: String,
    rut: String,
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_location),
                    contentDescription = "Location",
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
                Text(
                    text = "Temuco, CL",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            // Imagen de perfil como botón clickeable
            IconButton(
                onClick = {
                    coroutineScope.launch { drawerState.open() }
                    navController.navigate("perfil/$username/$email/$rut")
                },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
                    .border(2.dp, Color.White, CircleShape)
            ) {
                val bitmap = imageBase64?.let {
                    val imageBytes = Base64.decode(it, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                }
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Imagen de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.clip(CircleShape)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Icono de perfil",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun WeatherCarousel() {
    val apiService = WeatherRetrofitInstance.api
    val scope = rememberCoroutineScope()
    var forecast by remember { mutableStateOf<List<WeatherForecastDay>>(emptyList()) }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response: WeatherForecastResponse = apiService.getWeatherForecast(
                    apiKey = "a9331d9fc22f491197860510242410",
                    location = "Temuco",
                    days = 3  // Cambiado a 3 días para obtener solo los próximos 3 días
                )
                forecast = response.forecast.forecastday.map { day ->
                    WeatherForecastDay(
                        date = day.date,
                        conditionText = day.day.condition.text,
                        iconUrl = "https:${day.day.condition.icon}",
                        maxTemp = day.day.maxtemp_c,
                        minTemp = day.day.mintemp_c
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Solo mostramos las primeras 3 tarjetas en una fila
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        forecast.take(3).forEach { dayForecast ->
            WeatherCard2(dayForecast)
        }
    }
}

@Composable
fun WeatherCard2(weather: WeatherForecastDay) {
    Card(
        modifier = Modifier
            .width(100.dp)  // Ajustamos el ancho para que quepan tres tarjetas
            .padding(4.dp),  // Ajustamos el padding para que haya espacio entre las tarjetas
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFFFC107))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val painter: Painter = rememberAsyncImagePainter(weather.iconUrl)

            Image(
                painter = painter,
                contentDescription = "Icono del clima",
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = weather.date,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
            Text(
                text = translateCondition2(weather.conditionText),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = "${weather.maxTemp}° / ${weather.minTemp}°C",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black
            )
        }
    }
}

@Composable
fun translateCondition2(conditionText: String): String {
    return when (conditionText.trim().lowercase()) {
        "sunny" -> "Soleado"
        "partly cloudy" -> "Parcialmente nublado"
        "cloudy" -> "Nublado"
        "rain" -> "Lluvia"
        "snow" -> "Nieve"
        "thunderstorm" -> "Tormenta"
        "fog" -> "Niebla"
        "patchy rain nearby" -> "Llovizna"
        else -> conditionText
    }
}

// Other existing functions remain the same


@Composable
fun WelcomeSection(username: String) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Hola $username,",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = Color.White // Cambiar a color blanco para la sección azul
        )
        Text(
            text = "Bienvenido nuevamente",
            fontSize = 18.sp,
            color = Color.White // Cambiar a color blanco para la sección azul
        )
    }
}

@Composable
fun ButtonSection(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MainScreenButton(text = "Reservar",) {
                navController.navigate("calendario")
            }
            MainScreenButton(text = "Editar reservas") {
                navController.navigate("editarReserva")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MainScreenButton(text = "Eliminar reservas") {
                navController.navigate("eliminarReserva")
            }
            MainScreenButton(text = "Clima") {
                navController.navigate("weather")
            }
        }
    }
}


@Composable
fun MainScreenButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(140.dp, 60.dp) // Mantiene el tamaño específico
            .padding(horizontal = 8.dp), // Espaciado horizontal entre los botones
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFfcc40d)),
        elevation = ButtonDefaults.buttonElevation(8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround // Espaciado entre icono y texto
        ) {
            Text(
                text = text,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}


@Composable
fun RecommendedSection() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Recommended",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Black
            )
            Text(
                text = "View More",
                fontSize = 14.sp,
                color = Color(0xFF3D5AFE)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RecommendationCard()
            RecommendationCard()
        }
    }
}

@Composable
fun RecommendationCard() {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .size(150.dp, 200.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color.Gray)
            ) {
                // Imagen de fondo de la recomendación
                Image(
                    painter = painterResource(id = R.drawable.complejo),
                    contentDescription = "Recommendation",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Campus Norte, Temuco",
                modifier = Modifier.padding(horizontal = 8.dp),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "★★★",
                modifier = Modifier.padding(horizontal = 8.dp),
                color = Color.Gray
            )
        }
    }
}






@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MyApplicationTheme {
        MainScreen(
            navController = rememberNavController(),
            isLoggedIn = true,
            onLogout = {},
            email = "",
            username = "Louise",
            rut = "",
            imageBase64 = ""
        )
    }
}
