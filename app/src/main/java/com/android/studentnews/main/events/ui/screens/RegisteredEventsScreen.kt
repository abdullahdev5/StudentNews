@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.studentnews.main.settings.registered_events

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.android.studentnews.core.domain.constants.Status
import com.android.studentnews.core.ui.common.LoadingDialog
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

    LaunchedEffect(Unit) {
        registeredEventsViewModel.getRegisteredEventsList()
    }

    val context = LocalContext.current

    val registeredEventsList by registeredEventsViewModel.registeredEventsList.collectAsStateWithLifecycle()


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
                        imageVector = Icons.Outlined.Book,
                        contentDescription = "Icon for Registered Events"
                    )
                }
            )
        },
        modifier = Modifier
            .fillMaxSize()
    ) { innerPadding ->

        if (registeredEventsList.size != 0) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {

                items(registeredEventsList.size) { index ->
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
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(text = "No Registered Events")
            }
        }


        if (registeredEventsViewModel.registeredEventsListStatus == Status.Loading) {
            LoadingDialog()
        }

    }

}