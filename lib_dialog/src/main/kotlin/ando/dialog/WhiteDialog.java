package ando.dialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * 白色标题背景的弹窗
 * <p>
 * Pop-up window with white title background
 *
 * @author javakam
 */
public abstract class WhiteDialog extends FragmentDialog {

    private RelativeLayout mRlHead;
    private TextView mTvTitle;
    private ImageView mIvClose;
    private FrameLayout mFlContent;

    private String mTitle = "";

    @Override
    public final void initView(View view) {
        //WindowUtils.hideNavigation(getWindow());
        setWidth(500);
        setHeight(250);
        mRlHead = (RelativeLayout) view.findViewById(R.id.rl_head);
        mTvTitle = (TextView) view.findViewById(R.id.tv_title);
        mIvClose = (ImageView) view.findViewById(R.id.iv_close);
        mFlContent = (FrameLayout) view.findViewById(R.id.fl_content);
    }

    @Override
    public final void initData() {
        mTvTitle.setText(mTitle);
        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mFlContent.addView(onCreateView(LayoutInflater.from(getActivity())));
        mFlContent.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public abstract View onCreateView(LayoutInflater inflater);

    public void setTitle(String title) {
        this.mTitle = title;
        if (mTvTitle != null) {
            mTvTitle.setText(title);
        }
    }

    public final ViewGroup getHeadLayout() {
        return mRlHead;
    }

    public final ViewGroup getContentLayout() {
        return mFlContent;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_white;
    }
}