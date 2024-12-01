package com.android.studentnews.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.android.studentnews.main.events.ADDRESS
import com.android.studentnews.main.events.BOOKINGS
import com.android.studentnews.main.events.DESCRIPTION
import com.android.studentnews.main.events.ENDING_DATE
import com.android.studentnews.main.events.ENDING_TIME_HOUR
import com.android.studentnews.main.events.ENDING_TIME_MINUTES
import com.android.studentnews.main.events.ENDING_TIME_STATUS
import com.android.studentnews.main.events.EVENT_ID
import com.android.studentnews.main.events.IS_AVAILABLE
import com.android.studentnews.main.events.SAVED_EVENT_ACTION
import com.android.studentnews.main.events.STARTING_DATE
import com.android.studentnews.main.events.STARTING_TIME_HOUR
import com.android.studentnews.main.events.STARTING_TIME_MINUTES
import com.android.studentnews.main.events.STARTING_TIME_STATUS
import com.android.studentnews.main.events.TITLE
import com.android.studentnews.main.events.URL_LIST
import com.android.studentnews.main.events.domain.models.EventsBookingModel
import com.android.studentnews.main.events.domain.repository.EventsRepository
import com.android.studentnews.main.news.CATEGORY
import com.android.studentnews.main.news.LIKES
import com.android.studentnews.main.news.LINK
import com.android.studentnews.main.news.LINK_TITLE
import com.android.studentnews.main.news.NEWS_ID
import com.android.studentnews.main.news.NOTIFICATION_ID
import com.android.studentnews.main.news.SAVE_NEWS_ACTION
import com.android.studentnews.news.domain.model.NewsModel
import com.android.studentnews.news.domain.model.UrlList
import com.android.studentnews.news.domain.repository.NewsRepository
import com.android.studentnews.news.domain.resource.NewsState
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
import kotlin.collections.map
import kotlin.text.split
import kotlin.text.toLong

class MyBroadcastReceiver : BroadcastReceiver(), KoinComponent {

    private val notificationManager: NotificationManagerCompat by inject()
    private val newsRepository: NewsRepository by inject()
    private val scope = CoroutineScope(Dispatchers.Default)


    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.action == SAVE_NEWS_ACTION) {

            val notificationId = intent.getIntExtra(NOTIFICATION_ID, 1)
            notificationManager.cancel(notificationId)

            val title = intent.getStringExtra(TITLE) ?: ""
            val description = intent.getStringExtra(DESCRIPTION) ?: ""
            val newsId = intent.getStringExtra(NEWS_ID) ?: ""
            val category = intent.getStringExtra(CATEGORY) ?: ""
            val link = intent.getStringExtra(LINK) ?: ""
            val linkTitle = intent.getStringExtra(LINK_TITLE) ?: ""
            val serializedUrlListInString = intent.getStringExtra(URL_LIST)
            val likesStringArray = intent.getStringArrayExtra(LIKES) ?: emptyArray()


            val urlList = serializedUrlListInString
                ?.split(",")
                ?.map {
                    val parts = it.split(";")
                    UrlList(parts[0], parts[1], parts[2].toLong(), parts[3])
                } ?: emptyList()

            val likes = likesStringArray.mapNotNull { it }


            try {

                val news = NewsModel(
                    title = title,
                    description = description,
                    newsId = newsId,
                    category = category,
                    timestamp = Timestamp.now(),
                    link = link,
                    linkTitle = linkTitle,
                    urlList = urlList,
                    likes = likes
                )

                scope.launch {
                    newsRepository
                        .onNewsSave(news)
                        .collect { result ->
                            when (result) {
                                is NewsState.Success<*> -> {
                                    scope.cancel()
                                }

                                else -> {
                                    scope.cancel()
                                }
                            }
                        }
                }

            } catch (e: Exception) {
                scope.cancel()
            }

        }
    }

}