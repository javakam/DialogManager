package ando.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.*

/**
 * FragmentDialog
 *
 * @author javakam
 */
open class FragmentDialog : DialogFragment() {

    private val mDefaultTag: String by lazy { tag ?: javaClass.simpleName }
    private var contentView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            dismissAllowingStateLoss()
        }
        Log.e("123", "onCreate contentView = $contentView")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        Log.e("123", "onCreateView contentView = $contentView")
        return contentView
    }

    fun setContentView(v: View) {
        this.contentView = v
        Log.e("123", "setContentView = $v  $contentView")
    }

    /**
     * androidx.fragment.app.DialogFragment
     *
     * @Override
     * public void onStart() {
     *    super.onStart();
     *    if (mDialog != null) {
     *       mViewDestroyed = false;
     *       mDialog.show();
     *    }
     * }
     */
    override fun onStart() {
        super.onStart()
        Log.e("123", "contentView = $contentView")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e("123", "onDestroyView = $contentView")
    }

    override fun show(manager: FragmentManager, tag: String?) {
        removePreFragment(manager)
        super.show(manager, tag)
    }

    override fun showNow(manager: FragmentManager, tag: String?) {
        removePreFragment(manager)
        super.showNow(manager, tag)
    }

    fun show(fragment: Fragment) =
        fragment.activity?.supportFragmentManager?.apply { show(this, mDefaultTag) }

    fun show(activity: FragmentActivity) {
        val manager = activity.supportFragmentManager
        removePreFragment(manager)
        show(manager, mDefaultTag)
    }

    fun removePreFragment(manager: FragmentManager) {
        val transaction = manager.beginTransaction()
        transaction.remove(this)
        transaction.commitAllowingStateLoss()
    }

    override fun dismiss() {
        //解决Activity状态无法保存崩溃的异常
        super.dismissAllowingStateLoss()
    }

}