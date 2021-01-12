package ando.dialog.sample

import ando.dialog.core.DialogManager
import ando.dialog.usage.BottomDialog
import ando.dialog.usage.DateTimePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.bt_loading_progressbar_imageview).setOnClickListener(this)
        findViewById<Button>(R.id.bt_loading_imageview).setOnClickListener(this)
        findViewById<Button>(R.id.bt_dialog_fragment_usage).setOnClickListener(this)
        findViewById<Button>(R.id.bt_dialog_fragment_datetime).setOnClickListener(this)
        findViewById<Button>(R.id.bt_dialog_replace).setOnClickListener(this)
        findViewById<Button>(R.id.bt_dialog_bottom).setOnClickListener(this)
        findViewById<Button>(R.id.bt_dialog_progress_param).setOnClickListener(this)
        findViewById<Button>(R.id.bt_dialog_configuration).setOnClickListener(this)
        findViewById<Button>(R.id.bt_dialog_edit_text).setOnClickListener(this)

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.e(
            ConfigurationActivity.TAG,
            "MainActivity Configuration= $newConfig isShowing= ${DialogManager.isShowing()}"
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        DialogManager.dismiss()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.bt_loading_progressbar_imageview -> showLoadingDialogByProgressBarImageView()
            R.id.bt_loading_imageview -> showLoadingDialogByImageView()
            R.id.bt_dialog_fragment_usage -> showDialogFragmentSimpleUsage()
            R.id.bt_dialog_fragment_datetime -> showDateTimePickerDialog()
            R.id.bt_dialog_replace -> replaceDialog()
            R.id.bt_dialog_bottom -> showBottomDialog()
            R.id.bt_dialog_progress_param -> showProgressDialog()
            R.id.bt_dialog_configuration -> showDialogConfiguration()
            R.id.bt_dialog_edit_text -> showDialogEditText()
        }
    }

    /**
     * 动态改变显示中的Dialog位置/宽高/动画等
     */
    private fun changeDialogSize() {
        findViewById<View>(R.id.bt_loading_progressbar_imageview).postDelayed({
            //改变弹窗宽高(Change the width and height of the dialog)
            DialogManager.setWidth(280)
            DialogManager.setHeight(280)
            DialogManager.applySize()

            //控制背景亮度(Control background brightness)
            DialogManager.setDimAmount(0.3F)
            DialogManager.applyDimAmount()
            //or 直接移除背景变暗(Directly remove the background darkening)
            //DialogManager.dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        }, 2000)
    }

    private fun showLoadingDialogByProgressBarImageView() {
        DialogManager.with(this, R.style.AndoLoadingDialog)
            .setContentView(R.layout.layout_ando_dialog_loading) { v ->
                val progressBar:ProgressBar=v.findViewById(R.id.progressbar_ando_dialog_loading)
                progressBar .visibility =View.VISIBLE

                v.findViewById<TextView>(R.id.tv_ando_dialog_loading_text).text = getText(R.string.str_ando_dialog_loading_text)
            }
            .setAnimationId(R.style.AndoBottomDialogAnimation)
            .setCancelable(true)
            .setCanceledOnTouchOutside(true)
            .setDimAmount(0.7F)
            .setOnDismissListener {
            }
            .apply {
                //setTitle("Title")
                //setOnCancelListener{}
                //setOnDismissListener{}
                //...
            }
            //.addOnGlobalLayoutListener {width, height -> }
            .show()

        changeDialogSize()
    }

    private fun showLoadingDialogByImageView() {
        DialogManager.with(this, R.style.AndoLoadingDialog)
            .setContentView(R.layout.layout_ando_dialog_loading) { v ->
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
            .setDimmedBehind(false)
            .setCancelable(true)
            .setCanceledOnTouchOutside(false)
            .setSize(390, 280)
            .setOnShowListener {
                findViewById<View>(R.id.bt_loading_imageview).postDelayed({
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
            .setContentView(R.layout.layout_ando_dialog_loading) { v ->
                v.findViewById<View>(R.id.progressbar_ando_dialog_loading).visibility =
                    View.VISIBLE

                v.findViewById<TextView>(R.id.tv_ando_dialog_loading_text).text =
                    getText(R.string.str_ando_dialog_loading_text)
            }
            .setCancelable(true)
            .setCanceledOnTouchOutside(true)
            .setOnDismissListener {
                Log.e("123", "Dismiss... ")
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
            .setSize(450, 260)
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

        override fun initConfig(savedInstanceState: Bundle?) {
            super.initConfig(savedInstanceState)
        }

        override fun initWindow(window: Window) {
            super.initWindow(window)
        }

        override fun getLayoutId(): Int = R.layout.layout_dialog_bottom
    }

    private fun showProgressDialog() {
        val progressDialog = ProgressDialog(this, R.style.AndoLoadingDialogLight)
        progressDialog.setTitle("ProgressDialog")
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(true)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)

        progressDialog.setProgressNumberFormat("%1d/%2d") //系统默认为 "%1d/%2d" , null 则不显示
        //progressDialog.setProgressPercentFormat(null) //null 则不显示

        //val colorDrawable = ColorDrawable(Color.BLUE)
        //progressDialog.setProgressDrawable(colorDrawable)
        //progressDialog.setIndeterminateDrawable(colorDrawable)

        progressDialog.isIndeterminate = false //true 显示无限动画,false 显示进度
        progressDialog.max = 100
        progressDialog.secondaryProgress = 25
        progressDialog.show()

        //设置进度必须在`show`之后才会生效, `secondaryProgress`不需要
        progressDialog.progress = 20

        val handle: Handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                progressDialog.incrementProgressBy(2) // Incremented By Value 2
                progressDialog.incrementSecondaryProgressBy(5)
            }
        }

        Thread {
            try {
                while (progressDialog.progress <= progressDialog.max) {
                    Thread.sleep(300)
                    handle.sendMessage(handle.obtainMessage())
                    if (progressDialog.progress == progressDialog.max) {
                        progressDialog.dismiss()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun showDialogConfiguration() {
        startActivity(Intent(this, ConfigurationActivity::class.java))
    }

    private fun showDialogEditText() {
        startActivity(Intent(this, EditTextActivity::class.java))
    }

}