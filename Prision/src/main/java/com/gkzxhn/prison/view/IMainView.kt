package com.gkzxhn.prison.view

import com.gkzxhn.prison.entity.MeetingEntity
import com.gkzxhn.prison.entity.VersionEntity

/**
 * Created by Raleigh.Luo on 17/4/12.
 */

interface IMainView : IBaseView {
    fun showProgress()
    fun dismissProgress()
    fun updateItems(datas: List<MeetingEntity>?)
    fun onCanceled()
    fun updateVersion(version: VersionEntity)
    fun startZijingService()
    fun zijingServiceFailed()
}
