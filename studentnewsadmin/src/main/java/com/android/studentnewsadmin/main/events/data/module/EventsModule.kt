package com.android.studentnewsadmin.main.events.data.module

import com.android.studentnewsadmin.main.events.data.repository.EventsRepositoryImpl
import com.android.studentnewsadmin.main.events.data.worker.UploadEventsWorker
import com.android.studentnewsadmin.main.events.domain.repository.EventsRepository
import com.android.studentnewsadmin.main.events.ui.viewModels.EventsViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val eventsModule = module {

    singleOf(::EventsRepositoryImpl) { bind<EventsRepository>()  }

    viewModelOf(::EventsViewModel)

    workerOf(::UploadEventsWorker)

}