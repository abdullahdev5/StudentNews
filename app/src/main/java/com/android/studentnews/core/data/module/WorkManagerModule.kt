package com.android.studentnews.core.data.module

import androidx.work.WorkManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val workManagerModule = module {
    single<WorkManager> { WorkManager.getInstance(androidContext()) }
}