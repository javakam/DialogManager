package ando.dialog.sample

import ando.dialog.core.DialogManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * Title: ConfigurationActivity
 * <p>
 * Description:
 * </p>
 * @author javakam
 * @date 2021/1/8  14:43
 */
class ConfigurationActivity : AppCompatActivity() {

    private lateinit var mBtShowDialog: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog_configuration)
        mBtShowDialog = findViewById(R.id.bt_dialog_show_configuration)
        mBtShowDialog.setOnClickListener { showDialog() }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Log.e("123", "屏幕方向: ${newConfig.orientation}")
        super.onConfigurationChanged(newConfig)
    }

    private fun showDialog() {
        DialogManager.with(this, R.style.AndoLoadingDialog)
            .setContentView(R.layout.layout_ando_dialog_loading) { _, v ->
                v.findViewById<View>(R.id.progressbar_ando_dialog_loading).visibility =
                    View.VISIBLE

                v.findViewById<TextView>(R.id.tv_ando_dialog_loading_text).text =
                    getText(R.string.str_ando_dialog_loading_text)
            }
            .setAnimationId(R.style.AndoBottomDialogAnimation)
            .setCancelable(true) //支持返回键关闭弹窗 true
            .setCanceledOnTouchOutside(true)
            .setOnDismissListener {

            }
            .apply {
                //setTitle("Title")
                //setOnCancelListener{}
                //...
            }
            .show()

    }

}