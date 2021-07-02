package ando.dialog.sample

import ando.dialog.bottomsheet.AbsBottomSheetDialogFragment
import ando.dialog.bottomsheet.ModalBottomSheetDialogFragment
import ando.widget.option.list.MODE_CHECK_MULTI
import ando.widget.option.list.MODE_CHECK_SINGLE
import ando.widget.option.list.OptionItem
import ando.widget.option.list.OptionView
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog

class ModalActivity : AppCompatActivity() {

    var dismissibleDialog: ModalBottomSheetDialogFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modal)

        findViewById<View>(R.id.buttonWithHeader).setOnClickListener {
            dismissibleDialog = ModalBottomSheetDialogFragment.Builder()
                .setTitle("Title of modal")
                .addItem(this, R.menu.options)
                .setShowClose(false)
                .setOnItemClickListener(onItemClickListener)
                .build()

            dismissibleDialog?.show(supportFragmentManager, "WithHeader")
        }

        findViewById<View>(R.id.buttonWithoutHeader).setOnClickListener {
            dismissibleDialog = ModalBottomSheetDialogFragment.Builder()
                .addItem(this, R.menu.options)
                .setShowClose(false)
                .setOnItemClickListener(onItemClickListener)
                .build()

            dismissibleDialog?.show(supportFragmentManager, "WithoutHeader")
        }

        findViewById<View>(R.id.buttonGrid).setOnClickListener {
            ModalBottomSheetDialogFragment.Builder()
                .setTitle("Grid bottom layout")
                .addItem(this, R.menu.options)
                .setColumns(3)
                .setOnItemClickListener(onItemClickListener)
                .show(supportFragmentManager, "GridLayout")
        }

        val list = mutableListOf(
            OptionItem(
                1,
                "QQ",
                ContextCompat.getDrawable(this, R.drawable.umeng_socialize_qq)
            ),
            OptionItem(
                2,
                "QQ空间",
                ContextCompat.getDrawable(this, R.drawable.umeng_socialize_qzone)
            ),
            OptionItem(
                3,
                "微信",
                ContextCompat.getDrawable(this, R.drawable.umeng_socialize_wechat)
            ),
            OptionItem(
                4,
                "朋友圈",
                ContextCompat.getDrawable(this, R.drawable.umeng_socialize_wxcircle)
            ),
        )
        findViewById<View>(R.id.buttonCustomLayout).setOnClickListener {
            ModalBottomSheetDialogFragment.Builder()
                .setTitle("Custom title and item layouts")
                .setTitleLayout(R.layout.layout_bottom_sheet_fragment_header)
                .addItem(list)
                .setItemLayout(R.layout.layout_bottom_sheet_fragment_item)
                .setColumns(3)
                .setOnItemClickListener(onItemClickListener)
                .show(supportFragmentManager, "CustomHeader")
        }

        findViewById<View>(R.id.buttonScrollableList).setOnClickListener {
            dismissibleDialog = ModalBottomSheetDialogFragment.Builder()
                .setTitle("Scrolling layout")
                .addItem(this, R.menu.options)
                .addItem(this, R.menu.options)
                .addItem(this, R.menu.options)
                .setItemViewDirection(false)
                .setDraggable(true)
                .setTopRounded(false)
                .setFullScreen(false)
                .setOnItemClickListener(onItemClickListener)
                .setCallBack(object : AbsBottomSheetDialogFragment.OnDialogLifeCycleCallback {
                    override fun onDialogCreated(dialog: BottomSheetDialog) {
                        dialog.behavior.setPeekHeight(350, true)
                    }
                })
                .build()

            dismissibleDialog?.show(supportFragmentManager, "ScrollLayout")
        }

        findViewById<View>(R.id.buttonRounded).setOnClickListener {
            val listCheckBox = mutableListOf(
                OptionItem(1, "QQ", null),
                OptionItem(2, "QQ空间", null, true),
                OptionItem(3, "微信", null),
                OptionItem(4, "朋友圈", null, true),
                OptionItem(5, "收藏", null, true),
                OptionItem(6, "钉钉", null, false),
            )
            val decoration = LinearItemDecoration.Builder()
                .color(ContextCompat.getColor(this, android.R.color.holo_blue_light))
                .dividerSize(dp2px(0.5F))
                .marginStart(dp2px(15F))
                .marginEnd(dp2px(15F))
                //.hideDividerForItemType(BaseQuickAdapter.HEADER_VIEW)
                //.hideAroundDividerForItemType(BaseQuickAdapter.FOOTER_VIEW)
                .build()

            ModalBottomSheetDialogFragment.Builder()
                .setTitle("Rounded layout")
                .setTopRounded(true)             //圆角, 仅支持左上角和右上角
                .setCheckMode(MODE_CHECK_MULTI)  //是否单选或多选       单选true;多选false
                .setCheckTriggerByItemView(true) //是否点击整个ItemView触发CheckBox事件
                .setCheckAllowNothing(false)     //是否允许选择结果为空  允许true;不允许false
                .setItemViewDirection(false)     //是否横向显示         竖向true;横向false
                .setItemDecoration(decoration)
                .setFullScreen(true)
                .addItem(listCheckBox)
                //.setOnItemClickListener(onItemClickListener)
                .setOnSelectedCallBack(object : ModalBottomSheetDialogFragment.OnSelectedCallBack {
                    override fun onSelected(items: List<OptionItem>) {
                        val sb = StringBuilder()
                        items.filter { it.isChecked }.forEach {
                            sb.append("${it.id}. ${it.title}").append("\n")
                        }
                        Toast.makeText(this@ModalActivity, "选择结果:\n$sb", Toast.LENGTH_LONG).show()
                    }
                })
                .setCallBack(object : AbsBottomSheetDialogFragment.OnDialogLifeCycleCallback {
                    override fun onDialogCreated(dialog: BottomSheetDialog) {
                    }
                })
                .show(supportFragmentManager, "RoundedLayout")
        }

        findViewById<View>(R.id.buttonShare).setOnClickListener {
            dismissibleDialog = ModalBottomSheetDialogFragment.Builder()
                //.setTitle("分享")
                //.setTitleLayout(R.layout.layout_bottom_sheet_fragment_header)
                .addItem(this, R.menu.options)
                .setItemViewDirection(true)
                .setColumns(3)
                .setDraggable(true)
                .setOnItemClickListener(object : OptionView.OnItemClickListener {
                    override fun onItemSelected(item: OptionItem) {
                        Toast.makeText(applicationContext, "Inner clicked on: " + item.title, Toast.LENGTH_SHORT).show()
                    }
                })
                .build()

            dismissibleDialog?.show(supportFragmentManager, "Share")
        }

        findViewById<View>(R.id.buttonFullPage).setOnClickListener {
            val dialogFull = AbsBottomSheetDialogFragment.obtain(
                R.layout.layout_bottom_sheet_custom, isFullScreen = true, isTopRounded = false,
                isDraggable = true, object : AbsBottomSheetDialogFragment.OnDialogLifeCycleCallback {
                    override fun onDialogCreated(dialog: BottomSheetDialog) {
                        dialog.setCanceledOnTouchOutside(false)

                        //350
                        dialog.behavior.setPeekHeight(resources.displayMetrics.heightPixels, true)
                    }
                })
            dialogFull.show(supportFragmentManager, "FullScreen")
        }

        //列表单选 or 多选
        findViewById<View>(R.id.buttonSelect).setOnClickListener {
            val listCheckBox = mutableListOf(
                OptionItem(0, "食物", null),
                OptionItem(1, "建筑", null, true),
                OptionItem(2, "节日", null),
                OptionItem(3, "商务", null),
                OptionItem(4, "医疗", null),
                OptionItem(5, "科技", null),
                OptionItem(6, "教育", null),
                OptionItem(7, "自然", null),
            )
            val decoration = LinearItemDecoration.Builder()
                .color(ContextCompat.getColor(this, android.R.color.holo_blue_light))
                .dividerSize(dp2px(0.5F))
                .marginStart(dp2px(15F))
                .marginEnd(dp2px(15F))
                //.hideDividerForItemType(BaseQuickAdapter.HEADER_VIEW)
                //.hideAroundDividerForItemType(BaseQuickAdapter.FOOTER_VIEW)
                .build()

            //方式一
            /*
            AbsBottomSheetDialogFragment
                .obtain(
                    R.layout.layout_bottom_sheet_classify,
                    isFullScreen = true,
                    isTopRounded = false,
                    isDraggable = true,
                    object : AbsBottomSheetDialogFragment.OnDialogLifeCycleCallback {
                        override fun onDialogCreated(dialog: BottomSheetDialog) {
                            dialog.setCanceledOnTouchOutside(false)
                        }

                        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                            val optionView: OptionView = view.findViewById(R.id.optionView)
                            optionView.addItemDecoration(decoration)
                            optionView.obtain(
                                false, OptionView.OptSetting(
                                    isCheckSingle = true,
                                    isCheckTriggerByItemView = true,
                                    isCheckAllowNothing = false
                                ), listCheckBox, null
                            )
                        }

                        override fun onDialogDestroy(view: View) {
                            val optionView: OptionView = view.findViewById(R.id.optionView)
                            val sb = StringBuilder()
                            optionView.getData().filter { it.isChecked }.forEach {
                                sb.append("${it.id}. ${it.title}").append("\n")
                            }
                            Toast.makeText(this@ModalActivity, "选择结果:\n$sb", Toast.LENGTH_LONG).show()
                        }
                    })
                .show(supportFragmentManager, "classify")
                */

            //方式二
            dismissibleDialog = ModalBottomSheetDialogFragment.Builder()
                .setTitle("")
                .setTitleLayout(R.layout.layout_bottom_sheet_custom_head)
                .setTopRounded(false)            //圆角, 仅支持左上角和右上角]
                .setCheckMode(MODE_CHECK_SINGLE) //是否单选或多选       单选true;多选false
                .setCheckTriggerByItemView(true) //是否点击整个ItemView触发CheckBox事件
                .setCheckAllowNothing(false)     //是否允许选择结果为空  允许true;不允许false
                .setItemViewDirection(false)     //是否横向显示         竖向true;横向false
                .setItemDecoration(decoration)
                .setFullScreen(true)
                .addItem(listCheckBox)
                .setOnItemViewCallBack(object : OptionView.OnItemViewCallBack {
                    override fun onHeaderCreated(v: View) {
                        val ivBack = v.findViewById<ImageView>(R.id.iv_back)
                        ivBack.setOnClickListener {
                            dismissibleDialog?.dismissAllowingStateLoss()
                        }
                    }
                })
                .setOnItemClickListener(object : OptionView.OnItemClickListener {
                    override fun onItemSelected(item: OptionItem) {
                        Toast.makeText(applicationContext, "onItemSelected: " + item.title, Toast.LENGTH_SHORT).show()

                        //选择完后直接关闭页面
                        dismissibleDialog?.dismissAllowingStateLoss()
                    }
                })
                .setOnSelectedCallBack(object : ModalBottomSheetDialogFragment.OnSelectedCallBack {
                    override fun onSelected(items: List<OptionItem>) {
                        val sb = StringBuilder()
                        items.filter { it.isChecked }.forEach {
                            sb.append("${it.id}. ${it.title}").append("\n")
                        }
                        Toast.makeText(this@ModalActivity, "选择结果:\n$sb", Toast.LENGTH_LONG).show()
                    }
                })
                .setCallBack(object : AbsBottomSheetDialogFragment.OnDialogLifeCycleCallback {
                    override fun onDialogCreated(dialog: BottomSheetDialog) {
                    }
                })
                .build()

            dismissibleDialog?.show(supportFragmentManager, "classify")

        }
    }

    private val onItemClickListener = object : OptionView.OnItemClickListener {
        override fun onItemSelected(item: OptionItem) {
            Toast.makeText(applicationContext, "clicked on: " + item.title, Toast.LENGTH_SHORT).show()
            //dismissibleDialog.dismiss();
        }
    }

}