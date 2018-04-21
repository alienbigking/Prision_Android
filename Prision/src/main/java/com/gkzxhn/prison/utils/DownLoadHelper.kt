package com.gkzxhn.prison.utils

import android.os.AsyncTask
import android.os.Build
import android.os.Handler
import android.os.Message
import android.util.Log

import com.gkzxhn.prison.common.Constants
import com.starlight.mobile.android.lib.util.HttpStatus

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors


/**
 * Created by Raleigh.Luo on 17/3/14.
 */

class DownLoadHelper {
    private var listener: DownloadFinishListener? = null
    private var downloadAsyn: DownloadAsyn? = null
    private var mUrl: String? = null
    private var filePath: String? = null
    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 1) {
                val currentSize = msg.arg1
                val size = msg.arg2
                if (listener != null) listener?.onProgress(currentSize, size)
            }
        }
    }
    private val onAsynFinishListener = object : OnAsynFinishListener {
        override fun onFinish(code: Int) {
            try {
                if (code == HttpStatus.SC_CREATED || code == HttpStatus.SC_OK) {
                    listener?.onSuccess(filePath)
                } else if (code == HttpStatus.SC_REQUEST_TIMEOUT) {
                    startAsynTask()
                } else {
                    listener?.onFailed(null)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                listener?.onFailed(null)
            }

        }

        override fun onProgress(currentSize: Int, totalSize: Int) {
            listener?.onProgress(currentSize, totalSize)
        }
    }


    fun setListener(listener: DownloadFinishListener) {
        this.listener = listener
    }

    fun download(filePath: String) {
        this.mUrl = filePath
        startAsynTask()
    }

    fun onStop() {
        this.listener = null
        if (downloadAsyn != null) {
            if (downloadAsyn?.status == AsyncTask.Status.RUNNING) downloadAsyn?.cancel(true)
            downloadAsyn = null
        }
    }

    /**
     * 鍚姩寮傛浠诲姟
     *
     */
    private fun startAsynTask() {
        try {
            if (downloadAsyn != null) {
                if (downloadAsyn?.status == AsyncTask.Status.RUNNING) downloadAsyn?.cancel(true)
                downloadAsyn = null
            }
            downloadAsyn = DownloadAsyn()
            downloadAsyn?.setOnAsynFinishListener(onAsynFinishListener)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                downloadAsyn?.executeOnExecutor(Executors.newCachedThreadPool())
            } else {
                downloadAsyn?.execute()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    interface DownloadFinishListener {
        fun onSuccess(filePath: String?)
        fun onFailed(error: String?)
        fun onProgress(currentSize: Int, totalSize: Int)
    }

    internal inner class DownloadAsyn : AsyncTask<Void, Int, Int>() {

        private var listener: OnAsynFinishListener? = null
        fun setOnAsynFinishListener(listener: OnAsynFinishListener) {
            this.listener = listener
        }

        override fun doInBackground(vararg params: Void): Int? {
            var responseCode = 0
            val rootFile = File(Constants.CACHE_FILE)
            if (!rootFile.exists()) {
                rootFile.mkdirs()
            }

            val file = File(Constants.CACHE_FILE + Constants.APK_NAME)
            //            final File file = new File(Constants.SD_ROOT_PATH + "/" + "app.apk");
            filePath = file.getAbsolutePath()
//            if(!file.canWrite()||!file.canRead()) {
                Log.i(DownLoadHelper::class.java.simpleName, "path : " + filePath)

//            }
            try {
                val url = URL(mUrl)
                val connection = url
                        .openConnection() as HttpURLConnection
                val inputStream = connection.inputStream
                val size = connection.contentLength//文件大小
                val fos = FileOutputStream(file)
                val buffer = ByteArray(1024)
                connection.connect()
                while (true) {
                    if (inputStream != null) {
                        val numRead = inputStream.read(buffer)
                        if (numRead <= 0) {
                            break
                        } else {
                            fos.write(buffer, 0, numRead)
                            val currentSize = file.length().toInt()
                            val message = Message()
                            message.what = 1
                            message.arg1 = currentSize
                            message.arg2 = size
                            handler.sendMessage(message)
                        }
                    }
                }
                responseCode = connection.responseCode
                connection.disconnect()
                fos.close()
                inputStream?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val cmd = "chmod 777 " + filePath
            try {
                val p = Runtime.getRuntime().exec(cmd)
                val status = p.waitFor()
                if (status == 0) {
                    Log.i(DownLoadHelper::class.java.simpleName, "doInBackground: 权限修改成功")
                } else {
                    Log.i(DownLoadHelper::class.java.simpleName, "doInBackground: 权限修改失败")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            return responseCode
        }

        override fun onPostExecute(responseCode: Int) {
            try {
                listener?.onFinish(responseCode)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    }

    interface OnAsynFinishListener {
        fun onFinish(responseCode: Int)
        fun onProgress(currentSize: Int, totalSize: Int)
    }

}
