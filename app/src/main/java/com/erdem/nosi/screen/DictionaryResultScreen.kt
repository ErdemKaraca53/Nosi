package com.erdem.nosi.screen

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.erdem.nosi.ViewModels.DictionaryUiState
import com.erdem.nosi.ViewModels.DictionaryViewModel
import com.erdem.nosi.data.DictionaryApiResponse
import com.erdem.nosi.data.Definitions
import com.erdem.nosi.data.Meaning
import com.erdem.nosi.ui.theme.NosiTheme
import com.erdem.nosi.ui.theme.CardBackgroundDark
import com.erdem.nosi.ui.theme.CardBackgroundMedium
import com.erdem.nosi.ui.theme.CardBorderColor
import com.erdem.nosi.ui.theme.GradientGoldEnd
import com.erdem.nosi.ui.theme.GradientGoldStart
import com.erdem.nosi.ui.theme.GradientTealEnd
import com.erdem.nosi.ui.theme.GradientTealStart
import com.erdem.nosi.ui.theme.SectionHeaderColor
import com.erdem.nosi.ui.theme.SubtleTextColor
import com.erdem.nosi.ui.theme.White
import kotlinx.coroutines.delay

// ─── Dictionary Result Ekranı ────────────────────────────────────────────────
@Composable
fun DictionaryResultScreen(
    word: String,
    onNavigateBack: () -> Unit = {}
) {
    var saveState by remember { mutableStateOf("IDLE") }

    val viewmodel: DictionaryViewModel = viewModel()
    val uiState by viewmodel.uiState.collectAsStateWithLifecycle()

    viewmodel.getDictionary(word)

    Log.e("deneme", "word: $word")

    LaunchedEffect(saveState) {
        if (saveState == "SAVING") {
            delay(1000)
            saveState = "SUCCESS"
        }
    }

    Scaffold(
        topBar = {
            TopBarWithBack(
                text = "Dictionary Lookup",
                onBack = { onNavigateBack() }
            )
        },
    ) { innerPadding ->
        when (val state = uiState) {
            is DictionaryUiState.Loading -> {
                // ── Loading State ──
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .background(color = CardBackgroundDark)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = GradientGoldStart,
                            trackColor = CardBorderColor.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Searching for \"$word\"...",
                            color = SectionHeaderColor,
                            fontSize = 16.sp,
                            fontFamily = LexendFontFamily
                        )
                    }
                }
            }

            is DictionaryUiState.Success -> {
                // ── Sonuç State ──
                DictionaryResultContent(
                    responses = state.response,
                    word = word,
                    saveState = saveState,
                    onSave = { saveState = "SAVING" },
                    modifier = Modifier
                        .padding(innerPadding)
                        .background(color = CardBackgroundDark)
                        .fillMaxSize()
                )
            }
        }
    }
}

// ─── Sonuç İçeriği (POS Seçici + Kart) ──────────────────────────────────────
@Composable
private fun DictionaryResultContent(
    responses: List<DictionaryApiResponse>,
    word: String,
    saveState: String,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (responses.isEmpty()) return

    // Tüm response'lardan tüm meaning'leri düzleştir (flatten)
    val allMeanings = remember(responses) {
        responses.flatMap { it.meanings }
    }

    if (allMeanings.isEmpty()) return

    // Mevcut part of speech tiplerini çıkar
    val posTypes = remember(allMeanings) {
        allMeanings.map { it.partOfSpeech }
    }

    // Noun varsa default seç, yoksa ilk elemanı seç
    var selectedIndex by remember(posTypes) {
        val nounIndex = posTypes.indexOfFirst { it.equals("noun", ignoreCase = true) }
        mutableIntStateOf(if (nounIndex >= 0) nounIndex else 0)
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // ── Kelime Başlığı ───────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Baş harf dairesi
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = CircleShape,
                        ambientColor = GradientGoldStart.copy(alpha = 0.4f),
                        spotColor = GradientGoldStart.copy(alpha = 0.4f)
                    )
                    .background(
                        Brush.linearGradient(listOf(GradientGoldStart, GradientGoldEnd)),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = word.take(1).uppercase(),
                    color = White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = LexendFontFamily
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = word.replaceFirstChar { it.uppercase() },
                color = White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = LexendFontFamily,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${posTypes.size} part${if (posTypes.size > 1) "s" else ""} of speech found",
                color = SubtleTextColor,
                fontSize = 14.sp,
                fontFamily = LexendFontFamily
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Part of Speech Chip Seçici ───────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            posTypes.forEachIndexed { index, type ->
                PosChip(
                    label = type.replaceFirstChar { it.uppercase() },
                    isSelected = index == selectedIndex,
                    onClick = { selectedIndex = index }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Seçili POS'a Göre Kart (animasyonlu geçiş) ──────────────────
        AnimatedContent(
            targetState = selectedIndex,
            transitionSpec = {
                fadeIn(tween(250)) togetherWith fadeOut(tween(200))
            },
            label = "pos_card_transition"
        ) { targetIndex ->
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                MeaningResultCard(meaning = allMeanings[targetIndex])
                SaveTranslationButton(
                    saveState = saveState,
                    onSave = onSave
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

// ─── Part of Speech Chip ─────────────────────────────────────────────────────
@Composable
private fun PosChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val background = if (isSelected) {
        Brush.linearGradient(listOf(GradientGoldStart, GradientGoldEnd))
    } else {
        Brush.linearGradient(listOf(CardBackgroundMedium, CardBackgroundMedium))
    }

    val borderBrush = if (isSelected) {
        Brush.linearGradient(listOf(GradientGoldStart, GradientGoldEnd))
    } else {
        Brush.linearGradient(
            listOf(
                CardBorderColor.copy(alpha = 0.6f),
                CardBorderColor.copy(alpha = 0.3f)
            )
        )
    }

    val textColor = if (isSelected) CardBackgroundDark else White.copy(alpha = 0.85f)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                brush = borderBrush,
                shape = RoundedCornerShape(12.dp)
            )
            .background(background)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontFamily = LexendFontFamily,
            letterSpacing = 0.5.sp
        )
    }
}

// ─── Meaning Result Card ─────────────────────────────────────────────────────
// Direkt API'den gelen Meaning nesnesini alıp gösterir.
// Her Definition kendi bloğunda: tanım + synonyms + antonyms + example
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MeaningResultCard(meaning: Meaning) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .wrapContentHeight()
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.3f)
            )
            .border(
                width = 1.dp,
                color = CardBorderColor.copy(alpha = 0.6f),
                shape = RoundedCornerShape(20.dp)
            ),
        color = CardBackgroundMedium,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // ── Tip Badge + Tanım Sayısı ─────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = GradientGoldStart.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        text = meaning.partOfSpeech.replaceFirstChar { it.uppercase() },
                        color = GradientGoldStart,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = LexendFontFamily
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "${meaning.definitions.size} definition${if (meaning.definitions.size > 1) "s" else ""}",
                    color = SubtleTextColor,
                    fontSize = 12.sp,
                    fontFamily = LexendFontFamily
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Her Definition ayrı ayrı gösteriliyor ────────────────────
            meaning.definitions.forEachIndexed { index, def ->

                // Definition'lar arası ayırıcı (ilk hariç)
                if (index > 0) {
                    Spacer(modifier = Modifier.height(16.dp))
                    GradientDivider()
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // ── Tanım Numarası + Metin ───────────────────────────────
                Row(modifier = Modifier.fillMaxWidth()) {
                    // Numara dairesi
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(
                                GradientGoldStart.copy(alpha = 0.15f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            color = GradientGoldStart,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = LexendFontFamily
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = def.definition,
                        color = White.copy(alpha = 0.9f),
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        fontFamily = LexendFontFamily,
                        modifier = Modifier.weight(1f)
                    )
                }

                // ── Bu tanıma ait Example ────────────────────────────────
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 40.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp, end = 10.dp)
                            .width(3.dp)
                            .height(14.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(GradientTealStart, GradientTealEnd)
                                )
                            )
                    )
                    if (def.example != null) {
                        Text(
                            text = "\"${def.example}\"",
                            color = SubtleTextColor,
                            fontSize = 14.sp,
                            fontStyle = FontStyle.Italic,
                            fontFamily = LexendFontFamily,
                            lineHeight = 20.sp
                        )
                    } else {
                        Text(
                            text = "No example provided",
                            color = SubtleTextColor.copy(alpha = 0.5f),
                            fontSize = 14.sp,
                            fontStyle = FontStyle.Italic,
                            fontFamily = LexendFontFamily,
                            lineHeight = 20.sp
                        )
                    }
                }

                // ── Bu tanıma ait Synonyms ───────────────────────────────
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 40.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "Syn",
                        color = GradientTealStart,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = LexendFontFamily,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier
                            .padding(top = 6.dp, end = 10.dp)
                    )
                    
                    if (def.synonyms.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            def.synonyms.forEach { synonym ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(
                                                    GradientTealStart.copy(alpha = 0.15f),
                                                    GradientTealEnd.copy(alpha = 0.08f)
                                                )
                                            )
                                        )
                                        .border(
                                            width = 1.dp,
                                            brush = Brush.linearGradient(
                                                colors = listOf(
                                                    GradientTealStart.copy(alpha = 0.5f),
                                                    GradientTealEnd.copy(alpha = 0.25f)
                                                )
                                            ),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = synonym,
                                        color = White,
                                        fontSize = 12.sp,
                                        fontFamily = LexendFontFamily
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "No synonyms",
                            color = SubtleTextColor.copy(alpha = 0.5f),
                            fontSize = 13.sp,
                            fontStyle = FontStyle.Italic,
                            fontFamily = LexendFontFamily,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                // ── Bu tanıma ait Antonyms ───────────────────────────────
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 40.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "Ant",
                        color = GradientGoldStart,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = LexendFontFamily,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier
                            .padding(top = 6.dp, end = 10.dp)
                    )
                    
                    if (def.antonyms.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            def.antonyms.forEach { antonym ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(
                                                    GradientGoldStart.copy(alpha = 0.12f),
                                                    GradientGoldEnd.copy(alpha = 0.06f)
                                                )
                                            )
                                        )
                                        .border(
                                            width = 1.dp,
                                            brush = Brush.linearGradient(
                                                colors = listOf(
                                                    GradientGoldStart.copy(alpha = 0.4f),
                                                    GradientGoldEnd.copy(alpha = 0.2f)
                                                )
                                            ),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = antonym,
                                        color = White.copy(alpha = 0.9f),
                                        fontSize = 12.sp,
                                        fontFamily = LexendFontFamily
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "No antonyms",
                            color = SubtleTextColor.copy(alpha = 0.5f),
                            fontSize = 13.sp,
                            fontStyle = FontStyle.Italic,
                            fontFamily = LexendFontFamily,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

// ─── Preview ─────────────────────────────────────────────────────────────────
@Preview(showBackground = true)
@Composable
fun DictionaryResultScreenPreview() {
    val mockResponses = listOf(
        DictionaryApiResponse(
            meanings = listOf(
                Meaning(
                    partOfSpeech = "noun",
                    definitions = listOf(
                        Definitions(
                            definition = "An act or spell of running.",
                            synonyms = listOf("sprint", "dash", "jog"),
                            antonyms = listOf("walk", "stroll"),
                            example = "I went for a run in the park."
                        ),
                        Definitions(
                            definition = "A continuous spell of a particular situation.",
                            synonyms = listOf("stretch", "spell"),
                            antonyms = emptyList(),
                            example = "The play had a long run on Broadway."
                        )
                    )
                ),
                Meaning(
                    partOfSpeech = "verb",
                    definitions = listOf(
                        Definitions(
                            definition = "Move at a speed faster than a walk.",
                            synonyms = listOf("sprint", "dash", "race"),
                            antonyms = listOf("walk", "crawl"),
                            example = "She runs five miles every morning."
                        ),
                        Definitions(
                            definition = "To manage or be in charge of.",
                            synonyms = listOf("manage", "operate", "direct"),
                            antonyms = emptyList(),
                            example = "He runs a successful business."
                        ),
                        Definitions(
                            definition = "To function or cause to function.",
                            synonyms = listOf("operate", "function", "work"),
                            antonyms = emptyList(),
                            example = "The engine is running smoothly."
                        )
                    )
                )
            )
        )
    )

    NosiTheme {
        DictionaryResultContent(
            responses = mockResponses,
            word = "run",
            saveState = "IDLE",
            onSave = {},
            modifier = Modifier
                .background(CardBackgroundDark)
                .fillMaxSize()
        )
    }
}
