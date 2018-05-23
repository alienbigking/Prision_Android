package com.gkzxhn.prison.activity


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo

import com.gkzxhn.prison.R
import com.gkzxhn.prison.adapter.CallFreeAdapter
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.entity.FreeFamilyEntity
import com.gkzxhn.prison.presenter.CallFreePresenter
import com.gkzxhn.prison.view.ICallFreeView
import com.starlight.mobile.android.lib.adapter.OnItemClickListener
import com.starlight.mobile.android.lib.util.CommonHelper
import com.starlight.mobile.android.lib.view.CusSwipeRefreshLayout
import com.starlight.mobile.android.lib.view.RecycleViewDivider
import kotlinx.android.synthetic.main.common_list_layout.*

import kotlinx.android.synthetic.main.call_free_layout.call_free_layout_tv_leave_time
as tvFreeTime

import kotlinx.android.synthetic.main.call_free_layout.call_free_layout_et_phone
as etPhone
import kotlinx.android.synthetic.main.call_free_layout.call_free_layout_iv_clear
as ivSearchClear
import kotlinx.android.synthetic.main.call_free_layout.call_free_layout_tv_search
as tvSearch


import kotlinx.android.synthetic.main.common_list_layout.common_list_layout_rv_list
as mRecylerView
import kotlinx.android.synthetic.main.i_common_loading_layout.common_loading_layout_tv_load
as tvLoading
import kotlinx.android.synthetic.main.i_common_no_data_layout.common_no_data_layout_iv_image
as ivNodata
import kotlinx.android.synthetic.main.common_list_layout.common_list_layout_swipeRefresh
as mSwipeRefresh
import kotlinx.android.synthetic.main.i_common_no_data_layout.common_no_data_layout_iv_hint
as tvNoData

/**免费会见
 * Created by Raleigh.Luo on 18/3/29.
 */

class CallFreeActivity : SuperActivity(), ICallFreeView {
    //请求
    private lateinit  var   mPresenter: CallFreePresenter
    //免费呼叫次数
    private var mCallFreeTime = 0
    private lateinit var adapter:CallFreeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.call_free_layout)
        common_list_layout_fl_root.setBackgroundResource(android.R.color.transparent)
        stopRefreshAnim()
        mPresenter = CallFreePresenter(this, this)
        adapter= CallFreeAdapter(this)
        adapter.setOnItemClickListener(onItemClickListener)
        //设置加载模式，为只顶部上啦刷新
        mSwipeRefresh.setMode(CusSwipeRefreshLayout.Mode.DISABLED)
        mSwipeRefresh.setLoadNoFull(false)
        mRecylerView.adapter = adapter
        val sizeMenu = resources.getDimensionPixelSize(R.dimen.margin_half)
        mRecylerView.addItemDecoration(RecycleViewDivider(
                this, LinearLayoutManager.HORIZONTAL, sizeMenu, resources.getColor(R.color.common_bg_color)))

        //请求免费次数
        mPresenter.requestFreeTime()
//        //TODO
//        etPhone.setText("17621091511")
        //设置输入框监听器
        etPhone.addTextChangedListener(mTextWatcher)
        etPhone.setOnEditorActionListener { v, actionId, event ->
            //点击搜索键
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (mCallFreeTime > 0) {//有免费次数才可进行搜索
                    val phone = etPhone?.text.toString().trim()
                    if (!phone.isEmpty()) {
                        recovery()//恢复页面
                        //搜索手机号码
                        mPresenter.request(phone)
                    }
                }
                true
            } else {
                false
            }
        }
    }
    private val onItemClickListener = object : OnItemClickListener {
        override fun onClickListener(convertView: View, position: Int) {
            //有免费次数
            if (mCallFreeTime > 0) {
                val intent = Intent(this@CallFreeActivity, CallUserActivity::class.java)
                intent.action=Constants.CALL_FREE_ACTION
                intent.putExtra(Constants.EXTRA, "")
                intent.putExtra(Constants.EXTRAS,adapter.getCurrentItem().id)
                intent.putExtra(Constants.EXTRA_TAB,adapter.getCurrentItem().prisonerName)
                startActivity(intent)
            } else {//没有免费次数
                showToast(R.string.no_call_free_time)
            }

        }
    }
    /**
     * 初始化［搜索］按钮
     */
    private fun initSearchBtn() {
        if (mCallFreeTime > 0) {//有免费呼叫次数，则可点击
            val padingBottom = tvSearch.paddingBottom
            val padingLeft = tvSearch.paddingLeft
            val padingRight = tvSearch.paddingRight
            val padingTop = tvSearch.paddingTop
            //不可点击
            tvSearch.isEnabled = true
            tvSearch.setBackgroundResource(R.drawable.search_btn_selector)
            tvSearch.setPadding(padingLeft, padingTop, padingRight, padingBottom)
        } else {//没有免费呼叫次数，显示不点击
            val padingBottom = tvSearch.paddingBottom
            val padingLeft = tvSearch.paddingLeft
            val padingRight = tvSearch.paddingRight
            val padingTop = tvSearch.paddingTop
            tvSearch.setBackgroundResource(R.drawable.search_btn_uneable_selector)
            tvSearch.isEnabled = false
            tvSearch.setPadding(padingLeft, padingTop, padingRight, padingBottom)
        }
    }

    fun onClickListener(view: View) {
        CommonHelper.clapseSoftInputMethod(this)
        when (view.id) {
        //点击返回
            R.id.common_head_layout_iv_left -> finish()
            R.id.call_free_layout_tv_search//搜索
            -> {
                val phone = etPhone?.text.toString().trim()
                if (!phone.isEmpty()) {
                    recovery()//恢复页面
                    mPresenter.request(phone)//请求
                }
            }

        //清空搜索内容
            R.id.call_free_layout_iv_clear -> {
                etPhone?.setText("")//清空
                recovery()//恢复状态
            }
        }
    }

    /**
     * 恢复初始状态
     */
    private fun recovery() {
        mPresenter.clearEntity()
    }

    /**
     * 开始加载动画
     */
    override fun startRefreshAnim() {
        handler.sendEmptyMessage(Constants.START_REFRESH_UI)
    }

    /**
     * 停止加载动画
     */
    override fun stopRefreshAnim() {
        handler.sendEmptyMessage(Constants.STOP_REFRESH_UI)
    }


    /**
     * 搜索手机号码成功
     */
    override fun onSuccess(datas:List<FreeFamilyEntity>?) {
        adapter.updateItems(datas)
    }

    /**
     * 获取到了免费会见次数
     */
    override fun updateFreeTime(time: Int) {
        mCallFreeTime = time
        tvFreeTime.text = mCallFreeTime.toString()
        //初始化搜索按钮
        initSearchBtn()
    }

    override fun onResume() {
        super.onResume()
        mPresenter.startAsynTask(Constants.CLOSE_GUI_TAB,null)
        mCallFreeTime = mPresenter.getSharedPreferences().getInt(Constants.CALL_FREE_TIME, 0)
        tvFreeTime.text = mCallFreeTime.toString()
        initSearchBtn()
    }

    override fun onDestroy() {
        mPresenter.onDestory()
        super.onDestroy()
    }

    /**
     * phone输入框监听
     */
    private val mTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable) {
            //清除按钮 是否显示
            ivSearchClear.visibility = if (etPhone.text.length > 0) View.VISIBLE else View.GONE
        }
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
