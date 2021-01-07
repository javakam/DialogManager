package ando.dialog.core

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.*
import androidx.annotation.StyleRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity

/**
 * # DialogManager
 *
 * ## 支持Dialog/DialogFragment
 * - Dialog: useDialog() ; DialogFragment: useDialogFragment()
 *
 * ## 开启/关闭背景变暗
 * - 开启/关闭背景变暗 Window.addFlags/clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
 *
 * ## Dialog使用注意
 *
 * ### 1. `Dialog.show`之前设置
 * - Dialog/Window.requestWindowFeature(Window.FEATURE_LEFT_ICON)
 *
 * ### 2. `Dialog.show`之后设置
 * - Window.mWindowAttributes(WindowManager.LayoutParams) 相关属性, 如动态改变Dialog的宽高、动画等
 *
 * - setFeatureDrawable/setFeatureDrawableResource/setFeatureDrawableUri/setFeatureDrawableAlpha
 *
 * - setFeatureXXX 方法必须在`Dialog.show`之前设置`requestWindowFeature`才能生效,
 * 否则出现BUG:java.lang.RuntimeException: The feature has not been requested
 *
 * @author javakam
 * @date 2021/1/5  10:00
 */
object DialogManager {

    private var mContext: Context? = null
    private var mThemeResId: Int = R.style.Theme_AppCompat_Dialog
    private var mWidth = -3
    private var mHeight = -3

    /**
     * Dialog(true); DialogFragment(false)
     */
    private var isDialogType: Boolean = true

    /**
     * 默认该窗口后面的所有内容都会变暗
     *
     * By default, everything behind the window will be dimmed.
     */
    private var isDimmedBehind: Boolean = true

    var contentView: View? = null
    var dialog: Dialog? = null
    var dialogFragment: FragmentDialog? = null

    private fun currentDialog(): Dialog? {
        synchronized(this) {
            if (dialog != null) return dialog

            return mContext?.run {
                dialog = Dialog(this, mThemeResId)
                dialog?.apply {
                    if (!isDialogType && dialogFragment == null) {
                        dialogFragment = FragmentDialog(this)
                    }
                }
            }
        }
    }

    /**
     * 使用外部创建好的Dialog
     *
     * Use externally created Dialog
     */
    fun replaceDialog(externalDialog: Dialog): DialogManager {
        synchronized(this) {
            reset()
            dismiss()
            mContext = externalDialog.context
            isDialogType = true
            dialog = externalDialog
            return this
        }
    }

    private fun reset() {
        mContext = null
        isDialogType = true
        mThemeResId = R.style.Theme_AppCompat_Dialog
        mWidth = -3
        mHeight = -3
        isDimmedBehind = true
        contentView = null
    }

    fun dismiss() {
        synchronized(this) {
            dialogFragment?.dismissAllowingStateLoss()
            dialogFragment = null

            dialog?.dismiss()
            dialog = null
        }
    }

    fun with(
        context: Context,
        @StyleRes themeResId: Int = R.style.Theme_AppCompat_Dialog
    ): DialogManager {
        reset()
        dismiss()
        mContext = context
        mThemeResId = themeResId
        return this
    }

    fun useDialog(): DialogManager {
        isDialogType = true
        currentDialog()
        return this
    }

    fun useDialogFragment(): DialogManager {
        isDialogType = false
        currentDialog()
        return this
    }

    fun setTitle(titleId: Int): DialogManager {
        currentDialog()?.setTitle(titleId)
        return this
    }

    fun setTitle(title: String): DialogManager {
        currentDialog()?.setTitle(title)
        return this
    }

    fun setContentView(
        layoutId: Int,
        block: ((Dialog?, View) -> Unit)? = null
    ): DialogManager {
        contentView = LayoutInflater.from(mContext).inflate(layoutId, null, false)
        contentView?.apply {
            if (isDialogType) {
                currentDialog()?.setContentView(this)
            } else {
                dialogFragment?.setContentView(this)
            }
            block?.invoke(currentDialog(), this)
        }
        return this
    }

    fun setContentView(
        view: View,
        params: ViewGroup.LayoutParams? = null,
        block: ((Dialog?, View) -> Unit)? = null
    ): DialogManager {
        contentView = view
        contentView?.apply {
            if (params == null) {
                if (isDialogType) currentDialog()?.setContentView(this)
                else dialogFragment?.setContentView(this)
            } else {
                if (isDialogType) currentDialog()?.setContentView(this, params)
                else dialogFragment?.setContentView(this)
            }
            block?.invoke(currentDialog(), this)
        }
        return this
    }

    fun addContentView(view: View, params: ViewGroup.LayoutParams?): DialogManager {
        currentDialog()?.addContentView(view, params)
        return this
    }

    fun addOnGlobalLayoutListener(onGlobalLayout: () -> Unit): DialogManager {
        val observer = contentView?.viewTreeObserver
        observer?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                Log.e("123", "onGlobalLayout ... ${contentView?.width} ${contentView?.height}")
                observer.removeOnGlobalLayoutListener(this)
                onGlobalLayout.invoke()
            }
        })
        return this
    }

    fun setSize(width: Int, height: Int): DialogManager {
        mWidth = width
        mHeight = height
        return this
    }

    fun setWidth(width: Int): DialogManager {
        mWidth = width
        return this
    }

    fun setHeight(height: Int): DialogManager {
        mHeight = height
        return this
    }

    fun setCancelable(cancelable: Boolean): DialogManager {
        if (isDialogType) {
            currentDialog()?.setCancelable(cancelable)
        } else {
            dialogFragment?.isCancelable = cancelable
        }
        return this
    }

    fun setCanceledOnTouchOutside(cancel: Boolean): DialogManager {
        currentDialog()?.setCanceledOnTouchOutside(cancel)
        return this
    }

    fun setDimmedBehind(dimmedBehind: Boolean): DialogManager {
        isDimmedBehind = dimmedBehind
        return this
    }

    fun setOnDismissListener(listener: DialogInterface.OnDismissListener): DialogManager {
        currentDialog()?.setOnDismissListener(listener)
        return this
    }

    fun setOnShowListener(listener: (Window) -> Unit): DialogManager {
        currentDialog()?.apply {
            setOnShowListener {
                this.window?.apply {
                    listener.invoke(this)
                }
            }
        }
        return this
    }

    fun applySize(window: Window? = null) {
        (window ?: dialog?.window ?: return).apply {
            if (mWidth != -3 && mHeight != -3) {
                setLayout(mWidth, mHeight)
            }
        }
    }

    fun setOnCancelListener(listener: DialogInterface.OnCancelListener): DialogManager {
        currentDialog()?.setOnCancelListener(listener)
        return this
    }

    fun setOnKeyListener(listener: DialogInterface.OnKeyListener): DialogManager {
        currentDialog()?.setOnKeyListener(listener)
        return this
    }

    fun create(): DialogManager {
        if (isDialogType) {
            currentDialog()?.create()
        } else {
            show()
        }
        return this
    }

    fun show(): DialogManager {
        if (isDialogType) {
            currentDialog()?.show()
        } else {
            (mContext as? FragmentActivity?)?.run {
                if (dialog?.isShowing == false) {
                    dialogFragment?.show(this)
                }
            }
        }

        //注: 对Dialog.Window的设置需要在显示后才有效果 ╮(╯▽╰)╭
        //Note: The setting of Dialog.Window needs to be effective after display .
        currentDialog()?.apply {
            window?.apply {
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

                if (isDimmedBehind) addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                else clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

                applySize(this)
            }
        }
        return this
    }

}