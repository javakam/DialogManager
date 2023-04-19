package ando.dialog.usage

import ando.dialog.core.DialogManager
import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar

/**
 * # DialogUsage
 *
 * @author javakam
 * @date 2021/3/8  16:23
 */

//默认样式
fun loadingDialog(context: Context): DialogManager = loadingCircle(context = context)

fun loadingLine(context: Context): DialogManager =
    DialogManager.with(context, R.style.AndoLoadingDialog)
        .setContentView(R.layout.layout_ando_dialog_loading) { v ->
            v.findViewById<ProgressBar>(R.id.progressbar_ando_dialog_loading).visibility = View.VISIBLE
        }
        .setDimmedBehind(true)
        .setCancelable(true)
        .setCanceledOnTouchOutside(true)

fun loadingCircle(context: Context): DialogManager =
    DialogManager.with(context, R.style.AndoLoadingDialog)
        .setContentView(R.layout.layout_ando_dialog_loading) { v ->
            val image: ImageView = v.findViewById(R.id.iv_ando_dialog_loading)
            image.visibility = View.VISIBLE
            val anim = AnimationUtils.loadAnimation(context, R.anim.anim_ando_dialog_loading)
            image.startAnimation(anim)
        }
        .setDimmedBehind(true)
        .setCancelable(true)
        .setCanceledOnTouchOutside(true)
