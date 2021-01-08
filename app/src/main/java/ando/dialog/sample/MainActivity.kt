package ando.dialog.sample

import ando.dialog.core.DialogManager
import ando.dialog.usage.BottomDialog
import ando.dialog.usage.DateTimePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
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
        findViewById<Button>(R.id.bt_dialog_fragment_datetime).setOnClickListener(this)
        findViewById<Button>(R.id.bt_dialog_replace).setOnClickListener(this)
        findViewById<Button>(R.id.bt_dialog_bottom).setOnClickListener(this)
        findViewById<Button>(R.id.bt_dialog_configuration).setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        DialogManager.dismiss()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.bt_loading_by_progressbar -> showLoadingDialogByProgressBarShape()
            R.id.bt_loading_by_progressbar_bg_imageview -> showLoadingDialogByProgressBarImageView()
            R.id.bt_loading_by_imageview -> showLoadingDialogByImageView()
            R.id.bt_dialog_fragment -> showDialogFragmentSimpleUsage()
            R.id.bt_dialog_fragment_datetime -> showDateTimePickerDialog()
            R.id.bt_dialog_replace -> replaceDialog()
            R.id.bt_dialog_bottom -> showBottomDialog()
            R.id.bt_dialog_configuration -> showDialogConfiguration()
        }
    }

    /**
     * 动态改变显示中的Dialog宽高
     */
    private fun changeDialogSize() {
        findViewById<View>(R.id.bt_loading_by_progressbar).postDelayed({
            DialogManager.setWidth(500)
            DialogManager.setHeight(300)
            DialogManager.applySize()
            //移除背景变暗
            DialogManager.dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }, 2000)
    }

    private fun showLoadingDialogByProgressBarShape() {
        DialogManager.with(this, R.style.AndoLoadingDialog)
            .setContentView(R.layout.layout_ando_dialog_loading) { _, v ->
                v.findViewById<View>(R.id.progressbar_ando_dialog_loading).visibility =
                    View.VISIBLE

                v.findViewById<TextView>(R.id.tv_ando_dialog_loading_text).text =
                    getText(R.string.str_ando_dialog_loading_text)
            }
            .setCancelable(true) //支持返回键关闭弹窗 true
            .setCanceledOnTouchOutside(false)
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
        DialogManager.with(this, R.style.AndoLoadingDialog)
            .setContentView(R.layout.layout_ando_dialog_loading) { _, v ->
                v.findViewById<View>(R.id.progressbar_ando_dialog_loading).visibility =
                    View.VISIBLE
                v.findViewById<TextView>(R.id.tv_ando_dialog_loading_text).text =
                    getText(R.string.str_ando_dialog_loading_text)
            }
            .setDimmedBehind(false)
            .setSize(800, 600)
            .setCancelable(true) //支持返回键关闭弹窗 true
            .setCanceledOnTouchOutside(true)
            .apply {
                //setTitle("Title")
                //setOnCancelListener{}
                //...
            }
            .show()

        changeDialogSize()
    }

    private fun showLoadingDialogByImageView() {
        DialogManager.with(this, R.style.AndoLoadingDialog)
            .setContentView(R.layout.layout_ando_dialog_loading) { _, v ->
                v.findViewById<TextView>(R.id.tv_ando_dialog_loading_text).text =
                    getText(R.string.str_ando_dialog_loading_text)

                val imageView: ImageView = v.findViewById(R.id.iv_ando_dialog_loading)
                imageView.visibility = View.VISIBLE
                val anim = AnimationUtils.loadAnimation(
                    this,
                    R.anim.anim_ando_dialog_loading
                )
                imageView.startAnimation(anim)
            }
            .setCancelable(true) //支持返回键关闭弹窗 true
            .setCanceledOnTouchOutside(true)
            .setSize(800, 600)
            .setOnShowListener {
                findViewById<View>(R.id.bt_loading_by_imageview).postDelayed({
                    it.attributes?.apply {
                        width = 360
                        height = 360
                        gravity = Gravity.BOTTOM //居中显示
                        dimAmount = 0.6f         //背景透明度 取值范围 0 ~ 1
                        it.attributes = this
                    }
                }, 2000)
            }
            .setOnDismissListener {}
            .show()

        //changeDialogSize()
    }

    private fun showDialogFragmentSimpleUsage() {
        DialogManager.with(this, R.style.AndoLoadingDialog)
            .useDialogFragment()
            .setContentView(R.layout.layout_ando_dialog_loading) { _, v ->
                v.findViewById<View>(R.id.progressbar_ando_dialog_loading).visibility =
                    View.VISIBLE

                v.findViewById<TextView>(R.id.tv_ando_dialog_loading_text).text =
                    getText(R.string.str_ando_dialog_loading_text)
            }
//            .apply {
//                //`requestWindowFeature`必须在`show`之前调用
//                //Fixed: java.lang.RuntimeException: The feature has not been requested
//                dialog?.requestWindowFeature(Window.FEATURE_ACTION_BAR)
//            }
//            .setTitle("Hello World")
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

    private fun showDateTimePickerDialog() {
        DialogManager.dismiss()
        DateTimePickerDialog().show(this)
        changeDialogSize()
    }

    private fun replaceDialog() {
        val externalDialog = Dialog(this, android.R.style.Theme_Dialog)

        DialogManager.replaceDialog(externalDialog)
            .apply {
                //`requestWindowFeature`必须在`show`之前调用
                //Fixed: java.lang.RuntimeException: The feature has not been requested
                dialog?.requestWindowFeature(Window.FEATURE_LEFT_ICON)
            }
            .setSize(800, 600)
            .setTitle("Hello World")
            .setOnShowListener {
                DialogManager.dialog?.setFeatureDrawableResource(
                    Window.FEATURE_LEFT_ICON, android.R.drawable.star_on
                )
            }
            .create()
            .show()

        changeDialogSize()
    }

    private fun showBottomDialog() {
        val bottomDialog = CustomBottomDialog(this)
        DialogManager.replaceDialog(bottomDialog)
            .setCancelable(true)
            .setCanceledOnTouchOutside(true)
            .setDimmedBehind(true)
            .show()
    }

    private class CustomBottomDialog(context: Context) :
        BottomDialog(context, R.style.CustomBottomStyle) {
        override fun initView() {

        }

        override fun initBeforeShow(savedInstanceState: Bundle?) {
            super.initBeforeShow(savedInstanceState)
        }

        override fun initWindow(window: Window) {
            super.initWindow(window)
        }

        override fun getLayoutId(): Int = R.layout.layout_dialog_bottom
    }

    private fun showDialogConfiguration() {
        startActivity(Intent(this, DialogConfigurationSampleActivity::class.java))
    }

}