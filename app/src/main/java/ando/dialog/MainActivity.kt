package ando.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.bt_loading_by_progressbar).setOnClickListener(this)
        findViewById<Button>(R.id.bt_loading_by_imageview).setOnClickListener(this)

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.bt_loading_by_progressbar -> showLoadingDialogByProgressBar()
            R.id.bt_loading_by_imageview -> showLoadingDialogByImageView()
        }
    }

    private var dialog: Dialog? = null

    private fun showLoadingDialogByProgressBar() {
        dialog?.dismiss()

        val loadBuilder: LoadingDialog.Builder = LoadingDialog.Builder(this)
                .setMessage(getString(R.string.str_ando_dialog_loading))
                .setCancelable(true) //支持返回键关闭弹窗 true
                .setCancelOutside(true)

        dialog = loadBuilder.create()
        dialog?.show()
    }

    private fun showLoadingDialogByImageView() {
        dialog?.dismiss()

        dialog = CustomDialog(this)
        dialog?.show()
    }

}