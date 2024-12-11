package com.smb.jc_mylogin.screens.ranking

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.smb.jc_mylogin.navigation.Screens

data class UserRanking(
    val name: String,
    val puntos: Int,
    val position: Int
)

@Composable
fun RankingScreen(
    navController: NavController,
    currentUserEmail: String
) {
    // Lista para almacenar los usuarios y sus puntos
    val rankingList = remember { mutableStateListOf<UserRanking>() }
    val currentUser = remember { mutableStateOf<UserRanking?>(null) }
    val loading = remember { mutableStateOf(true) }
    val error = remember { mutableStateOf<String?>(null) }

    // Cargar los datos de Firestore
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .orderBy("puntos", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                rankingList.clear()
                var position = 1
                result.forEach { document ->
                    val name = document.getString("name") ?: "Desconocido"
                    val puntos = document.getLong("puntos")?.toInt() ?: 0
                    val email = document.id

                    val userRanking = UserRanking(name, puntos, position)
                    rankingList.add(userRanking)

                    if (email == currentUserEmail) {
                        currentUser.value = userRanking
                    }

                    position++
                }
                loading.value = false
            }
            .addOnFailureListener { exception ->
                Log.e("RankingScreen", "Error al cargar el ranking: ", exception)
                error.value = "Error al cargar el ranking. Intenta nuevamente."
                loading.value = false
            }
    }

    // Pantalla de Ranking
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFFF1C1)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color.Red),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "RANKING",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                error.value?.let {
                    Text(
                        text = it,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                // Mostrar la lista de rankings solo si los datos están listos
                if (loading.value) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(rankingList) { user ->
                            UserRankItem(user = user)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Mostrar el usuario actual de forma destacada
                currentUser.value?.let {
                    Text(
                        text = "Tu posición: ${it.position} - ${it.name} - ${it.puntos} puntos",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Blue,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }

            // Botón "Volver" para regresar al Home
            Button(
                onClick = {
                    navController.navigate(Screens.HomeScreen.name)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(16.dp)
            ) {
                Text(
                    text = "VOLVER",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun UserRankItem(user: UserRanking) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "#${user.position}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = user.name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black,
            modifier = Modifier.weight(2f)
        )

        Text(
            text = "${user.puntos} puntos",
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
    }
}
