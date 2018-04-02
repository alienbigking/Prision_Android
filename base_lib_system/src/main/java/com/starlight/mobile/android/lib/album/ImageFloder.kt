package com.starlight.mobile.android.lib.album

/**
 * Created by Raleigh on 15/7/10.
 */
class ImageFloder {
    /**
     * 图片的文件夹路径
     */
    var dir: String? = null
        set(dir) {
            field = dir
            val lastIndexOf = this.dir?.lastIndexOf("/")
            lastIndexOf?.let {
                this.name = this.dir?.substring(it)
            }
        }

    /**
     * 第一张图片的路径
     */
    var firstImagePath: String? = null

    /**
     * 文件夹的名称
     */
    var name: String? = null
        private set

    /**
     * 图片的数量
     */
    var count: Int = 0


}
