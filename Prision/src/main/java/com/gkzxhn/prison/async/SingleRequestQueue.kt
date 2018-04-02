package com.gkzxhn.wisdom.async

import android.content.Context

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.gkzxhn.prison.common.GKApplication


/**
 * Created by Raleigh on 15/8/27.
 * 请求队列
 */
class SingleRequestQueue private constructor() {
    private var mRequestQueue: RequestQueue

    init {
        mRequestQueue = Volley.newRequestQueue(GKApplication.instance)
    }

    /**Add Task to the task queue,And you can  cancel the task by the tag
     * @param req
     * @param tag
     * @param <T>
    </T> */
    fun <T> add(req: Request<T>, tag: String?) {
        tag?.let {
            req.tag = it
        }
        mRequestQueue.add(req)
    }

    /**cancel the task
     * @param tag
     */
    fun cancelAll(tag: String?) {
        tag?.let {
            mRequestQueue.cancelAll(tag)
        }
    }
    //单例
    private object Holder { val INSTANCE = SingleRequestQueue() }
    companion object {
        val instance: SingleRequestQueue by lazy {
            Holder.INSTANCE
        }

    }
}
