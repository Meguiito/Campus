package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch
import kotlin.math.min
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

class WeatherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherScreen(navController = rememberNavController(), isLoggedIn = true, onLogout = {})
        }
    }
}

@Composable
fun WeatherScreen(
    navController: NavController,
    isLoggedIn: Boolean,
    onLogout: () -> Unit
) {
    val apiService = WeatherRetrofitInstance.api
    val scope = rememberCoroutineScope()
    var forecast by remember { mutableStateOf<List<WeatherForecastDay>>(emptyList()) }
    var currentPage by remember { mutableStateOf(0) } // Página actual
    val itemsPerPage = 5 // Número de días por página

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response: WeatherForecastResponse = apiService.getWeatherForecast(
                    apiKey = "a9331d9fc22f491197860510242410",
                    location = "Temuco",
                    days = 10
                )
                forecast = response.forecast.forecastday.map { day: ForecastDay ->
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

    val totalPages = (forecast.size + itemsPerPage - 1) / itemsPerPage

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo degradado (cielo azul con efecto soleado)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D47A1), // Azul oscuro
                        Color(0xFF42A5F5)  // Azul claro
                    )
                ))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Flecha de retroceso en la esquina superior izquierda
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Título del clima
            Text(
                text = "El clima en Temuco",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(32.dp))


            Spacer(modifier = Modifier.height(16.dp))

            // Pronóstico de varios días
            Column {
                val startIndex = currentPage * itemsPerPage
                val endIndex = min(startIndex + itemsPerPage, forecast.size)
                val currentForecast = forecast.subList(startIndex, endIndex)

                currentForecast.forEach { dayForecast ->
                    WeatherCard(dayForecast)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Controles de paginación con botones numerados
            Row(
                modifier = Modifier.padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (page in 0 until totalPages) {
                    Button(
                        onClick = { currentPage = page },
                        enabled = currentPage != page,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text("${page + 1}")
                    }
                }
            }
        }
    }
}

fun translateCondition(conditionText: String): String {
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

@Composable
fun WeatherCard(weather: WeatherForecastDay) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(2.dp, Color(0xFFFFC107)) // Borde amarillo como en la imagen
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val painter: Painter = rememberAsyncImagePainter(weather.iconUrl)

            Image(
                painter = painter,
                contentDescription = "Icono del clima",
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = weather.date,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = Color.Black
                )
                Text(
                    text = translateCondition(weather.conditionText),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
                Row(
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = "Máx: ${weather.maxTemp}°C",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Mín: ${weather.minTemp}°C",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherScreenPreview() {
    MyApplicationTheme {
        WeatherScreen(
            navController = rememberNavController(),
            isLoggedIn = true,
            onLogout = {}
        )
    }
}
