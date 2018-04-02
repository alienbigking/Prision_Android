package com.gkzxhn.prison.view

/**
 * Created by Raleigh.Luo on 17/4/13.
 */

interface ICallUserView : IBaseView {
    fun onSuccess()
    fun dialSuccess(password: String)
}
