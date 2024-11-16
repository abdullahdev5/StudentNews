package com.android.studentnewsadmin

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationManagerCompat
import com.android.studentnewsadmin.core.data.module.firebaseModule
import com.android.studentnewsadmin.core.data.module.workManagerModule
import com.android.studentnewsadmin.main.news.data.module.newsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin


class StudentNewsAdminApp : Application(), KoinComponent {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@StudentNewsAdminApp)
            androidLogger()
            workManagerFactory()
            modules(
                listOf(
                    firebaseModule,
                    workManagerModule,
                    newsModule,
                )
            )
        }

        val mediaAddingNotificationChannel = NotificationChannel(
            NotificationRelated.MEDIA_ADDING_CHANNEL_ID,
            NotificationRelated.MEDIA_ADDING_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )

        val successNotificationChannel = NotificationChannel(
            NotificationRelated.SUCCESS_CHANNEL_ID,
            NotificationRelated.SUCCESS_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )

        val failureNotificationChannel = NotificationChannel(
            NotificationRelated.FAILURE_CHANNEL_ID,
            NotificationRelated.FAILURE_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )

        val notificationManager = NotificationManagerCompat.from(this)

        notificationManager.createNotificationChannel(mediaAddingNotificationChannel)
        notificationManager.createNotificationChannel(successNotificationChannel)
        notificationManager.createNotificationChannel(failureNotificationChannel)

    }

}

class NotificationRelated {
    companion object {
        // Channel ID
        const val MEDIA_ADDING_CHANNEL_ID = "media_adding"
        const val SUCCESS_CHANNEL_ID = "success_channel"
        const val FAILURE_CHANNEL_ID = "failure_channel"

        // Channel Name
        const val MEDIA_ADDING_CHANNEL_NAME = "Media Adding"
        const val SUCCESS_CHANNEL_NAME = "Success Channel"
        const val FAILURE_CHANNEL_NAME = "Failure Channel"

        // Notification ID
        const val MEDIA_ADDING_NOTIFICATION_ID = 3
        const val SUCCESS_NOTIFICATION_ID = 0
        const val FAILURE_NOTIFICATION_ID = 1
    }
}
