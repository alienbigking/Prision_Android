package com.starlight.mobile.android.lib.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.RadioButton

import com.starlight.mobile.android.lib.R


/**底部菜单按钮
 * @author raleighluo
 */
class RadioButtonPlus : RadioButton {
    private var leftHeight = -1
    private var leftWidth = -1
    private var rightHeight = -1
    private var rightWidth = -1
    private var topHeight = -1
    private var topWidth = -1
    private var bottomHeight = -1
    private var bottomWidth = -1
    private var isShow = false
    /**
     * Get Rect
     * @return Rect
     */
    var rect: Rect? = null
        private set


    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context, attrs, defStyle)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val paint = Paint()
        paint.color = Color.TRANSPARENT
        val size = resources.getDimensionPixelSize(R.dimen.radio_button_plus_size)
        val rect = Rect(this.width - size, 0, this.width, this.height - size)
        this.rect = rect
        canvas.drawRect(rect, paint)
        if (isShow) {
            val icon = BitmapFactory.decodeResource(resources, R.drawable.tab_notification_bg)
            val matrix = Matrix()
            matrix.postScale(0.5f, 0.5f)
            val newIcon = Bitmap.createBitmap(icon, 0, 0, icon.width, icon.height, matrix, true)
            canvas.drawBitmap(newIcon, (this.width - 45).toFloat(), 10f, null)
        } else {
            canvas.save()
            canvas.restore()
        }
    }


    /**
     * Set the notification String
     * @param isShow set is show
     */
    fun setNotification(isShow: Boolean) {
        this.isShow = isShow
    }

    private fun init(context: Context, attrs: AttributeSet, defStyle: Int) {
        val a = context.obtainStyledAttributes(attrs,
                R.styleable.RadioButtonPlus, defStyle, 0)
        val count = a.indexCount
        var index = 0
        for (i in 0..count - 1) {
            index = a.getIndex(i)
            if (index == R.styleable.RadioButtonPlus_radio_bottom_height)
                bottomHeight = a.getDimensionPixelSize(index, -1)
            if (index == R.styleable.RadioButtonPlus_radio_bottom_width)
                bottomWidth = a.getDimensionPixelSize(index, -1)
            if (index == R.styleable.RadioButtonPlus_radio_left_height)
                leftHeight = a.getDimensionPixelSize(index, -1)
            if (index == R.styleable.RadioButtonPlus_radio_left_width)
                leftWidth = a.getDimensionPixelSize(index, -1)
            if (index == R.styleable.RadioButtonPlus_radio_right_height)
                rightHeight = a.getDimensionPixelSize(index, -1)
            if (index == R.styleable.RadioButtonPlus_radio_right_width)
                rightWidth = a.getDimensionPixelSize(index, -1)
            if (index == R.styleable.RadioButtonPlus_radio_top_height)
                topHeight = a.getDimensionPixelSize(index, -1)
            if (index == R.styleable.RadioButtonPlus_radio_top_width)
                topWidth = a.getDimensionPixelSize(index, -1)
        }
        val drawables = compoundDrawables
        var dir = 0
        // 0-left; 1-top; 2-right; 3-bottom;
        for (drawable in drawables) {
            setImageSize(drawable, dir++)
        }
        setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3])
    }

    fun setTopDrawable(drawableTop: Drawable) {
        drawableTop.setBounds(0, 0, topWidth, topHeight)
        setCompoundDrawables(null, drawableTop, null, null)
    }

    fun setLeftDrawable(drawableLeft: Drawable) {
        drawableLeft.setBounds(0, 0, leftWidth, leftHeight)
        setCompoundDrawables(drawableLeft, null, null, null)
    }

    fun setRightDrawable(drawableRight: Drawable) {
        drawableRight.setBounds(0, 0, rightWidth, rightHeight)
        setCompoundDrawables(null, null, drawableRight, null)
    }

    fun setBottomDrawable(drawableBottom: Drawable) {
        drawableBottom.setBounds(0, 0, bottomWidth, bottomHeight)
        setCompoundDrawables(null, null, null, drawableBottom)
    }

    private fun setImageSize(d: Drawable?, dir: Int) {
        if (d == null) {
            return
        }

        var height = -1
        var mWidth = -1
        when (dir) {
            0 -> {
                // left
                height = leftHeight
                mWidth = leftWidth
            }
            1 -> {
                // top
                height = topHeight
                mWidth = topWidth
            }
            2 -> {
                // right
                height = rightHeight
                mWidth = rightWidth
            }
            3 -> {
                // bottom
                height = bottomHeight
                mWidth = bottomWidth
            }
        }
        if (mWidth != -1 && height != -1) {
            d.setBounds(0, 0, mWidth, height)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val measuredWidth = measuredWidth
        if (measuredWidth > 0 && mWidth != measuredWidth) {
            mWidth = measuredWidth
            listener?.onChange(mWidth)
        }
    }

    private var mWidth = 0

    interface OnChangeWidthListener {
        fun onChange(measuredWidth: Int)
    }

    var listener: OnChangeWidthListener? = null
}