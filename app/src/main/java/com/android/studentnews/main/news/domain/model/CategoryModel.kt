package com.android.studentnews.main.news.domain.model

import com.google.firebase.Timestamp

data class CategoryModel(
    val name: String? = "",
    val imageUrl: String? = "",
    val categoryId: String? = "",
    val timestamp: Timestamp? = null
) {
    constructor() : this("", "", "", null)
}
