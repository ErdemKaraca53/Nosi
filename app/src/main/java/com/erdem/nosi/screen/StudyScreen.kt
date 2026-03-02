package com.erdem.nosi.screen

import android.app.Application
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.erdem.nosi.data.local.NosiDatabase
import com.erdem.nosi.data.local.SavedWordEntity
import com.erdem.nosi.ui.theme.CardBackgroundDark
import com.erdem.nosi.ui.theme.CardBackgroundMedium
import com.erdem.nosi.ui.theme.CardBorderColor
import com.erdem.nosi.ui.theme.GlowTeal
import com.erdem.nosi.ui.theme.GradientTealEnd
import com.erdem.nosi.ui.theme.GradientTealStart
import com.erdem.nosi.ui.theme.SectionHeaderColor
import com.erdem.nosi.ui.theme.SubtleTextColor
import com.erdem.nosi.ui.theme.White
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

// ──────────────────────────────────────
// Study ViewModel
// ──────────────────────────────────────
class StudyViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = NosiDatabase.getInstance(application).translationDao()

    val words = mutableStateListOf<SavedWordEntity>()
    var collectionName by mutableStateOf("Study")
        private set
    var isLoading by mutableStateOf(true)
        private set

    fun loadWords(collectionId: Long) {
        viewModelScope.launch {
            isLoading = true
            collectionName = dao.getCollectionName(collectionId) ?: "Study"
            val translations = dao.getTranslationsForCollectionOnce(collectionId)
            val allWords = mutableListOf<SavedWordEntity>()
            translations.forEach { translation ->
                allWords.addAll(dao.getWordsForTranslation(translation.id))
            }
            words.clear()
            words.addAll(allWords)
            isLoading = false
        }
    }
}

// ──────────────────────────────────────
// Study Screen
// ──────────────────────────────────────
@Composable
fun StudyScreen(
    collectionId: Long,
    onNavigateBack: () -> Unit = {}
) {
    val viewModel: StudyViewModel = viewModel()
    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(collectionId) {
        viewModel.loadWords(collectionId)
    }

    Scaffold(
        topBar = {
            TopBarWithBack(
                text = "Study — ${viewModel.collectionName}",
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
                viewModel.isLoading -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🃏", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading cards...",
                            color = SubtleTextColor,
                            fontSize = 16.sp,
                            fontFamily = LexendFontFamily
                        )
                    }
                }

                viewModel.words.isEmpty() -> {
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

                currentIndex >= viewModel.words.size -> {
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
                            text = "You've reviewed all ${viewModel.words.size} words",
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
                    // Card stack + swipe
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Progress
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "${currentIndex + 1} / ${viewModel.words.size}",
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
                        // Sürükleme ilerlemesi (0 = durağan, 1 = tam atılmış)
                        val dragProgress = (offsetX.value.absoluteValue / swipeThreshold).coerceIn(0f, 1f)

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            // Deste efekti — alttaki kartlar hafif açılı ve kaymış
                            val remaining = viewModel.words.size - currentIndex

                            // 3. kart (en altta)
                            if (remaining > 3) {
                                FlashCard(
                                    word = viewModel.words[currentIndex + 3],
                                    modifier = Modifier
                                        .graphicsLayer {
                                            rotationZ = 5f
                                            translationX = 8f
                                            translationY = 6f
                                            alpha = 0.2f
                                        }
                                )
                            }

                            // 2. kart — sürükleme ilerledikçe hafifçe düzeliyor
                            if (remaining > 2) {
                                FlashCard(
                                    word = viewModel.words[currentIndex + 2],
                                    modifier = Modifier
                                        .graphicsLayer {
                                            rotationZ = -3f + (dragProgress * 1f)
                                            translationX = -5f + (dragProgress * 2f)
                                            translationY = 4f - (dragProgress * 1f)
                                            alpha = 0.35f + (dragProgress * 0.1f)
                                        }
                                )
                            }

                            // 1. kart — sürükleme ilerledikçe ön plana geçiyor
                            if (remaining > 1) {
                                FlashCard(
                                    word = viewModel.words[currentIndex + 1],
                                    modifier = Modifier
                                        .graphicsLayer {
                                            rotationZ = 2f * (1f - dragProgress)
                                            translationX = 4f * (1f - dragProgress)
                                            translationY = 2f * (1f - dragProgress)
                                            alpha = 0.55f + (dragProgress * 0.45f)
                                        }
                                )
                            }

                            // Aktif kart (swipeable)
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
                                        detectHorizontalDragGestures(
                                            onDragEnd = {
                                                scope.launch {
                                                    when {
                                                        offsetX.value > swipeThreshold -> {
                                                            offsetX.animateTo(
                                                                targetValue = screenWidth * 2f,
                                                                animationSpec = tween(200)
                                                            )
                                                            currentIndex++
                                                            offsetX.snapTo(0f)
                                                        }
                                                        offsetX.value < -swipeThreshold -> {
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
                                FlashCard(word = viewModel.words[currentIndex])
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
                                text = "← Skip",
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
// Swipeable Flash Card
// ──────────────────────────────────────
@Composable
private fun SwipeableFlashCard(
    word: SavedWordEntity,
    onSwipedLeft: () -> Unit,
    onSwipedRight: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    // Swipe threshold
    val swipeThreshold = screenWidth.value * 0.35f

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.value.roundToInt(), 0) }
            .graphicsLayer {
                // Kartı döndür (sürükleme mesafesine göre)
                rotationZ = (offsetX.value / 40f).coerceIn(-15f, 15f)
                // Hafif ölçek efekti
                val scale = 1f - (offsetX.value.absoluteValue / 3000f).coerceIn(0f, 0.05f)
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(word) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        scope.launch {
                            when {
                                offsetX.value > swipeThreshold -> {
                                    // Sağa atıldı → animasyonla ekran dışına çık
                                    offsetX.animateTo(
                                        targetValue = screenWidth.value * 2f,
                                        animationSpec = tween(200)
                                    )
                                    onSwipedRight()
                                    offsetX.snapTo(0f)
                                }
                                offsetX.value < -swipeThreshold -> {
                                    // Sola atıldı → animasyonla ekran dışına çık
                                    offsetX.animateTo(
                                        targetValue = -screenWidth.value * 2f,
                                        animationSpec = tween(200)
                                    )
                                    onSwipedLeft()
                                    offsetX.snapTo(0f)
                                }
                                else -> {
                                    // Yetersiz sürükleme → geri dön
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
        FlashCard(word = word)
    }
}

// ──────────────────────────────────────
// Flash Card (iskambil kartı şeklinde)
// ──────────────────────────────────────
@Composable
private fun FlashCard(
    word: SavedWordEntity,
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
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Üst köşe süsü (iskambil tarzı)
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = word.pos.take(3).uppercase(),
                        color = GradientTealStart,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = LexendFontFamily,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "♦",
                        color = GradientTealStart,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Alt köşe süsü (ters iskambil tarzı)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .graphicsLayer { rotationZ = 180f }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = word.pos.take(3).uppercase(),
                        color = GradientTealStart.copy(alpha = 0.4f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = LexendFontFamily,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "♦",
                        color = GradientTealStart.copy(alpha = 0.4f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Kart ortası — kelime bilgileri
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
                            Brush.linearGradient(
                                colors = listOf(GradientTealStart, GradientTealEnd)
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (word.lemma.ifBlank { word.word }).take(1).uppercase(),
                        color = White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Kelime (lemma)
                Text(
                    text = word.lemma.ifBlank { word.word },
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
                        text = word.pos,
                        color = GradientTealStart,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = LexendFontFamily
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Orta çizgi
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(2.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    CardBorderColor,
                                    Color.Transparent
                                )
                            )
                        )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Türkçe anlam
                Text(
                    text = word.meaningTr,
                    color = SectionHeaderColor,
                    fontSize = 18.sp,
                    fontFamily = LexendFontFamily,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    lineHeight = 26.sp
                )
            }
        }
    }
}
