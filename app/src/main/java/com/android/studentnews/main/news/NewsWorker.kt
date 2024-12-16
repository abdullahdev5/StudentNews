package com.android.studentnews.main.news

import android.Manifest
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import androidx.compose.ui.util.fastJoinToString
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import coil.imageLoader
import coil.request.ImageRequest
import com.android.studentnews.MainActivity
import com.android.studentnews.NotificationRelated
import com.android.studentnews.R
import com.android.studentnews.main.MyBroadcastReceiver
import com.android.studentnews.main.events.DESCRIPTION
import com.android.studentnews.main.events.TITLE
import com.android.studentnews.main.events.URL_LIST
import com.android.studentnews.core.domain.common.getUrlOfImageNotVideo
import com.android.studentnews.news.domain.model.NewsModel
import com.android.studentnews.news.domain.repository.NewsRepository
import kotlin.apply
import kotlin.jvm.java
import kotlin.random.Random
import kotlin.run

const val NEWS_URI = "https://www.news.com"
const val SAVE_NEWS_ACTION = "com.android.studentnews.SAVE_NEWS_ACTION"
const val NEWS_ID = "newsId"
const val CATEGORY = "category"
const val LINK = "link"
const val LINK_TITLE = "linkTitle"
const val LIKES = "likes"
const val SHARE_COUNT = "shareCount"
const val NOTIFICATION_ID = "notification_id"


class NewsWorker(
    private val context: Context, // This is Mandatory for Worker
    val workerParameters: WorkerParameters, // This is Mandatory for Worker
    private val newsRepository: NewsRepository, // This is Injected by Koin
    private val notificationManager: NotificationManagerCompat, // This is Injected by koin
) : CoroutineWorker(context, workerParameters) { // Or Worker(context, workerParameters)

    override suspend fun doWork(): Result {
        return try {

            val news = newsRepository.getNewsUpdates()

            if (news != null) {

                showNotification(news)

            }


            Result.success()

        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun showNotification(news: NewsModel?) {

        val flag =
            PendingIntent.FLAG_IMMUTABLE

        val savedFlag =
            PendingIntent.FLAG_MUTABLE or
                    PendingIntent.FLAG_CANCEL_CURRENT

        val notificationId = Random.nextInt()

        val clickedIntentRequestCode = Random.nextInt()
        val savedIntentRequestCode = Random.nextInt()

        val clickedIntent = Intent(
            Intent.ACTION_VIEW,
            "$NEWS_URI/newsId=${news?.newsId ?: ""}".toUri(),
            context,
            MainActivity::class.java
        )

        val clickedPendingIntent = PendingIntent.getActivity(
            context,
            clickedIntentRequestCode,
            clickedIntent,
            flag
        )

        val serializedUrlList = news?.urlList?.fastJoinToString(",") {
            "${it.url};${it.contentType};${it.sizeBytes};${it.lastPathSegment}"
        }

        val title = news?.title ?: ""
        val description = news?.description
        val newsId = news?.newsId ?: ""
        val category = news?.category
        val link = news?.link
        val linkTitle = news?.linkTitle
        val imageUrl = getUrlOfImageNotVideo(news?.urlList ?: emptyList())
        val shareCount = news?.shareCount ?: 0
        val likes = news?.likes?.map { it }?.toTypedArray() ?: emptyArray()


        val request = ImageRequest.Builder(context)
            .data(imageUrl)
            .allowHardware(false)
            .target { drawable ->
                val bitmap = (drawable as BitmapDrawable).bitmap

                val saveIntent = Intent(
                    context,
                    MyBroadcastReceiver::class.java,
                ).apply {
                    action = SAVE_NEWS_ACTION
                    putExtra(TITLE, title)
                    putExtra(DESCRIPTION, description)
                    putExtra(NEWS_ID, newsId)
                    putExtra(CATEGORY, category)
                    putExtra(LINK, link)
                    putExtra(LINK_TITLE, linkTitle)
                    putExtra(URL_LIST, serializedUrlList)
                    putExtra(SHARE_COUNT, shareCount)
                    putExtra(LIKES, likes)
                    putExtra(NOTIFICATION_ID, notificationId)
                }

                val savedPendingIntent: PendingIntent =
                    PendingIntent.getBroadcast(
                        context,
                        savedIntentRequestCode,
                        saveIntent,
                        savedFlag
                    )


                val notification =
                    NotificationCompat.Builder(context, NotificationRelated.NEWS_CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(description)
                        .setLargeIcon(bitmap)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setContentIntent(clickedPendingIntent)
                        .addAction(
                            NotificationCompat.Action(
                                null,
                                HtmlCompat.fromHtml(
                                    "<font color=\"" + ContextCompat.getColor(
                                        context,
                                        R.color.green
                                    ) + "\">" + "Save" + "</font>",
                                    HtmlCompat.FROM_HTML_MODE_LEGACY
                                ),
                                savedPendingIntent
                            )
                        )

                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                }
                notificationManager.notify(
                    notificationId,
                    notification.build()
                )

            }
            .build()
        context.imageLoader.enqueue(request)
    }

}