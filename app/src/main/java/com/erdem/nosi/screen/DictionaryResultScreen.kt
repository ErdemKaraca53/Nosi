package com.erdem.nosi.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.erdem.nosi.ui.theme.NosiTheme
import com.erdem.nosi.model.DictionaryResult
import com.erdem.nosi.model.MockData
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
    // 0 = loading, 1 = sonuç göster
    var screenState by remember { mutableIntStateOf(1) }
    var saveState by remember { mutableStateOf("IDLE") }

    LaunchedEffect(saveState) {
        if (saveState == "SAVING") {
            delay(1000)
            saveState = "SUCCESS"
        }
    }

    // Loading → sonuç geçişi
    LaunchedEffect(Unit) {
        delay(1200)
        screenState = 1
    }

    Scaffold(
        topBar = {
            TopBarWithBack(
                text = "Dictionary Lookup",
                onBack = { onNavigateBack() }
            )
        },
    ) { innerPadding ->
        when (screenState) {
            0 -> {
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

            1 -> {
                // ── Sonuç State ──
                val result = MockData.sampleDictionaryResult
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .background(color = CardBackgroundDark)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    Spacer(modifier = Modifier.height(4.dp))
                    DictionaryResultCard(result = result)
                    SaveTranslationButton(
                        saveState = saveState,
                        onSave = { saveState = "SAVING" }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

// ─── Result Card ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DictionaryResultCard(result: DictionaryResult) {
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = result.word,
                    color = White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = LexendFontFamily
                )
                Surface(
                    color = GradientGoldStart.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        text = result.type,
                        color = GradientGoldStart,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = LexendFontFamily
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            GradientDivider()
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "DEFINITIONS",
                color = SectionHeaderColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = LexendFontFamily,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            result.definitions.forEachIndexed { index, def ->
                Row(modifier = Modifier.padding(bottom = 6.dp)) {
                    Text(
                        text = "${index + 1}.",
                        color = GradientGoldStart,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = def,
                        color = White.copy(alpha = 0.9f),
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        fontFamily = LexendFontFamily
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "SYNONYMS",
                color = SectionHeaderColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = LexendFontFamily,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                result.synonyms.forEach { synonym ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
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
                                        GradientTealStart.copy(alpha = 0.6f),
                                        GradientTealEnd.copy(alpha = 0.3f)
                                    )
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = synonym,
                            color = White,
                            fontSize = 14.sp,
                            fontFamily = LexendFontFamily
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            GradientDivider()
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "EXAMPLES",
                color = SectionHeaderColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = LexendFontFamily,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            result.exampleSentences.forEach { example ->
                Row(
                    modifier = Modifier.padding(bottom = 8.dp),
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
                    Text(
                        text = "\"$example\"",
                        color = SubtleTextColor,
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic,
                        fontFamily = LexendFontFamily,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DictionaryResultScreenPreview() {
    NosiTheme {
        DictionaryResultScreen(word = "example")
    }
}

