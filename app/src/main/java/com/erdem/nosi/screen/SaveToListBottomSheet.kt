package com.erdem.nosi.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.erdem.nosi.database.WordListEntity
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

// ─── Renk seçenekleri ───────────────────────────────────────────────────────
private data class ListColorOption(val hex: String, val color: Color)

private val listColorOptions = listOf(
    ListColorOption("#0D9488", Color(0xFF0D9488)),  // Teal
    ListColorOption("#D4A853", Color(0xFFD4A853)),  // Gold
    ListColorOption("#6366F1", Color(0xFF6366F1)),  // Indigo
    ListColorOption("#EC4899", Color(0xFFEC4899)),  // Pink
    ListColorOption("#F97316", Color(0xFFF97316)),  // Orange
    ListColorOption("#22C55E", Color(0xFF22C55E)),  // Green
    ListColorOption("#EF4444", Color(0xFFEF4444)),  // Red
    ListColorOption("#A855F7", Color(0xFFA855F7)),  // Purple
)

// ─── Emoji seçenekleri ──────────────────────────────────────────────────────
private val emojiOptions = listOf("📚","✈️","💼","🎯","🔬","💡","🎨","🏆","❤️","⭐","🌍","🎵")

// ─── Ana Bottom Sheet ────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveToListBottomSheet(
    word: String,
    partOfSpeech: String,
    existingLists: List<WordListEntity>,
    savedListIds: List<Long>,          // Bu kelime zaten hangi listelerde kayıtlı
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    onDismiss: () -> Unit,
    onSaveToList: (listId: Long) -> Unit,
    onCreateAndSave: (name: String, emoji: String, color: String) -> Unit
) {
    var showCreateForm by remember { mutableStateOf(false) }
    var selectedListId by remember { mutableStateOf<Long?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CardBackgroundDark,
        dragHandle = {
            // Özel drag handle
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(CardBorderColor.copy(alpha = 0.6f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            // ── Başlık ───────────────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            Brush.linearGradient(listOf(GradientGoldStart, GradientGoldEnd)),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "💾", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Save Word",
                        color = White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = LexendFontFamily
                    )
                    Text(
                        text = "\"${word.replaceFirstChar { it.uppercase() }}\" · ${partOfSpeech.replaceFirstChar { it.uppercase() }}",
                        color = SubtleTextColor,
                        fontSize = 13.sp,
                        fontFamily = LexendFontFamily
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            GradientDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // ── Animasyonlu içerik: Liste seçimi ↔ Form ──────────────────────
            AnimatedContent(
                targetState = showCreateForm,
                transitionSpec = {
                    fadeIn(tween(200)) togetherWith fadeOut(tween(150))
                },
                label = "sheet_content"
            ) { isCreating ->
                if (isCreating) {
                    // ── Yeni Liste Formu ──────────────────────────────────────
                    CreateListForm(
                        onCancel = { showCreateForm = false },
                        onCreate = { name, emoji, color ->
                            onCreateAndSave(name, emoji, color)
                            showCreateForm = false
                        }
                    )
                } else {
                    // ── Liste Seçim Ekranı ────────────────────────────────────
                    Column {
                        if (existingLists.isEmpty()) {
                            // Boş durum
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "📭", fontSize = 40.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No lists yet",
                                    color = White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = LexendFontFamily
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Create your first list below",
                                    color = SubtleTextColor,
                                    fontSize = 13.sp,
                                    fontFamily = LexendFontFamily
                                )
                            }
                        } else {
                            Text(
                                text = "Choose a list",
                                color = SectionHeaderColor,
                                fontSize = 13.sp,
                                fontFamily = LexendFontFamily,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            existingLists.forEach { list ->
                                val isAlreadySaved = list.id in savedListIds
                                val isSelected = selectedListId == list.id

                                ListSelectRow(
                                    list = list,
                                    isSelected = isSelected,
                                    isAlreadySaved = isAlreadySaved,
                                    onClick = {
                                        if (!isAlreadySaved) {
                                            selectedListId = if (isSelected) null else list.id
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // ── Kaydet butonu ─────────────────────────────────
                            AnimatedVisibility(
                                visible = selectedListId != null,
                                enter = expandVertically(tween(200)) + fadeIn(tween(200)),
                                exit = shrinkVertically(tween(150)) + fadeOut(tween(150))
                            ) {
                                Column {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    SaveButton(
                                        label = "Save to List",
                                        onClick = { selectedListId?.let { onSaveToList(it) } }
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // ── Yeni Liste Oluştur butonu ─────────────────────────
                        NewListButton(onClick = { showCreateForm = true })
                    }
                }
            }
        }
    }
}

// ─── Liste Seçim Satırı ──────────────────────────────────────────────────────
@Composable
private fun ListSelectRow(
    list: WordListEntity,
    isSelected: Boolean,
    isAlreadySaved: Boolean,
    onClick: () -> Unit
) {
    val listColor = try {
        Color(android.graphics.Color.parseColor(list.color))
    } catch (e: Exception) {
        GradientTealStart
    }

    val borderBrush = when {
        isAlreadySaved -> Brush.linearGradient(
            listOf(GradientTealStart.copy(alpha = 0.6f), GradientTealEnd.copy(alpha = 0.3f))
        )
        isSelected -> Brush.linearGradient(
            listOf(GradientGoldStart, GradientGoldEnd)
        )
        else -> Brush.linearGradient(
            listOf(CardBorderColor.copy(alpha = 0.5f), CardBorderColor.copy(alpha = 0.3f))
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(width = 1.dp, brush = borderBrush, shape = RoundedCornerShape(14.dp))
            .background(
                if (isSelected) GradientGoldStart.copy(alpha = 0.08f)
                else CardBackgroundMedium
            )
            .clickable(
                enabled = !isAlreadySaved,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Renkli daire + emoji
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(
                    color = listColor.copy(alpha = 0.2f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = list.emoji, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = list.name,
                color = if (isAlreadySaved) SubtleTextColor else White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = LexendFontFamily
            )
        }

        // Durum ikonu
        if (isAlreadySaved) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(GradientTealStart.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "✓ Saved",
                    color = GradientTealStart,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = LexendFontFamily
                )
            }
        } else if (isSelected) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(
                        Brush.linearGradient(listOf(GradientGoldStart, GradientGoldEnd)),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "✓", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ─── Yeni Liste Oluşturma Formu ──────────────────────────────────────────────
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CreateListForm(
    onCancel: () -> Unit,
    onCreate: (name: String, emoji: String, color: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf("📚") }
    var selectedColor by remember { mutableStateOf(listColorOptions.first()) }
    val keyboard = LocalSoftwareKeyboardController.current

    Column {
        Text(
            text = "Create New List",
            color = White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = LexendFontFamily
        )
        Spacer(modifier = Modifier.height(16.dp))

        // İsim alanı
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "List name...",
                    color = SubtleTextColor,
                    fontFamily = LexendFontFamily,
                    fontSize = 15.sp
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = White,
                unfocusedTextColor = White,
                cursorColor = GradientGoldStart,
                focusedBorderColor = GradientGoldStart.copy(alpha = 0.6f),
                unfocusedBorderColor = CardBorderColor.copy(alpha = 0.4f),
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { keyboard?.hide() }),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Emoji seçici
        Text(
            text = "Emoji",
            color = SectionHeaderColor,
            fontSize = 12.sp,
            fontFamily = LexendFontFamily,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            emojiOptions.forEach { emoji ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (emoji == selectedEmoji) GradientGoldStart.copy(alpha = 0.2f)
                            else CardBackgroundMedium
                        )
                        .border(
                            width = 1.dp,
                            color = if (emoji == selectedEmoji) GradientGoldStart else CardBorderColor.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { selectedEmoji = emoji },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 20.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Renk seçici
        Text(
            text = "Color",
            color = SectionHeaderColor,
            fontSize = 12.sp,
            fontFamily = LexendFontFamily,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listColorOptions.forEach { option ->
                val isSelected = option.hex == selectedColor.hex
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(option.color)
                        .border(
                            width = if (isSelected) 2.dp else 0.dp,
                            color = White,
                            shape = CircleShape
                        )
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { selectedColor = option },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Text(text = "✓", color = White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Butonlar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // İptal
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = 1.dp,
                        color = CardBorderColor.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .background(CardBackgroundMedium)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onCancel() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Cancel",
                    color = SubtleTextColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = LexendFontFamily
                )
            }

            // Oluştur
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (name.isNotBlank()) {
                            Brush.linearGradient(listOf(GradientGoldStart, GradientGoldEnd))
                        } else {
                            Brush.linearGradient(listOf(CardBorderColor, CardBorderColor))
                        }
                    )
                    .clickable(
                        enabled = name.isNotBlank(),
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        keyboard?.hide()
                        onCreate(name.trim(), selectedEmoji, selectedColor.hex)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Create & Save",
                    color = if (name.isNotBlank()) White else SubtleTextColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = LexendFontFamily
                )
            }
        }
    }
}

// ─── Kaydet Butonu ────────────────────────────────────────────────────────────
@Composable
private fun SaveButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.linearGradient(listOf(GradientGoldStart, GradientGoldEnd)))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = LexendFontFamily
        )
    }
}

// ─── Yeni Liste Butonu ────────────────────────────────────────────────────────
@Composable
private fun NewListButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(GradientTealStart.copy(alpha = 0.5f), GradientTealEnd.copy(alpha = 0.3f))
                ),
                shape = RoundedCornerShape(14.dp)
            )
            .background(GradientTealStart.copy(alpha = 0.06f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "+",
            color = GradientTealStart,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "New List",
            color = GradientTealStart,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = LexendFontFamily
        )
    }
}
