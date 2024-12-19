@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.studentnews.news.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.BookmarkRemove
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
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import com.android.studentnews.auth.domain.models.UserModel
import com.android.studentnews.core.data.paginator.LENGTH_ERROR
import com.android.studentnews.core.domain.common.CollapsingAppBarNestedScrollConnection
import com.android.studentnews.core.domain.common.ErrorMessageContainer
import com.android.studentnews.core.domain.common.formatDateOrTimeToAgo
import com.android.studentnews.core.domain.common.getUrlOfImageNotVideo
import com.android.studentnews.core.domain.constants.FontSize
import com.android.studentnews.main.MainBottomNavigationBarList
import com.android.studentnews.main.account.ui.viewmodel.AccountViewModel
import com.android.studentnews.main.events.domain.destination.EventsDestination
import com.android.studentnews.main.events.ui.screens.CategoryListItem
import com.android.studentnews.main.events.ui.screens.EventsScreen
import com.android.studentnews.main.events.ui.viewModels.EventsViewModel
import com.android.studentnews.main.news.domain.destination.NewsDestination
import com.android.studentnews.main.news.domain.model.CategoryModel
import com.android.studentnews.navigation.SubGraph
import com.android.studentnews.news.domain.destination.MainDestination
import com.android.studentnews.news.domain.model.NewsModel
import com.android.studentnews.main.MainNavigationDrawerList
import com.android.studentnews.main.MainTabRowList
import com.android.studentnews.main.news.ui.viewModel.NewsDetailViewModel
import com.android.studentnews.news.ui.viewModel.NewsViewModel
import com.android.studentnews.ui.theme.Black
import com.android.studentnews.ui.theme.DarkColor
import com.android.studentnews.ui.theme.DarkGray
import com.android.studentnews.ui.theme.Gray
import com.android.studentnews.ui.theme.Green
import com.android.studentnews.ui.theme.ItemBackgroundColor
import com.android.studentnews.ui.theme.LightGray
import com.android.studentnews.ui.theme.White
import com.google.firebase.Timestamp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@SuppressLint(
    "RememberReturnType", "FrequentlyChangedStateReadInComposition"
)
@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class)
@Composable
fun NewsScreen(
    navHostController: NavHostController,
    newsViewModel: NewsViewModel,
    eventsViewModel: EventsViewModel,
    accountViewModel: AccountViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
) {

    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val context = LocalContext.current
    val drawerScrollState = rememberScrollState()
    val moreOptionsInNewsItemsSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val lazyListState = rememberLazyListState()
    val pullToRefreshState = rememberPullToRefreshState()
    val configuration = LocalConfiguration.current
    val isLandScape = remember {
        configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }
    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )
    val tabPagerState = rememberPagerState(pageCount = { 2 })

    // Top Bar Scroll Connection
    val topBarMaxHeight: Int = with(density) { (30).dp.roundToPx() }
    val topBarScrollConnection: CollapsingAppBarNestedScrollConnection =
        remember(topBarMaxHeight) {
            CollapsingAppBarNestedScrollConnection(topBarMaxHeight)
        }

    // Bottom Bar Scroll Connection
    val bottomBarMaxHeight: Int = with(density) { (50).dp.roundToPx() }
    val bottomBarScrollConnection: CollapsingAppBarNestedScrollConnection =
        remember(bottomBarMaxHeight) {
            CollapsingAppBarNestedScrollConnection(bottomBarMaxHeight)
        }


    val tabList = listOf(
        MainTabRowList.News,
        MainTabRowList.Events,
    )
    var refreshCount by rememberSaveable { mutableIntStateOf(0) }

    var newsCategoryStatus by rememberSaveable { mutableStateOf("") }
    var isMoreDropDownMenuItemOpen by rememberSaveable { mutableStateOf(false) }
    var isMoreOptionsInNewsItemsSheetStateOpen by rememberSaveable { mutableStateOf(false) }
    var eventsListGettingCount by rememberSaveable { mutableIntStateOf(0) }

    val newsList = newsViewModel.newsList.collectAsLazyPagingItems()
    val categoriesList = newsViewModel.categoriesList.collectAsLazyPagingItems()
    val currentUser by accountViewModel.currentUser.collectAsStateWithLifecycle()

    val categoryPagerState = rememberPagerState(
        pageCount = {
            categoriesList.itemCount
        },
        initialPage = 0
    )

    var selectedNewsCategoryIndex by rememberSaveable { mutableStateOf<Int?>(null) }

    var selectedNavBarIndex by remember { mutableIntStateOf(0) }

    val navBarList = listOf(
        MainBottomNavigationBarList.Home,
        MainBottomNavigationBarList.Search,
        MainBottomNavigationBarList.Account,
    )

    LaunchedEffect(tabPagerState.currentPage) {
        if (tabPagerState.currentPage == 1) {
            if (eventsListGettingCount < 1) {
                eventsViewModel.getEventsList(null)
                eventsListGettingCount++
            }
        }
    }

    LaunchedEffect(newsViewModel.isRefreshing) {
        if (newsViewModel.isRefreshing) {
            val currentPage = tabPagerState.currentPage
            scope.launch {
                pullToRefreshState.startRefresh()
                delay(2000L)
                if (currentPage == 0) {
                    newsViewModel.getNewsList(null)
                    newsCategoryStatus = ""
                    selectedNewsCategoryIndex?.let {
                        selectedNewsCategoryIndex = null
                    }
                }
                if (currentPage == 1) {
                    eventsViewModel.getEventsList(null)
                    eventsViewModel.selectedCategoryIndex?.let {
                        eventsViewModel.selectedCategoryIndex = null
                    }
                }
                refreshCount++
            }
        } else {
            pullToRefreshState.endRefresh()
        }
    }


    BackHandler(
        drawerState.isOpen || tabPagerState.currentPage != 0
                || lazyListState.firstVisibleItemIndex != 0
    ) {
        scope.launch {
            if (drawerState.isOpen) {
                drawerState.close()

            } else if (tabPagerState.currentPage != 0) {
                tabPagerState.animateScrollToPage(0)
            } else if (lazyListState.firstVisibleItemIndex != 0) {
                lazyListState.scrollToItem(0)
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
                    drawerContainerColor = if (isSystemInDarkTheme()) DarkColor else White,
                    modifier = Modifier
                        .widthIn(max = configuration.screenWidthDp.dp / 1.3f)
                ) {
                    MainDrawerContent(
                        currentUser = currentUser,
                        scrollState = drawerScrollState,
                        context = context,
                        animatedVisibilityScope = animatedVisibilityScope,
                        sharedTransitionScope = sharedTransitionScope,
                        onClick = { name ->
                            when (name) {

                                MainNavigationDrawerList.Account.name -> {
                                    currentUser?.let {
                                        navHostController.navigate(MainDestination.ACCOUNT_SCREEN)
                                    }
                                }

                                MainNavigationDrawerList.Search.name -> {
                                    navHostController.navigate(MainDestination.SEARCH_SCREEN)
                                }

                                MainNavigationDrawerList.Saved.name -> {
                                    navHostController.navigate(SubGraph.SAVED)
                                }

                                MainNavigationDrawerList.Liked.name -> {
                                    navHostController.navigate(NewsDestination.LIKED_NEWS_SCREEN)
                                }

                                MainNavigationDrawerList.Registered_Events.name -> {
                                    navHostController.navigate(EventsDestination.REGISTERED_EVENTS_SCREEN)
                                }

                                MainNavigationDrawerList.Settings.name -> {
                                    navHostController.navigate(SubGraph.SETTINGS)
                                }

                                MainNavigationDrawerList.Log_out.name -> {
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
                                }
                            }
                        },
                        onUserImageClick = {
                            currentUser?.let {
                                navHostController.navigate(MainDestination.ACCOUNT_SCREEN)
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
                    Column {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    text = if (tabPagerState.currentPage == 0) "News"
                                    else "Events",
                                )
                            },
                            actions = {
                                IconButton(onClick = {
                                    isMoreDropDownMenuItemOpen = !isMoreDropDownMenuItemOpen
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "Icon for More"
                                    )
                                }
                                if (isMoreDropDownMenuItemOpen) {
                                    MoreDropDownMenuMain(
                                        expanded = isMoreDropDownMenuItemOpen,
                                        currentPage = tabPagerState.currentPage,
                                        onSavedClick = {
                                            navHostController.navigate(SubGraph.SAVED)
                                        },
                                        onLikedClick = {
                                            navHostController
                                                .navigate(NewsDestination.LIKED_NEWS_SCREEN)
                                        },
                                        onRegisteredEventsClick = {
                                            navHostController
                                                .navigate(EventsDestination.REGISTERED_EVENTS_SCREEN)
                                        },
                                        onDismiss = {
                                            isMoreDropDownMenuItemOpen = false
                                        },
                                        modifier = Modifier
                                            .background(color = if (isSystemInDarkTheme()) DarkColor else White)
                                            .border(
                                                width = 1.dp,
                                                color = if (isSystemInDarkTheme()) DarkGray else LightGray,
                                                shape = RoundedCornerShape(5.dp)
                                            )
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
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent,
                                scrolledContainerColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .statusBarsPadding()
                                .then(
                                    with(density) {
                                        Modifier
                                            .height(
                                                (topBarMaxHeight + topBarScrollConnection.appBarOffset).toDp()
                                            )
                                    }
                                )
                                .offset { IntOffset(0, topBarScrollConnection.appBarOffset) }
                        )

                        MainTabRow(
                            tabPagerState = tabPagerState,
                            tabList = tabList,
                            onClick = { index ->
                                scope.launch {
                                    tabPagerState.animateScrollToPage(index)
                                }

                                if (index == 0) {
                                    if (lazyListState.firstVisibleItemIndex != 0) {
                                        scope.launch {
                                            lazyListState.scrollToItem(0)
                                        }
                                    }
                                }

                                if (index == 1) {
                                    if (eventsViewModel.lazyListState.firstVisibleItemIndex != 0) {
                                        scope.launch {
                                            eventsViewModel
                                                .lazyListState
                                                .scrollToItem(0)
                                        }

                                    }
                                }
                            },
                        )

                    }
                },
                bottomBar = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .then(
                                with(density) {
                                    Modifier
                                        .height(
                                            (bottomBarMaxHeight + bottomBarScrollConnection.appBarOffset).toDp()
                                        )
                                }
                            ),
                    ) {
                        HorizontalDivider()

                        BottomAppBar(
                            containerColor = Color.Transparent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset { IntOffset(0, -bottomBarScrollConnection.appBarOffset) },
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
                    .nestedScroll(topBarScrollConnection)
                    .nestedScroll(bottomBarScrollConnection)
            ) { innerPadding ->

                Column(
                    modifier = Modifier
                        .padding(paddingValues = innerPadding)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(pullToRefreshState.nestedScrollConnection),
                    ) {

                        val categoriesMaxHeight =
                            with(density) { (50).dp.roundToPx() }

                        val categoriesScrollConnection = remember(categoriesMaxHeight) {
                            CollapsingAppBarNestedScrollConnection(
                                categoriesMaxHeight
                            )
                        }

                        HorizontalPager(
                            state = tabPagerState,
                        ) { page ->

                            when (page) {

                                0 -> {
                                    Column(
                                        modifier = Modifier
                                            .then(
                                                if (lazyListState.firstVisibleItemIndex > 1)
                                                    Modifier
                                                        .nestedScroll(categoriesScrollConnection)
                                                else Modifier
                                            )
                                    ) {
                                        AnimatedVisibility(
                                            lazyListState.firstVisibleItemIndex > 1
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .horizontalScroll(
                                                        rememberScrollState()
                                                    )
                                                    .background(
                                                        color = MaterialTheme
                                                            .colorScheme
                                                            .surface
                                                    )
                                                    .then(
                                                        with(density) {
                                                            Modifier
                                                                .height(
                                                                    (categoriesMaxHeight + categoriesScrollConnection.appBarOffset).toDp()
                                                                )
                                                        }
                                                    )
                                                    .offset {
                                                        IntOffset(
                                                            0,
                                                            categoriesScrollConnection.appBarOffset
                                                        )
                                                    },
                                            ) {
                                                categoriesList
                                                    .itemSnapshotList
                                                    .items
                                                    .forEachIndexed { index, item ->
                                                        CategoryListItem(
                                                            categoryName = item.name
                                                                ?: "",
                                                            colors = SegmentedButtonDefaults.colors(
                                                                activeBorderColor = if (isSystemInDarkTheme()) White else Black,
                                                                inactiveBorderColor = if (isSystemInDarkTheme()) DarkGray else LightGray,
                                                                activeContainerColor = Color.Transparent,
                                                                inactiveContainerColor = Color.Transparent,
                                                                activeContentColor = LocalContentColor.current,
                                                                inactiveContentColor = if (selectedNewsCategoryIndex == null)
                                                                    LocalContentColor.current else Gray
                                                            ),
                                                            index = index,
                                                            selectedCategoryIndex = selectedNewsCategoryIndex,
                                                            onClick = { index, category ->
                                                                selectedNewsCategoryIndex =
                                                                    index
                                                                newsViewModel
                                                                    .getNewsList(
                                                                        category
                                                                    )
                                                                newsCategoryStatus =
                                                                    "$category News"
                                                            },
                                                            modifier = Modifier
                                                                .padding(
                                                                    start = 5.dp,
                                                                    end = 5.dp
                                                                ),
                                                        )
                                                    }
                                            }
                                        }

                                        LazyColumn(
                                            state = lazyListState,
                                            flingBehavior = ScrollableDefaults.flingBehavior(),
                                            userScrollEnabled = true,
                                            modifier = Modifier
                                                .fillMaxSize(),
                                        ) {
                                            item(
                                                key = "news_category"
                                            ) {
                                                Column {
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
                                                        key = categoriesList.itemKey {
                                                            it.categoryId ?: ""
                                                        },
                                                        modifier = Modifier
                                                            .height(250.dp),
                                                    ) { pagerIndex ->
                                                        val item =
                                                            categoriesList[pagerIndex]!!
                                                        CategoriesListPagerItem(
                                                            item = item,
                                                            context = context,
                                                            onItemCLick = { category ->

                                                                selectedNewsCategoryIndex =
                                                                    pagerIndex
                                                                newsViewModel.getNewsList(category)
                                                                newsCategoryStatus =
                                                                    "${category} News"
                                                            }
                                                        )
                                                    }

                                                    Row(
                                                        modifier = Modifier
                                                            .align(alignment = Alignment.CenterHorizontally),
                                                    ) {
                                                        repeat(categoriesList.itemCount) { index ->
                                                            PagerIndicator(
                                                                index = index,
                                                                pagerState = categoryPagerState
                                                            )
                                                        }
                                                    }

                                                }
                                            }

                                            item(
                                                key = "news_category_status"
                                            ) {
                                                CategoryStatusText(
                                                    category = if (newsCategoryStatus.isNotEmpty())
                                                        newsCategoryStatus else "For You",
                                                    style = TextStyle(
                                                        fontSize = FontSize.LARGE.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Gray
                                                    ),
                                                    modifier = Modifier
                                                        .padding(
                                                            top = 10.dp,
                                                            start = 10.dp,
                                                            bottom = 5.dp
                                                        ),
                                                )
                                            }

                                            if (newsList.loadState.refresh is LoadState.NotLoading) {
                                                items(
                                                    count = newsList.itemCount,
                                                    key = newsList.itemKey {
                                                        it.newsId ?: ""
                                                    },
                                                    contentType = newsList.itemContentType {
                                                        "news_list"
                                                    }
                                                ) { index ->
                                                    val item = newsList[index]

                                                    NewsItem(
                                                        item = item,
                                                        onItemClick = { newsId ->
                                                            navHostController.navigate(
                                                                NewsDestination.NEWS_DETAIL_SCREEN(
                                                                    newsId
                                                                )
                                                            )
                                                        },
                                                        onMoreOptionsClick = { thisNewsId ->
                                                            newsViewModel.newsIdWhenMoreOptionClick =
                                                                thisNewsId
                                                            scope.launch {
                                                                moreOptionsInNewsItemsSheetState.show()
                                                            }.invokeOnCompletion {
                                                                if (moreOptionsInNewsItemsSheetState.isVisible) {
                                                                    isMoreOptionsInNewsItemsSheetStateOpen =
                                                                        true
                                                                }
                                                            }
                                                        },
                                                        context = context,
                                                        animatedVisibilityScope = animatedVisibilityScope,
                                                        sharedTransitionScope = sharedTransitionScope,
                                                    )

                                                }
                                            }

                                            if (
                                                newsList.loadState.append is LoadState.Loading
                                                || newsList.loadState.refresh is LoadState.Loading
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

                                            if (newsList.loadState.refresh is LoadState.Error) {
                                                item {
                                                    ErrorMessageContainer(
                                                        errorMessage =
                                                        (newsList.loadState.refresh as LoadState.Error
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

                                            if (
                                                newsList.loadState.append is LoadState.Error
                                                && (newsList.loadState.append as LoadState.Error
                                                        ).error.localizedMessage != LENGTH_ERROR
                                            ) {
                                                item {
                                                    ErrorMessageContainer(
                                                        errorMessage =
                                                        (newsList.loadState.append as LoadState.Error
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
                                    }
                                }

                                1 -> {
                                    EventsScreen(
                                        navHostController = navHostController,
                                        eventsViewModel = eventsViewModel,
                                        animatedVisibilityScope = animatedVisibilityScope,
                                        sharedTransitionScope = sharedTransitionScope,
                                    )
                                }
                            }
                        }

                        if (pullToRefreshState.isRefreshing) {
                            LaunchedEffect(true) {
                                scope.launch {
                                    newsViewModel.isRefreshing = true
                                    delay(2000L)
                                    newsViewModel.isRefreshing = false
                                }
                            }
                        }

                        PullToRefreshContainer(
                            state = pullToRefreshState,
                            containerColor = if (
                                pullToRefreshState.progress == 0f
                                && !pullToRefreshState.isRefreshing
                            )
                                Color.Transparent else {
                                if (isSystemInDarkTheme()) DarkColor else White
                            },
                            contentColor = if (
                                pullToRefreshState.progress == 0f
                                && !pullToRefreshState.isRefreshing
                            )
                                Color.Transparent else Green,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                        )

                    }

                }

                if (isMoreOptionsInNewsItemsSheetStateOpen) {

                    newsViewModel.newsIdWhenMoreOptionClick?.let { thisNewsId ->
                        MoreOptionSheetOfNewsItems(
                            newsId = thisNewsId,
                            sheetState = moreOptionsInNewsItemsSheetState,
                            context = context,
                            onDismiss = {
                                scope.launch {
                                    moreOptionsInNewsItemsSheetState.hide()
                                }.invokeOnCompletion {
                                    if (!moreOptionsInNewsItemsSheetState.isVisible) {
                                        newsViewModel.newsIdWhenMoreOptionClick = null
                                        isMoreOptionsInNewsItemsSheetStateOpen =
                                            false
                                    }
                                }
                            }
                        )
                    }
                }

            }

        }
    }

}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NewsItem(
    item: NewsModel?,
    context: Context,
    onItemClick: (String) -> Unit,
    onMoreOptionsClick: (String) -> Unit = {},
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
) {
    with(sharedTransitionScope) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 10.dp,
                    bottom = 10.dp,
                )
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(key = "container/${item?.newsId}"),
                    animatedVisibilityScope = animatedVisibilityScope,
                    renderInOverlayDuringTransition = true
                ),
            colors = CardDefaults.cardColors(
                containerColor = ItemBackgroundColor
            )
        ) {
            Column(
                modifier = Modifier
                    .clickable {
                        onItemClick.invoke(item?.newsId ?: "")
                    },
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(end = 5.dp)
                            .weight(1f)
                    ) {
                        Column(
                            modifier = Modifier
                                .background(
                                    color = Black.copy(0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(all = 2.dp)
                        ) {
                            Text(
                                text = item?.category ?: "",
                                style = TextStyle(
                                    fontSize = FontSize.SMALL.sp
                                ),
                                modifier = Modifier
//                                    .padding(all = 2.dp)
                            )
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
                                .sharedBounds(
                                    sharedContentState = rememberSharedContentState(key = "title/${item?.newsId}"),
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
                                .padding(top = 5.dp)
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
                            .width(80.dp)
                            .heightIn(max = 80.dp)
                            .clip(shape = RoundedCornerShape(10.dp))
                            .sharedElement(
                                state = rememberSharedContentState(key = "image/${item?.newsId ?: ""}"),
                                animatedVisibilityScope = animatedVisibilityScope,
                                clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(10.dp))
                            )
                    )
                }

                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .padding(all = 10.dp)
                ) {
                    item?.timestamp?.let { timestamp ->
                        val dateChar = formatDateOrTimeToAgo(timestamp.toDate())

                        Text(
                            text = dateChar.toString(),
                            style = TextStyle(
                                fontSize = FontSize.SMALL.sp,
                                color = Gray,
                            )
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = {
                            item?.newsId?.let { thisNewsId ->
                                onMoreOptionsClick(thisNewsId)
                            }
                        },
                        modifier = Modifier
                            .width(20.dp)
                            .height(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Icon for More Options",
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun CategoriesListPagerItem(
    item: CategoryModel,
    context: Context,
    onItemCLick: (String) -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 20.dp,
                bottom = 5.dp
            )
            .clickable(
                role = Role.Image
            ) {
                onItemCLick.invoke(item.name ?: "")
            },
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
        ),
        content = {}
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainDrawerContent(
    currentUser: UserModel?,
    scrollState: ScrollState,
    context: Context,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    onClick: (name: String) -> Unit,
    onUserImageClick: () -> Unit,
    onDismiss: () -> Unit,
) {

    var drawerList = listOf(
        MainNavigationDrawerList.Account,
        MainNavigationDrawerList.Search,
        MainNavigationDrawerList.Saved,
        MainNavigationDrawerList.Liked,
        MainNavigationDrawerList.Registered_Events,
        MainNavigationDrawerList.Settings,
        MainNavigationDrawerList.Log_out,
    )

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

                with(sharedTransitionScope) {
                    Card(
                        modifier = Modifier
                            .width(100.dp)
                            .height(100.dp)
                            .clip(shape = CircleShape)
                            .sharedElement(
                                state = rememberSharedContentState(key = "user_image/${currentUser?.uid}"),
                                animatedVisibilityScope = animatedVisibilityScope,
                                clipInOverlayDuringTransition = OverlayClip(CircleShape),
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
                                            onUserImageClick.invoke()
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
                }
                Spacer(modifier = Modifier.height(10.dp))
                with(sharedTransitionScope) {
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
                            )
                    )
                }
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
            drawerList = drawerList,
            onClick = { name ->
                if (name != MainNavigationDrawerList.Account.name) {
                    onDismiss()
                }
                onClick(name)
            }
        )
    }
}

@Composable
fun MainDrawerItems(
    drawerList: List<MainNavigationDrawerList>,
    onClick: (name: String) -> Unit,
) {
    drawerList
        .forEachIndexed { index, item ->
            NavigationDrawerItem(
                label = {
                    Text(text = item.text)
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = "Icon"
                    )
                },
                selected = false,
                onClick = {
                    onClick(item.name)
                },
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = Color.Transparent,
                ),
                shape = RectangleShape,
            )
        }
}

@Composable
fun MoreDropDownMenuMain(
    expanded: Boolean,
    modifier: Modifier = Modifier,
    currentPage: Int,
    onSavedClick: () -> Unit,
    onLikedClick: () -> Unit,
    onRegisteredEventsClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        properties = PopupProperties(
            focusable = true,
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        ),
        modifier = modifier
    ) {
        // Saved Item
        DropdownMenuItem(
            text = {
                Text(text = "Saved")
            },
            onClick = {
                onSavedClick.invoke()
                onDismiss.invoke()
            },
            contentPadding = PaddingValues(10.dp)
        )
        if (currentPage == 0) {
            // Liked Item
            DropdownMenuItem(
                text = {
                    Text(text = "Liked")
                },
                onClick = {
                    onLikedClick.invoke()
                    onDismiss.invoke()
                },
                contentPadding = PaddingValues(10.dp)
            )
        }

        if (currentPage == 1) {
            // Registered Events Item
            DropdownMenuItem(
                text = {
                    Text(text = "Registered Events")
                },
                onClick = {
                    onRegisteredEventsClick.invoke()
                    onDismiss.invoke()
                },
                contentPadding = PaddingValues(10.dp)
            )
        }
    }
}

@Composable
inline fun MainTabRow(
    tabPagerState: PagerState,
    modifier: Modifier = Modifier,
    tabList: List<MainTabRowList>,
    crossinline onClick: (index: Int) -> Unit,
) {
    TabRow(
        selectedTabIndex = tabPagerState.currentPage,
        containerColor = if (isSystemInDarkTheme()) DarkColor else White,
        modifier = modifier
    ) {
        tabList.forEach { item ->
            Tab(
                selected = item.index == tabPagerState.currentPage,
                onClick = {
                    onClick(item.index)
                },
                text = {
                    Text(text = item.text)
                },
                selectedContentColor = Green,
                unselectedContentColor = if (isSystemInDarkTheme()) White else Black
            )
        }
    }
}

@Composable
fun MoreOptionSheetOfNewsItems(
    newsId: String,
    sheetState: SheetState,
    context: Context,
    onDismiss: () -> Unit,
) {

    val newsDetailViewModel = koinViewModel<NewsDetailViewModel>()

    LaunchedEffect(Unit) {
        newsDetailViewModel.getNewsById(newsId)
        newsDetailViewModel.getIsNewsSaved(newsId)
    }

    val newsById by newsDetailViewModel.newsById.collectAsState()

    val isNewsSaved = remember(newsDetailViewModel.isNewsSaved) {
        derivedStateOf {
            newsDetailViewModel.isNewsSaved ?: false
        }
    }.value


    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = 100.dp)
        ) {
            newsById?.let { news ->

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val newsForSave = NewsModel(
                                title = news.title ?: "",
                                description = news.description ?: "",
                                newsId = news.newsId ?: "",
                                category = news.category ?: "",
                                timestamp = Timestamp.now(),
                                link = news.link ?: "",
                                linkTitle = news.linkTitle ?: "",
                                urlList = news.urlList,
                                shareCount = news.shareCount ?: 0,
                                likes = news.likes ?: emptyList(),
                            )

                            if (isNewsSaved) {
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
                            onDismiss()
                        }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 20.dp)
                    ) {

                        Icon(
                            imageVector = if (isNewsSaved)
                                Icons.Outlined.BookmarkRemove
                            else Icons.Default.BookmarkBorder,
                            contentDescription = "Icon for Save or UnSave the News"
                        )

                        Text(text = if (isNewsSaved) "UnSave" else "Save")

                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {

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

                            onDismiss()
                        }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 20.dp)
                    ) {

                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Icon for Share the News"
                        )

                        Text(text = "Share")

                    }
                }

            } ?: CircularProgressIndicator()
        }

    }
}

@Composable
inline fun CategoryStatusText(
    category: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle(),
) {
    Text(
        text = category,
        style = style,
        modifier = modifier
    )
}