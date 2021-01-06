package ando.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.Window
import android.view.WindowManager

/**
 * 从底部弹出的Dialog
 *
 * @author javakam
 */
abstract class BottomDialog @JvmOverloads constructor(
    context: Context,
    themeResId: Int = R.style.AndoBottomDialogStyle
) : Dialog(context, themeResId) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(true)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.setContentView(getLayoutId())
        initWindow()
        initView()
    }

    open fun initWindow() {
        val window = window
        if (window != null) {
            //WindowUtils.hideNavigation(window);
            window.setGravity(Gravity.BOTTOM)
            val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val dm = DisplayMetrics()
            manager.defaultDisplay.getMetrics(dm)
            val lp = window.attributes
            lp.width = dm.widthPixels
            window.attributes = lp
        }
    }

    abstract fun initView()

    abstract fun getLayoutId(): Int

}