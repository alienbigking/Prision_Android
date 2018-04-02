/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.starlight.mobile.android.lib.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Style
import android.graphics.Typeface
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.support.v4.view.ViewPager
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*

import com.starlight.mobile.android.lib.R

import java.util.Locale


class PagerSlidingTabStrip @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : HorizontalScrollView(context, attrs, defStyle) {

    interface IconTabProvider {
        fun getPageIconResId(position: Int): Int
    }
    // @formatter:on

    private val defaultTabLayoutParams: LinearLayout.LayoutParams
    private val expandedTabLayoutParams: LinearLayout.LayoutParams

    private val pageListener = PageListener()
    var onPageChangeListener: OnPageChangeListener? = null

    private val tabsContainer: LinearLayout
    private var pager: ViewPager? = null

    private var tabCount: Int = 0

    private var currentPosition = 0
    private var currentPositionOffset = 0f

    private val rectPaint: Paint
    private val dividerPaint: Paint

    var indicatorColor  = 0xFF666666.toInt()
        set(value) {
            field = value
            invalidate()
        }

    var underlineColor  = 0x1A000000
        set(value) {
            field = value
            invalidate()
        }
    var dividerColor = 0x1A000000
        set(value) {
            field = value
            invalidate()
        }

    var shouldExpand  = false
        set(value) {
            field = value
            requestLayout()
        }
    var isTextAllCaps = true

    var scrollOffset  = 52
        set(value) {
            field = value
            invalidate()
        }

    var indicatorHeight= 8
        set(value){
            field = value
            invalidate()
        }
    var underlineHeight = 2
        set(value){
            field = value
            invalidate()
        }
    var dividerPadding = 12
        set(value){
            field = value
            invalidate()
        }
    var tabPadding = 24
        set(value) {
            field= value
            updateTabStyles()
        }
    private var dividerWidth = 1

    var tabTextSize = 12
        set(value) {
            field = value
            updateTabStyles()
        }
    private var tabTextSelectedColor = 0xFF666666.toInt()
    private var tabTextUnSelectedColor = 0xFF666666.toInt()
    private var tabTypeface: Typeface? = null
    private var tabTypefaceStyle = Typeface.BOLD

    private var lastScrollX = 0

    var tabBackground = R.drawable.background_tab

    private var locale: Locale? = null



    init {

        isFillViewport = true
        setWillNotDraw(false)

        tabsContainer = LinearLayout(context)
        tabsContainer.orientation = LinearLayout.HORIZONTAL
        tabsContainer.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        addView(tabsContainer)

        val dm = resources.displayMetrics

        scrollOffset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrollOffset.toFloat(), dm).toInt()
        indicatorHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorHeight.toFloat(), dm).toInt()
        underlineHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, underlineHeight.toFloat(), dm).toInt()
        dividerPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerPadding.toFloat(), dm).toInt()
        tabPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabPadding.toFloat(), dm).toInt()
        dividerWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerWidth.toFloat(), dm).toInt()
        tabTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, tabTextSize.toFloat(), dm).toInt()

        // get system attrs (android:textSize and android:textColor)

        var a = context.obtainStyledAttributes(attrs, ATTRS)

        tabTextSize = a.getDimensionPixelSize(0, tabTextSize)


        a.recycle()

        // get custom attrs

        a = context.obtainStyledAttributes(attrs, R.styleable.PagerSlidingTabStrip)

        indicatorColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsIndicatorColor, indicatorColor)
        underlineColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsUnderlineColor, underlineColor)
        dividerColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsDividerColor, dividerColor)
        indicatorHeight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsIndicatorHeight, indicatorHeight)
        underlineHeight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsUnderlineHeight, underlineHeight)
        dividerPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsDividerPadding, dividerPadding)
        tabPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsTabPaddingLeftRight, tabPadding)
        tabBackground = a.getResourceId(R.styleable.PagerSlidingTabStrip_pstsTabBackground, tabBackground)
        shouldExpand = a.getBoolean(R.styleable.PagerSlidingTabStrip_pstsShouldExpand, shouldExpand)
        scrollOffset = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsScrollOffset, scrollOffset)
        isTextAllCaps = a.getBoolean(R.styleable.PagerSlidingTabStrip_pstsTextAllCaps, isTextAllCaps)
        tabTextSelectedColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsTextSelectedColor, tabTextSelectedColor)
        tabTextUnSelectedColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsTextUnSelectedColor, tabTextUnSelectedColor)
        a.recycle()

        rectPaint = Paint()
        rectPaint.isAntiAlias = true
        rectPaint.style = Style.FILL

        dividerPaint = Paint()
        dividerPaint.isAntiAlias = true
        dividerPaint.strokeWidth = dividerWidth.toFloat()

        defaultTabLayoutParams = LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT)
        expandedTabLayoutParams = LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.MATCH_PARENT, 1.0f)

        if (locale == null) {
            locale = resources.configuration.locale
        }
    }
    fun setViewPager(pager: ViewPager) {
        this.pager = pager

        if (pager.adapter == null) {
            throw IllegalStateException("ViewPager does not have adapter instance.")
        }

        pager.addOnPageChangeListener(pageListener)

        notifyDataSetChanged()
    }
    fun notifyDataSetChanged() {

        tabsContainer.removeAllViews()

        tabCount = pager?.adapter?.count?:0

        for (i in 0..tabCount - 1) {

            if (pager?.adapter is IconTabProvider) {
                addIconTab(i, (pager?.adapter as IconTabProvider).getPageIconResId(i))
            } else {
                addTextTab(i, pager?.adapter?.getPageTitle(i).toString())
            }

        }

        updateTabStyles()

        viewTreeObserver.addOnGlobalLayoutListener {
            //				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            //					getViewTreeObserver().removeGlobalOnLayoutListener(this);
            //				} else {
            //					getViewTreeObserver().removeOnGlobalLayoutListener(this);
            //				}

            currentPosition = pager?.currentItem?:0
            scrollToChild(currentPosition, 0)
        }

    }

    private fun addTextTab(position: Int, title: String) {
        val tab = TextView(context)
        tab.text = title
        tab.gravity = Gravity.CENTER
        tab.setSingleLine()
        addTab(position, tab)
    }

    private fun addIconTab(position: Int, resId: Int) {
        val tab = ImageButton(context)
        tab.setImageResource(resId)
        addTab(position, tab)
    }

    private fun addTab(position: Int, tab: View) {
        tab.isFocusable = true
        tab.setOnClickListener { pager?.currentItem = position }

        tab.setPadding(tabPadding, 0, tabPadding, 0)
        tabsContainer.addView(tab, position, if (shouldExpand) expandedTabLayoutParams else defaultTabLayoutParams)
    }

    private fun updateTabStyles() {

        for (i in 0..tabCount - 1) {

            val v = tabsContainer.getChildAt(i)

            v.setBackgroundResource(tabBackground)

            if (v is TextView) {

                v.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize.toFloat())
                v.setTypeface(tabTypeface, tabTypefaceStyle)
                if (currentPosition == i) {
                    v.setTextColor(tabTextSelectedColor)
                } else {
                    v.setTextColor(tabTextUnSelectedColor)
                }

                // setAllCaps() is only available from API 14, so the upper case is made manually if we are on a
                // pre-ICS-build
                if (isTextAllCaps) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        v.setAllCaps(true)
                    } else {
                        locale?.let {
                            v.text = v.text.toString().toUpperCase(it)
                        }
                    }
                }
            }
        }

    }

    private fun scrollToChild(position: Int, offset: Int) {

        if (tabCount == 0) {
            return
        }

        var newScrollX = tabsContainer.getChildAt(position).left + offset

        if (position > 0 || offset > 0) {
            newScrollX -= scrollOffset
        }

        if (newScrollX != lastScrollX) {
            lastScrollX = newScrollX
            scrollTo(newScrollX, 0)
        }

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (isInEditMode || tabCount == 0) {
            return
        }

        val height = height

        // draw indicator line

        rectPaint.color = indicatorColor

        // default: line below current tab
        val currentTab = tabsContainer.getChildAt(currentPosition)
        var lineLeft = currentTab.left.toFloat()
        var lineRight = currentTab.right.toFloat()

        // if there is an offset, start interpolating left and right coordinates between current and next tab
        if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {

            val nextTab = tabsContainer.getChildAt(currentPosition + 1)
            val nextTabLeft = nextTab.left.toFloat()
            val nextTabRight = nextTab.right.toFloat()

            lineLeft = currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * lineLeft
            lineRight = currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * lineRight
        }

        canvas.drawRect(lineLeft, (height - indicatorHeight).toFloat(), lineRight, height.toFloat(), rectPaint)

        // draw underline

        rectPaint.color = underlineColor
        canvas.drawRect(0f, (height - underlineHeight).toFloat(), tabsContainer.width.toFloat(), height.toFloat(), rectPaint)

        // draw divider

        dividerPaint.color = dividerColor
        for (i in 0..tabCount - 1 - 1) {
            val tab = tabsContainer.getChildAt(i)
            canvas.drawLine(tab.right.toFloat(), dividerPadding.toFloat(), tab.right.toFloat(), (height - dividerPadding).toFloat(), dividerPaint)
        }
    }

    private fun switchTextColor(position: Int, isSelected: Boolean) {
        try {
            val v = tabsContainer.getChildAt(position)
            (v as? TextView)?.setTextColor(if (isSelected) tabTextSelectedColor else tabTextUnSelectedColor)
        } catch (e: Exception) {
            notifyDataSetChanged()
        }

    }

    private inner class PageListener : OnPageChangeListener {

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            for (i in 0..tabsContainer.childCount - 1) {
                switchTextColor(i, false)
            }
            currentPosition = position
            switchTextColor(currentPosition, true)
            currentPositionOffset = positionOffset
            scrollToChild(position, (positionOffset * tabsContainer.getChildAt(position).width).toInt())
            invalidate()
            onPageChangeListener?.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageScrollStateChanged(state: Int) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                scrollToChild(pager?.currentItem?:0, 0)
            }
            onPageChangeListener?.onPageScrollStateChanged(state)
        }

        override fun onPageSelected(position: Int) {
            switchTextColor(currentPosition, false)
            switchTextColor(currentPosition, true)
            onPageChangeListener?.onPageSelected(position)
        }

    }

    fun setIndicatorColorResource(resId: Int) {
        this.indicatorColor = resources.getColor(resId)
        invalidate()
    }
    fun setUnderlineColorResource(resId: Int) {
        this.underlineColor = resources.getColor(resId)
        invalidate()
    }
    fun setDividerColorResource(resId: Int) {
        this.dividerColor = resources.getColor(resId)
        invalidate()
    }




    fun setTypeface(typeface: Typeface, style: Int) {
        this.tabTypeface = typeface
        this.tabTypefaceStyle = style
        updateTabStyles()
    }


    public override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        currentPosition = savedState.currentPosition
        requestLayout()
    }

    public override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val savedState = SavedState(superState)
        savedState.currentPosition = currentPosition
        return savedState
    }

    internal class SavedState : View.BaseSavedState {
        var currentPosition: Int = 0

        constructor(superState: Parcelable) : super(superState)

        private constructor(`in`: Parcel) : super(`in`) {
            currentPosition = `in`.readInt()
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeInt(currentPosition)
        }

        companion object {

            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(`in`: Parcel): SavedState {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    companion object {
        // @formatter:off
        private val ATTRS = intArrayOf(android.R.attr.textSize, android.R.attr.textColor)
    }

}
