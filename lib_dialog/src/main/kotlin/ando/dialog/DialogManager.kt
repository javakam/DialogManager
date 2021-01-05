package ando.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.*
import androidx.annotation.StyleRes

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

    private var mWidth = -1
    private var mHeight = -1

    var dialog: Dialog? = null
    var contentView: View? = null

    private fun currentDialog(): Dialog? {
        synchronized(this) {
            if (dialog == null) {
                dialog = Dialog(mContext, mThemeResId)
            }
            return dialog
        }
    }

    fun getWindow(): Window? = currentDialog()?.window

    fun dismiss() {
        synchronized(this) {
            if (dialog?.isShowing == true) dialog?.dismiss()
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
            currentDialog()?.setContentView(this)
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
            if (params == null) currentDialog()?.setContentView(this)
            else currentDialog()?.setContentView(this, params)

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

    fun setWidth(width: Int) {
        mWidth = width
    }

    fun setHeight(height: Int) {
        mHeight = height
    }

    fun setCancelable(cancelable: Boolean): DialogManager {
        currentDialog()?.setCancelable(cancelable)
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
            setOnShowListener {

                //对话框显示后再设置窗体才有效果
                this.window?.apply {
                    clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                    if (isDimmedBehind) addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

                    applySize(this)

                    listener.invoke(this)
                }
            }
        }
        return this
    }

    fun applySize(window: Window? = null) {
        val win: Window = window ?: dialog?.window ?: return
        if (mWidth != -1 && mHeight != -1) {
            val attr = win.attributes
            attr.width = mWidth
            attr.height = mHeight
            win.attributes = attr
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
        currentDialog()?.create()
        return this
    }

    fun show(): DialogManager {
        currentDialog()?.show()
        return this
    }

}