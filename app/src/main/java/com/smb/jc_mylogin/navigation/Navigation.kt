package com.smb.jc_mylogin.navigation

import JuegoScreen
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smb.jc_mylogin.screens.splash.SplashScreen
import com.smb.jc_mylogin.screens.login.LoginScreen
import com.smb.jc_mylogin.screens.home.Home
import com.smb.jc_mylogin.screens.jugar.Dificultad
import com.smb.jc_mylogin.screens.jugar.DificultadScreen
import com.smb.jc_mylogin.screens.jugar.JuegoTerminadoScreen
import com.smb.jc_mylogin.screens.ranking.RankingScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screens.SplashScreen.name
    ) {
        composable(Screens.SplashScreen.name) {
            SplashScreen(navController = navController)
        }
        composable(Screens.LoginScreen.name) {
            LoginScreen(navController = navController)
        }
        composable(Screens.HomeScreen.name) {
            Home(navController = navController)
        }
        composable(
            route = "${Screens.RankingScreen.name}/{userEmail}"
        ) { backStackEntry ->
            // Extraemos el parámetro 'userEmail' de la ruta
            val userEmail = backStackEntry.arguments?.getString("userEmail") ?: ""
            // Pasamos el correo como parámetro a la pantalla de Ranking
            RankingScreen(navController = navController, currentUserEmail = userEmail)
        }
        composable(
            route = "${Screens.DificultadScreen.name}/{userEmail}"
        ) { backStackEntry ->
            // Extraemos el parámetro 'userEmail' de la ruta
            val userEmail = backStackEntry.arguments?.getString("userEmail") ?: ""
            // Pasamos el correo como parámetro a la pantalla de Ranking
            DificultadScreen(navController = navController, userEmail)
        }
        composable(
            route = "${Screens.JuegoScreen.name}/{dificultad}/{userEmail}"
        ) { backStackEntry ->
            val dificultad = backStackEntry.arguments?.getString("dificultad")?.let { Dificultad.valueOf(it) }
            val userEmail = backStackEntry.arguments?.getString("userEmail") ?: ""

            if (dificultad != null) {
                JuegoScreen(
                    navController = navController,
                    dificultad = dificultad,
                    userEmail = userEmail
                )
            }
        }

        composable(
            route = "${Screens.JuegoTerminadoScreen.name}/{puntos}"
        ) { backStackEntry ->
            val puntos = backStackEntry.arguments?.getString("puntos")?.toIntOrNull() ?: 0
            JuegoTerminadoScreen(navController = navController, puntos = puntos)
        }

    }
}
