package com.erdem.nosi.screen

import android.util.Log
import android.util.MutableInt
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.SelectableChipElevation
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.tooling.SourceInformation
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.erdem.nosi.R
import com.erdem.nosi.data.Content
import com.erdem.nosi.data.GeminiRequest
import com.erdem.nosi.data.Part
import com.erdem.nosi.request.ApiInterface
import com.erdem.nosi.request.RetrofitInstance
import com.erdem.nosi.ui.theme.AiTutorTextColor
import com.erdem.nosi.ui.theme.MainBackgroundrColor
import com.erdem.nosi.ui.theme.SelectedTransleteContainer
import com.erdem.nosi.ui.theme.SelectedTransleteText
import com.erdem.nosi.ui.theme.TranslationContainerBackground
import com.erdem.nosi.ui.theme.UnSelectedTransleteContainer
import com.erdem.nosi.ui.theme.UnSelectedTransleteText
import com.erdem.nosi.ui.theme.White
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

val LexendFontFamily = FontFamily(
    Font(R.font.lexend)
)


// Initialize the Gemini Developer API backend service
// Create a `GenerativeModel` instance with a model that supports your use case
val model = Firebase.ai(backend= GenerativeBackend.googleAI())
    .generativeModel("gemini-2.5-flash")

// Initialize the Vertex AI Gemini API backend service
// Create a `GenerativeModel` instance with a model that supports your use case
// val model = Firebase.ai(backend=GenerativeBackend.vertexAI())
//     .generativeModel("gemini-2.0-flash")


// Provide a prompt that contains text
val prompt = "Write a story about a magic backpack."

// To generate text output, call generateContent with the text input
private lateinit var apiInterface: ApiInterface
private fun getApiInterface() {
    apiInterface = RetrofitInstance.getInstance().create(ApiInterface::class.java)
}

fun sendRequest() {
    apiInterface = RetrofitInstance.getInstance().create(ApiInterface::class.java)
    GlobalScope.launch {

        try {
            val request = GeminiRequest(
                contents = listOf(
                    Content(
                        parts = listOf(
                            Part(text = "Retrofit nedir?")
                        )
                    )
                )
            )
            val url =
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"
            val result = apiInterface.generateContent(
                url,
                request = request,
                apiKey = ""
            )

            Log.d("GEMINI", result.toString())

        } catch (e: Exception) {
            Log.e("GEMINI_ERROR", e.message ?: "Unknown error")
        }
    }
}





fun modelCall() {

    val translationSchema = Schema.obj(
        mapOf(
            "sentence" to Schema.string(),                 // Orijinal Türkçe cümle
            "translatedSentence" to Schema.string(),       // İngilizce çeviri
            "translatedWords" to Schema.array(             // İngilizce kelimelerin analizi
                Schema.obj(
                    mapOf(
                        "word" to Schema.string(),
                        "lemma" to Schema.string(),
                        // İngilizce için kelime türlerini genişlettik:
                        "type" to Schema.enumeration(listOf(
                            "noun",
                            "verb",
                            "adj",
                            "adv",
                            "interjection",
                            "pronoun",      // "I", "he" için gerekli
                            "preposition",  // "on", "at" için gerekli
                            "conjunction",  // "and", "but" için gerekli
                            "determiner"    // "the", "a" için gerekli
                        )),
                        "meanings" to Schema.array(Schema.string()),
                        "examples" to Schema.array(Schema.string())
                    )
                )
            )
        )
    )

    val model = Firebase.ai(backend= GenerativeBackend.googleAI())
        .generativeModel("gemini-2.0-flash",
                // In the generation config, set the `responseMimeType` to `application/json`
                // and pass the JSON schema object into `responseSchema`.
                generationConfig = generationConfig {
            responseMimeType = "application/json"
            responseSchema = translationSchema
        })

    MainScope().launch {
        val inputSentence = "Dün akşam ayağım takıldı"

        // ÖNEMLİ: Modele ne yapması gerektiğini açıkça söyleyen Prompt
        val prompt = """
            Translate the following Turkish sentence to English: "$inputSentence"

        """.trimIndent()

        try {
            val response = model.generateContent(prompt)
            // Sonucu logluyoruz
            Log.e("erdemii", response.text ?: "No response text")
        } catch (e: Exception) {
            Log.e("erdemii", "Error: ${e.message}")
        }
    }

}


@Composable
fun TranslationScaffol() {
    var presses by remember { mutableIntStateOf(0) }

    //modelCall()
    sendRequest()
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
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            //Üst kısımdaki ai bilgilendirme mesajı
            AiInfo()
            TransletedSentence()
        }
    }
}
/**
 * Ai tutor tasarımı,  ai bilgilendirme mesajı
 *
 * @param text TopBar üzerindeki text yazısı
 * @param id TopBar üzerindeki logo
 * @return IconDescription logonun description texti.
 */
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
            modifier = Modifier.wrapContentSize()
        ) {
            Text(
                modifier = Modifier
                    .padding(start = 12.dp),
                text = stringResource(R.string.AiTextTitle),
                color = AiTutorTextColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = LexendFontFamily
            )
            Surface(
                modifier = Modifier
                    .padding(start = 12.dp, top = 8.dp, bottom = 4.dp, end = 20.dp)
                    .wrapContentHeight()
                    .fillMaxWidth()/*Padding for surface*/,
                color = Color(0xFF233C48),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(R.string.AiText),
                    color = White,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Start,
                    lineHeight = TextUnit.Unspecified,
                    fontWeight = FontWeight.Normal,
                    fontFamily = LexendFontFamily
                )
            }
        }

    }

}

@Composable
fun TextSurface(color: Color, textColor: Color, text: String) {
    Surface(
        modifier = Modifier
            .padding(start = 12.dp, bottom = 12.dp, end = 20.dp)
            .wrapContentHeight()
            .fillMaxWidth()/*Padding for surface*/,
        color = color,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = "Which color do you like most",
            color = textColor,
            fontSize = 17.sp,
            textAlign = TextAlign.Start,
            lineHeight = TextUnit.Unspecified,
            fontFamily = LexendFontFamily
        )
    }
}

@Composable
fun TransletedSentence() {

    Surface(
        modifier = Modifier
            .padding(start = 12.dp, top = 0.dp, bottom = 12.dp, end = 20.dp)
            .wrapContentHeight()
            .fillMaxWidth()/*Padding for surface*/,
        color = TranslationContainerBackground,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(

        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(R.string.text),
                color = AiTutorTextColor,
                fontSize = 16.sp,
                textAlign = TextAlign.Start,
                lineHeight = TextUnit.Unspecified,
                fontWeight = FontWeight.SemiBold,
                fontFamily = LexendFontFamily
            )

            Text(
                modifier = Modifier.padding(start = 16.dp, bottom = 16.dp, end = 16.dp),
                text = "What is your favorite color",
                color = White,
                fontSize = 18.sp,
                textAlign = TextAlign.Start,
                lineHeight = TextUnit.Unspecified,
                fontFamily = LexendFontFamily
            )
            HorizontalDivider(
                Modifier.padding(start = 16.dp, end = 16.dp),
                DividerDefaults.Thickness,
                DividerDefaults.color
            )
            Text(
                modifier = Modifier.padding(16.dp),
                text = "Alternative english Sentences",
                color = Color(0xFF9CA3AF),
                fontSize = 16.sp,
                textAlign = TextAlign.Start,
                lineHeight = TextUnit.Unspecified,
                fontWeight = FontWeight.SemiBold,
                fontFamily = LexendFontFamily
            )
            //Api'den gelen yanıt yazdırılacak
            TextSurface(
                color = SelectedTransleteContainer,
                textColor =  SelectedTransleteText,
                text = "")
            TextSurface(
                color = UnSelectedTransleteContainer,
                textColor = UnSelectedTransleteText,
                text = ""
            )
            HorizontalDivider(
                Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp),
                DividerDefaults.Thickness,
                DividerDefaults.color
            )
            Text(
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                text = "Breakdown for \"Which color do you like the most?\"",
                color = Color(0xFF9CA3AF),
                fontSize = 15.sp,
                textAlign = TextAlign.Start,
                lineHeight = TextUnit.Unspecified,
                fontWeight = FontWeight.SemiBold,
                fontFamily = LexendFontFamily
            )

            val words = listOf("Which", "color", "do", "you", "like", "most")
            var selectedIndex by remember { mutableIntStateOf(0) }
            FlowRowSimpleUsageExample(
                list = words,
                selectedIndex = selectedIndex,
                onSelectedIndexChange = { selectedIndex = it }
            )
            HorizontalDivider(
                Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp),
                DividerDefaults.Thickness,
                DividerDefaults.color
            )
            WordExplanation(
                word = words.get(selectedIndex),
                wordInformation = "",
                wordExplanation = "")
        }
    }
}

@Composable
private fun FlowRowSimpleUsageExample(
    list: List<String>,
    selectedIndex:Int,
    onSelectedIndexChange: (Int) -> Unit
) {

    FlowRow(
        modifier = Modifier
            .padding(8.dp),
    ) {
        //seçilen indexi burada kontrol ediyoruz.
        //FilterChipExample içerisinde yapmak compose'a uygun değilmiş --> Gemini
        list.forEachIndexed { index, word ->
            val isSelected = (index == selectedIndex)
            FilterChipExample(
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

@Composable
fun FilterChipExample(text: String, isSelected: Boolean, onSelect: ()-> Unit) {
    val cornerRadius = 64.dp
    FilterChip(
        modifier = Modifier.padding(start = 4.dp , end = 4.dp),
        onClick = onSelect,
        label = {
            Text(
                text = text,
                fontFamily = LexendFontFamily,
                color = Color(0xFFE5E7EB),
                fontSize = 16.sp
            )
        },
        // RENK AYARLARI BURADA YAPILIYOR:
        colors = FilterChipDefaults.filterChipColors(
            // 1. Seçildiğindeki Arka Plan Rengi
            selectedContainerColor = Color(0xFFD0BB95),

            // 2. Seçildiğindeki Yazı ve İkon Rengi
            selectedLabelColor = Color.White,
            selectedLeadingIconColor = Color.White,

            // 3. Seçili DEĞİLKENki Arka Plan Rengi (İsteğe bağlı)
            containerColor = Color(0xFF374151),

            // 4. Seçili DEĞİLKENki Yazı Rengi (İsteğe bağlı)
            labelColor = Color.LightGray
        ),
        shape = RoundedCornerShape(cornerRadius),
        selected = isSelected,
    )
}

@Composable
fun WordExplanation(
    word: String,
    wordInformation: String,
    wordExplanation: String) {

    //Ana kelime
    Text(
        modifier = Modifier
            .padding(top = 20.dp, start = 16.dp ),
        text = word,
        color = White,
        fontSize = 24.sp,
        fontFamily = LexendFontFamily,
        fontWeight = FontWeight.Bold
    )
    //Kelime telafuzu ve kelimenin türü
    Text(
        modifier = Modifier
            .padding(top = 8.dp, start = 16.dp ),
        text = "/wɪtʃ/ | Determiner, Pronoun", //wordInformation
        color = Color(0xFF9CA3AF),
        fontSize = 16.sp,
        fontFamily = LexendFontFamily,
        fontWeight = FontWeight.Normal
    )
    //Kelimenin kelime anlamı.
    Text(
        modifier = Modifier
            .padding(16.dp),
        text = "Asking for information specifying one or" +
                " more people or things from a definite set.", //wordInformation
        color = Color(0xFFE5E7EB),
        fontSize = 18.sp,
        fontFamily = LexendFontFamily,
        fontWeight = FontWeight.Normal,
        textAlign = TextAlign.Start
    )

}
@Preview(showBackground = true)
@Composable
fun TranslationPreview() {
    TranslationScaffol()
}