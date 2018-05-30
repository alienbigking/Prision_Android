package com.gkzxhn.prison.customview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

import com.gkzxhn.prison.R

/**
 * Created by Raleigh.Luo on 18/5/30.
 */

class CircleLayout
/**
 * @param context
 * @param attrs
 * @param defStyle
 */
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : ViewGroup(context, attrs, defStyle) {
    // Event listeners
    private var mOnItemClickListener: OnItemClickListener? = null
    private var mOnItemSelectedListener: OnItemSelectedListener? = null
    private var mOnCenterClickListener: OnCenterClickListener? = null


    private var mTappedViewsPostition = -1
    private var mTappedView: View? = null
    var selected = 0

    // Child sizes
    private var mMaxChildWidth = 0
    private var mMaxChildHeight = 0
    private var childWidth = 0
    private var childHeight = 0

    // Sizes of the ViewGroup
    private var circleWidth: Int = 0
    private var radius = 0

    // Touch detection
    private var mGestureDetector: GestureDetector? = null
    // needed for detecting the inversed rotations
    private lateinit var quadrantTouched: BooleanArray

    // Settings of the ViewGroup
    private var allowRotating = true
    private var angle = 90f
    private var firstChildPos = 90f
    private var rotateToCenter = true
    private var isRotating = true
    private val mCirclerPaint = Paint()
    private var isFirstInit=true
    /**
     * Returns the currently selected menu
     * @return the view which is currently the closest to the start position
     */
    val selectedItem: View?
        get() = if (selected >= 0) getChildAt(selected) else null

    private var startAngle: Double = 0.toDouble()
    /**
     * Initializes the ViewGroup and modifies it's default behavior by the passed attributes
     * @param attrs    the attributes used to modify default settings
     */
    init {
        mCirclerPaint.setColor(resources.getColor(R.color.common_blue));// 设置红色
        mCirclerPaint.setAntiAlias(true);//取消锯齿
        mCirclerPaint.setStyle(Paint.Style.FILL);
        mGestureDetector = GestureDetector(context,
                MyGestureListener())
        quadrantTouched = booleanArrayOf(false, false, false, false, false)

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs,
                    R.styleable.Circle)

            // The angle where the first menu item will be drawn
            angle = a.getInt(R.styleable.Circle_firstChildPosition, 0).toFloat()
            firstChildPos = angle

            rotateToCenter = a.getBoolean(R.styleable.Circle_rotateToCenter,
                    true)
            isRotating = a.getBoolean(R.styleable.Circle_isRotating, true)

            // If the menu is not rotating then it does not have to be centered
            // since it cannot be even moved
            if (!isRotating) {
                rotateToCenter = false
            }

            a.recycle()


            // Needed for the ViewGroup to be drawn
            setWillNotDraw(false)
        }
    }


    override fun onDraw(canvas: Canvas) {
//        // the sizes of the ViewGroup
        circleWidth = Math.min(width*2 , height)


        val cx = circleWidth/2
        val cy = circleWidth/2
        canvas.drawCircle(cx.toFloat(),cy.toFloat(),circleWidth.toFloat()/2,mCirclerPaint)
        Log.e("raleigh_test","1111")
        val layoutParams = layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.leftMargin = -circleWidth / 2
        setLayoutParams(layoutParams)
//
//
//        if(isFirstInit){
//            rotateViewToCenter(getChildAt(selected-1) as CircleTextView,true)
//            isFirstInit=false
//        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mMaxChildWidth = 0
        mMaxChildHeight = 0

        // Measure once to find the maximum child size.
        var childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.AT_MOST)
        var childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.AT_MOST)

        val count = childCount
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE) {
                continue
            }

            child.measure(childWidthMeasureSpec, childHeightMeasureSpec)

            mMaxChildWidth = Math.max(mMaxChildWidth, child.measuredWidth)
            mMaxChildHeight = Math.max(mMaxChildHeight,
                    child.measuredHeight)
        }

        // Measure again for each child to be exactly the same size.
        childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(mMaxChildWidth,
                View.MeasureSpec.EXACTLY)
        childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(mMaxChildHeight,
                View.MeasureSpec.EXACTLY)

        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE) {
                continue
            }

            child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
        }
        val width=View.resolveSize(mMaxChildWidth, widthMeasureSpec)
        setMeasuredDimension(width, width)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val layoutWidth = r - l
        val layoutHeight = b - t

        // Laying out the child views
        val childCount = childCount
        var left: Int
        var top: Int



        childWidth = (getChildAt(0)as CircleTextView).measuredWidth
        childHeight = (getChildAt(0)as CircleTextView).measuredHeight

        radius = if (layoutWidth <= layoutHeight)
            layoutWidth / 2-childWidth
        else
            layoutHeight / 2-childWidth

        val angleDelay = (360 / getChildCount()).toFloat()

        for (i in 0 until childCount) {

            val child = getChildAt(i) as CircleTextView
            if (child.visibility == View.GONE) {
                continue
            }

            if (angle > 360) {
                angle -= 360f
            } else {
                if (angle < 0) {
                    angle += 360f
                }
            }

            child.setAngle(angle)
            child.setPosition(i)

            left = Math
                    .round((layoutWidth / 2 - childWidth / 2 + radius * Math.cos(Math.toRadians(angle.toDouble()))).toFloat())
            top = Math
                    .round((layoutHeight / 2 - childHeight / 2 + radius * Math.sin(Math.toRadians(angle.toDouble()))).toFloat())

            child.layout(left, top, left + childWidth, top + childHeight)
            angle += angleDelay
        }
    }

    /**
     * Rotate the buttons.
     *
     * @param degrees The degrees, the menu items should get rotated.
     */
    private fun rotateButtons(degrees: Float) {
        var left: Int
        var top: Int
        val childCount = childCount
        val angleDelay = (360 / childCount).toFloat()
        angle += degrees

        if (angle > 360) {
            angle -= 360f
        } else {
            if (angle < 0) {
                angle += 360f
            }
        }

        for (i in 0 until childCount) {
            if (angle > 360) {
                angle -= 360f
            } else {
                if (angle < 0) {
                    angle += 360f
                }
            }

            val child = getChildAt(i) as CircleTextView
            if (child.visibility == View.GONE) {
                continue
            }
            left = Math
                    .round((circleWidth / 2 - childWidth / 2 + radius * Math.cos(Math.toRadians(angle.toDouble()))).toFloat())
            top = Math
                    .round((circleWidth / 2 - childHeight / 2 + radius * Math.sin(Math.toRadians(angle.toDouble()))).toFloat())

            child.setAngle(angle)

            if (Math.abs(angle - firstChildPos) < angleDelay / 2 && selected != child.getPosition()) {
                selected = child.getPosition()

                if (mOnItemSelectedListener != null && rotateToCenter) {
                    mOnItemSelectedListener?.onItemSelected(child, selected,
                            child.id.toLong(), child.getName())
                }
            }

            child.layout(left, top, left + childWidth, top + childHeight)
            angle += angleDelay
        }
    }

    /**
     * @return The angle of the unit circle with the image view's center
     */
    private fun getAngle(xTouch: Double, yTouch: Double): Double {
        val x = xTouch - circleWidth / 2.0
        val y = circleWidth.toDouble() - yTouch - circleWidth / 2.0

        when (getQuadrant(x, y)) {
            1 -> return Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI

            2, 3 -> return 180 - Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI

            4 -> return 360 + Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI

            else ->
                // ignore, does not happen
                return 0.0
        }
    }

    /**
     * @return The selected quadrant.
     */
    private fun getQuadrant(x: Double, y: Double): Int {
        return if (x >= 0) {
            if (y >= 0) 1 else 4
        } else {
            if (y >= 0) 2 else 3
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isEnabled) {
            if (isRotating) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {

                        // reset the touched quadrants
                        for (i in quadrantTouched.indices) {
                            quadrantTouched[i] = false
                        }

                        allowRotating = false

                        startAngle = getAngle(event.x.toDouble(), event.y.toDouble())
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val currentAngle = getAngle(event.x.toDouble(), event.y.toDouble())
                        rotateButtons((startAngle - currentAngle).toFloat())
                        startAngle = currentAngle
                    }
                    MotionEvent.ACTION_UP -> {
                        allowRotating = true
                        rotateViewToCenter(getChildAt(selected) as CircleTextView,
                                false)
                    }
                }
            }

            // set the touched quadrant to true
            quadrantTouched[getQuadrant((event.x - circleWidth / 2).toDouble(),
                    (circleWidth.toFloat() - event.y - (circleWidth / 2).toFloat()).toDouble())] = true
            mGestureDetector?.onTouchEvent(event)
            return true
        }
        return false
    }

    private inner class MyGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float,
                             velocityY: Float): Boolean {
            if (!isRotating) {
                return false
            }
            // get the quadrant of the start and the end of the fling
            val q1 = getQuadrant((e1.x - circleWidth / 2).toDouble(), (circleWidth.toFloat()
                    - e1.y - (circleWidth / 2).toFloat()).toDouble())
            val q2 = getQuadrant((e2.x - circleWidth / 2).toDouble(), (circleWidth.toFloat()
                    - e2.y - (circleWidth / 2).toFloat()).toDouble())

            // the inversed rotations
            if (q1 == 2 && q2 == 2 && Math.abs(velocityX) < Math
                    .abs(velocityY)
                    || q1 == 3 && q2 == 3
                    || q1 == 1 && q2 == 3
                    || q1 == 4 && q2 == 4 && Math.abs(velocityX) > Math
                    .abs(velocityY)
                    || q1 == 2 && q2 == 3 || q1 == 3 && q2 == 2
                    || q1 == 3 && q2 == 4 || q1 == 4 && q2 == 3
                    || q1 == 2 && q2 == 4 && quadrantTouched[3]
                    || q1 == 4 && q2 == 2 && quadrantTouched[3]) {

                this@CircleLayout.post(FlingRunnable(-1 * (velocityX + velocityY)))
            } else {
                // the normal rotation
                this@CircleLayout
                        .post(FlingRunnable(velocityX + velocityY))
            }

            return true

        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            mTappedViewsPostition = pointToPosition(e.x, e.y)
            if (mTappedViewsPostition >= 0) {
                mTappedView = getChildAt(mTappedViewsPostition)
                mTappedView?.isPressed = true
            } else {
                val centerX = (circleWidth / 2).toFloat()
                val centerY = (circleWidth / 2).toFloat()

                if (e.x < centerX + childWidth / 2
                        && e.x > centerX - childWidth / 2
                        && e.y < centerY + childHeight / 2
                        && e.y > centerY - childHeight / 2) {
                    if (mOnCenterClickListener != null) {
                        mOnCenterClickListener?.onCenterClick()
                        return true
                    }
                }
            }

            mTappedView?.let{
                val view = mTappedView as CircleTextView
                if (selected != mTappedViewsPostition) {
                    rotateViewToCenter(view, false)
                    Log.e("raleigh_test","selected == mTappedViewsPostition")
                    if (!rotateToCenter) {
                        mOnItemSelectedListener?.onItemSelected(it,
                                mTappedViewsPostition, it.id.toLong(), view.getName())
                    }
                    mOnItemClickListener?.onItemClick(it,
                            mTappedViewsPostition, it.id.toLong(), view.getName())

                } else {
                    Log.e("raleigh_test","selected == mTappedViewsPostition")
                    rotateViewToCenter(view, false)
                    mOnItemClickListener?.onItemClick(it,
                            mTappedViewsPostition, it.id.toLong(), view.getName())
                }
                return true
            }
            return super.onSingleTapUp(e)
        }
    }

    /**
     * Rotates the given view to the center of the menu.
     * @param view            the view to be rotated to the center
     * @param fromRunnable    if the method is called from the runnable which animates the rotation
     * then it should be true, otherwise false
     */
    private fun rotateViewToCenter(view: CircleTextView, fromRunnable: Boolean) {
        if (rotateToCenter) {
            var velocityTemp = 1f
            var destAngle = (firstChildPos - view.getAngle()).toFloat()
            var startAngle = 0f
            var reverser = 1

            if (destAngle < 0) {
                destAngle += 360f
            }

            if (destAngle > 180) {
                reverser = -1
                destAngle = 360 - destAngle
            }

            while (startAngle < destAngle) {
                startAngle += velocityTemp / 75
                velocityTemp *= 1.0666f
            }

            this@CircleLayout.post(FlingRunnable(reverser * velocityTemp,
                    !fromRunnable))
        }
    }

    /**
     * A [Runnable] for animating the menu rotation.
     */
    private inner class FlingRunnable @JvmOverloads constructor(private var velocity: Float, isFirst: Boolean = true) : Runnable {
        internal var angleDelay: Float = 0.toFloat()
        internal var isFirstForwarding = true

        init {
            this.angleDelay = (360 / childCount).toFloat()
            this.isFirstForwarding = isFirst
        }

        override fun run() {
            if (Math.abs(velocity) > 5 && allowRotating) {
                if (rotateToCenter) {
                    if (!(Math.abs(velocity) < 200 && Math.abs(angle - firstChildPos) % angleDelay < 2)) {
                        rotateButtons(velocity / 75)
                        velocity /= 1.0666f

                        this@CircleLayout.post(this)
                    }
                } else {
                    rotateButtons(velocity / 75)
                    velocity /= 1.0666f

                    this@CircleLayout.post(this)
                }
            } else {
                if (isFirstForwarding) {
                    isFirstForwarding = false
                    this@CircleLayout.rotateViewToCenter(
                            getChildAt(selected) as CircleTextView, true)
                }
            }
        }
    }

    private fun pointToPosition(x: Float, y: Float): Int {

        for (i in 0 until childCount) {

            val item = getChildAt(i) as View
            if (item.left < x && (item.right > x) and (item.top < y)
                    && item.bottom > y) {
                return i
            }

        }
        return -1
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.mOnItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int, id: Long, name: String?)
    }

    fun setOnItemSelectedListener(
            onItemSelectedListener: OnItemSelectedListener) {
        this.mOnItemSelectedListener = onItemSelectedListener
    }

    interface OnItemSelectedListener {
        fun onItemSelected(view: View, position: Int, id: Long, name: String?)
    }

    interface OnCenterClickListener {
        fun onCenterClick()
    }

    fun setOnCenterClickListener(
            onCenterClickListener: OnCenterClickListener) {
        this.mOnCenterClickListener = onCenterClickListener
    }
}
/**
 * @param context
 */
/**
 * @param context
 * @param attrs
 */
