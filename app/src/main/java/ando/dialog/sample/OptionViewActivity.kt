package ando.dialog.sample

import ando.widget.option.list.*
import ando.widget.option.list.OptionView.Companion.LAYOUT_ITEM_HORIZONTAL
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat

class OptionViewActivity : AppCompatActivity() {
    companion object {
        fun open(activity: Activity, isMultiChoiceMode: Boolean) {
            val intent = Intent(activity, OptionViewActivity::class.java)
            intent.putExtra("isMultiChoiceMode", isMultiChoiceMode)
            activity.startActivity(intent)
        }
    }

    private var isMultiChoiceMode: Boolean = false
    private val mBtShowResult: Button by lazy { findViewById(R.id.bt_show_check) }
    private val mOptionView: OptionView by lazy { findViewById(R.id.optionView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_option_view)
        this.isMultiChoiceMode = intent.getBooleanExtra("isMultiChoiceMode", false)
        initView()
    }

    private fun initView() {
        mOptionView.obtain(
            OptConfig(
                itemLayoutResource = LAYOUT_ITEM_HORIZONTAL,
                setting = OptSetting(
                    checkMode = if (isMultiChoiceMode) MODE_CHECK_MULTI else MODE_CHECK_SINGLE,
                    isItemViewHorizontal = true, isCheckTriggerByItemView = true, isCheckAllowNothing = true
                )
            ),
            data = null, onItemViewCallBack = null,
            onItemClickListener = object : OptionView.OnItemClickListener {
                override fun onItemSelected(item: OptionItem) {
                    super.onItemSelected(item)
                    Toast.makeText(this@OptionViewActivity, "selectedPosition: $item", Toast.LENGTH_SHORT).show()
                }
            }
        )

        //模拟网络延迟加载数据
        mOptionView.postDelayed({
            val list = mutableListOf(
                OptionItem(
                    1, "QQ", ContextCompat.getDrawable(this, R.drawable.umeng_socialize_qq), false
                ),
                OptionItem(
                    2, "QQ空间", ContextCompat.getDrawable(this, R.drawable.umeng_socialize_qzone), false
                ),
                OptionItem(
                    3, "微信", ContextCompat.getDrawable(this, R.drawable.umeng_socialize_wechat), false
                ),
                OptionItem(
                    4, "朋友圈", ContextCompat.getDrawable(this, R.drawable.umeng_socialize_wxcircle), false
                ),
                OptionItem(5, "张三", null, true),
                OptionItem(6, "李四", null, false),
                OptionItem(7, "王五", null, false),
                OptionItem(8, "赵六", null, false),
            )
            setData(list)
        }, 1200)

        //展示结果
        mBtShowResult.setOnClickListener {
            val sb = StringBuilder()
            mOptionView.getData().forEach {
                if (it.isChecked) sb.append(it.title).append(",")
            }
            val str = sb.toString()
            val result = if (str.isBlank()) return@setOnClickListener else str.subSequence(0, str.length - 1)
            Toast.makeText(this, "选择结果: $result", Toast.LENGTH_SHORT).show()
            Log.w("123", "选择结果: $result")
        }
    }

    private fun setData(data: List<OptionItem>) {
        mOptionView.setData(data = data)
    }
}