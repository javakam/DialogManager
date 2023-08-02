package ando.dialog.sample

import ando.dialog.sample.utils.QMUIStatusBarHelper
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

class BottomDialogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_dialog_bottom_activity)

        setDisplay()
    }

    private fun setDisplay() {
        val dm = DisplayMetrics()
        window.windowManager.defaultDisplay.getMetrics(dm)
        val p = window.attributes
        p.height = WindowManager.LayoutParams.MATCH_PARENT
        p.width = WindowManager.LayoutParams.MATCH_PARENT
        //window.setWindowAnimations(ando.dialog.usage.R.style.AndoBottomDialogAnimation)
        window.attributes = p

        QMUIStatusBarHelper.translucent(this)
        QMUIStatusBarHelper.setStatusBarLightMode(this)
    }
}