package com.gkzxhn.prison.view

/**
 * Created by Raleigh.Luo on 18/3/29.
 */

interface ICallFreeView : IBaseView {
    fun onSuccess()
    fun updateFreeTime(time: Int)
}
