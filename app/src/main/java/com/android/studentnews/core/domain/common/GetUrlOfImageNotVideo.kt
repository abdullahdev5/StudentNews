package com.android.studentnews.core.domain.common

import androidx.compose.runtime.mutableIntStateOf
import com.android.studentnews.news.domain.model.UrlList

inline fun getUrlOfImageNotVideo(urlList: List<UrlList?>): String {
    var imageIndex = mutableIntStateOf(0)
    val imageUrl =
        if (
            urlList.get(imageIndex.intValue)
                ?.contentType.toString().startsWith("image/")
        ) urlList.get(imageIndex.intValue)?.url ?: ""
        else urlList.get(imageIndex.intValue++)?.url ?: ""

    return imageUrl
}