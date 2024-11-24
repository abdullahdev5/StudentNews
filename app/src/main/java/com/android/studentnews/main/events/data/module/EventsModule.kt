package com.android.studentnewsadmin.main.events.data.module

import com.android.studentnews.main.events.data.repository.EventsRepositoryImpl
import com.android.studentnews.main.events.domain.repository.EventsRepository
import com.android.studentnews.main.events.ui.viewModels.EventsViewModel
import com.android.studentnews.main.events.ui.viewModels.SavedEventsViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val eventsModule = module {

    singleOf(::EventsRepositoryImpl) { bind<EventsRepository>()  }

    viewModelOf(::EventsViewModel)

    viewModelOf(::SavedEventsViewModel)

}