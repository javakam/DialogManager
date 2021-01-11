package ando.dialog.sample

import ando.dialog.core.DialogManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Title: EditTextActivity
 * <p>
 * Description:
 * </p>
 * @author javakam
 * @date 2021/1/11  16:32
 */
class EditTextActivity : AppCompatActivity() {

    private val mBtDialog: Button by lazy { findViewById(R.id.bt_dialog_show_dialog) }
    private val mBtDialogFragment: Button by lazy { findViewById(R.id.bt_dialog_show_dialog_fragment) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog_edittext)

        mBtDialog.setOnClickListener { showDialog() }
        mBtDialogFragment.setOnClickListener { showDialogFragment() }
    }

    private fun showDialog() {
        DialogManager.with(this, R.style.EditTextDialogStyle)
            .useDialog()
            .setContentView(R.layout.layout_dialog_edittext) { v: View ->
                val edtDialog: EditText = v.findViewById(R.id.edt_dialog)
                val btCancel: Button = v.findViewById(R.id.bt_dialog_cancel)
                val btConfirm: Button = v.findViewById(R.id.bt_dialog_confirm)

                btCancel.setOnClickListener { DialogManager.dismiss() }
                btConfirm.setOnClickListener {
                    val msg = edtDialog.text.toString()
                    Log.e("123", "msg=$msg")
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                }
            }
            .setTitle("Dialog EditText")
            .setCanceledOnTouchOutside(false)
            .setOnShowListener {
                val edtDialog: EditText? = DialogManager.contentView?.findViewById(R.id.edt_dialog)
                edtDialog?.isFocusable = true
                edtDialog?.requestFocus()
                edtDialog?.requestFocusFromTouch()
            }
            .show()
            .apply {
            }
    }

    private fun showDialogFragment() {
        DialogManager.with(this, R.style.EditTextDialogStyle)
            .useDialogFragment()
            .setContentView(R.layout.layout_dialog_edittext) { v: View ->
                val edtDialog: EditText = v.findViewById(R.id.edt_dialog)
                val btCancel: Button = v.findViewById(R.id.bt_dialog_cancel)
                val btConfirm: Button = v.findViewById(R.id.bt_dialog_confirm)

                btCancel.setOnClickListener { DialogManager.dismiss() }
                btConfirm.setOnClickListener {
                    val msg = edtDialog.text.toString()
                    Log.e("123", "msg=$msg")
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                }
            }
            .setTitle("DialogFragment EditText")
            .setCanceledOnTouchOutside(false)
            .setOnShowListener {
                val edtDialog: EditText? = DialogManager.contentView?.findViewById(R.id.edt_dialog)
                edtDialog?.isFocusable = true
                edtDialog?.requestFocus()
                edtDialog?.requestFocusFromTouch()
            }
            .show()
            .apply {
            }
    }

}