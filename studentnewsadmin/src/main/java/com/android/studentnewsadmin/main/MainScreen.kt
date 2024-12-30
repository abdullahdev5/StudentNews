@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.studentnewsadmin.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.studentnewsadmin.main.events.ui.screens.EventsScreen
import com.android.studentnewsadmin.main.events.ui.viewModels.EventsViewModel
import com.android.studentnewsadmin.main.navigation.Destination
import com.android.studentnewsadmin.main.news.ui.screens.NewsScreen
import com.android.studentnewsadmin.main.news.ui.viewmodel.NewsViewModel
import com.android.studentnewsadmin.main.offers.ui.screens.OffersScreen
import com.android.studentnewsadmin.main.offers.ui.viewModel.OffersViewModel
import com.android.studentnewsadmin.ui.theme.Green
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(
    navHostController: NavHostController,
) {

    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scrollBehaviour = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    var isActionButtonClick by remember { mutableStateOf(false) }
    val animatedHeight = remember { Animatable(0f) }


    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }


    LaunchedEffect(isActionButtonClick) {
        if (isActionButtonClick) {
            animatedHeight.animateTo(200f)
        }
    }

    BackHandler(isActionButtonClick || drawerState.isOpen) {
        if (isActionButtonClick) {
            scope.launch {
                animatedHeight.animateTo(0f)
            }.invokeOnCompletion {
                isActionButtonClick = false
            }
        }
        if (drawerState.isOpen) {
            scope.launch {
                drawerState.close()
            }
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = if (isSystemInDarkTheme()) Color.DarkGray else Color.White,
                    modifier = Modifier
                        .widthIn(max = configuration.screenWidthDp.dp / 1.3f)
                ) {
                    DrawerContent(
                        onUploadNewsClick = {
                            scope.launch {
                                drawerState.close()
                            }.invokeOnCompletion {
                                navHostController.navigate(Destination.UPLOAD_NEWS_SCREEN)
                            }
                        },
                        onUploadCategoryClick = {
                            scope.launch {
                                drawerState.close()
                            }.invokeOnCompletion {
                                navHostController.navigate(Destination.UPLOAD_CATEGORY_SCREEN)
                            }
                        },
                        onUploadEvents = {
                            scope.launch {
                                drawerState.close()
                            }.invokeOnCompletion {
                                navHostController.navigate(Destination.UPLOAD_EVENTS_SCREEN)
                            }
                        },
                        onUploadOffers = {
                            scope.launch {
                                drawerState.close()
                            }.invokeOnCompletion {
                                navHostController.navigate(Destination.UPLOAD_OFFERS_SCREEN)
                            }
                        }
                    )
                }
            }
        ) {

            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = if (selectedIndex == 0) "News" else if (selectedIndex == 1)
                                    "Events" else if (selectedIndex == 2) "Offers" else ""
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Icon for Drawer"
                                )
                            }
                        },
                        scrollBehavior = scrollBehaviour,
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            scrolledContainerColor = Color.Transparent
                        )
                    )
                },
                floatingActionButton = {

                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {

                        if (isActionButtonClick) {

                            Column(
                                verticalArrangement = Arrangement.spacedBy(5.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .height(animatedHeight.value.dp)
                            ) {

                                SmallFloatingActionButton(
                                    onClick = {
                                        isActionButtonClick = false
                                        navHostController.navigate(Destination.UPLOAD_NEWS_SCREEN)
                                    },
                                    containerColor = if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PostAdd,
                                        contentDescription = "Icon for News"
                                    )
                                }

                                SmallFloatingActionButton(
                                    onClick = {
                                        isActionButtonClick = false
                                        navHostController.navigate(Destination.UPLOAD_CATEGORY_SCREEN)
                                    },
                                    containerColor = if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Category,
                                        contentDescription = "Icon for Category"
                                    )
                                }

                                SmallFloatingActionButton(
                                    onClick = {
                                        isActionButtonClick = false
                                        navHostController.navigate(Destination.UPLOAD_EVENTS_SCREEN)
                                    },
                                    containerColor = if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Event,
                                        contentDescription = "Icon for Events"
                                    )
                                }

                                SmallFloatingActionButton(
                                    onClick = {
                                        isActionButtonClick = false
                                        navHostController.navigate(Destination.UPLOAD_OFFERS_SCREEN)
                                    },
                                    containerColor = if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.LocalOffer,
                                        contentDescription = "Icon for Offers"
                                    )
                                }


                            }

                        }

                        FloatingActionButton(
                            onClick = {
                                scope.launch {
                                    delay(500L)
                                }.invokeOnCompletion {
                                    if (!isActionButtonClick) {
                                        isActionButtonClick = true
                                    } else {
                                        scope.launch {
                                            animatedHeight.animateTo(0f)
                                        }.invokeOnCompletion {
                                            isActionButtonClick = false
                                        }
                                    }
                                }
                            },
                            containerColor = Green,
                            contentColor = Color.White
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Icon for Add News"
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehaviour.nestedScrollConnection)
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                ) {
                    TabRow(
                        selectedTabIndex = selectedIndex,
                    ) {
                        Tab(
                            selected = selectedIndex == 0,
                            onClick = { selectedIndex = 0 },
                            text = { Text("News") }
                        )

                        Tab(
                            selected = selectedIndex == 1,
                            onClick = { selectedIndex = 1 },
                            text = { Text("Events") }
                        )

                        Tab(
                            selected = selectedIndex == 2,
                            onClick = { selectedIndex = 2 },
                            text = { Text("Offers") }
                        )
                    }

                    when (selectedIndex) {
                        0 -> {
                            val newsViewModel = koinViewModel<NewsViewModel>()
                            NewsScreen(
                                navHostController = navHostController,
                                newsViewModel = newsViewModel
                            )
                        }

                        1 -> {
                            val eventsViewModel = koinViewModel<EventsViewModel>()
                            EventsScreen(
                                navHostController = navHostController,
                                eventsViewModel = eventsViewModel
                            )
                        }

                        2 -> {
                            val offersViewModel = koinViewModel<OffersViewModel>()
                            OffersScreen(
                                navHostController = navHostController,
                                offersViewModel = offersViewModel
                            )
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun DrawerContent(
    onUploadNewsClick: () -> Unit,
    onUploadCategoryClick: () -> Unit,
    onUploadEvents: () -> Unit,
    onUploadOffers: () -> Unit,
) {
    // Upload News
    NavigationDrawerItem(
        label = {
            Text(text = "Upload News")
        },
        icon = {
            Icon(
                imageVector = Icons.Default.PostAdd,
                contentDescription = "Icon for News"
            )
        },
        onClick = {
            onUploadNewsClick.invoke()
        },
        selected = false,
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
        ),
        shape = RectangleShape,
    )

    // Upload Category
    NavigationDrawerItem(
        label = {
            Text(text = "Upload Category")
        },
        onClick = {
            onUploadCategoryClick.invoke()
        },
        icon = {
            Icon(
                imageVector = Icons.Default.Category,
                contentDescription = "Icon for Category"
            )
        },
        selected = false,
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
        ),
        shape = RectangleShape,
    )

    // Upload Events
    NavigationDrawerItem(
        label = {
            Text(text = "Upload Events")
        },
        onClick = {
            onUploadEvents.invoke()
        },
        icon = {
            Icon(
                imageVector = Icons.Default.Event,
                contentDescription = "Icon for Events"
            )
        },
        selected = false,
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
        ),
        shape = RectangleShape,
    )

    // Upload Offers
    NavigationDrawerItem(
        label = {
            Text(text = "Upload Offers")
        },
        onClick = {
            onUploadOffers.invoke()
        },
        icon = {
            Icon(
                imageVector = Icons.Outlined.LocalOffer,
                contentDescription = "Icon for Offers"
            )
        },
        selected = false,
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
        ),
        shape = RectangleShape,
    )

}