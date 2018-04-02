package com.starlight.mobile.android.lib.album

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.starlight.mobile.android.lib.R
import com.starlight.mobile.android.lib.adapter.ViewHolder
//设置控件别名
import kotlinx.android.synthetic.main.album_dir_item_layout.view.album_dir_item_layout_iv_image as ivImage
import kotlinx.android.synthetic.main.album_dir_item_layout.view.album_dir_item_layout_iv_select as vSelect
import kotlinx.android.synthetic.main.album_dir_item_layout.view.album_dir_item_layout_tv_name as tvName
import kotlinx.android.synthetic.main.album_dir_item_layout.view.album_dir_item_layout_tv_count as tvCount


import java.util.ArrayList

/**
 * Created by Raleigh on 15/7/10.
 */
class FolderAdapter(private val mContext: Context, mDatas: List<ImageFloder>?, allPhotosCount: Int) : RecyclerView.Adapter<ViewHolder>() {
    private val mDatas = ArrayList<ImageFloder>()
    var mAlbumItemClickListener: AlbumItemClickListener? = null
    private val allPhotosCount:Int
    private var currentFloderPosition = 0
    private val mAlbumImageLoader: AlbumImageLoader

    init {
        this.allPhotosCount = allPhotosCount
        mAlbumImageLoader = AlbumImageLoader(3, AlbumImageLoader.Type.LIFO)
        mDatas?.let {
            this.mDatas.addAll(it)
        }
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.album_dir_item_layout, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        try {
            with(viewHolder.itemView){
                var photoCount = 0
                var floderName = mContext.getString(R.string.album_all_photos)
                if (position == 0) {
                    photoCount = allPhotosCount
                    tvName.text=context.getText(R.string.album_all_photos)
                    val floder:ImageFloder = mDatas[position]
                    mAlbumImageLoader.loadImage(floder.firstImagePath?:"", ivImage)
                    viewHolder.itemView.setOnClickListener { v ->
                        currentFloderPosition = position
                        notifyDataSetChanged()
                        mAlbumItemClickListener?.let {
                            it.onClick(v, null)
                        }
                    }
                } else {
                    val mFloder = mDatas[position - 1]
                    floderName = mFloder.name
                    tvName.text = mFloder.name?.substring(1)
                    mAlbumImageLoader.loadImage(mFloder.firstImagePath?:"", ivImage)
                    photoCount = mFloder.count
                    viewHolder.itemView.setOnClickListener { v ->
                        currentFloderPosition = position
                        notifyDataSetChanged()
                        mAlbumItemClickListener?.let {
                            it.onClick(v,mFloder)
                        }
                    }
                }
                if (currentFloderPosition == position)
                    vSelect.visibility = View.VISIBLE
                else
                    vSelect.visibility = View.GONE
                tvCount.text = String.format("%s %s", photoCount, if (photoCount > 1) mContext.getString(R.string.album_pictures) else mContext.getString(R.string.album_picture))
            }
        } catch (e: Exception) {
        }

    }

    override fun getItemCount(): Int {
        //多一个All Photos
        return mDatas.size + 1
    }
    interface AlbumItemClickListener {
        fun onClick(v: View, floder: ImageFloder?)
    }

    fun onDestory() {
        mAlbumImageLoader.onDestory()
    }
}
