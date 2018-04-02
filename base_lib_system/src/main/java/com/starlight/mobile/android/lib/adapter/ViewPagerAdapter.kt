package com.starlight.mobile.android.lib.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.ViewGroup

import java.util.ArrayList

/**主页ViewPager适配器
 * @author raleighluo
 */
class ViewPagerAdapter : FragmentPagerAdapter {
    private var fragmentList: MutableList<Fragment>
    var currentFragment: Fragment? = null
        private set

    private var context: Context? = null

    constructor(context: Context, fm: FragmentManager, fragment: Fragment) : super(fm) {
        fragmentList = ArrayList()
        fragmentList.add(fragment)
        this.context = context
    }

    constructor(context: Context, fm: FragmentManager, fragmentlist: MutableList<Fragment>) : super(fm) {
        this.fragmentList = fragmentlist
        this.context = context
    }

    fun addFragment(fm: FragmentManager, fragment: Fragment) {
        fragmentList.add(fragment)
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Fragment? {
        var fragment:Fragment?= fragmentList.let {
             if(it.size==0) null else it[position]
        }
        return fragment
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun setPrimaryItem(container: ViewGroup?, position: Int, `object`: Any) {
        currentFragment = `object` as Fragment
        super.setPrimaryItem(container, position, `object`)
    }

}

