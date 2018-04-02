package com.starlight.mobile.android.lib.view


import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.TextView

import com.starlight.mobile.android.lib.R

/**自定义textview 图标样式
 * @author raleigh
 * @date 2015-02-09
 */
class CusTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : TextView(context, attrs) {
    private var leftHeight = -1
    private var leftWidth = -1
    private var rightHeight = -1
    private var rightWidth = -1
    private var topHeight = -1
    private var topWidth = -1
    private var bottomHeight = -1
    private var bottomWidth = -1
    private var isDrawableCenter=false

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CusTextView_Attrs)
        val count = a.indexCount
        var index = 0
        for (i in 0..count - 1) {
            index = a.getIndex(i)
            if (index == R.styleable.CusTextView_Attrs_ctv_bottomImg_height)
                bottomHeight = a.getDimensionPixelSize(index, -1)
            if (index == R.styleable.CusTextView_Attrs_ctv_bottomImg_width)
                bottomWidth = a.getDimensionPixelSize(index, -1)
            if (index == R.styleable.CusTextView_Attrs_ctv_leftImg_height)
                leftHeight = a.getDimensionPixelSize(index, -1)
            if (index == R.styleable.CusTextView_Attrs_ctv_leftImg_width)
                leftWidth = a.getDimensionPixelSize(index, -1)
            if (index == R.styleable.CusTextView_Attrs_ctv_rightImg_height)
                rightHeight = a.getDimensionPixelSize(index, -1)
            if (index == R.styleable.CusTextView_Attrs_ctv_rightImg_width)
                rightWidth = a.getDimensionPixelSize(index, -1)
            if (index == R.styleable.CusTextView_Attrs_ctv_topImg_height)
                topHeight = a.getDimensionPixelSize(index, -1)
            if (index == R.styleable.CusTextView_Attrs_ctv_topImg_width)
                topWidth = a.getDimensionPixelSize(index, -1)
            if(index == R.styleable.CusTextView_Attrs_ctv_drawable_center)
                isDrawableCenter=a.getBoolean(index,false)
        }

        val drawables = compoundDrawables
        var dir = 0
        // 0-left; 1-top; 2-right; 3-bottom;
        for (drawable in drawables) {
            setImageSize(drawable, dir++)
        }
        setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3])
    }
    var bodyWidth:Float=-1f

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        //图片和文字居中
        if(isDrawableCenter) {
            val drawables = compoundDrawables
            if (drawables != null) {
                for(i in 0..drawables.size-1){
                    drawcenter(drawables[i],i)
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.translate((width - bodyWidth) / 2, 0f)
    }
    private fun drawcenter(drawable: Drawable?,position: Int){
        if (drawable != null) {
            val textWidth = paint.measureText(text.toString())
            val drawablePadding = compoundDrawablePadding
            var drawableWidth =  2*drawable.intrinsicWidth
            bodyWidth = textWidth + drawableWidth.toFloat() + drawablePadding.toFloat()
            val padding=((width - bodyWidth)/2).toInt()
            setPadding(if(position==0||position==2)padding else paddingLeft,
                    if(position==1||position==3)padding else paddingTop,
                    if(position==0||position==2)padding else paddingRight,
                    if(position==1||position==3)padding else paddingBottom)
        }
    }

    fun setTopDrawable(drawableTop: Drawable?) {
        val drawables = compoundDrawables
        drawableTop?.setBounds(0, 0, topWidth, topHeight)
        setCompoundDrawables(drawables[0], drawableTop, drawables[2], drawables[3])
    }

    fun setLeftDrawable(drawableLeft: Drawable?) {
        val drawables = compoundDrawables
        drawableLeft?.setBounds(0, 0, leftWidth, leftHeight)
        setCompoundDrawables(drawableLeft, drawables[1], drawables[2], drawables[3])
    }

    fun setRightDrawable(drawableRight: Drawable?) {
        val drawables = compoundDrawables
        drawableRight?.setBounds(0, 0, rightWidth, rightHeight)
        setCompoundDrawables(drawables[0], drawables[1], drawableRight, drawables[3])
    }

    fun setBottomDrawable(drawableBottom: Drawable?) {
        val drawables = compoundDrawables
        drawableBottom?.setBounds(0, 0, bottomWidth, bottomHeight)
        setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawableBottom)
    }

    private fun setImageSize(d: Drawable?, dir: Int) {
        if (d == null) {
            return
        }

        var height = -1
        var width = -1
        when (dir) {
            0 -> {
                // left
                height = leftHeight
                width = leftWidth
            }
            1 -> {
                // top
                height = topHeight
                width = topWidth
            }
            2 -> {
                // right
                height = rightHeight
                width = rightWidth
            }
            3 -> {
                // bottom
                height = bottomHeight
                width = bottomWidth
            }
        }
        if (width != -1 && height != -1) {
            d.setBounds(0, 0, width, height)
        }
    }


}
