package com.gkzxhn.prison.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout

import com.gkzxhn.prison.R
import com.gkzxhn.prison.entity.MeetingEntity
import com.starlight.mobile.android.lib.adapter.OnItemClickListener
import com.starlight.mobile.android.lib.adapter.ViewHolder
import com.starlight.mobile.android.lib.util.ConvertUtil
import java.util.*
import kotlin.math.max

import kotlinx.android.synthetic.main.main_item_layout.view.main_item_layout_tv_time
as tvTime
import kotlinx.android.synthetic.main.main_item_layout.view.main_item_layout_tv_name
as tvName
import kotlinx.android.synthetic.main.main_item_layout.view.main_item_layout_tv_prison_area
as tvArea
import kotlinx.android.synthetic.main.main_item_layout.view.main_item_layout_iv_cancel
as ivCancel
/**
 * Created by Raleigh.Luo on 17/4/11.
 */

class MainAdapter(private val mContext: Context) : RecyclerView.Adapter<ViewHolder>() {

    private var mDatas: ArrayList<MeetingEntity> = ArrayList()
    private var onItemClickListener: OnItemClickListener? = null
    private var mCurrentIndex = -1


    /**
     *  获取当前项实体
     */
    fun getCurrentItem(): MeetingEntity{
        return mDatas[mCurrentIndex]
    }
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    /**
     * 获取当前时长
     */
    fun getCurrentTimeLength():Long{
        var timeLenth=0L
        try {
            //解析会见时间段2017-08-03 19:00-19:30 获取会见时长
            val meetingTime = mDatas[mCurrentIndex].time
            var time = "-"
            meetingTime?.let {
                time = if (it.length > 10) it.substring(10, it.length) else "-"
            }
            val array = time.split("-")
            if (array.size > 0) {
                val mDate1 = ConvertUtil.stringToDate(array[0].trim(), "HH:mm")
                val mDate2 = ConvertUtil.stringToDate(array[1].trim(), "HH:mm")
                val t2=mDate2?.time?:0
                val t1=mDate1?.time?:0
                //获取相差的毫秒数，单位转为秒
                timeLenth=(t2-t1)/1000
            }
        }catch (e:Exception){}

        return timeLenth
    }
    /**
     * 获取当前时长
     */
    fun getFirstTime():Long{
        var timeInMillis=0L
        try {
            if(itemCount>0){//取第一条数据的时分
                //解析会见时间段2017-08-03 19:00-19:30 获取会见时长
                val meetingTime = mDatas[0].time
                var time = "-"
                meetingTime?.let {
                    time = if (it.length > 10) it.substring(10, it.length) else "-"
                }
                val array = time.split("-")
                if (array.size > 0) {
                    val mDate = ConvertUtil.stringToDate(array[0].trim(), "HH:mm")
                    mDate?.let {
                        val cal=Calendar.getInstance()
                        cal.set(Calendar.HOUR_OF_DAY,it.hours)
                        cal.set(Calendar.MINUTE,it.minutes)
                        cal.set(Calendar.SECOND,0)

                        val currentCal=Calendar.getInstance()
                        Log.e("raleigh_test","firstTime="+ConvertUtil.getSystemLongDateFormat(cal.timeInMillis))
                        if(currentCal.timeInMillis<cal.timeInMillis){
                            //会见时间必须大于当前时间，才提醒
                            //前10分钟,分钟减去10
                            cal.add(Calendar.MINUTE,-10)
                            //取最大
                            timeInMillis= Math.max(cal.timeInMillis,currentCal.timeInMillis)
                        }
                    }
                }
            }
        }catch (e:Exception){}

        return timeInMillis
    }
    /**
     *  移除当前操作项
     */
    fun removeCurrentItem() {
        this.mDatas.removeAt(mCurrentIndex)
        notifyItemRemoved(mCurrentIndex)
    }

    /**
     * 更新数据
     */
    fun updateItems(mDatas: List<MeetingEntity>?) {
        this.mDatas.clear()
        if (mDatas != null && mDatas.isNotEmpty()) {
            this.mDatas.addAll(mDatas)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.main_item_layout, null)
        view.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.itemView){
            val entity = mDatas[position]
            tvName.text = entity.name
            val meetingTime = entity.time?:""
            //        2017-08-03 19:00-19:30
            tvTime.text =if(meetingTime.length>10)meetingTime.substring(10,meetingTime.length) else "-"
            tvArea.text = entity.prisonerNumber
            this.setOnClickListener { v ->
                mCurrentIndex = position
                onItemClickListener?.onClickListener(v, position)
            }
            ivCancel.setOnClickListener { v ->
                mCurrentIndex = position
                onItemClickListener?.onClickListener(v, position)
            }
        }

    }

    override fun getItemCount(): Int {
        return mDatas.size
    }
}
