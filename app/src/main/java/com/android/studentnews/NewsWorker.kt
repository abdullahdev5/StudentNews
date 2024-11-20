package com.android.studentnews

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.os.Build
import android.service.autofill.Validators.or
import androidx.compose.ui.util.fastJoinToString
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import coil.ImageLoader
import coil.imageLoader
import coil.request.ImageRequest
import com.android.studentnews.main.news.ui.screens.getUrlOfImageNotVideo
import com.android.studentnews.news.domain.model.NewsModel
import com.android.studentnews.news.domain.repository.NewsRepository

const val MY_URI = "https://www.google.com"
const val SAVE_NEWS_ACTION = "SAVE_NEWS_ACTION"
const val CLICKED_INTENT_REQUEST_CODE = 0
const val SAVED_INTENT_REQUEST_CODE = 1


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


        val clickedIntent = Intent(
            Intent.ACTION_VIEW,
            "$MY_URI/newsId=${news?.newsId ?: ""}".toUri(),
            context,
            MainActivity::class.java
        )

        val clickedPendingIntent: PendingIntent =
            TaskStackBuilder.create(context).run {
                addNextIntentWithParentStack(clickedIntent)
                getPendingIntent(CLICKED_INTENT_REQUEST_CODE, flag) as PendingIntent
            }

        val serializedUrlList = news?.urlList?.fastJoinToString(",") {
            "${it.url};${it.contentType};${it.sizeBytes};${it.lastPathSegment}"
        }
        val title = news?.title ?: "empty"
        val description = news?.description
        val newsId = news?.newsId ?: ""
        val category = news?.category
        val link = news?.link
        val linkTitle = news?.linkTitle
        val imageUrl = getUrlOfImageNotVideo(news?.urlList ?: emptyList())

        val request = ImageRequest.Builder(context)
            .data(imageUrl)
            .allowHardware(false)
            .target { drawable ->
                val bitmap = (drawable as BitmapDrawable).bitmap

                val saveIntent = Intent(
                    context,
                    SaveNewsBroadcastReceiver::class.java,
                ).apply {
                    action = SAVE_NEWS_ACTION
                    putExtra("title", title)
                    putExtra("description", description)
                    putExtra("newsId", newsId)
                    putExtra("category", category)
                    putExtra("link", link)
                    putExtra("link_title", linkTitle)
                    putExtra("url_list", serializedUrlList)
                }

                val savedPendingIntent: PendingIntent =
                    PendingIntent.getBroadcast(
                        context,
                        SAVED_INTENT_REQUEST_CODE,
                        saveIntent,
                        savedFlag
                    )


                val notification =
                    NotificationCompat.Builder(context, NotificationRelated.NEWS_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(title)
                        .setContentText(description)
                        .setLargeIcon(bitmap)
                        .setStyle(
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                NotificationCompat.BigPictureStyle()
                                    .bigPicture(bitmap)
                            } else {
                                NotificationCompat.BigPictureStyle()
                                    .bigPicture(bitmap)
                            }
                        )
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setContentIntent(clickedPendingIntent)
                        .addAction(
                            NotificationCompat.Action(
                                null,
                                "Save",
                                savedPendingIntent
                            )
                        )

                if (ActivityCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                }
                notificationManager.notify(
                    NotificationRelated.NEWS_NOTIFICATION_ID,
                    notification.build()
                )

            }
            .build()
        context.imageLoader.enqueue(request)
    }

}