package com.android.studentnews.core.data.module

import android.app.NotificationManager
import androidx.core.app.NotificationManagerCompat
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val notificationModule = module {

    single<NotificationManagerCompat> {
        NotificationManagerCompat.from(androidContext())
    }

}