@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.android.studentnews.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.android.studentnews.main.news.MY_URI
import com.android.studentnews.auth.domain.RegistrationDataNavType
import com.android.studentnews.auth.domain.destination.AuthDestination
import com.android.studentnews.auth.domain.models.RegistrationData
import com.android.studentnews.auth.ui.AuthenticationScreen
import com.android.studentnews.auth.ui.RegistrationFormScreen
import com.android.studentnews.auth.ui.viewModel.AuthViewModel
import com.android.studentnews.main.account.ui.AccountScreen
import com.android.studentnews.main.account.ui.viewmodel.AccountViewModel
import com.android.studentnews.main.events.domain.destination.EventsDestination
import com.android.studentnews.main.events.ui.screens.BookedEventsScreen
import com.android.studentnews.main.events.ui.screens.EventsDetailScreen
import com.android.studentnews.main.events.ui.screens.EventsScreen
import com.android.studentnews.main.settings.saved.ui.screens.SavedEventsScreen
import com.android.studentnews.main.events.ui.viewModels.EventsViewModel
import com.android.studentnews.main.settings.saved.ui.viewModels.SavedEventsViewModel
import com.android.studentnews.main.news.domain.destination.NewsDestination
import com.android.studentnews.main.news.ui.screens.NewsDetailScreen
import com.android.studentnews.main.news.ui.screens.NewsLinkScreen
import com.android.studentnews.main.settings.saved.ui.screens.SavedNewsScreen
import com.android.studentnews.main.news.ui.viewModel.NewsDetailViewModel
import com.android.studentnews.main.settings.saved.ui.viewModels.SavedNewsViewModel
import com.android.studentnews.main.search.SearchScreen
import com.android.studentnews.main.search.SearchViewModel
import com.android.studentnews.main.settings.SettingsDestination
import com.android.studentnews.main.settings.SettingsScreen
import com.android.studentnews.main.settings.saved.domain.destination.SavedDestination
import com.android.studentnews.main.settings.saved.ui.screens.SavedScreen
import com.android.studentnews.news.domain.destination.MainDestination
import com.android.studentnews.news.ui.NewsScreen
import com.android.studentnews.news.ui.viewModel.NewsViewModel
import org.koin.androidx.compose.koinViewModel
import kotlin.reflect.typeOf
import kotlin.to

@UnstableApi
@Composable
fun NavigationGraph(
    navHostController: NavHostController,
    authViewModel: AuthViewModel,
) {
    SharedTransitionLayout {
        NavHost(
            navController = navHostController,
            startDestination = if (authViewModel.currentUser != null)
                SubGraph.Main else SubGraph.AUTH,
        ) {

            // Auth Graph
            navigation<SubGraph.AUTH>(
                startDestination = AuthDestination.REGISTRATION_FORM_SCREEN
            ) {
                composable<AuthDestination.REGISTRATION_FORM_SCREEN>() {
                    RegistrationFormScreen(
                        navHostController = navHostController
                    )
                }

                composable<AuthDestination.AUTHENTICATION_SCREEN>(
                    typeMap = mapOf(
                        typeOf<RegistrationData>() to RegistrationDataNavType.registrationDataType
                    ),
                    enterTransition = {
                        slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
                    },
                    exitTransition = {
                        slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
                    },
                    popEnterTransition = {
                        slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right)
                    },
                    popExitTransition = {
                        slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
                    }
                ) {
                    val arguments = it.toRoute<AuthDestination.AUTHENTICATION_SCREEN>()
                    AuthenticationScreen(
                        navHostController = navHostController,
                        arguments = arguments.registrationData,
                        comeFor = arguments.comeFor,
                        authViewModel = authViewModel,
                    )
                }
            }

            navigation<SubGraph.Main>(
                startDestination = SubGraph.NEWS,
            ) {
                navigation<SubGraph.NEWS>(
                    startDestination = NewsDestination.MAIN_SCREEN
                ) {
                    composable<NewsDestination.MAIN_SCREEN>() {
                        val newsViewModel = koinViewModel<NewsViewModel>()

                        NewsScreen(
                            navHostController = navHostController,
                            newsViewModel = newsViewModel,
                            animatedVisibilityScope = this
                        )
                    }

                    composable<NewsDestination.NEWS_DETAIL_SCREEN>(
                        deepLinks = listOf(
                            navDeepLink {
                                uriPattern = "$MY_URI/newsId={newsId}"
                            }
                        ),
                    ) {
                        val arguments = it.toRoute<NewsDestination.NEWS_DETAIL_SCREEN>()
                        val newsDetailViewModel = koinViewModel<NewsDetailViewModel>()

                        NewsDetailScreen(
                            newsId = arguments.newsId,
                            navHostController = navHostController,
                            newsDetailViewModel = newsDetailViewModel,
                            animatedVisibilityScope = this
                        )
                    }


                    composable<NewsDestination.NEWS_LINK_SCREEN>(
                        enterTransition = {
                            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
                        },
                        exitTransition = {
                            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
                        },
                        popEnterTransition = {
                            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right)
                        },
                        popExitTransition = {
                            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
                        }
                    ) {
                        val arguments = it.toRoute<NewsDestination.NEWS_LINK_SCREEN>()

                        NewsLinkScreen(
                            link = arguments.link,
                            navHostController = navHostController
                        )
                    }
                }

                navigation<SubGraph.EVENTS>(
                    startDestination = EventsDestination.EVENTS_SCREEN
                ) {
                    composable<EventsDestination.EVENTS_SCREEN> {
                        val eventsViewModel = koinViewModel<EventsViewModel>()

                        EventsScreen(
                            navHostController = navHostController,
                            eventsViewModel = eventsViewModel,
                            animatedVisibilityScope = this
                        )
                    }

                    composable<EventsDestination.EVENTS_DETAIL_SCREEN> {

                        val arguments = it.toRoute<EventsDestination.EVENTS_DETAIL_SCREEN>()
                        val eventsViewModel = koinViewModel<EventsViewModel>()

                        EventsDetailScreen(
                            eventId = arguments.eventId,
                            navHostController = navHostController,
                            eventsViewModel = eventsViewModel,
                            animatedVisibilityScope = this
                        )
                    }

                    composable<EventsDestination.BOOKED_EVENTS_SCREEN>() {
                        val eventsViewModel = koinViewModel<EventsViewModel>()
                        BookedEventsScreen(
                            navHostController = navHostController,
                            eventsViewModel = eventsViewModel,
                            animatedVisibilityScope = this
                        )
                    }

                }

                navigation<SubGraph.SETTINGS>(
                    startDestination = SettingsDestination.SETTINGS_SCREEN
                ) {

                    composable<SettingsDestination.SETTINGS_SCREEN>(
                        enterTransition = {
                            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
                        },
                        exitTransition = {
                            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
                        },
                        popEnterTransition = {
                            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right)
                        },
                        popExitTransition = {
                            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
                        }
                    ) {
                        SettingsScreen(
                            navHostController = navHostController,
                        )
                    }

                    navigation<SubGraph.SETTINGS_SAVED>(
                        startDestination = SettingsDestination.SAVED_SCREEN

                    ) {

                        composable<SettingsDestination.SAVED_SCREEN>(
                            enterTransition = {
                                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
                            },
                            exitTransition = {
                                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
                            },
                            popEnterTransition = {
                                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right)
                            },
                            popExitTransition = {
                                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
                            }
                        ) {

                            SavedScreen(
                                navHostController = navHostController,
                                animatedVisibilityScope = this
                            )
                        }

                        composable<SavedDestination.SAVED_NEWS_SCREEN>(
                            enterTransition = {
                                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
                            },
                            exitTransition = {
                                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
                            },
                            popEnterTransition = {
                                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right)
                            },
                            popExitTransition = {
                                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
                            }
                        ) {
                            val savedNewsViewModel = koinViewModel<SavedNewsViewModel>()

                            SavedNewsScreen(
                                navHostController = navHostController,
                                savedNewsViewModel = savedNewsViewModel,
                                animatedVisibilityScope = this
                            )
                        }

                        composable<SavedDestination.SAVED_EVENTS_SCREEN>() {
                            val savedEventsViewModel = koinViewModel<SavedEventsViewModel>()
                            SavedEventsScreen(
                                navHostController = navHostController,
                                savedEventsViewModel = savedEventsViewModel,
                                animatedVisibilityScope = this
                            )
                        }
                    }

                }

                composable<MainDestination.ACCOUNT_SCREEN>() {
                    val accountViewModel = koinViewModel<AccountViewModel>()
                    AccountScreen(
                        navHostController = navHostController,
                        accountViewModel = accountViewModel,
                        animatedVisibilityScope = this
                    )
                }

                composable<MainDestination.SEARCH_SCREEN>(
                    enterTransition = {
                        slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
                    },
                    exitTransition = {
                        slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
                    },
                    popEnterTransition = {
                        slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right)
                    },
                    popExitTransition = {
                        slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
                    }
                ) {
                    val searchViewModel = koinViewModel<SearchViewModel>()

                    SearchScreen(
                        navHostController = navHostController,
                        searchViewModel = searchViewModel,
                        animatedVisibilityScope = this
                    )
                }

            }
        }
    }
}