package ando.dialog.usage.bottomsheet;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

import ando.dialog.usage.R;

/**
 * BottomSheet 的 ItemView
 *
 * @author javakam
 * @date 2018/11/30 下午1:58
 */
public class BottomSheetItemView extends LinearLayout {

    private AppCompatImageView mAppCompatImageView;
    private TextView mTextView;

    public BottomSheetItemView(Context context) {
        super(context);
    }

    public BottomSheetItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomSheetItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mAppCompatImageView = findViewById(R.id.grid_item_image);
        mTextView = findViewById(R.id.grid_item_title);
    }

    public AppCompatImageView getAppCompatImageView() {
        return mAppCompatImageView;
    }

    public TextView getTextView() {
        return mTextView;
    }

    @Override
    public String toString() {
        return mTextView.getText().toString();
    }
}