package com.gkzxhn.prison.view

import com.gkzxhn.prison.entity.VersionEntity

/**
 * Created by Raleigh.Luo on 17/4/10.
 */

interface ILoginView : IBaseView {
    fun onSuccess()
    //网络状态
    fun networkStatus(isConnected: Boolean)
    fun updateVersion(version: VersionEntity)
}
