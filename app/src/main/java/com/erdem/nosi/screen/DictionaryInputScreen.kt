package com.erdem.nosi.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.erdem.nosi.ViewModels.AutoCompleteUiState
import com.erdem.nosi.ViewModels.AutoCompleteViewModel
import com.erdem.nosi.ui.theme.CardBackgroundDark
import com.erdem.nosi.ui.theme.CardBackgroundMedium
import com.erdem.nosi.ui.theme.CardBorderColor
import com.erdem.nosi.ui.theme.GradientGoldEnd
import com.erdem.nosi.ui.theme.GradientGoldStart
import com.erdem.nosi.ui.theme.NosiTheme
import com.erdem.nosi.ui.theme.SectionHeaderColor
import com.erdem.nosi.ui.theme.SubtleTextColor
import com.erdem.nosi.ui.theme.White

// ─── Dictionary Input Ekranı ─────────────────────────────────────────────────
@Composable
fun DictionaryInputScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToResult: (String) -> Unit = {}
) {
    val viewModel: AutoCompleteViewModel = viewModel()

    Scaffold(
        topBar = {
            TopBarWithBack(
                text = "Dictionary Lookup",
                onBack = { onNavigateBack() }
            )
        },
    ) { innerPadding ->
        DictionaryInputContent(
            modifier = Modifier
                .padding(innerPadding)
                .background(color = CardBackgroundDark)
                .fillMaxSize(),
            onSubmit = { word -> onNavigateToResult(word) },
            viewModel = viewModel
        )
    }
}

// ─── Input içeriği (autocomplete dahil) ──────────────────────────────────────
@Composable
private fun DictionaryInputContent(
    modifier: Modifier = Modifier,
    onSubmit: (String) -> Unit,
    viewModel: AutoCompleteViewModel
) {
    var isFocused by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(-1) }
    val keyboardController = LocalSoftwareKeyboardController.current

    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val suggestions = when (uiState) {
        is AutoCompleteUiState.Idle -> emptyList()
        is AutoCompleteUiState.Loading -> (uiState as AutoCompleteUiState.Loading).oldSuggestion
        is AutoCompleteUiState.Success -> (uiState as AutoCompleteUiState.Success).suggestions
        is AutoCompleteUiState.Error -> emptyList()
    }

    val showDropdown = isFocused && suggestions.isNotEmpty()
    // Header gizlenme koşulu: input dolu ise gizle, boşsa göster
    val showHeader = query.isBlank()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .imePadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── Header bölümü (animasyonlu) ──────────────────────────────────
        AnimatedVisibility(
            visible = showHeader,
            enter = expandVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                expandFrom = Alignment.Top
            ) + fadeIn(tween(300)),
            exit = shrinkVertically(
                animationSpec = tween(250),
                shrinkTowards = Alignment.Top
            ) + fadeOut(tween(200))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(48.dp))
                // ── İkon ────────────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .shadow(
                            elevation = 16.dp,
                            shape = CircleShape,
                            ambientColor = GradientGoldStart.copy(alpha = 0.5f),
                            spotColor = GradientGoldStart.copy(alpha = 0.5f)
                        )
                        .background(
                            Brush.linearGradient(listOf(GradientGoldStart, GradientGoldEnd)),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "📖", fontSize = 32.sp)
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Word Dictionary",
                    color = White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = LexendFontFamily,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Enter a single word to define and explore synonyms",
                    color = SectionHeaderColor,
                    fontSize = 15.sp,
                    fontFamily = LexendFontFamily,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        // Header gizliyken üstte küçük boşluk bırak
        if (!showHeader) {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ── Kart (TextField + Dropdown + Buton) ─────────────────────────────
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = SubtleTextColor.copy(alpha = 0.2f),
                    spotColor = SubtleTextColor.copy(alpha = 0.2f)
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            GradientGoldStart.copy(alpha = 0.5f),
                            GradientGoldEnd.copy(alpha = 0.2f)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                ),
            color = CardBackgroundMedium,
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                // ── TextField ────────────────────────────────────────────────
                Box(modifier = Modifier.zIndex(1f)) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = viewModel::onSearchQueryChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { isFocused = it.isFocused },
                        placeholder = {
                            Text(
                                text = "Type a word...",
                                color = SubtleTextColor,
                                fontFamily = LexendFontFamily,
                                fontSize = 15.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = White,
                            unfocusedTextColor = White.copy(alpha = 0.8f),
                            cursorColor = GradientGoldStart,
                            focusedBorderColor = GradientGoldStart.copy(alpha = 0.6f),
                            unfocusedBorderColor = CardBorderColor.copy(alpha = 0.4f),
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                val word = when {
                                    selectedIndex >= 0 && selectedIndex < suggestions.size ->
                                        suggestions[selectedIndex].word
                                    query.isNotBlank() -> query.trim()
                                    else -> return@KeyboardActions
                                }
                                keyboardController?.hide()
                                onSubmit(word)
                            }
                        ),
                        singleLine = true
                    )
                }

                // ── Autocomplete Dropdown ────────────────────────────────────
                AnimatedVisibility(
                    visible = showDropdown,
                    enter = expandVertically(tween(200)) + fadeIn(tween(200)),
                    exit = shrinkVertically(tween(150)) + fadeOut(tween(150))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(
                                width = 1.dp,
                                color = CardBorderColor.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .background(CardBackgroundDark)
                    ) {
                        suggestions.forEachIndexed { index, (word) ->
                            val isHighlighted = index == selectedIndex

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) {
                                        keyboardController?.hide()
                                        onSubmit(word)
                                    }
                                    .background(
                                        if (isHighlighted)
                                            GradientGoldStart.copy(alpha = 0.12f)
                                        else
                                            Color.Transparent
                                    )
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Prefix bold, geri kalan muted
                                Text(
                                    text = buildAnnotatedString {
                                        val prefix = query.lowercase()
                                        withStyle(
                                            SpanStyle(
                                                color = White,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        ) {
                                            append(word.take(prefix.length))
                                        }
                                        withStyle(SpanStyle(color = SubtleTextColor)) {
                                            append(word.drop(prefix.length))
                                        }
                                    },
                                    fontSize = 15.sp,
                                    fontFamily = LexendFontFamily
                                )
                            }

                            // Divider (son öğe hariç)
                            if (index < suggestions.lastIndex) {
                                HorizontalDivider(
                                    color = CardBorderColor.copy(alpha = 0.3f),
                                    thickness = 0.5.dp
                                )
                            }
                        }

                        // ── Klavye hint ──────────────────────────────────────
                        HorizontalDivider(
                            color = CardBorderColor.copy(alpha = 0.4f),
                            thickness = 0.5.dp
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "tap to select",
                                color = SubtleTextColor.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                fontFamily = LexendFontFamily
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── Look Up butonu ────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (query.isNotBlank()) {
                                Brush.linearGradient(
                                    colors = listOf(GradientGoldStart, GradientGoldEnd)
                                )
                            } else {
                                Brush.linearGradient(
                                    colors = listOf(CardBorderColor, CardBorderColor)
                                )
                            }
                        )
                        .clickable(
                            enabled = query.isNotBlank(),
                            onClick = {
                                keyboardController?.hide()
                                onSubmit(query.trim())
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Look Up",
                        color = if (query.isNotBlank()) White else SubtleTextColor,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = LexendFontFamily
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DictionaryInputScreenPreview() {
    NosiTheme {
        DictionaryInputScreen()
    }
}
