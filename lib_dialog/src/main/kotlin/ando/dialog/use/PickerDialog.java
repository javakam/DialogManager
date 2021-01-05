package ando.dialog.use;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import java.util.Date;

import ando.dialog.R;
import ando.dialog.WhiteDialog;

/**
 * 日历控件
 * <p>
 * @author javakam
 */
public class PickerDialog extends WhiteDialog {

    private LinearLayout mLlDate;
    private DatePicker mTpDate;
    private TimePicker mTpTime;
    private Button mBtnCancel;
    private Button mBtnCertain;
    private String mType = Y_M_D;
    private Date mDate;

    public static final String Y_M_D_H_M = "yyyy-MM-dd HH:mm";
    public static final String Y_M_D = "yyyy-MM-dd";
    public static final String H_M = "HH:mm";

    private OnClickListener mListener;

    public interface OnClickListener {
        void onClick(String date);
    }

    @Override
    public View onCreateView(LayoutInflater inflater) {
        setWidth(600);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        View view = inflater.inflate(R.layout.dialog_picker, null);
        mLlDate = (LinearLayout) view.findViewById(R.id.ll_date);
        mTpDate = (DatePicker) view.findViewById(R.id.tp_date);
        mTpTime = (TimePicker) view.findViewById(R.id.tp_time);
        if (TextUtils.equals(mType, Y_M_D)) {
            mLlDate.removeView(mTpTime);
        }
        if (TextUtils.equals(mType, H_M)) {
            mLlDate.removeView(mTpDate);
        }

//        if (mTpDate != null) {
//            mTpDate.init(DateUtils.getYear(mDate), DateUtils.getMonth(mDate), DateUtils.getDay(mDate), null);
//        }
//
//        if (mTpTime != null) {
//            mTpTime.setIs24HourView(true);
//            mTpTime.setCurrentHour(DateUtils.getHour(mDate));
//            mTpTime.setCurrentMinute(DateUtils.getMinute(mDate));
//        }
//
//        mBtnCancel = (Button) view.findViewById(R.id.btn_cancel);
//        mBtnCertain = (Button) view.findViewById(R.id.btn_certain);
//        mBtnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });
//
//        mBtnCertain.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Calendar calendar = Calendar.getInstance();
//                if (mTpDate != null) {
//                    int year = mTpDate.getYear();
//                    int month = mTpDate.getMonth();
//                    int day = mTpDate.getDayOfMonth();
//                    calendar.set(Calendar.YEAR, year);
//                    calendar.set(Calendar.MONTH, month);
//                    calendar.set(Calendar.DAY_OF_MONTH, day);
//                }
//                if (mTpTime != null) {
//                    int hour = mTpTime.getCurrentHour();
//                    int minute = mTpTime.getCurrentMinute();
//                    calendar.set(Calendar.HOUR_OF_DAY, hour);
//                    calendar.set(Calendar.MINUTE, minute);
//                }
//                if (mListener != null) {
//                    mListener.onClick(DateUtils.getDate(calendar.getTime(), mType));
//                }
//                dismiss();
//            }
//        });
        return view;
    }

    public void setDate(Date date) {
        this.mDate = date;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public void setOnClickListener(OnClickListener listener) {
        this.mListener = listener;
    }

}
