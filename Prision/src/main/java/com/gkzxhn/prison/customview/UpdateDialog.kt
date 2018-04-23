package com.gkzxhn.prison.customview


import android.app.Dialog
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Display
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

import com.gkzxhn.prison.R
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.common.GKApplication
import com.gkzxhn.prison.utils.DownLoadHelper
import java.io.File
import kotlinx.android.synthetic.main.update_dialog_layout.update_dialog_layout_tv_cancel
as tvCancel
import kotlinx.android.synthetic.main.update_dialog_layout.update_dialog_layout_v_mid
as vMidLine
import kotlinx.android.synthetic.main.update_dialog_layout.update_dialog_layout_tv_download
as tvDownload
import kotlinx.android.synthetic.main.update_dialog_layout.update_dialog_layoutt_tv_new_version
as tvVersion
import kotlinx.android.synthetic.main.update_dialog_layout.update_dialog_layout_tv_progress
as tvProgress
import kotlinx.android.synthetic.main.update_dialog_layout.update_dialog_layout_progress
as mProgress
import android.os.Build
import android.util.Log
import java.io.IOException


class UpdateDialog(context: Context) : Dialog(context, R.style.update_dialog_style) {
    private var versionName: String? = null
    private var mHelper: DownLoadHelper
    private var downloadUrl: String? = null
    private var isForceUpdate = false
    private var versionCode: Int = 0
    private val downloadFinishListener = object : DownLoadHelper.DownloadFinishListener {
        override fun onSuccess(filePath: String?) {
            try {
                //安装apk
                val apkfile = File(filePath)
                if (!apkfile.exists()) {
                    return
                }
                //打开未知来源权限 允许安装非电子市场提供的应用程序
                if (Build.VERSION.SDK_INT < 17) {
                    val flag = Settings.Secure.getInt(context.contentResolver,
                            Settings.Secure.INSTALL_NON_MARKET_APPS, 0)
                    if (flag == 0) {
                        Settings.Secure.putInt(context.contentResolver,
                                Settings.Secure.INSTALL_NON_MARKET_APPS, 1)
                    }
                } else {
                    val flag = Settings.Global.getInt(context.contentResolver,
                            android.provider.Settings.Secure.INSTALL_NON_MARKET_APPS, 0)
                    if (flag == 0) {
                        Settings.Global.putInt(context.contentResolver,
                                android.provider.Settings.Secure.INSTALL_NON_MARKET_APPS, 1)
                    }
                }

                // 通过Intent安装APK文件
                val i = Intent(Intent.ACTION_VIEW)
                i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                        "application/vnd.android.package-archive")
                context.startActivity(i)
            }catch (e:Exception){
                e.printStackTrace()
                Toast.makeText(getContext(), R.string.install_failed, Toast.LENGTH_SHORT).show()
            }
            dismiss()
        }

        override fun onFailed(error: String?) {
            Toast.makeText(getContext(), R.string.download_failed, Toast.LENGTH_SHORT).show()
            dismiss()

        }

        override fun onProgress(currentSize: Int, totalSize: Int) {
            val total = totalSize.toFloat() / 1024f / 1024f
            val current = currentSize.toFloat() / 1024f / 1024f
            mProgress.progress = (current / total * 100).toInt()
            tvProgress.text = String.format("%.2fMB/%.2fMB", if (current > total) total else current, total)

        }
    }

    init {
        mHelper = DownLoadHelper()

    }

    fun setDownloadInfor(versionName: String, versionCode: Int, downloadUrl: String) {
        this.downloadUrl = downloadUrl
        this.versionName = versionName
        this.versionCode = versionCode
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LayoutInflater.from(getContext()).inflate(R.layout.update_dialog_layout, null))
        initControls()
        init()
        measureWindow()
    }

    fun measureWindow() {
        val dialogWindow = this.window
        val params = dialogWindow.attributes
        val m = dialogWindow.windowManager

        val d = m.defaultDisplay
        params.width = d.width/2
        //	        params.height=d.getHeight();
        dialogWindow.setGravity(Gravity.CENTER)
        dialogWindow.attributes = params
    }

    private fun initControls() {
        tvCancel.isFocusable=true
        tvCancel.visibility = if (isForceUpdate) View.GONE else View.VISIBLE
        vMidLine.visibility = if (isForceUpdate) View.GONE else View.VISIBLE
        setCanceledOnTouchOutside(false)
        setCancelable(false)
        tvVersion.text = context.getString(R.string.new_version_colon) + versionName
    }

    fun setForceUpdate(isForceUpdate: Boolean) {
        this.isForceUpdate = isForceUpdate
        if (tvDownload != null) {
            setCanceledOnTouchOutside(!isForceUpdate)
            tvCancel.visibility = if (isForceUpdate) View.GONE else View.VISIBLE
            vMidLine.visibility = if (isForceUpdate) View.GONE else View.VISIBLE
            tvDownload.isEnabled = true
            tvProgress.visibility = View.GONE
            mProgress.visibility = View.GONE
        }
    }


    private fun init() {
        tvDownload.setOnClickListener {
            mHelper.setListener(downloadFinishListener)
            tvDownload.isEnabled = false
            tvProgress.visibility = View.VISIBLE
            mProgress.visibility = View.VISIBLE
            mHelper.download(downloadUrl?:"")
        }
        findViewById(R.id.update_dialog_layout_tv_cancel).setOnClickListener {
            dismiss()
            //报错忽略升级的版本
            val preferences = getContext().getSharedPreferences(Constants.USER_TABLE, Context.MODE_PRIVATE)
            preferences.edit().putInt(Constants.LAST_IGNORE_VERSION, versionCode).commit()
            mHelper?.onStop()
        }
    }

    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        super.setOnDismissListener(listener)
        mHelper?.onStop()
    }

    override fun show() {
        super.show()
        if(tvDownload!=null){
            tvProgress.visibility = View.INVISIBLE
            tvDownload.isEnabled = true

            //聚焦－遥控器点击ok即可触发
            tvDownload.isFocusable=true
            tvDownload.requestFocus()
            tvDownload.isFocusableInTouchMode=true
            tvDownload.requestFocusFromTouch()
            tvDownload.isSelected=true
        }
    }
}
