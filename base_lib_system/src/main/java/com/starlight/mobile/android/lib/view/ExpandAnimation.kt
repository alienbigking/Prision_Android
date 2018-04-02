package com.starlight.mobile.android.lib.view

import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout.LayoutParams
import android.widget.TextView

/**
 * @author raleigh
 */
class ExpandAnimation
/**
 * Initialize the animation
 *
 * @param view
 * The layout we want to animate
 * @param duration
 * The duration of the animation, in ms
 */
(private val mAnimatedView: TextView, duration: Int, private val onClickListener: OnExpandClickListener?) : Animation() {
    private val mViewLayoutParams: LayoutParams
    private val mMarginStart: Int
    private val mMarginEnd: Int
    private var mIsVisibleAfter = false
    private var mWasEndedAlready = false
    init {
        mAnimatedView.setSingleLine(false)
        setDuration(duration.toLong())
        mViewLayoutParams = mAnimatedView.layoutParams as LayoutParams
        // if the bottom margin is 0,
        // then after the animation will end it'll be negative, and invisible.
        mIsVisibleAfter = mViewLayoutParams.bottomMargin == 0
        mMarginStart = mViewLayoutParams.bottomMargin
        mMarginEnd = if (mMarginStart == 0) 0 - mAnimatedView.height else 0
        this.onClickListener?.up()
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        super.applyTransformation(interpolatedTime, t)
        if (interpolatedTime < 1.0f) {
            // Calculating the new bottom margin, and setting it
            mViewLayoutParams.bottomMargin = mMarginStart + ((mMarginEnd - mMarginStart) * interpolatedTime).toInt()
            // Invalidating the layout, making us seeing the changes we made
            mAnimatedView.requestLayout()
            // Making sure we didn't run the ending before (it happens!)
        } else if (!mWasEndedAlready) {
            if (mIsVisibleAfter) {
                mAnimatedView.setSingleLine(true)
                mViewLayoutParams.bottomMargin = 1//这个值主要是为了mIsVisibleAfter = (mViewLayoutParams.bottomMargin == 0);为false
                mAnimatedView.requestLayout()
                onClickListener?.down()
            } else {
                mViewLayoutParams.bottomMargin = 0
                mAnimatedView.requestLayout()
            }
            mWasEndedAlready = true
        }
    }
    interface OnExpandClickListener {
        fun down()
        fun up()
    }
}
