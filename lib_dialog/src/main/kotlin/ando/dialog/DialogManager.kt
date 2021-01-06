package ando.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.*
import androidx.annotation.StyleRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity

/**
 * Title: DialogManager
 *
 * @author javakam
 * @date 2021/1/5  10:00
 */
object DialogManager {

    private lateinit var mContext: Context
    private var mThemeResId: Int = R.style.Theme_AppCompat_Dialog
    private var isDimmedBehind: Boolean = false

    /**
     * Dialog(true); DialogFragment(false)
     */
    private var isDialogType: Boolean = true

    private var mWidth = -1
    private var mHeight = -1

    var dialog: Dialog? = null
    var contentView: View? = null

    var dialogFragment: FragmentDialog? = null

    private fun currentDialog(): Dialog? {
        synchronized(this) {
            if (dialog == null) {
                if (isDialogType) {
                    dialog = Dialog(mContext, mThemeResId)
                } else {
                    dialogFragment = FragmentDialog()
                    dialog = dialogFragment?.dialog
                }
            }
            return dialog
        }
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
        dismiss()
        this.mContext = context
        this.mThemeResId = themeResId
        this.useDialog()
        return this
    }

    /**
     * 默认使用Dialog实现
     */
    fun useDialog(): DialogManager {
        this.isDialogType = true
        return this
    }

    fun useDialogFragment(): DialogManager {
        this.isDialogType = false
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
        //getDialog().setContentView(layoutId)
        this.contentView = LayoutInflater.from(mContext).inflate(layoutId, null, false)
        this.contentView?.apply {
            if (isDialogType) {
                currentDialog()?.setContentView(this)
            } else {
                Log.e("123", "setContentView $dialogFragment $this")
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
        this.contentView = view
        this.contentView?.apply {
            if (params == null) {
                if (isDialogType) currentDialog()?.setContentView(this)
                else dialogFragment?.setContentView(this)
            } else {
                //DialogFragment 用的是 setContentView(View) 一种方式
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
                Log.e("123", "onGlobalLayout ... ${contentView?.width}  ${contentView?.height}")
                observer.removeOnGlobalLayoutListener(this)
                onGlobalLayout.invoke()
            }
        })
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

    fun setDimmedBehind(dimmedBehind: Boolean) {
        this.isDimmedBehind = dimmedBehind
    }

    fun setOnDismissListener(listener: DialogInterface.OnDismissListener): DialogManager {
        currentDialog()?.setOnDismissListener(listener)
        return this
    }

    fun setOnShowListener(listener: (Window) -> Unit): DialogManager {
        currentDialog()?.apply {
            //对话框显示后再设置窗体才有效果
            setOnShowListener {
                this.window?.apply {
                    configWindow(this)
                    applySize(this)

                    listener.invoke(this)
                }
            }
        }
        return this
    }

    private fun configWindow(window: Window) {
        window.apply {
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            if (isDimmedBehind) addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }
    }

    fun applyConfig(): DialogManager {
        currentDialog()?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return this
    }

    /**
     * 修改Dialog宽高
     */
    fun applySize(window: Window? = null) {
        (window ?: dialog?.window ?: return).apply {
            if (mWidth != -1 && mHeight != -1) {
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
            if (mContext is FragmentActivity) {
                (mContext as FragmentActivity).run {
                    dialogFragment?.show(this)
                }
            }
        }
        return this
    }

}