package com.starlight.mobile.android.lib.view.dotsloading

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathEffect
import android.util.AttributeSet
import android.view.View

import com.starlight.mobile.android.lib.R


/**
 * Created by Administrator on 2016/11/14 0014.
 */

class DottedLine : View {
    private val paint: Paint
    private val path: Path
    private val pe: PathEffect
    init{
        this.paint = Paint()
        this.path = Path()
        val arrayOfFloat = FloatArray(4)
        arrayOfFloat[0] = dip2px(context, 2.0f).toFloat()
        arrayOfFloat[1] = dip2px(context, 2.0f).toFloat()
        arrayOfFloat[2] = dip2px(context, 2.0f).toFloat()
        arrayOfFloat[3] = dip2px(context, 2.0f).toFloat()
        this.pe = DashPathEffect(arrayOfFloat, dip2px(context, 1.0f).toFloat())
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.DottedLine)
        val lineColor = a.getColor(R.styleable.DottedLine_lineColor, 0XFF000000.toInt())
        a.recycle()
        this.paint.style = Paint.Style.STROKE
        this.paint.color = lineColor
        this.paint.isAntiAlias = true
        this.paint.strokeWidth = dip2px(getContext(), 2.0f).toFloat()

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        this.path.moveTo(0.0f, 0.0f)
        this.path.lineTo(0.0f, measuredHeight.toFloat())
        this.paint.pathEffect = this.pe
        canvas.drawPath(this.path, this.paint)
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue
     * （DisplayMetrics类中属性density）
     * @return
     */
    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     * （DisplayMetrics类中属性density）
     * @return
     */
    fun dip2px(context: Context, dipValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    fun px2sp(context: Context, pxValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    fun sp2px(context: Context, spValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }
}
