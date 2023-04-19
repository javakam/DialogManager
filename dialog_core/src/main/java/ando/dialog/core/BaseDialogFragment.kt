package ando.dialog.core

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.*
import java.lang.RuntimeException

/**
 * # BaseDialogFragment
 *
 * - 如果要改变`Window`属性, 可以在`onStart`中处理。因为`DialogFragment.onStart`中执行了`Dialog.show()`
 *
 * @author javakam
 */
open class BaseDialogFragment : DialogFragment {

    private val mDefaultTag: String by lazy { this.tag ?: javaClass.simpleName }
    private var customDialog: Dialog? = null
    private var contentView: View? = null
    private var onDismissListener: DialogInterface.OnDismissListener? = null
    private var onCancelListener: DialogInterface.OnCancelListener? = null

    open fun initWindow(window: Window) {}

    constructor()

    constructor(dialog: Dialog) {
        this.customDialog = dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (this.customDialog == null) customDialog = Dialog(requireContext(), theme)
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            dismissAllowingStateLoss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //注: 改用自定义的Dialog (Note: Use a custom Dialog instead)
        //return super.onCreateDialog(savedInstanceState)
        return customDialog ?: throw RuntimeException("Custom Dialog is null")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = contentView

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply { initWindow(this) }
    }

    fun show(fragment: Fragment) = fragment.activity?.supportFragmentManager?.apply {
        removePreFragment(this)
        super.show(this, mDefaultTag)
    }

    fun show(activity: FragmentActivity) = activity.supportFragmentManager.apply {
        removePreFragment(this)
        super.show(this, mDefaultTag)
    }

    private fun removePreFragment(manager: FragmentManager) {
        val transaction = manager.beginTransaction()
        transaction.remove(this)
        transaction.commitAllowingStateLoss()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        onCancelListener?.onCancel(dialog)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.onDismiss(dialog)
    }

    /**
     * 解决Activity状态无法保存崩溃的异常
     */
    override fun dismiss() = super.dismissAllowingStateLoss()

    fun setContentView(v: View) {
        this.contentView = v
    }

    fun setOnDismissListener(listener: DialogInterface.OnDismissListener) {
        this.onDismissListener = listener
    }

    fun setOnCancelListener(listener: DialogInterface.OnCancelListener) {
        this.onCancelListener = listener
    }
}