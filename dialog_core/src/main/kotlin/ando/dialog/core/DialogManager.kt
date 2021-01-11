package ando.dialog.core

import android.app.Activity
import android.app.Dialog
import android.content.ComponentCallbacks
import android.content.Context
import android.content.DialogInterface
import android.content.res.Configuration
import android.os.Build
import android.view.*
import androidx.annotation.StyleRes
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
    private var mAnimResId: Int = 0
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
    var dialogFragment: BaseDialogFragment? = null

    private fun currentDialog(): Dialog? {
        synchronized(this) {
            if (dialog != null) return dialog

            return mContext?.run {
                dialog = Dialog(this, mThemeResId)
                createInternalDialog(dialog)

                dialog?.apply {
                    if (!isDialogType && dialogFragment == null) {
                        dialogFragment = BaseDialogFragment(this)
                    }
                }
            }
        }
    }

    /**
     * 使用外部创建好的 Dialog
     *
     * Use externally created Dialog
     */
    fun replaceDialog(externalDialog: Dialog): DialogManager {
        synchronized(this) {
            reset()
            dismiss()
            mContext = externalDialog.context
            isDialogType = true
            createInternalDialog(externalDialog)
            return this
        }
    }

    private fun createInternalDialog(externalDialog: Dialog?) {
        dialog = externalDialog
        mContext?.apply {
            if (this is Activity) dialog?.setOwnerActivity(this)
            registerComponentCallbacks(object : ComponentCallbacks {
                override fun onConfigurationChanged(newConfig: Configuration) {
                    reset()
                    dismiss()
                }

                override fun onLowMemory() {
                }
            })
        }
    }

    private fun isShowing() = currentDialog()?.isShowing ?: false

    private fun isContextIllegal(dialog: Dialog?): Boolean {
        dialog?.context?.apply {
            if (this is Activity) {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    isFinishing || isDestroyed
                } else {
                    // Api < 17. Unfortunately cannot check for isDestroyed()
                    isFinishing
                }
            }
        }
        return false
    }

    private fun reset() {
        mContext = null
        isDialogType = true
        mThemeResId = R.style.Theme_AppCompat_Dialog
        mAnimResId = 0
        mWidth = -3
        mHeight = -3
        isDimmedBehind = true
        contentView = null
    }

    fun dismiss() {
        synchronized(this) {
            if (!isContextIllegal(dialog)) {
                try {
                    dialogFragment?.dismissAllowingStateLoss()
                } catch (e: Exception) {
                }
                dialogFragment = null

                dialog?.dismiss()
                dialog = null
            }
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

    fun addOnGlobalLayoutListener(onGlobalLayout: (width:Int,height:Int) -> Unit): DialogManager {
        contentView?.viewTreeObserver?.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                contentView?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                onGlobalLayout.invoke(contentView?.width?:0, contentView?.height?:0)
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
            //不是 Dialog 的 setCancelable() 方法 (Not the setCancelable() method of Dialog)
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

    /**
     * 设置动画(Set animation)
     *
     * ```xml
     * <style name="DialogAnimation" parent="XXX">
     *      <item name="android:windowEnterAnimation">@anim/anim_xxx_in</item>
     *      <item name="android:windowExitAnimation">@anim/anim_xxx_out</item>
     * </style>
     * ```
     */
    fun setAnimationId(animResId: Int): DialogManager {
        if (animResId <= 0) return this
        this.mAnimResId = animResId
        return this
    }

    fun applySize(window: Window? = null) {
        if (isContextIllegal(dialog) || !isShowing()) return

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
        if (isContextIllegal(dialog) || isShowing()) return this
        if (isDialogType) {
            currentDialog()?.create()
        } else {
            show()
        }
        return this
    }

    fun show(): DialogManager {
        if (isContextIllegal(dialog) || isShowing()) return this

        if (isDialogType) {
            currentDialog()?.show()
        } else {
            (mContext as? FragmentActivity?)?.run {
                dialogFragment?.show(this)
            }
        }

        //注: 对Dialog.Window的设置需要在显示后才有效果 ╮(╯▽╰)╭
        //Note: The setting of Dialog.Window needs to be effective after display .
        currentDialog()?.apply {
            window?.apply {
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

                if (isDimmedBehind) addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                else clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

                if (mAnimResId > 0) setWindowAnimations(mAnimResId)

                applySize(this)
            }
        }
        return this
    }

}