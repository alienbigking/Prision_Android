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
/**
 * Created by Raleigh.Luo on 18/3/29.
 */

class CallFreeActivity : SuperActivity(), ICallFreeView {
    private lateinit  var   mPresenter: CallFreePresenter
    private var mCallFreeTime = 0
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

    private val mTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable) {
            ivSearchClear.visibility = if (etPhone.text.length > 0) View.VISIBLE else View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.call_free_layout)
        etPhone?.addTextChangedListener(mTextWatcher)
        etPhone?.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (mCallFreeTime > 0) {
                    val phone = etPhone?.text.toString().trim()
                    if (!phone.isEmpty()) {
                        recovery()//恢复页面
                        mPresenter.request(phone)//请求
                    }
                }
                true
            } else {
                false
            }
        }
        stopRefreshAnim()
        mPresenter = CallFreePresenter(this, this)
        mCallFreeTime = mPresenter.getSharedPreferences().getInt(Constants.CALL_FREE_TIME, 0)
        tvFreeTime.text = mCallFreeTime.toString()
        initSearchBtn()
        mPresenter.requestFreeTime()
        etPhone.setText("18163657553")//TODO

    }

    private fun initSearchBtn() {
        if (mCallFreeTime > 0) {
            val padingBottom = tvSearch.paddingBottom
            val padingLeft = tvSearch.paddingLeft
            val padingRight = tvSearch.paddingRight
            val padingTop = tvSearch.paddingTop
            tvSearch.isEnabled = true
            tvSearch.setBackgroundResource(R.drawable.search_btn_selector)
            tvSearch.setPadding(padingLeft, padingTop, padingRight, padingBottom)
        } else {
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
            R.id.common_head_layout_iv_left -> finish()
            R.id.call_free_layout_tv_search//搜索
            -> {
                val phone = etPhone?.text.toString().trim()
                if (!phone.isEmpty()) {
                    recovery()//恢复页面
                    mPresenter.request(phone)//请求
                }
            }
            R.id.call_free_layout_rl_item//点击项
            ->{
                mPresenter.entity?.let {
                    if (mCallFreeTime > 0) {
                        val intent = Intent(this, CallUserActivity::class.java)
                        intent.putExtra(Constants.EXTRA, "")
                        intent.putExtra(Constants.EXTRAS, mPresenter.entity?.phone)
                        intent.putExtra(Constants.EXTRA_TAB, mPresenter.entity?.prisonerName)
                        startActivityForResult(intent, Constants.EXTRA_CODE)
                    } else {
                        showToast(R.string.no_call_free_time)
                    }
                }
            }
            R.id.call_free_layout_iv_clear -> {
                etPhone?.setText("")
                recovery()
            }
        }
    }

    private fun recovery() {
        mPresenter.clearEntity()
        tvFamilyName.setText(R.string.family_name_default)
        tvPhone.setText(R.string.family_phone_default)
        tvPrisionName.setText(R.string.prision_name_default)
        tvPrisionNumber.setText(R.string.prision_number_default)
        tvClickCall.visibility = View.GONE
    }

    override fun startRefreshAnim() {
        handler.sendEmptyMessage(Constants.START_REFRESH_UI)
    }

    override fun stopRefreshAnim() {
        handler.sendEmptyMessage(Constants.STOP_REFRESH_UI)
    }


    override fun onSuccess() {
        tvFamilyName.setText(mPresenter.entity?.name)
        tvPhone.setText(mPresenter.entity?.phone)
        tvPrisionName.setText(mPresenter.entity?.prisonerName)
        tvPrisionNumber.setText(mPresenter.entity?.prisonerNumber + " " + mPresenter.entity?.relationship)
        tvClickCall.visibility = View.VISIBLE
    }

    override fun updateFreeTime(time: Int) {
        mCallFreeTime = time
        tvFreeTime.text = mCallFreeTime.toString()
        initSearchBtn()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == Constants.EXTRA_CODE && resultCode == Activity.RESULT_OK) {
            mPresenter.requestFreeTime()
            mCallFreeTime--
            tvFreeTime.text = mCallFreeTime.toString()
            initSearchBtn()
        }
    }

}
