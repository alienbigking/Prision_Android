package com.starlight.mobile.android.lib.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.ViewGroup

/**
 * Created by Raleigh on 15/7/2.
 */
class PagerTabAdapter(fm: FragmentManager, private val fragmentList: List<Fragment>?, private val titleList: List<String>) : FragmentPagerAdapter(fm) {

    var currentFragment: Fragment? = null
    var mOnPageSelected: OnPageSelected? = null
    fun setOnPageSelected(onPageSelected: OnPageSelected) {
        this.mOnPageSelected = onPageSelected

    }

    override fun setPrimaryItem(container: ViewGroup?, position: Int, `object`: Any) {
        currentFragment = `object` as Fragment
        mOnPageSelected?.let {
            it.OnPageSelected()
        }
        super.setPrimaryItem(container, position, `object`)
    }

    /**
     * 得到每个页面
     */
    override fun getItem(arg0: Int): Fragment? {
        val fragment:Fragment?=fragmentList?.let {
            if(it.size==0) null else it[arg0]
        }
        return fragment
    }

    /**
     * 每个页面的title
     */
    override fun getPageTitle(position: Int): CharSequence {
        return  if (titleList.size > position) titleList[position] else ""
    }

    interface OnPageSelected {
        fun OnPageSelected()
    }

    /**
     * 页面的总个数
     */
    override fun getCount(): Int {
        return fragmentList?.size ?: 0
    }

}
