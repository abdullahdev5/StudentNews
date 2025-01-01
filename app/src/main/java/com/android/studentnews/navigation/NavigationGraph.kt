@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.android.studentnews.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.android.studentnews.R
import com.android.studentnews.auth.domain.RegistrationDataNavType
import com.android.studentnews.auth.domain.destination.AuthDestination
import com.android.studentnews.auth.domain.models.RegistrationData
import com.android.studentnews.auth.ui.AuthenticationScreen
import com.android.studentnews.auth.ui.RegistrationFormScreen
import com.android.studentnews.auth.ui.viewModel.AuthViewModel
import com.android.studentnews.core.domain.common.getUrlOfImageNotVideo
import com.android.studentnews.main.account.ui.AccountScreen
import com.android.studentnews.main.account.ui.viewmodel.AccountViewModel
import com.android.studentnews.main.events.EVENTS_REGISTRATION_URI
import com.android.studentnews.main.events.EVENTS_URI
import com.android.studentnews.main.events.domain.destination.EventsDestination
import com.android.studentnews.main.events.ui.screens.EventsDetailScreen
import com.android.studentnews.main.events.ui.viewModels.EventsViewModel
import com.android.studentnews.main.news.NEWS_ID
import com.android.studentnews.main.news.NEWS_URI
import com.android.studentnews.main.news.domain.destination.NewsDestinations
import com.android.studentnews.main.news.ui.screens.NewsDetailScreen
import com.android.studentnews.main.news.ui.screens.NewsLinkScreen
import com.android.studentnews.main.news.ui.screens.NewsListItemMoreBottomSheet
import com.android.studentnews.main.news.ui.viewModel.NewsDetailViewModel
import com.android.studentnews.main.referral_bonus.domain.EarnedPointsModelNavType
import com.android.studentnews.main.referral_bonus.domain.destination.ReferralBonusDestinations
import com.android.studentnews.main.referral_bonus.domain.model.EarnedPointsModel
import com.android.studentnews.main.referral_bonus.ui.composables.CongratulationDialpg
import com.android.studentnews.main.referral_bonus.ui.composables.PointsClaimDialog
import com.android.studentnews.main.referral_bonus.ui.screens.ReferralBonusScreen
import com.android.studentnews.main.referral_bonus.ui.viewModel.ReferralBonusViewModel
import com.android.studentnews.main.search.SearchScreen
import com.android.studentnews.main.search.SearchViewModel
import com.android.studentnews.main.settings.SettingsDestination
import com.android.studentnews.main.settings.SettingsScreen
import com.android.studentnews.main.settings.liked.LikedNewsScreen
import com.android.studentnews.main.settings.liked.LikedNewsViewModel
import com.android.studentnews.main.settings.registered_events.RegisteredEventsScreen
import com.android.studentnews.main.settings.registered_events.RegisteredEventsViewModel
import com.android.studentnews.main.settings.saved.domain.destination.SavedDestination
import com.android.studentnews.main.settings.saved.ui.screens.SavedEventsScreen
import com.android.studentnews.main.settings.saved.ui.screens.SavedNewsScreen
import com.android.studentnews.main.settings.saved.ui.screens.SavedScreen
import com.android.studentnews.main.settings.saved.ui.viewModels.SavedEventsViewModel
import com.android.studentnews.main.settings.saved.ui.viewModels.SavedNewsViewModel
import com.android.studentnews.news.domain.destination.MainDestination
import com.android.studentnews.news.domain.model.NewsModel
import com.android.studentnews.news.ui.NewsScreen
import com.android.studentnews.news.ui.viewModel.NewsViewModel
import com.android.studentnews.ui.theme.DarkColor
import com.android.studentnews.ui.theme.White
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.firebase.Timestamp
import org.koin.androidx.compose.koinViewModel
import kotlin.reflect.typeOf
import kotlin.to

private const val NAVIGATION_TIME = 500

@OptIn(ExperimentalMaterialNavigationApi::class)
@UnstableApi
@Composable
fun NavigationGraph(
    navHostController: NavHostController,
    bottomSheetNavigator: BottomSheetNavigator,
    authViewModel: AuthViewModel,
) {
    ModalBottomSheetLayout(
        bottomSheetNavigator,
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
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(NAVIGATION_TIME)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(NAVIGATION_TIME)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(NAVIGATION_TIME)
                        )

                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(NAVIGATION_TIME)
                        )
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
                        startDestination = NewsDestinations.NEWS_SCREEN
                    ) {
                        composable<NewsDestinations.NEWS_SCREEN>() {
                            val newsViewModel = koinViewModel<NewsViewModel>()
                            val accountViewModel = koinViewModel<AccountViewModel>()
                            val eventsViewModel = koinViewModel<EventsViewModel>()

                            NewsScreen(
                                navHostController = navHostController,
                                newsViewModel = newsViewModel,
                                eventsViewModel = eventsViewModel,
                                accountViewModel = accountViewModel,
                                animatedVisibilityScope = this,
                                sharedTransitionScope = this@SharedTransitionLayout
                            )
                        }

                        composable<NewsDestinations.NEWS_DETAIL_SCREEN>(
                            deepLinks = listOf(
                                navDeepLink {
                                    uriPattern = "$NEWS_URI/$NEWS_ID={$NEWS_ID}"
                                }
                            ),
                            enterTransition = { fadeIn() },
                            exitTransition = { fadeOut() },
                            popEnterTransition = { fadeIn() },
                            popExitTransition = { fadeOut() },
                        ) {
                            val arguments = it.toRoute<NewsDestinations.NEWS_DETAIL_SCREEN>()
                            val newsDetailViewModel = koinViewModel<NewsDetailViewModel>()
                            val accountViewModel = koinViewModel<AccountViewModel>()

                            NewsDetailScreen(
                                newsId = arguments.newsId,
                                navHostController = navHostController,
                                newsDetailViewModel = newsDetailViewModel,
                                accountViewModel = accountViewModel,
                                animatedVisibilityScope = this,
                                sharedTransitionScope = this@SharedTransitionLayout
                            )
                        }

                        bottomSheet(
                            route = NewsDestinations
                                .BottomSheetDestinations
                                .NEWS_LIST_ITEM_MORE_OPTIONS_BOTTOM_SHEET_DESTINATION +
                                    "/$NEWS_ID={$NEWS_ID}",
                            arguments = listOf(
                                navArgument(NEWS_ID) {
                                    type = NavType.StringType
                                }
                            )
                        ) {
                            val newsId = it.arguments?.getString(NEWS_ID) ?: ""
                            val newsDetailViewModel = koinViewModel<NewsDetailViewModel>()

                            LaunchedEffect(Unit) {
                                newsDetailViewModel.getNewsById(newsId)
                                newsDetailViewModel.getIsNewsSaved(newsId)
                            }
                            val context = LocalContext.current

                            val newsById by newsDetailViewModel.newsById.collectAsStateWithLifecycle()

                            val isNewsSaved = remember(newsDetailViewModel.isNewsSaved) {
                                derivedStateOf {
                                    newsDetailViewModel.isNewsSaved
                                }
                            }.value

                            NewsListItemMoreBottomSheet(
                                isNewsSaved = isNewsSaved,
                                onSave = {
                                    val newsForSave = NewsModel(
                                        title = newsById?.title ?: "",
                                        description = newsById?.description ?: "",
                                        newsId = newsId,
                                        category = newsById?.category ?: "",
                                        timestamp = Timestamp.now(),
                                        link = newsById?.link ?: "",
                                        linkTitle = newsById?.linkTitle ?: "",
                                        urlList = newsById?.urlList ?: emptyList(),
                                        shareCount = newsById?.shareCount ?: 0,
                                        likes = newsById?.likes ?: emptyList(),
                                    )

                                    if (isNewsSaved == true) {
                                        newsDetailViewModel.onNewsRemoveFromSave(
                                            news = newsForSave,
                                            wantToShowSuccessMessage = true
                                        )
                                    } else {
                                        newsDetailViewModel.onNewsSave(
                                            news = newsForSave,
                                            wantToShowSuccessMessage = true
                                        )
                                    }
                                },
                                onShare = {
                                    val title = newsById?.title ?: ""
                                    val imageUrl = getUrlOfImageNotVideo(
                                        urlList = newsById?.urlList ?: emptyList()
                                    )

                                    newsDetailViewModel
                                        .onNewsShare(
                                            title = title,
                                            imageUrl = imageUrl,
                                            context = context,
                                            newsId = newsId
                                        )
                                },
                                onDismiss = {
                                    navHostController.navigateUp()
                                },
                            )

                        }


                    composable<NewsDestinations.NEWS_LINK_SCREEN>(
                        enterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(NAVIGATION_TIME)
                            )
                        },
                        exitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(NAVIGATION_TIME)
                            )
                        },
                        popEnterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(NAVIGATION_TIME)
                            )

                        },
                        popExitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(NAVIGATION_TIME)
                            )
                        }
                    ) {
                        val arguments = it.toRoute<NewsDestinations.NEWS_LINK_SCREEN>()

                        NewsLinkScreen(
                            link = arguments.link,
                            navHostController = navHostController
                        )
                    }


                    // Events Detail Screen
                    composable<EventsDestination.EVENTS_DETAIL_SCREEN>(
                        deepLinks = listOf(
                            navDeepLink {
                                uriPattern = "$EVENTS_URI/eventId={eventId}"
                            },
                            navDeepLink {
                                uriPattern = "$EVENTS_REGISTRATION_URI/eventId={eventId}" +
                                        "/isComeForRegistration={isComeForRegistration}" +
                                        "/notificationId={notificationId}"
                            }
                        ),
                        enterTransition = { fadeIn() },
                        exitTransition = { fadeOut() },
                        popEnterTransition = { fadeIn() },
                        popExitTransition = { fadeOut() },
                    ) {

                        val arguments = it.toRoute<EventsDestination.EVENTS_DETAIL_SCREEN>()
                        val eventsViewModel = koinViewModel<EventsViewModel>()
                        val accountViewModel = koinViewModel<AccountViewModel>()

                        EventsDetailScreen(
                            eventId = arguments.eventId,
                            isComeForRegistration = arguments.isComeForRegistration,
                            notificationId = arguments.notificationId,
                            navHostController = navHostController,
                            eventsViewModel = eventsViewModel,
                            accountViewModel = accountViewModel,
                            animatedVisibilityScope = this,
                            sharedTransitionScope = this@SharedTransitionLayout
                        )
                    }

                }

                // EVents Graph
//                navigation<SubGraph.EVENTS>(
//                    startDestination = EventsDestination.EVENTS_SCREEN
//                ) {
//                    composable<EventsDestination.EVENTS_SCREEN> {
//                        val eventsViewModel = koinViewModel<EventsViewModel>()
//
//                        EventsScreen(
//                            navHostController = navHostController,
//                            eventsViewModel = eventsViewModel,
//                            animatedVisibilityScope = this,
//                            sharedTransitionScope = this@SharedTransitionLayout,
//                            modifier = Modifier
//                                .statusBarsPadding()
//                        )
//                    }
//
//                    composable<EventsDestination.EVENTS_DETAIL_SCREEN>(
//                        deepLinks = listOf(
//                            navDeepLink {
//                                uriPattern = "$EVENTS_URI/eventId={eventId}"
//                            },
//                            navDeepLink {
//                                uriPattern = "$EVENTS_REGISTRATION_URI/eventId={eventId}" +
//                                        "/isComeForRegistration={isComeForRegistration}" +
//                                        "/notificationId={notificationId}"
//                            }
//                        )
//                    ) {
//
//                        val arguments = it.toRoute<EventsDestination.EVENTS_DETAIL_SCREEN>()
//                        val eventsViewModel = koinViewModel<EventsViewModel>()
//
//                        EventsDetailScreen(
//                            eventId = arguments.eventId,
//                            isComeForRegistration = arguments.isComeForRegistration,
//                            notificationId = arguments.notificationId,
//                            navHostController = navHostController,
//                            eventsViewModel = eventsViewModel,
//                            animatedVisibilityScope = this,
//                            sharedTransitionScope = this@SharedTransitionLayout
//                        )
//                    }
//
//                }

                // Saved Graph
                navigation<SubGraph.SAVED>(
                    startDestination = SavedDestination.SAVED_SCREEN

                ) {

                    composable<SavedDestination.SAVED_SCREEN>(
                        enterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(NAVIGATION_TIME)
                            )
                        },
                        exitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(NAVIGATION_TIME)
                            )
                        },
                        popEnterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(NAVIGATION_TIME)
                            )

                        },
                        popExitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(NAVIGATION_TIME)
                            )
                        }
                    ) {

                        SavedScreen(
                            navHostController = navHostController,
                            animatedVisibilityScope = this,
                            sharedTransitionScope = this@SharedTransitionLayout
                        )
                    }

                    composable<SavedDestination.SAVED_NEWS_SCREEN>() {
                        val savedNewsViewModel = koinViewModel<SavedNewsViewModel>()

                        SavedNewsScreen(
                            navHostController = navHostController,
                            savedNewsViewModel = savedNewsViewModel,
                            animatedVisibilityScope = this,
                            sharedTransitionScope = this@SharedTransitionLayout
                        )
                    }

                    composable<SavedDestination.SAVED_EVENTS_SCREEN>() {
                        val savedEventsViewModel = koinViewModel<SavedEventsViewModel>()
                        SavedEventsScreen(
                            navHostController = navHostController,
                            savedEventsViewModel = savedEventsViewModel,
                            animatedVisibilityScope = this,
                            sharedTransitionScope = this@SharedTransitionLayout
                        )
                    }
                }

                // Liked News Screen
                composable<NewsDestinations.LIKED_NEWS_SCREEN>(
                    enterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(NAVIGATION_TIME)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(NAVIGATION_TIME)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(NAVIGATION_TIME)
                        )

                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(NAVIGATION_TIME)
                        )
                    }
                ) {
                    val likedNewsViewModel = koinViewModel<LikedNewsViewModel>()
                    LikedNewsScreen(
                        navHostController = navHostController,
                        likedNewsViewModel = likedNewsViewModel,
                        animatedVisibilityScope = this,
                        sharedTransitionScope = this@SharedTransitionLayout
                    )
                }

                // Registered Events Screen
                composable<EventsDestination.REGISTERED_EVENTS_SCREEN>(
                    enterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(NAVIGATION_TIME)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(NAVIGATION_TIME)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(NAVIGATION_TIME)
                        )

                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(NAVIGATION_TIME)
                        )
                    }
                ) {
                    val registeredEventsViewModel = koinViewModel<RegisteredEventsViewModel>()
                    RegisteredEventsScreen(
                        navHostController = navHostController,
                        registeredEventsViewModel = registeredEventsViewModel,
                        animatedVisibilityScope = this,
                        sharedTransitionScope = this@SharedTransitionLayout
                    )
                }

                navigation<SubGraph.SETTINGS>(
                    startDestination = SettingsDestination.SETTINGS_SCREEN
                ) {

                    // Settings Screen
                    composable<SettingsDestination.SETTINGS_SCREEN>(
                        enterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(NAVIGATION_TIME)
                            )
                        },
                        exitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(NAVIGATION_TIME)
                            )
                        },
                        popEnterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(NAVIGATION_TIME)
                            )

                        },
                        popExitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(NAVIGATION_TIME)
                            )
                        }
                    ) {
                        SettingsScreen(
                            navHostController = navHostController,
                        )
                    }

                }

                composable<MainDestination.ACCOUNT_SCREEN>(
                    enterTransition = { fadeIn() },
                    exitTransition = { fadeOut() },
                    popEnterTransition = { fadeIn() },
                    popExitTransition = { fadeOut() },
                ) {
                    val accountViewModel = koinViewModel<AccountViewModel>()
                    AccountScreen(
                        navHostController = navHostController,
                        accountViewModel = accountViewModel,
                        animatedVisibilityScope = this,
                        sharedTransitionScope = this@SharedTransitionLayout
                    )
                }

                composable<MainDestination.SEARCH_SCREEN>(
                    enterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(NAVIGATION_TIME)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(NAVIGATION_TIME)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(NAVIGATION_TIME)
                        )

                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(NAVIGATION_TIME)
                        )
                    }
                ) {
                    val searchViewModel = koinViewModel<SearchViewModel>()

                    SearchScreen(
                        navHostController = navHostController,
                        searchViewModel = searchViewModel,
                        animatedVisibilityScope = this,
                        sharedTransitionScope = this@SharedTransitionLayout
                    )
                }

                }
                // Referral Bonus Graph
                navigation<SubGraph.REFERRAL_BONUS>(
                    startDestination = ReferralBonusDestinations.REFERRAL_BONUS_SCREEN
                ) {
                    // Referral Bonus Screen
                    composable<ReferralBonusDestinations.REFERRAL_BONUS_SCREEN>(
                        enterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(NAVIGATION_TIME)
                            )
                        },
                        exitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(NAVIGATION_TIME)
                            )
                        },
                        popEnterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(NAVIGATION_TIME)
                            )

                        },
                        popExitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(NAVIGATION_TIME)
                            )
                        }
                    ) {
                        val referralBonusViewModel = koinViewModel<ReferralBonusViewModel>()

                        ReferralBonusScreen(
                            navHostController = navHostController,
                            referralBonusViewModel = referralBonusViewModel,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this@composable
                        )
                    }

                    dialog<ReferralBonusDestinations.CLAIM_POINTS_DIALOG>(
                        typeMap = mapOf(
                            typeOf<EarnedPointsModel>() to EarnedPointsModelNavType.earnedPointsModelType
                        )
                    ) {
                        val arguments =
                            it.toRoute<ReferralBonusDestinations.CLAIM_POINTS_DIALOG>()
                        val referralBonusViewModel = koinViewModel<ReferralBonusViewModel>()

                        PointsClaimDialog(
                            titleText = {
                                arguments.titleText
                            },
                            descriptionText = {
                                arguments.descriptionText
                            },
                            onClaim = {
                                navHostController.navigateUp()
                                referralBonusViewModel.onReferralPointsCollect(
                                    arguments.earnedPointsModel
                                )
                                navHostController.navigate(
                                    ReferralBonusDestinations.CONGRATULATION_DIALOG(
                                        resId = R.raw.reward_anim,
                                        lottieHeight = 200,
                                        titleText = "Congratulations",
                                        descriptionText = "You Earned" +
                                                " ${arguments.earnedPointsModel.earnedPoints}" +
                                                " Points."
                                    )
                                )
                            },
                            onDismiss = {
                                navHostController.navigateUp()
                            }
                        )
                    }

                    dialog<ReferralBonusDestinations.CONGRATULATION_DIALOG>() { entry ->
                        val arguments = entry.toRoute<ReferralBonusDestinations.CONGRATULATION_DIALOG>()

                        CongratulationDialpg(
                            resId = { arguments.resId },
                            lottieHeight = { arguments.lottieHeight },
                            titleText = { arguments.titleText },
                            descriptionText = { arguments.descriptionText },
                            onConfirm = { navHostController.navigateUp() },
                            onDismiss = { navHostController.navigateUp() }
                        )
                    }

                }

            }
        }
    }
}