package ando.dialog.usage

import ando.dialog.DialogManager
import ando.dialog.use.PickerDialog
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
        findViewById<Button>(R.id.bt_loading_by_imageview).setOnClickListener(this)
        findViewById<Button>(R.id.bt_dialog_base).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.bt_loading_by_progressbar -> showLoadingDialogByProgressBar()
            R.id.bt_loading_by_imageview -> showLoadingDialogByImageView()
            R.id.bt_dialog_base -> showBaseDialogPicker()
        }
    }

    private fun changeDialogSize() {
        findViewById<View>(R.id.bt_loading_by_progressbar).postDelayed({
            DialogManager.setWidth(500)
            DialogManager.setHeight(500)
            DialogManager.applySize()
        }, 2000)
    }

    private fun showLoadingDialogByProgressBar() {
        DialogManager.with(this, R.style.CustomDialog)
            .setContentView(R.layout.layout_ando_dialog_progressbar) { _, v ->
                val textView = v.findViewById<View>(ando.dialog.R.id.tipTextView) as TextView
                textView.text = getText(R.string.str_ando_dialog_loading)
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
        DialogManager.with(this, R.style.CustomDialog)
            .setContentView(R.layout.layout_ando_dialog_imageview) { _, v ->
                val tvLoadingTx: TextView = v.findViewById(ando.dialog.R.id.tv_loading_tx)
                tvLoadingTx.text = getText(R.string.str_ando_dialog_loading)

                val ivLoading: ImageView = v.findViewById(ando.dialog.R.id.iv_loading)
                val anim =
                    AnimationUtils.loadAnimation(this, ando.dialog.R.anim.ando_dialog_anim_loading)
                ivLoading.startAnimation(anim)

            }
            .setCancelable(true) //支持返回键关闭弹窗 true
            .setCanceledOnTouchOutside(true)
            .setOnShowListener {
                //对话框显示后再设置窗体才有效果
                val attributes = it.attributes
                attributes?.apply {
                    width = 800
                    height = 500
                    gravity = Gravity.CENTER //居中显示
                    dimAmount = 0.5f         //背景透明度  取值范围 0 ~ 1
                }
                it.attributes = attributes
            }
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

    private fun showBaseDialogPicker() {
        DialogManager.dismiss()
        val pickerDialog = PickerDialog()
        pickerDialog.show(this)
    }

}