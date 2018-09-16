package com.gkzxhn.prison.presenter

import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Build
import android.widget.Toast
import com.android.volley.VolleyError
import com.gkzxhn.prison.R
import com.gkzxhn.prison.async.AsynHelper
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.common.GKApplication
import com.gkzxhn.prison.model.IBaseModel
import com.gkzxhn.prison.view.IBaseView
import com.starlight.mobile.android.lib.util.HttpStatus
import java.lang.ref.WeakReference
import java.util.concurrent.Executors


/**
 * Created by Raleigh on 15/11/13.
 */
open class BasePresenter<M : IBaseModel, V : IBaseView>(context: Context?, protected val mModel: M, view: V?) {
    protected var mWeakContext: WeakReference<Context>? = null //弱引用Context
    protected val UNAUTHCODE = "401"
    protected val PAGE_SIZE = 100//分页 每页数量
    protected val FIRST_PAGE = 1//分页 起始页
    protected var currentPage = FIRST_PAGE //分页 当前页面
    protected var mWeakView: WeakReference<V>? = null//弱引用 view
    protected var asynHelper: AsynHelper? = null //异步解析数据

    init {
        context?.let {
            mWeakContext = WeakReference(it)
        }
        view?.let {
            mWeakView = WeakReference(it)
        }
    }

    fun getSharedPreferences(): SharedPreferences {
        return mModel.sharedPreferences
    }

    fun getMeettingAccount(): String? {
        val roomNum = getSharedPreferences().getString(Constants.TERMINAL_ROOM_NUMBER, null)
        if (roomNum == null || roomNum.isEmpty()) {
            return null
        } else {
            return String.format("%s##%s##%s", getSharedPreferences().getString(Constants.TERMINAL_ROOM_NUMBER, "")
                    , getSharedPreferences().getString(Constants.TERMINAL_HOST_PASSWORD, ""),
                    getSharedPreferences().getString(Constants.TERMINAL_GUEST_PASSWORD, ""))
        }
    }

    protected fun unauthorized() {//getRooms has expired
        if (mContext != null && getSharedPreferences().getBoolean(Constants.USER_IS_UNAUTHORIZED, false) == false) {
            Toast.makeText(GKApplication.instance.applicationContext, R.string.user_not_authorized, Toast.LENGTH_SHORT).show()
            GKApplication.instance.loginOff()
            //清除别名
        }
    }

    /**
     * 启动异步任务
     *
     * @param params
     */
    fun startAsynTask(TAB: Int, taskFinishedListener: AsynHelper.TaskFinishedListener?, vararg params: Any) {
        try {
            asynHelper?.let {
                if (asynHelper?.status == AsyncTask.Status.RUNNING) asynHelper?.cancel(true)
            }
            asynHelper = null
            asynHelper = AsynHelper(TAB)
            if (taskFinishedListener != null) {
                asynHelper?.setOnTaskFinishedListener(taskFinishedListener)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                asynHelper?.executeOnExecutor(Executors.newCachedThreadPool(), *params)
            } else {
                asynHelper?.execute(*params)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    open fun onDestory() {
        asynHelper?.let {
            if (it.status == AsyncTask.Status.RUNNING) it.cancel(true)
        }
        asynHelper = null
        mModel.stopAllRequest()
    }

    var mView: V? = null
        get() = mWeakView?.get()

    var mContext: Context? = null
        get() = mWeakContext?.get()

    /**
     * 错误提示
     */
    protected fun showErrors(error: VolleyError) {
        var code = -1
        try {
            code = error.networkResponse.statusCode
            when (code) {
                HttpStatus.SC_REQUEST_TIMEOUT -> {//请求超时
                    stopAnim()
                    mView?.showToast(R.string.request_timeout_with_try)
                }
                HttpStatus.SC_BAD_REQUEST, HttpStatus.SC_BAD_GATEWAY, HttpStatus.SC_SERVICE_UNAVAILABLE,
                HttpStatus.SC_INTERNAL_SERVER_ERROR -> {//服务器错误
                    stopAnim()
                    mView?.showToast(R.string.service_not_available)
                }
                HttpStatus.SC_UNAUTHORIZED, HttpStatus.SC_PROXY_AUTHENTICATION_REQUIRED -> {
                    //401未授权
                    stopAnim()
                    unauthorized()
                }
                else -> {//其他
                    stopAnim()
                    mView?.showToast(R.string.unexpected_errors)
                }
            }
        } catch (e: Exception) {
            stopAnim()
            mView?.showToast(R.string.request_timeout_with_try)
        }
    }

    protected open fun stopAnim() {
        mView?.stopRefreshAnim()
        //自动化测试监听使用
        mView?.setIdleNow(true)
    }

}
