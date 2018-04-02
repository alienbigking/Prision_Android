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
import android.graphics.Matrix.ScaleToFit
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewParent
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.ImageView
import android.widget.ImageView.ScaleType

import java.lang.ref.WeakReference

import android.view.MotionEvent.ACTION_CANCEL
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP

class PhotoViewAttacher(imageView: ImageView) : View.OnTouchListener, OnGestureListener, GestureDetector.OnDoubleTapListener, ViewTreeObserver.OnGlobalLayoutListener {
    val mPhotoViewImlp = object : IPhotoView {
        override val displayMatrix: Matrix
            get() = Matrix(mSuppMatrix)

        override fun setAllowParentInterceptOnEdge(allow: Boolean) {
            mAllowParentInterceptOnEdge = allow
        }

        override fun setOnLongClickListener(listener: OnLongClickListener) {
            mLongClickListener = listener
        }

        override fun setOnMatrixChangeListener(listener: OnMatrixChangedListener) {
            mMatrixChangeListener = listener
        }

        override fun setOnPhotoTapListener(listener: OnPhotoTapListener) {
            mPhotoTapListener = listener
        }

        override fun setOnViewTapListener(listener: OnViewTapListener) {
            mViewTapListener = listener
        }

        override fun setScale(scale: Float, animate: Boolean) {

            imageView.let {
                setScale(scale,
                        (it.right / 2).toFloat(),
                        (it.bottom / 2).toFloat(),
                        animate)
            }
        }

        override fun setScale(scale: Float, focalX: Float, focalY: Float,
                              animate: Boolean) {

            imageView.let{
                // Check to see if the scale is within bounds
                if (scale < mMinScale || scale > mMaxScale) {
                    return
                }

                if (animate) {
                    it.post(AnimatedZoomRunnable(scale, scale,
                            focalX, focalY))
                } else {
                    mSuppMatrix.setScale(scale, scale, focalX, focalY)
                    checkAndDisplayMatrix()
                }
            }
        }

        override fun setZoomable(zoomable: Boolean) {
            mZoomEnabled = zoomable
            update()
        }

        override fun canZoom(): Boolean {
            return mZoomEnabled
        }

        override val displayRect: RectF?
            get() {
                checkMatrixBounds()
                return getDisplayRect(drawMatrix)
            }

        override fun setDisplayMatrix(finalMatrix: Matrix): Boolean {
            if (finalMatrix == null)
                throw IllegalArgumentException("Matrix cannot be null")

            if (null == imageView)
                return false

            if (null == imageView.drawable)
                return false

            mSuppMatrix.set(finalMatrix)
            setImageViewMatrix(drawMatrix)
            checkMatrixBounds()

            return true
        }


        override var minimumScale: Float?
            get() = mMinScale
            set(minimumScale) {
                checkZoomLevels(minimumScale?:0f, mMidScale, mMaxScale)
                mMinScale = minimumScale?:0f
            }


        override var mediumScale: Float?
            get() = mMidScale
            set(mediumScale) {
                checkZoomLevels(mMinScale, mediumScale?:0f, mMaxScale)
                mMidScale = mediumScale?:0f
            }


        override var maximumScale: Float?
            get() = mMaxScale
            set(maximumScale) {
                checkZoomLevels(mMinScale, mMidScale, maximumScale?:0f)
                mMaxScale = maximumScale?:0f
            }

        override var scale: Float?
            get() = Math.sqrt((Math.pow(getValue(mSuppMatrix, Matrix.MSCALE_X).toDouble(), 2.0).toFloat() + Math.pow(getValue(mSuppMatrix, Matrix.MSKEW_Y).toDouble(), 2.0).toFloat()).toDouble()).toFloat()
            set(scale) = setScale(scale?:0f, false)
        // Finally update
        override  var scaleType: ScaleType?
            get() = mScaleType
            set(scaleType) {
                if (isSupportedScaleType(scaleType) && scaleType != mScaleType) {
                    scaleType?.let {
                        mScaleType = it
                        update()
                    }
                }
            }
        override fun setPhotoViewRotation(degrees: Float) {
            var degrees = degrees
            degrees %= 360f
            mSuppMatrix.postRotate(mLastRotation - degrees)
            mLastRotation = degrees
            checkAndDisplayMatrix()
        }
    }

    private var mMinScale = DEFAULT_MIN_SCALE
    private var mMidScale = DEFAULT_MID_SCALE
    private var mMaxScale = DEFAULT_MAX_SCALE

    private var mAllowParentInterceptOnEdge = true

    private var mImageView: WeakReference<ImageView>? = null

    // Gesture Detectors
    private val mGestureDetector: GestureDetector?
    private val mScaleDragDetector: com.starlight.mobile.android.lib.view.photoview.GestureDetector?

    // These are set so we don't keep allocating them on the heap
    private val mBaseMatrix = Matrix()
    private val mDrawMatrix = Matrix()
    private val mSuppMatrix = Matrix()
    private val mDisplayRect = RectF()
    private val mMatrixValues = FloatArray(9)

    // Listeners
    private var mMatrixChangeListener: OnMatrixChangedListener? = null
    private var mPhotoTapListener: OnPhotoTapListener? = null
    private var mViewTapListener: OnViewTapListener? = null
    private var mLongClickListener: OnLongClickListener? = null

    private var mIvTop: Int = 0
    private var mIvRight: Int = 0
    private var mIvBottom: Int = 0
    private var mIvLeft: Int = 0
    private var mCurrentFlingRunnable: FlingRunnable? = null
    private var mScrollEdge = EDGE_BOTH

    private var mZoomEnabled: Boolean = false
    private var mScaleType = ScaleType.FIT_CENTER

    init {
        mImageView = WeakReference(imageView)

        imageView.setOnTouchListener(this)

        val observer = imageView.viewTreeObserver
        observer?.addOnGlobalLayoutListener(this)

        // Make sure we using MATRIX Scale Type
        setImageViewScaleTypeMatrix(imageView)

        if (imageView.isInEditMode) {
//            return
        }
        // Create Gesture Detectors...
        mScaleDragDetector = VersionedGestureDetector.newInstance(
                imageView.context, this)

        mGestureDetector = GestureDetector(imageView.context,
                object : GestureDetector.SimpleOnGestureListener() {

                    // forward long click mPhotoFromClickListener
                    override fun onLongPress(e: MotionEvent) {
                        mLongClickListener?.onLongClick(imageView)
                    }
                })

        mGestureDetector.setOnDoubleTapListener(this)

        // Finally, update the UI so that we're zoomable
        mPhotoViewImlp.setZoomable(true)
    }


    /**
     * Clean-up the resources attached to this object. This needs to be called
     * when the ImageView is no longer used. A good example is from
     * [View.onDetachedFromWindow] or from
     * [android.app.Activity.onDestroy]. This is automatically called if
     * you are using [].
     */
    fun cleanup() {
        if (null == mImageView) {
            return  // cleanup already done
        }

        val imageView = mImageView?.get()
        imageView?.let{
            // Remove this as a global layout mPhotoFromClickListener
            val observer = imageView.viewTreeObserver
            observer?.let {
                if ( it.isAlive) {
                    it.removeGlobalOnLayoutListener(this)
                }
            }
            // Remove the ImageView's reference to this
            imageView.setOnTouchListener(null)

            // make sure a pending fling runnable won't be run
            cancelFling()
        }

        mGestureDetector?.setOnDoubleTapListener(null)

        // Clear listeners too
        mMatrixChangeListener = null
        mPhotoTapListener = null
        mViewTapListener = null

        // Finally, clear ImageView
        mImageView = null
    }


    private var mLastRotation = 0f


    // If we don't have an ImageView, call cleanup()
    val imageView: ImageView?
        get() {
            var imageView: ImageView? = null
            mImageView?.let {
                imageView = mImageView?.get()
            }
            if (null == imageView) {
                cleanup()
                Log.i(LOG_TAG,
                        "ImageView no longer exists. You should not use this PhotoViewAttacher any more.")
            }

            return imageView
        }


    override fun onDoubleTap(ev: MotionEvent): Boolean {
        try {
            val scale = mPhotoViewImlp.scale
            val x = ev.x
            val y = ev.y

            scale?.let{
                if (it < mMidScale) {
                    mPhotoViewImlp.setScale(mMidScale, x, y, true)
                } else if (it >= mMidScale && it < mMaxScale) {
                    mPhotoViewImlp.setScale(mMaxScale, x, y, true)
                } else {
                    mPhotoViewImlp.setScale(mMinScale, x, y, true)
                }
            }

        } catch (e: ArrayIndexOutOfBoundsException) {
            // Can sometimes happen when getX() and getY() is called
        }

        return true
    }

    override fun onDoubleTapEvent(e: MotionEvent): Boolean {
        // Wait for the confirmed onDoubleTap() instead
        return false
    }

    override fun onDrag(dx: Float, dy: Float) {
        if (DEBUG) {
        }

        val imageView = imageView
        mSuppMatrix.postTranslate(dx, dy)
        checkAndDisplayMatrix()

        /**
         * Here we decide whether to let the ImageView's parent to start taking
         * over the touch event.
         *
         * First we check whether this function is enabled. We never want the
         * parent to take over if we're scaling. We then check the edge we're
         * on, and the direction of the scroll (i.e. if we're pulling against
         * the edge, aka 'overscrolling', let the parent take over).
         */
        if (mAllowParentInterceptOnEdge && mScaleDragDetector?.isScaling != true) {
            if (mScrollEdge == EDGE_BOTH
                    || mScrollEdge == EDGE_LEFT && dx >= 1f
                    || mScrollEdge == EDGE_RIGHT && dx <= -1f) {
                val parent = imageView?.parent
                parent?.requestDisallowInterceptTouchEvent(false)
            }
        }
    }

    override fun onFling(startX: Float, startY: Float, velocityX: Float,
                         velocityY: Float) {
        if (DEBUG) {

        }
        val imageView = imageView
        imageView?.let{
            mCurrentFlingRunnable = FlingRunnable(it.context)
            mCurrentFlingRunnable?.fling(getImageViewWidth(it),
                    getImageViewHeight(it), velocityX.toInt(), velocityY.toInt())
            it.post(mCurrentFlingRunnable)
        }

    }

    override fun onGlobalLayout() {
        val imageView = imageView
        imageView?.let {
            if (mZoomEnabled) {
                val top = it.top
                val right = it.right
                val bottom = it.bottom
                val left = it.left

                /**
                 * We need to check whether the ImageView's bounds have changed.
                 * This would be easier if we targeted API 11+ as we could just use
                 * View.OnLayoutChangeListener. Instead we have to replicate the
                 * work, keeping track of the ImageView's bounds and then checking
                 * if the values change.
                 */
                if (top != mIvTop || bottom != mIvBottom || left != mIvLeft
                        || right != mIvRight) {
                    // Update our base matrix, as the bounds have changed
                    updateBaseMatrix(it.drawable)
                    // Update values as something has changed
                    mIvTop = top
                    mIvRight = right
                    mIvBottom = bottom
                    mIvLeft = left
                }
            }
        }
    }

    override fun onScale(scaleFactor: Float, focusX: Float, focusY: Float) {
        if (DEBUG) {

        }

        if (mPhotoViewImlp.scale ?:0f< mMaxScale || scaleFactor < 1f) {
            mSuppMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY)
            checkAndDisplayMatrix()
        }
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        val imageView = imageView

        mPhotoTapListener?.let {
            val displayRect = mPhotoViewImlp.displayRect

            if (null != displayRect) {
                val x = e.x
                val y = e.y
                // Check to see if the user tapped on the photo
                if (displayRect.contains(x, y)) {

                    val xResult = (x - displayRect.left) / displayRect.width()
                    val yResult = (y - displayRect.top) / displayRect.height()
                    it.onPhotoTap(imageView, xResult, yResult)
                    return true
                }
            }
        }
        mViewTapListener?.onViewTap(imageView, e.x, e.y)

        return false
    }

    interface OnShortTouchListener {
        fun back(upX: Float, upY: Float)
        fun doubleTab()

    }

    var onShortTouchListener: OnShortTouchListener? = null

    private var preX: Float = 0.toFloat()
    private var preY: Float = 0.toFloat()
    private var curX: Float = 0.toFloat()
    private var curY: Float = 0.toFloat()
    override fun onTouch(v: View, ev: MotionEvent): Boolean {
        var handled = false

        if (mZoomEnabled && hasDrawable(v as ImageView)) {
            val parent = v.getParent()
            when (ev.action) {
                ACTION_DOWN -> {
                    preX = ev.x
                    preY = ev.y
                    // First, disable the Parent from intercepting the touch
                    // event
                    parent?.requestDisallowInterceptTouchEvent(true) ?: Log.i(LOG_TAG, "onTouch getParent() returned null")

                    // If we're flinging, and the user presses down, cancel
                    // fling
                    cancelFling()
                }

                ACTION_CANCEL, ACTION_UP -> {
                    curX = ev.x
                    curY = ev.y
                    //点击一下 没有双击
                    if ((null == mGestureDetector || !mGestureDetector.onTouchEvent(ev))
                            && Math.abs(curX - preX) < 10 && Math.abs(curY - preY) < 10) {
                        onShortTouchListener?.let {
                            it.back(curX, curY)
                        }
                    }

                    // If the user has zoomed less than min scale, zoom back
                    // to min scale
                    if (mPhotoViewImlp.scale?:0f < mMinScale) {
                        val rect = mPhotoViewImlp.displayRect
                        rect?.let {
                            v.post(AnimatedZoomRunnable(mPhotoViewImlp.scale?:0f, mMinScale,
                                    it.centerX(), it.centerY()))
                            handled = true
                        }
                    }
                }
            }

            // Check to see if the user double tapped
            mGestureDetector?.let {
                if (it.onTouchEvent(ev)) {
                    handled = true
                    onShortTouchListener?.let {
                        it.doubleTab()
                    }
                }
            }
            if (!handled) {
                parent.requestDisallowInterceptTouchEvent(false)
            }
            // Finally, try the Scale/Drag detector
            if (mScaleDragDetector?.onTouchEvent(ev) == true) {
                handled = true
            }
        }

        return handled
    }


    fun update() {
        val imageView = imageView

        imageView?.let{
            if (mZoomEnabled) {
                // Make sure we using MATRIX Scale Type
                setImageViewScaleTypeMatrix(it)

                // Update the base matrix using the current drawable
                updateBaseMatrix(it.drawable)
            } else {
                // Reset the Matrix...
                resetMatrix()
            }
        }
    }


    val drawMatrix: Matrix
        get() {
            mDrawMatrix.set(mBaseMatrix)
            mDrawMatrix.postConcat(mSuppMatrix)
            return mDrawMatrix
        }

    private fun cancelFling() {
        mCurrentFlingRunnable?.let {
            it.cancelFling()
        }
        mCurrentFlingRunnable = null
    }

    /**
     * Helper method that simply checks the Matrix, and then displays the result
     */
    private fun checkAndDisplayMatrix() {
        if (checkMatrixBounds()) {
            setImageViewMatrix(drawMatrix)
        }
    }

    private fun checkImageViewScaleType() {
        val imageView = imageView

        /**
         * PhotoView's getScaleType() will just divert to this.getScaleType() so
         * only call if we're not attached to a PhotoView.
         */
        if (null != imageView && imageView !is PhotoView) {
            if (ScaleType.MATRIX != imageView.scaleType) {
                throw IllegalStateException(
                        "The ImageView's ScaleType has been changed since attaching a PhotoViewAttacher")
            }
        }
    }

    private fun checkMatrixBounds(): Boolean {
        val imageView = imageView ?: return false

        val rect = getDisplayRect(drawMatrix) ?: return false

        val height = rect.height()
        val width = rect.width()
        var deltaX = 0f
        var deltaY = 0f

        val viewHeight = getImageViewHeight(imageView)
        if (height <= viewHeight) {
            when (mScaleType) {
                ImageView.ScaleType.FIT_START -> deltaY = -rect.top
                ImageView.ScaleType.FIT_END -> deltaY = viewHeight.toFloat() - height - rect.top
                else -> deltaY = (viewHeight - height) / 2 - rect.top
            }
        } else if (rect.top > 0) {
            deltaY = -rect.top
        } else if (rect.bottom < viewHeight) {
            deltaY = viewHeight - rect.bottom
        }

        val viewWidth = getImageViewWidth(imageView)
        if (width <= viewWidth) {
            when (mScaleType) {
                ImageView.ScaleType.FIT_START -> deltaX = -rect.left
                ImageView.ScaleType.FIT_END -> deltaX = viewWidth.toFloat() - width - rect.left
                else -> deltaX = (viewWidth - width) / 2 - rect.left
            }
            mScrollEdge = EDGE_BOTH
        } else if (rect.left > 0) {
            mScrollEdge = EDGE_LEFT
            deltaX = -rect.left
        } else if (rect.right < viewWidth) {
            deltaX = viewWidth - rect.right
            mScrollEdge = EDGE_RIGHT
        } else {
            mScrollEdge = EDGE_NONE
        }

        // Finally actually translate the matrix
        mSuppMatrix.postTranslate(deltaX, deltaY)
        return true
    }

    /**
     * Helper method that maps the supplied Matrix to the current Drawable
     *
     * @param matrix - Matrix to map Drawable against
     * @return RectF - Displayed Rectangle
     */
    private fun getDisplayRect(matrix: Matrix): RectF? {
        val imageView = imageView

        imageView?.let {
            val d = imageView.drawable
            if (null != d) {
                mDisplayRect.set(0f, 0f, d.intrinsicWidth.toFloat(),
                        d.intrinsicHeight.toFloat())
                matrix.mapRect(mDisplayRect)
                return mDisplayRect
            }
        }
        return null
    }

    /**
     * Helper method that 'unpacks' a Matrix and returns the required value
     *
     * @param matrix     - Matrix to unpack
     * @param whichValue - Which value from Matrix.M* to return
     * @return float - returned value
     */
    private fun getValue(matrix: Matrix, whichValue: Int): Float {
        matrix.getValues(mMatrixValues)
        return mMatrixValues[whichValue]
    }

    /**
     * Resets the Matrix back to FIT_CENTER, and then displays it.s
     */
    private fun resetMatrix() {
        mSuppMatrix.reset()
        setImageViewMatrix(drawMatrix)
        checkMatrixBounds()
    }

    private fun setImageViewMatrix(matrix: Matrix) {
        val imageView = imageView
        imageView?.let{

            checkImageViewScaleType()
            it.imageMatrix = matrix

            // Call MatrixChangedListener if needed
            if (null != mMatrixChangeListener) {
                val displayRect = getDisplayRect(matrix)
                if (null != displayRect) {
                    mMatrixChangeListener?.onMatrixChanged(displayRect)
                }
            }
        }
    }

    /**
     * Calculate Matrix for FIT_CENTER
     *
     * @param d - Drawable being displayed
     */
    private fun updateBaseMatrix(d: Drawable?) {
        val imageView = imageView
        if (null == imageView || null == d) {
            return
        }

        val viewWidth = getImageViewWidth(imageView).toFloat()
        val viewHeight = getImageViewHeight(imageView).toFloat()
        val drawableWidth = d.intrinsicWidth
        val drawableHeight = d.intrinsicHeight

        mBaseMatrix.reset()

        val widthScale = viewWidth / drawableWidth
        val heightScale = viewHeight / drawableHeight

        if (mScaleType == ScaleType.CENTER) {
            mBaseMatrix.postTranslate((viewWidth - drawableWidth) / 2f,
                    (viewHeight - drawableHeight) / 2f)

        } else if (mScaleType == ScaleType.CENTER_CROP) {
            val scale = Math.max(widthScale, heightScale)
            mBaseMatrix.postScale(scale, scale)
            mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2f,
                    (viewHeight - drawableHeight * scale) / 2f)

        } else if (mScaleType == ScaleType.CENTER_INSIDE) {
            val scale = Math.min(1.0f, Math.min(widthScale, heightScale))
            mBaseMatrix.postScale(scale, scale)
            mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2f,
                    (viewHeight - drawableHeight * scale) / 2f)

        } else {
            val mTempSrc = RectF(0f, 0f, drawableWidth.toFloat(), drawableHeight.toFloat())
            val mTempDst = RectF(0f, 0f, viewWidth, viewHeight)

            when (mScaleType) {
                ImageView.ScaleType.FIT_CENTER -> mBaseMatrix
                        .setRectToRect(mTempSrc, mTempDst, ScaleToFit.CENTER)

                ImageView.ScaleType.FIT_START -> mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.START)

                ImageView.ScaleType.FIT_END -> mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.END)

                ImageView.ScaleType.FIT_XY -> mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.FILL)

                else -> {
                }
            }
        }

        resetMatrix()
    }

    private fun getImageViewWidth(imageView: ImageView?): Int {
        return if (null == imageView) 0 else imageView.width - imageView.paddingLeft - imageView.paddingRight
    }

    private fun getImageViewHeight(imageView: ImageView?): Int {
        return if (null == imageView) 0 else imageView.height - imageView.paddingTop - imageView.paddingBottom
    }

    /**
     * Interface definition for a callback to be invoked when the internal
     * Matrix has changed for this View.
     *
     * @author Chris Banes
     */
    interface OnMatrixChangedListener {
        /**
         * Callback for when the Matrix displaying the Drawable has changed.
         * This could be because the View's bounds have changed, or the user has
         * zoomed.
         *
         * @param rect - Rectangle displaying the Drawable's new bounds.
         */
        fun onMatrixChanged(rect: RectF)
    }

    /**
     * Interface definition for a callback to be invoked when the Photo is
     * tapped with a single tap.
     *
     * @author Chris Banes
     */
    interface OnPhotoTapListener {

        /**
         * A callback to receive where the user taps on a photo. You will only
         * receive a callback if the user taps on the actual photo, tapping on
         * 'whitespace' will be ignored.
         *
         * @param view - View the user tapped.
         * @param x    - where the user tapped from the of the Drawable, as
         * percentage of the Drawable width.
         * @param y    - where the user tapped from the top of the Drawable, as
         * percentage of the Drawable height.
         */
        fun onPhotoTap(view: View?, x: Float, y: Float)
    }

    /**
     * Interface definition for a callback to be invoked when the ImageView is
     * tapped with a single tap.
     *
     * @author Chris Banes
     */
    interface OnViewTapListener {

        /**
         * A callback to receive where the user taps on a ImageView. You will
         * receive a callback if the user taps anywhere on the view, tapping on
         * 'whitespace' will not be ignored.
         *
         * @param view - View the user tapped.
         * @param x    - where the user tapped from the left of the View.
         * @param y    - where the user tapped from the top of the View.
         */
        fun onViewTap(view: View?, x: Float, y: Float)
    }

    private inner class AnimatedZoomRunnable(private val mZoomStart: Float, private val mZoomEnd: Float,
                                             private val mFocalX: Float, private val mFocalY: Float) : Runnable {
        private val mStartTime: Long

        init {
            mStartTime = System.currentTimeMillis()
        }

        override fun run() {
            val imageView = imageView ?: return

            val t = interpolate()
            val scale = mZoomStart + t * (mZoomEnd - mZoomStart)
            val deltaScale = scale / (mPhotoViewImlp.scale ?:0f)
            mSuppMatrix.postScale(deltaScale, deltaScale, mFocalX, mFocalY)
            checkAndDisplayMatrix()

            // We haven't hit our target scale yet, so post ourselves again
            if (t < 1f) {
                Compat.postOnAnimation(imageView, this)
            }
        }

        private fun interpolate(): Float {
            var t = 1f * (System.currentTimeMillis() - mStartTime) / ZOOM_DURATION
            t = Math.min(1f, t)
            t = sInterpolator.getInterpolation(t)
            return t
        }
    }

    private inner class FlingRunnable(context: Context) : Runnable {

        private val mScroller: ScrollerProxy
        private var mCurrentX: Int = 0
        private var mCurrentY: Int = 0

        init {
            mScroller = ScrollerProxy.getScroller(context)
        }

        fun cancelFling() {
            if (DEBUG) {
            }
            mScroller.forceFinished(true)
        }

        fun fling(viewWidth: Int, viewHeight: Int, velocityX: Int,
                  velocityY: Int) {
            val rect = mPhotoViewImlp.displayRect ?: return

            val startX = Math.round(-rect.left)
            val minX: Int
            val maxX: Int
            val minY: Int
            val maxY: Int

            if (viewWidth < rect.width()) {
                minX = 0
                maxX = Math.round(rect.width() - viewWidth)
            } else {
                maxX = startX
                minX = maxX
            }

            val startY = Math.round(-rect.top)
            if (viewHeight < rect.height()) {
                minY = 0
                maxY = Math.round(rect.height() - viewHeight)
            } else {
                maxY = startY
                minY = maxY
            }

            mCurrentX = startX
            mCurrentY = startY

            if (DEBUG) {

            }

            // If we actually can move, fling the scroller
            if (startX != maxX || startY != maxY) {
                mScroller.fling(startX, startY, velocityX, velocityY, minX,
                        maxX, minY, maxY, 0, 0)
            }
        }

        override fun run() {
            if (mScroller.isFinished) {
                return  // remaining post that should not be handled
            }

            val imageView = imageView
            if (null != imageView && mScroller.computeScrollOffset()) {

                val newX = mScroller.currX
                val newY = mScroller.currY

                if (DEBUG) {
                }

                mSuppMatrix.postTranslate((mCurrentX - newX).toFloat(), (mCurrentY - newY).toFloat())
                setImageViewMatrix(drawMatrix)

                mCurrentX = newX
                mCurrentY = newY

                // Post On animation
                Compat.postOnAnimation(imageView, this)
            }
        }
    }

    companion object {

        private val LOG_TAG = "PhotoViewAttacher"

        // let debug flag be dynamic, but still Proguard can be used to remove from
        // release builds
        private val DEBUG = Log.isLoggable(LOG_TAG, Log.DEBUG)

        internal val sInterpolator: Interpolator = AccelerateDecelerateInterpolator()
        internal val ZOOM_DURATION = 200

        internal val EDGE_NONE = -1
        internal val EDGE_LEFT = 0
        internal val EDGE_RIGHT = 1
        internal val EDGE_BOTH = 2

        val DEFAULT_MAX_SCALE = 3.0f
        val DEFAULT_MID_SCALE = 1.75f
        val DEFAULT_MIN_SCALE = 1.0f

        private fun checkZoomLevels(minZoom: Float, midZoom: Float,
                                    maxZoom: Float) {
            if (minZoom >= midZoom) {
                throw IllegalArgumentException(
                        "MinZoom has to be less than MidZoom")
            } else if (midZoom >= maxZoom) {
                throw IllegalArgumentException(
                        "MidZoom has to be less than MaxZoom")
            }
        }

        /**
         * @return true if the ImageView exists, and it's Drawable existss
         */
        private fun hasDrawable(imageView: ImageView?): Boolean {
            return null != imageView && null != imageView.drawable
        }

        /**
         * @return true if the ScaleType is supported.
         */
        private fun isSupportedScaleType(scaleType: ScaleType?): Boolean {
            if (null == scaleType) {
                return false
            }

            when (scaleType) {
                ImageView.ScaleType.MATRIX -> throw IllegalArgumentException(scaleType.name + " is not supported in PhotoView")

                else -> return true
            }
        }

        /**
         * Set's the ImageView's ScaleType to Matrix.
         */
        private fun setImageViewScaleTypeMatrix(imageView: ImageView?) {
            /**
             * PhotoView sets it's own ScaleType to Matrix, then diverts all calls
             * setScaleType to this.setScaleType automatically.
             */
            if (null != imageView && imageView !is PhotoView) {
                if (ScaleType.MATRIX != imageView.scaleType) {
                    imageView.scaleType = ScaleType.MATRIX
                }
            }
        }
    }

}
