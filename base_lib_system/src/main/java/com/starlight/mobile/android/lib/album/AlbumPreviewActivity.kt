package com.starlight.mobile.android.lib.album

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.KeyEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast

import com.starlight.mobile.android.lib.R
import com.starlight.mobile.android.lib.view.photoview.PhotoViewAttacher
import kotlinx.android.synthetic.main.album_preview_layout.preview_layout_viewpager as viewPager
import kotlinx.android.synthetic.main.album_preview_layout.preview_layout_rb_select as rbSelect
import kotlinx.android.synthetic.main.album_preview_layout.preview_layout_ll_bottom as llBottom
import kotlinx.android.synthetic.main.album_preview_layout.preview_layout_ch_head as chHead

import java.io.File
import java.io.Serializable
import java.util.ArrayList

/**图片预览
 * Created by Raleigh on 15/7/23.
 * return
 * List<String> SelectedImages 已选择的图片路径集合
</String> */
class AlbumPreviewActivity : Activity() {
    private var currentPosition = 0
    private lateinit var adapter: AlbumPreviewAdapter
    private var mAllPhotoPath: MutableList<File> = ArrayList()
    private var mAllPhotoSize: Int = 0
    //listData的大小，图片的总数量
    private var mMaxOptionalCount: Int = 0//本次可选的总数
    private var isScrolling = false//viewpager 是否正在滑动
    private var mSelectedPhotosPath: MutableList<String> = ArrayList()
    private val EXTRA_IMAGE_LIST = "extra_image_list"
    private val EXTRA_IMAGE_SELECT_COUNT = "extra_image_select_count"
    private val EXTRA_CURRENT_POSITION = "extra_current_position"
    private val EXTRA_HAS_SELECTED_IMAGE = "extra_has_selected_image"//已经选择的图片路径列表
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.album_preview_layout)
        init()
    }
    private fun init() {
        try {
            viewPager.addOnPageChangeListener(onPageChangeListener)
            val bundle:Bundle? = intent?.extras
            bundle?.let {
                mAllPhotoPath = it.getSerializable(EXTRA_IMAGE_LIST) as MutableList<File>
                if (it.containsKey(EXTRA_CURRENT_POSITION))
                    currentPosition = it.getInt(EXTRA_CURRENT_POSITION)
                if (it.containsKey(EXTRA_IMAGE_SELECT_COUNT))
                //本次可选的总数
                    mMaxOptionalCount = it.getInt(EXTRA_IMAGE_SELECT_COUNT)
                if (it.containsKey(EXTRA_HAS_SELECTED_IMAGE)) {
                    mSelectedPhotosPath = it.getSerializable(EXTRA_HAS_SELECTED_IMAGE) as MutableList<String>
                }
                mAllPhotoSize = mAllPhotoPath.size
                adapter = AlbumPreviewAdapter(this, mAllPhotoPath, onShortTouchListener)
                if (mSelectedPhotosPath.contains(mAllPhotoPath[currentPosition].absolutePath)) rbSelect.isChecked = true//当前图片被选中

                viewPager?.adapter = adapter
                viewPager?.currentItem = currentPosition

                chHead.getTvTitle().text = String.format("%d/%d", currentPosition + 1, mAllPhotoSize)
                if (mSelectedPhotosPath.size == 0)
                    chHead.getTvRight().setText(R.string.album_finish)
                else
                    chHead.getTvRight().text = String.format("%s(%d/%d)", getString(R.string.album_finish), mSelectedPhotosPath.size, mMaxOptionalCount)
                rbSelect.setOnClickListener(onCheckedChangeListener)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showOrHidePanel(isShown: Boolean) {
        if (isShown) {
            val headHideAnim = AnimationUtils.loadAnimation(this,
                    R.anim.slide_out_to_top)
            val bottomHideAnim = AnimationUtils.loadAnimation(this,
                    R.anim.slide_out_to_bottom)
            //设置动画时间
            headHideAnim.duration = 400
            bottomHideAnim.duration = 400
            chHead.startAnimation(headHideAnim)
            chHead.visibility = View.GONE
            llBottom.startAnimation(bottomHideAnim)
            llBottom.visibility = View.GONE
        } else {
            val headShowAnim = AnimationUtils.loadAnimation(this,R.anim.slide_in_from_top)
            val bottomShowAnim = AnimationUtils.loadAnimation(this,R.anim.slide_in_from_bottom)
            //设置动画时间
            headShowAnim.duration = 400
            bottomShowAnim.duration = 400
            chHead.startAnimation(headShowAnim)
            chHead.visibility = View.VISIBLE
            llBottom.startAnimation(bottomShowAnim)
            llBottom.visibility = View.VISIBLE
        }


    }

    fun onClickListener(v: View) {
        if (v.id == R.id.common_head_layout_iv_left) {
            val data = Intent()
            data.putExtra(AlbumActivity.EXTRAS, mSelectedPhotosPath as Serializable)
            setResult(Activity.RESULT_CANCELED, data)
            this.finish()
        } else if (v.id == R.id.common_head_layout_tv_right) {
            val data = Intent()
            data.putExtra(AlbumActivity.EXTRAS, mSelectedPhotosPath as Serializable)
            setResult(Activity.RESULT_OK, data)
            this.finish()
        }
    }

    private val onShortTouchListener = object : PhotoViewAttacher.OnShortTouchListener {
        @SuppressLint("NewApi")
        override fun back(upX: Float, upY: Float) {
            if (chHead.measuredHeight < upY && llBottom.y > upY)
                showOrHidePanel(chHead.isShown)
        }

        override fun doubleTab() {//双击
            showOrHidePanel(true)
        }
    }
    private val onCheckedChangeListener = View.OnClickListener {
        val isChecked = !mSelectedPhotosPath.contains(mAllPhotoPath[currentPosition].absolutePath)
        if (isChecked) {
            if (!isScrolling) {//没有在滑动页面
                if (mSelectedPhotosPath.size < mMaxOptionalCount) {
                    mSelectedPhotosPath.add(mAllPhotoPath[currentPosition].absolutePath)
                } else {
                    rbSelect.isChecked = false
                    val hint = String.format("%s %s %s", getString(R.string.album_up_to), mMaxOptionalCount, if (mMaxOptionalCount > 1) getString(R.string.album_pictures) else getString(R.string.album_picture))
                    Toast.makeText(this@AlbumPreviewActivity, hint, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            if (!isScrolling) {//没有在滑动页面
                rbSelect.isChecked = false
                mSelectedPhotosPath.remove(mAllPhotoPath[currentPosition].absolutePath)
            }
        }
        if (mSelectedPhotosPath.size == 0)
            chHead.getTvRight().setText(R.string.album_finish)
        else
            chHead.getTvRight().text = String.format("%s(%d/%d)", getString(R.string.album_finish), mSelectedPhotosPath.size, mMaxOptionalCount)
    }


    private val onPageChangeListener = object : ViewPager.OnPageChangeListener {

        override fun onPageSelected(position: Int) {
            isScrolling = true
            mAllPhotoPath.let {
                currentPosition = position
                chHead.getTvTitle().text = String.format("%d/%d", currentPosition + 1, mAllPhotoSize)
                rbSelect.isChecked = mSelectedPhotosPath.contains(mAllPhotoPath[position].absolutePath)
                isScrolling = false
            }
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {

        }

        override fun onPageScrollStateChanged(arg0: Int) {

        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val data = Intent()
            data.putExtra(AlbumActivity.EXTRAS, mSelectedPhotosPath as Serializable)
            setResult(Activity.RESULT_CANCELED, data)
            this.finish()
        }
        return true
    }

    override fun onDestroy() {
        adapter.onDestory()
        super.onDestroy()
    }
}
