package com.erdem.nosi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.erdem.nosi.screen.CollectionDetailScreen
import com.erdem.nosi.screen.MainScreenContent
import com.erdem.nosi.screen.TranslationScaffol
import com.erdem.nosi.ui.theme.NosiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NosiTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            MainScreenContent(
                onNavigateToTranslation = {
                    navController.navigate("translation")
                },
                onNavigateToCollection = { collectionId ->
                    navController.navigate("collection/$collectionId")
                }
            )
        }
        composable("translation") {
            TranslationScaffol(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = "collection/{collectionId}",
            arguments = listOf(navArgument("collectionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getLong("collectionId") ?: 1L
            CollectionDetailScreen(
                collectionId = collectionId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}