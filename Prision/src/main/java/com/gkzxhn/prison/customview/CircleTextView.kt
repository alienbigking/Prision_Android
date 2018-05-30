package com.gkzxhn.prison.customview

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.TextView

import com.gkzxhn.prison.R

/**
 * Created by Raleigh.Luo on 18/5/30.
 */

class CircleTextView:TextView{

    private var angle = 0f
    private var position = 0
    private var name: String? = null

    fun getAngle(): Float {
        return angle
    }

    fun setAngle(angle: Float) {
        this.angle = angle
    }

    fun getPosition(): Int {
        return position
    }

    fun setPosition(position: Int) {
        this.position = position
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String) {
        this.name = name
    }

    /**
     * @param context
     */
    constructor(context: Context): super(context,null) {
    }

    /**
     * @param context
     * @param attrs
     */
    constructor(context: Context, attrs: AttributeSet?):  super(context, attrs, 0) {
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int): super(context, attrs, defStyle) {

        if (attrs != null) {
            val a = getContext().obtainStyledAttributes(attrs,
                    R.styleable.CircleImageView)

            name = a.getString(R.styleable.CircleImageView_name)
        }
    }

}

