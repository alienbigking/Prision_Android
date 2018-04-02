package com.starlight.mobile.android.lib.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View

/**
 * @author raleigh
 */
class CutPhotoBorderView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : View(context, attrs, defStyle) {
    /**
     * 水平方向与View的边距
     */
    private var mHorizontalPadding: Int = 0
    /**
     * 垂直方向与View的边距
     */
    private var mVerticalPadding: Int = 0
    /**
     * 绘制的矩形的宽度
     */
    private var mWidth: Int = 0
    /**
     * 边框的颜色，默认为白色
     */
    private val mBorderColor = Color.parseColor("#FFFFFF")
    /**
     * 边框的宽度 单位dp
     */
    private var mBorderWidth = 1

    private val mPaint: Paint

    init {

        mBorderWidth = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, mBorderWidth.toFloat(), resources
                .displayMetrics).toInt()
        mPaint = Paint()
        mPaint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 计算矩形区域的宽度
        mWidth = width - 2 * mHorizontalPadding
        // 计算距离屏幕垂直边界 的边距
        mVerticalPadding = (height - mWidth) / 2
        mPaint.color = Color.parseColor("#aa000000")
        mPaint.style = Style.FILL
        // 绘制左边1
        canvas.drawRect(0f, 0f, mHorizontalPadding.toFloat(), height.toFloat(), mPaint)
        // 绘制右边2
        canvas.drawRect((width - mHorizontalPadding).toFloat(), 0f, width.toFloat(),
                height.toFloat(), mPaint)
        // 绘制上边3
        canvas.drawRect(mHorizontalPadding.toFloat(), 0f, (width - mHorizontalPadding).toFloat(),
                mVerticalPadding.toFloat(), mPaint)
        // 绘制下边4
        canvas.drawRect(mHorizontalPadding.toFloat(), (height - mVerticalPadding).toFloat(),
                (width - mHorizontalPadding).toFloat(), height.toFloat(), mPaint)
        // 绘制外边框
        mPaint.color = mBorderColor
        mPaint.strokeWidth = mBorderWidth.toFloat()
        mPaint.style = Style.STROKE
        canvas.drawRect(mHorizontalPadding.toFloat(), mVerticalPadding.toFloat(), (width - mHorizontalPadding).toFloat(), (height - mVerticalPadding).toFloat(), mPaint)

    }

    fun setHorizontalPadding(mHorizontalPadding: Int) {
        this.mHorizontalPadding = mHorizontalPadding

    }

}
