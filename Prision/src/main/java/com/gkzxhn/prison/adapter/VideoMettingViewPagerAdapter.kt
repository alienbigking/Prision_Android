package com.gkzxhn.prison.adapter

import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.gkzxhn.prison.R
import com.gkzxhn.prison.entity.MeetingMemberEntity
import com.gkzxhn.prison.utils.Utils.getImageUrl
import com.nostra13.universalimageloader.core.ImageLoader
import java.util.*

/**
 * Explanation：
 * @author LSX
 * Created on 2018/9/14.
 */

class VideoMettingViewPagerAdapter(private val datas: ArrayList<MeetingMemberEntity>) : PagerAdapter() {

    override fun getCount(): Int {
        return datas.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val data = datas[position]
        val inflate = View.inflate(container.context, R.layout.item_video_metting, null)
        val ivItemIcon = inflate.findViewById(R.id.item_video_metting_iv_1) as ImageView
        val ivItemFront = inflate.findViewById(R.id.item_video_metting_iv_2) as ImageView
        val ivItemBack = inflate.findViewById(R.id.item_video_metting_iv_3) as ImageView
        ivItemIcon.post { ImageLoader.getInstance().displayImage(getImageUrl(data.familyAvatarUrl.toString()), ivItemIcon) }
        ivItemFront.post { ImageLoader.getInstance().displayImage(getImageUrl(data.familyIdCardFront.toString()), ivItemFront) }
        ivItemBack.post { ImageLoader.getInstance().displayImage(getImageUrl(data.familyIdCardBack.toString()), ivItemBack) }
        container.addView(inflate)
        return inflate
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        //注意这里要移除object
        container.removeView(`object` as View)
    }
}