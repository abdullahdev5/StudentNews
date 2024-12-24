package com.android.studentnewsadmin.main.offers.data.module

import com.android.studentnewsadmin.main.offers.data.repository.OffersRepositoryImpl
import com.android.studentnewsadmin.main.offers.domain.repository.OffersRepository
import com.android.studentnewsadmin.main.offers.ui.viewModel.OffersViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val offersModule = module(
    createdAtStart = true
) {

    singleOf(::OffersRepositoryImpl) { bind<OffersRepository>() }

    viewModelOf(::OffersViewModel)

}