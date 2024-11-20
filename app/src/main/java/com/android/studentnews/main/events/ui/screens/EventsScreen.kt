package com.android.studentnews.main.events.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.android.studentnews.core.domain.constants.Status
import com.android.studentnews.core.ui.common.LoadingDialog
import com.android.studentnews.main.events.ui.viewModels.EventsViewModel
import com.android.studentnews.ui.theme.Green

@Composable
fun EventsScreen(
    navHostController: NavHostController,
    eventsViewModel: EventsViewModel
) {

    val eventsList by eventsViewModel.eventsList.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
        ) {

            item {
                if (eventsList.isEmpty()) {
                    Text(text = "No Events Available")
                } else if (eventsViewModel.eventsListErrorMsg.isNotEmpty()) {
                    Text(text = eventsViewModel.eventsListErrorMsg)
                }
            }

            items(eventsList.size) { index ->
                val item = eventsList[index]
                Text(text = item.title ?: "")
            }

        }


        if (eventsViewModel.eventsListStatus == Status.Loading) {
            LoadingDialog()
        }
    }
}