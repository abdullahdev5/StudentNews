package com.android.studentnews

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationManagerCompat
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.util.DebugLogger
import com.android.studentnews.auth.data.module.authModule
import com.android.studentnews.core.data.module.firebaseModule
import com.android.studentnews.core.data.module.notificationModule
import com.android.studentnews.core.data.module.workManagerModule
import com.android.studentnews.main.account.data.module.accountModule
import com.android.studentnews.main.search.searchModule
import com.android.studentnews.news.data.module.newsDetailModule
import com.android.studentnews.news.data.module.newsModule
import com.android.studentnews.news.data.module.savedNewsModule
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import java.util.logging.Logger

class StudentsNewsApp : Application(), KoinComponent, ImageLoaderFactory {

    private val notificationManager: NotificationManagerCompat by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@StudentsNewsApp)
            workManagerFactory()
            modules(
                listOf(
                    firebaseModule,
                    authModule,
                    workManagerModule,
                    newsModule,
                    newsDetailModule,
                    searchModule,
                    savedNewsModule,
                    notificationModule,
                    accountModule
                )
            )
        }

        val channel = NotificationChannel(
            NotificationRelated.NEWS_CHANNEL_ID,
            NotificationRelated.NEWS_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.1)
                    .strongReferencesEnabled(true)
                    .build()
            }
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCache {
                DiskCache.Builder()
                    .maxSizePercent(0.03)
                    .directory(cacheDir)
                    .build()
            }
            .logger(DebugLogger())
            .build()
    }

}


class NotificationRelated {
    companion object {
        const val NEWS_CHANNEL_NAME = "News Channel"
        const val NEWS_CHANNEL_ID = "news_channel"
        const val NEWS_NOTIFICATION_ID = 1
    }
}