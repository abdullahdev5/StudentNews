package com.android.studentnews.core.domain.common

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import kotlin.math.nextUp
import kotlin.math.roundToInt

class CollapsingAppBarNestedScrollConnection(
    val appBarMaxHeight: Int,
) : NestedScrollConnection {

    var appBarOffset by mutableIntStateOf(0)
        private set

    override fun onPreScroll(
        available: Offset,
        source: NestedScrollSource,
    ): Offset {
        val delta: Int = available.y.roundToInt()
        val newsOffset: Int = appBarOffset + delta
        val previousOffset: Int = appBarOffset
        appBarOffset = newsOffset.coerceIn(
            -appBarMaxHeight,
            0
        )
        val consumed: Int = appBarOffset - previousOffset
        return Offset(0f, consumed.toFloat())
    }

}

class CollapsingTopBarButAppearWhenTopReached(
    private val topBarMaxHeightFloat: Float
): NestedScrollConnection {

    var topBarOffsetFloat by mutableFloatStateOf(topBarMaxHeightFloat)
        private set

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val delta = available.y

        if (delta >= 0) {
            return Offset.Zero
        }

        val newOffset = topBarOffsetFloat + delta
        val previousOffset = topBarOffsetFloat

        topBarOffsetFloat = newOffset.coerceIn(0f, topBarMaxHeightFloat)

        val consumed = topBarOffsetFloat - previousOffset

        return Offset(0f, consumed)
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource,
    ): Offset {
        val delta = available.y

        val newOffset = topBarOffsetFloat + delta
        val previousOffset = topBarOffsetFloat

        topBarOffsetFloat = newOffset.coerceIn(0f, topBarMaxHeightFloat)

        val consumed = topBarOffsetFloat - previousOffset

        return Offset(0f, consumed)
    }

}