package com.gkzxhn.prison.view

import com.gkzxhn.prison.entity.VersionEntity

/**
 * Created by Raleigh.Luo on 18/3/28.
 */

interface ISettingView : IBaseView {
    fun updateVersion(version: VersionEntity)
    fun updateFreeTime(time: Int)
}
