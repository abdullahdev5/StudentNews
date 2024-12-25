package com.android.studentnews.main.referral_bonus.ui.screens

import com.android.studentnews.R
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.android.studentnews.core.domain.constants.FontSize
import com.android.studentnews.core.ui.common.ButtonColors
import com.android.studentnews.ui.theme.DarkColor
import com.android.studentnews.ui.theme.DarkGray
import com.android.studentnews.ui.theme.Gray
import com.android.studentnews.ui.theme.LightGray
import com.android.studentnews.ui.theme.White

@Composable
fun PointsCollectDialog(
    descriptionText: () -> String,
    onCollect: () -> Unit,
    onDismiss: () -> Unit,
) {

    val preloaderLottieComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(
            resId = R.raw.bonus_gift
        )
    )

    val preloaderProgress by animateLottieCompositionAsState(
        composition = preloaderLottieComposition,
        isPlaying = true,
        restartOnPlay = false,
    )

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isSystemInDarkTheme()) DarkColor else White
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 10.dp)
                .border(
                    width = 1.dp,
                    color = if (isSystemInDarkTheme()) DarkGray else LightGray,
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Icon for Dismiss"
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    LottieAnimation(
                        composition = preloaderLottieComposition,
                        progress = preloaderProgress,
                        alignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 250.dp)
                    )

                    Text(
                        text = "Referral Points",
                        style = TextStyle(
                            fontSize = FontSize.LARGE.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    Text(
                        text = descriptionText(),
                        style = TextStyle(
                            fontSize = FontSize.MEDIUM.sp,
                            color = Gray
                        ),
                        modifier = Modifier
                            .padding(all = 20.dp)
                    )

                    Button(
                        onClick = onCollect,
                        shape = RoundedCornerShape(5.dp),
                        colors = ButtonColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 10.dp)
                    ) {
                        Text(text = "collect")
                    }

                }
            }

        }
    }

}