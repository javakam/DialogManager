package ando.widget.option.list

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList
import kotlin.math.abs

/**
 * ### 列表支持单选或者多选功能, 由`RecyclerView`实现
 *
 * @author javakam
 * @date 2021-05-13  9:38
 */
class OptionView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    companion object {
        val LAYOUT_TITLE: Int by lazy { R.layout.option_item_layout_header }
        val LAYOUT_ITEM_VERTICAL: Int by lazy { R.layout.option_item_layout_vertical }
        val LAYOUT_ITEM_HORIZONTAL: Int by lazy { R.layout.option_item_layout_horizontal }

        const val VIEW_TYPE_TITLE = 0
        const val VIEW_TYPE_ITEM = 1
    }

    data class OptConfig(
        var title: String?,
        var titleLayoutResource: Int = LAYOUT_TITLE,
        var itemLayoutResource: Int = LAYOUT_ITEM_HORIZONTAL,
        var columns: Int = 1,
        var setting: OptSetting = OptSetting(null, isCheckTriggerByItemView = false, true),
    )

    data class OptSetting(
        var isSingleChoice: Boolean? = null,            //null表示不显示CheckBox ; true单选 ; false多选
        var isCheckTriggerByItemView: Boolean = false,
        var isCheckAllowNothing: Boolean = true,
    )

    private lateinit var mConfig: OptConfig
    private lateinit var mAdapter: Adapter
    private var isShowCheckBox: Boolean = false
    private var onItemViewCallBack: OnItemViewCallBack? = null
    private var onItemClickListener: OnItemClickListener? = null

    override fun onDetachedFromWindow() {
        onItemViewCallBack = null
        onItemClickListener = null
        super.onDetachedFromWindow()
    }

    fun obtain(
        isItemVertical: Boolean,
        setting: OptSetting,
        data: List<OptionItem>? = null,
        onItemViewCallBack: OnItemViewCallBack?,
        onItemClickListener: OnItemClickListener?,
    ): OptionView {
        if (isItemVertical) {
            obtain(
                OptConfig(null, LAYOUT_TITLE, LAYOUT_ITEM_VERTICAL, 1, setting),
                data, onItemViewCallBack, onItemClickListener
            )
        } else {
            obtain(
                OptConfig(null, LAYOUT_TITLE, LAYOUT_ITEM_HORIZONTAL, 1, setting),
                data, onItemViewCallBack, onItemClickListener
            )
        }
        return this
    }

    fun obtain(
        config: OptConfig?, data: List<OptionItem>? = null,
        onItemViewCallBack: OnItemViewCallBack?, onItemClickListener: OnItemClickListener?
    ): OptionView {
        this.mConfig = config ?: OptConfig(
            null, LAYOUT_TITLE, LAYOUT_ITEM_HORIZONTAL, 1,
            OptSetting(null, isCheckTriggerByItemView = false, true)
        )
        this.onItemViewCallBack = onItemViewCallBack
        this.onItemClickListener = onItemClickListener
        this.isShowCheckBox = (mConfig.setting.isSingleChoice != null)

        val columns = mConfig.columns
        layoutManager = if (columns > 1) {
            GridLayoutManager(context, columns).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (mConfig.title != null && position == 0) columns else 1
                    }
                }
            }
        } else LinearLayoutManager(context)

        val items: List<OptionItem> = data ?: emptyList()
        if (mConfig.setting.isSingleChoice == true) {
            val index = items.indexOfFirst { it.isChecked }
            if (index != -1) {
                items.forEachIndexed { i, it -> if (index != i) it.isChecked = false }
            }
        }
        //items.forEach { Log.e("123", "${it.title} ${it.isChecked}") }
        if (!this::mAdapter.isInitialized) {
            this.mAdapter = Adapter(this.onItemClickListener)
        }
        mAdapter.applyConfig(data)
        adapter = mAdapter
        return this
    }

    fun getData(): List<OptionItem> {
        if (this::mAdapter.isInitialized) {
            return mAdapter.getItems()
        }
        return emptyList()
    }

    inner class Adapter(private val listener: OnItemClickListener?) : RecyclerView.Adapter<ViewHolder>() {

        private var title: String? = null
        private val items = ArrayList<OptionItem>()
        private var currentSelectedItem: OptionItem? = null
        private var preSelectIndex: Int = 0

        fun applyConfig(data: List<OptionItem>?) {
            this.title = mConfig.title
            this.items.clear()
            this.items.addAll(data ?: emptyList())
            if (mConfig.setting.isSingleChoice == true) {
                preSelectIndex = items.indexOfFirst { it.isChecked }
                currentSelectedItem = items[preSelectIndex]
            }
        }

        fun getItems(): List<OptionItem> = items

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            if (viewType == VIEW_TYPE_TITLE) {
                val header = LayoutInflater.from(parent.context).inflate(mConfig.titleLayoutResource, parent, false)
                onItemViewCallBack?.onHeaderCreated(header)
                return TitleViewHolder(header)
            }

            if (viewType == VIEW_TYPE_ITEM) {
                val view = LayoutInflater.from(parent.context).inflate(
                    mConfig.itemLayoutResource, parent, false
                )
                onItemViewCallBack?.onItemCreated(view)
                val holder = ItemViewHolder(view)
                val isHorizontal = (mConfig.itemLayoutResource == LAYOUT_ITEM_HORIZONTAL)
                if (isShowCheckBox && isHorizontal) {
                    holder.setIsHorizontal(isHorizontal)
                    holder.setCheckMode(isShowCheckBox)
                    //至少选择一项
                    holder.checkBox?.setOnCheckedChangeListener { buttonView, isChecked ->
                        if (!mConfig.setting.isCheckAllowNothing && !isChecked) {
                            if (mConfig.setting.isSingleChoice == true) {
                                if (currentSelectedItem == items[getRealPosition(holder)]) {
                                    buttonView.isChecked = true
                                }
                            } else {
                                //多选
                                if (items.filter { it.isChecked }.size > 1) {
                                    return@setOnCheckedChangeListener
                                }
                                items.find { it.isChecked }?.let {
                                    if (it == items[getRealPosition(holder)]) {
                                        buttonView.isChecked = true
                                    }
                                }
                            }
                        }
                    }
                    holder.checkBox?.setOnClickListener {
                        val isChecked = (it as CheckBox).isChecked
                        val position = getRealPosition(holder)
                        val itemSheet = items[position]
                        if (mConfig.setting.isSingleChoice == true) {
                            if (currentSelectedItem != itemSheet) {
                                currentSelectedItem?.isChecked = false
                                //取消之前的选项 notifyItemChanged(preSelectIndex,1)
                                view.post {
                                    notifyDataSetChanged()
                                    itemSheet.isChecked = isChecked
                                    preSelectIndex = position
                                    currentSelectedItem = itemSheet
                                }
                            }
                        } else {
                            itemSheet.isChecked = isChecked
                            view.post { notifyDataSetChanged() }
                        }
//                        Log.e(
//                            "123", "pos=$position preSelectIndex=$preSelectIndex " +
//                                    " ${currentSelectedItem?.title} ${currentSelectedItem?.isChecked} ;" +
//                                    " ${itemSheet.title} ${itemSheet.isChecked}"
//                        )
                    }
                }

                view.setOnClickListener(object : NoShakeListener(300) {
                    override fun onSingleClick(v: View) {
                        if (isShowCheckBox && isHorizontal && mConfig.setting.isCheckTriggerByItemView) {
                            holder.checkBox?.performClick()//not toggle
                        }
                        listener?.onItemSelected(items[getRealPosition(holder)])
                    }
                })
                return holder
            }
            throw IllegalStateException("Can't recognize this type")
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val correctPosition = if (title == null) position else position - 1
            if (holder is ItemViewHolder) {
                holder.bind(items[correctPosition])
            } else if (holder is TitleViewHolder) {
                holder.bind(title)
            }
        }

        override fun getItemCount(): Int {
            return if (title == null) items.size else items.size + 1
        }

        override fun getItemViewType(position: Int): Int {
            if (title != null) {
                if (position == 0) return VIEW_TYPE_TITLE
            }
            return VIEW_TYPE_ITEM
        }

        private fun getRealPosition(holder: ViewHolder): Int {
            return if (title != null) holder.adapterPosition - 1 else holder.adapterPosition
        }
    }

    internal class ItemViewHolder(itemView: View) : ViewHolder(itemView) {
        private var isHorizontal: Boolean = false
        private var isShowCheckBox: Boolean = false
        private var tvTitle: TextView? = null
        private var ivIcon: ImageView? = null
        internal var checkBox: CheckBox? = null

        fun setIsHorizontal(isHorizontal: Boolean) {
            this.isHorizontal = isHorizontal
        }

        fun setCheckMode(isShowCheckBox: Boolean) {
            this.isShowCheckBox = isShowCheckBox
        }

        fun bind(item: OptionItem) {
            if (item.title.isNullOrBlank()) {
                tvTitle?.visibility = View.GONE
            } else {
                tvTitle?.visibility = View.VISIBLE
                tvTitle?.text = item.title
            }

            if (item.icon == null) {
                ivIcon?.visibility = View.GONE
            } else {
                ivIcon?.visibility = View.VISIBLE
                ivIcon?.setImageDrawable(item.icon)
            }

            if (isShowCheckBox && isHorizontal) {
                if (checkBox?.visibility == View.INVISIBLE) checkBox?.visibility = View.VISIBLE
                checkBox?.isChecked = item.isChecked
            }
        }

        init {
            tvTitle = itemView.findViewById(R.id.id_ando_bottom_sheet_item_title)
            ivIcon = itemView.findViewById(R.id.id_ando_bottom_sheet_item_icon)
            checkBox = itemView.findViewById(R.id.id_ando_bottom_sheet_item_check_box)
            check(!(tvTitle == null && ivIcon == null)) {
                "At least define a TextView with id 'id_ando_bottom_sheet_item_title' or an ImageView with id 'id_ando_bottom_sheet_item_icon' in the item resource"
            }
        }
    }

    internal class TitleViewHolder(itemView: View) : ViewHolder(itemView) {
        private var text: TextView? = null
        fun bind(header: String?) {
            text?.text = header
        }

        init {
            text = itemView.findViewById(R.id.id_ando_bottom_sheet_item_head_title)
            checkNotNull(text) { "TextView in the Alternative header resource must have the id 'header'" }
        }
    }

    private abstract class NoShakeListener(private val duration: Long = 500) : OnClickListener {
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

    interface OnItemViewCallBack {
        fun onHeaderCreated(v: View) {}
        fun onItemCreated(v: View) {}
    }

    interface OnItemClickListener {
        fun onItemSelected(item: OptionItem) {}
    }

}