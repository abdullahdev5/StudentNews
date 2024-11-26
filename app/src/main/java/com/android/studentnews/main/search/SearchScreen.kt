@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
    ExperimentalSharedTransitionApi::class
)

package com.android.studentnews.main.search

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.android.studentnews.core.data.snackbar_controller.SnackBarController
import com.android.studentnews.core.data.snackbar_controller.SnackBarEvents
import com.android.studentnews.core.domain.common.isInternetAvailable
import com.android.studentnews.core.domain.constants.FontSize
import com.android.studentnews.core.domain.constants.Status
import com.android.studentnews.main.news.domain.destination.NewsDestination
import com.android.studentnews.news.domain.model.NewsModel
import com.android.studentnews.news.ui.NewsItem
import com.android.studentnews.ui.theme.Black
import com.android.studentnews.ui.theme.DarkColor
import com.android.studentnews.ui.theme.DarkGray
import com.android.studentnews.ui.theme.Green
import com.android.studentnews.ui.theme.LightGray
import com.android.studentnews.ui.theme.White
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch

@SuppressLint("FrequentlyChangedStateReadInComposition")
@Composable
fun SearchScreen(
    navHostController: NavHostController,
    searchViewModel: SearchViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val lazyListState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    var isSearchBarActive by remember { mutableStateOf(true) }
    var currentSelectedCategory by remember { mutableStateOf<String?>(null) }

    var query by rememberSaveable { mutableStateOf("") }

    val searchNewsList by searchViewModel.searchNewsList.collectAsStateWithLifecycle()
    val categoryList by searchViewModel.categoriesList.collectAsStateWithLifecycle()


    LaunchedEffect(true) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(isSearchBarActive) {
        if (!isSearchBarActive) {
            navHostController.navigateUp()
        }
    }

    SearchBar(
        query = query,
        onQueryChange = {
            query = it
        },
        active = isSearchBarActive,
        onActiveChange = {
            isSearchBarActive = it
        },
        onSearch = {
            if (query.isNotEmpty()) {
                focusManager.clearFocus()
                searchViewModel.onSearch(query, currentSelectedCategory)
            }
        },
        placeholder = {
            Text(text = "Search...")
        },
        leadingIcon = {
            IconButton(
                onClick = {
                    isSearchBarActive = false
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Icon for Navigate Back"
                )
            }
        },
        trailingIcon = {
            Row {
                // Clear query Text Icon if not empty
                if (query.isNotEmpty()) {
                    IconButton(onClick = {
                        query = ""
                    }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Icon for Clear the Query Text"
                        )
                    }
                }

                // Search Icon
                IconButton(onClick = {
                    if (query.isNotEmpty()) {
                        focusManager.clearFocus()
                        searchViewModel.onSearch(query, currentSelectedCategory)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Icon for Search"
                    )
                }
            }
        },
        colors = SearchBarDefaults.colors(
            containerColor = Color.Transparent,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .focusRequester(focusRequester)
    ) {
        var selectedCategoryIndex by rememberSaveable { mutableStateOf<Int?>(null) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            if (searchViewModel.searchingStatus.value == Status.Loading) {
                LinearProgressIndicator(
                    color = Green,
                    trackColor = Color.Transparent,
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier
                        .fillMaxWidth(),
                )
            }

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                            ) {
                                // Category List
                                categoryList.forEachIndexed { index, item ->

                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (selectedCategoryIndex == index) {
                                                if (isSystemInDarkTheme()) White else Black
                                            } else {
                                                if (isSystemInDarkTheme()) DarkGray else LightGray.copy(
                                                    0.3f
                                                )
                                            },
                                            contentColor = if (selectedCategoryIndex == index) {
                                                if (isSystemInDarkTheme()) Black else White
                                            } else Color.Unspecified
                                        ),
                                        modifier = Modifier
                                            .padding(all = 5.dp)
                                            .clickable {
                                                selectedCategoryIndex = index
                                                searchViewModel.getNewsListByCategory(
                                                    item.name ?: "",
                                                )
                                                currentSelectedCategory = item.name
                                                query = ""
                                                focusManager.clearFocus()
                                            }
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                        ) {
                                            Text(
                                                text = item.name ?: "",
                                                style = TextStyle(
                                                    fontSize = FontSize.MEDIUM.sp,
                                                    fontWeight = FontWeight.Bold,
                                                ),
                                                modifier = Modifier
                                                    .padding(all = 5.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        scrollBehavior = scrollBehavior,
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            scrolledContainerColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .statusBarsPadding()
                    )
                },
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                containerColor = Color.Transparent
            ) { innerPadding ->

                Column(
                    modifier = Modifier
                        .padding(paddingValues = innerPadding)
                ) {

                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier
                            .fillMaxSize()
                    ) {

                        items(
                            count = searchNewsList.size,
                            key = { index ->
                                searchNewsList[index].newsId ?: ""
                            }
                        ) { index ->
                            val item = searchNewsList[index]

                            NewsItem(
                                item = item,
                                context = context,
                                onItemClick = { newsId ->
                                    navHostController.navigate(
                                        NewsDestination.NEWS_DETAIL_SCREEN(
                                            newsId = newsId
                                        )
                                    )
                                },
                                animatedVisibilityScope = animatedVisibilityScope,
                                sharedTransitionScope = sharedTransitionScope,
                                onSave = { it ->
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

                                    searchViewModel.onNewsSave(
                                        news = news,
                                        onSeeAction = { thisNewsId ->
                                            navHostController.navigate(
                                                NewsDestination.NEWS_DETAIL_SCREEN(thisNewsId)
                                            )
                                        }
                                    )
                                }
                            )
                        }
                    }
                }

                if (searchViewModel.searchingStatus.value == Status.FAILED
                    || searchNewsList.isEmpty()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = if (searchViewModel.searchingStatus.value == Status.FAILED)
                                "${searchViewModel.errorMsg}"
                            else if (searchNewsList.isEmpty())
                                "No Search Result Found!"
                            else ""
                        )
                    }
                }

            }
        }
    }
}