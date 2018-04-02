package com.starlight.mobile.android.lib.util

import android.app.Activity
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import android.util.Base64
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager

import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.StringReader

object CommonHelper {
    /**
     * Check the double value is equal 0
     * @param arg1 double value
     * @return  value
     */
    fun isDoubleEqualZero(arg1: Double): Boolean {
        var result = false
        if (Math.abs(arg1 - 0) < 0.0005) {
            result = true
        }
        return result
    }

    /**
     * 创建文件
     *
     * 如果是/sdcard/download/123.doc则只需传入filePath=download/123.doc
     *
     * @param filePath
     * 文件路径
     * @return 创建文件的路径
     * @throws IOException
     */
    @Throws(IOException::class)
    fun creatFileToSDCard(filePath: String): String {
        // 无论传入什么值 都是从根目录开始 即/sdcard/+filePath
        // 创建文件路径包含的文件夹
        var sdPath = ""
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            sdPath = Environment.getExternalStorageDirectory().toString() + "/"
        }
        var dir = ""
        if (filePath.startsWith(sdPath)) {
            dir = filePath.replace(getFileName(filePath), "")
        } else {
            dir = sdPath + filePath.replace(getFileName(filePath), "")
        }
        val filedir = creatDirToSDCard(dir)
        val fileFinalPath = filedir + getFileName(filePath)
        val file = File(fileFinalPath)
        if (!file.exists()) {
            file.createNewFile()
        }
        return fileFinalPath
    }

    /**
     * 获取文件名
     *
     * @param filePath
     * @return
     */
    private fun getFileName(filePath: String): String {
        var index = 0
        var tempName = ""
        index = filePath.lastIndexOf("/")
        if (index != -1) {
            // 如果有后缀名才
            tempName = filePath.substring(index + 1)
            index = filePath.lastIndexOf("/")
        }
        return if (tempName.contains(".")) tempName else ""
    }

    /**
     * 创建文件夹
     *
     * @param dirPath
     */
    fun creatDirToSDCard(dirPath: String): String {
        val file = File(dirPath)
        if (!file.exists()) {
            file.mkdirs()
        }
        return dirPath
    }


    fun isBackground(context: Context): Boolean {
        val activityManager = context
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager
                .runningAppProcesses
        for (appProcess in appProcesses) {
            if (appProcess.processName == context.packageName) {
                /*
				BACKGROUND=400 EMPTY=500 FOREGROUND=100
				GONE=1000 PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
				 */
                //				Log.i(context.getPackageName(), "此appimportace ="
                //						+ appProcess.importance
                //						+ ",context.getClass().getName()="
                //						+ context.getClass().getName());
                return appProcess.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND
            }
        }
        return false
    }

    /**关闭虚拟键盘
     * @param  activity
     */
    fun clapseSoftInputMethod(activity: Activity) {

        try {//activity
            val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (inputMethodManager.isActive)
            //键盘是打开的状态
                inputMethodManager.hideSoftInputFromWindow(activity.currentFocus.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        } catch (e: Exception) {
        }

    }

    /**
     * 显示软键盘
     */
    fun showSoftInputMethod(context: Context, v: View?) {
        try {
             v?.let {
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(it, 0)
            }
        } catch (e: Exception) {
        }

    }

    /**虚拟键盘是否已经打开
     * @param activity
     */
    fun softInputIsOpened(activity: Activity): Boolean {
        val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return inputMethodManager.isActive//键盘是打开的状态
    }

    /**
     * @Description 判断存储卡是否存在
     * @return
     */
    fun checkSDCard(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    }


    /**
     * 写入图片到sdcard
     *
     * @param b
     * @param destDirStr
     * @param file
     */
    fun writeFile(b: ByteArray?, destDirStr: String, file: String) {
        var b = b
        // 获取扩展SD卡设备状态
        val sDStateString = Environment.getExternalStorageState()
        var myFile: File? = null
        // 拥有可读可写权限
        if (sDStateString == Environment.MEDIA_MOUNTED) {
            try {
                // 获取扩展存储设备的文件目录
                val SDFile = Environment
                        .getExternalStorageDirectory()
                val destDir = File(SDFile.absolutePath
                        + File.separator + destDirStr)
                if (!destDir.exists())
                    destDir.mkdirs()
                // 打开文件
                myFile = File(destDir.toString() + File.separator + file)
                // 判断是否存在,不存在则创建
                if (!myFile.exists()) {
                    myFile.createNewFile()
                } else {
                    myFile.delete()
                }
                // 写数据
                var outputStream: FileOutputStream? = FileOutputStream(myFile)
                outputStream?.write(b)
                outputStream?.flush()
                outputStream?.close()
                outputStream = null
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                b = null
            }
        }
    }

    /**
     * 直接将字符串文本写入到文件中
     *
     * @param content
     * 文本文件
     * @param destDirStr
     * 目标目录
     * @param file
     */
    fun writeFile(content: String, destDirStr: String, file: String) {
        // 获取扩展SD卡设备状态
        val sDStateString = Environment.getExternalStorageState()
        var myFile: File? = null
        var bufferedReader: BufferedReader? = null
        var bufferedWriter: BufferedWriter? = null

        // 拥有可读可写权限
        if (sDStateString == Environment.MEDIA_MOUNTED) {
            try {
                // 获取扩展存储设备的文件目录
                val SDFile = Environment
                        .getExternalStorageDirectory()
                val destDir = File(SDFile.absolutePath
                        + File.separator + destDirStr)
                if (!destDir.exists())
                    destDir.mkdirs()
                // 打开文件
                myFile = File(destDir.toString() + File.separator + file)
                // 判断是否存在,不存在则创建
                if (!myFile.exists()) {
                    myFile.createNewFile()
                } else {
                    myFile.delete()
                }
                bufferedReader = BufferedReader(StringReader(content))
                val writerStream = FileOutputStream(
                        myFile)
                bufferedWriter = BufferedWriter(
                        java.io.OutputStreamWriter(writerStream, "utf-8"))
                val buf = CharArray(1024) // 字符缓冲区
                var len: Int
                len = bufferedReader.read(buf)
                while (len != -1) {
                    bufferedWriter.write(buf, 0, len)
                    len = bufferedReader.read(buf)
                }
                bufferedWriter.flush()
                bufferedReader.close()
                bufferedWriter.close()
                bufferedReader = null
                bufferedWriter = null
                // 写数据
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    /**
     * 写入图片到sdcard
     *
     * @param b
     * @param destDirStr
     * @param file
     */
    fun writeFileAppend(b: ByteArray, fileFullPath: String,
                        isAppend: Boolean) {
        // 获取扩展SD卡设备状态
        val sDStateString = Environment.getExternalStorageState()
        var myFile: File? = null
        // 拥有可读可写权限
        if (sDStateString == Environment.MEDIA_MOUNTED) {
            try {
                // 获取扩展存储设备的文件目录
                val fileDir = fileFullPath.substring(0,
                        fileFullPath.lastIndexOf("/") + 1)
                val dir = File(fileDir)
                if (!dir.exists()) {
                    dir.mkdirs()
                }
                // 打开文件
                myFile = File(fileFullPath)
                // 判断是否存在,不存在则创建
                if (!myFile.exists()) {
                    myFile.createNewFile()
                }
                // 写数据
                val outputStream = FileOutputStream(myFile,
                        isAppend)
                outputStream.write(b)
                outputStream.flush()
                outputStream.close()
            } catch (e: Exception) {
                //				CommonHelper.log("写入文件" + fileFullPath + "出错");
                e.printStackTrace()
            }

        } else {
            //			CommonHelper.log("写入文件" + fileFullPath + "无权限");
        }
    }

    fun writeSegmentFile(context: Context, b: ByteArray,
                         fileFullPath: String, fileFullLen: Long) {
        // 获取扩展SD卡设备状态
        val sDStateString = Environment.getExternalStorageState()
        var myFile: File? = null
        // 拥有可读可写权限
        if (sDStateString == Environment.MEDIA_MOUNTED) {
            try {
                // 获取扩展存储设备的文件目录
                val fileDir = fileFullPath.substring(0,
                        fileFullPath.lastIndexOf("/") + 1)
                val dir = File(fileDir)
                if (!dir.exists()) {
                    dir.mkdirs()
                }
                var isAppend = false
                myFile = File(fileFullPath)
                if (myFile.exists()) {
                    isAppend = true
                } else {
                    myFile.createNewFile()
                }
                myFile.let{
                    val outputStream = FileOutputStream(
                            myFile, isAppend)
                    outputStream.write(b)
                    outputStream.flush()
                    outputStream.close()
                    // xml存储完整文件大小
                    writeFileSize(context, fileFullPath, fileFullLen)
                }
            } catch (e: Exception) {
                //				CommonHelper.log("写入文件" + fileFullPath + "出错");
                e.printStackTrace()
            }

        } else {
            //			CommonHelper.log("写入文件" + fileFullPath + "无权限");

        }
    }

    fun writeFileSize(c: Context, key: String, value: Long): Boolean {
        val settings = c.getSharedPreferences("file",
                Context.MODE_PRIVATE)
        val editor = settings.edit()
        editor.putLong(key, value)
        return editor.commit()
    }

    fun readFileSize(c: Context, key: String): Long {
        val settings = c.getSharedPreferences("file",
                Context.MODE_PRIVATE)
        return settings.getLong(key, -1)
    }

    /**
     * 删除所有文件，目录或者文件都适用，递归删除所有层级的文件或者文件夹
     *
     * @param file
     * @throws Exception
     */
    @Throws(Exception::class)
    fun deleteAllFile(file: File) {
        if (file.exists()) {
            if (file.isFile) {
                // 判断是否是文件
                file.delete()
            } else if (file.isDirectory) {
                // 否则如果它是一个目录
                val files = file.listFiles()
                for (i in files.indices) {
                    // 遍历目录下所有的文件
                    deleteAllFile(files[i])
                }
            }
            file.delete()
        } else {
            throw Exception("要删除的文件" + file + "不存在！")
        }
    }

    /**
     * 根据图片路径读取字节流
     *
     * @param filePath
     * 文件路径
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun getFileByte(filePath: String): ByteArray {
        // 获取扩展SD卡设备状态
        val sDStateString = Environment.getExternalStorageState()
        var myFile: File? = null
        // 拥有可读可写权限
        return if (sDStateString == Environment.MEDIA_MOUNTED) {
            try {
                // 打开文件
                myFile = File(filePath)
                // 判断是否存在,不存在则创建
                if (!myFile.exists()) {
                    throw Exception("image file not found!")
                }
                val len = myFile.length()
                val bytes = ByteArray(len.toInt())

                val bufferedInputStream = BufferedInputStream(
                        FileInputStream(myFile))
                val r = bufferedInputStream.read(bytes)
                bufferedInputStream.close()
                if (r.toLong() != len) {
                    throw IOException("error read image file")
                }
                bytes

            } catch (e: Exception) {
                throw e
            }

        } else {
            throw Exception("没有读写权限")
        }
    }

    /**
     * 获取文件夹下面的所以文件
     *
     * @param directory
     * @param lstFiles
     */
    fun getAllFiles(directory: File, lstFiles: MutableList<File>) {
        val files = directory.listFiles()
        files?.let {
            for (f in files) {
                if (f.isDirectory) {
                    lstFiles.add(f)
                    getAllFiles(f, lstFiles)
                } else {
                    lstFiles.add(f)
                }
            }
        }
    }
    /**
     * voice to String
     *
     * @param filePath
     * file path
     * @return BASE64 coded
     */
    fun voiceToString(filePath: String): String {
        val file = File(filePath)
        val buffer = ByteArray(file.length().toInt())
        try {
            val inputFile = FileInputStream(file)
            inputFile.read(buffer)
            inputFile.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return Base64.encodeToString(buffer, Base64.DEFAULT)
    }

    private fun px2dip(pxValue: Float, context: Context): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }
    fun getElementSzie(context: Context?): Int {
        context?.let {
            val dm = context.resources.displayMetrics
            val screenHeight = px2dip(dm.heightPixels.toFloat(), context)
            val screenWidth = px2dip(dm.widthPixels.toFloat(), context)
            var size = screenWidth / 6
            if (screenWidth >= 800) {
                size = 60
            } else if (screenWidth >= 650) {
                size = 55
            } else if (screenWidth >= 600) {
                size = 50
            } else if (screenHeight <= 400) {
                size = 20
            } else if (screenHeight <= 480) {
                size = 25
            } else if (screenHeight <= 520) {
                size = 30
            } else if (screenHeight <= 570) {
                size = 35
            } else if (screenHeight <= 640) {
                if (dm.heightPixels <= 960) {
                    size = 35
                } else if (dm.heightPixels <= 1000) {
                    size = 45
                }
            }
            return size
        }
        return 40
    }

    fun getImageMessageItemMinWidth(context: Context): Int {
        return getElementSzie(context) * 3
    }

    fun getImageMessageItemMinHeight(context: Context): Int {
        return getElementSzie(context) * 3
    }

    fun getImageMessageItemDefaultWidth(context: Context): Int {
        return getElementSzie(context) * 5
    }

    fun getImageMessageItemDefaultHeight(context: Context): Int {
        return getElementSzie(context) * 7
    }
}
