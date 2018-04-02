package com.starlight.mobile.android.lib.album

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import com.starlight.mobile.android.lib.R
import com.starlight.mobile.android.lib.adapter.ViewHolder
//设置控件别名
import kotlinx.android.synthetic.main.album_grid_item_layout.view.album_grid_item_layout_iv_image as ivImage
import kotlinx.android.synthetic.main.album_grid_item_layout.view.album_grid_item_layout_rb_select as rbSelect
import java.io.File
import java.util.ArrayList

/**
 * Created by Raleigh on 15/7/10.
 */
class AlbumAdapter(private val mContext: Context, tvPreView: TextView, private val max_optional_count: Int, private val isSigleMode: Boolean) : RecyclerView.Adapter<ViewHolder>() {
    /**
     * 用户选择的图片，存储为图片的完整路径
     */
    var mSelectedImage = ArrayList<String>()
        set(selectedImage) {
            this.mSelectedImage.clear()
            selectedImage.let{
                this.mSelectedImage.addAll(selectedImage)
            }
            notifyDataSetChanged()
        }

    private val mInflater: LayoutInflater
    val mDatas = ArrayList<File>()
    private val mAlbumImageLoader: AlbumImageLoader


    init {
        this.mInflater = LayoutInflater.from(mContext)
        mAlbumImageLoader = AlbumImageLoader(3, AlbumImageLoader.Type.LIFO)
    }

    fun updateAll(mDatas: List<File>?) {
        this.mDatas.clear()
        mDatas?.let{
            this.mDatas.addAll(mDatas)
        }
        notifyDataSetChanged()
    }
    fun getItem(position: Int): File {
        return mDatas[position]
    }
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.album_grid_item_layout, viewGroup,
                false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        with(viewHolder.itemView){
            ivImage.setImageResource(R.mipmap.ic_picture_load)
            //设置no_pic
            //设置图片
            val imagePath = mDatas[position].absolutePath
            mAlbumImageLoader.loadImage(imagePath, ivImage)
            ivImage.colorFilter = null
            ivImage.setOnTouchListener(ShadowTouchListener)
            if (isSigleMode) {
                rbSelect.visibility = View.GONE
            } else {
                rbSelect.visibility = View.VISIBLE
                /**
                 * 已经选择过的图片，显示出选择过的效果
                 */
                if (mSelectedImage.contains(imagePath)) {
                    rbSelect.isChecked = true
                    ivImage.setColorFilter(Color.parseColor("#77000000"))
                } else {
                    //设置no_selected
                    rbSelect.isChecked = false
                }
                rbSelect.setOnClickListener {
                    // 已经选择过该图片
                    if (mSelectedImage.contains(imagePath)) {
                        mSelectedImage.remove(imagePath)
                        rbSelect.isChecked = false
                        ivImage.colorFilter = null
                        mAlbumClickListener?.checkedChange(mSelectedImage.size)
                    } else {// 未选择该图片
                        if (mSelectedImage.size < max_optional_count) {
                            mSelectedImage.add(imagePath)
                            rbSelect.isChecked = true
                            ivImage.setColorFilter(Color.parseColor("#77000000"))
                            mAlbumClickListener?.checkedChange(mSelectedImage.size)
                        } else {
                            rbSelect.isChecked = false
                            val hint = String.format("%s %s %s", mContext.getString(R.string.album_up_to), max_optional_count, if (max_optional_count > 1) mContext.getString(R.string.album_pictures) else mContext.getString(R.string.album_picture))
                            Toast.makeText(mContext, hint, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            ivImage.setOnClickListener {  mAlbumClickListener?.itemClick(position) }
        }
    }
    override fun getItemCount(): Int {
        return mDatas.size
    }
    interface AlbumClickListener {
        fun itemClick(position: Int)
        fun checkedChange(selectedCount: Int)
    }

    var mAlbumClickListener: AlbumClickListener? = null

    private val ShadowTouchListener = View.OnTouchListener { v, event ->
        //点击阴影效果的实现 黑色背景色+透明度
        val imgView = v as ImageView
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                imgView.imageAlpha = 0x88
            else
                imgView.setAlpha(0x88)

            imgView.invalidate()
        } else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                imgView.imageAlpha = 0xFF
            else
                imgView.setAlpha(0xFF)
            imgView.invalidate()
        }
        false
    }
    fun onDestory() {
        mAlbumImageLoader.onDestory()
    }
}
