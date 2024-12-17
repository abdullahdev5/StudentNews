package com.android.studentnews.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.android.studentnews.core.domain.constants.FontSize
import com.android.studentnews.main.account.ui.viewmodel.AccountViewModel
import com.android.studentnews.ui.theme.White
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferralBonusScreen(
    navHostController: NavHostController,
    accountViewModel: AccountViewModel,
) {

    val context = LocalContext.current
    val density = LocalDensity.current


    val currentUser by accountViewModel.currentUser.collectAsStateWithLifecycle()

    val cardMaxHeight = with(density) { (500).dp.toPx() }
    var currentCardHeight by remember { mutableFloatStateOf(cardMaxHeight) }

    val cardScrollConnection = remember(cardMaxHeight) {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                val delta = available.y
                var newOffset = currentCardHeight + delta
                var previousOffset = currentCardHeight
                currentCardHeight = newOffset.coerceIn(0f, cardMaxHeight)

                val consumed = currentCardHeight - previousOffset

                return Offset(0f, consumed)
            }
        }
    }



    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.Magenta
                    )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(all = 20.dp)
                ) {

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

                }

            }
        },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(cardScrollConnection)
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues = innerPadding),
        ) {

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.Magenta
                ),
                shape = RoundedCornerShape(
                    bottomStart = 20.dp,
                    bottomEnd = 20.dp,
                    topStart = 0.dp,
                    topEnd = 0.dp,
                ),
                modifier = Modifier
                    .then(
                        with(density) {
                            Modifier
                                .height(
                                    currentCardHeight.toDp()
                                )
                        }
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {

                    Text(
                        text = "Your Bonus\nPoints",
                        style = TextStyle(
                            fontSize = FontSize.LARGE.sp,
                            textAlign = TextAlign.Center,
                            color = White
                        ),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp)
                    ) {

                        Icon(
                            imageVector = Icons.Filled.CardGiftcard,
                            contentDescription = "Icon for Referral Bonus",
                            tint = Color.Yellow,
                            modifier = Modifier
                                .width(70.dp)
                                .height(70.dp)
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                        ) {
                            Text(
                                text = "Total Points",
                                style = TextStyle(
                                    fontSize = FontSize.SMALL.sp,
                                    color = White
                                )
                            )

                            Text(
                                text = "${100}",
                                style = TextStyle(
                                    fontSize = FontSize.MEDIUM.sp,
                                    color = White
                                )
                            )
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