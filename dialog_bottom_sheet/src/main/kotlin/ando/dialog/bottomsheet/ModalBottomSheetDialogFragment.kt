package ando.dialog.bottomsheet

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.annotation.MenuRes
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*
import kotlin.math.abs

/**
 * Modified from https://github.com/invissvenska/ModalBottomSheetDialog
 */
class ModalBottomSheetDialogFragment : BottomSheetDialogFragment() {

    companion object {
        private const val KEY_ITEMS = "items"               //data list
        private const val KEY_ITEM_LAYOUT = "item_layout"   //itemView layout resource id
        private const val KEY_TITLE = "title"               //title
        private const val KEY_TITLE_LAYOUT = "title_layout" //title layout
        private const val KEY_GRID_COLUMNS = "columns"      //grid columns
        private const val KEY_ROUND = "round"               //topLeft,topRight corner
        private const val KEY_DRAGGABLE = "draggable"       //draggable
        private const val KEY_SHOW_CLOSE = "isShowClose"    //close button
        private var callback: OnDialogCreatedCallback? = null
        private var listener: OnItemClickListener? = null

        private val LAYOUT_TITLE: Int by lazy { R.layout.bottom_sheet_fragment_header }
        private val LAYOUT_ITEM_VERTICAL: Int by lazy { R.layout.bottom_sheet_fragment_item_vertical }
        private val LAYOUT_ITEM_HORIZONTAL: Int by lazy { R.layout.bottom_sheet_fragment_item_horizontal }

        private fun newInstance(builder: Builder): ModalBottomSheetDialogFragment {
            val args = Bundle()
            args.putParcelableArrayList(KEY_ITEMS, builder.items)
            args.putInt(KEY_ITEM_LAYOUT, builder.itemLayoutResource)
            args.putString(KEY_TITLE, builder.title)
            args.putInt(KEY_TITLE_LAYOUT, builder.titleLayoutResource)
            args.putInt(KEY_GRID_COLUMNS, builder.columns)
            args.putBoolean(KEY_ROUND, builder.isTopRounded)
            args.putBoolean(KEY_DRAGGABLE, builder.isDraggable)
            args.putBoolean(KEY_SHOW_CLOSE, builder.isShowClose)
            callback = builder.callback
            listener = builder.listener

            val fragment = ModalBottomSheetDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    interface OnDialogCreatedCallback {
        fun onDialogCreated(dialog: BottomSheetDialog)
    }

    interface OnItemClickListener {
        fun onItemSelected(item: ModalBottomSheetItem)
    }

    override fun getTheme(): Int {
        return if (arguments?.getBoolean(KEY_ROUND, false) == true) R.style.BottomSheetDialog else super.getTheme()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (dialog is BottomSheetDialog) {
            val dialog = dialog as BottomSheetDialog
            dialog.setCanceledOnTouchOutside(true)
            dialog.behavior.isDraggable = arguments?.getBoolean(KEY_DRAGGABLE, true) ?: true
            callback?.onDialogCreated(dialog)
        }
        return inflater.inflate(R.layout.bottom_sheet_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments?.getBoolean(KEY_ROUND, false) == false) view.setBackgroundColor(Color.TRANSPARENT)
        else view.setBackgroundResource(R.drawable.bg_bottom_sheet_dialog_fragment)

        val vClose: View = view.findViewById(R.id.id_ando_bottom_sheet_close)
        if (arguments?.getBoolean(KEY_SHOW_CLOSE, false) == true) vClose.visibility = View.VISIBLE
        else vClose.visibility = View.GONE

        val recycler: RecyclerView = view.findViewById(R.id.id_ando_bottom_sheet_recycler)
        val items: List<ModalBottomSheetItem> = arguments?.getParcelableArrayList(KEY_ITEMS) ?: emptyList()
        val adapter = Adapter(listener)
        adapter.setTitle(arguments?.getString(KEY_TITLE))
        adapter.setTitleLayoutResource(arguments?.getInt(KEY_TITLE_LAYOUT) ?: LAYOUT_TITLE)
        adapter.setItems(items)
        adapter.setItemLayoutRes(arguments?.getInt(KEY_ITEM_LAYOUT) ?: LAYOUT_ITEM_HORIZONTAL)
        recycler.adapter = adapter

        val columns = arguments?.getInt(KEY_GRID_COLUMNS) ?: 1
        val manager: RecyclerView.LayoutManager
        if (columns > 1) {
            manager = GridLayoutManager(context, columns)
            manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (adapter.title != null && position == 0) columns else 1
                }
            }
        } else {
            manager = LinearLayoutManager(context)
        }
        recycler.layoutManager = manager

        val tvClose: TextView = view.findViewById(R.id.id_ando_bottom_sheet_close_tv)
        tvClose.setOnClickListener { dismissAllowingStateLoss() }
    }

    class Builder {
        val items = ArrayList<ModalBottomSheetItem>()
        var title: String? = null
            private set
        var columns = 1
            private set
        var isTopRounded = false
            private set
        var isDraggable = true
            private set
        var isShowClose = true
            private set
        var callback: OnDialogCreatedCallback? = null
            private set
        var listener: OnItemClickListener? = null
            private set
        var titleLayoutResource = LAYOUT_TITLE
        var itemLayoutResource = LAYOUT_ITEM_HORIZONTAL

        fun setTitle(title: String?): Builder {
            this.title = title
            return this
        }

        fun setTitleLayout(@LayoutRes titleLayoutResource: Int): Builder {
            this.titleLayoutResource = titleLayoutResource
            return this
        }

        fun addItem(items: List<ModalBottomSheetItem>): Builder {
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
                val item = ModalBottomSheetItem(menuItem.itemId, menuItem.title?.toString(), menuItem.icon)
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

        fun setTopRounded(topRounded: Boolean): Builder {
            isTopRounded = topRounded
            return this
        }

        fun setDraggable(isDraggable: Boolean): Builder {
            this.isDraggable = isDraggable
            return this
        }

        fun setShowClose(isShowClose: Boolean): Builder {
            this.isShowClose = isShowClose
            return this
        }

        fun setItemDirection(isVertical: Boolean): Builder {
            this.itemLayoutResource = if (isVertical) LAYOUT_ITEM_VERTICAL else LAYOUT_ITEM_HORIZONTAL
            return this
        }

        fun setCallback(callback: OnDialogCreatedCallback): Builder {
            this.callback = callback
            return this
        }

        fun setListener(listener: OnItemClickListener): Builder {
            this.listener = listener
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

    internal class Adapter(private val listener: OnItemClickListener?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        companion object {
            private const val VIEW_TYPE_TITLE = 0
            private const val VIEW_TYPE_ITEM = 1
        }

        private var titleLayoutResource = LAYOUT_TITLE
        private var itemLayoutResource = LAYOUT_ITEM_HORIZONTAL
        private val items = ArrayList<ModalBottomSheetItem>()   //VIEW_TYPE_ITEM
        var title: String? = null                              //VIEW_TYPE_HEADER
            private set

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            if (viewType == VIEW_TYPE_TITLE) {
                return TitleViewHolder(LayoutInflater.from(parent.context).inflate(titleLayoutResource, parent, false))
            }
            if (viewType == VIEW_TYPE_ITEM) {
                val view = LayoutInflater.from(parent.context).inflate(itemLayoutResource, parent, false)
                val holder = ItemViewHolder(view)
                view.setOnClickListener(object : NoShakeListener() {
                    override fun onSingleClick(v: View) {
                        val position = if (!title.isNullOrBlank()) holder.adapterPosition - 1 else holder.adapterPosition
                        listener?.onItemSelected(items[position])
                    }
                })
                return holder
            }
            throw IllegalStateException("Can't recognize this type")
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val correctPosition = if (title == null) position else position - 1
            if (holder is ItemViewHolder) {
                val item = items[correctPosition]
                holder.bind(item)
            } else if (holder is TitleViewHolder) {
                holder.bind(title)
            }
        }

        override fun getItemCount(): Int {
            return if (title == null) items.size else items.size + 1
        }

        override fun getItemViewType(position: Int): Int {
            if (!title.isNullOrBlank()) {
                if (position == 0) return VIEW_TYPE_TITLE
            }
            return VIEW_TYPE_ITEM
        }

        fun setItems(items: List<ModalBottomSheetItem>) {
            this.items.clear()
            this.items.addAll(items)
            notifyDataSetChanged()
        }

        fun setTitle(title: String?) {
            this.title = title
        }

        fun setItemLayoutRes(@LayoutRes itemLayoutResource: Int) {
            this.itemLayoutResource = itemLayoutResource
        }

        fun setTitleLayoutResource(@LayoutRes titleLayoutResource: Int) {
            this.titleLayoutResource = titleLayoutResource
        }
    }

    internal class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var tvTitle: TextView? = null
        private var ivIcon: ImageView? = null

        fun bind(item: ModalBottomSheetItem) {
            tvTitle?.text = item.title
            ivIcon?.setImageDrawable(item.icon)
        }

        init {
            tvTitle = itemView.findViewById(R.id.id_ando_bottom_sheet_item_title)
            ivIcon = itemView.findViewById(R.id.id_ando_bottom_sheet_item_icon)
            check(!(tvTitle == null && ivIcon == null)) {
                "At least define a TextView with id 'id_ando_bottom_sheet_item_title' or an ImageView with id 'id_ando_bottom_sheet_item_icon' in the item resource"
            }
        }
    }

    internal class TitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var text: TextView? = null
        fun bind(header: String?) {
            text?.text = header
        }

        init {
            text = itemView.findViewById(R.id.id_ando_bottom_sheet_title)
            checkNotNull(text) { "TextView in the Alternative header resource must have the id 'header'" }
        }
    }

    private abstract class NoShakeListener : View.OnClickListener {
        private var lastClickTime: Long = 0

        private val isFastDoubleClick: Boolean
            get() {
                val nowTime = System.currentTimeMillis()
                return if (abs(nowTime - lastClickTime) < 500) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        callback = null
        listener = null
    }

}