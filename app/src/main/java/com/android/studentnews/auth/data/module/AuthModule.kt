package com.android.studentnews.auth.data.module

import com.android.studentnews.auth.data.repository.AuthRepositoryImpl
import com.android.studentnews.auth.domain.repository.AuthRepository
import com.android.studentnews.auth.ui.viewModel.AuthViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val authModule = module {

    singleOf(::AuthRepositoryImpl) { bind<AuthRepository>() }

    viewModelOf(::AuthViewModel)

}