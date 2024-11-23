@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalSharedTransitionApi::class, ExperimentalCoilApi::class
)

package com.android.studentnews.news.ui

import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import com.android.studentnews.auth.domain.models.UserModel
import com.android.studentnews.core.data.snackbar_controller.SnackBarActions
import com.android.studentnews.core.data.snackbar_controller.SnackBarController
import com.android.studentnews.core.data.snackbar_controller.SnackBarEvents
import com.android.studentnews.core.domain.common.isInternetAvailable
import com.android.studentnews.core.domain.constants.FontSize
import com.android.studentnews.core.domain.constants.Status
import com.android.studentnews.core.ui.common.LoadingDialog
import com.android.studentnews.main.NavigationBarItems
import com.android.studentnews.main.events.ui.screens.EventsScreen
import com.android.studentnews.main.events.ui.viewModels.EventsViewModel
import com.android.studentnews.main.news.domain.destination.NewsDestination
import com.android.studentnews.main.news.domain.model.CategoryModel
import com.android.studentnews.main.news.ui.screens.getUrlOfImageNotVideo
import com.android.studentnews.navigation.SubGraph
import com.android.studentnews.news.domain.destination.MainDestination
import com.android.studentnews.news.domain.model.NewsModel
import com.android.studentnews.news.domain.resource.NewsState
import com.android.studentnews.news.ui.viewModel.NewsViewModel
import com.android.studentnews.ui.theme.Black
import com.android.studentnews.ui.theme.DarkGray
import com.android.studentnews.ui.theme.Gray
import com.android.studentnews.ui.theme.Green
import com.android.studentnews.ui.theme.White
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@SuppressLint(
    "UnusedMaterial3ScaffoldPaddingParameter", "RememberReturnType",
    "FrequentlyChangedStateReadInComposition"
)
@Composable
fun SharedTransitionScope.NewsScreen(
    navHostController: NavHostController,
    newsViewModel: NewsViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val drawerScrollState = rememberScrollState()
    val lazyListState = rememberLazyListState()
    val pullToRefreshState = rememberPullToRefreshState()
    val configuration = LocalConfiguration.current
    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )
    val tabPagerState = rememberPagerState(pageCount = { 2 })
    val topBarScrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())


    var newsCategoryStatus by rememberSaveable { mutableStateOf("") }
    var isMoreDropDownMenuItemOpen by rememberSaveable { mutableStateOf(false) }

    val newsList = newsViewModel.newsList.collectAsStateWithLifecycle()
    val categoriesList = newsViewModel.categoriesList.collectAsStateWithLifecycle()
    val currentUser = newsViewModel.currentUser.collectAsStateWithLifecycle()

    val categoryPagerState = rememberPagerState(
        pageCount = {
            categoriesList.value.size
        }
    )


    BackHandler(drawerState.isOpen || tabPagerState.currentPage != 0) {
        scope.launch {
            if (drawerState.isOpen) {
                drawerState.close()

            } else if (tabPagerState.currentPage != 0) {
                tabPagerState.animateScrollToPage(0)
            }
        }
    }


    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = true,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = if (isSystemInDarkTheme()) DarkGray.copy(0.9f) else White.copy(
                        0.9f
                    ),
                    modifier = Modifier
                        .widthIn(max = configuration.screenWidthDp.dp / 1.3f)
                ) {
                    MainDrawerContent(
                        currentUser = currentUser.value,
                        scrollState = drawerScrollState,
                        context = context,
                        animatedVisibilityScope = animatedVisibilityScope,
                        onAccountClick = {
                            navHostController.navigate(MainDestination.ACCOUNT_SCREEN)
                        },
                        onSearchClick = {
                            navHostController.navigate(MainDestination.SEARCH_SCREEN)
                        },
                        onSavedNewsClick = {
                            navHostController.navigate(NewsDestination.SAVED_NEWS_SCREEN)
                        },
                        onSignOutClick = {
                            newsViewModel.cancelPeriodicNewsWorkRequest()
                            context.cacheDir.delete()
                            context.imageLoader.memoryCache?.clear()
                            context.imageLoader.diskCache?.clear()
                            newsViewModel.signOut()
                            navHostController.navigate(SubGraph.AUTH) {
                                popUpTo(SubGraph.Main) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        },
                        onDismiss = {
                            scope.launch {
                                drawerState.close()
                            }
                        }
                    )
                }
            },
        ) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = if (tabPagerState.currentPage == 0) "News"
                                else "Events",
                            )
                        },
                        actions = {
                            IconButton(onClick = {
                                isMoreDropDownMenuItemOpen = true
                            }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Icon for More"
                                )
                            }
                            if (isMoreDropDownMenuItemOpen) {
                                MoreDropDownMenu(
                                    expanded = isMoreDropDownMenuItemOpen,
                                    onSearchClick = {
                                        navHostController.navigate(MainDestination.SEARCH_SCREEN)
                                    },
                                    onAccountClick = {
                                        navHostController.navigate(MainDestination.ACCOUNT_SCREEN)
                                    },
                                    onDismiss = {
                                        isMoreDropDownMenuItemOpen = false
                                    }
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Icon for Navigation Drawer",
                                )
                            }
                        },
                        scrollBehavior = topBarScrollBehavior,
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            scrolledContainerColor = Color.Transparent
                        ),
                    )
                },
                bottomBar = {
                    var selectedNavBarIndex by remember { mutableStateOf(0) }

                    val navBarList = listOf(
                        NavigationBarItems.Home,
                        NavigationBarItems.Search,
                        NavigationBarItems.Account,
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .height(50.dp),
                    ) {

                        HorizontalDivider(color = Gray)

                        BottomAppBar(
                            containerColor = Color.Transparent,
                            modifier = Modifier
                                .fillMaxWidth(),
                        ) {
                            navBarList.forEachIndexed { index, item ->
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            imageVector = if (selectedNavBarIndex == index) item.selectedIcon else item.unselectedIcon,
                                            contentDescription = null
                                        )
                                    },
                                    selected = index == selectedNavBarIndex,
                                    onClick = {
                                        selectedNavBarIndex = index
                                        if (selectedNavBarIndex == 0) {
                                            if (lazyListState.firstVisibleItemIndex != 0) {
                                                scope.launch {
                                                    lazyListState.scrollToItem(0)
                                                }
                                            }
                                        }
                                        if (selectedNavBarIndex == 1) {
                                            navHostController.navigate(MainDestination.SEARCH_SCREEN)
                                        }
                                        if (selectedNavBarIndex == 2) {
                                            navHostController.navigate(MainDestination.ACCOUNT_SCREEN)
                                        }
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = Color.Transparent,
                                        selectedIconColor = if (isSystemInDarkTheme()) White else Black,
                                        unselectedIconColor = if (isSystemInDarkTheme()) White else Black
                                    )
                                )
                            }
                        }
                    }
                },
                modifier = Modifier
                    .nestedScroll(topBarScrollBehavior.nestedScrollConnection)
            ) { innerPadding ->

                Column(
                    modifier = Modifier
                        .padding(paddingValues = innerPadding)
                ) {

                    AnimatedVisibility(
                        lazyListState.firstVisibleItemIndex < 1 &&
                                tabPagerState.currentPage == 0
                    ) {
                        HorizontalPager(
                            state = categoryPagerState,
                            pageSpacing = if (categoryPagerState.currentPage != categoryPagerState.pageCount - 1)
                                (-30).dp else (-15).dp,
                            flingBehavior = PagerDefaults.flingBehavior(
                                state = categoryPagerState,
                                pagerSnapDistance = PagerSnapDistance.atMost(
                                    1
                                ),
                                snapAnimationSpec = spring(
                                    stiffness = Spring.StiffnessVeryLow,
                                    dampingRatio = Spring.DampingRatioLowBouncy
                                )
                            ),
                            modifier = Modifier
                                .height(270.dp)
//                                    .height(newsCategoryPagerHeight.value)
                        ) { pagerIndex ->
                            val item = categoriesList.value[pagerIndex]
                            CategoriesListPagerItem(
                                item = item,
                                categoriesList = categoriesList.value,
                                categoryPagerState = categoryPagerState,
                                context = context,
                                onItemCLick = { category ->
                                    newsViewModel.getNewsListByCategory(
                                        category
                                    )
                                    newsCategoryStatus =
                                        "${category} News"
                                }
                            )
                        }

                    }


                    Column {

                        TabRow(
                            selectedTabIndex = tabPagerState.currentPage,
                        ) {
                            // News
                            Tab(
                                selected = tabPagerState.currentPage == 0,
                                onClick = {
                                    scope.launch {
                                        tabPagerState.animateScrollToPage(0)
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Newspaper,
                                        contentDescription = "Icon For News"
                                    )
                                },
                                text = {
                                    Text(text = "News")
                                },
                                selectedContentColor = Green,
                                unselectedContentColor = if (isSystemInDarkTheme()) White else Black
                            )
                            // Events
                            Tab(
                                selected = tabPagerState.currentPage == 1,
                                onClick = {
                                    scope.launch {
                                        tabPagerState.animateScrollToPage(1)
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Event,
                                        contentDescription = "Icon For Events"
                                    )
                                },
                                text = {
                                    Text(text = "Events")
                                },
                                selectedContentColor = Green,
                                unselectedContentColor = if (isSystemInDarkTheme()) White else Black
                            )
                        }

                        AnimatedVisibility(tabPagerState.currentPage == 0) {
                            Text(
                                text = if (newsCategoryStatus.isNotEmpty())
                                    newsCategoryStatus else "For You",
                                style = TextStyle(
                                    fontSize = FontSize.LARGE.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Gray
                                ),
                                modifier = Modifier
                                    .padding(all = 15.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(pullToRefreshState.nestedScrollConnection),
                    ) {
                        HorizontalPager(
                            state = tabPagerState
                        ) { page ->
                            when (page) {

                                0 -> {

                                    LaunchedEffect(newsViewModel.isRefreshing.value) {
                                        if (newsViewModel.isRefreshing.value) {
                                            scope.launch {
                                                pullToRefreshState.startRefresh()
                                                delay(1000L)
                                                if (tabPagerState.currentPage == 0) {
                                                    newsViewModel.getNewsList()
                                                    newsCategoryStatus = ""
                                                }
                                            }
                                        } else {
                                            pullToRefreshState.endRefresh()
                                        }
                                    }

                                    LazyColumn(
                                        state = lazyListState,
                                        flingBehavior = ScrollableDefaults.flingBehavior(),
                                        userScrollEnabled = true,
                                        modifier = Modifier
                                            .fillMaxSize(),
                                    ) {

                                        items(
                                            count = newsList.value.size,
                                            key = { index ->
                                                newsList.value[index].newsId ?: ""
                                            }
                                        ) { index ->
                                            val item = newsList.value[index]

                                            NewsItem(
                                                item = item,
                                                onItemClick = { newsId ->
                                                    navHostController.navigate(
                                                        NewsDestination.NEWS_DETAIL_SCREEN(
                                                            newsId
                                                        )
                                                    )
                                                },
                                                context = context,
                                                animatedVisibilityScope = animatedVisibilityScope,
                                                onSave = { it ->
                                                    if (isInternetAvailable(context)) {

                                                        val news = NewsModel(
                                                            newsId = it.newsId,
                                                            title = it.title,
                                                            description = it.description,
                                                            category = it.category,
                                                            timestamp = Timestamp.now(),
                                                            link = it.link,
                                                            linkTitle = it.linkTitle,
                                                            urlList = it.urlList,
                                                            shareCount = it.shareCount ?: 0,
                                                            likes = it.likes
                                                        )

                                                        newsViewModel.onNewsSave(
                                                            news,
                                                            onSeeAction = { thisNewsId ->
                                                                navHostController.navigate(
                                                                    NewsDestination.NEWS_DETAIL_SCREEN(
                                                                        thisNewsId
                                                                    )
                                                                )
                                                            },
                                                        )
                                                    } else {
                                                        scope.launch {
                                                            SnackBarController
                                                                .sendEvent(
                                                                    SnackBarEvents(
                                                                        message = "No Internet Connection!",
                                                                        duration = SnackbarDuration.Long
                                                                    )
                                                                )
                                                        }
                                                    }
                                                }
                                            )
                                        }

                                    }
                                }

                                1 -> {
                                    val eventsViewModel = koinViewModel<EventsViewModel>()

                                    LaunchedEffect(newsViewModel.isRefreshing.value) {
                                        if (newsViewModel.isRefreshing.value) {
                                            scope.launch {
                                                pullToRefreshState.startRefresh()
                                                delay(1000L)
                                                if (tabPagerState.currentPage == 1) {
                                                    eventsViewModel.getEventsList()
                                                }
                                            }
                                        } else {
                                            pullToRefreshState.endRefresh()
                                        }
                                    }

                                    EventsScreen(
                                        navHostController = navHostController,
                                        eventsViewModel = eventsViewModel,
                                        animatedVisibilityScope = animatedVisibilityScope
                                    )
                                }

                            }
                        }

                        if (pullToRefreshState.isRefreshing) {
                            LaunchedEffect(true) {
                                scope.launch {
                                    newsViewModel.isRefreshing.value = true
                                    delay(1000L)
                                    newsViewModel.isRefreshing.value = false
                                }
                            }
                        }

                        PullToRefreshContainer(
                            state = pullToRefreshState,
                            containerColor = Color.Transparent,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                        )

                    }

                    if (newsViewModel.newsListStatus.value == Status.Loading) {
                        LoadingDialog()
                    }

                    if (newsViewModel.newsListStatus.value == Status.FAILED
                        || newsList.value.isEmpty()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues = innerPadding),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (newsViewModel.newsListStatus.value == Status.FAILED)
                                    "${newsViewModel.errorMsg}"
                                else if (newsList.value.isEmpty()) {
                                    if (newsViewModel.newsListStatus.value == Status.SUCCESS)
                                        "No News Found!" else ""
                                } else ""
                            )
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun SharedTransitionScope.NewsItem(
    item: NewsModel?,
    context: Context,
    onItemClick: (String) -> Unit,
    onSave: (NewsModel) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 10.dp,
                bottom = 10.dp,
            )
            .sharedElement(
                state = rememberSharedContentState(key = "container/${item?.newsId}"),
                animatedVisibilityScope = animatedVisibilityScope,
                renderInOverlayDuringTransition = true
            ),
        colors = CardDefaults.cardColors(
            containerColor = Green.copy(0.1f) // LightGray.copy(alpha = 0.3f)
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 10.dp)
                .clickable {
                    onItemClick.invoke(item?.newsId ?: "")
                },
        ) {
            Column(
                modifier = Modifier
                    .padding(end = 5.dp)
                    .weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {

                    IconButton(
                        onClick = {
                            item?.let {
                                onSave.invoke(it)
                            }
                        },
                        modifier = Modifier
                            .width(20.dp)
                            .height(20.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.BookmarkAdd,
                            contentDescription = "Icon for unSaved News"
                        )
                    }

                    Column(
                        modifier = Modifier
                            .background(
                                color = Black.copy(0.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Text(
                            text = item?.category ?: "",
                            modifier = Modifier
                                .padding(all = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = item?.title ?: "",
                    style = TextStyle(
                        fontSize = (FontSize.MEDIUM - 1).sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .sharedElement(
                            state = rememberSharedContentState(key = "title/${item?.newsId}"),
                            animatedVisibilityScope = animatedVisibilityScope,
                            renderInOverlayDuringTransition = true,
                        )
                )
                Text(
                    text = item?.description ?: "",
                    style = TextStyle(
                        fontSize = FontSize.SMALL.sp,
                        color = Gray,
                    ),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(top = 10.dp)
                )
            }

            val imageRequest = ImageRequest.Builder(context)
                .data(getUrlOfImageNotVideo(item?.urlList ?: emptyList()))
                .crossfade(true)
                .build()

            AsyncImage(
                model = imageRequest,
                contentDescription = "News Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(90.dp)
                    .heightIn(max = 100.dp)
                    .clip(shape = RoundedCornerShape(10.dp))
                    .sharedElement(
                        state = rememberSharedContentState(key = "image/${item?.newsId ?: ""}"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(10.dp))
                    )
            )
        }
    }
}

@Composable
fun CategoriesListPagerItem(
    item: CategoryModel,
    categoriesList: List<CategoryModel>,
    categoryPagerState: PagerState,
    context: Context,
    onItemCLick: (String) -> Unit,
) {
    Column {

        Card(
            modifier = Modifier
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 20.dp,
                    bottom = 5.dp
                )
                .clickable {
                    onItemCLick.invoke(item.name ?: "")
                }
                .weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(item.imageUrl ?: "")
                        .crossfade(true)
                        .build(),
                    contentDescription = "Category Image",
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                )

                Text(
                    text = item.name ?: "",
                    style = TextStyle(
                        fontSize = FontSize.LARGE.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    ),
                    modifier = Modifier
                        .padding(all = 10.dp)
                        .align(alignment = Alignment.BottomStart)
                        .shadow(
                            elevation = 10.dp,
                        )
                )


            }
        }

        Row(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally),
        ) {
            repeat(categoriesList.size) { index ->
                PagerIndicator(
                    index = index,
                    pagerState = categoryPagerState
                )
            }
        }

    }
}

@Composable
fun PagerIndicator(
    index: Int,
    pagerState: PagerState,
) {
    Card(
        modifier = Modifier
            .width(if (pagerState.currentPage == index) 18.dp else 15.dp)
            .height(if (pagerState.currentPage == index) 18.dp else 15.dp)
            .clip(shape = CircleShape)
            .padding(all = 5.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (pagerState.currentPage == index) {
                Green
            } else {
                Gray
            }
        )
    ) {

    }
}

@Composable
fun SharedTransitionScope.MainDrawerContent(
    currentUser: UserModel?,
    scrollState: ScrollState,
    context: Context,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onAccountClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSavedNewsClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(all = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                val color = currentUser?.profilePicBgColor ?: 0

                Card(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .clip(shape = CircleShape)
                        .sharedElement(
                            state = rememberSharedContentState(key = "user_image/${currentUser?.uid}"),
                            animatedVisibilityScope = animatedVisibilityScope,
                            clipInOverlayDuringTransition = OverlayClip(CircleShape),
                            renderInOverlayDuringTransition = true
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (currentUser?.profilePic.isNullOrEmpty())
                            Color(color)
                        else
                            Color.LightGray
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(true) {
                                detectTapGestures(
                                    onTap = {
                                        onAccountClick.invoke()
                                    }
                                )
                            },
                    ) {
                        if (currentUser?.profilePic.isNullOrEmpty()) {
                            Text(
                                text = currentUser?.registrationData?.name?.first().toString()
                                    ?: "",
                                color = White,
                                fontSize = FontSize.LARGE.sp,
                                modifier = Modifier
                                    .align(alignment = Alignment.Center)
                                    .shadow(
                                        elevation = 20.dp,
                                        shape = CircleShape,
                                        ambientColor = if (isSystemInDarkTheme()) White else Black,
                                        spotColor = if (isSystemInDarkTheme()) White else Black
                                    )
                            )
                        } else {

                            val imageRequest = ImageRequest.Builder(context)
                                .data(currentUser?.profilePic ?: "")
                                .crossfade(true)
                                .build()

                            AsyncImage(
                                model = imageRequest,
                                contentDescription = "User Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = currentUser?.registrationData?.name ?: "",
                    style = TextStyle(
                        fontSize = FontSize.MEDIUM.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .sharedElement(
                            state = rememberSharedContentState(key = "user_name/${currentUser?.uid}"),
                            animatedVisibilityScope = animatedVisibilityScope,
                            renderInOverlayDuringTransition = true,
                        )
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = {
                onDismiss.invoke()
            }) {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = "Icon for closing the drawer"
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = Gray)
        Spacer(modifier = Modifier.height(16.dp))
        MainDrawerItems(
            onAccountClick = onAccountClick,
            onSearchClick = {
                onDismiss.invoke()
                onSearchClick.invoke()
            },
            onSavedNewsClick = {
                onSavedNewsClick.invoke()
                onDismiss.invoke()
            },
            onSignOutClick = {
                onSignOutClick.invoke()
                onDismiss.invoke()
            }
        )
    }
}

@Composable
fun MainDrawerItems(
    onAccountClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSavedNewsClick: () -> Unit,
    onSignOutClick: () -> Unit,
) {
    // Account
    NavigationDrawerItem(
        label = {
            Text(text = "Account")
        },
        icon = {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "icon of Account"
            )
        },
        selected = false,
        onClick = onAccountClick,
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
        ),
        shape = RectangleShape,
    )

    // Search
    NavigationDrawerItem(
        label = {
            Text(text = "Search")
        },
        icon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "icon of Search"
            )
        },
        selected = false,
        onClick = onSearchClick,
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
        ),
        shape = RectangleShape,
    )

    // Saved News Item
    NavigationDrawerItem(
        label = {
            Text(text = "Saved News")
        },
        icon = {
            Icon(
                imageVector = Icons.Outlined.Bookmark,
                contentDescription = "icon for Saved News"
            )
        },
        selected = false,
        onClick = onSavedNewsClick,
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
        ),
        shape = RectangleShape,
    )

    // Items for Test
    NavigationDrawerItem(
        label = {
            Text(text = "Settings")
        },
        icon = {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "icon of Settings"
            )
        },
        selected = false,
        onClick = {},
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
        ),
        shape = RectangleShape,
    )

    NavigationDrawerItem(
        label = {
            Text(text = "Log out")
        },
        icon = {
            Icon(
                imageVector = Icons.Outlined.Logout,
                contentDescription = "icon of Sign out"
            )
        },
        selected = false,
        onClick = onSignOutClick,
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
        ),
        shape = RectangleShape,
    )

}

@Composable
fun MoreDropDownMenu(
    expanded: Boolean,
    onSearchClick: () -> Unit,
    onAccountClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        modifier = Modifier
            .background(color = if (isSystemInDarkTheme()) DarkGray else White)
    ) {
        // Search Item
        DropdownMenuItem(
            text = {
                Text(text = "Search")
            },
            onClick = {
                onSearchClick.invoke()
                onDismiss.invoke()
            }
        )

        // Account Item
        DropdownMenuItem(
            text = {
                Text(text = "Account")
            },
            onClick = {
                onAccountClick.invoke()
                onDismiss.invoke()
            }
        )
    }
}