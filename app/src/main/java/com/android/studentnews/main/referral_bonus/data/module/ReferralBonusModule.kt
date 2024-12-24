package com.android.studentnews.main.referral_bonus.data.module

import com.android.studentnews.main.referral_bonus.data.repository.ReferralBonusRepositoryImpl
import com.android.studentnews.main.referral_bonus.domain.repository.ReferralBonusRepository
import com.android.studentnews.main.referral_bonus.ui.viewModel.ReferralBonusViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val referralBonusModule = module {

    singleOf(::ReferralBonusRepositoryImpl) { bind<ReferralBonusRepository>() }

    viewModelOf(::ReferralBonusViewModel)

}