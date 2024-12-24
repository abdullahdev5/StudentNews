package com.android.studentnews.main.referral_bonus.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material3.Button
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.android.studentnews.auth.domain.models.UserModel
import com.android.studentnews.core.domain.constants.FirestoreNodes
import com.android.studentnews.core.domain.constants.FontSize
import com.android.studentnews.core.domain.constants.Status
import com.android.studentnews.main.account.ui.viewmodel.AccountViewModel
import com.android.studentnews.main.referral_bonus.domain.model.OffersModel
import com.android.studentnews.main.referral_bonus.ui.viewModel.ReferralBonusViewModel
import com.android.studentnews.ui.theme.DarkColor
import com.android.studentnews.ui.theme.Gray
import com.android.studentnews.ui.theme.Green
import com.android.studentnews.ui.theme.ReferralScreenBgColorLight
import com.android.studentnews.ui.theme.ReferralLinearColor1
import com.android.studentnews.ui.theme.ReferralLinearColor2
import com.android.studentnews.ui.theme.ReferralScreenBgColorDark
import com.android.studentnews.ui.theme.White
import kotlin.ranges.coerceIn

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun ReferralBonusScreen(
    navHostController: NavHostController,
    accountViewModel: AccountViewModel,
    referralBonusViewModel: ReferralBonusViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {

    LaunchedEffect(Unit) {
        referralBonusViewModel.getOffers()
    }

    val context = LocalContext.current
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val currentUser by accountViewModel.currentUser.collectAsStateWithLifecycle()

    val offersList by referralBonusViewModel.offersList.collectAsStateWithLifecycle()


    val cardMaxHeightPx = with(density) { (320).dp.toPx() }
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

//    val preloaderLottieComposition by rememberLottieComposition(
//        spec = LottieCompositionSpec.RawRes(
//            resId = R.raw.bonus_gift
//        )
//    )
//
//    val preloaderProgress by animateLottieCompositionAsState(
//        composition = preloaderLottieComposition,
//        isPlaying = currentCardHeightPx != 0f,
//        restartOnPlay = true,
//    )


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navHostController.navigateUp()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Icon for Navigate back",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isSystemInDarkTheme())
                        ReferralScreenBgColorDark else ReferralScreenBgColorLight
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
                .verticalScroll(rememberScrollState())
                .background(
                    color = if (isSystemInDarkTheme())
                        ReferralScreenBgColorDark else ReferralScreenBgColorLight
                ),
        ) {

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        with(density) {
                            Modifier
                                .height(
                                    currentCardHeightPx.toDp()
                                )
                        }
                    )
                    .padding(all = 20.dp)
                    .background(
                        brush = Brush.linearGradient(
                            listOf(
                                ReferralLinearColor1,
                                ReferralLinearColor2,
                            ),
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(all = 20.dp)
                    ) {
                        // Total Points
                        Column(
                            verticalArrangement = Arrangement.spacedBy(5.dp),
                            modifier = Modifier
                                .padding(all = 20.dp)
                        ) {
                            Text(
                                text = "Total Points",
                                style = TextStyle(
                                    fontSize = FontSize.MEDIUM.sp,
                                    color = White,
                                ),
                            )

                            Text(
                                text = (currentUser?.referralBonus?.totalPoints ?: 0.0).toString(),
                                style = TextStyle(
                                    fontSize = FontSize.EXTRA_LARGE.sp,
                                    color = White
                                )
                            )
                        }

                        // Used Points
                        Column(
                            verticalArrangement = Arrangement.spacedBy(5.dp),
                            modifier = Modifier
                                .padding(all = 20.dp)
                        ) {
                            Text(
                                text = "Used Points",
                                style = TextStyle(
                                    fontSize = FontSize.MEDIUM.sp,
                                    color = White,
                                ),
                            )

                            Text(
                                text = (currentUser?.referralBonus?.usedPoints ?: 0.0).toString(),
                                style = TextStyle(
                                    fontSize = FontSize.EXTRA_LARGE.sp,
                                    color = White
                                )
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = referralBonusViewModel.offersListStatus != Status.Loading,
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    Row(
                        modifier = Modifier
                            .padding(all = 10.dp)
                            .padding(bottom = 10.dp)
                    ) {
                        Text(
                            text = FirestoreNodes.OFFERS_COL,
                            style = TextStyle(
                                fontSize = FontSize.LARGE.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null
                        )
                    }
                    LazyRow(
                        modifier = Modifier
//                            .weight(1f)
                    ) {
                        items(offersList.size) { index ->
                            val item = offersList[index]

                            OffersListItem(
                                item = item,
                                currentUser = currentUser
                            )
                        }
                    }
                }
            }


        }

    }


}

@Composable
fun OffersListItem(
    item: OffersModel,
    currentUser: UserModel?,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isSystemInDarkTheme()) DarkColor else White
        ),
        modifier = Modifier
            .padding(all = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(all = 10.dp)
        ) {
            Text(
                text = item.offerName ?: "",
                style = TextStyle(
                    fontSize = FontSize.MEDIUM.sp
                ),
                modifier = Modifier
                    .padding(all = 5.dp)
            )

            Text(
                text = "${item.pointsWhenAbleToCollect ?: 0.0} points need to collect",
                style = TextStyle(
                    color = Gray,
                    fontSize = FontSize.SMALL.sp
                ),
                modifier = Modifier
                    .padding(all = 5.dp)
            )

            Button(
                onClick = {

                },
                shape = RoundedCornerShape(5.dp),
                enabled = (currentUser?.referralBonus?.totalPoints ?: 0.0)
                        > (item.pointsWhenAbleToCollect ?: 0.0),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 5.dp)
            ) {
                Text(text = "Collect")
            }
        }
    }
}