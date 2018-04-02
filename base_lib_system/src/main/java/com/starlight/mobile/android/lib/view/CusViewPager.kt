package com.starlight.mobile.android.lib.view

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet

class CusViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {
    var isCanScroll = true
    override fun scrollTo(x: Int, y: Int) {
        if (isCanScroll) {
            super.scrollTo(x, y)
        }
    }
}
