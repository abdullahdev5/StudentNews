package com.android.studentnews.main

import com.android.studentnews.R
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.android.studentnews.core.domain.constants.FontSize
import com.android.studentnews.main.account.ui.viewmodel.AccountViewModel
import com.android.studentnews.ui.theme.ReferralBonusTopCardColor
import com.android.studentnews.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun ReferralBonusScreen(
    navHostController: NavHostController,
    accountViewModel: AccountViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {

    val context = LocalContext.current
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val currentUser by accountViewModel.currentUser.collectAsStateWithLifecycle()


    val cardMaxHeightPx = with(density) { (configuration.screenHeightDp / 3).dp.toPx() }
    var currentCardHeightPx by remember { mutableFloatStateOf(cardMaxHeightPx) }

    val cardScrollConnection = remember(cardMaxHeightPx) {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                val delta = available.y
                if (delta >= 0f) {
                    return Offset.Zero
                }
                var newCardHeight = currentCardHeightPx + delta
                var previousCardHeight = currentCardHeightPx
                currentCardHeightPx = newCardHeight.coerceIn(0f, cardMaxHeightPx)

                val consumed = currentCardHeightPx - previousCardHeight

                return Offset(0f, consumed)
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                val delta = available.y
                var newCardHeight = currentCardHeightPx + delta
                var previousCardHeight = currentCardHeightPx
                currentCardHeightPx = newCardHeight.coerceIn(0f, cardMaxHeightPx)

                val consumed = currentCardHeightPx - previousCardHeight

                return Offset(0f, consumed)
            }
        }
    }

    val preloaderLottieComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(
            resId = R.raw.bonus_gift
        )
    )

    val preloaderProgress by animateLottieCompositionAsState(
        composition = preloaderLottieComposition,
        isPlaying = currentCardHeightPx != 0f,
        restartOnPlay = true,
    )



    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Referral\nBonus",
                        style = TextStyle(
                            fontSize = FontSize.LARGE.sp,
                            textAlign = TextAlign.Center,
                            color = White
                        ),
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navHostController.navigateUp()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Icon for Navigate back",
                            tint = White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = ReferralBonusTopCardColor
                )
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(cardScrollConnection)
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = ReferralBonusTopCardColor
                ),
                shape = RectangleShape,
                modifier = Modifier
                    .then(
                        with(density) {
                            Modifier
                                .height(
                                    currentCardHeightPx.toDp()
                                )
                        }
                    )
            ) {


                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 5.dp)
                    ) {
                        LottieAnimation(
                            composition = preloaderLottieComposition,
                            progress = preloaderProgress,
                            modifier = Modifier
                                .weight(1f)
                        )

                        AnimatedVisibility(
                            visible = currentCardHeightPx != 0f
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(5.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier

                            ) {
                                Text(
                                    text = "${100}",
                                    style = TextStyle(
                                        fontSize = FontSize.EXTRA_LARGE.sp,
                                        color = White
                                    )
                                )
                                Text(
                                    text = "Total Points",
                                    style = TextStyle(
                                        fontSize = (FontSize.SMALL - 1).sp,
                                        color = White
                                    )
                                )
                            }
                        }
                    }

                }
            }

            (1..300).forEach {
                Text(text = "Item $it")
            }


        }

    }


}