package ando.dialog.usage

import ando.dialog.core.BaseDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager

/**
 * 从底部弹出的Dialog
 *
 * @author javakam
 */
abstract class BottomDialog : BaseDialog {

    constructor(
        context: Context
    ) : this(context, 0)

    constructor(
        context: Context,
        themeResId: Int = R.style.AndoDialog
    ) : super(context, themeResId)

    override fun initDialog(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
    }

    override fun initWindow(window: Window) {
        window.apply {
            hideNavigation(this)
            setGravity(Gravity.BOTTOM)
            val params = attributes

            val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val dm = DisplayMetrics()
            @Suppress("DEPRECATION")
            manager.defaultDisplay.getMetrics(dm)

            params.width = dm.widthPixels
            attributes = params

            setWindowAnimations(R.style.AndoBottomDialogAnimation)
        }
    }

    /**
     * 隐藏底部导航栏
     */
    @Suppress("DEPRECATION")
    open fun hideNavigation(window: Window) {
        window.decorView.apply {
            val option = (View.SYSTEM_UI_FLAG_IMMERSIVE
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            systemUiVisibility = option
            setOnSystemUiVisibilityChangeListener { visibility ->
                if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                    systemUiVisibility = option
                }
            }
        }
    }

}