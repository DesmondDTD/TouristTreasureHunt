package com.example.touriststreasurehunt.ui.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.touriststreasurehunt.ui.*

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = NavRoutes.OBJECTIVES
    ) {

        composable(NavRoutes.OBJECTIVES) {
            MainScreen {
                navController.navigate(NavRoutes.CLUES)
            }
        }

        composable(NavRoutes.CLUES) {
            LaunchedEffect(Unit) {
                context.startActivity(
                    Intent(context, ClueActivity::class.java)
                )
            }
        }

        composable(NavRoutes.DESTINATION) {
            LaunchedEffect(Unit) {
                context.startActivity(
                    Intent(context, FinishActivity::class.java)
                )
            }
        }
    }
}


