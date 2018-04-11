package com.gkzxhn.prison.view

import com.gkzxhn.prison.entity.FreeFamilyEntity

/**
 * Created by Raleigh.Luo on 18/3/29.
 */

interface ICallFreeView : IBaseView {
    fun onSuccess(datas:List<FreeFamilyEntity>?)
    fun updateFreeTime(time: Int)
}
