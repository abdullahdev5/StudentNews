@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.studentnews.main.settings.registered_events

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.android.studentnews.core.data.paginator.LENGTH_ERROR
import com.android.studentnews.core.domain.common.ErrorMessageContainer
import com.android.studentnews.main.events.domain.destination.EventsDestination
import com.android.studentnews.main.events.ui.screens.EventsItem

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RegisteredEventsScreen(
    navHostController: NavHostController,
    registeredEventsViewModel: RegisteredEventsViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
) {

    val context = LocalContext.current

    val registeredEventsList =
        registeredEventsViewModel.registeredEventsList.collectAsLazyPagingItems()

    val registeredEventsListNotFound = remember {
        derivedStateOf {
            registeredEventsList.itemCount == 0
                    && registeredEventsList.loadState.refresh is LoadState.NotLoading
                    && registeredEventsList.loadState.hasError
        }
    }.value


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Registered Events")
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
                        imageVector = Icons.Outlined.CollectionsBookmark, // this icon looks like Registered Events collection
                        contentDescription = "Icon for Registered Events"
                    )
                }
            )
        },
        modifier = Modifier
            .fillMaxSize()
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (registeredEventsList.loadState.refresh is LoadState.NotLoading) {
                items(
                    count = registeredEventsList.itemCount,
                    key = registeredEventsList.itemKey {
                        it.eventId ?: ""
                    },
                    contentType = registeredEventsList.itemContentType {
                        "registered_events_list"
                    }
                ) { index ->
                    val item = registeredEventsList[index]

                    EventsItem(
                        item = item,
                        context = context,
                        animatedVisibilityScope = animatedVisibilityScope,
                        sharedTransitionScope = sharedTransitionScope,
                        onItemClick = { thisEventId ->
                            navHostController
                                .navigate(
                                    EventsDestination.EVENTS_DETAIL_SCREEN(thisEventId)
                                )
                        }
                    )
                }
            }

            if (
                registeredEventsList.loadState.append is LoadState.Loading
                || registeredEventsList.loadState.refresh is LoadState.Loading
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

            if (
                registeredEventsList.loadState.refresh is LoadState.Error
            ) {
                item {
                    ErrorMessageContainer(
                        errorMessage =
                        (registeredEventsList.loadState.refresh as LoadState.Error
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
                registeredEventsList.loadState.append is LoadState.Error
                && (registeredEventsList.loadState.append as LoadState.Error
                        ).error.localizedMessage != LENGTH_ERROR
            ) {
                item {
                    ErrorMessageContainer(
                        errorMessage =
                        (registeredEventsList.loadState.append as LoadState.Error
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

        if (registeredEventsListNotFound) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(text = "No Registered Events Found!")
            }
        }

    }

}