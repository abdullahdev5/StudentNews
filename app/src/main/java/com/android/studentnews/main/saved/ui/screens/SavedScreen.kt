@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.studentnews.main.settings.saved.ui.screens

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.studentnews.core.domain.common.CollapsingAppBarNestedScrollConnection
import com.android.studentnews.main.MainTabRowList
import com.android.studentnews.main.settings.saved.ui.viewModels.SavedEventsViewModel
import com.android.studentnews.main.settings.saved.ui.viewModels.SavedNewsViewModel
import com.android.studentnews.news.ui.MainTabRow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SavedScreen(
    navHostController: NavHostController,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
) {

    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val topBarMaxHeight = with(density) { (30).dp.roundToPx() }
    val topBarScrollConnection = remember(topBarMaxHeight) {
        CollapsingAppBarNestedScrollConnection(topBarMaxHeight)
    }
    val isLandscape = remember {
        configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    val tabList = listOf(
        MainTabRowList.News,
        MainTabRowList.Events,
    )

    var currentTab by remember { mutableIntStateOf(0) }


    BackHandler(currentTab != 0) {
        scope.launch {
            currentTab = 0
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Saved")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navHostController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Icon for Navigate Back"
                        )
                    }
                },
                actions = {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = "Icon For Saved"
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    scrolledContainerColor = Color.Transparent
                ),
                modifier = Modifier
                    .statusBarsPadding()
                    .then(
                        with(density) {
                            Modifier.height(
                                (topBarMaxHeight + topBarScrollConnection.appBarOffset).toDp()
                            )
                        }
                    )
                    .offset { IntOffset(0, topBarScrollConnection.appBarOffset) }
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .then(
                if (isLandscape)
                    Modifier
                        .nestedScroll(topBarScrollConnection)
                else Modifier
            ),
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {

            TabRow(
                selectedTabIndex = currentTab,
                tabs = {
                    for (tab in tabList) {
                        Tab(
                            selected = tab.index == currentTab,
                            text = {
                                Text(text = tab.text)
                            },
                            onClick = {
                                currentTab = tab.index
                            }
                        )
                    }
                }
            )

            when (currentTab) {

                0 -> {
                    val savedNewsViewModel = koinViewModel<SavedNewsViewModel>()

                    SavedNewsScreen(
                        navHostController = navHostController,
                        savedNewsViewModel = savedNewsViewModel,
                        animatedVisibilityScope = animatedVisibilityScope,
                        sharedTransitionScope = sharedTransitionScope
                    )
                }

                1 -> {
                    val savedEventsViewModel = koinViewModel<SavedEventsViewModel>()

                    SavedEventsScreen(
                        navHostController = navHostController,
                        savedEventsViewModel = savedEventsViewModel,
                        animatedVisibilityScope = animatedVisibilityScope,
                        sharedTransitionScope = sharedTransitionScope
                    )
                }

            }

        }

    }


}