package ando.widget.option.list

import androidx.annotation.IntDef

/**
 * @author javakam
 * @date 2021-06-01  10:31
 */

//不显示选项框/单选/多选 注:单选和多选仅支持横向ItemView布局
const val MODE_CHECK_NONE = 1
const val MODE_CHECK_SINGLE = 2
const val MODE_CHECK_MULTI = 3

@Retention(AnnotationRetention.SOURCE)
@IntDef(value = [MODE_CHECK_NONE, MODE_CHECK_SINGLE, MODE_CHECK_MULTI])
annotation class CheckMode

data class OptConfig(
    var title: String? = null,
    var titleLayoutResource: Int = OptionView.LAYOUT_TITLE,
    var itemLayoutResource: Int = OptionView.LAYOUT_ITEM_HORIZONTAL,
    var columns: Int = 1,
    var setting: OptSetting = OptSetting(MODE_CHECK_NONE, isItemViewHorizontal = true, isCheckTriggerByItemView = false),
)

data class OptSetting(
    @CheckMode var checkMode: Int = MODE_CHECK_NONE, //不显示选项框/单选/多选
    var isItemViewHorizontal: Boolean = true,        //是否为横向显示的 ItemView
    var isCheckTriggerByItemView: Boolean = false,   //用于控制点击时由 Adapter.ItemView 还是 CheckBox 切换勾选状态, 默认为 CheckBox
)

fun OptSetting.isCheckShow(): Boolean = (checkMode != MODE_CHECK_NONE)
fun OptSetting.isCheckSingle(): Boolean = (checkMode == MODE_CHECK_SINGLE)
