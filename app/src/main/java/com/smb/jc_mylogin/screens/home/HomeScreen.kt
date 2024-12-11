package com.smb.jc_mylogin.screens.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.smb.jc_mylogin.navigation.Screens

@Composable
fun Home(navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }

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
                    .background(Color.Red),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "TRIVIANDO",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            // Centro con botones
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Botón "Jugar"
                Button(
                    onClick = {
                        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
                        navController.navigate("${Screens.DificultadScreen.name}/$userEmail")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .width(250.dp)
                        .height(80.dp)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "JUGAR",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Botón "Ranking"
                Button(
                    onClick = {
                        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
                        if (userEmail.isNotEmpty()) {
                            navController.navigate("${Screens.RankingScreen.name}/$userEmail")
                        } else {
                            Log.e("Ranking", "No user is authenticated.")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .width(250.dp)
                        .height(80.dp)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "RANKING",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            // Botón "Cambiar de cuenta" pegado abajo
            val context = LocalContext.current

            Button(
                onClick = {
                    showDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "CAMBIAR DE CUENTA",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Diálogo de confirmación
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    confirmButton = {
                        Button(
                            onClick = {
                                // Cerrar sesión de Firebase
                                FirebaseAuth.getInstance().signOut()

                                // Cerrar sesión de Google (si aplica)
                                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                                googleSignInClient.signOut().addOnCompleteListener {
                                    navController.navigate(Screens.LoginScreen.name)
                                }
                                showDialog = false
                            }
                        ) {
                            Text("Confirmar")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDialog = false }) {
                            Text("Cancelar")
                        }
                    },
                    title = {
                        Text("Confirmación")
                    },
                    text = {
                        Text("¿Estás seguro de que deseas cerrar sesión y cambiar de cuenta?")
                    }
                )
            }
        }
    }
}
