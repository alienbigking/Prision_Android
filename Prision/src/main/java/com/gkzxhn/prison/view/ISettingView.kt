package com.gkzxhn.prison.view

import com.gkzxhn.prison.entity.VersionEntity

/**
 * Created by Raleigh.Luo on 18/3/28.
 */

interface ISettingView : IBaseView {
    fun updateVersion(version: VersionEntity?)
    //网络状态
    fun networkStatus(isConnected: Boolean)
}
