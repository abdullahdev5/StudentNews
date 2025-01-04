package com.android.studentnewsadmin.main.events.data.module

import com.android.studentnews.main.events.EventsWorker
import com.android.studentnews.main.events.data.repository.EventsRepositoryImpl
import com.android.studentnews.main.events.domain.repository.EventsRepository
import com.android.studentnews.main.events.ui.viewModels.EventsDetailViewModel
import com.android.studentnews.main.events.ui.viewModels.EventsViewModel
import com.android.studentnews.main.settings.registered_events.RegisteredEventsViewModel
import com.android.studentnews.main.settings.saved.ui.viewModels.SavedEventsViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val eventsModule = module {

    // Events Repository
    singleOf(::EventsRepositoryImpl) { bind<EventsRepository>()  }

    // Events ViewModel
    viewModelOf(::EventsViewModel)

    // Events Detail ViewModel
    viewModelOf(::EventsDetailViewModel)

    // Saved Events ViewModel
    viewModelOf(::SavedEventsViewModel)

    // Registered Events ViewModel
    viewModelOf(::RegisteredEventsViewModel)

    // Events Worker
    workerOf(::EventsWorker)

}