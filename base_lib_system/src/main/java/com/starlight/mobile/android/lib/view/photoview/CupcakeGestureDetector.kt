/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.starlight.mobile.android.lib.view.photoview

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewConfiguration

open class CupcakeGestureDetector(context: Context) : GestureDetector {

    protected var mListener: OnGestureListener?=null
    internal var mLastTouchX: Float = 0.toFloat()
    internal var mLastTouchY: Float = 0.toFloat()
    internal val mTouchSlop: Float
    internal val mMinimumVelocity: Float

    override fun setOnGestureListener(listener: OnGestureListener) {
        this.mListener = listener
    }

    init {
        val configuration = ViewConfiguration
                .get(context)
        mMinimumVelocity = configuration.scaledMinimumFlingVelocity.toFloat()
        mTouchSlop = configuration.scaledTouchSlop.toFloat()
    }

    private var mVelocityTracker: VelocityTracker? = null
    private var mIsDragging: Boolean = false

    internal open fun getActiveX(ev: MotionEvent): Float {
        return ev.x
    }

    internal open fun getActiveY(ev: MotionEvent): Float {
        return ev.y
    }

    override val isScaling: Boolean
        get() = false

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mVelocityTracker = VelocityTracker.obtain()
                mVelocityTracker?.let {
                    mVelocityTracker?.addMovement(ev)
//                } else {
//                    Log.i(LOG_TAG, "Velocity tracker is null")
                }

                mLastTouchX = getActiveX(ev)
                mLastTouchY = getActiveY(ev)
                mIsDragging = false
            }

            MotionEvent.ACTION_MOVE -> {
                val x = getActiveX(ev)
                val y = getActiveY(ev)
                val dx = x - mLastTouchX
                val dy = y - mLastTouchY

                if (!mIsDragging) {
                    // Use Pythagoras to see if drag length is larger than
                    // touch slop
                    mIsDragging = Math.sqrt((dx * dx + dy * dy).toDouble()) >= mTouchSlop
                }

                if (mIsDragging) {
                    mListener?.onDrag(dx, dy)
                    mLastTouchX = x
                    mLastTouchY = y

                    mVelocityTracker?.let{
                        mVelocityTracker?.addMovement(ev)
                    }
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                // Recycle Velocity Tracker
                mVelocityTracker?.let {
                    it.recycle()
                }
                mVelocityTracker = null
            }

            MotionEvent.ACTION_UP -> {
                if (mIsDragging) {
                    mVelocityTracker?.let{
                        mLastTouchX = getActiveX(ev)
                        mLastTouchY = getActiveY(ev)

                        // Compute velocity within the last 1000ms
                        it.addMovement(ev)
                        it.computeCurrentVelocity(1000)

                        val vX = it.xVelocity
                        val vY = it.yVelocity

                        // If the velocity is greater than minVelocity, call
                        // mPhotoFromClickListener
                        if (Math.max(Math.abs(vX), Math.abs(vY)) >= mMinimumVelocity) {
                            mListener?.onFling(mLastTouchX, mLastTouchY, -vX,
                                    -vY)
                        }
                    }
                }

                // Recycle Velocity Tracker
                mVelocityTracker?.let{
                    it.recycle()
                }
                mVelocityTracker = null
            }
        }

        return true
    }

    companion object {
        private val LOG_TAG = "CupcakeGestureDetector"
    }
}
