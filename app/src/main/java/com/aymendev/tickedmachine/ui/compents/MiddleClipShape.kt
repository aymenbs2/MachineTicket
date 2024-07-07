package com.aymendev.tickedmachine.ui.compents

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class MiddleClipShape (val  percentageW:Float=1f,val percentageH:Float=1f ): Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val middleHeight = size.height *percentageH
        val top = middleHeight
        return Outline.Rectangle(Rect(0f, 0F, size.width, top + middleHeight))
    }
}

fun VerticalClipShape(percentage: Float) = GenericShape { size, _ ->
    val clipHeight = size.height * percentage
    moveTo(0f, 0f)
    lineTo(size.width, 0f)
    lineTo(size.width, clipHeight)
    lineTo(0f, clipHeight)
    close()
}

fun HorizontalClipShapeLeftToRight(percentage: Float) = GenericShape { size, _ ->
    val clipWidth = size.width *  (1-percentage)
    moveTo(0f, 0f)
    lineTo(clipWidth, 0f)
    lineTo(clipWidth, size.height)
    lineTo(0f, size.height)
    close()
}

fun HorizontalClipShapeRightToLeft(percentage: Float) = GenericShape { size, _ ->
    val clipWidth = size.width * percentage
    moveTo(size.width, 0f)
    lineTo(clipWidth, 0f)
    lineTo(clipWidth, size.height)
    lineTo(size.width, size.height)
    close()
}
fun HorizontalClipShape(percentage: Float) = GenericShape { size, _ ->
    val clipWidth = size.width * percentage
    moveTo(0f, 0f)
    lineTo(clipWidth, 0f)
    lineTo(clipWidth, size.height)
    lineTo(0f, size.height)
    close()
}