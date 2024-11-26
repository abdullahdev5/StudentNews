package com.android.studentnewsadmin.main.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.android.studentnewsadmin.main.MainScreen
import com.android.studentnewsadmin.main.events.domain.models.EditEventModel
import com.android.studentnewsadmin.main.events.domain.navType.EditEventNavType
import com.android.studentnewsadmin.main.events.ui.screens.EditEventScreen
import com.android.studentnewsadmin.main.events.ui.screens.UploadEVentsScreen
import com.android.studentnewsadmin.main.events.ui.viewModels.EventsViewModel
import com.android.studentnewsadmin.main.news.ui.screens.UploadCategoryScreen
import com.android.studentnewsadmin.main.news.ui.screens.UploadNewsScreen
import com.android.studentnewsadmin.main.news.ui.screens.NewsScreen
import com.android.studentnewsadmin.main.news.ui.viewmodel.NewsViewModel
import org.koin.androidx.compose.koinViewModel
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@UnstableApi
@Composable
fun NavigationGraph(
    navHostController: NavHostController,
) {
    NavHost(
        navController = navHostController,
        startDestination = Destination.MAIN_SCREEN,
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tween(500))
        },
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tween(500))
        },
    ) {
        composable<Destination.MAIN_SCREEN>() {
            MainScreen(
                navHostController = navHostController,
            )
        }

        composable<Destination.UPLOAD_NEWS_SCREEN>() {
            val newsViewModel = koinViewModel<NewsViewModel>()
            UploadNewsScreen(
                navHostController = navHostController,
                newsViewModel = newsViewModel
            )
        }

        composable<Destination.UPLOAD_CATEGORY_SCREEN>() {
            val newsViewModel = koinViewModel<NewsViewModel>()
            UploadCategoryScreen(
                navHostController = navHostController,
                newsViewModel = newsViewModel
            )
        }

        composable<Destination.UPLOAD_EVENTS_SCREEN>() {

            val eventsViewModel = koinViewModel<EventsViewModel>()

            UploadEVentsScreen(
                navHostController = navHostController,
                eventsViewModel = eventsViewModel
            )
        }

        composable<Destination.EDIT_EVENT_SCREEN>(
            typeMap = mapOf(
                typeOf<EditEventModel>() to EditEventNavType.editEventNavType
            )
        ) {

            val eventsViewModel = koinViewModel<EventsViewModel>()
            val arguments = it.toRoute<Destination.EDIT_EVENT_SCREEN>()

            EditEventScreen(
                eventId = arguments.eventId,
                args = arguments.eventRelatedData,
                navHostController = navHostController,
                eventsViewModel = eventsViewModel,
            )
        }

    }
}