@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.studentnews.main.settings.liked

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.android.studentnews.core.data.paginator.LENGTH_ERROR
import com.android.studentnews.core.domain.common.ErrorMessageContainer
import com.android.studentnews.main.news.NEWS_ID
import com.android.studentnews.main.news.domain.destination.NewsDestinations
import com.android.studentnews.news.ui.NewsItem

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun LikedNewsScreen(
    navHostController: NavHostController,
    likedNewsViewModel: LikedNewsViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val likedNewsList = likedNewsViewModel.likedNewsList.collectAsLazyPagingItems()

    val likedNewsListNotFound = remember {
        derivedStateOf {
            likedNewsList.itemCount == 0
                    && likedNewsList.loadState.refresh is LoadState.NotLoading
                    && likedNewsList.loadState.hasError
        }
    }.value


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Liked")
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
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Icon for Liked"
                    )
                }
            )
        },
        modifier = Modifier
            .fillMaxSize()
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (likedNewsList.loadState.refresh is LoadState.NotLoading) {
                items(
                    count = likedNewsList.itemCount,
                    key = likedNewsList.itemKey {
                        it.newsId ?: ""
                    },
                    contentType = likedNewsList.itemContentType {
                        "liked_news_list"
                    }
                ) { index ->
                    val item = likedNewsList[index]

                    NewsItem(
                        item = item,
                        context = context,
                        animatedVisibilityScope = animatedVisibilityScope,
                        sharedTransitionScope = sharedTransitionScope,
                        onItemClick = { thisNewsId ->
                            navHostController
                                .navigate(
                                    NewsDestinations.NEWS_DETAIL_SCREEN(thisNewsId)
                                )
                        },
                        onMoreOptionsClick = { thisNewsId ->
                            navHostController.navigate(
                                NewsDestinations
                                    .BottomSheetDestinations
                                    .NEWS_LIST_ITEM_MORE_OPTIONS_BOTTOM_SHEET_DESTINATION +
                                "/$NEWS_ID=$thisNewsId"
                            )
                        }
                    )
                }
            }

            if (
                likedNewsList.loadState.append is LoadState.Loading
                || likedNewsList.loadState.refresh is LoadState.Loading
            ) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            if (
                likedNewsList.loadState.refresh is LoadState.Error
            ) {
                item {
                    ErrorMessageContainer(
                        errorMessage =
                        (likedNewsList.loadState.refresh as LoadState.Error
                                ).error.localizedMessage ?: "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(
                            20.dp
                        ),
                    )
                }
            }

            // Append Failed Error
            if (
                likedNewsList.loadState.prepend is LoadState.Error
                && (likedNewsList.loadState.append as LoadState.Error)
                    .error.localizedMessage != LENGTH_ERROR
            ) {
                item {
                    ErrorMessageContainer(
                        errorMessage =
                        (likedNewsList.loadState.append as LoadState.Error
                                ).error.localizedMessage ?: "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(
                            20.dp
                        ),
                    )
                }
            }

        }

        if (likedNewsListNotFound) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(text = "No Liked News Found!")
            }
        }

    }

}