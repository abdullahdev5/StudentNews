@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.studentnews.main.settings.saved.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Newspaper
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
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalScrollCaptureInProgress
import androidx.navigation.NavHostController
import com.android.studentnews.core.domain.constants.FontSize
import com.android.studentnews.main.settings.saved.ui.viewModels.SavedEventsViewModel
import com.android.studentnews.main.settings.saved.ui.viewModels.SavedNewsViewModel
import com.android.studentnews.ui.theme.Black
import com.android.studentnews.ui.theme.Green
import com.android.studentnews.ui.theme.White
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
    val scope = rememberCoroutineScope()

    val pagerState = rememberPagerState(pageCount = { 2 })


    BackHandler(pagerState.currentPage != 0) {
        scope.launch {
            pagerState.animateScrollToPage(0)
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
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = "Icon For Saved"
                    )
                },
            )
        },
        modifier = Modifier
            .fillMaxSize(),
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {

            TabRow(
                selectedTabIndex = pagerState.currentPage
            ) {
                Tab(
                    selected = pagerState.currentPage == 0,
                    text = {
                        Text(text = "News")
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Newspaper,
                            contentDescription = "Icon for News"
                        )
                    },
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    },
                    selectedContentColor = Green,
                    unselectedContentColor = if (isSystemInDarkTheme()) White else Black
                )

                Tab(
                    selected = pagerState.currentPage == 1,
                    text = {
                        Text(text = "Events")
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Event,
                            contentDescription = "Icon for Events"
                        )
                    },
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                    selectedContentColor = Green,
                    unselectedContentColor = if (isSystemInDarkTheme()) White else Black
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
            ) { page ->

                when (page) {

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


}