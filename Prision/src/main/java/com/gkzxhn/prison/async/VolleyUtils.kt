package com.gkzxhn.wisdom.async

import android.content.Context
import android.os.Build
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray
import org.json.JSONObject

import java.util.HashMap
import com.android.volley.toolbox.Volley
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.common.GKApplication
import com.squareup.okhttp.OkHttpClient


/**
 * Created by Raleigh on 15/6/23.
 * volley请求
 */
class VolleyUtils{
    private val applicationContext: Context

    init {
        applicationContext = GKApplication.instance
    }

    /**
     * GET request
     *
     * @param obj                返回请求类型的实例，不能为null,如，String.class,JSONObject.class,JSONArray.class
     * @param url
     * @param onFinishedListener
     */
    @Throws(AuthFailureError::class)
    operator fun get(obj: Class<*>?, url: String, tag: String?, onFinishedListener: OnFinishedListener<*>?) {
        try {
            if (obj != null && obj == String::class.java) {
                val request = object : StringRequest(url, getStringListener(onFinishedListener as OnFinishedListener<String>), getErrorListener(onFinishedListener)) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        return if (tag == null) HashMap() else cusHeaders
                    }
                }
                request.retryPolicy = DefaultRetryPolicy(Constants.REQUEST_TIMEOUT,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                SingleRequestQueue.instance.add(request, tag)
            } else if (obj != null && obj == JSONObject::class.java) {
                val request = object : JsonObjectRequest(url, null, getJSONObjectListener(onFinishedListener as OnFinishedListener<JSONObject>) , getErrorListener(onFinishedListener)) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        return if (tag == null) HashMap() else cusHeaders
                    }
                }
                request.retryPolicy = DefaultRetryPolicy(Constants.REQUEST_TIMEOUT,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                SingleRequestQueue.instance.add(request, tag)
            } else if (obj != null && obj == JSONArray::class.java) {
                val request = object : JsonArrayRequest(url, getJSONArrayListener(onFinishedListener as OnFinishedListener<JSONArray>), getErrorListener(onFinishedListener)) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        return if (tag == null) HashMap() else cusHeaders
                    }
                }
                request.retryPolicy = DefaultRetryPolicy(Constants.REQUEST_TIMEOUT,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                SingleRequestQueue.instance.add(request, tag)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * POST request
     *
     * @param url
     * @param params             参数，String:Map<String></String>,String> ,JSONObject:JSONObject,JSONArray:JSONArray
     * @param onFinishedListener
     */
    @Throws(AuthFailureError::class)
    fun post(url: String, params: Any, tag: String?, onFinishedListener: OnFinishedListener<*>?) {
        try {
            if (params is JSONObject) {
                val request = object : JsonObjectRequest(Request.Method.POST, url, params, getJSONObjectListener(onFinishedListener as OnFinishedListener<JSONObject>)  , getErrorListener(onFinishedListener)) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        //                        headers.put("Accept", "application/json");
                        return if (tag == null) HashMap() else cusHeaders
                    }

                }
                request.retryPolicy = DefaultRetryPolicy(Constants.REQUEST_TIMEOUT,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                SingleRequestQueue.instance.add(request, tag)
            } else if (params is JSONArray) {
                val request = object : JsonArrayRequest(Request.Method.POST, url, params, getJSONArrayListener(onFinishedListener as OnFinishedListener<JSONArray>) , getErrorListener(onFinishedListener)) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        //                        headers.put("Accept", "application/json");
                        return if (tag == null) HashMap() else cusHeaders
                    }
                }
                request.retryPolicy = DefaultRetryPolicy(Constants.REQUEST_TIMEOUT,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                SingleRequestQueue.instance.add(request, tag)
            } else if (params is Map<*, *>) {
                val request = object : StringRequest(Request.Method.POST, url, getStringListener(onFinishedListener as OnFinishedListener<String>) , getErrorListener(onFinishedListener)) {

                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        return params as Map<String, String>
                    }

                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        return if (tag == null) HashMap() else cusHeaders
                    }
                }
                request.retryPolicy = DefaultRetryPolicy(Constants.REQUEST_TIMEOUT,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                SingleRequestQueue.instance.add(request, tag)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * PUT request
     *
     * @param url
     * @param params
     * @param onFinishedListener
     */
    @Throws(AuthFailureError::class)
    fun put(url: String, params: Any, tag: String?, onFinishedListener: OnFinishedListener<*>?) {
        try {
            if (params is JSONObject) {
                val request = object : JsonObjectRequest(Request.Method.PUT, url, params, getJSONObjectListener(onFinishedListener as OnFinishedListener<JSONObject>)  , getErrorListener(onFinishedListener)) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        //                        headers.put("Accept", "application/json");
                        return if (tag == null) HashMap() else cusHeaders
                    }
                }
                request.retryPolicy = DefaultRetryPolicy(Constants.REQUEST_TIMEOUT,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                SingleRequestQueue.instance.add(request, tag)
            } else if (params is JSONArray) {
                val request = object : JsonArrayRequest(Request.Method.PUT, url, params, getJSONArrayListener(onFinishedListener as OnFinishedListener<JSONArray>)  , getErrorListener(onFinishedListener)) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        //                        headers.put("Accept", "application/json");
                        return if (tag == null) HashMap() else cusHeaders
                    }
                }
                request.retryPolicy = DefaultRetryPolicy(Constants.REQUEST_TIMEOUT,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                SingleRequestQueue.instance.add(request, tag)
            } else if (params is Map<*, *>) {
                val request = object : StringRequest(Request.Method.PUT, url, getStringListener(onFinishedListener as OnFinishedListener<String>)  , getErrorListener(onFinishedListener)) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        return params as Map<String, String>
                    }

                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        return if (tag == null) HashMap() else cusHeaders
                    }
                }
                request.retryPolicy = DefaultRetryPolicy(Constants.REQUEST_TIMEOUT,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                SingleRequestQueue.instance.add(request, tag)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * PUT request
     *
     * @param url
     * @param params
     * @param onFinishedListener
     */
    @Throws(AuthFailureError::class)
    fun patch(url: String, params: Any, tag: String?, onFinishedListener: OnFinishedListener<*>?) {
        try {
            if (params is JSONObject) {
                val request = object : JsonObjectRequest(Request.Method.PATCH, url, params, getJSONObjectListener(onFinishedListener as OnFinishedListener<JSONObject>), getErrorListener(onFinishedListener)) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        //                        headers.put("Accept", "application/json");
                        return if (tag == null) HashMap() else cusHeaders
                    }
                }
                request.retryPolicy = DefaultRetryPolicy(Constants.REQUEST_TIMEOUT,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){//5.0及以上使用volley
                    SingleRequestQueue.instance.add(request, tag)
                }else{//5.0以下使用okhttp
                    Volley.newRequestQueue(GKApplication.instance, OkHttpStack(OkHttpClient())).add(request)
                }
            } else if (params is JSONArray) {
                val request = object : JsonArrayRequest(Request.Method.PATCH, url, params, getJSONArrayListener(onFinishedListener as OnFinishedListener<JSONArray>)  , getErrorListener(onFinishedListener)) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        //                        headers.put("Accept", "application/json");
                        return if (tag == null) HashMap() else cusHeaders
                    }
                }
                request.retryPolicy = DefaultRetryPolicy(Constants.REQUEST_TIMEOUT,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){//5.0及以上使用volley
                    SingleRequestQueue.instance.add(request, tag)
                }else{//5.0以下使用okhttp
                    Volley.newRequestQueue(GKApplication.instance, OkHttpStack(OkHttpClient())).add(request)
                }
            } else if (params is Map<*, *>) {
                val request = object : StringRequest(Request.Method.PATCH, url, getStringListener(onFinishedListener as OnFinishedListener<String>)  , getErrorListener(onFinishedListener)) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        return params as Map<String, String>
                    }

                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        return if (tag == null) HashMap() else cusHeaders
                    }
                }
                request.retryPolicy = DefaultRetryPolicy(Constants.REQUEST_TIMEOUT,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){//5.0及以上使用volley
                    SingleRequestQueue.instance.add(request, tag)
                }else{//5.0以下使用okhttp
                    Volley.newRequestQueue(GKApplication.instance, OkHttpStack(OkHttpClient())).add(request)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * DELETE request
     *
     * @param url
     * @param params
     * @param onFinishedListener
     */
    @Throws(AuthFailureError::class)
    fun delete(url: String, params: Any, tag: String?, onFinishedListener: OnFinishedListener<*>?) {
        try {
            if (params is JSONObject) {
                val request = object : JsonObjectRequest(Request.Method.DELETE, url, params, getJSONObjectListener(onFinishedListener as OnFinishedListener<JSONObject>)   , getErrorListener(onFinishedListener)) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        //                        headers.put("Accept", "application/json");
                        return if (tag == null) HashMap() else cusHeaders
                    }
                }
                //                //超时时间10s,最大重连次数2次
                //                request.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 2, 1.0f));
                request.retryPolicy = DefaultRetryPolicy(Constants.REQUEST_TIMEOUT,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                SingleRequestQueue.instance.add(request, tag)
            } else if (params is JSONArray) {
                val request = object : JsonArrayRequest(Request.Method.DELETE, url, params, getJSONArrayListener(onFinishedListener as OnFinishedListener<JSONArray>) , getErrorListener(onFinishedListener)) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        //                        headers.put("Accept", "application/json");
                        return if (tag == null) HashMap() else cusHeaders
                    }
                }
                request.retryPolicy = DefaultRetryPolicy(Constants.REQUEST_TIMEOUT,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                SingleRequestQueue.instance.add(request, tag)
            } else if (params is Map<*, *>) {
                val request = object : StringRequest(Request.Method.DELETE, url, getStringListener(onFinishedListener as OnFinishedListener<String>)  , getErrorListener(onFinishedListener)) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        return params as Map<String, String>
                    }

                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        return if (tag == null) HashMap() else cusHeaders
                    }
                }
                request.retryPolicy = DefaultRetryPolicy(Constants.REQUEST_TIMEOUT,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                SingleRequestQueue.instance.add(request, tag)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun getStringListener(onFinishedListener: OnFinishedListener<String>?): Response.Listener<String>{
        return Response.Listener<String> { response ->
            try {
                onFinishedListener?.onSuccess(response)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun getJSONObjectListener(onFinishedListener: OnFinishedListener<JSONObject>?): Response.Listener<JSONObject> {
        return Response.Listener<JSONObject> { response ->
            try {
                onFinishedListener?.onSuccess(response)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun getJSONArrayListener(onFinishedListener: OnFinishedListener<JSONArray>?): Response.Listener<JSONArray> {
        return Response.Listener<JSONArray> { response ->
            try {
                onFinishedListener?.onSuccess(response)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getErrorListener(onFinishedListener: OnFinishedListener<*>?): Response.ErrorListener {
        return Response.ErrorListener { error ->
            try {
                onFinishedListener?.onFailed(error)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    interface OnFinishedListener<J> {
        fun onSuccess(response: J)

        fun onFailed(error: VolleyError)
    }


    //TODO
    //            headers.put(HTTP.CONTENT_TYPE, "charset=utf-8");
    //            headers.put("Accept-Language", "zh-cn,zh");
    //            headers.put("MobileType", "Android");
    //            headers.put("DeviceModel", android.os.Build.MODEL);
    //            headers.put("OSVersion", android.os.Build.VERSION.RELEASE);
    //            headers.put("Longitude", String.valueOf(WitsParkApplication.getInstance().getLongitude()));
    //            headers.put("Latitude", String.valueOf(WitsParkApplication.getInstance().getLatitude()));
    //            try {
    //                headers.put("DeviceToken", android.os.Build.SERIAL);
    //            } catch (Exception e) {
    //            }
    //
    //            try {
    //                headers.put("AppVersion", String.valueOf(applicationContext.getPackageManager().getPackageInfo(applicationContext.getPackageName(), 0).versionCode));
    //            } catch (Exception e) {
    //                headers.put("AppVersion", "-1");
    //            }
    //            SharedPreferences preferences=SAASApplication.getInstance().getSharedPreferences(Constants.USER_TABLE, Context.MODE_PRIVATE);
    //            String sessionId = preferences.getString(Constants.USER_SESSION_ID, null);
    //            if (sessionId != null && sessionId.length() > 0) {
    //                headers.put("sessionId", sessionId);
    //            }
    //            String authorizationValue =preferences.getString(Constants.USER_ACCESS_TOKEN, null);
    //            String tokenType= preferences.getString(Constants.USER_TOKEN_TYPE, null);
    //            if (authorizationValue != null && authorizationValue.length() > 0 && tokenType!=null) {
    //                headers.put("Authorization", String.format("%s %s", tokenType, authorizationValue));
    //            }
    //
    //            headers.put("AppName", "SAAS");
    val cusHeaders: Map<String, String>
        get() {
            val headers = HashMap<String, String>()
//            try {
//                val token = applicationContext.getSharedPreferences(Constants.USER_TABLE, Context.MODE_PRIVATE)
//                        .getString(Constants.USER_TOKEN, "")
//                if (token.length > 0)
//                    headers.put("Authorization", token)
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }

            return headers
        }


}
