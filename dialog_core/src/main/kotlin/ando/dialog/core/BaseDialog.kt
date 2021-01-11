package ando.dialog.core

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.Window

/**
 * Title: BaseDialog
 * <p>
 * Description:
 * </p>
 * @author javakam
 * @date 2021/1/11  11:00
 */
abstract class BaseDialog : Dialog {

    constructor(
        context: Context
    ) : this(context, 0)

    constructor(
        context: Context,
        themeResId: Int = android.R.style.Theme_Dialog
    ) : super(context, themeResId)

    constructor(
        context: Context, cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener
    ) : super(context, cancelable, cancelListener)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.initConfig(savedInstanceState)
        if (getLayoutView() != null) {
            this.setContentView(getLayoutView() ?: return)
        } else {
            this.setContentView(getLayoutId())
        }
        window?.apply { initWindow(this) }
        initView()
    }

    open fun initConfig(savedInstanceState: Bundle?) {}

    open fun initWindow(window: Window) {}

    open fun getLayoutView(): View? = null

    abstract fun initView()

    abstract fun getLayoutId(): Int
}