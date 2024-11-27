@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.studentnews.main.settings.liked

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.android.studentnews.core.domain.constants.Status
import com.android.studentnews.core.ui.common.LoadingDialog
import com.android.studentnews.main.news.domain.destination.NewsDestination
import com.android.studentnews.news.domain.model.NewsModel
import com.android.studentnews.news.ui.NewsItem
import com.google.firebase.Timestamp

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun LikedNewsScreen(
    navHostController: NavHostController,
    likedNewsViewModel: LikedNewsViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
) {

    val context = LocalContext.current

    val likedNewsList by likedNewsViewModel.likedNewsList.collectAsStateWithLifecycle()


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

        if (likedNewsList.size != 0) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(likedNewsList.size) { index ->
                    val item = likedNewsList[index]

                    NewsItem(
                        item = item,
                        context = context,
                        animatedVisibilityScope = animatedVisibilityScope,
                        sharedTransitionScope = sharedTransitionScope,
                        onItemClick = { thisNewsId ->
                            navHostController
                                .navigate(
                                    NewsDestination.NEWS_DETAIL_SCREEN(thisNewsId)
                                )
                        },
                        onSave = { thisNewsItem ->

                            val news = NewsModel(
                                newsId = thisNewsItem.newsId,
                                title = thisNewsItem.title,
                                description = thisNewsItem.description,
                                category = thisNewsItem.category,
                                timestamp = Timestamp.now(),
                                link = thisNewsItem.link,
                                linkTitle = thisNewsItem.linkTitle,
                                urlList = thisNewsItem.urlList,
                                shareCount = thisNewsItem.shareCount ?: 0,
                                likes = thisNewsItem.likes
                            )

                            likedNewsViewModel.onNewsSave(
                                news,
                                onSee = { thisNewsId ->
                                    navHostController.navigate(
                                        NewsDestination.NEWS_DETAIL_SCREEN(
                                            thisNewsId
                                        )
                                    )
                                },
                            )

                        }
                    )
                }
            }
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(text = "No Liked News")
            }
        }


        if (likedNewsViewModel.likedNewsListStatus == Status.Loading) {
            LoadingDialog()
        }

    }

}