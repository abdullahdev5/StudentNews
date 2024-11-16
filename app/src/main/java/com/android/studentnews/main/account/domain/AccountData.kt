package com.android.studentnews.main.account.domain

data class AccountList(
    val label: String,
    val value: String,
)

class AccountDataLabel {
    companion object {
        const val EMAIL = "Email"
        const val DEGREE = "Degree"
        const val DEGREE_TITLE = "Degree Title"
        const val SEMESTER = "Semester"
        const val PHONE_NUMBER = "Phone Number"
        const val CITY = "City"
        const val ADDRESS = "Address"
    }
}