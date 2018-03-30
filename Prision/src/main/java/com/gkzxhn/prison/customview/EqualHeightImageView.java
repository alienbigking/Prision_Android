package com.gkzxhn.prison.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Raleigh.Luo on 18/3/30.
 */

public class EqualHeightImageView extends ImageView {
    public EqualHeightImageView(Context context) {
        super(context);
    }

    public EqualHeightImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EqualHeightImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EqualHeightImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredHeight=getMeasuredHeight();
        setMeasuredDimension(measuredHeight, measuredHeight);
    }
}
