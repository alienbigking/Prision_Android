package com.starlight.mobile.android.lib.view

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup

/**
 * Created by liruwang on 15/9/7.
 */
class FullyLinearLayoutManager : LinearLayoutManager {

    constructor(context: Context) : super(context)

    constructor(context: Context, orientation: Int, reverseLayout: Boolean) : super(context, orientation, reverseLayout)

    private val mMeasuredDimension = IntArray(2)

    override fun onMeasure(recycler: RecyclerView.Recycler?, state: RecyclerView.State?,
                           widthSpec: Int, heightSpec: Int) {

        val widthMode = View.MeasureSpec.getMode(widthSpec)
        val heightMode = View.MeasureSpec.getMode(heightSpec)
        val widthSize = View.MeasureSpec.getSize(widthSpec)
        val heightSize = View.MeasureSpec.getSize(heightSpec)

        Log.i(TAG, "onMeasure called. \nwidthMode " + widthMode
                + " \nheightMode " + heightSpec
                + " \nwidthSize " + widthSize
                + " \nheightSize " + heightSize
                + " \ngetItemCount() " + itemCount)

        var width = 0
        var height = 0
        for (i in 0..itemCount - 1) {
            measureScrapChild(recycler, i,
                    View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
                    mMeasuredDimension)

            if (orientation == LinearLayoutManager.HORIZONTAL) {
                width = width + mMeasuredDimension[0]
                if (i == 0) {
                    height = mMeasuredDimension[1]
                }
            } else {
                height = height + mMeasuredDimension[1]
                if (i == 0) {
                    width = mMeasuredDimension[0]
                }
            }
        }
        when (widthMode) {
            View.MeasureSpec.EXACTLY -> width = widthSize
        }

        when (heightMode) {
            View.MeasureSpec.EXACTLY -> height = heightSize
        }

        setMeasuredDimension(width, height)
    }

    private fun measureScrapChild(recycler: RecyclerView.Recycler?, position: Int, widthSpec: Int,
                                  heightSpec: Int, measuredDimension: IntArray) {
        try {
            val view = recycler?.getViewForPosition(0)//fix 动态添加时报IndexOutOfBoundsException

            view?.let {
                val p = it.layoutParams as RecyclerView.LayoutParams

                val childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec,
                        paddingLeft + paddingRight, p.width)

                val childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec,
                        paddingTop + paddingBottom, p.height)

                it.measure(childWidthSpec, childHeightSpec)
                measuredDimension[0] = it.measuredWidth + p.leftMargin + p.rightMargin
                measuredDimension[1] = it.measuredHeight + p.bottomMargin + p.topMargin
                recycler.recycleView(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
        }
    }

    companion object {

        private val TAG = FullyLinearLayoutManager::class.java.simpleName
    }
}