package com.erdem.nosi

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.translation.Translator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.erdem.nosi.screen.CollectionDetailScreen
import com.erdem.nosi.screen.DictionaryScreen
import com.erdem.nosi.screen.MainScreenContent
import com.erdem.nosi.screen.StudyScreen
import com.erdem.nosi.screen.TranslationScaffol
import com.erdem.nosi.ui.theme.CardBackgroundDark
import com.erdem.nosi.ui.theme.NosiTheme
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.genai.common.DownloadStatus
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.prompt.Generation
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            NosiTheme {
                AppNavigation()
            }
        }




        //-----
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.TURKISH)
            .setTargetLanguage(TranslateLanguage.ENGLISH)
            .build()

        val englishTurkishTranslator = Translation.getClient(options)

        var conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        englishTurkishTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                Log.e("translation","Model downloaded successfully")
            }
            .addOnFailureListener { exception ->
                Log.e("translation","Model download failed: $exception")
            }

        var text = "Ona aşık olduğum anı dün gibi hatırlıyorum"

        englishTurkishTranslator.translate(text)
            .addOnSuccessListener { translatedText ->
                Log.e("translation","Translated text: $translatedText")
            }
            .addOnFailureListener { exception ->
                Log.e("translation","Translation failed: $exception")
            }
        var modelDownloaded = false
        fun testGemini() {
            CoroutineScope(Dispatchers.IO).launch {
                try {

                    /*val model = Firebase.ai(
                        backend = GenerativeBackend.googleAI()
                    ).generativeModel("gemini-3-flash-preview")

                    val prompt = "Dün gece aşık oldum. Bu cümleyi ingilizce diline çevir ve cümlede kullandığın kelimelerin" +
                            " zaman ve çoğul eklerinden arındılmış halde anlamlarını ve türlerini ver. bunu bir json formatında ver" +
                            ""

                    val response = model.generateContent(prompt)

                    Log.e("yapay","AI Response: ${response.text}")*/

                } catch (e: Exception) {
                    println("Error: ${e.message}")
                }
            }
        }
        testGemini()

    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "main",
        modifier = Modifier.background(CardBackgroundDark),
        // Giriş: yukarıdan aşağıya açılma + fade in
        enterTransition = {
            expandVertically(
                animationSpec = tween(900)
            )
        },
        // Çıkış: aşağıdan yukarıya kapanma + fade out
        exitTransition = {
            shrinkVertically(
                animationSpec = tween(900)
            )
        },
        // Geri gelirken: yukarıdan aşağıya açılma + fade in
        popEnterTransition = {
            expandVertically(
                animationSpec = tween(900)
            )
        },
        // Geri giderken: aşağıdan yukarıya kapanma + fade out
        popExitTransition = {
            shrinkVertically(
                animationSpec = tween(900)
            )
        }
    ) {
        composable("main") {
            MainScreenContent(
                onNavigateToTranslation = {
                    navController.navigate("translation")
                },
                onNavigateToWordLookup = {
                    navController.navigate("dictionary")
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
        composable("dictionary") {
            DictionaryScreen(
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
                },
                onNavigateToStudy = {
                    navController.navigate("study/$collectionId")
                }
            )
        }
        composable(
            route = "study/{collectionId}",
            arguments = listOf(navArgument("collectionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getLong("collectionId") ?: 1L
            StudyScreen(
                collectionId = collectionId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}