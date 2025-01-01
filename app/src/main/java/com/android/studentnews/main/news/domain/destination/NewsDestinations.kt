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
    data class NEWS_LINK_SCREEN(
        val link: String,
    ): NewsDestinations()

    @Serializable
    data object LIKED_NEWS_SCREEN: NewsDestinations()

    class BottomSheetDestinations {
        companion object {
            const val NEWS_LIST_ITEM_MORE_OPTIONS_BOTTOM_SHEET_DESTINATION =
                "news_list_item_more_options_bottom_sheet"
        }
    }
}