package com.android.studentnewsadmin.main.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.android.studentnewsadmin.main.events.ui.screens.UploadEVentsScreen
import com.android.studentnewsadmin.main.news.ui.screens.UploadCategoryScreen
import com.android.studentnewsadmin.main.news.ui.screens.UploadNewsScreen
import com.android.studentnewsadmin.main.news.ui.screens.NewsScreen
import com.android.studentnewsadmin.main.news.ui.viewmodel.NewsViewModel

@UnstableApi
@Composable
fun NavigationGraph(
    navHostController: NavHostController,
    newsViewModel: NewsViewModel
) {
    NavHost(
        navController = navHostController,
        startDestination = Destination.NEWS_SCREEN,
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tween(500))
        },
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tween(500))
        },
    ) {
        composable<Destination.NEWS_SCREEN>() {
            NewsScreen(
                navHostController = navHostController,
                newsViewModel = newsViewModel
            )
        }

        composable<Destination.UPLOAD_NEWS_SCREEN>() {
            UploadNewsScreen(
                navHostController = navHostController,
                newsViewModel = newsViewModel
            )
        }

        composable<Destination.UPLOAD_CATEGORY_SCREEN>() {
            UploadCategoryScreen(
                navHostController = navHostController,
                newsViewModel = newsViewModel
            )
        }

        composable<Destination.UPLOAD_EVENTS_SCREEN>() {
            UploadEVentsScreen(
                navHostController = navHostController,
            )
        }

    }
}