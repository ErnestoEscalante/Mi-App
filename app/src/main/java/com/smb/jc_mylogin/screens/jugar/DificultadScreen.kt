package com.smb.jc_mylogin.screens.jugar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smb.jc_mylogin.navigation.Screens

@Composable
fun DificultadScreen(navController: NavController, userEmail: String) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFFF1C1)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Encabezado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color(0xFF4CAF50)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Elige la dificultad",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Botones de dificultad
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DifficultyButton(
                    text = "FACIL",
                    color = Color(0xFF4CAF50),
                    onClick = {
                        navController.navigate("${Screens.JuegoScreen.name}/FACILES/$userEmail")
                    }
                )

                Spacer(modifier = Modifier.height(30.dp))

                DifficultyButton(
                    text = "MEDIO",
                    color = Color(0xFF4CAF50),
                    onClick = {
                        navController.navigate("${Screens.JuegoScreen.name}/MEDIAS/$userEmail")
                    }
                )

                Spacer(modifier = Modifier.height(30.dp))

                DifficultyButton(
                    text = "DIFICIL",
                    color = Color(0xFF4CAF50),
                    onClick = {
                        navController.navigate("${Screens.JuegoScreen.name}/DIFICILES/$userEmail")
                    }
                )
            }

            Button(
                onClick = {
                    navController.navigate(Screens.HomeScreen.name)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .height(100.dp)
                    .padding(19.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Volver",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun DifficultyButton(
    text: String,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(50),
        modifier = Modifier
            .width(200.dp)
            .height(60.dp)
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}
