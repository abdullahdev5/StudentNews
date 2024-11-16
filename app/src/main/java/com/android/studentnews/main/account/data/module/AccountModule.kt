package com.android.studentnews.main.account.data.module

import com.android.studentnews.main.account.data.repository.AccountRepositoryImpl
import com.android.studentnews.main.account.domain.repository.AccountRepository
import com.android.studentnews.main.account.ui.viewmodel.AccountViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val accountModule = module {

    singleOf(::AccountRepositoryImpl) { bind<AccountRepository>() }

    viewModelOf(::AccountViewModel)

}