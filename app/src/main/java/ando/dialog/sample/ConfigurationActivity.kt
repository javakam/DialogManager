package ando.dialog.sample

import ando.dialog.core.DialogManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment

/**
 * Title: ConfigurationActivity
 * <p>
 * Description:
 * </p>
 * @author javakam
 * @date 2021/1/8  14:43
 */
class ConfigurationActivity : AppCompatActivity() {

    companion object {
        const val TAG = "123"
    }

    private lateinit var mBtShowDialog: Button
    private lateinit var mBtShowDialogFragment: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog_configuration)
        title = "旋屏测试(Rotating Screen Test)"

        mBtShowDialog = findViewById(R.id.bt_dialog_show_dialog)
        mBtShowDialogFragment = findViewById(R.id.bt_dialog_show_dialog_fragment)
        mBtShowDialog.setOnClickListener { showDialog() }
        mBtShowDialogFragment.setOnClickListener { showDialogFragment() }

        Log.e(TAG, "Activity onCreate...")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "Activity onDestroy... ")
        DialogManager.dismiss()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Log.e(TAG, "屏幕方向: ${newConfig.orientation} isShowing= ${DialogManager.isShowing()}")
        super.onConfigurationChanged(newConfig)
    }

    private fun showDialog() {
        DialogManager.with(this, R.style.AndoLoadingDialog)
            .setContentView(R.layout.layout_ando_dialog_loading) { v ->
                v.findViewById<View>(R.id.progressbar_ando_dialog_loading).visibility =
                    View.VISIBLE

                v.findViewById<TextView>(R.id.tv_ando_dialog_loading_text).text =
                    getText(R.string.str_ando_dialog_loading_text)
            }
            .setCancelable(true)
            .setCanceledOnTouchOutside(true)
            .setOnCancelListener {
                Log.e(TAG, "Dialog Cancel... ")
            }
            .setOnDismissListener {
                Log.e(TAG, "Dialog Dismiss... ")
                Toast.makeText(this, "Dialog Dismiss", Toast.LENGTH_SHORT).show()
            }
            .show()

    }

    private fun showDialogFragment() {
        DialogManager.with(this, R.style.AndoLoadingDialog)
            .useDialogFragment()
            .setContentView(R.layout.layout_ando_dialog_loading) { v ->
                v.findViewById<View>(R.id.progressbar_ando_dialog_loading).visibility =
                    View.VISIBLE
                v.findViewById<TextView>(R.id.tv_ando_dialog_loading_text).text =
                    getText(R.string.str_ando_dialog_loading_text)
            }
            .setCancelable(true)
            .setCanceledOnTouchOutside(false)
            .setOnCancelListener {
                Log.e(TAG, "DialogFragment Cancel... ")
            }
            .setOnDismissListener {
                Log.e(TAG, "DialogFragment Dismiss... ")
                Toast.makeText(this, "DialogFragment Dismiss", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

}