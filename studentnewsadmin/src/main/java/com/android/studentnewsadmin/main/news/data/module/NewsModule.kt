package com.android.studentnewsadmin.main.news.data.module

import com.android.studentnewsadmin.main.news.data.repository.NewsRepositoryImpl
import com.android.studentnewsadmin.main.news.data.worker.NewsWorker
import com.android.studentnewsadmin.main.news.domain.repository.NewsRepository
import com.android.studentnewsadmin.main.news.ui.viewmodel.NewsViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val newsModule = module {

    singleOf(::NewsRepositoryImpl) { bind<NewsRepository>() }

    viewModelOf(::NewsViewModel)

    workerOf(::NewsWorker)

}