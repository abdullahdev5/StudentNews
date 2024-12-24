package com.android.studentnews.auth.domain.models

import com.android.studentnews.main.referral_bonus.domain.model.ReferralBonusModel

data class UserModel(
    val email: String? = "",
    val password: String,
    val uid: String? = "",
    val registrationData: RegistrationData? = null,
    val profilePic: String? = "",
    val profilePicBgColor: Int? = 0,
    var referralBonus: ReferralBonusModel? = null,
) {
    constructor(): this(
        email = "",
        password = "",
        uid = "",
        registrationData = null,
        profilePic = "",
        profilePicBgColor = 0,
        referralBonus = null
    )
}
