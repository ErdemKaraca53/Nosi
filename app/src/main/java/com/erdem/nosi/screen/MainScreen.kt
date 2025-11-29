package com.erdem.nosi.screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.erdem.nosi.R
import com.erdem.nosi.ui.theme.Black
import com.erdem.nosi.ui.theme.BottomAppBarBackground
import com.erdem.nosi.ui.theme.BottomAppBarContent
import com.erdem.nosi.ui.theme.IconBoxBackground
import com.erdem.nosi.ui.theme.MainBackgroundrColor
import com.erdem.nosi.ui.theme.MainStartButton
import com.erdem.nosi.ui.theme.MainYellowText
import com.erdem.nosi.ui.theme.NosiTheme
import com.erdem.nosi.ui.theme.RowTextColor
import com.erdem.nosi.ui.theme.TopBarColor
import com.erdem.nosi.ui.theme.White

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)
val fontName = GoogleFont("Space Grotesk")
val fontFamily = FontFamily(
    Font(googleFont = fontName, fontProvider = provider)
)
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

fun NavHost(navController: NavController) {
    //composable<Profile> { ProfileScreen( /* ... */ ) }
    //composable<FriendsList> { FriendsListScreen( /* ... */ ) }
}

@Composable
fun ScaffoldExample() {

    val navController = rememberNavController()

    var presses by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopBar(
                "WordWise",
                R.drawable.ayarlar_simge,
                "Ayarlar"
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = BottomAppBarBackground,
                contentColor = BottomAppBarContent,
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "Bottom app bar",
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .background(
                    color = MainBackgroundrColor
                )
                .fillMaxSize()
                //Kaydırılabilir olmasını sağlıyormuş ??
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()

            ) {
                Text(
                    modifier = Modifier
                        .padding(start = 16.dp, bottom = 8.dp),
                    text = stringResource(R.string.main_up),
                    color = White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(start = 12.dp, top = 12.dp, end = 12.dp)
            ) {
                Image(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(8.dp)
                        ),
                    painter = painterResource(id = R.drawable.ana_ekran_foto2),
                    contentDescription = "Ana ekran foto",
                    contentScale = ContentScale.Crop
                )
                Text(
                    modifier = Modifier
                        .padding(top = 16.dp),
                    fontFamily = fontFamily,
                    text = stringResource(R.string.main_middle),
                    color = White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,

                    )
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        modifier = Modifier
                            .weight(2f)
                            .padding(bottom = 8.dp, top = 4.dp),
                        fontFamily = fontFamily,
                        text = stringResource(R.string.main_middle2),
                        color = MainYellowText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        maxLines = 2,

                        )
                    Column (
                        verticalArrangement = Arrangement.Center
                    ) {
                        Button(
                            modifier = Modifier
                                .wrapContentSize(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor =MainStartButton
                            ),
                            onClick = {  }
                        ) {
                            Text("Start", color = Black)
                        }
                    }
                }
                CollectionsTitleText(stringResource(R.string.CollectionTitleMyWord))
                CollectionsRow(
                    R.drawable.book,
                    stringResource(R.string.RowTextTitle),
                    stringResource(R.string.RowTextText))
                CollectionsTitleText("Explore Collections")
                CollectionsRow(
                    R.drawable.others,
                    "Title",
                    "Word count"
                )
                CollectionsRow(
                    R.drawable.others,
                    "Title",
                    "Word count"
                )
                CollectionsRow(
                    R.drawable.others,
                    "Title",
                    "Word count"
                )
            }
        }
    }
}

/**
 * TopBar tasarımı
 *
 *
 *
 * @param text TopBar üzerindeki text yazısı
 * @param id TopBar üzerindeki logo
 * @return IconDescription logonun description texti.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(text: String, @DrawableRes id: Int, IconDescription: String) {
//Bunun sayesinde text tam ortalanıyor
    CenterAlignedTopAppBar(
        title = {
            Box (
                modifier = Modifier
                    .fillMaxWidth()
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

@Composable
fun CollectionsTitleText(title: String) {
    Text(
        modifier = Modifier
            .padding(top = 28.dp),
        text = title,
        fontFamily = fontFamily,
        fontSize = 24.sp,
        color = White
    )
}

@Composable
fun CollectionsRow(@DrawableRes id: Int, title: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(color = MainBackgroundrColor).
            padding(top = 16.dp)
    ) {
        IconBox2Row(id)
        RowText(title, text)
    }
}

@Composable
fun RowText(title:String, wordCount: String) {
    Column(
        modifier = Modifier.wrapContentSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier
                .padding(top = 8.dp, start = 12.dp),
            fontFamily = fontFamily,
            text = title,
            color = White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
        )
        Text(
            modifier = Modifier
                .padding(start = 12.dp, top = 0.dp),
            fontFamily = fontFamily,
            text = wordCount,
            color = RowTextColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
        )
    }
}


@Composable
fun IconBox2Row(@DrawableRes id: Int) {
//Clip backgrounddan sonra gelmeli yoksa köşe kıvrımı oluşmuyor
    Box(
        modifier = Modifier
            .padding(start = 0.dp, 8.dp)
            .wrapContentSize()
            .clip(RoundedCornerShape(8.dp))     // önce kırp
            .background(IconBoxBackground)      // sonra background
            .padding(16.dp),                     // en son padding
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = id),
            contentDescription = "Ayarlar",
            tint = White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NosiTheme {
        ScaffoldExample()
    }
}