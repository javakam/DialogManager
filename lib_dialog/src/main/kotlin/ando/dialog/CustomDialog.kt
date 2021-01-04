package ando.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView

class CustomDialog constructor(context: Context, theme: Int, string: String) :
    Dialog(context, theme) {

    var tvLoadingTx: TextView
    var ivLoading: ImageView

    constructor(context: Context) : this(
        context,
        R.style.loading_dialog,
        context.getString(R.string.str_ando_dialog_loading)
    )

    constructor(context: Context, string: String) : this(context, R.style.loading_dialog, string)

    init {
        setCanceledOnTouchOutside(true)//点击其他区域时   true  关闭弹窗  false  不关闭弹窗
        setOnCancelListener { dismiss() }
        setContentView(R.layout.layout_ando_dialog_imageview)
        tvLoadingTx = findViewById(R.id.tv_loading_tx)
        tvLoadingTx.text = string
        ivLoading = findViewById(R.id.iv_loading)
        val anim = AnimationUtils.loadAnimation(context, R.anim.ando_dialog_anim_loading)
        ivLoading.startAnimation(anim)

        window?.apply {
            attributes.gravity = Gravity.CENTER //居中显示
            attributes.dimAmount = 0.5f         //背景透明度  取值范围 0 ~ 1
        }
    }

}