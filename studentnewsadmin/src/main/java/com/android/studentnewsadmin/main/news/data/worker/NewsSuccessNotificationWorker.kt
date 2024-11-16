package com.android.studentnewsadmin.main.news.data.worker

import android.Manifest
import android.R
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.android.studentnewsadmin.NotificationRelated

class NewsSuccessNotificationWorker(
    private val context: Context,
    val workerParameters: WorkerParameters
) : Worker(context, workerParameters) {

    override fun doWork(): Result {

        val title = inputData.getString(TITLE) ?: ""
        val successMsg = inputData.getString(SUCCESS_MSG) ?: ""

        successNotification(context, title, successMsg)
        Result.success()

        return Result.success()


    }

    private fun successNotification(
        context: Context,
        title: String,
        successMsg: String
    ) {

        val notificationManager = NotificationManagerCompat.from(context)

        val notification = NotificationCompat.Builder(context, NotificationRelated.SUCCESS_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_media_next)
            .setContentTitle(successMsg)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(title)
            )

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        notificationManager.notify(NotificationRelated.SUCCESS_NOTIFICATION_ID, notification.build())

    }

}