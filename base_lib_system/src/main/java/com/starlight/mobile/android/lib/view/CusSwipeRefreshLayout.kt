package com.starlight.mobile.android.lib.view


import android.content.Context
import android.graphics.Canvas
import android.support.v4.view.MotionEventCompat
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation
import android.widget.AbsListView


/**自定义刷新控件
 * Created by Raleigh on 15/6/19.
 * 使用上拉下拉，eg：
 * mSwipeLayout.setOnRefreshListener(this);
 * mSwipeLayout.setOnLoadListener(this);
 * mSwipeLayout.setColor(R.color.holo_blue_bright,
 * R.color.holo_green_light,
 * R.color.holo_orange_light,
 * R.color.holo_red_light);
 * mSwipeLayout.setMode(CusSwipeRefreshLayout.Mode.BOTH);
 * mSwipeLayout.setLoadNoFull(false);
 * Controls： mSwipeLayout.setRefreshing(false);mSwipeLayout.setLoading(false);
 */


class CusSwipeRefreshLayout
/**
 * Constructor that is called when inflating SwipeRefreshLayout from XML.
 * @param context
 * @param attrs
 */
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : ViewGroup(context, attrs) {

    private val mProgressBar: SwipeProgressBar //the thing that shows progress is going
    private val mProgressBarBottom: SwipeProgressBar
    private var mTarget: View? = null //the content that gets pulled down
    private var mOriginalOffsetTop: Int = 0
    var onRefreshListener: OnRefreshListener? = null
    var onLoadListener: OnLoadListener? = null
    private var mFrom: Int = 0
    /**
     * @return Whether the SwipeRefreshWidget is actively showing refresh
     * progress.
     */
    /**
     * Notify the widget that refresh state has changed. Do not call this when
     * refresh is triggered by a swipe gesture.
     *
     * @param refreshing Whether or not the view should show refresh progress.
     */
    var isRefreshing = false
        set(refreshing) {
            if (isRefreshing != refreshing) {
                ensureTarget()
                mCurrPercentage = 0f
                field = refreshing
                if (isRefreshing) {
                    mProgressBar.start()
                } else {
                    mLastDirection = Mode.DISABLED
                    mProgressBar.stop()
                }
            }
        }
    var isLoading = false
        set(loading) {
            if (isLoading != loading) {
                ensureTarget()
                mCurrPercentage = 0f
                field = loading
                if (isLoading) {
                    mProgressBarBottom.start()
                } else {
                    mLastDirection = Mode.DISABLED
                    mProgressBarBottom.stop()
                }
            }
        }
    private val mTouchSlop: Int
    private var mDistanceToTriggerSync = -1f
    private val mMediumAnimationDuration: Int
    private var mFromPercentage = 0f
    private var mCurrPercentage = 0f
    private val mProgressBarHeight: Int
    private var mCurrentTargetOffsetTop: Int = 0

    private var mInitialMotionY: Float = 0.toFloat()
    private var mLastMotionY: Float = 0.toFloat()
    private var mIsBeingDragged: Boolean = false
    private var mActivePointerId = INVALID_POINTER

    // Target is returning to its start offset because it was cancelled or a
    // refresh was triggered.
    private var mReturningToStart: Boolean = false
    private val mDecelerateInterpolator: DecelerateInterpolator
    private val mAccelerateInterpolator: AccelerateInterpolator
    private var mMode = Mode.BOTH
    //之前手势的方向，为了解决同一个触点前后移动方向不同导致后一个方向会刷新的问题，
    //这里Mode.DISABLED无意义，只是一个初始值，和上拉/下拉方向进行区分
    private var mLastDirection = Mode.DISABLED
    private var mDirection = 0
    //当子控件移动到尽头时才开始计算初始点的位置
    private var mStartPoint: Float = 0.toFloat()
    private var up: Boolean = false
    private var down: Boolean = false
    //数据不足一屏时是否打开上拉加载模式
    private var loadNoFull = false

    init {
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        mMediumAnimationDuration = resources.getInteger(
                android.R.integer.config_mediumAnimTime)
        setWillNotDraw(false)
        mProgressBar = SwipeProgressBar(this)
        mProgressBarBottom = SwipeProgressBar(this)
        val metrics = resources.displayMetrics
        mProgressBarHeight = (metrics.density * PROGRESS_BAR_HEIGHT).toInt()
        mDecelerateInterpolator = DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR)
        mAccelerateInterpolator = AccelerateInterpolator(ACCELERATE_INTERPOLATION_FACTOR)

        val a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS)
        isEnabled = a.getBoolean(0, true)
        a.recycle()
    }


    //对下拉或上拉进行复位
    private val mAnimateToStartPosition = object : Animation() {
        public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            var targetTop = 0
            if (mFrom != mOriginalOffsetTop) {
                targetTop = mFrom + ((mOriginalOffsetTop - mFrom) * interpolatedTime).toInt()
            }
            val offset = targetTop - (mTarget?.top?:0)
            //注释掉这里，不然上拉后回复原位置会很快，不平滑
            //            final int currentTop = mTarget.getTop();
            //            if (offset + currentTop < 0) {
            //                offset = 0 - currentTop;
            //            }
            setTargetOffsetTopAndBottom(offset)
        }
    }

    //设置上方进度条的完成度百分比
    private val mShrinkTrigger = object : Animation() {
        public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            val percent = mFromPercentage + (0 - mFromPercentage) * interpolatedTime
            mProgressBar.setTriggerPercentage(percent)
        }
    }

    //设置下方进度条的完成度百分比
    private val mShrinkTriggerBottom = object : Animation() {
        public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            val percent = mFromPercentage + (0 - mFromPercentage) * interpolatedTime
            mProgressBarBottom.setTriggerPercentage(percent)
        }
    }

    //监听，回复初始位置
    private val mReturnToStartPositionListener = object : BaseAnimationListener() {
        override fun onAnimationEnd(animation: Animation) {
            // Once the target content has returned to its start position, reset
            // the target offset to 0
            mCurrentTargetOffsetTop = 0
            mLastDirection = Mode.DISABLED
        }
    }

    //回复进度条百分比
    private val mShrinkAnimationListener = object : BaseAnimationListener() {
        override fun onAnimationEnd(animation: Animation) {
            mCurrPercentage = 0f
        }
    }

    //回复初始位置
    private val mReturnToStartPosition = Runnable {
        mReturningToStart = true
        animateOffsetToStartPosition(mCurrentTargetOffsetTop + paddingTop,
                mReturnToStartPositionListener)
    }

    // Cancel the refresh gesture and animate everything back to its original state.
    private val mCancel = Runnable {
        mReturningToStart = true
        // Timeout fired since the user last moved their finger; animate the
        // trigger to 0 and put the target back at its original position
        if (mProgressBar != null || mProgressBarBottom != null) {
            mFromPercentage = mCurrPercentage
            if (mDirection > 0 && (mMode == Mode.PULL_FROM_START || mMode == Mode.BOTH)) {
                mShrinkTrigger.duration = mMediumAnimationDuration.toLong()
                mShrinkTrigger.setAnimationListener(mShrinkAnimationListener)
                mShrinkTrigger.reset()
                mShrinkTrigger.interpolator = mDecelerateInterpolator
                startAnimation(mShrinkTrigger)
            } else if (mDirection < 0 && (mMode == Mode.PULL_FROM_END || mMode == Mode.BOTH)) {
                mShrinkTriggerBottom.duration = mMediumAnimationDuration.toLong()
                mShrinkTriggerBottom.setAnimationListener(mShrinkAnimationListener)
                mShrinkTriggerBottom.reset()
                mShrinkTriggerBottom.interpolator = mDecelerateInterpolator
                startAnimation(mShrinkTriggerBottom)
            }
        }
        mDirection = 0
        animateOffsetToStartPosition(mCurrentTargetOffsetTop + paddingTop,
                mReturnToStartPositionListener)
    }


    public override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        removeCallbacks(mCancel)
        removeCallbacks(mReturnToStartPosition)
    }

    public override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeCallbacks(mReturnToStartPosition)
        removeCallbacks(mCancel)
    }

    //对子控件进行移动
    private fun animateOffsetToStartPosition(from: Int, listener: AnimationListener) {
        mFrom = from
        mAnimateToStartPosition.reset()
        mAnimateToStartPosition.duration = mMediumAnimationDuration.toLong()
        mAnimateToStartPosition.setAnimationListener(listener)
        mAnimateToStartPosition.interpolator = mDecelerateInterpolator
        mTarget?.startAnimation(mAnimateToStartPosition)
    }

    //设置进度条的显示百分比
    private fun setTriggerPercentage(percent: Float) {
        if (percent == 0f) {
            // No-op. A null trigger means it's uninitialized, and setting it to zero-percent
            // means we're trying to reset state, so there's nothing to reset in this case.
            mCurrPercentage = 0f
            return
        }
        mCurrPercentage = percent
        if ((mMode == Mode.PULL_FROM_START || mMode == Mode.BOTH)
                && mLastDirection != Mode.PULL_FROM_END && !isLoading) {
            mProgressBar.setTriggerPercentage(percent)
        } else if ((mMode == Mode.PULL_FROM_END || mMode == Mode.BOTH)
                && mLastDirection != Mode.PULL_FROM_START && !isRefreshing) {
            mProgressBarBottom.setTriggerPercentage(percent)
        }
    }


    @Deprecated("Use {@link #setColorSchemeResources(int, int, int, int)}")
    private fun setColorScheme(colorRes1: Int, colorRes2: Int, colorRes3: Int, colorRes4: Int) {
        setColorSchemeResources(colorRes1, colorRes2, colorRes3, colorRes4)
    }

    /**
     * Set the four colors used in the progress animation from color resources.
     * The first color will also be the color of the bar that grows in response
     * to a user swipe gesture.
     */
    fun setTopColor(colorRes1: Int, colorRes2: Int, colorRes3: Int,
                    colorRes4: Int) {
        setColorSchemeResources(colorRes1, colorRes2, colorRes3, colorRes4)
    }

    fun setBottomColor(colorRes1: Int, colorRes2: Int, colorRes3: Int,
                       colorRes4: Int) {
        setColorSchemeResourcesBottom(colorRes1, colorRes2, colorRes3, colorRes4)
    }

    fun setColor(colorRes1: Int, colorRes2: Int, colorRes3: Int,
                 colorRes4: Int) {
        setColorSchemeResources(colorRes1, colorRes2, colorRes3, colorRes4)
        setColorSchemeResourcesBottom(colorRes1, colorRes2, colorRes3, colorRes4)
    }

    private fun setColorSchemeResources(colorRes1: Int, colorRes2: Int, colorRes3: Int,
                                        colorRes4: Int) {
        val res = resources
        setColorSchemeColors(res.getColor(colorRes1), res.getColor(colorRes2),
                res.getColor(colorRes3), res.getColor(colorRes4))
    }

    private fun setColorSchemeResourcesBottom(colorRes1: Int, colorRes2: Int, colorRes3: Int,
                                              colorRes4: Int) {
        val res = resources
        setColorSchemeColorsBottom(res.getColor(colorRes1), res.getColor(colorRes2),
                res.getColor(colorRes3), res.getColor(colorRes4))
    }

    /**
     * Set the four colors used in the progress animation. The first color will
     * also be the color of the bar that grows in response to a user swipe
     * gesture.
     */
    private fun setColorSchemeColors(color1: Int, color2: Int, color3: Int, color4: Int) {
        ensureTarget()
        mProgressBar.setColorScheme(color1, color2, color3, color4)
    }

    private fun setColorSchemeColorsBottom(color1: Int, color2: Int, color3: Int, color4: Int) {
        ensureTarget()
        mProgressBarBottom.setColorScheme(color1, color2, color3, color4)
    }

    private fun ensureTarget() {
        // Don't bother getting the parent height if the parent hasn't been laid out yet.
        if (mTarget == null) {
            if (childCount > 1 && !isInEditMode) {
                throw IllegalStateException(
                        "SwipeRefreshLayout can host only one direct child")
            }
            mTarget = getChildAt(0)
            mOriginalOffsetTop = mTarget?.top?:0 + paddingTop
        }
        if (mDistanceToTriggerSync == -1f) {
            if ((parent as View).height > 0) {
                val metrics = resources.displayMetrics
                mDistanceToTriggerSync = Math.min(
                        (parent as View).height * MAX_SWIPE_DISTANCE_FACTOR,
                        REFRESH_TRIGGER_DISTANCE * metrics.density).toInt().toFloat()
            }
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        mProgressBar.draw(canvas)
        mProgressBarBottom.draw(canvas)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val width = measuredWidth
        val height = measuredHeight
        mProgressBar.setBounds(0, 0, width, mProgressBarHeight)
        if (childCount == 0) {
            return
        }
        val child = getChildAt(0)
        val childLeft = paddingLeft
        val childTop = mCurrentTargetOffsetTop + paddingTop
        val childWidth = width - paddingLeft - paddingRight
        val childHeight = height - paddingTop - paddingBottom
        child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight)
        mProgressBarBottom.setBounds(0, height - mProgressBarHeight, width, height)
    }

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (childCount > 1 && !isInEditMode) {
            throw IllegalStateException("SwipeRefreshLayout can host only one direct child")
        }
        if (childCount > 0) {
            getChildAt(0).measure(
                    View.MeasureSpec.makeMeasureSpec(
                            measuredWidth - paddingLeft - paddingRight,
                            View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(
                            measuredHeight - paddingTop - paddingBottom,
                            View.MeasureSpec.EXACTLY))
        }
    }

    /**
     * @return Whether it is possible for the child view of this layout to
     * scroll up. Override this if the child view is a custom view.
     */
    fun canChildScrollUp(): Boolean {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTarget is AbsListView) {
                val absListView = mTarget as AbsListView?
                return absListView?.childCount?:0 > 0 && (absListView?.firstVisiblePosition?:0 > 0 || absListView?.getChildAt(0)
                        ?.top?:0 < absListView?.paddingTop?:0)
            } else {
                return mTarget?.scrollY?:0 > 0
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget, -1)
        }
    }

    fun canChildScrollDown(): Boolean {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTarget is AbsListView) {
                val absListView = mTarget as AbsListView?
                val lastChild = absListView?.getChildAt(absListView.childCount - 1)
                return if (lastChild != null) {
                    absListView.lastVisiblePosition == absListView.count - 1 && lastChild.bottom > absListView.paddingBottom
                } else {
                    false
                }
            } else {
                return mTarget?.height?:0 - (mTarget?.scrollY?:0) > 0
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget, 1)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        ensureTarget()

        val action = MotionEventCompat.getActionMasked(ev)

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false
        }

        if (!isEnabled || mReturningToStart) {
            // Fail fast if we're not in a state where a swipe is possible
            return false
        }

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mInitialMotionY = ev.y
                mLastMotionY = mInitialMotionY
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0)
                mIsBeingDragged = false
                mCurrPercentage = 0f
                mStartPoint = mInitialMotionY

                //这里用up/down记录子控件能否下拉，如果当前子控件不能上下滑动，但当手指按下并移动子控件时，控件就会变得可滑动
                //后面的一些处理不能直接使用canChildScrollUp/canChildScrollDown
                //但仍存在问题：当数据不满一屏且设置可以上拉模式后，多次快速上拉会激发上拉加载
                up = canChildScrollUp()
                down = canChildScrollDown()
            }

            MotionEvent.ACTION_MOVE -> {
                if (mActivePointerId == INVALID_POINTER) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.")
                    return false
                }

                val pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId)
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.")
                    return false
                }

                val y = MotionEventCompat.getY(ev, pointerIndex)
                //                final float yDiff = y - mInitialMotionY;
                val yDiff = y - mStartPoint
                //若上个手势的方向和当前手势方向不一致，返回
                if (mLastDirection == Mode.PULL_FROM_START && yDiff < 0 || mLastDirection == Mode.PULL_FROM_END && yDiff > 0) {
                    return false
                }
                //下拉或上拉时，子控件本身能够滑动时，记录当前手指位置，当其滑动到尽头时，
                //mStartPoint作为下拉刷新或上拉加载的手势起点
                if (canChildScrollUp() && yDiff > 0 || canChildScrollDown() && yDiff < 0) {
                    mStartPoint = y
                }

                //下拉
                if (yDiff > mTouchSlop) {
                    //若当前子控件能向下滑动，或者上个手势为上拉，则返回
                    if (canChildScrollUp() || mLastDirection == Mode.PULL_FROM_END) {
                        mIsBeingDragged = false
                        return false
                    }
                    if (mMode == Mode.PULL_FROM_START || mMode == Mode.BOTH) {
                        mLastMotionY = y
                        mIsBeingDragged = true
                        mLastDirection = Mode.PULL_FROM_START
                    }
                } else if (-yDiff > mTouchSlop) {
                    //若当前子控件能向上滑动，或者上个手势为下拉，则返回
                    if (canChildScrollDown() || mLastDirection == Mode.PULL_FROM_START) {
                        mIsBeingDragged = false
                        return false
                    }
                    //若子控件不能上下滑动，说明数据不足一屏，若不满屏不加载，返回
                    if (!up && !down && !loadNoFull) {
                        mIsBeingDragged = false
                        return false
                    }
                    if (mMode == Mode.PULL_FROM_END || mMode == Mode.BOTH) {
                        mLastMotionY = y
                        mIsBeingDragged = true
                        mLastDirection = Mode.PULL_FROM_END
                    }
                }//上拉
            }

            MotionEventCompat.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mIsBeingDragged = false
                mCurrPercentage = 0f
                mActivePointerId = INVALID_POINTER
                mLastDirection = Mode.DISABLED
            }
        }

        return mIsBeingDragged
    }

    override fun requestDisallowInterceptTouchEvent(b: Boolean) {
        // Nope.
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        val action = MotionEventCompat.getActionMasked(ev)

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false
        }

        if (!isEnabled || mReturningToStart) {
            // Fail fast if we're not in a state where a swipe is possible
            return false
        }

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mInitialMotionY = ev.y
                mLastMotionY = mInitialMotionY
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0)
                mIsBeingDragged = false
                mCurrPercentage = 0f
                mStartPoint = mInitialMotionY

                up = canChildScrollUp()
                down = canChildScrollDown()
            }

            MotionEvent.ACTION_MOVE -> {
                val pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId)
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.")
                    return false
                }

                val y = MotionEventCompat.getY(ev, pointerIndex)
                //                final float yDiff = y - mInitialMotionY;
                val yDiff = y - mStartPoint

                if (mLastDirection == Mode.PULL_FROM_START && yDiff < 0 || mLastDirection == Mode.PULL_FROM_END && yDiff > 0) {
                    return true
                }

                if (!mIsBeingDragged && yDiff > 0 && mLastDirection == Mode.PULL_FROM_START || yDiff < 0 && mLastDirection == Mode.PULL_FROM_END) {
                    mIsBeingDragged = true
                }

                if (mIsBeingDragged) {
                    // User velocity passed min velocity; trigger a refresh
                    if (yDiff > mDistanceToTriggerSync) {
                        // User movement passed distance; trigger a refresh
                        if (mLastDirection == Mode.PULL_FROM_END) {
                            return true

                        }
                        if (mMode == Mode.PULL_FROM_START || mMode == Mode.BOTH) {
                            mLastDirection = Mode.PULL_FROM_START
                            startRefresh()
                        }
                    } else if (-yDiff > mDistanceToTriggerSync) {
                        if (!up && !down && !loadNoFull || mLastDirection == Mode.PULL_FROM_START) {
                            return true
                        }
                        if (mMode == Mode.PULL_FROM_END || mMode == Mode.BOTH) {
                            mLastDirection = Mode.PULL_FROM_END
                            startLoad()
                        }
                    } else {
                        if (!up && !down && yDiff < 0 && !loadNoFull) {
                            return true
                        }
                        // Just track the user's movement
                        //根据手指移动距离设置进度条显示的百分比
                        setTriggerPercentage(
                                mAccelerateInterpolator.getInterpolation(
                                        Math.abs(yDiff) / mDistanceToTriggerSync))
                        updateContentOffsetTop(yDiff.toInt())
                        if (mTarget?.top == paddingTop) {
                            // If the user puts the view back at the top, we
                            // don't need to. This shouldn't be considered
                            // cancelling the gesture as the user can restart from the top.
                            removeCallbacks(mCancel)
                            mLastDirection = Mode.DISABLED
                        } else {
                            mDirection = if (yDiff > 0) 1 else -1
                            updatePositionTimeout()
                        }
                    }
                    mLastMotionY = y
                }
            }

            MotionEventCompat.ACTION_POINTER_DOWN -> {
                val index = MotionEventCompat.getActionIndex(ev)
                mLastMotionY = MotionEventCompat.getY(ev, index)
                mActivePointerId = MotionEventCompat.getPointerId(ev, index)
            }

            MotionEventCompat.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mIsBeingDragged = false
                mCurrPercentage = 0f
                mActivePointerId = INVALID_POINTER
                mLastDirection = Mode.DISABLED
                return false
            }
        }

        return true
    }

    private fun startRefresh() {
        if (!isLoading && !isRefreshing) {
            removeCallbacks(mCancel)
            mReturnToStartPosition.run()
            isRefreshing = true
            onRefreshListener?.onRefresh()
        }
    }

    private fun startLoad() {
        if (!isLoading && !isRefreshing) {
            removeCallbacks(mCancel)
            mReturnToStartPosition.run()
            isLoading = true
            onLoadListener?.onLoad()
        }
    }

    //手指移动时更新子控件的位置
    private fun updateContentOffsetTop(targetTop: Int) {
        var targetTop = targetTop
        val currentTop = mTarget?.top?:0
        if (targetTop > mDistanceToTriggerSync) {
            targetTop = mDistanceToTriggerSync.toInt()
        }
        //注释掉，否则上拉的时候子控件会向下移动
        //        else if (targetTop < 0) {
        //            targetTop = 0;
        //        }
        setTargetOffsetTopAndBottom(targetTop - currentTop)
    }

    //根据偏移量对子控件进行移动
    private fun setTargetOffsetTopAndBottom(offset: Int) {
        mTarget?.offsetTopAndBottom(offset)
        mCurrentTargetOffsetTop = mTarget?.top?:0
    }

    private fun updatePositionTimeout() {
        removeCallbacks(mCancel)
        postDelayed(mCancel, RETURN_TO_ORIGINAL_POSITION_TIMEOUT)
    }

    private fun onSecondaryPointerUp(ev: MotionEvent) {
        val pointerIndex = MotionEventCompat.getActionIndex(ev)
        val pointerId = MotionEventCompat.getPointerId(ev, pointerIndex)
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            val newPointerIndex = if (pointerIndex == 0) 1 else 0
            mLastMotionY = MotionEventCompat.getY(ev, newPointerIndex)
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex)
        }
    }

    /**
     * Classes that wish to be notified when the swipe gesture correctly
     * triggers a refresh should implement this interface.
     */
    interface OnRefreshListener {
        fun onRefresh()
    }

    interface OnLoadListener {
        fun onLoad()
    }

    fun setMode(mode: Mode) {
        this.mMode = mode
    }

    fun setLoadNoFull(load: Boolean) {
        this.loadNoFull = load
    }

    enum class Mode// The modeInt values need to match those from attrs.xml
    (internal val intValue: Int) {
        /**
         * Disable all Pull-to-Refresh gesture and Refreshing handling
         */
        DISABLED(0x0),

        /**
         * Only allow the user to Pull from the start of the Refreshable View to
         * refresh. The start is either the Top or Left, depending on the
         * scrolling direction.
         */
        PULL_FROM_START(0x1),

        /**
         * Only allow the user to Pull from the end of the Refreshable View to
         * refresh. The start is either the Bottom or Right, depending on the
         * scrolling direction.
         */
        PULL_FROM_END(0x2),

        /**
         * Allow the user to both Pull from the start, from the end to refresh.
         */
        BOTH(0x3);

        internal fun permitsPullToRefresh(): Boolean {
            return this != DISABLED
        }

        internal fun permitsPullFromStart(): Boolean {
            return this == Mode.BOTH || this == Mode.PULL_FROM_START
        }

        internal fun permitsPullFromEnd(): Boolean {
            return this == Mode.BOTH || this == Mode.PULL_FROM_END
        }

    }

    /**
     * Simple AnimationListener to avoid having to implement unneeded methods in
     * AnimationListeners.
     */
    private open inner class BaseAnimationListener : AnimationListener {
        override fun onAnimationStart(animation: Animation) {}

        override fun onAnimationEnd(animation: Animation) {}

        override fun onAnimationRepeat(animation: Animation) {}
    }

    companion object {
        private val LOG_TAG = CusSwipeRefreshLayout::class.java.simpleName

        private val RETURN_TO_ORIGINAL_POSITION_TIMEOUT: Long = 300
        private val ACCELERATE_INTERPOLATION_FACTOR = 1.5f
        private val DECELERATE_INTERPOLATION_FACTOR = 2f
        private val PROGRESS_BAR_HEIGHT = 4f
        private val MAX_SWIPE_DISTANCE_FACTOR = .6f
        private val REFRESH_TRIGGER_DISTANCE = 120
        private val INVALID_POINTER = -1
        private val LAYOUT_ATTRS = intArrayOf(android.R.attr.enabled)
    }
}
/**
 * Simple constructor to use when creating a SwipeRefreshLayout from code.
 * @param context
 */
