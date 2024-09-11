package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.R

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

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


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CustomButton(text = "Realizar Reserva")
            Spacer(modifier = Modifier.height(16.dp))
            CustomButton(text = "Editar Reserva")
            Spacer(modifier = Modifier.height(16.dp))
            CustomButton(text = "Eliminar Reserva")
        }


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .align(Alignment.BottomCenter)
                .background(Color.Gray)
        )
    }
}

@Composable
fun CustomButton(text: String) {
    Button(
        onClick = { /* TODO: Implementar funcionalidad */ },
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(50.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF33D1FF)
        )
    ) {
        Text(text = text, color = Color.Black)
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MyApplicationTheme {
        MainScreen()
    }
}
