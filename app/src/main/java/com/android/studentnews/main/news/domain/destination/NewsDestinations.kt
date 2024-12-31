package com.android.studentnews.main.news.domain.destination

import kotlinx.serialization.Serializable

sealed class NewsDestinations {
    @Serializable
    data object NEWS_SCREEN: NewsDestinations()

    @Serializable
    data class NEWS_DETAIL_SCREEN(
        val newsId: String,
    ): NewsDestinations()

    @Serializable
    data class NEWS_LIST_ITEM_MORE_BOTTOM_SHEET(
        val newsId: String,
    ): NewsDestinations()

    @Serializable
    data class NEWS_LINK_SCREEN(
        val link: String,
    ): NewsDestinations()

    @Serializable
    data object LIKED_NEWS_SCREEN: NewsDestinations()
}