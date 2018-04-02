/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.starlight.mobile.android.lib.view.photoview

import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView

import com.starlight.mobile.android.lib.view.photoview.PhotoViewAttacher.OnMatrixChangedListener
import com.starlight.mobile.android.lib.view.photoview.PhotoViewAttacher.OnPhotoTapListener
import com.starlight.mobile.android.lib.view.photoview.PhotoViewAttacher.OnShortTouchListener
import com.starlight.mobile.android.lib.view.photoview.PhotoViewAttacher.OnViewTapListener


/**图片浏览的控件
 * @author raleigh
 */
class PhotoView @JvmOverloads constructor(context: Context, attr: AttributeSet? = null, defStyle: Int = 0) : AppCompatImageView(context, attr, defStyle) {

    private val mPhotoViewImlp = object : IPhotoView {
        override fun setPhotoViewRotation(rotationDegree: Float) {
            mAttacher?.mPhotoViewImlp?.setPhotoViewRotation(rotationDegree)
        }

        override fun canZoom(): Boolean {
            return mAttacher?.mPhotoViewImlp?.canZoom()?:false
        }

        override val displayRect: RectF?
            get() = mAttacher?.mPhotoViewImlp?.displayRect

        override val displayMatrix: Matrix?
            get() = mAttacher?.drawMatrix

        override fun setDisplayMatrix(finalRectangle: Matrix): Boolean {
            return mAttacher?.mPhotoViewImlp?.setDisplayMatrix(finalRectangle)?:false
        }



        override var minimumScale: Float?
            get() = mAttacher?.mPhotoViewImlp?.minimumScale
            set(minimumScale) {
                mAttacher?.mPhotoViewImlp?.minimumScale = minimumScale
            }



        override var mediumScale: Float?
            get() = mAttacher?.mPhotoViewImlp?.mediumScale
            set(mediumScale) {
                mAttacher?.mPhotoViewImlp?.mediumScale = mediumScale
            }


        override var maximumScale: Float?
            get() = mAttacher?.mPhotoViewImlp?.maximumScale
            set(maximumScale) {
                mAttacher?.mPhotoViewImlp?.maximumScale = maximumScale
            }

        override var scale: Float?
            get() = mAttacher?.mPhotoViewImlp?.scale
            set(scale) {
                mAttacher?.mPhotoViewImlp?.scale = scale
            }

        override var scaleType: ImageView.ScaleType?
            get() = mAttacher?.mPhotoViewImlp?.scaleType
            set(scaleType) = if (null != mAttacher) {
                mAttacher.mPhotoViewImlp.scaleType = scaleType
            } else {
                mPendingScaleType = scaleType
            }

        override fun setAllowParentInterceptOnEdge(allow: Boolean) {
            mAttacher?.mPhotoViewImlp?.setAllowParentInterceptOnEdge(allow)
        }

        override fun setOnMatrixChangeListener(listener: OnMatrixChangedListener) {
            mAttacher?.mPhotoViewImlp?.setOnMatrixChangeListener(listener)
        }

        override fun setOnLongClickListener(l: View.OnLongClickListener) {
            mAttacher?.mPhotoViewImlp?.setOnLongClickListener(l)
        }

        override fun setOnPhotoTapListener(listener: OnPhotoTapListener) {
            mAttacher?.mPhotoViewImlp?.setOnPhotoTapListener(listener)
        }

        override fun setOnViewTapListener(listener: OnViewTapListener) {
            mAttacher?.mPhotoViewImlp?.setOnViewTapListener(listener)
        }

        override fun setScale(scale: Float, animate: Boolean) {
            mAttacher?.mPhotoViewImlp?.setScale(scale, animate)
        }

        override fun setScale(scale: Float, focalX: Float, focalY: Float, animate: Boolean) {
            mAttacher?.mPhotoViewImlp?.setScale(scale, focalX, focalY, animate)
        }

        override fun setZoomable(zoomable: Boolean) {
            mAttacher?.mPhotoViewImlp?.setZoomable(zoomable)
        }

    }
    private val mAttacher: PhotoViewAttacher?

    private var mPendingScaleType: ImageView.ScaleType? = null

    init {
        super.setScaleType(ImageView.ScaleType.MATRIX)
        mAttacher = PhotoViewAttacher(this)

        mPendingScaleType?.let{
            scaleType = it
        }
        mPendingScaleType = null
    }

    /**点击一下的监听器
     * @param onShortTouchListener
     */
    fun setOnShortTouchListener(onShortTouchListener: OnShortTouchListener) {
        mAttacher?.onShortTouchListener=onShortTouchListener
    }

    override // setImageBitmap calls through to this method
    fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        mAttacher?.update()
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        mAttacher?.update()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        mAttacher?.update()
    }


    override fun onDetachedFromWindow() {
        mAttacher?.cleanup()
        super.onDetachedFromWindow()
    }

}