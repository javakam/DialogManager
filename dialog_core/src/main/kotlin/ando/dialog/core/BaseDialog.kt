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

    private var mView: View? = null

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
        this.initDialog(savedInstanceState)
        if (getLayoutView() != null) {
            this.mView = getLayoutView()
        } else {
            this.mView = layoutInflater.inflate(getLayoutId(), null, false)
        }
        mView?.apply {
            setContentView(this)
            this@BaseDialog.window?.apply { initWindow(this) }
            initView(this)
        }
    }

    open fun initDialog(savedInstanceState: Bundle?) {}

    open fun initWindow(window: Window) {}

    open fun getLayoutView(): View? = null

    abstract fun initView(contentView: View)

    abstract fun getLayoutId(): Int
}