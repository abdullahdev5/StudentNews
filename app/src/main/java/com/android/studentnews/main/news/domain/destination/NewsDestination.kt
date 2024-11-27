package com.android.studentnews.main.news.domain.destination

import kotlinx.serialization.Serializable

sealed class NewsDestination {
    @Serializable
    data object NEWS_SCREEN: NewsDestination()

    @Serializable
    data class NEWS_DETAIL_SCREEN(
        val newsId: String,
    ): NewsDestination()

    @Serializable
    data class NEWS_LINK_SCREEN(
        val link: String,
    ): NewsDestination()

    @Serializable
    data object LIKED_NEWS_SCREEN: NewsDestination()
}