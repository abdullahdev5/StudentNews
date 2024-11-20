package com.android.studentnews.main.news.domain.destination

import kotlinx.serialization.Serializable

sealed class NewsDestination {
    @Serializable
    object MAIN_SCREEN: NewsDestination()

    @Serializable
    data class NEWS_DETAIL_SCREEN(
        val newsId: String,
    ): NewsDestination()

    @Serializable
    data class NEWS_LINK_SCREEN(
        val link: String,
    ): NewsDestination()

    @Serializable
    object SAVED_NEWS_SCREEN: NewsDestination()
}