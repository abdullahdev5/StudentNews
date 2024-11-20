package com.android.studentnews

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.android.studentnews.news.domain.model.NewsModel
import com.android.studentnews.news.domain.model.UrlList
import com.android.studentnews.news.domain.repository.NewsRepository
import com.android.studentnews.news.domain.resource.NewsState
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SaveNewsBroadcastReceiver : BroadcastReceiver(), KoinComponent {


    private val newsRepository: NewsRepository by inject()
    private val notificationManager: NotificationManagerCompat by inject()
    private val scope = CoroutineScope(Dispatchers.Default)


    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.action == SAVE_NEWS_ACTION) {

            notificationManager.cancel(1)

            val title = intent.getStringExtra("title") ?: ""
            val description = intent.getStringExtra("description") ?: ""
            val newsId = intent.getStringExtra("newsId") ?: ""
            val category = intent.getStringExtra("category") ?: ""
            val link = intent.getStringExtra("link") ?: ""
            val linkTitle = intent.getStringExtra("link_title") ?: ""
            val serializedUrlList = intent.getStringExtra("url_list")

            val urlList = serializedUrlList
                ?.split(",")
                ?.map {
                    val parts = it.split(";")
                    UrlList(parts[0], parts[1], parts[2].toLong(), parts[3])
                } ?: emptyList()

            try {

                val news = NewsModel(
                    title = title,
                    description = description,
                    newsId = newsId,
                    category = category,
                    timestamp = Timestamp.now(),
                    link = link,
                    linkTitle = linkTitle,
                    urlList = urlList
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