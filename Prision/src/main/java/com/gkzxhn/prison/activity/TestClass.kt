package com.gkzxhn.prison.activity

import android.os.Bundle
import android.view.View
import com.android.volley.VolleyError
import com.gkzxhn.prison.R
import com.gkzxhn.prison.model.iml.TestModel
import com.gkzxhn.prison.utils.XtHttpUtil
import com.gkzxhn.wisdom.async.VolleyUtils
import kotlinx.android.synthetic.main.test_layout.*
import org.json.JSONObject

/**
 * Created by Raleigh.Luo on 18/4/4.
 */
class TestClass :SuperActivity() {
    private lateinit var  mModel:TestModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_layout)
        mModel= TestModel()
    }
    fun onClickListener(view: View){
        var url=""
        when(view.id){
            //一直显示{code:0}
            R.id.HANGUP->
                url=XtHttpUtil.HANGUP
            //{code:0,"v":{connected:true/false}}
            R.id.GET_NETWORK_STATUS->
                url=XtHttpUtil.GET_NETWORK_STATUS
            R.id.GET_ACCOUNT->
                url=XtHttpUtil.GET_ACCOUNT
            R.id.GET_INPUTFORMAT->
                url=XtHttpUtil.GET_INPUTFORMAT
            R.id.GET_ZJY_OEM->
                url=XtHttpUtil.GET_ZJY_OEM
            R.id.GET_CALLINFO->
                url=XtHttpUtil.GET_CALLINFO
            R.id.GET_MAIN_SOURCE->
                url=XtHttpUtil.GET_MAIN_SOURCE
            R.id.GET_CALL_LOOKBACK->
                url=XtHttpUtil.GET_CALL_LOOKBACK
            R.id.GET_RTMP->
                url=XtHttpUtil.GET_RTMP

            R.id.GET_LOCK_STATUS->
                url=XtHttpUtil.GET_LOCK_STATUS
        }
        mModel.request(url,object :VolleyUtils.OnFinishedListener<String>{
            override fun onFailed(error: VolleyError) {
                Hint.setText("Error: "+error.toString())
            }

            override fun onSuccess(response: String) {
                Hint.setText(response+","+url)
            }
        })
    }
}