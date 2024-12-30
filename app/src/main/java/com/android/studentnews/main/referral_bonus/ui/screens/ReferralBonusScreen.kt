package com.android.studentnews.main.referral_bonus.ui.screens

import com.android.studentnews.R
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.android.studentnews.auth.domain.models.UserModel
import com.android.studentnews.core.domain.constants.FontSize
import com.android.studentnews.core.domain.constants.Status
import com.android.studentnews.core.ui.common.ButtonColors
import com.android.studentnews.main.account.ui.viewmodel.AccountViewModel
import com.android.studentnews.main.referral_bonus.domain.common.calculatePercentage
import com.android.studentnews.main.referral_bonus.domain.destination.ReferralBonusDestinations
import com.android.studentnews.main.referral_bonus.domain.model.OffersModel
import com.android.studentnews.main.referral_bonus.ui.viewModel.ReferralBonusViewModel
import com.android.studentnews.ui.theme.DarkColor
import com.android.studentnews.ui.theme.Gray
import com.android.studentnews.ui.theme.Green
import com.android.studentnews.ui.theme.ReferralLinearColor1
import com.android.studentnews.ui.theme.ReferralLinearColor2
import com.android.studentnews.ui.theme.ReferralScreenBgColorDark
import com.android.studentnews.ui.theme.ReferralScreenBgColorLight
import com.android.studentnews.ui.theme.White
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.compose
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sign
import kotlin.ranges.coerceIn


@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class,
    ExperimentalFoundationApi::class
)
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
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val currentUser by accountViewModel.currentUser.collectAsStateWithLifecycle()

    val offersList by referralBonusViewModel.offersList.collectAsStateWithLifecycle()


    class MyOverScrollEffect(val scope: CoroutineScope) : OverscrollEffect {

        private val overScrollOffset = Animatable(0f)

        override fun applyToScroll(
            delta: Offset,
            source: NestedScrollSource,
            performScroll: (Offset) -> Offset,
        ): Offset {
            // in pre scroll we relax the overscroll if needed
            // relaxation: when we are in progress of the overscroll and user scrolls in the
            // different direction = substract the overscroll first

            val sameDirection = sign(delta.y) == sign(overScrollOffset.value)
            val consumedByPreScroll =
                if (abs(overScrollOffset.value) > 0.5 && !sameDirection) {
                    val previousOverScrollValue = overScrollOffset.value
                    val newOverScrollValue = overScrollOffset.value + delta.y

                    if (sign(previousOverScrollValue) != sign(newOverScrollValue)) {
                        // sign changed, coerce to start scrolling and exit
                        scope.launch { overScrollOffset.snapTo(0f) }
                        Offset(x = 0f, y = delta.y + previousOverScrollValue)
                    } else {
                        scope.launch { overScrollOffset.snapTo(overScrollOffset.value + delta.y) }
                        delta.copy(x = 0f)
                    }
                } else {
                    Offset.Zero
                }

            val leftForScroll = delta - consumedByPreScroll
            val consumedByScroll = performScroll(leftForScroll)
            val overScrollDelta = leftForScroll - consumedByScroll
            // if it is a drag, not a fling, add the delta left to our scroll value
            if (abs(overScrollDelta.y) > 0.5 && source == NestedScrollSource.UserInput) {
                scope.launch {
                    overScrollOffset.snapTo(overScrollOffset.value + overScrollDelta.y * 0.1f)
                }
            }

            return consumedByPreScroll + consumedByScroll
        }

        override suspend fun applyToFling(
            velocity: Velocity,
            performFling: suspend (Velocity) -> Velocity,
        ) {
            val consumed = performFling(velocity)

            // When the filing happens - we just gradually animate our overScroll to 0
            val remaining = velocity - consumed

            overScrollOffset.animateTo(
                targetValue = 0f,
                initialVelocity = remaining.y,
                animationSpec = spring()
            )
        }

        override val effectModifier: Modifier
            get() = Modifier.offset { IntOffset(x = 0, y = overScrollOffset.value.roundToInt()) }

        override val isInProgress: Boolean
            get() = overScrollOffset.value != 0f
    }

    val overScroll = remember(scope) { MyOverScrollEffect(scope) }

    var offset by remember { mutableFloatStateOf(0f) }

    val scrollStateRange = (-512f).rangeTo(512f)


    val lineHeight = 35

    val annotatedTotalAndUsedPointsString = buildAnnotatedString {

        // Total Points String
        withStyle(
            style = ParagraphStyle(
                lineHeight = lineHeight.sp
            )
        ) {
            withStyle(
                style = SpanStyle(
                    fontSize = FontSize.MEDIUM.sp,
                    color = White,
                )
            ) {
                appendLine("Total Points")
            }
            // Actual Total Points
            withStyle(
                style = SpanStyle(
                    fontSize = FontSize.EXTRA_LARGE.sp,
                    color = White,
                )
            ) {
                appendLine(
                    (currentUser?.referralBonus?.totalPoints
                        ?: 0.0).toString()
                )
            }
        }

        // Used Points String
        withStyle(
            style = ParagraphStyle(
                lineHeight = lineHeight.sp
            )
        ) {
            withStyle(
                style = SpanStyle(
                    fontSize = FontSize.MEDIUM.sp,
                    color = White,
                )
            ) {
                appendLine("Used Points")
            }
            // Actual Used Points
            withStyle(
                style = SpanStyle(
                    fontSize = FontSize.EXTRA_LARGE.sp,
                    color = White,
                )
            ) {
                appendLine(
                    (currentUser?.referralBonus?.usedPoints
                        ?: 0.0).toString()
                )
            }
        }
    }


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

            Column(
                modifier = Modifier
                    .scrollable(
                        orientation = Orientation.Vertical,
                        overscrollEffect = overScroll,
                        state = rememberScrollableState { delta ->
                            val oldValue = offset

                            offset = (offset + delta).coerceIn(scrollStateRange)

                            offset - oldValue
                        },
                    )
                    .overscroll(overScroll)
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
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
                        .clickable(
                            enabled = false
                        ) {

                        }
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
                            // Total and Used Points
                            Text(
                                text = annotatedTotalAndUsedPointsString,
                                modifier = Modifier
                                    .padding(all = 20.dp)
                            )
                        }
                    }
                }

                this@Column.AnimatedVisibility(
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
                                text = "Offers For You",
                                style = TextStyle(
                                    fontSize = FontSize.LARGE.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            if (offersList.size > 1) {
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null
                                )
                            }
                        }
                        // Offers List
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            items(
                                count = offersList.size,
                                key = { index ->
                                    offersList[index].offerId
                                },
                            ) { index ->
                                val item = offersList[index]

                                OffersListItem(
                                    item = item,
                                    currentUser = currentUser,
                                    density = density,
                                    context = context,
                                    onRedeem = { thisOfferId ->

                                        referralBonusViewModel
                                            .onOfferCollect(thisOfferId)

                                        navHostController.navigate(
                                            ReferralBonusDestinations.CONGRATULATION_DIALOG(
                                                resId = R.raw.reward_anim,
                                                lottieHeight = 200,
                                                titleText = "Congratulations",
                                                descriptionText = "You Won The Prize.",
                                            )
                                        )
                                    }
                                )
                            }
                        }

                        if (offersList.isEmpty()
                            && referralBonusViewModel.offersListStatus != Status.Loading
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(text = "No Offers for You Yet!")
                            }
                        }
                    }
                }

                if (referralBonusViewModel.offersListStatus == Status.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }

        }

    }


}

@Composable
fun OffersListItem(
    item: OffersModel,
    currentUser: UserModel?,
    density: Density,
    context: Context,
    onRedeem: (offerId: String) -> Unit,
) {

    var itemWidthWithPadding by remember { mutableStateOf(50.dp) }

    val totalPoints = currentUser?.referralBonus?.totalPoints ?: 0.0

    val offersPoints = item.pointsRequired

    val isTotalLessThanOfferPoints = remember {
        derivedStateOf {
            totalPoints < offersPoints
        }
    }.value


    val animatedProgress = remember {
        Animatable(0f)
    }

    LaunchedEffect(Unit) {
        animatedProgress.animateTo(
            targetValue = calculatePercentage(
                totalPoints.toInt(),
                offersPoints.toInt()
            ),
            animationSpec = tween(
                durationMillis = 1000,
                easing = LinearEasing
            )
        )
    }

    val annotatedTitleAndDescription = buildAnnotatedString {
        // Title
        withStyle(
            SpanStyle(
                fontSize = FontSize.MEDIUM.sp
            )
        ) {
            appendLine(item.offerName)
        }
        // Description
        withStyle(
            SpanStyle(
                color = Gray,
                fontSize = FontSize.SMALL.sp
            )
        ) {
            appendLine(item.offerDescription)
        }
    }


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
                .onSizeChanged { size ->
                    itemWidthWithPadding = with(density) { size.width.toDp() }
                }
        ) {

            Text(
                text = annotatedTitleAndDescription,
                modifier = Modifier
                    .padding(all = 5.dp)
            )

            Column(
                modifier = Modifier
                    .width(itemWidthWithPadding)
                    .padding(all = 5.dp)
            ) {

                Text(
                    text = buildAnnotatedString {
                        // Progress
                        withStyle(
                            SpanStyle(
                                fontSize = FontSize.LARGE.sp,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("${animatedProgress.value.toInt()}%")
                        }
                    }
                )

                LinearProgressIndicator(
                    progress = {
                        animatedProgress.value / 100f
                    },
                    color = Green,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                fontSize = FontSize.SMALL.sp,
                                color = if (
                                    isTotalLessThanOfferPoints
                                ) Gray else Green
                            )
                        ) {
                            append(
                                if (isTotalLessThanOfferPoints)
                                    "Reach $offersPoints Points to Redeemed This"
                                else
                                    "You Reached $offersPoints Points, Redeem This"
                            )
                        }
                    },
                    modifier = Modifier
                        .padding(top = 5.dp)
                )

            }

            Button(
                onClick = {
                    onRedeem(item.offerId)
                },
                shape = RoundedCornerShape(5.dp),
                colors = ButtonColors(),
                enabled = totalPoints >= offersPoints,
                modifier = Modifier
                    .width(itemWidthWithPadding)
                    .padding(all = 5.dp)
            ) {
                Text(text = "Redeem")
            }
        }
    }
}