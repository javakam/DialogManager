package ando.dialog.sample

import android.content.Context

/**
 * @author javakam
 * @date 2021-05-11  15:30
 */
fun Context.dp2px(value: Int): Int = (value * resources.displayMetrics.density).toInt()

fun Context.dp2px(value: Float): Int = (value * resources.displayMetrics.density).toInt()
