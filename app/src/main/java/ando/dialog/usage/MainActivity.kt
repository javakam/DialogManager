package ando.dialog.usage

import ando.dialog.DialogManager
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.bt_loading_by_progressbar).setOnClickListener(this)
        findViewById<Button>(R.id.bt_loading_by_progressbar_bg_imageview).setOnClickListener(this)
        findViewById<Button>(R.id.bt_loading_by_imageview).setOnClickListener(this)
        findViewById<Button>(R.id.bt_dialog_fragment).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.bt_loading_by_progressbar -> showLoadingDialogByProgressBarShape()
            R.id.bt_loading_by_progressbar_bg_imageview -> showLoadingDialogByProgressBarImageView()
            R.id.bt_loading_by_imageview -> showLoadingDialogByImageView()
            R.id.bt_dialog_fragment -> showFragmentDialog()
        }
    }

    /**
     * 2秒后改变Dialog宽高
     */
    private fun changeDialogSize() {
        findViewById<View>(R.id.bt_loading_by_progressbar).postDelayed({
            DialogManager.setWidth(500)
            DialogManager.setHeight(500)
            DialogManager.applySize()
        }, 2000)
    }

    private fun showLoadingDialogByProgressBarShape() {
        DialogManager.with(this, R.style.AndoDialog)
            .setContentView(R.layout.layout_ando_dialog_loading) { _, v ->
                v.findViewById<View>(ando.dialog.R.id.progressbar_ando_dialog_loading).visibility =
                    View.VISIBLE

                val textView =
                    v.findViewById<View>(ando.dialog.R.id.tv_ando_dialog_loading_text) as TextView
                textView.text = getText(R.string.str_ando_dialog_loading_text)
            }
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

        changeDialogSize()
    }

    private fun showLoadingDialogByProgressBarImageView() {
        DialogManager.with(this, R.style.AndoDialog)
            .setContentView(R.layout.layout_ando_dialog_loading) { _, v ->
                v.findViewById<View>(ando.dialog.R.id.progressbar_ando_dialog_loading).visibility =
                    View.VISIBLE

                val textView =
                    v.findViewById<View>(ando.dialog.R.id.tv_ando_dialog_loading_text) as TextView
                textView.text = getText(R.string.str_ando_dialog_loading_text)
            }
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

        changeDialogSize()
    }

    private fun showLoadingDialogByImageView() {
        DialogManager.with(this, R.style.AndoDialog)
            .useDialog()
            .applyConfig {

            }
            .setContentView(R.layout.layout_ando_dialog_loading) { _, v ->
                val tvLoadingTx: TextView =
                    v.findViewById(ando.dialog.R.id.tv_ando_dialog_loading_text)
                tvLoadingTx.text = getText(R.string.str_ando_dialog_loading_text)

                val ivLoading: ImageView = v.findViewById(ando.dialog.R.id.iv_ando_dialog_loading)
                ivLoading.visibility = View.VISIBLE
                val anim =
                    AnimationUtils.loadAnimation(this, ando.dialog.R.anim.anim_ando_dialog_loading)
                ivLoading.startAnimation(anim)
            }
            .setCancelable(true) //支持返回键关闭弹窗 true
            .setCanceledOnTouchOutside(true)
            .setWidth(300)
            .setHeight(300)
            .setOnShowListener {
                //对话框显示后再设置窗体才有效果
                findViewById<View>(R.id.bt_loading_by_progressbar).postDelayed({
                    val attributes = it.attributes
                    attributes?.apply {
                        width = 400
                        height = 400
                        gravity = Gravity.CENTER //居中显示
                        dimAmount = 0.5f         //背景透明度  取值范围 0 ~ 1
                    }
                    it.attributes = attributes
                }, 2000)
            }
            .setOnDismissListener {
            }
            .apply {
                //setTitle("Title")
                //setOnCancelListener{}
                //...
            }
            .show()

        //changeDialogSize()
    }

    private fun showFragmentDialog() {
        DialogManager.dismiss()
        //PickerDialog().show(this)
        //changeDialogSize()

        DialogManager.with(this, R.style.AndoDialog)
            .useDialogFragment()
            .applyConfig {

            }
            .setContentView(R.layout.layout_ando_dialog_loading) { _, v ->
                v.findViewById<View>(ando.dialog.R.id.progressbar_ando_dialog_loading).visibility =
                    View.VISIBLE

                val textView =
                    v.findViewById<View>(ando.dialog.R.id.tv_ando_dialog_loading_text) as TextView
                textView.text = getText(R.string.str_ando_dialog_loading_text)
            }
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

        changeDialogSize()
    }

}