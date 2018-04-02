package com.starlight.mobile.android.lib.util

import android.content.Context
import com.starlight.mobile.android.lib.R
import java.net.URI
import java.net.URL
import java.net.URLDecoder
import java.net.URLEncoder
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import java.util.regex.Pattern


/**日期转换--工具类
 *
 * yyyy/MM/dd HH:mm:ss
 *
 *
 * @author raleigh
 */
object ConvertUtil {

    /**
     * 获取当前系统时间
     * @return  long型的毫秒数
     */
    val currentTime: Long
        get() {
            val data = Date()
            return data.time
        }
    /**
     *
     * @return 当前系统时间：格式为yyyy-MM-dd HH:mm:ss
     */
    val currentTimeFormate: String
        get() {
            val dataFormat = SimpleDateFormat(
                    "yyyy/MM/dd HH:mm:ss")
            return dataFormat.format(Date())
        }

    /**毫秒数转为date,有指定格式
     * @param dateTime 时间毫秒数
     * @param formate，日期格式如yyyy/MM/dd，默认为yyyy/MM/dd HH:mm:ss
     * @return
     */
    fun timeInMillisToDateFormate(timeInMillis: Long, formate: String="yyyy/MM/dd HH:mm:ss"): String {
        var result = ""
        try {
            val date = Date(timeInMillis)
            val df = SimpleDateFormat(formate)
            result = df.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    /**
     * Convert String date to Date
     * @param date date for String format
     * @return Date object
     */
    fun stringToDate(date: String,formate: String="yyyy/MM/dd HH:mm:ss"): Date? {
        var dateTime: Date? = null
        try {
            val df = SimpleDateFormat(formate)
            dateTime = df.parse(date)
        } catch (e: Exception) {
        }

        return dateTime
    }

    /**获取当前系统格式 时分秒
     * @param timeInMillis 时间毫秒数
     * @return 格式 （下午）06:09:08
     */
    fun getSystemTimeFormat(timeInMillis: Long): String {
        var result = ""
        try {
            val date = Date(timeInMillis)
            val df = DateFormat.getTimeInstance()
            result = df.format(date)//06:09:08
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    /**获取当前系统格式 时分
     * @param timeInMillis 时间毫秒数
     * @return 格式 （下午）06:09
     */
    fun getSystemShortTimeFormat(timeInMillis: Long): String {
        var result = ""
        try {
            val date = Date(timeInMillis)
            val df = DateFormat.getTimeInstance(DateFormat.SHORT)
            result = df.format(date)//(上下午)06:09:08
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    /**获取系统日期格式  年月日时分秒
     * @param timeInMillis 时间毫秒数
     * @return 格式2014年09月08日 （下午）06:09:08
     */
    fun getSystemLongDateFormat(timeInMillis: Long): String {
        var result = ""
        try {
            val date = Date(timeInMillis)
            val df = DateFormat.getDateTimeInstance()
            result = df.format(date)//格式2014年09月08日 （下午）06:09:08
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    /**获取系统日期格式 年月日时分
     * @param timeInMillis 时间毫秒数
     * @return 格式2014年09月08日 （下午）06:09，不是本年返回9月8日（下午）06:09
     */
    fun getSystemDateFormat(timeInMillis: Long): String {
        var result = ""
        try {
            val date = Date(timeInMillis)
            val df = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT)

            result = df.format(date)//格式2014年09月08日 06:09:08
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }


    /**获取系统日期格式 年月日
     * @param  timeInMillis 时间毫秒数
     * @return 格式2014年9月8日,不是本年返回9月8日
     */
    fun getSystemShortDateFormat(timeInMillis: Long): String {
        var result = ""
        try {
            val date = Date(timeInMillis)
            val df = DateFormat.getDateInstance()
            result = df.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    /**与当前时间比较大小， 并返回相差的天数（不比较时分秒比较）
     * @param timeInMillis 时间毫秒数
     * @return 相差的天数，小于0：小于当前时间，0等于当前时间，大于0 大于当前时间，
     */
    fun compareWithCurDate(timeInMillis: Long): Int {
        var result = -1
        try {
            val mCal = Calendar.getInstance()
            mCal.timeInMillis = timeInMillis
            val nowCal = Calendar.getInstance()
            if (mCal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR)) {//同年
                result = mCal.get(Calendar.DAY_OF_YEAR) - nowCal.get(Calendar.DAY_OF_YEAR)
            } else {//上一年，小于今年
                val intervalMilli = mCal.timeInMillis - nowCal.timeInMillis
                val divider = (1000 * 60 * 60 * 24).toLong()
                result = Math.ceil((intervalMilli / divider).toDouble()).toInt()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result

    }

    /**与当前时间比较大小， 并返回相差的分钟(不比较秒数)
     * @param timeInMillis 时间毫秒数
     * @return 小于0：小于当前时间，0等于当前时间，大于0 大于当前时间，
     */
    fun compareWithCurTime(timeInMillis: Long): Long {
        var result: Long = -1
        try {
            val mCal = Calendar.getInstance()
            mCal.timeInMillis = timeInMillis
            val nowCal = Calendar.getInstance()
            val intervalMilli = mCal.timeInMillis - nowCal.timeInMillis
            //向上取整
            result = Math.ceil((intervalMilli / (60 * 1000)).toDouble()).toLong()
            //一天毫秒数（24*60*60*1000）

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result

    }

    /**与当前时间比较大小， 并返回相差的小时(不比较秒数)
     * @param timeInMillis 时间毫秒数
     * @return 小于0：小于当前时间，0等于当前时间，大于0 大于当前时间，
     */
    fun compareWithCurTimeForHour(timeInMillis: Long): Int {
        var result = -1
        try {
            val mCal = Calendar.getInstance()
            mCal.timeInMillis = timeInMillis
            val nowCal = Calendar.getInstance()
            val intervalMilli = mCal.timeInMillis - nowCal.timeInMillis
            //向上取整
            result = Math.ceil((intervalMilli / (60 * 60 * 1000)).toDouble()).toInt()
            //一天毫秒数（24*60*60*1000）

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result

    }

    /**时间比较大小， 并返回相差的分钟(不比较秒数)
     * @param timeInMillis 时间毫秒数
     * @return timeInMillis2 大于 timeInMillis1 返回正数，否则返回负数，等于返回0
     */
    fun compareTime(timeInMillis1: Long, timeInMillis2: Long): Int {
        var result = -1
        try {
            val mCal1 = Calendar.getInstance()
            mCal1.timeInMillis = timeInMillis1
            val mCal2 = Calendar.getInstance()
            mCal2.timeInMillis = timeInMillis2
            val intervalMilli = mCal2.timeInMillis - mCal1.timeInMillis
            //向上取整
            result = Math.ceil((intervalMilli / (60 * 1000)).toDouble()).toInt()
            //一天毫秒数（24*60*60*1000）

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result

    }

    /**比较日期相差的天数
     * @param timeInMillis1
     * @param timeInMillis2
     * @return 相差的天数，timeInMillis2 大于 timeInMillis1  返回正数，否则返回负数，等于返回0
     */
    fun compareDate(timeInMillis1: Long, timeInMillis2: Long): Int {
        var result = -1
        try {
            val mCal1 = Calendar.getInstance()
            mCal1.timeInMillis = timeInMillis1
            val mCal2 = Calendar.getInstance()
            mCal2.timeInMillis = timeInMillis2

            if (mCal2.get(Calendar.YEAR) == mCal1.get(Calendar.YEAR)) {//同年
                result = mCal2.get(Calendar.DAY_OF_YEAR) - mCal1.get(Calendar.DAY_OF_YEAR)
            } else if (mCal2.get(Calendar.YEAR) > mCal1.get(Calendar.YEAR)) {//下一年，大于今年
                //今年的最后一天
                val thisYearLastDay = mCal1.getActualMaximum(Calendar.DAY_OF_YEAR)
                //今年剩余的天数+下一年的年天数
                result = thisYearLastDay - mCal1.get(Calendar.DAY_OF_YEAR) + mCal2.get(Calendar.DAY_OF_YEAR)
            } else {//上一年，小于今年
                //上一年的最后一天
                val lastYearLastDay = mCal2.getActualMaximum(Calendar.DAY_OF_YEAR)
                //上一年剩余的天数+今年的年天数
                result = -(lastYearLastDay - mCal2.get(Calendar.DAY_OF_YEAR) + mCal1.get(Calendar.DAY_OF_YEAR))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result

    }

    /**比较时分，时间格式HH:mm
     * @param timeStr
     * @return timeStr1 小于 timeStr2返回 true
     */
    fun compareShortTime(timeStr1: String, timeStr2: String): Boolean {
        var result = false
        try {
            val df = SimpleDateFormat("HH:mm")
            val time1 = df.parse(timeStr1)
            val time2 = df.parse(timeStr2)
            result = time1.before(time2)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result

    }


    /**比较月日，格式MM月dd日
     * @param timeStr
     * @return timeStr1 小于 timeStr2返回 true
     */
    fun compareShortDate(dateStr1: String, dateStr2: String): Int {
        var result = 0
        try {
            val df = SimpleDateFormat("MM月dd日")
            val time1 = df.parse(dateStr1)
            val time2 = df.parse(dateStr2)
            result = time1.compareTo(time2)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result

    }


    fun getDistanceFromLatLng(latFrom: Double, lngFrom: Double,
                              latTo: Double, lngTo: Double): Double {
        val radLatFrom = getRad(latFrom)
        val radLatTo = getRad(latTo)
        val rad1 = radLatFrom - radLatTo
        val rad2 = getRad(lngFrom) - getRad(lngTo)
        var dis = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(rad1 / 2), 2.0) + Math.cos(radLatFrom) * Math.cos(radLatTo) * Math.pow(Math.sin(rad2 / 2), 2.0)))
        dis = dis * EARTHRADIUS
        dis = (Math.round(dis * 10000) / 10000).toDouble()
        return dis / 1.609
    }

    // radius of earth
    private val EARTHRADIUS = 6378.137

    /**
     * Get radian
     *
     * @param d
     * @return
     */
    private fun getRad(d: Double): Double {
        return d * Math.PI / 180.0
    }

    /**
     * Get json time, the time of the incoming string format for display in the
     * application of this format example："\/Date(1375427338000+0000)\/"
     *
     * @param date
     * @return
     */
    fun getJsonTimeFromDateStr(dateStr: String): String {
        var result = ""
        try {
            val cal = Calendar.getInstance()
            val dateArray = dateStr.substring(0, dateStr.indexOf(" ")).split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val timeStr = dateStr.substring(dateStr.indexOf(" ") + 1)
            val timeArray = timeStr.substring(0, timeStr.indexOf(" ")).split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val quantumStr = timeStr.substring(timeStr.indexOf(" ") + 1)
            var quantum = Calendar.AM
            if (dateStr.indexOf("上午") > 0 || dateStr.indexOf("下午") > 0) {
                quantum = if ("上午" == quantumStr) Calendar.AM else Calendar.PM
            } else {
                quantum = if ("AM" == quantumStr) Calendar.AM else Calendar.PM
            }

            cal.set(Integer.valueOf(dateArray[2])!!,
                    Integer.valueOf(dateArray[0])!! - 1,
                    Integer.valueOf(dateArray[1])!!,
                    Integer.valueOf(timeArray[0])!!,
                    Integer.valueOf(timeArray[1])!!)
            cal.set(Calendar.AM_PM, quantum)

            val date = cal.time
            var utcDate: Long = 0
            val tz = TimeZone.getDefault()
            val tOffset = tz.rawOffset
            val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            utcCal.time = date
            utcCal.add(Calendar.MILLISECOND, -tOffset)
            //			String zoneTime = utcCal.getTime() + "";
            //			int pos = zoneTime.indexOf("+");
            //			if(pos==-1){
            //				pos = zoneTime.indexOf("-");
            //			}
            //			String res = zoneTime.substring(pos, pos + 6);
            //			String lastRes = res.replace(":", "");
            val utcTempDate = utcCal.time
            utcDate = utcTempDate.time
            result = "/Date($utcDate)/"
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    /**
     * Get json time, the time of the incoming string format for display in the
     * application of this format example："\/Date(1375427338000+0000)\/"
     *
     * @param dateTimeStr  "MM/dd/yyyy HH:mm"
     * @return
     */
    fun getJsonTimeFromDateTimeStr(dateTimeStr: String): String {
        var result = ""
        try {
            val cal = Calendar.getInstance()
            val dateArray = dateTimeStr.substring(0, dateTimeStr.indexOf(" ")).split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val timeStr = dateTimeStr.substring(dateTimeStr.indexOf(" ") + 1)
            val timeArray = timeStr.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            cal.set(Integer.valueOf(dateArray[2])!!,
                    Integer.valueOf(dateArray[0])!! - 1,
                    Integer.valueOf(dateArray[1])!!,
                    Integer.valueOf(timeArray[0])!!,
                    Integer.valueOf(timeArray[1])!!)

            val date = cal.time
            var utcDate: Long = 0
            val tz = TimeZone.getDefault()
            val tOffset = tz.rawOffset
            val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            utcCal.time = date
            utcCal.add(Calendar.MILLISECOND, -tOffset)
            val utcTempDate = utcCal.time
            utcDate = utcTempDate.time
            result = "/Date($utcDate)/"
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }




    /**
     * Return date format String MM/dd/yyyy a
     * @param jsonStr json format string
     * @return date format string
     */
    fun getTimeInMilliFormJSONString(jsonStr: String?): Long {
        var timeInMilli: Long = 0
        try {
            jsonStr?.let {
                val JSONDateToMilliseconds = "\\/(Date\\((.*?)(\\+.*)?\\))\\/"
                val pattern = Pattern.compile(JSONDateToMilliseconds)
                val matcher = pattern.matcher(jsonStr)
                val result = matcher.replaceAll("$2")
                if (result != null && "null" != result) {
                    timeInMilli = result as Long
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return timeInMilli
    }


    /**字符串转换为整数
     * @param numberStr
     * @return
     */
    fun strToInt(numberStr: String): Int {
        var result = 0
        try {
            if (isNum(numberStr)) {
                result = numberStr.toInt()
            }
        } catch (e: Exception) {
        }

        return result
    }

    /**字符串转换为double
     * @param numberStr
     * @return
     */
    fun strToDoubleValue(numberStr: String): Double {
        var result = 0.0
        try {
            if (isNum(numberStr))
                result =  numberStr.toDouble()
        } catch (e: Exception) {
        }

        return result
    }
    /**字符串转换为double
     * @param numberStr
     * @return
     */
    fun strToFloatValue(numberStr: String): Float {
        var result = 0f
        try {
            if (isNum(numberStr))
                result = numberStr.toFloat()
        } catch (e: Exception) {
        }

        return result
    }
    /**判断是否为数字 包括正数，负数，小数
     * @param str
     * @return 是纯数字则返回true
     */
    fun isNum(str: String?): Boolean {
        var result = false
        try {
            str?.let{
                val pattern = Pattern.compile("-?[0-9]+.*[0-9]*")
                val isNum = pattern.matcher(str)
                result = isNum.matches()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }


    /**
     * Get utc time from TimeInMillis
     * @param timeInMillis TimeInMillis
     * @return json format
     */
    fun getJSONDateStringFromTimeInMillis(timeInMillis: Long): String {
        val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        utcCal.timeInMillis = timeInMillis
        return String.format("/Date(%d)/", utcCal.timeInMillis)
    }


    /**
     * get month name
     * @param month month name
     */
    fun getMonthName(context: Context, month: Int): String {
        var monthName = R.string.January
        when (month) {
            Calendar.JANUARY -> monthName = R.string.January
            Calendar.FEBRUARY -> monthName = R.string.February
            Calendar.MARCH -> monthName = R.string.March
            Calendar.APRIL -> monthName = R.string.April
            Calendar.MAY -> monthName = R.string.May
            Calendar.JUNE -> monthName = R.string.June
            Calendar.JULY -> monthName = R.string.July
            Calendar.AUGUST -> monthName = R.string.August
            Calendar.SEPTEMBER -> monthName = R.string.September
            Calendar.OCTOBER -> monthName = R.string.October
            Calendar.NOVEMBER -> monthName = R.string.November
            Calendar.DECEMBER -> monthName = R.string.December
            Calendar.UNDECIMBER -> monthName = R.string.Undecimber
        }
        return context.getString(monthName)
    }
    /**
     * get Week name
     * @param month month name
     */
    fun getWeekName(context: Context, week: Int): String {
        var weekName = R.string.Sunday
        when (week) {
            Calendar.SUNDAY -> weekName = R.string.Sunday
            Calendar.MONDAY -> weekName = R.string.Monday
            Calendar.TUESDAY -> weekName = R.string.Tuesday
            Calendar.WEDNESDAY -> weekName = R.string.Wednesday
            Calendar.THURSDAY -> weekName = R.string.Thursday
            Calendar.FRIDAY -> weekName = R.string.Friday
            Calendar.SATURDAY -> weekName = R.string.Saturday
        }
        return context.getString(weekName)
    }

    /**指定日期是否在当前日期的本周内
     * @param date
     * @return
     */
    fun isInNowDateWeek(timeInMillis: Long): Boolean {
        var result = false
        try {
            //特定日期
            val mCal = Calendar.getInstance()
            mCal.firstDayOfWeek = Calendar.MONDAY
            mCal.timeInMillis = timeInMillis
            //当前的日期
            val cal = Calendar.getInstance()
            cal.firstDayOfWeek = Calendar.MONDAY

            val distance = compareWithCurDate(timeInMillis)//不比较时分，只比较年月日
            if (Math.abs(distance) < 7 && cal.get(Calendar.WEDNESDAY) <= mCal.get(Calendar.WEDNESDAY))
                result = true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result

    }
//
//    /**url转码
//     * @param urlStr
//     * @return
//     */
//    fun urlEncoder(urlStr: String): String {
//        var url: URL? = null
//        try {
//            url = URL(urlStr)
//            val uri = URI(url.protocol, url.userInfo, url.host, url.port, url.path, url.query, url.ref)
//            url = uri.toURL()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        return if (url == null) "" else url.toString()
//    }

    /**
     * 对字符串进行编码
     *
     * @param encodeString
     * @return
     */
    fun urlEncode(encodeString: String): String {
        var result = ""
        try {
            result = URLEncoder.encode(encodeString, "UTF-8")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result

    }


    /**
     * 对字符串进行解码
     *
     * @param encodeString
     * @return
     */
    fun urlDecode(decodeString: String): String {
        var result = ""
        try {
            result = URLDecoder.decode(decodeString, "UTF-8")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

}

