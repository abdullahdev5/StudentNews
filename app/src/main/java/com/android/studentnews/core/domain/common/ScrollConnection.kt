package com.android.studentnews.core.domain.common

import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource

class CollapsingAppBarNestedScrollConnection(
    val appBarMaxHeight: Float,
) : NestedScrollConnection {

    var currentAppBarHeight by mutableFloatStateOf(appBarMaxHeight)
        private set

    override fun onPreScroll(
        available: Offset,
        source: NestedScrollSource,
    ): Offset {
        val delta = available.y
        val newsHeight = currentAppBarHeight + delta
        val previousHeight = currentAppBarHeight
        currentAppBarHeight = newsHeight.coerceIn(0f, appBarMaxHeight)
        val consumed = currentAppBarHeight - previousHeight
        return Offset(0f, consumed)
    }

}