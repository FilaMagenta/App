package com.arnyminerz.filamagenta.ui.shape

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import com.arnyminerz.filamagenta.utils.randomFloat
import kotlin.math.floor

private fun Path.tearedPaper(
    dx: Float,
    steps: Int,
    xOffsetPercentMargin: Float = .1f,
    yOffsetPercentMargin: Float = .2f,
) {
    for (i in 0 until steps) {
        val randomHorizontalOffset = randomFloat(1f - xOffsetPercentMargin, 1f)
        val randomVerticalOffset = randomFloat(1f - yOffsetPercentMargin, 1f)

        relativeLineTo(dx * randomHorizontalOffset, dx * randomVerticalOffset)
        relativeLineTo(2 * dx - dx * randomHorizontalOffset, -dx * randomVerticalOffset)
    }
    relativeLineTo(dx * .6f, dx * .7f)
}

@Composable
@Suppress("MagicNumber", "UnusedPrivateProperty")
fun BrokenPaperShape(
    brokenEvery: Float,
    xOffsetPercentMargin: Float = .1f,
    yOffsetPercentMargin: Float = .2f,
    bottom: Boolean = true
): Shape {
    return GenericShape { size, _ ->
        val steps = floor(size.width / brokenEvery).toInt()
        moveTo(0f, 0f)

        val dx = brokenEvery / 2
        tearedPaper(dx, steps, xOffsetPercentMargin, yOffsetPercentMargin)

        lineTo(size.width, 0f)
        lineTo(size.width, size.height)

        if (bottom) {
            tearedPaper(-dx, steps, xOffsetPercentMargin, yOffsetPercentMargin)
        }

        lineTo(0f, size.height)

        close()
    }
}
