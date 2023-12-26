package ando.dialog.bottomsheet

import ando.widget.option.list.*
import ando.widget.option.list.OptionView.Companion.LAYOUT_ITEM_HORIZONTAL
import ando.widget.option.list.OptionView.Companion.LAYOUT_ITEM_VERTICAL
import ando.widget.option.list.OptionView.Companion.LAYOUT_TITLE
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.annotation.MenuRes
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * Modified from https://github.com/invissvenska/ModalBottomSheetDialog
 */
class ModalBottomSheetDialogFragment : AbsBottomSheetDialogFragment() {

    companion object {
        private const val KEY_ITEMS = "items"               //data list
        private const val KEY_ITEM_LAYOUT = "item_layout"   //itemView layout resource id
        private const val KEY_TITLE = "title"               //title
        private const val KEY_TITLE_LAYOUT = "title_layout" //title layout
        private const val KEY_GRID_COLUMNS = "columns"      //grid columns
        private const val KEY_SHOW_CLOSE = "showClose"      //close button
        private const val KEY_SHOW_CHECK_BOX_MODE = "checkMode"       //不显示or单选or多选(Do not show or Single choice or multiple choice)
        private const val KEY_SHOW_CHECK_BOX_TRIGGER_ITEM = "trigger" //点击 Adapter.ItemView 触发 checkbox

        private var itemDecoration: RecyclerView.ItemDecoration? = null
        private var mOnItemViewCallBack: OptionView.OnItemViewCallBack? = null
        private var mOnItemClickListener: OptionView.OnItemClickListener? = null
        private var mOnSelectedCallBack: OnSelectedCallBack? = null

        private fun newInstance(builder: Builder): ModalBottomSheetDialogFragment {
            val args = Bundle()
            args.putParcelableArrayList(KEY_ITEMS, builder.items)
            args.putInt(KEY_ITEM_LAYOUT, builder.itemLayoutResource)
            args.putString(KEY_TITLE, builder.title)
            args.putInt(KEY_TITLE_LAYOUT, builder.titleLayoutResource)
            args.putInt(KEY_GRID_COLUMNS, builder.columns)
            args.putInt(KEY_SHOW_CHECK_BOX_MODE, builder.checkMode)
            args.putBoolean(KEY_FULL, builder.isFullScreen)
            args.putBoolean(KEY_ROUND, builder.isTopRounded)
            args.putBoolean(KEY_DRAGGABLE, builder.isDraggable)
            args.putBoolean(KEY_SHOW_CLOSE, builder.isShowClose)
            args.putBoolean(KEY_SHOW_CHECK_BOX_TRIGGER_ITEM, builder.isCheckTriggerByItemView)
            itemDecoration = builder.itemDecoration
            mOnItemViewCallBack = builder.onItemViewCallBack
            mOnItemClickListener = builder.onItemClickListener
            mOnSelectedCallBack = builder.selectedCallBack

            val fragment = ModalBottomSheetDialogFragment()
            fragment.arguments = args
            fragment.setCallBack(builder.callback)
            return fragment
        }
    }

    private lateinit var optionView: OptionView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        applyDialogConfig()
        return inflater.inflate(R.layout.bottom_sheet_fragment_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!this::optionView.isInitialized) {
            this.optionView = view.findViewById(R.id.id_ando_optional_recycler)
            itemDecoration?.apply { optionView.addItemDecoration(this) }

            this.optionView.obtain(
                OptConfig(
                    arguments?.getString(KEY_TITLE),
                    arguments?.getInt(KEY_TITLE_LAYOUT) ?: LAYOUT_TITLE,
                    arguments?.getInt(KEY_ITEM_LAYOUT) ?: LAYOUT_ITEM_HORIZONTAL,
                    columns = arguments?.getInt(KEY_GRID_COLUMNS) ?: 1,
                    OptSetting(
                        checkMode = arguments?.getInt(KEY_SHOW_CHECK_BOX_MODE, MODE_CHECK_NONE) ?: MODE_CHECK_NONE,
                        isCheckTriggerByItemView = arguments?.getBoolean(KEY_SHOW_CHECK_BOX_TRIGGER_ITEM, false) ?: false
                    )
                ),
                data = arguments?.getParcelableArrayList(KEY_ITEMS) ?: emptyList(),
                mOnItemViewCallBack,
                mOnItemClickListener
            )
        }

        //关闭
        val vClose: View = view.findViewById(R.id.id_ando_bottom_sheet_close)
        if (arguments?.getBoolean(KEY_SHOW_CLOSE, false) == true) vClose.visibility = View.VISIBLE
        else vClose.visibility = View.GONE

        val tvClose: TextView = view.findViewById(R.id.id_ando_bottom_sheet_close_tv)
        tvClose.setOnClickListener(object : NoShakeListener() {
            override fun onSingleClick(v: View) {
                dismissAllowingStateLoss()
            }
        })
    }

    override fun onDestroyView() {
        if (this::optionView.isInitialized) {
            mOnSelectedCallBack?.onSelected(optionView.getData())
        }
        super.onDestroyView()
        mOnSelectedCallBack = null
        itemDecoration = null
        mOnItemClickListener = null
    }

    class Builder {
        internal val items = ArrayList<OptionItem>()
        internal var title: String? = null
        internal var columns = 1
        internal var isShowClose: Boolean = true

        @CheckMode
        internal var checkMode: Int = MODE_CHECK_NONE
        internal var isCheckTriggerByItemView: Boolean = false
        internal var titleLayoutResource = LAYOUT_TITLE
        internal var itemLayoutResource = LAYOUT_ITEM_HORIZONTAL
        internal var onItemViewCallBack: OptionView.OnItemViewCallBack? = null
        internal var onItemClickListener: OptionView.OnItemClickListener? = null
        internal var selectedCallBack: OnSelectedCallBack? = null

        internal var isFullScreen: Boolean = false
        internal var isTopRounded: Boolean = false
        internal var isDraggable: Boolean = true
        internal var callback: OnDialogLifeCycleCallback? = null
        internal var itemDecoration: RecyclerView.ItemDecoration? = null

        fun setCallBack(callback: OnDialogLifeCycleCallback): Builder {
            this.callback = callback
            return this
        }

        fun setFullScreen(isFullScreen: Boolean): Builder {
            this.isFullScreen = isFullScreen
            return this
        }

        fun setTopRounded(isTopRounded: Boolean): Builder {
            this.isTopRounded = isTopRounded
            return this
        }

        fun setDraggable(isDraggable: Boolean): Builder {
            this.isDraggable = isDraggable
            return this
        }

        fun setTitle(title: String?): Builder {
            this.title = title
            return this
        }

        fun setTitleLayout(@LayoutRes titleLayoutResource: Int): Builder {
            this.titleLayoutResource = titleLayoutResource
            return this
        }

        fun addItem(items: List<OptionItem>): Builder {
            this.items.addAll(items)
            return this
        }

        @SuppressLint("RestrictedApi")
        fun addItem(context: Context, @MenuRes menuResource: Int): Builder {
            val menu = MenuBuilder(context)
            val inflater = MenuInflater(context)
            inflater.inflate(menuResource, menu)
            for (i in 0 until menu.size()) {
                val menuItem: MenuItem = menu.getItem(i)
                val item = OptionItem(
                    menuItem.itemId,
                    menuItem.title?.toString(),
                    menuItem.icon
                )
                this.items.add(item)
            }
            return this
        }

        fun setItemLayout(@LayoutRes itemLayoutResource: Int): Builder {
            this.itemLayoutResource = itemLayoutResource
            return this
        }

        fun setColumns(columns: Int): Builder {
            this.columns = columns
            return this
        }

        fun setShowClose(isShowClose: Boolean): Builder {
            this.isShowClose = isShowClose
            return this
        }

        /**
         * 仅支持横向布局
         */
        fun setCheckMode(@CheckMode mode: Int): Builder {
            this.checkMode = mode
            return this
        }

        fun setCheckTriggerByItemView(isCheckTriggerByItemView: Boolean): Builder {
            this.isCheckTriggerByItemView = isCheckTriggerByItemView
            return this
        }

        fun setItemDecoration(itemDecoration: RecyclerView.ItemDecoration): Builder {
            this.itemDecoration = itemDecoration
            return this
        }

        fun setItemViewDirection(isVertical: Boolean): Builder {
            this.itemLayoutResource = if (isVertical) LAYOUT_ITEM_VERTICAL else LAYOUT_ITEM_HORIZONTAL
            return this
        }

        fun setOnItemViewCallBack(onItemViewCallBack: OptionView.OnItemViewCallBack): Builder {
            this.onItemViewCallBack = onItemViewCallBack
            return this
        }

        fun setOnItemClickListener(onItemClickListener: OptionView.OnItemClickListener): Builder {
            this.onItemClickListener = onItemClickListener
            return this
        }

        fun setOnSelectedCallBack(selectedCallBack: OnSelectedCallBack): Builder {
            this.selectedCallBack = selectedCallBack
            return this
        }

        fun build(): ModalBottomSheetDialogFragment {
            return newInstance(this)
        }

        fun show(fragmentManager: FragmentManager, tag: String?) {
            val dialog = build()
            dialog.show(fragmentManager, tag)
        }
    }

    interface OnSelectedCallBack {
        fun onSelected(items: List<OptionItem>)
    }

}