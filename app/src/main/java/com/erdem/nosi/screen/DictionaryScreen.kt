package com.erdem.nosi.screen

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.erdem.nosi.ViewModels.AutoCompleteUiState
import com.erdem.nosi.ViewModels.AutoCompleteViewModel
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
import com.erdem.nosi.ui.theme.NosiTheme
import kotlinx.coroutines.delay
import kotlin.collections.emptyList

// ─── Mock kelime bankası — ileride API ile değiştirilebilir ──────────────────
private val SuggestionWordBank = listOf(
    "ability" to "noun",
    "about" to "preposition",
    "above" to "preposition",
    "abstract" to "adjective",
    "accept" to "verb",
    "account" to "noun",
    "achieve" to "verb",
    "across" to "preposition",
    "action" to "noun",
    "actually" to "adverb",
    "adapt" to "verb",
    "add" to "verb",
    "address" to "noun",
    "advance" to "verb",
    "adventure" to "noun",
    "afraid" to "adjective",
    "after" to "preposition",
    "again" to "adverb",
    "against" to "preposition",
    "age" to "noun",
    "agree" to "verb",
    "air" to "noun",
    "allow" to "verb",
    "already" to "adverb",
    "also" to "adverb",
    "always" to "adverb",
    "ambiguous" to "adjective",
    "analyze" to "verb",
    "ancient" to "adjective",
    "animal" to "noun",
    "appear" to "verb",
    "approach" to "verb",
    "argue" to "verb",
    "around" to "preposition",
    "ask" to "verb",
    "beautiful" to "adjective",
    "become" to "verb",
    "begin" to "verb",
    "believe" to "verb",
    "benefit" to "noun",
    "bold" to "adjective",
    "brave" to "adjective",
    "break" to "verb",
    "bright" to "adjective",
    "build" to "verb",
    "calm" to "adjective",
    "capable" to "adjective",
    "careful" to "adjective",
    "cause" to "noun",
    "change" to "verb",
    "character" to "noun",
    "choose" to "verb",
    "clear" to "adjective",
    "clever" to "adjective",
    "collect" to "verb",
    "complex" to "adjective",
    "concern" to "noun",
    "confident" to "adjective",
    "connect" to "verb",
    "consider" to "verb",
    "continue" to "verb",
    "control" to "verb",
    "create" to "verb",
    "curious" to "adjective",
    "dark" to "adjective",
    "decide" to "verb",
    "define" to "verb",
    "describe" to "verb",
    "develop" to "verb",
    "different" to "adjective",
    "discover" to "verb",
    "display" to "verb",
    "distinct" to "adjective",
    "dream" to "noun",
    "drive" to "verb",
    "dynamic" to "adjective",
    "eager" to "adjective",
    "earth" to "noun",
    "easy" to "adjective",
    "effect" to "noun",
    "effort" to "noun",
    "elegant" to "adjective",
    "emerge" to "verb",
    "emotion" to "noun",
    "enable" to "verb",
    "enjoy" to "verb",
    "enormous" to "adjective",
    "enter" to "verb",
    "establish" to "verb",
    "event" to "noun",
    "evolve" to "verb",
    "example" to "noun",
    "expand" to "verb",
    "explain" to "verb",
    "explore" to "verb",
    "express" to "verb",
    "fail" to "verb",
    "familiar" to "adjective",
    "far" to "adverb",
    "fast" to "adjective",
    "feature" to "noun",
    "feel" to "verb",
    "fierce" to "adjective",
    "figure" to "noun",
    "find" to "verb",
    "follow" to "verb",
    "foot" to "noun",
    "fool" to "noun",
    "football" to "noun",
    "footprint" to "noun",
    "footstep" to "noun",
    "force" to "noun",
    "forget" to "verb",
    "form" to "noun",
    "forward" to "adverb",
    "found" to "verb",
    "fragile" to "adjective",
    "free" to "adjective",
    "fresh" to "adjective",
    "friendly" to "adjective",
    "function" to "noun",
    "further" to "adverb",
    "gather" to "verb",
    "generate" to "verb",
    "give" to "verb",
    "grand" to "adjective",
    "great" to "adjective",
    "grow" to "verb",
    "happy" to "adjective",
    "hard" to "adjective",
    "help" to "verb",
    "high" to "adjective",
    "hold" to "verb",
    "honest" to "adjective",
    "imagine" to "verb",
    "impact" to "noun",
    "improve" to "verb",
    "include" to "verb",
    "increase" to "verb",
    "indicate" to "verb",
    "inspire" to "verb",
    "interest" to "noun",
    "involve" to "verb",
    "join" to "verb",
    "journey" to "noun",
    "keep" to "verb",
    "kind" to "adjective",
    "knowledge" to "noun",
    "lead" to "verb",
    "learn" to "verb",
    "level" to "noun",
    "light" to "noun",
    "likely" to "adverb",
    "limit" to "noun",
    "listen" to "verb",
    "logical" to "adjective",
    "long" to "adjective",
    "look" to "verb",
    "love" to "noun",
    "make" to "verb",
    "manage" to "verb",
    "matter" to "noun",
    "mean" to "verb",
    "measure" to "verb",
    "meet" to "verb",
    "method" to "noun",
    "mind" to "noun",
    "mistake" to "noun",
    "model" to "noun",
    "move" to "verb",
    "natural" to "adjective",
    "near" to "adjective",
    "need" to "verb",
    "observe" to "verb",
    "occur" to "verb",
    "offer" to "verb",
    "open" to "adjective",
    "order" to "noun",
    "own" to "verb",
    "pass" to "verb",
    "pattern" to "noun",
    "perform" to "verb",
    "persist" to "verb",
    "plan" to "noun",
    "point" to "noun",
    "powerful" to "adjective",
    "practice" to "noun",
    "precise" to "adjective",
    "prepare" to "verb",
    "present" to "verb",
    "problem" to "noun",
    "process" to "noun",
    "produce" to "verb",
    "provide" to "verb",
    "purpose" to "noun",
    "push" to "verb",
    "question" to "noun",
    "quick" to "adjective",
    "quiet" to "adjective",
    "reach" to "verb",
    "realize" to "verb",
    "reduce" to "verb",
    "reflect" to "verb",
    "relate" to "verb",
    "remain" to "verb",
    "remember" to "verb",
    "resolve" to "verb",
    "result" to "noun",
    "reveal" to "verb",
    "rich" to "adjective",
    "rise" to "verb",
    "robust" to "adjective",
    "run" to "verb",
    "safe" to "adjective",
    "search" to "verb",
    "see" to "verb",
    "sense" to "noun",
    "sharp" to "adjective",
    "show" to "verb",
    "simple" to "adjective",
    "skill" to "noun",
    "slow" to "adjective",
    "solve" to "verb",
    "speak" to "verb",
    "start" to "verb",
    "stay" to "verb",
    "still" to "adverb",
    "story" to "noun",
    "strong" to "adjective",
    "success" to "noun",
    "suggest" to "verb",
    "support" to "verb",
    "take" to "verb",
    "think" to "verb",
    "time" to "noun",
    "together" to "adverb",
    "transform" to "verb",
    "true" to "adjective",
    "trust" to "noun",
    "try" to "verb",
    "understand" to "verb",
    "unique" to "adjective",
    "use" to "verb",
    "value" to "noun",
    "vast" to "adjective",
    "view" to "noun",
    "vivid" to "adjective",
    "want" to "verb",
    "wise" to "adjective",
    "wonder" to "verb",
    "work" to "verb",
    "world" to "noun",
    "write" to "verb"
)

// ─── Öneri filtresi ──────────────────────────────────────────────────────────
private fun getSuggestions(query: String, limit: Int = 6): List<Pair<String, String>> {
    if (query.isBlank()) return emptyList()
    val q = query.lowercase().trim()

    return SuggestionWordBank
        .filter { it.first.startsWith(q) && it.first != q }
        .sortedBy { it.first }
        .take(limit)
}

// ─── Ana ekran ────────────────────────────────────────────────────────────────
@Composable
fun DictionaryScreen(onNavigateBack: () -> Unit = {}) {

    var currentState by remember { mutableIntStateOf(0) }
    var saveState by remember { mutableStateOf("IDLE") }
    var searchedWord by remember { mutableStateOf("") }

    val viewModel: AutoCompleteViewModel = viewModel()

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
                onBack = {
                    if (currentState != 0) {
                        currentState = 0
                        saveState = "IDLE"
                    } else {
                        onNavigateBack()
                    }
                }
            )
        },
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentState,
            transitionSpec = {
                (fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 4 })
                    .togetherWith(
                        fadeOut(tween(300)) + slideOutVertically(tween(300)) { -it / 4 }
                    )
            },
            label = "DictionaryState"
        ) { state ->
            when (state) {
                0 -> {
                    DictionaryInputScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .background(color = CardBackgroundDark)
                            .fillMaxSize(),
                        onSubmit = { word ->
                            searchedWord = word
                            currentState = 1
                        },
                        viewModel = viewModel
                    )
                }

                1 -> {
                    LaunchedEffect(Unit) {
                        delay(1200)
                        currentState = 2
                    }
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
                                text = "Searching for \"$searchedWord\"...",
                                color = SectionHeaderColor,
                                fontSize = 16.sp,
                                fontFamily = LexendFontFamily
                            )
                        }
                    }
                }

                2 -> {

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
}

// ─── Input ekranı (autocomplete dahil) ───────────────────────────────────────
@Composable
fun DictionaryInputScreen(
    modifier: Modifier = Modifier,
    onSubmit: (String) -> Unit,
    viewModel: AutoCompleteViewModel
) {
    var inputText by remember { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(-1) }
    val keyboardController = LocalSoftwareKeyboardController.current

    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // Her input değişiminde seçili index sıfırlanır
    val suggestions = when(uiState) {
        is AutoCompleteUiState.Idle -> emptyList()
        is AutoCompleteUiState.Loading -> (uiState as AutoCompleteUiState.Loading).oldSuggestion
        is AutoCompleteUiState.Success -> (uiState as AutoCompleteUiState.Success).suggestions
        is AutoCompleteUiState.Error -> emptyList()
    }

    val showDropdown = isFocused && suggestions.isNotEmpty()
    Log.e("deneme", "suggestions: $suggestions")

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
                // zIndex: dropdown TextField'ın üstünde görünsün diye
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

                                /*// Part-of-speech badge
                                Surface(
                                    color = CardBorderColor.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        modifier = Modifier.padding(
                                            horizontal = 8.dp,
                                            vertical = 3.dp
                                        ),
                                        text = type,
                                        color = SubtleTextColor,
                                        fontSize = 11.sp,
                                        fontFamily = LexendFontFamily
                                    )
                                }*/
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

// ─── Result Card (değişmedi) ──────────────────────────────────────────────────
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
fun DictionaryScreenPreview() {
    NosiTheme {
        DictionaryScreen()
    }
}