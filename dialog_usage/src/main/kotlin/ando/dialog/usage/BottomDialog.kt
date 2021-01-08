package ando.dialog.usage

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.view.marginTop

/**
 * 从底部弹出的Dialog
 *
 * @author javakam
 */
abstract class BottomDialog : Dialog {

    constructor(
        context: Context
    ) : this(context, 0)

    constructor(
        context: Context,
        themeResId: Int = R.style.AndoDialog
    ) : super(context, themeResId)

    constructor(
        context: Context, cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener
    ) : super(context, cancelable, cancelListener)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.initBeforeShow(savedInstanceState)
        this.setContentView(getLayoutId())
        this.window?.apply { initWindow(this) }
        this.initView()
    }

    open fun initBeforeShow(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
    }

    open fun initWindow(window: Window) {
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

    abstract fun initView()

    abstract fun getLayoutId(): Int

}