package com.android.studentnews.core.domain.common

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isFinite
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
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