package com.gkzxhn.prison.view

/**
 * Created by Raleigh.Luo on 17/4/10.
 */

interface ILoginView : IBaseView {
    fun onSuccess()
    //网络状态
    fun networkStatus(isConnected: Boolean)
}
