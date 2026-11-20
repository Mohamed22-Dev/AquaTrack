package com.example.watertrack.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.watertrack.R
import kotlinx.coroutines.launch

private val Navy = Color(0xFF0B2A5B)
private val BlueDark = Color(0xFF0B5ED7)
private val BlueMid = Color(0xFF3B8FF0)
private val TextGray = Color(0xFF4A5A75)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    val isLast = pagerState.currentPage == 2

    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFE3F0FF), Color(0xFFF2F8FF), Color.White)
                )
            )
    ) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            when (page) {
                0 -> PageOne()
                1 -> PageTwo()
                else -> PageThree()
            }
        }

        // Bouton Passer
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(16.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.White.copy(alpha = 0.95f))
                .clickable(onClick = onFinished)
                .padding(horizontal = 18.dp, vertical = 8.dp)
        ) {
            Text("Passer", color = BlueDark, fontWeight = FontWeight.SemiBold)
        }

        // Points + bouton
        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                repeat(3) { i ->
                    val selected = pagerState.currentPage == i
                    Box(
                        Modifier
                            .padding(4.dp)
                            .size(if (selected) 12.dp else 9.dp)
                            .clip(CircleShape)
                            .background(if (selected) BlueDark else BlueMid.copy(alpha = 0.3f))
                    )
                }
            }
            Spacer(Modifier.height(18.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = if (isLast) Arrangement.Center else Arrangement.End
            ) {
                Button(
                    onClick = {
                        if (isLast) onFinished()
                        else scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    modifier = if (isLast) Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                    else Modifier
                        .height(56.dp)
                        .widthIn(min = 140.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = BlueDark)
                ) {
                    if (isLast) {
                        Text("Commencer", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    } else {
                        Text("Suivant", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Filled.ArrowForward, null, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

/* ---------------------------------------------------------------- */
/*  Pages                                                           */
/* ---------------------------------------------------------------- */

@Composable
private fun PageLayout(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(top = 56.dp, bottom = 190.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content
    )
}

@Composable
private fun TextBlock(title: String, desc: String) {
    Column(
        Modifier.padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            title,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Navy,
            textAlign = TextAlign.Center,
            lineHeight = 34.sp
        )
        Spacer(Modifier.height(10.dp))
        Text(
            desc,
            fontSize = 16.sp,
            color = TextGray,
            textAlign = TextAlign.Center,
            lineHeight = 23.sp
        )
    }
}

@Composable
private fun PageOne() {
    PageLayout {
        Image(
            painter = painterResource(R.drawable.onboarding_1_top),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.height(8.dp))
        TextBlock(
            "Restez hydraté",
            "L'eau, c'est la vie !\nBuvez suffisamment d'eau pour une meilleure santé."
        )
        Image(
            painter = painterResource(R.drawable.onboarding_1_bottom),
            contentDescription = null,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun PageTwo() {
    PageLayout {
        Image(
            painter = painterResource(R.drawable.onboarding_2),
            contentDescription = null,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentScale = ContentScale.Fit
        )
        TextBlock(
            "Suivez votre eau",
            "Surveillez votre consommation quotidienne et atteignez vos objectifs."
        )
    }
}

@Composable
private fun PageThree() {
    PageLayout {
        Image(
            painter = painterResource(R.drawable.onboarding_3),
            contentDescription = null,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentScale = ContentScale.Fit
        )
        TextBlock(
            "N'oubliez jamais de boire",
            "Recevez des rappels et des conseils santé pour de meilleures habitudes."
        )
    }
}