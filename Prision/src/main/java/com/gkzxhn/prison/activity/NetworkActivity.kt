package com.gkzxhn.prison.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.gkzxhn.prison.R
import com.gkzxhn.prison.async.AsynHelper
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.entity.VersionEntity
import com.gkzxhn.prison.presenter.SettingPresenter
import com.gkzxhn.prison.service.EReportService
import com.gkzxhn.prison.view.ISettingView
import kotlinx.android.synthetic.main.network_layout.network_layout_tv_enable_gui_hint
as tvEnableGuiHint
import kotlinx.android.synthetic.main.network_layout.network_layout_tv_disable_gui_hint
as tvDisableGuiHint
import kotlinx.android.synthetic.main.network_layout.network_layout_tv_check_network_hint
as tvCheckNetworkHint

import kotlinx.android.synthetic.main.network_layout.network_layout_btn_enable_gui
as btnEnableGui
import kotlinx.android.synthetic.main.network_layout.network_layout_btn_disable_gui
as btnDisableGui
import kotlinx.android.synthetic.main.network_layout.network_layout_btn_check_network
as btnCheckNetwork
/**检查网络
 * Created by Raleigh.Luo on 18/5/8.
 */

class NetworkActivity: SuperActivity(), ISettingView {
    //请求presenter
    private lateinit var mPresenter: SettingPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.network_layout)
        //初始化Presenter
        mPresenter = SettingPresenter(this, this)
    }
    fun onClickListener(view: View){
        when(view.id){
            R.id.common_head_layout_iv_left ->{
                finish()
            }
            R.id.network_layout_btn_enable_gui ->{//启用Gui
                tvEnableGuiHint.setText(R.string.start_gui_ing)
                btnEnableGui.isEnabled=false
                // adb shell pm enable cn.com.rocware.c9gui
                mPresenter.startAsynTask(Constants.OPEN_GUI_TAB,object : AsynHelper.TaskFinishedListener{
                    override fun back(`object`: Any?) {
                        btnEnableGui.isEnabled=true
                        val i=`object` as Int
                        if(i==0){//启用成功
                            tvEnableGuiHint.setText(R.string.start_gui_success)
                            tvDisableGuiHint.setText(R.string.stop_gui_hint)
                        }else{
                            tvEnableGuiHint.setText(R.string.start_gui_failed)
                        }
                    }

                })

            }
            R.id.network_layout_btn_disable_gui ->{//关闭Gui
                btnDisableGui.isEnabled=false
                tvDisableGuiHint.setText(R.string.stop_gui_ing)
                //adb shell pm disable cn.com.rocware.c9gui
                mPresenter.startAsynTask(Constants.CLOSE_GUI_TAB,object : AsynHelper.TaskFinishedListener{
                    override fun back(`object`: Any?) {
                        btnDisableGui.isEnabled=true
                        val i=`object` as Int
                        if(i==0){//禁用用成功
                            tvDisableGuiHint.setText(R.string.stop_gui_success)
                            tvEnableGuiHint.setText(R.string.start_gui_hint)
                            stopService(Intent(this@NetworkActivity, EReportService::class.java))
                            val preferences = getSharedPreferences(Constants.USER_TABLE, Activity.MODE_PRIVATE)
                            if (preferences.getString(Constants.USER_ACCOUNT, "").length >0) {//登录 重启服务
                                val mService = Intent(this@NetworkActivity, EReportService::class.java)
                                startService(mService)
                            }
                        }else{
                            tvDisableGuiHint.setText(R.string.stop_gui_failed)
                        }
                    }
                })
            }
            R.id.network_layout_btn_check_network ->{//检查网络
                tvCheckNetworkHint.setText(R.string.check_network_ing)
                //按钮不可点击
                btnCheckNetwork.isEnabled=false
                //关闭GUI
                mPresenter.checkNetworkStatus()
            }
        }
    }



    override fun networkStatus(isConnected: Boolean) {
        //按钮可点击
        btnCheckNetwork.isEnabled=true
        if(isConnected){
            tvCheckNetworkHint.setTextColor(resources.getColor(R.color.connect_success))
            tvCheckNetworkHint.setText(R.string.check_network_normal)
        }else{
            tvCheckNetworkHint.setTextColor(resources.getColor(R.color.red_text))
            tvCheckNetworkHint.setText(R.string.check_network_innormal)
        }
    }

    override fun startRefreshAnim() {
    }

    override fun stopRefreshAnim() {
    }

    override fun updateVersion(version: VersionEntity?) {
    }

}
