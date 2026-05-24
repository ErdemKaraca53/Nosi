package com.erdem.nosi.screen


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.erdem.nosi.R
import com.erdem.nosi.model.MockData
import com.erdem.nosi.model.TranslationData
import com.erdem.nosi.ui.theme.CardBackgroundDark
import com.erdem.nosi.ui.theme.CardBackgroundMedium
import com.erdem.nosi.ui.theme.CardBorderColor
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
import kotlinx.coroutines.delay

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
// Main Scaffold – Instant Live Results
// ──────────────────────────────────────


@Composable
fun TranslationScaffol(onNavigateBack: () -> Unit = {}) {

    var inputText by remember { mutableStateOf("") }
    var saveState by remember { mutableStateOf("IDLE") }
    val hasInput = inputText.isNotBlank()

    // Mock: gerçek implementasyonda burada API/DB çağrısı olacak
    val translationData = if (hasInput) MockData.sampleTranslationData else null

    Scaffold(
        topBar = {
            TopBarWithBack(
                text = "Word & Sentence Analysis",
                onBack = { onNavigateBack() }
            )
        },
        bottomBar = {},
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .background(color = CardBackgroundDark)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── Compact Input Card ──
            LiveInputCard(
                inputText = inputText,
                onInputChange = {
                    inputText = it
                    saveState = "IDLE"
                },
                onClear = {
                    inputText = ""
                    saveState = "IDLE"
                }
            )

            // ── Instant Results ──
            AnimatedVisibility(
                visible = translationData != null,
                enter = fadeIn(tween(300)) + expandVertically(tween(350)),
                exit = fadeOut(tween(200)) + shrinkVertically(tween(250))
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Spacer(modifier = Modifier.height(4.dp))
                    translationData?.let { data ->
                        TransletedSentence(translationData = data)

                        SaveTranslationButton(
                            saveState = saveState,
                            onSave = { saveState = "SAVING" }
                        )

                        LaunchedEffect(saveState) {
                            if (saveState == "SAVING") {
                                delay(1000)
                                saveState = "SUCCESS"
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // ── Empty State ──
            if (!hasInput) {
                EmptyStateHint()
            }
        }
    }
}

// ──────────────────────────────────────
// Live Input Card – Compact top-aligned
// ──────────────────────────────────────
@Composable
fun LiveInputCard(
    inputText: String,
    onInputChange: (String) -> Unit,
    onClear: () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp)
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
            modifier = Modifier.padding(16.dp)
        ) {
            // ── Başlık satırı ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(GradientTealStart, GradientTealEnd)
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "✦", fontSize = 16.sp, color = White)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Instant Analysis",
                        color = White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = LexendFontFamily
                    )
                    Text(
                        text = "Results appear as you type",
                        color = SectionHeaderColor,
                        fontSize = 12.sp,
                        fontFamily = LexendFontFamily
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ── Input alanı ──
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Type a word or sentence...",
                        color = SubtleTextColor,
                        fontFamily = LexendFontFamily,
                        fontSize = 15.sp
                    )
                },
                trailingIcon = {
                    if (inputText.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(CardBorderColor.copy(alpha = 0.5f))
                                .clickable { onClear() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "✕",
                                color = White.copy(alpha = 0.8f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
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
                    imeAction = ImeAction.Done
                ),
                maxLines = 4,
                minLines = 1
            )

            // ── Karakter sayacı ──
            if (inputText.isNotBlank()) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    text = "${inputText.trim().split("\\s+".toRegex()).size} words",
                    color = GradientTealStart.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    fontFamily = LexendFontFamily,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

// ──────────────────────────────────────
// Empty State Hint
// ──────────────────────────────────────
@Composable
fun EmptyStateHint() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── Dekoratif ikon ──
        Box(
            modifier = Modifier
                .size(64.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = CircleShape,
                    ambientColor = GlowTeal,
                    spotColor = GlowTeal
                )
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            GradientTealStart.copy(alpha = 0.15f),
                            GradientTealEnd.copy(alpha = 0.08f)
                        )
                    ),
                    shape = CircleShape
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            GradientTealStart.copy(alpha = 0.4f),
                            GradientTealEnd.copy(alpha = 0.2f)
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🔍", fontSize = 28.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Start typing to see results",
            color = White.copy(alpha = 0.7f),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = LexendFontFamily
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Try: \"En sevdiğin renk hangisi?\"\nor \"What is your favorite color?\"",
            color = SubtleTextColor.copy(alpha = 0.6f),
            fontSize = 13.sp,
            fontFamily = LexendFontFamily,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
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
fun TransletedSentence(
    translationData: TranslationData,
    isPremium: Boolean = false,
    onUpgradeClick: () -> Unit = {}
) {

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
            // ── Section: Translation (FREE) ──
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

            // ── Premium Sections ──
            if (isPremium) {
                // ── Section: Alternative Sentences (PREMIUM) ──
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

                // ── Section: Word Breakdown (PREMIUM) ──
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

                    // ── Section: Word Detail (PREMIUM) ──
                    val selectedWord = words.getOrNull(selectedWordIndex)
                    if (selectedWord != null) {
                        WordExplanation(
                            word = selectedWord.lemma.ifBlank { selectedWord.word },
                            wordInformation = selectedWord.pos,
                            wordExplanation = selectedWord.meaningTr
                        )
                    }
                }
            } else {
                // ── Locked Premium Preview ──
                PremiumLockedOverlay(onUpgradeClick = onUpgradeClick)
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// ──────────────────────────────────────
// Premium Locked Overlay
// ──────────────────────────────────────
@Composable
fun PremiumLockedOverlay(onUpgradeClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // ── Blurred preview of premium content ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackgroundDark.copy(alpha = 0.4f))
        ) {
            // Fake alternative sentences (blurred look)
            repeat(2) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .fillMaxWidth()
                        .height(20.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(CardBorderColor.copy(alpha = 0.25f))
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Fake word chips
            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(4) {
                    Box(
                        modifier = Modifier
                            .height(28.dp)
                            .width((40 + it * 12).dp)
                            .clip(RoundedCornerShape(50))
                            .background(CardBorderColor.copy(alpha = 0.2f))
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            // Fake word detail
            Box(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBorderColor.copy(alpha = 0.15f))
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // ── Lock overlay on top ──
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(16.dp))
                .background(CardBackgroundDark.copy(alpha = 0.75f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Lock icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            ambientColor = GlowGold,
                            spotColor = GlowGold
                        )
                        .background(
                            Brush.linearGradient(
                                colors = listOf(GradientGoldStart, GradientGoldEnd)
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🔒", fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Premium Feature",
                    color = White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = LexendFontFamily
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Unlock word breakdown,\nalternatives & more",
                    color = SubtleTextColor,
                    fontSize = 13.sp,
                    fontFamily = LexendFontFamily,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Upgrade button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(GradientGoldStart, GradientGoldEnd)
                            )
                        )
                        .clickable { onUpgradeClick() }
                        .padding(horizontal = 28.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Upgrade Now ✦",
                        color = Color(0xFF1A1207),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = LexendFontFamily,
                        letterSpacing = 0.5.sp
                    )
                }
            }
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
fun SaveTranslationButton(saveState: String, onSave: () -> Unit) {
    val isSaved = saveState == "SUCCESS"
    val isSaving = saveState == "SAVING"

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