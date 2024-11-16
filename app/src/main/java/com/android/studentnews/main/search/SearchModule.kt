package com.android.studentnews.main.search

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val searchModule = module {
    viewModelOf(::SearchViewModel)
}