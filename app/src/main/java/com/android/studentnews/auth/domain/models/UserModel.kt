package com.android.studentnews.auth.domain.models

import com.android.studentnews.main.referral_bonus.domain.model.ReferralBonusModel
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Field

data class UserModel(
    val email: String? = "",
    val password: String,
    val uid: String? = "",
    val registrationData: RegistrationData? = null,
    val profilePic: String? = "",
    val profilePicBgColor: Int? = 0,
    @field:JvmField
    var isUserShareTheNews: Boolean? = false,
    var referralBonus: ReferralBonusModel? = null,
) {
    constructor(): this(
        email = "",
        password = "",
        uid = "",
        registrationData = null,
        profilePic = "",
        profilePicBgColor = 0,
        isUserShareTheNews = false,
        referralBonus = null
    )
}
