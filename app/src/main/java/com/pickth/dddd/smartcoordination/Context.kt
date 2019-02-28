package com.pickth.dddd.smartcoordination

import android.content.Context

fun Context.convertDpToPixel(dp: Float): Float = dp * resources.displayMetrics.density

fun Context.convertDpToPixel(dp: Int): Int = convertDpToPixel(dp.toFloat()).toInt()
