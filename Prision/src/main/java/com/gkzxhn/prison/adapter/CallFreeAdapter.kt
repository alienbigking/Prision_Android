package com.gkzxhn.prison.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.gkzxhn.prison.R
import com.gkzxhn.prison.entity.FreeFamilyEntity
import com.starlight.mobile.android.lib.adapter.OnItemClickListener
import com.starlight.mobile.android.lib.adapter.ViewHolder
import java.util.ArrayList

import kotlinx.android.synthetic.main.i_call_free_user_infor_layout.view.call_free_layout_tv_family_name
as tvFamilyName
import kotlinx.android.synthetic.main.i_call_free_user_infor_layout.view.call_free_layout_tv_phone
as tvPhone
import kotlinx.android.synthetic.main.i_call_free_user_infor_layout.view.call_free_layout_tv_prision_name
as tvPrisionName
import kotlinx.android.synthetic.main.i_call_free_user_infor_layout.view.call_free_layout_tv_prision_number
as tvPrisionNumber
import kotlinx.android.synthetic.main.i_call_free_user_infor_layout.view.call_free_layout_tv_relationship
as tvRelationShip

/**
 * Created by Raleigh.Luo on 18/4/11.
 */

class CallFreeAdapter(private val mContext: Context) : RecyclerView.Adapter<ViewHolder>(){

    private var mDatas: ArrayList<FreeFamilyEntity> = ArrayList()
    private var onItemClickListener: OnItemClickListener? = null
    private var mCurrentIndex = -1

    /**
     *  获取当前项实体
     */
    fun getCurrentItem(): FreeFamilyEntity{
        return mDatas[mCurrentIndex]
    }
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }
    /**
     * 更新数据
     */
    fun updateItems(mDatas: List<FreeFamilyEntity>?) {
        this.mDatas.clear()
        if (mDatas != null && mDatas.isNotEmpty()) {
            this.mDatas.addAll(mDatas)
        }
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.i_call_free_user_infor_layout, null)
        view.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.itemView){
            val entity=mDatas.get(position)
            tvFamilyName.setText(entity.name)
//            tvPhone.setText(entity.phone)
            tvPrisionName.setText(entity.prisonerName)
            tvPrisionNumber.setText(entity.prisonerNumber)
            tvRelationShip.setText(entity.relationship)
            holder.itemView.setOnClickListener(View.OnClickListener {
                mCurrentIndex=position
                onItemClickListener?.onClickListener(this,position)
            })
        }
    }

    override fun getItemCount(): Int {
        return mDatas.size
    }

}
