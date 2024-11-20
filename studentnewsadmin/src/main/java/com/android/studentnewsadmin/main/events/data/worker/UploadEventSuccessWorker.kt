package com.android.studentnewsadmin.main.events.data.worker

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
import com.android.studentnewsadmin.main.news.data.worker.SUCCESS_MSG
import com.android.studentnewsadmin.main.news.data.worker.TITLE

class UploadEventSuccessWorker(
    private val context: Context,
    val workerParameters: WorkerParameters,
) : Worker(context, workerParameters) {


    override fun doWork(): Result {

        val successMsg = inputData.getString(SUCCESS_MSG) ?: ""
        val title = inputData.getString(TITLE) ?: ""

        SuccessNotification(successMsg, title)
        return Result.success()
    }

    private fun SuccessNotification(successMsg: String, title: String) {

        val notification =
            NotificationCompat.Builder(context, NotificationRelated.SUCCESS_CHANNEL_ID)
                .setSmallIcon(R.drawable.stat_sys_upload_done)
                .setContentTitle(successMsg)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(title)
                )
                .build()

        val notificationManager = NotificationManagerCompat.from(context)

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
        notificationManager.notify(NotificationRelated.SUCCESS_NOTIFICATION_ID, notification)
    }

}