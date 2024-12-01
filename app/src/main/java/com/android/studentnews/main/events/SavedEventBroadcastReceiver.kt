package com.android.studentnews.main.events

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.android.studentnews.main.events.domain.models.EventsBookingModel
import com.android.studentnews.main.events.domain.repository.EventsRepository
import com.android.studentnews.main.news.NOTIFICATION_ID
import com.android.studentnews.news.domain.model.UrlList
import com.android.studentnewsadmin.core.domain.resource.EventsState
import com.android.studentnewsadmin.main.events.domain.models.EventsModel
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SavedEventBroadcastReceiver: BroadcastReceiver(), KoinComponent {

    val notificationManager: NotificationManagerCompat by inject()
    val eventsRepository: EventsRepository by inject()
    val scope = CoroutineScope(Dispatchers.Default)

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.action == SAVED_EVENT_ACTION) {


            val notificationId = intent.getIntExtra(NOTIFICATION_ID, 1)
            notificationManager.cancel(notificationId)

            val title = intent.getStringExtra(TITLE) ?: ""
            val description = intent.getStringExtra(DESCRIPTION) ?: ""
            val address = intent.getStringExtra(ADDRESS) ?: ""
            val eventId = intent.getStringExtra(EVENT_ID) ?: ""
            val startingDate = intent.getLongExtra(STARTING_DATE, 0L)
            val startingTimeHour = intent.getIntExtra(STARTING_TIME_HOUR, 0)
            val startingTimeMinutes = intent.getIntExtra(STARTING_TIME_MINUTES, 0)
            val startingTimeStatus = intent.getStringExtra(STARTING_TIME_STATUS) ?: ""
            val endingDate = intent.getLongExtra(ENDING_DATE, 0L)
            val endingTimeHour = intent.getIntExtra(ENDING_TIME_HOUR, 0)
            val endingTimeMinutes = intent.getIntExtra(ENDING_TIME_MINUTES, 0)
            val endingTimeStatus = intent.getStringExtra(ENDING_TIME_STATUS) ?: ""
            val serializedUrlList = intent.getStringExtra(URL_LIST)
            val serializedBookingsList = intent.getStringExtra(BOOKINGS) ?: null
            val isAvailable = intent.getBooleanExtra(IS_AVAILABLE, true)

            val urlList = serializedUrlList
                ?.split(",")
                ?.mapNotNull {
                    val parts = it.split(";")
                    UrlList(parts[0], parts[1], parts[2].toLong(), parts[3])
                } ?: emptyList()

            val bookings = serializedBookingsList
                ?.split(",")
                ?.mapNotNull {
                    val parts = it.split(";")
                    if (parts.size == 8) {
                        EventsBookingModel(
                            parts[0],
                            parts[1],
                            parts[2],
                            parts[3],
                            parts[4],
                            parts[5],
                            parts[6],
                            parts[7].toIntOrNull() ?: 0
                        )
                    } else null
                } ?: emptyList()

            try {

                val event = EventsModel(
                    title = title,
                    description = description,
                    eventId = eventId,
                    address = address,
                    startingDate = startingDate,
                    startingTimeHour = startingTimeHour,
                    startingTimeMinutes = startingTimeMinutes,
                    startingTimeStatus = startingTimeStatus,
                    endingDate = endingDate,
                    endingTimeHour = endingTimeHour,
                    endingTimeMinutes = endingTimeMinutes,
                    endingTimeStatus = endingTimeStatus,
                    timestamp = Timestamp.now(),
                    urlList = urlList,
                    bookings = bookings,
                    isAvailable = isAvailable
                )

                scope.launch {
                    eventsRepository
                        .onEventSave(event = event)
                        .collectLatest { result ->
                            when (result) {
                                is EventsState.Success -> {
                                    scope.cancel()
                                }

                                else -> scope.cancel()
                            }
                        }
                }
            } catch (e: Exception) {
                scope.cancel()
            }


        }


    }

}