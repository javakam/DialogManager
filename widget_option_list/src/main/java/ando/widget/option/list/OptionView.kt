package ando.widget.option.list

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    private lateinit var mConfig: OptConfig
    private lateinit var mAdapter: CheckAdapter
    private var isCheckShow: Boolean = false
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
            OptSetting(MODE_CHECK_NONE, isItemViewHorizontal = true, isCheckTriggerByItemView = false)
        )
        this.onItemViewCallBack = onItemViewCallBack
        this.onItemClickListener = onItemClickListener
        this.isCheckShow = (mConfig.setting.isCheckShow())

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

        if (!this::mAdapter.isInitialized) {
            this.mAdapter = CheckAdapter()

            this.mAdapter.selectMode = if (mConfig.setting.isCheckSingle()) EasyAdapter.SelectMode.SINGLE_SELECT
            else EasyAdapter.SelectMode.MULTI_SELECT

            this.mAdapter.setOnItemSingleSelectListener { itemPosition, _ ->
                if (itemPosition >= 0) {
                    this.onItemClickListener?.onItemSelected(mAdapter.getItems()[itemPosition])
                }
            }
        }
        //没有数据时候, 会有 Adapter.Title
        if (data.isNullOrEmpty()) visibility = View.GONE else {
            visibility = View.VISIBLE
            setData(data)
        }
        adapter = mAdapter
        return this
    }

    fun setData(data: List<OptionItem>?): OptionView {
        if (data.isNullOrEmpty()) return this
        //data.forEach { Log.e("123", "${it.title} ${it.isChecked}") }
        if (mConfig.setting.isCheckSingle()) {
            val index = data.indexOfFirst { it.isChecked }
            if (index != -1) {
                data.forEachIndexed { i, it -> if (index != i) it.isChecked = false }
            }
        }
        mAdapter.applyConfig(data = data)
        visibility = if (mAdapter.getItems().isEmpty()) View.GONE else View.VISIBLE
        mAdapter.notifyDataSetChanged()
        return this
    }

    fun getData(): List<OptionItem> {
        if (this::mAdapter.isInitialized) {
            return mAdapter.getItems()
        }
        return emptyList()
    }

    private inner class CheckAdapter : EasyAdapter<ViewHolder>() {
        private var title: String? = null
        private val items = ArrayList<OptionItem>()
        fun applyConfig(data: List<OptionItem>?) {
            this.title = mConfig.title
            if (data.isNullOrEmpty()) return
            this.items.clear()
            this.items.addAll(data)
            //设置预选位置
            if (items.isNotEmpty()) {
                if (mConfig.setting.isCheckSingle()) {//单选
                    val preSelectIndex: Int = items.indexOfFirst { it.isChecked }
                    if (preSelectIndex >= 0) {
                        items[preSelectIndex].isChecked = true
                    }
                    setSelected(preSelectIndex)
                } else {//多选
                    var preSelectIndexList = intArrayOf()
                    items.forEachIndexed { i, item ->
                        if (item.isChecked) preSelectIndexList = preSelectIndexList.plus(i)
                    }
                    if (preSelectIndexList.isNotEmpty()) setSelected(*preSelectIndexList)
                }
            }
        }

        fun getItems(): List<OptionItem> = items

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

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            if (viewType == VIEW_TYPE_TITLE) {
                val header = LayoutInflater.from(parent.context).inflate(mConfig.titleLayoutResource, parent, false)
                onItemViewCallBack?.onHeaderCreated(header)
                return TitleViewHolder(header)
            }

            if (viewType == VIEW_TYPE_ITEM) {
                val view = LayoutInflater.from(parent.context).inflate(mConfig.itemLayoutResource, parent, false)
                onItemViewCallBack?.onItemCreated(view)
                val holder = ItemViewHolder(view)
                val isHorizontal = (mConfig.setting.isItemViewHorizontal)
                if (isCheckShow && isHorizontal) {
                    holder.setIsHorizontal(isHorizontal)
                    holder.setShowCheckBox(isCheckShow)
                }
                return holder
            }
            throw IllegalStateException("Can't recognize this type")
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            /////////////////////////// EasyAdapter.onBindViewHolder 是没有 Title, 需要调整下 position ///////////////////////////
            //super.onBindViewHolder(holder, position)
            val correctPosition = if (title == null) position else position - 1
            holder.itemView.tag = correctPosition
            holder.itemView.setOnClickListener(this)
            when (selectMode) {
                SelectMode.CLICK -> { //点击
                    holder.itemView.isSelected = false
                }

                SelectMode.SINGLE_SELECT -> { //单选
                    holder.itemView.isSelected = (singleSelected == correctPosition)
                }

                SelectMode.MULTI_SELECT -> { //多选
                    holder.itemView.isSelected = multiSelectedPosition.contains(correctPosition)
                }

                else -> {}
            }
            /////////////////////////// END ///////////////////////////

            if (holder is ItemViewHolder) {
                holder.bind(items[correctPosition])

                val isSelected = isSelected(correctPosition)
                holder.ivCheckBox?.isSelected = isSelected
                items[correctPosition].isChecked = isSelected  //修改 Bean 的 Check 状态
                //Log.w("123", "vh = $correctPosition -> $isSelected -> haveTitle=${(title == null)}")
            } else if (holder is TitleViewHolder) {
                holder.bind(title)
            }
        }

        override fun whenBindViewHolder(holder: ViewHolder?, position: Int) {
        }
    }

    internal class ItemViewHolder(itemView: View) : ViewHolder(itemView) {
        var isHorizontal: Boolean = false
        var isCheckShow: Boolean = false
        var tvTitle: TextView? = null
        var ivIcon: ImageView? = null
        var ivCheckBox: ImageView? = null

        fun setIsHorizontal(isHorizontal: Boolean) {
            this.isHorizontal = isHorizontal
        }

        fun setShowCheckBox(isCheckShow: Boolean) {
            this.isCheckShow = isCheckShow
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

            if (isCheckShow && isHorizontal) {
                if (ivCheckBox?.visibility == View.INVISIBLE) ivCheckBox?.visibility = View.VISIBLE
                ivCheckBox?.isSelected = item.isChecked
            } else ivCheckBox?.visibility = View.INVISIBLE
        }

        init {
            tvTitle = itemView.findViewById(R.id.id_ando_bottom_sheet_item_title)
            ivIcon = itemView.findViewById(R.id.id_ando_bottom_sheet_item_icon)
            ivCheckBox = itemView.findViewById(R.id.id_ando_bottom_sheet_item_check_box)
            check(!(tvTitle == null && ivIcon == null)) {
                "At least define a TextView with id 'id_ando_bottom_sheet_item_title' or an ImageView with id 'id_ando_bottom_sheet_item_icon' in the item resource"
            }
        }
    }

    internal class TitleViewHolder(itemView: View) : ViewHolder(itemView) {
        var text: TextView? = null
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