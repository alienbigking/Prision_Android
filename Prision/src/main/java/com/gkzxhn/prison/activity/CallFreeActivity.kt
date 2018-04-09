package com.gkzxhn.prison.activity


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo

import com.gkzxhn.prison.R
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.presenter.CallFreePresenter
import com.gkzxhn.prison.view.ICallFreeView


import kotlinx.android.synthetic.main.i_call_free_user_infor_layout.call_free_layout_tv_click_call
as tvClickCall
import kotlinx.android.synthetic.main.call_free_layout.call_free_layout_tv_leave_time
as tvFreeTime
import kotlinx.android.synthetic.main.i_call_free_user_infor_layout.call_free_layout_tv_family_name
as tvFamilyName
import kotlinx.android.synthetic.main.i_call_free_user_infor_layout.call_free_layout_tv_phone
as tvPhone
import kotlinx.android.synthetic.main.i_call_free_user_infor_layout.call_free_layout_tv_prision_name
as tvPrisionName
import kotlinx.android.synthetic.main.i_call_free_user_infor_layout.call_free_layout_tv_prision_number
as tvPrisionNumber
import kotlinx.android.synthetic.main.i_common_loading_layout.common_loading_layout_tv_load
as tvLoading
import kotlinx.android.synthetic.main.call_free_layout.call_free_layout_et_phone
as etPhone
import kotlinx.android.synthetic.main.call_free_layout.call_free_layout_iv_clear
as ivSearchClear
import kotlinx.android.synthetic.main.call_free_layout.call_free_layout_tv_search
as tvSearch
/**免费会见
 * Created by Raleigh.Luo on 18/3/29.
 */

class CallFreeActivity : SuperActivity(), ICallFreeView {
    //请求
    private lateinit  var   mPresenter: CallFreePresenter
    //免费呼叫次数
    private var mCallFreeTime = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.call_free_layout)
        stopRefreshAnim()
        mPresenter = CallFreePresenter(this, this)
        //请求免费次数
        mPresenter.requestFreeTime()
        etPhone.setText("18163657553")//TODO
        //设置输入框监听器
        etPhone?.addTextChangedListener(mTextWatcher)
        etPhone?.setOnEditorActionListener { v, actionId, event ->
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
            //点击搜索到的项
            R.id.call_free_layout_rl_item
            ->{
                //项不为空
                mPresenter.entity?.let {
                    //有免费次数
                    if (mCallFreeTime > 0) {
                        val intent = Intent(this, CallUserActivity::class.java)
                        intent.action=Constants.CALL_FREE_ACTION
                        intent.putExtra(Constants.EXTRA, "")
                        intent.putExtra(Constants.EXTRAS, mPresenter.entity?.phone)
                        intent.putExtra(Constants.EXTRA_TAB, mPresenter.entity?.prisonerName)
                        startActivity(intent)
                    } else {//没有免费次数
                        showToast(R.string.no_call_free_time)
                    }
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
        tvFamilyName.setText(R.string.family_name_default)
        tvPhone.setText(R.string.family_phone_default)
        tvPrisionName.setText(R.string.prision_name_default)
        tvPrisionNumber.setText(R.string.prision_number_default)
        tvClickCall.visibility = View.GONE
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
    override fun onSuccess() {
        tvFamilyName.setText(mPresenter.entity?.name)
        tvPhone.setText(mPresenter.entity?.phone)
        tvPrisionName.setText(mPresenter.entity?.prisonerName)
        tvPrisionNumber.setText(mPresenter.entity?.prisonerNumber + " " + mPresenter.entity?.relationship)
        tvClickCall.visibility = View.VISIBLE
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
     * 加载动画
     */
    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.what == Constants.START_REFRESH_UI) {//开始加载动画
                tvLoading.visibility = View.VISIBLE
                if (!tvLoading.isPlaying) {
                    tvLoading.showAndPlay()
                }
            } else if (msg.what == Constants.STOP_REFRESH_UI) {//停止加载动画
                if (tvLoading.isPlaying || tvLoading.isShown) {
                    tvLoading.hideAndStop()
                    tvLoading.visibility = View.GONE
                }
            }
        }
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


}
