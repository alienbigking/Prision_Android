package com.starlight.mobile.android.lib.album

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.Toast

import com.starlight.mobile.android.lib.R
import com.starlight.mobile.android.lib.album.ImageDirPopupWindow.OnImageDirSelected


import java.io.File
import java.io.Serializable
import java.util.ArrayList
import java.util.Arrays
import java.util.Collections
import java.util.HashSet
//设置控件别名
import kotlinx.android.synthetic.main.album_layout.album_layout_ch_head as chHead
import kotlinx.android.synthetic.main.album_layout.album_layout_tv_album_preview as tvPreview
import kotlinx.android.synthetic.main.album_layout.album_layout_rv_list as mRecyclerView
import kotlinx.android.synthetic.main.album_layout.album_layout_tv_album_name as mAlbumName


/**
 * Created by Raleigh on 15/7/10.
 *
 * Intent入参
 * Intent action= AlbumActivity.EXTRAS_SIGLE_MODE  单张图片选择，不需要单选框和预览功能,isSigleMode=true
 * 无action isSigleMode＝false
 * IntExtra  AlbumActivity.EXTRA_IMAGE_SELECT_COUNT  本次可选的数量,default=9
 * return  AlbumActivity.EXTRAS
 * SIGLE MODE:String 图片路径
 * Not SIGLE MODE:List<String> SelectedImages 已选择的图片路径集合
</String> */
class AlbumActivity : Activity(), OnImageDirSelected {
    private lateinit var mProgressDialog: ProgressDialog

    /**
     * 存储文件夹中的图片数量
     */
    private var mPicsSize: Int = 0
    /**
     * 图片数量最多的文件夹
     */
    private var mImgDir: File? = null


    private var mAdapter: AlbumAdapter? = null
    /**
     * 临时的辅助类，用于防止同一个文件夹的多次扫描
     */
    private var mDirPaths: HashSet<String> = HashSet()

    /**
     * 扫描拿到所有的图片文件夹
     */
    private val mImageFloders = ArrayList<ImageFloder>()

    internal var totalCount = 0

    private var mScreenHeight: Int = 0
    private var max_optional_count: Int = 0//本次可选的总数
    private var isSigleMode: Boolean = false

    private lateinit var mListImageDirPopupWindow: ImageDirPopupWindow
    private var allPhotosCount = 0
    private val EXTRA_IMAGE_LIST = "extra_image_list"

    private val EXTRA_CURRENT_POSITION = "extra_current_position"
    private val EXTRA_HAS_SELECTED_IMAGE = "extra_has_selected_image"//已经选择的图片路径列表
    private val MAX_OPTIONAL_COUNT = 9//最大的可选数量
    private val REQUEST_PREVIEW_CODE = 0x001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.album_layout)
        val outMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(outMetrics)
        mScreenHeight = outMetrics.heightPixels
        max_optional_count = intent.getIntExtra(EXTRA_IMAGE_SELECT_COUNT, MAX_OPTIONAL_COUNT)
        val action = intent.action
        isSigleMode = action != null && action == EXTRAS_SIGLE_MODE
        initView()
        getImages()
    }
    /**
     * 初始化View
     */
    private fun initView() {
        mAdapter = AlbumAdapter(this, tvPreview, max_optional_count, isSigleMode)
        mAdapter?.mAlbumClickListener=albumClickListener
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = GridLayoutManager(this, 3)
        mRecyclerView.itemAnimator = DefaultItemAnimator()
        mRecyclerView.adapter = mAdapter
        chHead.getTvRight().isEnabled = false
        if (isSigleMode) {
            chHead.getTvRight().visibility = View.GONE
            tvPreview.visibility = View.GONE
        }
    }
    /**
     * 为View绑定数据
     */
    private fun loadAllPhotos() {
        allPhotosCount = 0
        val mImgs = ArrayList<File>()
        for (i in mImageFloders.indices) {
            val folder = mImageFloders[i]
            val mDir = File(folder.dir)
            mImgs.addAll(Arrays.asList(*mDir.listFiles { dir, filename ->
                filename.endsWith(".jpg") || filename.endsWith(".png")
                        || filename.endsWith(".jpeg")
            }))
            allPhotosCount += folder.count
        }
        Collections.sort(mImgs, AlbumFileComparator())//通过重写Comparator的实现类
        /**
         * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
         */
        mAdapter?.updateAll(mImgs)
        return
        Toast.makeText(applicationContext, R.string.album_no_photo, Toast.LENGTH_SHORT).show()
    }

    /**
     * 初始化展示文件夹的popupWindw
     */
    private fun initListDirPopupWindw() {
        try {
            mListImageDirPopupWindow = ImageDirPopupWindow(
                    LayoutParams.MATCH_PARENT, (mScreenHeight * 0.7).toInt(),
                    mImageFloders, allPhotosCount, LayoutInflater.from(applicationContext)
                    .inflate(R.layout.album_dir_layout, null))


            mListImageDirPopupWindow .setOnDismissListener {
                // 设置背景颜色变暗
                val lp = window.attributes
                lp.alpha = 1.0f
                window.attributes = lp
            }
            // 设置选择文件夹的回调
            mListImageDirPopupWindow .setOnImageDirSelected(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
     */
    private fun getImages() {
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            Toast.makeText(this, R.string.album_no_sd_card, Toast.LENGTH_SHORT).show()
            return
        }
        // 显示进度条
        mProgressDialog = ProgressDialog.show(this, null, getString(R.string.album_loading))

        Thread(Runnable {
            try {
                var firstImage: String? = null
                val mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                val mContentResolver = this@AlbumActivity
                        .contentResolver
                // 只查询jpeg jpg和png的图片
                val mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "+MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        arrayOf("image/jpeg","image/jpg", "image/png"),
                        MediaStore.Images.Media.DATE_MODIFIED)
                while (mCursor.moveToNext()) {
                    // 获取图片的路径
                    try {
                        val path = mCursor.getString(mCursor
                                .getColumnIndex(MediaStore.Images.Media.DATA))
                        // 拿到第一张图片的路径
                        if (firstImage == null)
                            firstImage = path
                        // 获取该图片的父路径名
                        val parentFile = File(path).parentFile ?: continue
                        val dirPath = parentFile.absolutePath
                        var imageFloder: ImageFloder? = null
                        // 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
                        if (mDirPaths.contains(dirPath)) {
                            continue
                        } else {
                            mDirPaths.add(dirPath)
                            // 初始化imageFloder
                            imageFloder = ImageFloder()
                            imageFloder.dir = dirPath
                            imageFloder.firstImagePath = path
                        }

                        val picSize = parentFile.list { dir, filename ->
                            filename.endsWith(".jpg")
                                    || filename.endsWith(".png")
                                    || filename.endsWith(".jpeg")
                        }.size
                        totalCount += picSize

                        imageFloder.count = picSize
                        mImageFloders.add(imageFloder)

                        if (picSize > mPicsSize) {
                            mPicsSize = picSize
                            mImgDir = parentFile
                        }
                    } catch (e: Exception) {
                    }

                }
                mCursor.close()

                // 扫描完成，辅助的HashSet也就可以释放内存了
                mDirPaths.clear()
                // 通知Handler扫描图片完成
                mHandler.sendEmptyMessage(0x110)
            } catch (e: Exception) {
            }
        }).start()

    }



    private val albumClickListener = object : AlbumAdapter.AlbumClickListener {
        override fun itemClick(position: Int) {
            if (isSigleMode) {
                val data = Intent()
                data.putExtra(EXTRAS, mAdapter?.getItem(position)?.absolutePath)
                setResult(Activity.RESULT_OK, data)
                finish()
            } else {
                val intent = Intent(this@AlbumActivity, AlbumPreviewActivity::class.java)
                val bundle = Bundle()
                bundle.putSerializable(EXTRA_IMAGE_LIST, mAdapter?.mDatas as Serializable)
                bundle.putSerializable(EXTRA_HAS_SELECTED_IMAGE, mAdapter?.mSelectedImage as Serializable)
                bundle.putInt(EXTRA_CURRENT_POSITION, position)
                bundle.putInt(EXTRA_IMAGE_SELECT_COUNT, max_optional_count)
                intent.putExtras(bundle)
                startActivityForResult(intent, REQUEST_PREVIEW_CODE)
            }
        }

        override fun checkedChange(selectedCount: Int) {
            if (!isSigleMode) {
                chHead.getTvRight().isEnabled = selectedCount > 0
                tvPreview.isEnabled = selectedCount > 0
                if (selectedCount > 0) {
                    chHead.getTvRight().text = String.format("%s(%d/%d)", getString(R.string.album_finish), selectedCount, max_optional_count)
                    tvPreview.text = String.format("%s(%s)", getString(R.string.album_preview), selectedCount)
                } else {
                    chHead.getTvRight().setText(R.string.album_finish)
                    tvPreview.setText(R.string.album_preview)
                }
            }

        }
    }

    override fun selected(floder: ImageFloder?) {
        if (floder == null) {
            loadAllPhotos()
            mAlbumName.setText(R.string.album_all_photos)
            mListImageDirPopupWindow.dismiss()
        } else {
            mImgDir = File(floder.dir)
            val mImgs = Arrays.asList(*mImgDir?.listFiles { dir, filename ->
                filename.endsWith(".jpg") || filename.endsWith(".png")
                        || filename.endsWith(".jpeg")
            })
            Collections.sort(mImgs, AlbumFileComparator())//通过重写Comparator的实现类
            /**
             * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
             */

            mAdapter?.updateAll(mImgs)
            mAlbumName.text = floder.name?.substring(1)
            mListImageDirPopupWindow.dismiss()
        }

    }

    fun onClickListener(v: View) {
        when(v.id){
            R.id.album_layout_tv_album_name ->{
                mListImageDirPopupWindow.let {
                    if (it.allPhotoCount > 0) {
                        it.animationStyle = R.style.anim_popup_dir
                        it.showAsDropDown(v, 0, 0)
                        // 设置背景颜色变暗
                        val lp = window.attributes
                        lp.alpha = .3f
                        window.attributes = lp
                    }
                }
            }
            R.id.album_layout_tv_album_preview ->{//预览
                val selectedImages:List<String>? = mAdapter?.mSelectedImage
                val files = ArrayList<File>()
                selectedImages?.let {
                    for (i in it.indices.reversed()) {
                        files.add(File(selectedImages[i]))
                    }
                    val intent = Intent(this@AlbumActivity, AlbumPreviewActivity::class.java)
                    val bundle = Bundle()
                    bundle.putSerializable(EXTRA_IMAGE_LIST, files as Serializable)
                    bundle.putSerializable(EXTRA_HAS_SELECTED_IMAGE, it as Serializable)
                    bundle.putInt(EXTRA_CURRENT_POSITION, 0)
                    bundle.putInt(EXTRA_IMAGE_SELECT_COUNT, max_optional_count)
                    intent.putExtras(bundle)
                    startActivityForResult(intent, REQUEST_PREVIEW_CODE)
                }
            }
            R.id.common_head_layout_tv_right ->{
                val data = Intent()
                data.putExtra(EXTRAS, mAdapter?.mSelectedImage as Serializable)
                setResult(Activity.RESULT_OK, data)
                this.finish()
            }
            R.id.common_head_layout_iv_left ->{
                this.finish()
            }
        }
    }

    private val mHandler = object : Handler() {
        override fun handleMessage(msg: android.os.Message) {
            mProgressDialog.dismiss()
            // 为View绑定数据
            loadAllPhotos()
            // 初始化展示文件夹的popupWindw
            initListDirPopupWindw()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PREVIEW_CODE && resultCode == Activity.RESULT_CANCELED) {
            mAdapter?.mSelectedImage = data?.getSerializableExtra(EXTRAS) as ArrayList<String>
        } else if (requestCode == REQUEST_PREVIEW_CODE && resultCode == Activity.RESULT_OK) {
            val intent = Intent()
            intent.putExtra(EXTRAS, data?.getSerializableExtra(EXTRAS) as ArrayList<String> as Serializable)
            setResult(Activity.RESULT_OK, data)
            this.finish()
        }
    }

    override fun onDestroy() {
        mAdapter?.onDestory()
        mListImageDirPopupWindow.onDestory()
        super.onDestroy()
    }

    companion object {
        val EXTRAS_SIGLE_MODE = "extras_sigle_mode"//Finish的结果集
        val EXTRA_IMAGE_SELECT_COUNT = "extra_image_select_count"
        var EXTRAS = "extras"//Finish的结果集
    }
}
