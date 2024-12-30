package com.android.studentnews.core.domain.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sign

@OptIn(ExperimentalFoundationApi::class)
class MyOverScrollEffect(private val scope: CoroutineScope): OverscrollEffect {


    companion object {
        const val overScrollFriction = 0.15f
        const val flingAnimationDuration = 500
    }

    private val overScrollOffset = Animatable(0f)

    override fun applyToScroll(
        delta: Offset,
        source: NestedScrollSource,
        performScroll: (Offset) -> Offset,
    ): Offset {
        // in pre scroll we relax the overscroll if needed
        // relaxation: when we are in progress of the overscroll and user scrolls in the
        // different direction = substract the overscroll first

        val sameDirection = sign(delta.y) == sign(overScrollOffset.value)
        val consumedByPreScroll =
            if (abs(overScrollOffset.value) > 0.5 && !sameDirection) {
                val previousOverScrollValue = overScrollOffset.value
                val newOverScrollValue = overScrollOffset.value + delta.y

                if (sign(previousOverScrollValue) != sign(newOverScrollValue)) {
                    // sign changed, coerce to start scrolling and exit
                    scope.launch { overScrollOffset.snapTo(0f) }
                    Offset(x = 0f, y = delta.y + previousOverScrollValue)
                } else {
                    scope.launch { overScrollOffset.snapTo(overScrollOffset.value + delta.y) }
                    delta.copy(x = 0f)
                }
            } else {
                Offset.Zero
            }

        val leftForScroll = delta - consumedByPreScroll
        val consumedByScroll = performScroll(leftForScroll)
        val overScrollDelta = leftForScroll - consumedByScroll
        // if it is a drag, not a fling, add the delta left to our scroll value
        if (abs(overScrollDelta.y) > 0.5f && source == NestedScrollSource.UserInput) {
            scope.launch {
                overScrollOffset.snapTo(overScrollOffset.value + overScrollDelta.y * overScrollFriction)
            }
        }

        return consumedByPreScroll + consumedByScroll
    }

    override suspend fun applyToFling(
        velocity: Velocity,
        performFling: suspend (Velocity) -> Velocity,
    ) {
        val consumed = performFling(velocity)

        // When the filing happens - we just gradually animate our overScroll to 0
        val remaining = velocity - consumed

        overScrollOffset.animateTo(
            targetValue = 0f,
            initialVelocity = remaining.y * 0.8f,
            animationSpec = tween(durationMillis = flingAnimationDuration)
        )
    }

    override val effectModifier: Modifier
        get() = Modifier.offset { IntOffset(x = 0, y = overScrollOffset.value.roundToInt()) }

    override val isInProgress: Boolean
        get() = overScrollOffset.value != 0f

}