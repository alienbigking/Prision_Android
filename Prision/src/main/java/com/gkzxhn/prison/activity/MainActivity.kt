package com.gkzxhn.prison.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Html
import android.view.View
import com.gkzxhn.prison.R
import com.gkzxhn.prison.adapter.MainAdapter
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.customview.CancelVideoDialog
import com.gkzxhn.prison.customview.CustomDialog
import com.gkzxhn.prison.customview.UpdateDialog
import com.gkzxhn.prison.customview.calendar.CalendarCard
import com.gkzxhn.prison.customview.calendar.CalendarViewAdapter
import com.gkzxhn.prison.customview.calendar.CustomDate
import com.gkzxhn.prison.entity.MeetingEntity
import com.gkzxhn.prison.entity.VersionEntity
import com.gkzxhn.prison.presenter.MainPresenter
import com.gkzxhn.prison.service.EReportService
import com.gkzxhn.prison.view.IMainView
import com.netease.nimlib.sdk.StatusCode
import com.starlight.mobile.android.lib.adapter.OnItemClickListener
import com.starlight.mobile.android.lib.view.CusSwipeRefreshLayout
import kotlinx.android.synthetic.main.common_list_layout.common_list_layout_rv_list as mRecylerView
import kotlinx.android.synthetic.main.common_list_layout.common_list_layout_swipeRefresh as mSwipeRefresh
import kotlinx.android.synthetic.main.i_common_loading_layout.common_loading_layout_tv_load as tvLoading
import kotlinx.android.synthetic.main.i_common_no_data_layout.common_no_data_layout_iv_hint as tvNoData
import kotlinx.android.synthetic.main.i_common_no_data_layout.common_no_data_layout_iv_image as ivNodata
import kotlinx.android.synthetic.main.i_main_center_layout.main_layout_tv_free_time as tvFreeTime
import kotlinx.android.synthetic.main.i_main_center_layout.main_layout_tv_month as tvMonth
import kotlinx.android.synthetic.main.i_main_center_layout.main_layout_tv_service_hint as tvServiceConnectHint
import kotlinx.android.synthetic.main.i_main_center_layout.main_layout_tv_year as tvYear
import kotlinx.android.synthetic.main.i_main_center_layout.main_layout_vp_calendar as mViewPager
import android.app.AlarmManager
import android.app.PendingIntent
import java.util.*

/**
 * 主页
 */
class MainActivity : SuperActivity(), IMainView, CusSwipeRefreshLayout.OnRefreshListener, CusSwipeRefreshLayout.OnLoadListener {
    //当前选择日期
    private var mDate: CustomDate? = null
    //列表适配器
    private lateinit var adapter: MainAdapter
    //请求Presenter
    private lateinit var mPresenter: MainPresenter
    //进度条
    private lateinit var mProgress: ProgressDialog
    //取消会见对话框
    private lateinit var mCancelVideoDialog: CancelVideoDialog
    //版本更新对话框
    private var updateDialog: UpdateDialog? = null
    //配置终端提示对话框
    private var mShowTerminalDialog: CustomDialog? = null
    //是否已经连接了紫荆服务
    private var isConnectZijing = false
    //所有月份
    private lateinit var months: Array<String>
    //上一个月，中文月份
    private var mLastMonth = 0
    //第一个闹钟时间戳，默认为0
    private var mFirstAlarmTime=0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isConnectZijing = false
        setContentView(R.layout.main_layout)
        months = resources.getStringArray(R.array.month_text)
        init()
        //启动凌晨闹钟
        createSystemAlarmClock()
        registerReceiver()

    }

    override fun onResume() {
        super.onResume()
        //更新免费会见次数
        updateFreeTime(mPresenter.getSharedPreferences().getInt(Constants.CALL_FREE_TIME, 0))
        //请求版本信息
        mPresenter.requestVersion()
        //开始请求列表数据
        onRefresh()
    }



    private fun init() {
        tvNoData.setText(R.string.no_meeting_data)
        initCalander()
        adapter = MainAdapter(this)
        adapter.setOnItemClickListener(onItemClickListener)
        mSwipeRefresh.setColor(R.color.holo_blue_bright, R.color.holo_green_light,
                R.color.holo_orange_light, R.color.holo_red_light)
        //设置加载模式，为只顶部上啦刷新
        mSwipeRefresh.setMode(CusSwipeRefreshLayout.Mode.BOTH)
        mSwipeRefresh.setLoadNoFull(false)
        mSwipeRefresh.onRefreshListener = this
        mSwipeRefresh.onLoadListener = this
        mRecylerView.adapter = adapter
        mRecylerView.setPadding(0, resources.getDimensionPixelSize(R.dimen.margin_half), 0, 0)
//        val sizeMenu = resources.getDimensionPixelSize(R.dimen.recycler_view_line_height)
//        mRecylerView.addItemDecoration(RecycleViewDivider(
//                this, LinearLayoutManager.HORIZONTAL, sizeMenu, resources.getColor(R.color.common_hint_text_color)))

        //初始化进度条
        mProgress = ProgressDialog.show(this, null, getString(R.string.please_waiting))
        mProgress.setCanceledOnTouchOutside(true)
        mProgress.setCancelable(true)

        dismissProgress()
        mCancelVideoDialog = CancelVideoDialog(this, false)
        mCancelVideoDialog.onClickListener = View.OnClickListener {
            val reason = mCancelVideoDialog.content
            mCancelVideoDialog.dismiss()
            mPresenter.requestCancel(adapter.getCurrentItem().id ?: "", reason)
        }
        //请求数据
        mPresenter = MainPresenter(this, this)
        //请求连接紫荆服务器
        mPresenter.requestZijing()
        mProgress.setMessage(getString(R.string.video_service_connecting))
        mProgress.show()
        //请求免费呼叫次数
        mPresenter.requestFreeTime()
    }

    /**
     * 配置好终端提示对话框
     */
    private fun initTerminalDialog() {
        if (mShowTerminalDialog == null) {
            mShowTerminalDialog = CustomDialog(this)
            with(mShowTerminalDialog!!) {
                this.title = getString(R.string.hint)
                this.content = getString(R.string.please_set_terminal_infor)
                this.confirmText = getString(R.string.to_setting)
                this.cancelText = getString(R.string.cancel)
                this.onClickListener = View.OnClickListener { v ->
                    if (v.id == R.id.custom_dialog_layout_tv_confirm)//去设置
                        startActivity(Intent(context, ConfigActivity::class.java))
                }
            }
        }
    }

    /**
     * 初始化日历
     */
    private fun initCalander() {
        val views = arrayOfNulls<CalendarCard>(3)
        for (i in 0..2) {
            views[i] = CalendarCard(this, onCellClickListener)
        }
        val adapter = CalendarViewAdapter(views)
        mDate = CalendarCard.mShowDate
        mViewPager.adapter = adapter
        mViewPager.currentItem = adapter.currentIndex
        mViewPager.addOnPageChangeListener(adapter.onPageChangeListener)
        mLastMonth = mDate?.month ?: 0
    }

    /**
     * 每天凌晨1点系统重复闹钟
     */
    fun createSystemAlarmClock(){
//        //一次性闹钟,自定义action
        val intent = Intent(Constants.SYSTEM_ALARM_CLOCK)
        //PendingIntent.FLAG_UPDATE_CURRENT
        val sender = PendingIntent.getBroadcast(this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT)
        //定义一个PendingIntent对象，PendingIntent.getBroadcast包含了sendBroadcast的动作。
        // Schedule the alarm!
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val cal=Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY,1)
        cal.set(Calendar.MINUTE,0)
        cal.set(Calendar.SECOND,0)
        //第二天开始
        cal.add(Calendar.DAY_OF_YEAR,1)
        // 时间间隔为1天
        val intervalMillis=24*60*60*1000L
        am.setRepeating(AlarmManager.RTC_WAKEUP, cal.timeInMillis,intervalMillis, sender)
    }

    /**
     * 日历点击监听器
     */
    private val onCellClickListener = object : CalendarCard.OnCellClickListener {
        override fun clickDate(date: CustomDate) {
            //每点击了一个日历，获取新数据列表
            mDate = date
            onRefresh()

        }

        override fun changeDate(date: CustomDate) {
            tvYear.text = date.getYear().toString() + getString(R.string.year)
            tvMonth.text = months[date.getMonth() - 1]
        }
    }
    /**
     *  列表项点击事件监听器
     */
    private val onItemClickListener = object : OnItemClickListener {
        override fun onClickListener(convertView: View, position: Int) {
            when (convertView.id) {

                R.id.main_item_layout_iv_cancel -> { //取消会见
                    if (mCancelVideoDialog != null && !mCancelVideoDialog.isShowing) mCancelVideoDialog.show()
                }
                else -> if (isConnectZijing) {//点击项，进入详情，必须已连接视频服务器
                    val intent = Intent(this@MainActivity, CallUserActivity::class.java)
                    intent.action = Constants.CALL_DEFUALT_ACTION
                    //会见ID
                    intent.putExtra(Constants.EXTRA, adapter.getCurrentItem().id)
                    //家属ID
                    intent.putExtra(Constants.EXTRAS, adapter.getCurrentItem().familyId)
                    //已通话时长
                    intent.putExtra(Constants.TOTAL_CALL_DURATION, adapter.getCurrentTimeLength())
                    startActivityForResult(intent, Constants.EXTRA_CODE)
                } else {
                    showToast(R.string.video_service_is_error)
                    reConnextZijing()
                }
            }

        }
    }


    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Constants.NIM_KIT_OUT) {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }else if(intent.action == Constants.SYSTEM_ALARM_CLOCK) {
                //重新进入主页
                startActivity(Intent(this@MainActivity, SplashActivity::class.java))
                finish()
            }
        }
    }

    /**
     * 页面点击事件
     */
    fun onClickListener(view: View) {
        when (view.id) {
//            R.id.main_layout_btn_last//上一个月
//            ->mViewPager.currentItem = mViewPager.currentItem - 1
//            R.id.main_layout_btn_next//下一个月
//            -> mViewPager.currentItem = mViewPager.currentItem + 1
            R.id.main_layout_tv_setting ->{
                startActivity(Intent(this, SettingActivity::class.java))
            }//设置
            R.id.main_layout_ll_service_hint//视频连接服务
            -> reConnextZijing()

        }
    }

    /**
     * 列表上啦刷新
     */
    override fun onRefresh() {
        //检查云信推送状态
        mPresenter.checkStatusCode()
        if (mPresenter.checkStatusCode() == StatusCode.LOGINED || mPresenter.checkStatusCode() == StatusCode.NET_BROKEN) {
            //云信为已经登录状态
            //没有设置终端，则提示用户设置终端
            if (mPresenter.getSharedPreferences().getString(Constants.TERMINAL_ROOM_NUMBER, "").isEmpty()) {
                stopRefreshAnim()
                initTerminalDialog()
                if (mShowTerminalDialog?.isShowing != true) mShowTerminalDialog?.show()
            } else {
                mPresenter.request(true, mDate.toString())
            }
        } else {
            stopRefreshAnim()
        }
    }

    /**
     * 列表下拉加载
     */
    override fun onLoad() {
        mPresenter.request(false, mDate.toString())
    }

    /**
     * 重连zijing服务器
     */
    private fun reConnextZijing() {
        tvServiceConnectHint.setTextColor(resources.getColor(R.color.connecting))
        tvServiceConnectHint.setText(R.string.check_network_ing)
        //重新请求
        mPresenter.requestZijing()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.EXTRA_CODE && resultCode == Activity.RESULT_OK) {
            onRefresh()
        }
    }

    override fun showProgress() {
        mProgress.setMessage(getString(R.string.please_waiting))
        if (mProgress != null && !mProgress.isShowing) mProgress.show()
    }

    override fun dismissProgress() {
        if (mProgress != null && mProgress.isShowing) mProgress.dismiss()
    }

    /**
     * 列表更新数据
     */
    override fun updateItems(datas: List<MeetingEntity>?) {
        adapter.updateItems(datas)
        //闹钟，第一次启动初始化一次；以后每天凌晨0点初始化一次，原则为每天提醒狱警第一个会见即将开始
//        if(mFirstAlarmTime==0L)initAlarmClock()
    }

    /**
     * 初始化闹钟
     */
    private fun initAlarmClock(){
//        //获取到第一个闹钟时间戳
//        mFirstAlarmTime=adapter.getFirstTime()
//        if(mFirstAlarmTime!=0L){
//            createAlarmClock(mFirstAlarmTime)
//        }
    }



    override fun onCanceled() {
        adapter.removeCurrentItem()
    }

    override fun updateVersion(version: VersionEntity) {
        //新版本
        val newVersion = version.versionCode
        val pm = packageManager
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = pm.getPackageInfo(packageName,
                    PackageManager.GET_CONFIGURATIONS)
            val currentVersion = packageInfo.versionCode//当前App版本
            val lastIgnoreVersion = mPresenter.getSharedPreferences().getInt(Constants.LAST_IGNORE_VERSION, 0)
            var isIgoreVersion = lastIgnoreVersion == newVersion//若是已忽略的版本，则不弹出升级对话框
            if (version.isForce == 1) isIgoreVersion = false
            if (newVersion > currentVersion && !isIgoreVersion) {//新版本大于当前版本，则弹出更新下载到对话框
                //版本名
                val versionName = version.versionName
                // 下载地址
                val downloadUrl = version.downloadUrl
                //是否强制更新
                val isForceUpdate = version.isForce
                if (updateDialog == null) updateDialog = UpdateDialog(this)
                updateDialog?.setForceUpdate(isForceUpdate == 1)
                updateDialog?.setDownloadInfor(versionName ?: "", newVersion, downloadUrl
                        ?: "", version.description ?: "")
                updateDialog?.show()//显示对话框
            }

        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

    }

    /**
     * 开启紫荆视频服务
     */
    override fun startZijingService(isNetworkConnected: Boolean) {
        if (!isConnectZijing) {
            isConnectZijing = true
            dismissProgress()
            val mService = Intent(this, EReportService::class.java)
            startService(mService)
        }
        if (isNetworkConnected) {
            tvServiceConnectHint.setText(R.string.video_service_connect_success)
            tvServiceConnectHint.setTextColor(resources.getColor(R.color.connect_success))
        } else {
//                "0040xf609"
            tvServiceConnectHint.setText(R.string.video_service_connect_failed)
            tvServiceConnectHint.setTextColor(resources.getColor(R.color.connect_failed))
        }
    }

    /**
     * 紫荆服务连接失败
     */
    override fun zijingServiceFailed() {
        isConnectZijing = false
        tvServiceConnectHint.setText(R.string.video_service_connect_failed)
        tvServiceConnectHint.setTextColor(resources.getColor(R.color.connect_failed))
    }

    /**
     * 更新免费次数
     */
    override fun updateFreeTime(freeTime: Int) {
        tvFreeTime.setText(String.format("%s  %s%s", getString(R.string.call_free), freeTime, getString(R.string.time)))
    }

    /**
     * 开始列表刷新动画
     */
    override fun startRefreshAnim() {
        handler.sendEmptyMessage(Constants.START_REFRESH_UI)
    }

    /**
     * 停止列表刷新动画
     */
    override fun stopRefreshAnim() {
        handler.sendEmptyMessage(Constants.STOP_REFRESH_UI)
    }


    override fun onDestroy() {
        //停止所有请求
        mPresenter.onDestory()
        //注销广播监听器
        unregisterReceiver(mBroadcastReceiver)
        //关闭窗口，避免窗口溢出
        if (mProgress.isShowing) mProgress.dismiss()
        if (mShowTerminalDialog?.isShowing ?: false) mShowTerminalDialog?.dismiss()
        if (mCancelVideoDialog.isShowing) mCancelVideoDialog.dismiss()
        if (updateDialog?.isShowing ?: false) updateDialog?.dismiss()
        super.onDestroy()
    }

    /**
     * 注册广播监听器
     */
    private fun registerReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Constants.NIM_KIT_OUT)
        intentFilter.addAction(Constants.SYSTEM_ALARM_CLOCK)
        registerReceiver(mBroadcastReceiver, intentFilter)
    }

    /**
     * 刷新动画加载
     */
    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.what == Constants.START_REFRESH_UI) {//开始动画
                if (adapter == null || adapter.itemCount == 0) {
                    if (ivNodata.isShown) {
                        ivNodata.visibility = View.GONE
                    }
                    tvLoading.visibility = View.VISIBLE
                    if (!tvLoading.isPlaying) {

                        tvLoading.showAndPlay()
                    }
                    if (mSwipeRefresh.isRefreshing) mSwipeRefresh.isRefreshing = false
                } else {
                    if (!mSwipeRefresh.isRefreshing) mSwipeRefresh.isRefreshing = true
                }
            } else if (msg.what == Constants.STOP_REFRESH_UI) {//停止动画
                if (tvLoading.isPlaying || tvLoading.isShown) {
                    tvLoading.hideAndStop()
                    tvLoading.visibility = View.GONE
                }
                if (mSwipeRefresh.isRefreshing) mSwipeRefresh.isRefreshing = false
                if (mSwipeRefresh.isLoading) mSwipeRefresh.isLoading = false
                if (adapter == null || adapter.itemCount == 0) {

                    if (!ivNodata.isShown) ivNodata.visibility = View.VISIBLE
                } else {
                    if (ivNodata.isShown) ivNodata.visibility = View.GONE
                }
            }
        }
    }

}
