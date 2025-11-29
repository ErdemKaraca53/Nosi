package com.erdem.nosi.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.erdem.nosi.R
import com.erdem.nosi.ui.theme.AiTutorTextColor
import com.erdem.nosi.ui.theme.MainBackgroundrColor
import com.erdem.nosi.ui.theme.TranslationContainerBackground
import com.erdem.nosi.ui.theme.White

@Composable
fun TranslationScaffol() {
    var presses by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopBar(
                "Word & Sentence Analysis",
                R.drawable.ayarlar_simge,
                "Translation Top Icon"
            )
        },
        bottomBar = {

        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .background(
                    color = MainBackgroundrColor
                )
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AiInfo()
        }
    }
}

@Composable
fun AiInfo() {
    Row(

    ) {
        Image(
            modifier = Modifier
                .padding(start = 8.dp)
                .clip(
                    RoundedCornerShape(8.dp)
                )
                .size(48.dp),
            painter = painterResource(id = R.drawable.ai),
            contentDescription = "Ana ekran foto",
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                modifier = Modifier
                    .padding(start = 12.dp),
                text = stringResource(R.string.AiTextTitle),
                color = AiTutorTextColor,
                fontSize = 16.sp
            )
            Surface(
                modifier = Modifier
                    .padding(start = 12.dp, top = 8.dp, bottom = 12.dp, end = 20.dp)
                    .wrapContentHeight()
                    .fillMaxWidth()/*Padding for surface*/,
                color = TranslationContainerBackground,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(R.string.AiText),
                    color = White,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Start,
                    lineHeight = TextUnit.Unspecified,
                    fontWeight = FontWeight.Bold
                )
            }
        }

    }

}


@Preview(showBackground = true)
@Composable
fun TranslationPreview() {
    TranslationScaffol()
}