package com.erdem.nosi.screen

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.erdem.nosi.R
import com.erdem.nosi.data.TranslationData
import com.erdem.nosi.request.GeminiViewModel
import com.erdem.nosi.request.SaveState
import com.erdem.nosi.request.UiState
import com.erdem.nosi.ui.theme.AiTutorTextColor
import com.erdem.nosi.ui.theme.CardBackgroundDark
import com.erdem.nosi.ui.theme.CardBackgroundMedium
import com.erdem.nosi.ui.theme.CardBorderColor
import com.erdem.nosi.ui.theme.CardSurfaceGlass
import com.erdem.nosi.ui.theme.ChipSelectedGradientEnd
import com.erdem.nosi.ui.theme.ChipSelectedGradientStart
import com.erdem.nosi.ui.theme.ChipUnselectedBg
import com.erdem.nosi.ui.theme.DividerGradientCenter
import com.erdem.nosi.ui.theme.DividerGradientEnd
import com.erdem.nosi.ui.theme.DividerGradientStart
import com.erdem.nosi.ui.theme.GlowGold
import com.erdem.nosi.ui.theme.GlowTeal
import com.erdem.nosi.ui.theme.GradientGoldEnd
import com.erdem.nosi.ui.theme.GradientGoldStart
import com.erdem.nosi.ui.theme.GradientTealEnd
import com.erdem.nosi.ui.theme.GradientTealStart
import com.erdem.nosi.ui.theme.SectionHeaderColor
import com.erdem.nosi.ui.theme.SelectedTransleteContainer
import com.erdem.nosi.ui.theme.SelectedTransleteText
import com.erdem.nosi.ui.theme.SubtleTextColor
import com.erdem.nosi.ui.theme.UnSelectedTransleteContainer
import com.erdem.nosi.ui.theme.UnSelectedTransleteText
import com.erdem.nosi.ui.theme.White
import com.erdem.nosi.ui.theme.WordDetailBg

val LexendFontFamily = FontFamily(
    Font(R.font.lexend)
)

// ──────────────────────────────────────
// Gradient Divider Helper
// ──────────────────────────────────────
@Composable
fun GradientDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        DividerGradientStart,
                        DividerGradientCenter,
                        DividerGradientEnd
                    )
                )
            )
    )
}

// ──────────────────────────────────────
// Main Scaffold
// ──────────────────────────────────────
@Composable
fun TranslationScaffol(onNavigateBack: () -> Unit = {}) {

    val viewModel: GeminiViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopBarWithBack(
                text = "Word & Sentence Analysis",
                onBack = {
                    viewModel.resetToIdle()
                    onNavigateBack()
                }
            )
        },
        bottomBar = {},
    ) { innerPadding ->
        AnimatedContent(
            targetState = uiState,
            transitionSpec = {
                (fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 4 })
                    .togetherWith(
                        fadeOut(tween(300)) + slideOutVertically(tween(300)) { -it / 4 }
                    )
            },
            label = "screenTransition"
        ) { currentState ->
            when (currentState) {
                is UiState.Idle -> {
                    // ── Input Ekranı ──
                    SentenceInputScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .background(color = CardBackgroundDark)
                            .fillMaxSize(),
                        onSubmit = { sentence ->
                            viewModel.fetchResponse(sentence)
                        }
                    )
                }

                is UiState.Loading -> {
                    // ── Yükleniyor ──
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .background(color = CardBackgroundDark)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = GradientTealStart,
                                trackColor = CardBorderColor.copy(alpha = 0.3f)
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "Analyzing...",
                                color = SectionHeaderColor,
                                fontSize = 16.sp,
                                fontFamily = LexendFontFamily,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }

                is UiState.Success -> {
                    // ── Sonuç Ekranı ──
                    val translationData = currentState.translationData
                    val saveState by viewModel.saveState.collectAsState()
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .background(color = CardBackgroundDark)
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                    ) {
                        Spacer(modifier = Modifier.height(4.dp))
                        TransletedSentence(translationData = translationData)

                        // ── Save Button ──
                        SaveTranslationButton(
                            saveState = saveState,
                            onSave = { viewModel.saveTranslation() }
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                is UiState.Error -> {
                    // ── Hata Ekranı ──
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .background(color = CardBackgroundDark)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        ) {
                            Text(
                                text = "⚠",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Something went wrong",
                                color = White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = LexendFontFamily
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentState.message,
                                color = SectionHeaderColor,
                                fontSize = 14.sp,
                                fontFamily = LexendFontFamily,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            // Tekrar dene butonu
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(GradientTealStart, GradientTealEnd)
                                        )
                                    )
                                    .clickable {
                                        viewModel.resetToIdle()
                                    }
                                    .padding(horizontal = 32.dp, vertical = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Try Again",
                                    color = White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
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

// ──────────────────────────────────────
// Sentence Input Screen
// ──────────────────────────────────────
@Composable
fun SentenceInputScreen(
    modifier: Modifier = Modifier,
    onSubmit: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── Üst dekoratif ikon ──
        Box(
            modifier = Modifier
                .size(80.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = CircleShape,
                    ambientColor = GlowTeal,
                    spotColor = GlowTeal
                )
                .background(
                    Brush.linearGradient(
                        colors = listOf(GradientTealStart, GradientTealEnd)
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✦",
                fontSize = 32.sp,
                color = White
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ── Başlık ──
        Text(
            text = "Discover Translations",
            color = White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = LexendFontFamily,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ── Alt başlık ──
        Text(
            text = "Enter a word or sentence to analyze",
            color = SectionHeaderColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = LexendFontFamily,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // ── Input alanı ──
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = GlowTeal.copy(alpha = 0.3f),
                    spotColor = GlowTeal.copy(alpha = 0.3f)
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            GradientTealStart.copy(alpha = 0.5f),
                            GradientTealEnd.copy(alpha = 0.2f)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                ),
            color = CardBackgroundMedium,
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Type your sentence here...",
                            color = SubtleTextColor,
                            fontFamily = LexendFontFamily,
                            fontSize = 15.sp
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = White,
                        unfocusedTextColor = White.copy(alpha = 0.8f),
                        cursorColor = GradientTealStart,
                        focusedBorderColor = GradientTealStart.copy(alpha = 0.6f),
                        unfocusedBorderColor = CardBorderColor.copy(alpha = 0.4f),
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (inputText.isNotBlank()) {
                                keyboardController?.hide()
                                onSubmit(inputText.trim())
                            }
                        }
                    ),
                    maxLines = 4,
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ── Gönder butonu ──
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .shadow(
                            elevation = if (inputText.isNotBlank()) 8.dp else 0.dp,
                            shape = RoundedCornerShape(14.dp),
                            ambientColor = GlowTeal,
                            spotColor = GlowTeal
                        )
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (inputText.isNotBlank()) {
                                Brush.linearGradient(
                                    colors = listOf(GradientTealStart, GradientTealEnd)
                                )
                            } else {
                                Brush.linearGradient(
                                    colors = listOf(
                                        CardBorderColor.copy(alpha = 0.4f),
                                        CardBorderColor.copy(alpha = 0.3f)
                                    )
                                )
                            }
                        )
                        .clickable(
                            enabled = inputText.isNotBlank(),
                            onClick = {
                                keyboardController?.hide()
                                onSubmit(inputText.trim())
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Analyze",
                            color = if (inputText.isNotBlank()) White else SubtleTextColor,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = LexendFontFamily,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "→",
                            color = if (inputText.isNotBlank()) White else SubtleTextColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── İpucu text ──
        Text(
            text = "Try: \"En sevdiğin renk hangisi?\" or \"What is your favorite color?\"",
            color = SubtleTextColor.copy(alpha = 0.6f),
            fontSize = 13.sp,
            fontFamily = LexendFontFamily,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )
    }
}

// ──────────────────────────────────────
// AI Tutor – Glassmorphism Card
// ──────────────────────────────────────
@Composable
fun AiInfo() {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
        // Avatar with teal glow ring
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            // Glow ring
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(GlowTeal, Color.Transparent)
                        ),
                        shape = CircleShape
                    )
            )
            Image(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .size(48.dp)
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(GradientTealStart, GradientTealEnd)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ),
                painter = painterResource(id = R.drawable.ai),
                contentDescription = "AI Tutor Avatar",
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.wrapContentSize()
        ) {
            // Title with gradient text effect
            Text(
                modifier = Modifier.padding(bottom = 6.dp),
                text = stringResource(R.string.AiTextTitle),
                color = AiTutorTextColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = LexendFontFamily,
                letterSpacing = 0.5.sp
            )

            // Glassmorphism speech bubble
            Surface(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = GlowTeal,
                        spotColor = GlowTeal
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                GradientTealStart.copy(alpha = 0.4f),
                                GradientTealEnd.copy(alpha = 0.1f)
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ),
                color = CardSurfaceGlass,
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    GradientTealStart.copy(alpha = 0.08f),
                                    Color.Transparent
                                )
                            )
                        )
                ) {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = stringResource(R.string.AiText),
                        color = White.copy(alpha = 0.9f),
                        fontSize = 15.sp,
                        textAlign = TextAlign.Start,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = LexendFontFamily
                    )
                }
            }
        }
    }
}

// ──────────────────────────────────────
// Alternative Sentence Card (Glassmorphism)
// ──────────────────────────────────────
@Composable
fun TextSurface(color: Color, textColor: Color, text: String, isSelected: Boolean = false, onClick: () -> Unit = {}) {
    val borderBrush = if (isSelected) {
        Brush.linearGradient(
            colors = listOf(GradientGoldStart, GradientGoldEnd)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(CardBorderColor.copy(alpha = 0.5f), CardBorderColor.copy(alpha = 0.2f))
        )
    }

    val bgColor by animateColorAsState(
        targetValue = if (isSelected) SelectedTransleteContainer else UnSelectedTransleteContainer,
        animationSpec = tween(300),
        label = "textSurfaceBg"
    )

    val animTextColor by animateColorAsState(
        targetValue = if (isSelected) SelectedTransleteText else UnSelectedTransleteText,
        animationSpec = tween(300),
        label = "textSurfaceText"
    )

    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .wrapContentHeight()
            .fillMaxWidth()
            .shadow(
                elevation = if (isSelected) 6.dp else 2.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = if (isSelected) GlowGold else Color.Transparent,
                spotColor = if (isSelected) GlowGold else Color.Transparent
            )
            .border(
                width = 1.dp,
                brush = borderBrush,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() },
        color = bgColor,
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Selection indicator dot
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(GradientGoldStart, GradientGoldEnd)
                            ),
                            shape = CircleShape
                        )
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Text(
                text = text,
                color = animTextColor,
                fontSize = 16.sp,
                textAlign = TextAlign.Start,
                lineHeight = 22.sp,
                fontFamily = LexendFontFamily
            )
        }
    }
}

// ──────────────────────────────────────
// Translated Sentence – Main Card
// ──────────────────────────────────────
@Composable
fun TransletedSentence(translationData: TranslationData) {

    // Seçili alternatif cümle index'i
    var selectedTranslationIndex by remember { mutableIntStateOf(0) }
    // Seçili kelime index'i
    var selectedWordIndex by remember { mutableIntStateOf(0) }

    val translations = translationData.translations
    val selectedTranslation = translations.getOrNull(selectedTranslationIndex)
    val firstSentence = translations.firstOrNull()?.translatedSentence ?: ""
    val words = selectedTranslation?.words ?: emptyList()
    val wordTexts = words.map { it.word }

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
        Column {
            // ── Section: Translation ──
            Text(
                modifier = Modifier.padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 4.dp),
                text = "✦  " + stringResource(R.string.text),
                color = SectionHeaderColor,
                fontSize = 13.sp,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.SemiBold,
                fontFamily = LexendFontFamily,
                letterSpacing = 1.sp
            )

            Text(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                text = firstSentence,
                color = White,
                fontSize = 20.sp,
                textAlign = TextAlign.Start,
                lineHeight = 28.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = LexendFontFamily
            )

            GradientDivider(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )

            // ── Section: Alternative Sentences ──
            if (translations.size > 1) {
                Text(
                    modifier = Modifier.padding(start = 20.dp, top = 8.dp, end = 20.dp, bottom = 8.dp),
                    text = "✧  Alternative English Sentences",
                    color = SectionHeaderColor,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = LexendFontFamily,
                    letterSpacing = 1.sp
                )

                // API'den gelen tüm çeviri alternatiflerini göster
                translations.forEachIndexed { index, translation ->
                    TextSurface(
                        color = if (index == selectedTranslationIndex) SelectedTransleteContainer else UnSelectedTransleteContainer,
                        textColor = if (index == selectedTranslationIndex) SelectedTransleteText else UnSelectedTransleteText,
                        text = translation.translatedSentence,
                        isSelected = index == selectedTranslationIndex,
                        onClick = {
                            selectedTranslationIndex = index
                            selectedWordIndex = 0
                        }
                    )
                }

                GradientDivider(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
            }

            // ── Section: Word Breakdown ──
            if (wordTexts.isNotEmpty()) {
                Text(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    text = "✦  Breakdown for \"${selectedTranslation?.translatedSentence ?: ""}\"",
                    color = SectionHeaderColor,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = LexendFontFamily,
                    letterSpacing = 0.5.sp
                )

                FlowRowSimpleUsageExample(
                    list = wordTexts,
                    selectedIndex = selectedWordIndex,
                    onSelectedIndexChange = { selectedWordIndex = it }
                )

                GradientDivider(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )

                // ── Section: Word Detail ──
                val selectedWord = words.getOrNull(selectedWordIndex)
                if (selectedWord != null) {
                    WordExplanation(
                        word = selectedWord.lemma.ifBlank { selectedWord.word },
                        wordInformation = selectedWord.pos,
                        wordExplanation = selectedWord.meaningTr
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// ──────────────────────────────────────
// Word Chips – FlowRow
// ──────────────────────────────────────
@Composable
private fun FlowRowSimpleUsageExample(
    list: List<String>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit
) {
    FlowRow(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        list.forEachIndexed { index, word ->
            val isSelected = (index == selectedIndex)
            ModernWordChip(
                text = word,
                isSelected = isSelected,
                onSelect = {
                    if (selectedIndex == index) {
                        onSelectedIndexChange(-1)
                    } else {
                        onSelectedIndexChange(index)
                    }
                }
            )
        }
    }
}

// ──────────────────────────────────────
// Modern Word Chip (replaces FilterChipExample)
// ──────────────────────────────────────
@Composable
fun ModernWordChip(text: String, isSelected: Boolean, onSelect: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1.0f,
        animationSpec = tween(200),
        label = "chipScale"
    )

    val bgBrush = if (isSelected) {
        Brush.linearGradient(
            colors = listOf(ChipSelectedGradientStart, ChipSelectedGradientEnd)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(ChipUnselectedBg, ChipUnselectedBg)
        )
    }

    val borderBrush = if (isSelected) {
        Brush.linearGradient(
            colors = listOf(GradientGoldStart, GradientGoldEnd)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(CardBorderColor.copy(alpha = 0.4f), CardBorderColor.copy(alpha = 0.2f))
        )
    }

    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF1A1207) else Color(0xFFE5E7EB),
        animationSpec = tween(200),
        label = "chipTextColor"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .shadow(
                elevation = if (isSelected) 4.dp else 0.dp,
                shape = RoundedCornerShape(50),
                ambientColor = if (isSelected) GlowGold else Color.Transparent,
                spotColor = if (isSelected) GlowGold else Color.Transparent
            )
            .clip(RoundedCornerShape(50))
            .background(bgBrush)
            .border(
                width = 1.dp,
                brush = borderBrush,
                shape = RoundedCornerShape(50)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onSelect
            )
            .padding(horizontal = 18.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = LexendFontFamily,
            color = textColor,
            fontSize = 15.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

// Keep old name for backward compatibility
@Composable
fun FilterChipExample(text: String, isSelected: Boolean, onSelect: () -> Unit) {
    ModernWordChip(text = text, isSelected = isSelected, onSelect = onSelect)
}

// ──────────────────────────────────────
// Word Explanation – Premium Detail
// ──────────────────────────────────────
@Composable
fun WordExplanation(
    word: String,
    wordInformation: String,
    wordExplanation: String
) {
    // Word detail card with subtle background
    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = CardBorderColor.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            ),
        color = WordDetailBg,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Word title with gradient-like accent
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Accent bar
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(28.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(GradientTealStart, GradientTealEnd)
                            ),
                            shape = RoundedCornerShape(2.dp)
                        )
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = word,
                    color = White,
                    fontSize = 26.sp,
                    fontFamily = LexendFontFamily,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Part of speech badge
            if (wordInformation.isNotBlank()) {
                Surface(
                    modifier = Modifier.wrapContentSize(),
                    color = CardBackgroundDark.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        text = "📝  $wordInformation",
                        color = SubtleTextColor,
                        fontSize = 14.sp,
                        fontFamily = LexendFontFamily,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 0.3.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Turkish meaning
            if (wordExplanation.isNotBlank()) {
                Text(
                    text = wordExplanation,
                    color = Color(0xFFD1D5DB),
                    fontSize = 16.sp,
                    fontFamily = LexendFontFamily,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Start,
                    lineHeight = 24.sp
                )
            }
        }
    }
}

// ──────────────────────────────────────
// Save Translation Button
// ──────────────────────────────────────
@Composable
fun SaveTranslationButton(saveState: SaveState, onSave: () -> Unit) {
    val isSaved = saveState is SaveState.Saved
    val isSaving = saveState is SaveState.Saving

    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(52.dp)
            .shadow(
                elevation = if (!isSaved) 8.dp else 0.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = if (isSaved) Color.Transparent else GlowTeal,
                spotColor = if (isSaved) Color.Transparent else GlowTeal
            )
            .clip(RoundedCornerShape(14.dp))
            .background(
                when {
                    isSaved -> Brush.linearGradient(
                        colors = listOf(Color(0xFF059669), Color(0xFF10B981))
                    )
                    else -> Brush.linearGradient(
                        colors = listOf(GradientTealStart, GradientTealEnd)
                    )
                }
            )
            .clickable(
                enabled = !isSaved && !isSaving,
                onClick = onSave
            ),
        contentAlignment = Alignment.Center
    ) {
        when {
            isSaving -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Saving...",
                        color = White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = LexendFontFamily
                    )
                }
            }
            isSaved -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "✓",
                        color = White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Saved",
                        color = White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = LexendFontFamily
                    )
                }
            }
            else -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Save Translation",
                        color = White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = LexendFontFamily,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "💾",
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TranslationPreview() {
    TranslationScaffol(onNavigateBack = {})
}