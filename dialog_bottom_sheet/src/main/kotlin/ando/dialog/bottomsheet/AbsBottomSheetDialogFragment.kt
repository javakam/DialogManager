package ando.dialog.bottomsheet

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.math.abs

/**
 * Modified from https://github.com/invissvenska/ModalBottomSheetDialog
 */
open class AbsBottomSheetDialogFragment : BottomSheetDialogFragment() {

    companion object {
        internal const val KEY_LAYOUT_ID = "layout"
        internal const val KEY_FULL = "full"
        internal const val KEY_ROUND = "round"      //Only Support topLeft & topRight corner
        internal const val KEY_DRAGGABLE = "drag"

        fun obtain(
            @LayoutRes mLayoutId: Int = -1, isFullScreen: Boolean = false,
            isTopRounded: Boolean = false, isDraggable: Boolean = true,
            callback: OnDialogLifeCycleCallback? = null
        ): AbsBottomSheetDialogFragment {
            val args = Bundle()
            args.putInt(KEY_LAYOUT_ID, mLayoutId)
            args.putBoolean(KEY_FULL, isFullScreen)
            args.putBoolean(KEY_ROUND, isTopRounded)
            args.putBoolean(KEY_DRAGGABLE, isDraggable)

            val fragment = AbsBottomSheetDialogFragment()
            fragment.arguments = args
            fragment.setCallBack(callback)
            return fragment
        }
    }

    private lateinit var mContentView: View
    private var mLifeCycleCallback: OnDialogLifeCycleCallback? = null

    override fun getTheme(): Int {
        return if (arguments?.getBoolean(KEY_ROUND, false) == true) R.style.AndoBottomSheetDialog else super.getTheme()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        applyDialogConfig()

        val layoutId = arguments?.getInt(KEY_LAYOUT_ID, -1) ?: -1
        mContentView = if (layoutId != -1) inflater.inflate(layoutId, container, false)
        else FrameLayout(requireContext())
        return mContentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyDialogCorner(view)
        mLifeCycleCallback?.onViewCreated(view, savedInstanceState)
    }

    /**
     * 设置默认全屏显示
     */
    override fun onStart() {
        super.onStart()
        val isFullScreen = arguments?.getBoolean(KEY_FULL, false) ?: false
        if (isFullScreen) {
            //拿到系统的 bottom_sheet
            val view: FrameLayout = dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
            //设置view高度
            view.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            //获取behavior
            val behavior = BottomSheetBehavior.from(view)
            //设置弹出高度
            behavior.peekHeight = 3000
            //设置展开状态
            behavior.state = BottomSheetBehavior.STATE_EXPANDED

            behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_EXPANDED -> {
                        }
                        BottomSheetBehavior.STATE_COLLAPSED -> {
                        }
                        BottomSheetBehavior.STATE_DRAGGING -> {
                        }
                        BottomSheetBehavior.STATE_SETTLING -> {
                        }
                        BottomSheetBehavior.STATE_HIDDEN -> {
                        }
                        BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                            TODO()
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }
            })
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        dialog.dismiss()
        super.onCancel(dialog)
    }

    override fun dismiss() {
        hideKeyBoard()
        super.dismiss()
    }

    override fun onDestroyView() {
        if (this::mContentView.isInitialized) {
            mLifeCycleCallback?.onDialogDestroy(mContentView)
        }
        super.onDestroyView()
        mLifeCycleCallback = null
    }

    open fun applyDialogConfig(): AbsBottomSheetDialogFragment {
        if (dialog is BottomSheetDialog) {
            val dialog = dialog as BottomSheetDialog
            dialog.setCanceledOnTouchOutside(true)
            dialog.behavior.isDraggable = arguments?.getBoolean(KEY_DRAGGABLE, true) ?: true
            mLifeCycleCallback?.onDialogCreated(dialog)
        }
        return this
    }

    open fun applyDialogCorner(view: View) {
        if (arguments?.getBoolean(KEY_ROUND, false) == false) view.setBackgroundColor(Color.TRANSPARENT)
        else view.setBackgroundResource(R.drawable.bg_bottom_sheet_dialog_fragment)
    }

    open fun setCallBack(callback: OnDialogLifeCycleCallback?) {
        this.mLifeCycleCallback = callback
    }

    open fun hideKeyBoard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = dialog?.window?.currentFocus
        view?.let {
            imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    abstract class NoShakeListener(private val duration: Long = 500) : View.OnClickListener {
        private var lastClickTime: Long = 0

        private val isFastDoubleClick: Boolean
            get() {
                val nowTime = System.currentTimeMillis()
                return if (abs(nowTime - lastClickTime) < duration) {
                    true // 快速点击事件
                } else {
                    lastClickTime = nowTime
                    false // 单次点击事件
                }
            }

        override fun onClick(v: View) {
            if (!isFastDoubleClick) onSingleClick(v)
        }

        protected abstract fun onSingleClick(v: View)
    }

    interface OnDialogLifeCycleCallback {
        fun onDialogCreated(dialog: BottomSheetDialog) {}
        fun onViewCreated(view: View, savedInstanceState: Bundle?) {}
        fun onDialogDestroy(view: View) {}
    }

}