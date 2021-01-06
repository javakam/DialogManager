package ando.dialog

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.TimePicker
import java.text.SimpleDateFormat
import java.util.*

/**
 * 日期&时间选择控件
 *
 * @author javakam
 */
class DateTimePickerDialog : FragmentDialog() {

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
    private var mListener: CallBack? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_picker, container, false)
        mLlDate = view.findViewById<View>(R.id.ll_date) as LinearLayout
        mTpDate = view.findViewById<View>(R.id.tp_date) as DatePicker
        mTpTime = view.findViewById<View>(R.id.tp_time) as TimePicker
        if (TextUtils.equals(mType, Y_M_D)) {
            mLlDate.removeView(mTpTime)
        }
        if (TextUtils.equals(mType, H_M)) {
            mLlDate.removeView(mTpDate)
        }

        mTpDate.init(
            Utils.getYear(getDate()),
            Utils.getMonth(getDate()),
            Utils.getDay(getDate()),
            null
        )

        mTpTime.setIs24HourView(true)
        mTpTime.currentHour = Utils.getHour(getDate())
        mTpTime.currentMinute = Utils.getMinute(getDate())

        mBtnCancel = view.findViewById(R.id.btn_cancel)
        mBtnCertain = view.findViewById(R.id.btn_certain)
        mBtnCancel.setOnClickListener {
            dismiss()
        }

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
            mListener?.onClick(Utils.getDate(calendar.time, mType))
            dismiss()
        }
        return view
    }

    fun getDate(): Date = mDate ?: Date()

    fun setDate(date: Date?) {
        mDate = date
    }

    fun setType(type: String) {
        mType = type
    }

    interface CallBack {
        fun onClick(dateTime: String)
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
        fun getDate(date: Date, format: String?): String {
            return SimpleDateFormat(format, Locale.getDefault()).format(date)
        }
    }

}