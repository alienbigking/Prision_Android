package com.gkzxhn.prison.view

/**
 * Created by Raleigh.Luo on 17/4/10.
 */

interface IBaseView {
    fun startRefreshAnim()
    fun stopRefreshAnim()
    fun showToast(testResId: Int)
    fun showToast(showText: String)
    fun setIdleNow(isIdleNow: Boolean)
}
