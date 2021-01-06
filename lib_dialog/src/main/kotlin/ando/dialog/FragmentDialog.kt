package ando.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.*
import java.lang.RuntimeException

/**
 * FragmentDialog
 *
 * @author javakam
 */
open class FragmentDialog : DialogFragment() {

    private val mDefaultTag: String by lazy { this.tag ?: javaClass.simpleName }
    private var contentView: View? = null
    private var customDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            dismissAllowingStateLoss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //Note: 改用自定义的Dialog (Use a custom Dialog instead)
        //return super.onCreateDialog(savedInstanceState)
        return this.customDialog ?: throw RuntimeException("Custom Dialog is null")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = contentView

    fun setCustomDialog(dialog: Dialog?) {
        this.customDialog = dialog
    }

    fun setContentView(v: View) {
        this.contentView = v
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

    /**
     * 解决Activity状态无法保存崩溃的异常
     */
    override fun dismiss() = super.dismissAllowingStateLoss()

}

