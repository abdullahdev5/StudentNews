package com.android.studentnews.news.domain.model

import androidx.compose.runtime.Immutable
import com.google.firebase.Timestamp

@Immutable
data class NewsModel(
    val title: String? = "",
    val description: String? = "",
    val newsId: String? = "",
    val category: String? = "",
    val timestamp: Timestamp? = null,
    val link: String? = "",
    val linkTitle: String? = "",
    val urlList: List<UrlList>,
    val shareCount: Int? = 0,
    val likes: List<String>? = emptyList()
) {
    constructor() : this("", "", "", "", null, "", "", emptyList(), 0, emptyList())
}

data class UrlList(
    val url: String,
    val contentType: String,
    val sizeBytes: Long
) {
    constructor() : this("", "", 0L)
}
