package com.android.studentnews.news.data.module

import com.android.studentnews.main.news.NewsWorker
import com.android.studentnews.main.news.data.repository.NewsDetailRepositoryImpl
import com.android.studentnews.main.news.domain.repository.NewsDetailRepository
import com.android.studentnews.main.news.ui.viewModel.NewsDetailViewModel
import com.android.studentnews.main.news.ui.viewModel.SavedNewsViewModel
import com.android.studentnews.news.data.repository.NewsRepositoryImpl
import com.android.studentnews.news.domain.repository.NewsRepository
import com.android.studentnews.news.ui.viewModel.NewsViewModel
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

val newsDetailModule = module {
    singleOf(::NewsDetailRepositoryImpl) { bind< NewsDetailRepository>() }
    viewModelOf(::NewsDetailViewModel)
}

val savedNewsModule = module {
    viewModelOf(::SavedNewsViewModel)
}