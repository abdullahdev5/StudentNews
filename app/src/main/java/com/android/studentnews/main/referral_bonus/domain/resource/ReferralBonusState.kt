package com.android.studentnews.main.referral_bonus.domain.resource

sealed class ReferralBonusState<out T> {

    data object Loading: ReferralBonusState<Nothing>()
    data class Failed(val error: Throwable): ReferralBonusState<Nothing>()
    data class Success<T>(val data: T): ReferralBonusState<T>()

}