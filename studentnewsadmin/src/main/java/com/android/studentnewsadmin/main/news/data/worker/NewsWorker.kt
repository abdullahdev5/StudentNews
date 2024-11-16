package com.android.studentnewsadmin.main.news.data.worker

import android.Manifest
import android.R
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.impl.foreground.SystemForegroundService
import androidx.work.workDataOf
import com.android.studentnewsadmin.NotificationRelated
import com.android.studentnewsadmin.core.domain.resource.NewsState
import com.android.studentnewsadmin.main.news.domain.model.NewsModel
import com.android.studentnewsadmin.main.news.domain.model.UrlList
import com.android.studentnewsadmin.main.news.domain.repository.NewsRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlin.math.roundToInt


const val TITLE = "title"
const val DESCRIPTION = "description"
const val URI_LIST = "uri_list"
const val CATEGORY = "category"
const val LINK = "link"
const val LINK_TITLE = "link_title"
const val SUCCESS_MSG = "success_msg"



class NewsWorker(
    private val context: Context,
    val workerParameters: WorkerParameters,
    private val newsRepository: NewsRepository,
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {

        return try {

            val title = inputData.getString(TITLE) ?: ""
            val description = inputData.getString(DESCRIPTION) ?: ""
            val stringArrayList = inputData.getStringArray(URI_LIST)
            val category = inputData.getString(CATEGORY) ?: ""
            val link = inputData.getString(LINK) ?: ""
            val linkTitle = inputData.getString(LINK_TITLE) ?: ""

            val uriList = stringArrayList?.map { Uri.parse(it) } ?: emptyList()

            setForeground(showLoadingNotification(context, title))

            val result = newsRepository
                .onNewsAdd(
                    title,
                    description,
                    uriList,
                    category,
                    link,
                    linkTitle,
                    context
                ).first()

            when (result) {

                is NewsState.Success<*> -> {
                    val data = Data.Builder()
                        .putString(TITLE, title)
                        .putString(SUCCESS_MSG, result.data.toString())
                        .build()

                    Result.success(data)
                }

                is NewsState.Failed -> {
                    failureNotification(context, result.message.localizedMessage ?: "", title)
                    Result.failure()
                }

                else -> Result.failure()
            }

        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun failureNotification(context: Context, errorMsg: String, title: String) {

        val notification = NotificationCompat
            .Builder(context, NotificationRelated.FAILURE_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_media_next)
            .setContentTitle("Failed to Add New. Title: $title")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Error:- $errorMsg")
            )
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(NotificationRelated.FAILURE_NOTIFICATION_ID, notification)

    }

    private fun showLoadingNotification(context: Context, title: String): ForegroundInfo {

        val notification = NotificationCompat.Builder(context, NotificationRelated.MEDIA_ADDING_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_media_next)
            .setContentTitle("Uploading....")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(title)
            )

        return ForegroundInfo(NotificationRelated.MEDIA_ADDING_NOTIFICATION_ID, notification.build())
    }

}