@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
    ExperimentalSharedTransitionApi::class
)

package com.android.studentnews.main.search

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ImagesearchRoller
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.android.studentnews.core.domain.constants.FontSize
import com.android.studentnews.core.domain.constants.Status
import com.android.studentnews.main.events.ui.screens.CategoryList
import com.android.studentnews.main.news.domain.destination.NewsDestination
import com.android.studentnews.news.ui.NewsItem
import com.android.studentnews.ui.theme.Gray
import com.android.studentnews.ui.theme.Green

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

    var isSearchBarActive by remember { mutableStateOf(true) }
    var currentSelectedCategory by remember { mutableStateOf<String?>(null) }

    var query by rememberSaveable { mutableStateOf("") }

    var searchCount by rememberSaveable { mutableIntStateOf(0) }

    val searchNewsList by searchViewModel.searchNewsList.collectAsStateWithLifecycle()
    val categoryList by searchViewModel.categoriesList.collectAsStateWithLifecycle()

    var isSearchResultNotFound = remember(searchViewModel.searchingStatus) {
        derivedStateOf {
            searchNewsList.isEmpty()
                    && searchViewModel.searchingStatus != Status.Loading
        }
    }.value


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
                searchCount++
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
                        searchCount++
                    }
                }) {
                    Icon(
                        imageVector = if (searchCount != 0 && query.isEmpty())
                            Icons.Outlined.SearchOff else Icons.Default.Search,
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
            if (searchViewModel.searchingStatus == Status.Loading) {
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
                    AnimatedVisibility(
                        lazyListState.lastScrolledBackward
                                || lazyListState.firstVisibleItemIndex == 0
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 10.dp)
                                .horizontalScroll(rememberScrollState())
                        ) {
                            // Category List
                            categoryList
                                .forEachIndexed { index, item ->
                                    CategoryList(
                                        categoryName = item.name ?: "",
                                        index = index,
                                        selectedCategoryIndex = selectedCategoryIndex,
                                        onClick = { thisIndex, categoryName ->
                                            selectedCategoryIndex = thisIndex
                                            currentSelectedCategory = categoryName
                                            focusManager.clearFocus()
                                            if (query.isNotEmpty()) {
                                                searchViewModel.onSearch(
                                                    query,
                                                    currentSelectedCategory
                                                )
                                            } else {
                                                searchViewModel
                                                    .getNewsListByCategory(
                                                        currentSelectedCategory!!
                                                    )
                                            }
                                            searchCount++
                                        }
                                    )
                                }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxSize(),
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
                            )
                        }
                    }
                }

                if (searchViewModel.searchingStatus == Status.FAILED
                    || searchNewsList.isEmpty()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(all = 10.dp)
                        ) {
                            if (
                                searchCount == 0 || isSearchResultNotFound
                            ) {
                                Icon(
                                    imageVector = if (
                                        searchCount != 0 && isSearchResultNotFound
                                    ) Icons.Outlined.SearchOff else Icons.Outlined.Search,
                                    contentDescription = "icon for Showing to do Search",
                                    modifier = Modifier
                                        .width(100.dp)
                                        .height(100.dp),
                                    tint = Green
                                )
                            }
                            Text(
                                text = if (searchCount == 0)
                                    "Search for news"
                                else if (searchViewModel.searchingStatus == Status.FAILED)
                                    searchViewModel.errorMsg
                                else if (isSearchResultNotFound)
                                    "No Search Result Found!"
                                else if (searchViewModel.searchingStatus == Status.Loading)
                                    "Searching....."
                                else "",
                                style = TextStyle(
                                    fontSize = if (searchCount == 0)
                                        FontSize.LARGE.sp else FontSize.MEDIUM.sp,
                                    fontWeight = if (searchCount == 0)
                                        FontWeight.Bold else FontWeight.Normal,
                                    color = if (searchCount == 0)
                                        Gray else LocalContentColor.current
                                )
                            )
                        }
                    }
                }

            }
        }
    }
}