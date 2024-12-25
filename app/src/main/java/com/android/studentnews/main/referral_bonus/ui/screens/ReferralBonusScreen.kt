package com.android.studentnews.main.referral_bonus.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.android.studentnews.auth.domain.models.UserModel
import com.android.studentnews.core.domain.constants.FirestoreNodes
import com.android.studentnews.core.domain.constants.FontSize
import com.android.studentnews.core.domain.constants.Status
import com.android.studentnews.core.ui.common.ButtonColors
import com.android.studentnews.main.account.ui.viewmodel.AccountViewModel
import com.android.studentnews.main.referral_bonus.domain.model.OffersModel
import com.android.studentnews.main.referral_bonus.ui.viewModel.ReferralBonusViewModel
import com.android.studentnews.ui.theme.DarkColor
import com.android.studentnews.ui.theme.Gray
import com.android.studentnews.ui.theme.ReferralScreenBgColorLight
import com.android.studentnews.ui.theme.ReferralLinearColor1
import com.android.studentnews.ui.theme.ReferralLinearColor2
import com.android.studentnews.ui.theme.ReferralScreenBgColorDark
import com.android.studentnews.ui.theme.White
import kotlinx.coroutines.CoroutineScope
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

    // For Only Seeing the Dialog
    var isCollectingPointsDialogOpen by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        referralBonusViewModel.getOffers()
    }

    val context = LocalContext.current
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


    val scope = rememberCoroutineScope()

    val overScroll = remember(scope) { MyOverScrollEffect(scope) }

    var offset by remember { mutableFloatStateOf(0f) }

    val scrollStateRange = (-512f).rangeTo(512f)


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
                        .clickable {
                            isCollectingPointsDialogOpen = true
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
                                    text = (currentUser?.referralBonus?.totalPoints
                                        ?: 0.0).toString(),
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
                                    text = (currentUser?.referralBonus?.usedPoints
                                        ?: 0.0).toString(),
                                    style = TextStyle(
                                        fontSize = FontSize.EXTRA_LARGE.sp,
                                        color = White
                                    )
                                )
                            }
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
                        LazyRow {
                            items(offersList.size) { index ->
                                val item = offersList[index]

                                OffersListItem(
                                    item = item,
                                    currentUser = currentUser,
                                    density = density,
                                    context = context
                                )
                            }
                        }
                    }
                }
            }

        }

        if (isCollectingPointsDialogOpen) {
            PointsCollectingDialog(
                descriptionText = {
                    "Collect these referral points for Sharing with Friend. (For Only Seeing the Dialog)"
                },
                onCollect = {
                    isCollectingPointsDialogOpen = false
                },
                onDismiss = {
                    isCollectingPointsDialogOpen = false
                }
            )
        }

    }


}

@Composable
fun OffersListItem(
    item: OffersModel,
    currentUser: UserModel?,
    density: Density,
    context: Context,
) {

    var itemWidthWithPadding by remember { mutableStateOf(50.dp) }

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
                .onSizeChanged { coordinates ->
                    itemWidthWithPadding = with(density) { coordinates.width.toDp() }
                }
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
                text = item.offerDescription ?: "",
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
                colors = ButtonColors(),
                enabled = (currentUser?.referralBonus?.totalPoints ?: 0.0)
                        > (item.pointsWhenAbleToCollect ?: 0.0),
                modifier = Modifier
                    .width(itemWidthWithPadding)
                    .padding(all = 5.dp)
            ) {
                Text(text = "Collect")
            }
        }
    }
}