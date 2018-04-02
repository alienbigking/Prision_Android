package com.starlight.mobile.android.lib.album

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup

import com.starlight.mobile.android.lib.R
import com.starlight.mobile.android.lib.view.photoview.PhotoViewAttacher
import kotlinx.android.synthetic.main.album_preview_item_layout.view.album_preview_item_layout_pv_image as photoView

import java.io.File
import java.util.ArrayList

/**
 * Created by Raleigh on 15/7/23.
 */
class AlbumPreviewAdapter(private val context: Context, list: List<File>, private val onShortTouchListener: PhotoViewAttacher.OnShortTouchListener) : PagerAdapter() {

    private val mAlbumImageLoader: AlbumImageLoader
    val list = ArrayList<File>()

    init {
        this.list.addAll(list)//不要用赋值，否则就是同一个变量了
        mAlbumImageLoader = AlbumImageLoader(3, AlbumImageLoader.Type.LIFO)
    }

    override fun getCount(): Int {
        return list.size
    }



    fun getItem(position: Int): File {
        return list[position]
    }

    fun remove(position: Int): Boolean {
        var result = false
        if (position < list.size) {
            getItemPosition(list[position])
            if (list.size > position) {
                list.removeAt(position)
                result = true
            }
            //直接使用notifyDataSetChanged是无法更新，需要同时重写getItemPosition返回常量 POSITION_NONE (此常量为viewpager带的)。
            notifyDataSetChanged()
        }
        return result
    }

    override fun instantiateItem(container: ViewGroup, position: Int): View {
        val view = View.inflate(context, R.layout.album_preview_item_layout, null)
        try {
            with(view){
                val imageItem = list[position]
                mAlbumImageLoader.loadImage(imageItem.absolutePath, photoView)
                photoView.setOnShortTouchListener(onShortTouchListener)
            }
            (container as ViewPager).addView(view)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return view
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getItemPosition(`object`: Any?): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    fun onDestory() {
        mAlbumImageLoader.onDestory()
    }
}
