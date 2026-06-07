package com.erdem.nosi.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.erdem.nosi.ViewModels.DatabaseViewModel
import com.erdem.nosi.database.SavedSentenceEntity
import com.erdem.nosi.model.StudyWord
import com.erdem.nosi.ui.theme.CardBackgroundDark
import com.erdem.nosi.ui.theme.CardBackgroundMedium
import com.erdem.nosi.ui.theme.CardBorderColor
import com.erdem.nosi.ui.theme.GradientGoldStart
import com.erdem.nosi.ui.theme.GradientTealEnd
import com.erdem.nosi.ui.theme.GradientTealStart
import com.erdem.nosi.ui.theme.NosiTheme
import com.erdem.nosi.ui.theme.SectionHeaderColor
import com.erdem.nosi.ui.theme.SubtleTextColor
import com.erdem.nosi.ui.theme.White

// ──────────────────────────────────────
// Collection Detail Screen — Stateful
// ──────────────────────────────────────
@Composable
fun CollectionDetailScreen(
    collectionId: Long,
    onNavigateBack: () -> Unit = {},
    onNavigateToStudy: () -> Unit = {}
) {
    val dbViewModel: DatabaseViewModel = viewModel()

    val words by dbViewModel.getStudyWordsForList(collectionId)
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val sentences by dbViewModel.getSentencesForList(collectionId)
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val allLists by dbViewModel.allLists.collectAsStateWithLifecycle()

    val list = allLists.find { it.id == collectionId }
    val title = list?.let { "${it.emoji}  ${it.name}" } ?: "Collection"

    CollectionDetailContent(
        title = title,
        words = words,
        sentences = sentences,
        onNavigateBack = onNavigateBack,
        onNavigateToStudy = onNavigateToStudy,
        onDeleteWord = { dbViewModel.deleteWord(it.id) },
        onDeleteSentence = { dbViewModel.deleteSentence(it.id) }
    )
}

// ──────────────────────────────────────
// Collection Detail — Stateless gövde
// ──────────────────────────────────────
@Composable
private fun CollectionDetailContent(
    title: String,
    words: List<StudyWord>,
    sentences: List<SavedSentenceEntity>,
    onNavigateBack: () -> Unit,
    onNavigateToStudy: () -> Unit,
    onDeleteWord: (StudyWord) -> Unit,
    onDeleteSentence: (SavedSentenceEntity) -> Unit
) {
    // Silme onayı bekleyenler
    var pendingDeleteWord by remember { mutableStateOf<StudyWord?>(null) }
    var pendingDeleteSentence by remember { mutableStateOf<SavedSentenceEntity?>(null) }

    pendingDeleteWord?.let { target ->
        AlertDialog(
            onDismissRequest = { pendingDeleteWord = null },
            containerColor = CardBackgroundMedium,
            title = {
                Text(
                    text = "Delete word?",
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = LexendFontFamily
                )
            },
            text = {
                Text(
                    text = "\"${target.word}\" will be removed from this list.",
                    color = SubtleTextColor,
                    fontFamily = LexendFontFamily
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteWord(target)
                    pendingDeleteWord = null
                }) {
                    Text(text = "Delete", color = GradientGoldStart, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteWord = null }) {
                    Text(text = "Cancel", color = SubtleTextColor)
                }
            }
        )
    }

    pendingDeleteSentence?.let { target ->
        AlertDialog(
            onDismissRequest = { pendingDeleteSentence = null },
            containerColor = CardBackgroundMedium,
            title = {
                Text(
                    text = "Delete sentence?",
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = LexendFontFamily
                )
            },
            text = {
                Text(
                    text = "This sentence will be removed from the list.",
                    color = SubtleTextColor,
                    fontFamily = LexendFontFamily
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteSentence(target)
                    pendingDeleteSentence = null
                }) {
                    Text(text = "Delete", color = GradientGoldStart, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteSentence = null }) {
                    Text(text = "Cancel", color = SubtleTextColor)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopBarWithBack(
                text = title,
                onBack = onNavigateBack
            )
        }
    ) { innerPadding ->
        if (words.isEmpty() && sentences.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .background(color = CardBackgroundDark)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(text = "📭", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Nothing saved yet",
                        color = White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = LexendFontFamily
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Save words or translations to this list",
                        color = SubtleTextColor,
                        fontSize = 14.sp,
                        fontFamily = LexendFontFamily
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .background(color = CardBackgroundDark)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                // ── Cümleler Bölümü ──
                if (sentences.isNotEmpty()) {
                    SectionLabel(text = "📝  Sentences (${sentences.size})")
                    sentences.forEach { sentence ->
                        SentenceCard(
                            sentence = sentence,
                            onLongPress = { pendingDeleteSentence = sentence }
                        )
                    }
                }

                // ── Kelimeler Bölümü ──
                if (words.isNotEmpty()) {
                    SectionLabel(text = "📚  Words (${words.size})")

                    // Çalışma butonu
                    StudyButton(onClick = onNavigateToStudy)

                    // İlerleme
                    val learned = words.count { it.isKnown }
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(CardBorderColor.copy(alpha = 0.3f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(learned.toFloat() / words.size)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        Brush.linearGradient(listOf(GradientTealStart, GradientTealEnd))
                                    )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "✓ $learned of ${words.size} learned",
                            color = GradientTealStart,
                            fontSize = 12.sp,
                            fontFamily = LexendFontFamily,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    words.forEach { word ->
                        SavedWordCard(
                            word = word,
                            onLongPress = { pendingDeleteWord = word }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ──────────────────────────────────────
// Bölüm Başlığı
// ──────────────────────────────────────
@Composable
private fun SectionLabel(text: String) {
    Text(
        modifier = Modifier.padding(horizontal = 20.dp),
        text = text,
        color = White,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = LexendFontFamily
    )
}

// ──────────────────────────────────────
// Kayıtlı Cümle Kartı (Türkçe → İngilizce)
// ──────────────────────────────────────
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SentenceCard(
    sentence: SavedSentenceEntity,
    onLongPress: () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        GradientTealStart.copy(alpha = 0.3f),
                        GradientTealEnd.copy(alpha = 0.1f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .combinedClickable(
                onClick = {},
                onLongClick = onLongPress
            ),
        color = CardBackgroundMedium,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Kaynak (Türkçe)
            if (sentence.sourceText.isNotBlank()) {
                Text(
                    text = sentence.sourceText,
                    color = SectionHeaderColor,
                    fontSize = 13.sp,
                    fontFamily = LexendFontFamily,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            // Çeviri (İngilizce)
            Text(
                text = sentence.translatedText,
                color = White,
                fontSize = 16.sp,
                fontFamily = LexendFontFamily,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 22.sp
            )
        }
    }
}

// ──────────────────────────────────────
// Study Button
// ──────────────────────────────────────
@Composable
private fun StudyButton(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(56.dp),
        color = GradientTealStart,
        shape = RoundedCornerShape(14.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "🃏", fontSize = 22.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Study Flashcards",
                color = White,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = LexendFontFamily,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "→",
                color = White.copy(alpha = 0.7f),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ──────────────────────────────────────
// Saved Word Card (genişleyebilir)
// ──────────────────────────────────────
@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
private fun SavedWordCard(
    word: StudyWord,
    onLongPress: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        GradientTealStart.copy(alpha = 0.3f),
                        GradientTealEnd.copy(alpha = 0.1f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .combinedClickable(
                onClick = { isExpanded = !isExpanded },
                onLongClick = onLongPress
            ),
        color = CardBackgroundMedium,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Baş harf dairesi
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            Brush.linearGradient(listOf(GradientTealStart, GradientTealEnd)),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = word.word.take(1).uppercase(),
                        color = White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = LexendFontFamily
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = word.word.replaceFirstChar { it.uppercase() },
                            color = White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = LexendFontFamily
                        )
                        // Türkçe anlam (varsa) kelimenin yanında
                        if (word.meaningTr.isNotBlank()) {
                            Text(
                                text = "  ·  ${word.meaningTr}",
                                color = SectionHeaderColor,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = LexendFontFamily
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = word.partOfSpeech,
                        color = GradientTealStart,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = LexendFontFamily
                    )
                }

                // "Öğrenildi" rozeti
                if (word.isKnown) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(GradientTealStart.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "✓ Known",
                            color = GradientTealStart,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = LexendFontFamily
                        )
                    }
                }

                Text(
                    text = if (isExpanded) "▲" else "▼",
                    color = GradientTealStart,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // İlk tanım (her zaman görünür, kısa önizleme)
            if (word.primaryDefinition.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = word.primaryDefinition,
                    color = White.copy(alpha = 0.85f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontFamily = LexendFontFamily,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 2
                )
            }

            // Genişleyen detay: kalan tanımlar + eşanlamlı/zıt anlamlı
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    // 2. tanımdan itibaren numaralı liste
                    if (word.definitions.size > 1) {
                        Spacer(modifier = Modifier.height(12.dp))
                        GradientDivider()
                        Spacer(modifier = Modifier.height(12.dp))
                        word.definitions.drop(1).forEachIndexed { index, def ->
                            if (index > 0) Spacer(modifier = Modifier.height(10.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "${index + 2}.",
                                    color = GradientTealStart,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = LexendFontFamily,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(
                                    text = def,
                                    color = White.copy(alpha = 0.85f),
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    fontFamily = LexendFontFamily
                                )
                            }
                        }
                    }

                    if (word.synonyms.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        ChipGroup(label = "Syn", items = word.synonyms, accent = GradientTealStart)
                    }
                    if (word.antonyms.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        ChipGroup(label = "Ant", items = word.antonyms, accent = GradientGoldStart)
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────
// Eşanlamlı / Zıt anlamlı chip grubu
// ──────────────────────────────────────
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChipGroup(
    label: String,
    items: List<String>,
    accent: androidx.compose.ui.graphics.Color
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Text(
            text = label,
            color = accent,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = LexendFontFamily,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(top = 6.dp, end = 10.dp)
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f)
        ) {
            items.forEach { item ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(accent.copy(alpha = 0.12f))
                        .border(
                            width = 1.dp,
                            color = accent.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = item,
                        color = White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        fontFamily = LexendFontFamily
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CollectionDetailScreenPreview() {
    val sampleWords = listOf(
        StudyWord(
            id = 1L,
            word = "run",
            partOfSpeech = "verb",
            meaningTr = "koşmak",
            definitions = listOf(
                "Move at a speed faster than a walk.",
                "To manage or be in charge of."
            ),
            synonyms = listOf("sprint", "dash", "operate"),
            antonyms = listOf("walk", "crawl"),
            masteryLevel = 2
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
    val sampleSentences = listOf(
        SavedSentenceEntity(
            id = 1L,
            listId = 1L,
            sourceText = "Bugün hava çok güzel.",
            translatedText = "The weather is very nice today."
        )
    )
    NosiTheme {
        CollectionDetailContent(
            title = "📚  Favorites",
            words = sampleWords,
            sentences = sampleSentences,
            onNavigateBack = {},
            onNavigateToStudy = {},
            onDeleteWord = {},
            onDeleteSentence = {}
        )
    }
}
