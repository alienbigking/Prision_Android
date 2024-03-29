package com.starlight.mobile.android.lib.view

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.ImageView

/**
 * Created by Raleigh on 15/12/10.
 */
class EqualImageView : ImageView {
    var listener: OnChangeHeightListener? = null
    private var mHeight = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val measuredWidth = measuredWidth
        setMeasuredDimension(measuredWidth, measuredWidth)

        if (measuredWidth > 0 && mHeight != measuredWidth) {
            mHeight = measuredWidth
            listener?.onChange(mHeight)
        }
    }

    interface OnChangeHeightListener {
        fun onChange(measuredHeight: Int)
    }
}
