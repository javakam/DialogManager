package ando.dialog.usage

import ando.dialog.core.BaseDialogFragment
import android.content.ComponentCallbacks
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.TimePicker
import java.text.SimpleDateFormat
import java.util.*

/**
 * # 日期/时间选择弹窗
 *
 * Date and time selection dialog.
 *
 * - 1. 通过`setType`进行切换
 *
 * - 2. 支持Phone和Tablet
 *
 * @author javakam
 */
class DateTimePickerDialog : BaseDialogFragment() {

    companion object {
        const val Y_M_D_H_M = "yyyy-MM-dd HH:mm"
        const val Y_M_D = "yyyy-MM-dd"
        const val H_M = "HH:mm"
    }

    private lateinit var mLlDate: LinearLayout
    private lateinit var mTpDate: DatePicker
    private lateinit var mTpTime: TimePicker
    private lateinit var mBtnCancel: Button
    private lateinit var mBtnCertain: Button
    private var mType = Y_M_D
    private var mDate: Date? = Date()
    private var mCallBack: CallBack? = null

    interface CallBack {
        fun onClick(originalTime: Date, dateTime: String)
        fun onDateChanged(v: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {}
        fun onTimeChanged(view: TimePicker?, hourOfDay: Int, minute: Int) {}
    }

    fun setCallBack(callBack: CallBack) {
        this.mCallBack = callBack
    }

    override fun initWindow(window: Window) {
        window.attributes?.apply {
            width = ViewGroup.LayoutParams.WRAP_CONTENT
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            window.attributes = this
        }
        window.setBackgroundDrawableResource(android.R.color.white)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.layout_ando_dialog_datetime, container, false)
        mLlDate = view.findViewById(R.id.ll_ando_date_time)
        mTpDate = view.findViewById(R.id.dp_ando_date)
        mTpTime = view.findViewById(R.id.tp_ando_time)
        if (Y_M_D.equals(mType, true)) {
            mLlDate.removeView(mTpTime)
        }
        if (H_M.equals(mType, true)) {
            mLlDate.removeView(mTpDate)
        }
        context?.apply { initConfiguration(this) }

        mTpDate.init(
            Utils.getYear(getDate()),
            Utils.getMonth(getDate()),
            Utils.getDay(getDate())
        ) { v, year, monthOfYear, dayOfMonth ->
            mCallBack?.onDateChanged(v, year, monthOfYear, dayOfMonth)
        }

        mTpTime.setIs24HourView(true)
        mTpTime.currentHour = Utils.getHour(getDate())
        mTpTime.currentMinute = Utils.getMinute(getDate())
        mTpTime.setOnTimeChangedListener { v, hourOfDay, minute ->
            mCallBack?.onTimeChanged(v, hourOfDay, minute)
        }

        mBtnCancel = view.findViewById(R.id.bt_ando_cancel)
        mBtnCertain = view.findViewById(R.id.bt_ando_certain)
        mBtnCancel.setOnClickListener { dismiss() }

        mBtnCertain.setOnClickListener {
            val calendar = Calendar.getInstance()
            //Date
            val year = mTpDate.year
            val month = mTpDate.month
            val day = mTpDate.dayOfMonth
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)

            //Time
            val hour = mTpTime.currentHour
            val minute = mTpTime.currentMinute
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            val time = calendar.time
            mCallBack?.onClick(time, Utils.getFormattedDate(time, mType))

            dismiss()
        }
        return view
    }

    private fun initConfiguration(context: Context) {
        mLlDate.orientation = context.resources.configuration.orientation
        context.registerComponentCallbacks(object : ComponentCallbacks {
            override fun onConfigurationChanged(newConfig: Configuration) {
                newConfig.orientation.apply { mLlDate.orientation = this }
            }

            override fun onLowMemory() {
            }
        })
    }

    private fun getDate(): Date = mDate ?: Date()

    fun setDate(date: Date?) {
        mDate = date
    }

    fun setType(type: String) {
        mType = type
    }

    internal object Utils {
        /**
         * 根据日期获取年
         */
        fun getYear(date: Date): Int {
            val cal = Calendar.getInstance()
            cal.time = date
            return cal[Calendar.YEAR]
        }

        /**
         * 根据日期获取月0-11
         */
        fun getMonth(date: Date): Int {
            val cal = Calendar.getInstance()
            cal.time = date
            return cal[Calendar.MONTH]
        }

        /**
         * 根据日期获取日
         */
        fun getDay(date: Date): Int {
            val cal = Calendar.getInstance()
            cal.time = date
            return cal[Calendar.DAY_OF_MONTH]
        }

        /**
         * 根据日期获取小时
         */
        fun getHour(date: Date): Int {
            val cal = Calendar.getInstance()
            cal.time = date
            return cal[Calendar.HOUR_OF_DAY]
        }

        /**
         * 根据日期获取分钟
         */
        fun getMinute(date: Date): Int {
            val cal = Calendar.getInstance()
            cal.time = date
            return cal[Calendar.MINUTE]
        }

        /**
         * 日期转换为指定格式字符串
         *
         * @param date   日期
         * @param format 日期格式
         * @return 格式化后日期字符串
         */
        fun getFormattedDate(date: Date, format: String?): String {
            return SimpleDateFormat(format, Locale.getDefault()).format(date)
        }
    }

}