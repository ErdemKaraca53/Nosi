package com.erdem.nosi.screen

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.erdem.nosi.data.local.NosiDatabase
import com.erdem.nosi.data.local.SavedTranslationEntity
import com.erdem.nosi.data.local.SavedWordEntity
import com.erdem.nosi.ui.theme.CardBackgroundDark
import com.erdem.nosi.ui.theme.CardBackgroundMedium
import com.erdem.nosi.ui.theme.CardBorderColor
import com.erdem.nosi.ui.theme.ChipSelectedGradientEnd
import com.erdem.nosi.ui.theme.ChipSelectedGradientStart
import com.erdem.nosi.ui.theme.ChipUnselectedBg
import com.erdem.nosi.ui.theme.GlowTeal
import com.erdem.nosi.ui.theme.GradientTealEnd
import com.erdem.nosi.ui.theme.GradientTealStart
import com.erdem.nosi.ui.theme.SectionHeaderColor
import com.erdem.nosi.ui.theme.SubtleTextColor
import com.erdem.nosi.ui.theme.White
import com.erdem.nosi.ui.theme.WordDetailBg
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

// ──────────────────────────────────────
// Collection Detail ViewModel
// ──────────────────────────────────────
class CollectionDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = NosiDatabase.getInstance(application).translationDao()

    fun getTranslations(collectionId: Long): Flow<List<SavedTranslationEntity>> =
        dao.getTranslationsForCollection(collectionId)

    suspend fun getWords(translationId: Long): List<SavedWordEntity> =
        dao.getWordsForTranslation(translationId)

    suspend fun getCollectionName(collectionId: Long): String =
        dao.getCollectionName(collectionId) ?: "Collection"
}

// ──────────────────────────────────────
// Collection Detail Screen
// ──────────────────────────────────────
@Composable
fun CollectionDetailScreen(
    collectionId: Long,
    onNavigateBack: () -> Unit = {},
    onNavigateToStudy: () -> Unit = {}
) {
    val viewModel: CollectionDetailViewModel = viewModel()
    val translations by viewModel.getTranslations(collectionId).collectAsState(initial = emptyList())
    var collectionName by remember { mutableStateOf("Collection") }

    LaunchedEffect(collectionId) {
        collectionName = viewModel.getCollectionName(collectionId)
    }

    Scaffold(
        topBar = {
            TopBarWithBack(
                text = collectionName,
                onBack = onNavigateBack
            )
        }
    ) { innerPadding ->
        if (translations.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .background(color = CardBackgroundDark)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "📭", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No translations in this collection yet",
                        color = SubtleTextColor,
                        fontSize = 16.sp,
                        fontFamily = LexendFontFamily,
                        fontWeight = FontWeight.Normal
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

                // ── Study Button ──
                StudyButton(onClick = onNavigateToStudy)

                // Summary header
                Text(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    text = "${translations.size} sentence${if (translations.size != 1) "s" else ""} saved",
                    color = SectionHeaderColor,
                    fontSize = 14.sp,
                    fontFamily = LexendFontFamily,
                    fontWeight = FontWeight.Normal
                )

                // Translation cards
                translations.forEach { translation ->
                    ExpandableTranslationCard(
                        translation = translation,
                        viewModel = viewModel
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
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
// Expandable Translation Card
// ──────────────────────────────────────
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ExpandableTranslationCard(
    translation: SavedTranslationEntity,
    viewModel: CollectionDetailViewModel
) {
    var isExpanded by remember { mutableStateOf(false) }
    val words = remember { mutableStateListOf<SavedWordEntity>() }
    var selectedWordIndex by remember { mutableIntStateOf(-1) }

    // Kelimeler sadece açıldığında yüklensin
    LaunchedEffect(isExpanded) {
        if (isExpanded && words.isEmpty()) {
            words.addAll(viewModel.getWords(translation.id))
        }
    }

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
            .clickable { isExpanded = !isExpanded },
        color = CardBackgroundMedium,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Source sentence
                    Text(
                        text = translation.sourceSentence,
                        color = SectionHeaderColor,
                        fontSize = 13.sp,
                        fontFamily = LexendFontFamily,
                        fontWeight = FontWeight.Normal,
                        maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Translated sentence
                    Text(
                        text = translation.translatedSentence,
                        color = White,
                        fontSize = 16.sp,
                        fontFamily = LexendFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Expand arrow
                Text(
                    text = if (isExpanded) "▲" else "▼",
                    color = GradientTealStart,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Expandable word section
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))

                    // Divider
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(CardBorderColor.copy(alpha = 0.5f))
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (words.isEmpty()) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.CenterHorizontally),
                            color = GradientTealStart,
                            strokeWidth = 2.dp
                        )
                    } else {
                        // Section header
                        Text(
                            text = "✦  Words (${words.size})",
                            color = SectionHeaderColor,
                            fontSize = 13.sp,
                            fontFamily = LexendFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Word chips
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            words.forEachIndexed { index, word ->
                                val isSelected = index == selectedWordIndex
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable {
                                            selectedWordIndex = if (isSelected) -1 else index
                                        },
                                    color = if (isSelected)
                                        ChipSelectedGradientStart.copy(alpha = 0.2f)
                                    else
                                        ChipUnselectedBg,
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                        text = word.word,
                                        color = if (isSelected)
                                            ChipSelectedGradientEnd
                                        else
                                            White.copy(alpha = 0.8f),
                                        fontSize = 14.sp,
                                        fontFamily = LexendFontFamily,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                }
                            }
                        }

                        // Selected word detail
                        if (selectedWordIndex in words.indices) {
                            val selectedWord = words[selectedWordIndex]

                            Spacer(modifier = Modifier.height(12.dp))

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = WordDetailBg,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp)
                                ) {
                                    // Lemma
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .background(
                                                    Brush.linearGradient(
                                                        colors = listOf(
                                                            GradientTealStart,
                                                            GradientTealEnd
                                                        )
                                                    ),
                                                    shape = CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = selectedWord.lemma.take(1).uppercase(),
                                                color = White,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Text(
                                            text = selectedWord.lemma.ifBlank { selectedWord.word },
                                            color = White,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = LexendFontFamily
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Surface(
                                            color = GradientTealStart.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                                text = selectedWord.pos,
                                                color = GradientTealStart,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                fontFamily = LexendFontFamily
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Turkish meaning
                                    Text(
                                        text = selectedWord.meaningTr,
                                        color = SectionHeaderColor,
                                        fontSize = 15.sp,
                                        fontFamily = LexendFontFamily,
                                        fontWeight = FontWeight.Normal,
                                        lineHeight = 22.sp
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
