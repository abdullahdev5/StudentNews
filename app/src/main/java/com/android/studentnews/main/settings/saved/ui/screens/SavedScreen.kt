@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)

package com.android.studentnews.main.settings.saved.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.android.studentnews.main.settings.saved.ui.viewModels.SavedEventsViewModel
import com.android.studentnews.main.settings.saved.ui.viewModels.SavedNewsViewModel
import com.android.studentnews.ui.theme.DarkGray
import com.android.studentnews.ui.theme.White
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun SharedTransitionScope.SavedScreen(
    navHostController: NavHostController,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())


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
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                    }
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
                    }
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
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    }

                    1 -> {
                        val savedEventsViewModel = koinViewModel<SavedEventsViewModel>()

                        SavedEventsScreen(
                            navHostController = navHostController,
                            savedEventsViewModel = savedEventsViewModel,
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    }

                }

            }

        }

    }


}