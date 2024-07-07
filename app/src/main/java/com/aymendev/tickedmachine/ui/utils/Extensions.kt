package com.aymendev.tickedmachine.ui.utils

import android.content.Context
import android.util.DisplayMetrics


fun dpToPixel(dp: Float, context: Context): Float {
    return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}