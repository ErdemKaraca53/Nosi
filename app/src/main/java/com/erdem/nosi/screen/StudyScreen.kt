package com.erdem.nosi.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.erdem.nosi.ViewModels.DatabaseViewModel
import com.erdem.nosi.model.StudyWord
import com.erdem.nosi.ui.theme.CardBackgroundDark
import com.erdem.nosi.ui.theme.CardBackgroundMedium
import com.erdem.nosi.ui.theme.CardBorderColor
import com.erdem.nosi.ui.theme.GlowTeal
import com.erdem.nosi.ui.theme.GradientTealEnd
import com.erdem.nosi.ui.theme.GradientTealStart
import com.erdem.nosi.ui.theme.NosiTheme
import com.erdem.nosi.ui.theme.SectionHeaderColor
import com.erdem.nosi.ui.theme.SubtleTextColor
import com.erdem.nosi.ui.theme.White
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

// ──────────────────────────────────────
// Study Screen — Stateful
// ──────────────────────────────────────
@Composable
fun StudyScreen(
    collectionId: Long,
    onNavigateBack: () -> Unit = {}
) {
    val dbViewModel: DatabaseViewModel = viewModel()

    val liveWords by dbViewModel.getStudyWordsForList(collectionId)
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val allLists by dbViewModel.allLists.collectAsStateWithLifecycle()

    val collectionName = allLists.find { it.id == collectionId }?.name ?: "Study"

    // Deste seans boyunca sabit kalsın (az bilinenler önce). Mastery güncellemeleri
    // DB'ye yazılır ama ekrandaki sıra seans içinde karışmaz.
    var deck by remember { mutableStateOf<List<StudyWord>>(emptyList()) }
    LaunchedEffect(liveWords) {
        if (deck.isEmpty() && liveWords.isNotEmpty()) {
            deck = liveWords.sortedBy { it.masteryLevel }
        }
    }

    StudyContent(
        collectionName = collectionName,
        words = deck,
        onKnown = { word ->
            dbViewModel.setMastery(word.id, (word.masteryLevel + 1).coerceAtMost(StudyWord.MASTERY_MAX))
        },
        onNeedsReview = { word ->
            dbViewModel.setMastery(word.id, 0)
        },
        onNavigateBack = onNavigateBack
    )
}

// ──────────────────────────────────────
// Study — Stateless gövde
// ──────────────────────────────────────
@Composable
private fun StudyContent(
    collectionName: String,
    words: List<StudyWord>,
    onKnown: (StudyWord) -> Unit,
    onNeedsReview: (StudyWord) -> Unit,
    onNavigateBack: () -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopBarWithBack(
                text = "Study — $collectionName",
                onBack = onNavigateBack
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .background(color = CardBackgroundDark)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                words.isEmpty() -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "📭", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No words to study",
                            color = SubtleTextColor,
                            fontSize = 16.sp,
                            fontFamily = LexendFontFamily
                        )
                    }
                }

                currentIndex >= words.size -> {
                    // Tamamlandı
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(text = "🎉", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Well done!",
                            color = White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = LexendFontFamily
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "You've reviewed all ${words.size} word${if (words.size != 1) "s" else ""}",
                            color = SubtleTextColor,
                            fontSize = 16.sp,
                            fontFamily = LexendFontFamily,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(32.dp))

                        // Restart button
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp)),
                            color = GradientTealStart,
                            shape = RoundedCornerShape(14.dp),
                            onClick = { currentIndex = 0 }
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = 32.dp, vertical = 14.dp),
                                text = "Restart",
                                color = White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = LexendFontFamily
                            )
                        }
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Progress
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "${currentIndex + 1} / ${words.size}",
                            color = SubtleTextColor,
                            fontSize = 14.sp,
                            fontFamily = LexendFontFamily,
                            fontWeight = FontWeight.Normal
                        )

                        // Card area
                        val scope = rememberCoroutineScope()
                        val offsetX = remember { Animatable(0f) }
                        val screenWidth = LocalConfiguration.current.screenWidthDp
                        val swipeThreshold = screenWidth * 0.35f
                        val dragProgress = (offsetX.value.absoluteValue / swipeThreshold).coerceIn(0f, 1f)

                        // Aktif kartın "çevrilmiş" (anlam görünür) durumu — her kelimede sıfırlanır
                        var revealed by remember(currentIndex) { mutableStateOf(false) }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            val remaining = words.size - currentIndex

                            // 3. kart (en altta)
                            if (remaining > 3) {
                                FlashCard(
                                    word = words[currentIndex + 3],
                                    revealed = false,
                                    modifier = Modifier
                                        .graphicsLayer {
                                            rotationZ = 5f
                                            translationX = 8f
                                            translationY = 6f
                                            alpha = 0.2f
                                        }
                                )
                            }

                            // 2. kart
                            if (remaining > 2) {
                                FlashCard(
                                    word = words[currentIndex + 2],
                                    revealed = false,
                                    modifier = Modifier
                                        .graphicsLayer {
                                            rotationZ = -3f + (dragProgress * 1f)
                                            translationX = -5f + (dragProgress * 2f)
                                            translationY = 4f - (dragProgress * 1f)
                                            alpha = 0.35f + (dragProgress * 0.1f)
                                        }
                                )
                            }

                            // 1. kart
                            if (remaining > 1) {
                                FlashCard(
                                    word = words[currentIndex + 1],
                                    revealed = false,
                                    modifier = Modifier
                                        .graphicsLayer {
                                            rotationZ = 2f * (1f - dragProgress)
                                            translationX = 4f * (1f - dragProgress)
                                            translationY = 2f * (1f - dragProgress)
                                            alpha = 0.55f + (dragProgress * 0.45f)
                                        }
                                )
                            }

                            // Aktif kart (swipe + tap-to-flip)
                            Box(
                                modifier = Modifier
                                    .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                                    .graphicsLayer {
                                        rotationZ = (offsetX.value / 40f).coerceIn(-15f, 15f)
                                        val scale = 1f - (offsetX.value.absoluteValue / 3000f).coerceIn(0f, 0.05f)
                                        scaleX = scale
                                        scaleY = scale
                                    }
                                    .pointerInput(currentIndex) {
                                        detectTapGestures(onTap = { revealed = !revealed })
                                    }
                                    .pointerInput(currentIndex) {
                                        detectHorizontalDragGestures(
                                            onDragEnd = {
                                                scope.launch {
                                                    when {
                                                        offsetX.value > swipeThreshold -> {
                                                            // Sağa → "öğrendim"
                                                            onKnown(words[currentIndex])
                                                            offsetX.animateTo(
                                                                targetValue = screenWidth * 2f,
                                                                animationSpec = tween(200)
                                                            )
                                                            currentIndex++
                                                            offsetX.snapTo(0f)
                                                        }
                                                        offsetX.value < -swipeThreshold -> {
                                                            // Sola → "tekrar et"
                                                            onNeedsReview(words[currentIndex])
                                                            offsetX.animateTo(
                                                                targetValue = -screenWidth * 2f,
                                                                animationSpec = tween(200)
                                                            )
                                                            currentIndex++
                                                            offsetX.snapTo(0f)
                                                        }
                                                        else -> {
                                                            offsetX.animateTo(
                                                                targetValue = 0f,
                                                                animationSpec = tween(400)
                                                            )
                                                        }
                                                    }
                                                }
                                            },
                                            onHorizontalDrag = { _, dragAmount ->
                                                scope.launch {
                                                    offsetX.snapTo(offsetX.value + dragAmount)
                                                }
                                            }
                                        )
                                    }
                            ) {
                                FlashCard(word = words[currentIndex], revealed = revealed)
                            }
                        }

                        // Swipe hints
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 48.dp, vertical = 24.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "← Review",
                                color = SubtleTextColor.copy(alpha = 0.6f),
                                fontSize = 14.sp,
                                fontFamily = LexendFontFamily
                            )
                            Text(
                                text = "Got it →",
                                color = GradientTealStart.copy(alpha = 0.6f),
                                fontSize = 14.sp,
                                fontFamily = LexendFontFamily
                            )
                        }
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────
// Flash Card (iskambil kartı şeklinde, iki yüzlü)
// ──────────────────────────────────────
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlashCard(
    word: StudyWord,
    revealed: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .width(280.dp)
            .height(400.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = GlowTeal.copy(alpha = 0.3f),
                spotColor = GlowTeal.copy(alpha = 0.3f)
            ),
        color = CardBackgroundMedium,
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Üst köşe süsü
            Box(modifier = Modifier.align(Alignment.TopStart).padding(16.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = word.partOfSpeech.take(3).uppercase(),
                        color = GradientTealStart,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = LexendFontFamily,
                        letterSpacing = 1.sp
                    )
                    Text(text = "♦", color = GradientTealStart, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Alt köşe süsü (düz okunur — ters değil)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = word.partOfSpeech.take(3).uppercase(),
                        color = GradientTealStart.copy(alpha = 0.4f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = LexendFontFamily,
                        letterSpacing = 1.sp
                    )
                    Text(text = "♦", color = GradientTealStart.copy(alpha = 0.4f), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Orta içerik
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Baş harf dairesi
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            Brush.linearGradient(listOf(GradientTealStart, GradientTealEnd)),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = word.word.take(1).uppercase(),
                        color = White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Kelime
                Text(
                    text = word.word.replaceFirstChar { it.uppercase() },
                    color = White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = LexendFontFamily,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                // POS tag
                Surface(
                    color = GradientTealStart.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        text = word.partOfSpeech,
                        color = GradientTealStart,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = LexendFontFamily
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Orta çizgi
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(2.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color.Transparent, CardBorderColor, Color.Transparent)
                            )
                        )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Ön yüz: ipucu — Arka yüz: anlam(lar)
                if (!revealed) {
                    Text(
                        text = "👆 Tap to reveal meaning",
                        color = SubtleTextColor.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        fontFamily = LexendFontFamily,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Türkçe anlam — birincil (varsa)
                        if (word.meaningTr.isNotBlank()) {
                            Text(
                                text = word.meaningTr,
                                color = White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = LexendFontFamily,
                                textAlign = TextAlign.Center,
                                lineHeight = 28.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // İngilizce tanım(lar) — ikincil
                        if (word.definitions.isEmpty() && word.meaningTr.isBlank()) {
                            Text(
                                text = "No meaning saved",
                                color = SubtleTextColor,
                                fontSize = 15.sp,
                                fontFamily = LexendFontFamily,
                                textAlign = TextAlign.Center
                            )
                        } else {
                            word.definitions.take(2).forEachIndexed { index, def ->
                                if (index > 0) Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = def,
                                    color = SectionHeaderColor,
                                    fontSize = 14.sp,
                                    fontFamily = LexendFontFamily,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 20.sp,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        if (word.synonyms.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                word.synonyms.take(4).forEach { syn ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(GradientTealStart.copy(alpha = 0.12f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = syn,
                                            color = GradientTealStart,
                                            fontSize = 11.sp,
                                            fontFamily = LexendFontFamily
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StudyScreenPreview() {
    val sampleWords = listOf(
        StudyWord(
            id = 1L,
            word = "run",
            partOfSpeech = "verb",
            meaningTr = "koşmak",
            definitions = listOf("Move at a speed faster than a walk.", "To manage or be in charge of."),
            synonyms = listOf("sprint", "dash", "operate"),
            antonyms = listOf("walk")
        ),
        StudyWord(
            id = 2L,
            word = "idea",
            partOfSpeech = "noun",
            meaningTr = "fikir",
            definitions = listOf("A thought or suggestion as to a possible course of action."),
            synonyms = listOf("concept", "notion"),
            antonyms = emptyList()
        )
    )
    NosiTheme {
        StudyContent(
            collectionName = "Favorites",
            words = sampleWords,
            onKnown = {},
            onNeedsReview = {},
            onNavigateBack = {}
        )
    }
}
