package com.erdem.nosi.screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.erdem.nosi.R
import com.erdem.nosi.data.local.CollectionSummary
import com.erdem.nosi.request.MainViewModel
import com.erdem.nosi.ui.theme.CardBackgroundDark
import com.erdem.nosi.ui.theme.CardBackgroundMedium
import com.erdem.nosi.ui.theme.CardBorderColor
import com.erdem.nosi.ui.theme.GlowTeal
import com.erdem.nosi.ui.theme.GradientTealEnd
import com.erdem.nosi.ui.theme.GradientTealStart
import com.erdem.nosi.ui.theme.NosiTheme
import com.erdem.nosi.ui.theme.SectionHeaderColor
import com.erdem.nosi.ui.theme.SubtleTextColor
import com.erdem.nosi.ui.theme.TopBarColor
import com.erdem.nosi.ui.theme.White


// ──────────────────────────────────────
// Font
// ──────────────────────────────────────
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)
val fontName = GoogleFont("Space Grotesk")
val fontFamily = FontFamily(
    Font(googleFont = fontName, fontProvider = provider)
)

// ──────────────────────────────────────
// TopBar — Standard (for MainScreen)
// ──────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(text: String, @DrawableRes id: Int, IconDescription: String) {
    CenterAlignedTopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = text,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontFamily = fontFamily
                )
                Icon(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp),
                    painter = painterResource(id = id),
                    contentDescription = IconDescription,
                )
            }
        },
        colors = topAppBarColors(
            containerColor = TopBarColor,
            titleContentColor = White,
        )
    )
}

// ──────────────────────────────────────
// TopBar — With Back Button (for TranslationScreen)
// ──────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithBack(text: String, onBack: () -> Unit) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = onBack) {
                Text(
                    text = "←",
                    color = White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        title = {
            Text(
                text = text,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontFamily = fontFamily
            )
        },
        colors = topAppBarColors(
            containerColor = CardBackgroundDark,
            titleContentColor = White,
        )
    )
}

// ──────────────────────────────────────
// Main Screen Content
// ──────────────────────────────────────
@Composable
fun MainScreenContent(
    onNavigateToTranslation: () -> Unit = {},
    onNavigateToCollection: (Long) -> Unit = {}
) {
    val mainViewModel: MainViewModel = viewModel()
    val collectionSummaries by mainViewModel.collectionSummaries.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopBar(
                "Nosi",
                R.drawable.ayarlar_simge,
                "Ayarlar"
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .background(color = CardBackgroundDark)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // ── Section 1: New Translation CTA ──
            NewTranslationCard(
                onClick = onNavigateToTranslation
            )

            // ── Section 2: My Collections ──
            SectionTitle(
                icon = "📚",
                title = "My Collections"
            )
            MyCollectionsSection(
                collections = collectionSummaries,
                onCollectionClick = onNavigateToCollection
            )

            // ── Section 3: Community Collections ──
            SectionTitle(
                icon = "🌍",
                title = "Community Collections"
            )
            CommunitySection()

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ──────────────────────────────────────
// Section Title
// ──────────────────────────────────────
@Composable
private fun SectionTitle(icon: String, title: String) {
    Text(
        modifier = Modifier.padding(horizontal = 20.dp),
        text = "$icon  $title",
        color = White,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = LexendFontFamily,
        letterSpacing = 0.3.sp
    )
}

// ──────────────────────────────────────
// New Translation CTA Card
// ──────────────────────────────────────
@Composable
private fun NewTranslationCard(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = GlowTeal,
                spotColor = GlowTeal
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        GradientTealStart.copy(alpha = 0.6f),
                        GradientTealEnd.copy(alpha = 0.3f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() },
        color = CardBackgroundMedium,
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            GradientTealStart.copy(alpha = 0.12f),
                            Color.Transparent
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon circle
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(
                            elevation = 8.dp,
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
                        fontSize = 24.sp,
                        color = White
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "New Translation",
                        color = White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = LexendFontFamily,
                        letterSpacing = 0.3.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Enter a sentence to analyze and learn",
                        color = SectionHeaderColor,
                        fontSize = 14.sp,
                        fontFamily = LexendFontFamily,
                        fontWeight = FontWeight.Normal
                    )
                }

                // Arrow
                Text(
                    text = "→",
                    color = GradientTealStart,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ──────────────────────────────────────
// My Collections Section
// ──────────────────────────────────────
@Composable
private fun MyCollectionsSection(
    collections: List<CollectionSummary>,
    onCollectionClick: (Long) -> Unit
) {
    if (collections.isEmpty()) {
        // Empty state
        Surface(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = CardBorderColor.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(16.dp)
                ),
            color = CardBackgroundMedium,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = CardBorderColor.copy(alpha = 0.3f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "📖", fontSize = 28.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No collections yet",
                    color = White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = LexendFontFamily
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Translate sentences and save words\nto build your personal collection",
                    color = SubtleTextColor,
                    fontSize = 14.sp,
                    fontFamily = LexendFontFamily,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }
        }
    } else {
        collections.forEach { collection ->
            CollectionCard(
                collection = collection,
                onClick = { onCollectionClick(collection.id) }
            )
        }
    }
}

// ──────────────────────────────────────
// Collection Card
// ──────────────────────────────────────
@Composable
private fun CollectionCard(collection: CollectionSummary, onClick: () -> Unit) {
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
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() },
        color = CardBackgroundMedium,
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(GradientTealStart.copy(alpha = 0.2f), GradientTealEnd.copy(alpha = 0.1f))
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📚", fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = collection.name,
                    color = White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = LexendFontFamily
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "${collection.sentenceCount} sentence${if (collection.sentenceCount != 1) "s" else ""}",
                        color = SubtleTextColor,
                        fontSize = 13.sp,
                        fontFamily = LexendFontFamily
                    )
                    Text(
                        text = "•",
                        color = SubtleTextColor,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "${collection.wordCount} word${if (collection.wordCount != 1) "s" else ""}",
                        color = SubtleTextColor,
                        fontSize = 13.sp,
                        fontFamily = LexendFontFamily
                    )
                }
            }

            // Arrow
            Text(
                text = "→",
                color = GradientTealStart,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ──────────────────────────────────────
// Community Collections — Coming Soon
// ──────────────────────────────────────
@Composable
private fun CommunitySection() {
    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = CardBorderColor.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            ),
        color = CardBackgroundMedium.copy(alpha = 0.6f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = CardBorderColor.copy(alpha = 0.3f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "👥",
                    fontSize = 22.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Community Collections",
                    color = White.copy(alpha = 0.7f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = LexendFontFamily
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Coming soon...",
                    color = SubtleTextColor,
                    fontSize = 13.sp,
                    fontFamily = LexendFontFamily,
                    fontWeight = FontWeight.Normal
                )
            }

            // Coming soon badge
            Surface(
                color = GradientTealStart.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    text = "Soon",
                    color = GradientTealStart,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = LexendFontFamily
                )
            }
        }
    }
}

// ──────────────────────────────────────
// Preview
// ──────────────────────────────────────
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    NosiTheme {
        MainScreenContent()
    }
}