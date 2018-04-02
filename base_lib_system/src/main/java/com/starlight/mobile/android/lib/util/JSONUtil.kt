package com.starlight.mobile.android.lib.util

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.net.URLDecoder
import java.net.URLEncoder
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.UUID

object JSONUtil {
    /**
     * 根据不同集合对象返回JSON对象
     *
     * @param obj
     * 集合对象
     * @return json对象
     */
    fun getJsObject(obj: Any?): Any {
        return if (obj is Map<*, *>) {
            getJsMap(obj)
        } else if (obj is Collection<*>) {
            getJsCollection(obj)
        } else {
            getJsValue(obj)
        }
    }

    /**
     * 将Map对象转换为JSON对象的方法
     *
     * @param map
     * Map对象
     * @return JSON对象
     */
    fun getJsMap(map: Map<*, *>): Any {
        val buf = StringBuffer()
        buf.append("{")
        val iter = map.entries.iterator()
        if (iter.hasNext()) {
            val ety = iter.next() as java.util.Map.Entry<*, *>
            buf.append(getJsValue(ety.key))
            buf.append(":")
            buf.append(getJsObject(ety.value))
        }
        while (iter.hasNext()) {
            val ety = iter.next() as java.util.Map.Entry<*, *>
            buf.append(",")
            buf.append(getJsValue(ety.key))
            buf.append(":")
            buf.append(getJsObject(ety.value))
        }
        buf.append("}")
        return buf
    }

    /**
     * 根据集合对象获取JSON对象
     *
     * @param list
     * 集合对象
     * @return JSON对象
     */
    fun getJsCollection(list: Collection<*>): Any {
        val buf = StringBuffer()
        buf.append("[")
        val iter = list.iterator()
        if (iter.hasNext()) {
            buf.append(getJsObject(iter.next()))
        }
        while (iter.hasNext()) {
            buf.append(",")
            buf.append(getJsObject(iter.next()))
        }
        buf.append("]")
        return buf
    }

    fun getJsString(obj: Any?): String {
        var obj = obj
        if (obj == null) {
            obj = ""
        }
        return obj.toString().replace("\\\\".toRegex(), "\\\\\\\\")
                .replace("'".toRegex(), "\\\\\'")
    }

    fun getJsValue(objValue: Any?): Any {
        val buf = StringBuffer()
        buf.append("'")
        buf.append(getJsString(objValue))
        buf.append("'")
        return buf
    }

    /**
     * 从JSONObject中获取JSON数组对象
     *
     * @param jsonObject
     * JSON对象
     * @param name
     * 数组的key
     */
    fun getJSONArray(jsonObject: JSONObject, name: String): JSONArray? {
        var array: JSONArray? = null
        val obj = getJSONObjectValue(jsonObject, name)
        try {
            array = JSONArray(obj.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return array
    }

    /**
     * 根据Map集合创建JSON对象
     *
     * @param map
     * Map集合
     * @return JSON对象
     */
    fun getJSONObject(map: Map<*, *>): JSONObject {
        return JSONObject(map)
    }

    /**
     * 根据Collection实现类创建JSON数组
     *
     * @param collection
     * 实现了Collection接口的集合类
     * @return JSON数组
     */
    fun getJSONArray(collection: Collection<*>): JSONArray {
        return JSONArray(collection)
    }

    /**
     * 根据JSON格式的字符串构建JSON对象
     *
     * @param jsonStr
     * JSON格式字符串
     * @return JSON对象
     */
    fun getJSONObject(jsonStr: String): JSONObject {
        var `object` = JSONObject()
        try {
            `object` = JSONObject(jsonStr)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return `object`
    }

    /**
     * 根据属性名称从JSON对象中取出值
     *
     * @param jsonObject
     * JSON对象
     * @param name
     * 属性名称
     * @return Object对象（有可能是单个值，有可能是JSONObject，也有可能是JSONArray）
     */
    fun getJSONObjectValue(jsonObject: JSONObject, name: String): Any {
        var `object` = Any()
        try {
            `object` = jsonObject.get(name)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return `object`
    }

    fun getJSONObjectStringValue(jsonObject: JSONObject, name: String): String {
        var `object` = ""
        try {
            if (jsonObject.has(name)) `object` = jsonObject.getString(name)
        } catch (e: JSONException) {
        }

        return `object`
    }

    /**
     * 根据属性名称从JSON对象中取出值
     *
     * @param jsonObject
     * JSON对象
     * @param name
     * 属性名称
     * @return Object对象（有可能是单个值，有可能是JSONObject，也有可能是JSONArray）
     */
    fun getJSONValue(jsonObject: JSONObject?, name: String): String? {
        var `object`: String? = null
        try {
            if (jsonObject != null && jsonObject.has(name)) {
                `object` = jsonObject.get(name).toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return `object`
    }

    /**
     * 根据JSON对象的属性名称从JSON中取出JSON对象
     *
     * @param jsonObject
     * JSON对象
     * @param name
     * 属性名称
     * @return JSON对象
     */
    fun getJSONObject(jsonObject: JSONObject, name: String): JSONObject {
        var `object` = JSONObject()
        try {
            `object` = JSONObject(jsonObject.get(name).toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return `object`
    }

    /**
     * 根据下标从JSONArray中取出JSON对象
     *
     * @param array
     * JSON数组
     * @param index
     * 下载
     * @return JSON对象
     */
    fun getJSONObjectByIndex(array: JSONArray, index: Int): JSONObject {
        var jsonObject = JSONObject()
        try {
            jsonObject = array.getJSONObject(index)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonObject
    }

    /**
     * 试图将String转换为 JSONArray
     *
     * @param sString
     * 符合json格式的string
     * @return JSONArray
     */
    fun getJSONArray(sString: String?): JSONArray? {
        var mArray: JSONArray? = null
        try {
            if (sString != null && sString.trim { it <= ' ' }.length > 0)
                mArray = JSONArray(sString)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return mArray
    }

    /**
     * 随机获得惟一资源ID
     *
     * @return 惟一资源ID
     */
    fun generateRandomBasedUUID(): String {
        return UUID.randomUUID().toString()
    }


}
