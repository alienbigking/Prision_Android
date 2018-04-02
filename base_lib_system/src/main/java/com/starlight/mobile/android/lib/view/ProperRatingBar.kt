package com.starlight.mobile.android.lib.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.starlight.mobile.android.lib.R

/**
 * Created by Raleigh.Luo on 16/5/29.
 */
class ProperRatingBar(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private var totalTicks: Int = 0
    private var lastSelectedTickIndex: Int = 0
    private var mIsClickable = false
    var symbolicTick: String? = null
        set(value){
            field = value
            afterInit()
        }
    private var customTextSize: Int = 0
    private var customTextStyle: Int = 0
    private var customTextNormalColor: Int = 0
    private var customTextSelectedColor: Int = 0
    private var tickNormalDrawable: Int = 0
    private var tickSelectedDrawable: Int = 0
    private var tickSpacing: Int = 0
    private var tickDrawableWidth = 0
    private var tickDrawableHeigt = 0
    private var isHalfstepSize = true//是否为0.5步长否则为1，默认为0.5

    private var useSymbolicTick = false
    var rating: Float = 0f
        set(value) {
            var rating = value
            if (rating > this.totalTicks) rating = totalTicks.toFloat()
            field = rating
            val doubleNum = (rating * 2).toInt()
            lastSelectedTickIndex = if (doubleNum % 2 > 0) doubleNum / 2 else (rating - 1).toInt()
            redrawChildren()
        }
    /**
     * Set the [RatingListener] to be called when user taps rating bar's ticks
     * @param listener photoFromClickListener to set
     *
     * @throws IllegalArgumentException if photoFromClickListener is **null**
     */
    var listener:RatingListener?=null
        set(value) {
            if (value == null) throw IllegalArgumentException("photoFromClickListener cannot be null!")
            field = value
            mIsClickable = true
        }


    private var mHalfBitmap: Bitmap? = null

    init {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ProperRatingBar)
        //
        totalTicks = a.getInt(R.styleable.ProperRatingBar_prb_totalTicks, DF_TOTAL_TICKS)
        rating = a.getFloat(R.styleable.ProperRatingBar_prb_defaultRating, DF_DEFAULT_TICKS.toFloat())
        //
        mIsClickable = a.getBoolean(R.styleable.ProperRatingBar_prb_clickable, DF_CLICKABLE)
        //
        symbolicTick = a.getString(R.styleable.ProperRatingBar_prb_symbolicTick)
        if (symbolicTick == null) symbolicTick = context.getString(DF_SYMBOLIC_TICK_RES)
        //
        customTextSize = a.getDimensionPixelSize(R.styleable.ProperRatingBar_android_textSize,
                context.resources.getDimensionPixelOffset(DF_SYMBOLIC_TEXT_SIZE_RES))
        customTextStyle = a.getInt(R.styleable.ProperRatingBar_android_textStyle, DF_SYMBOLIC_TEXT_STYLE)
        customTextNormalColor = a.getColor(R.styleable.ProperRatingBar_prb_symbolicTickNormalColor,
                DF_SYMBOLIC_TEXT_NORMAL_COLOR)
        customTextSelectedColor = a.getColor(R.styleable.ProperRatingBar_prb_symbolicTickSelectedColor,
                DF_SYMBOLIC_TEXT_SELECTED_COLOR)
        tickDrawableWidth = a.getDimensionPixelSize(R.styleable.ProperRatingBar_prb_tickDrawable_width, 0)
        tickDrawableHeigt = a.getDimensionPixelSize(R.styleable.ProperRatingBar_prb_tickDrawable_height, 0)
        isHalfstepSize = a.getBoolean(R.styleable.ProperRatingBar_prb_isHalf_stepSize, true)
        //
        tickNormalDrawable = a.getResourceId(R.styleable.ProperRatingBar_prb_tickNormalDrawable, 0)
        tickSelectedDrawable = a.getResourceId(R.styleable.ProperRatingBar_prb_tickSelectedDrawable, 0)
        tickSpacing = a.getDimensionPixelOffset(R.styleable.ProperRatingBar_prb_tickSpacing,
                context.resources.getDimensionPixelOffset(DF_TICK_SPACING_RES))
        //
        afterInit()
        //
        a.recycle()
    }

    private fun afterInit() {
        if (rating > totalTicks) rating = totalTicks.toFloat()
        val doubleNum = (rating * 2).toInt()
        lastSelectedTickIndex = (if (doubleNum % 2 > 0) doubleNum / 2 else rating.toInt() - 1)
        //
        if (tickNormalDrawable == 0 || tickSelectedDrawable == 0) {
            useSymbolicTick = true
        } else {
            initHalfImage()
        }
        //
        addChildren(this.context)
    }

    private fun addChildren(context: Context) {
        this.removeAllViews()
        for (i in 0..totalTicks - 1) {
            addChild(context, i)
        }
        redrawChildren()
    }

    private fun initHalfImage() {
        // 防止出现Immutable bitmap passed to Canvas constructor错误
        val normalBitmap = BitmapFactory.decodeResource(resources,
                tickNormalDrawable).copy(Bitmap.Config.ARGB_8888, true)
        val selectBitmap = (resources.getDrawable(
                tickSelectedDrawable) as BitmapDrawable).bitmap
        val newBitmap2 = Bitmap.createBitmap(selectBitmap, 0, 0, selectBitmap.width / 2, selectBitmap.height)
        mHalfBitmap = Bitmap.createBitmap(normalBitmap)

        val canvas = Canvas(mHalfBitmap!!)
        var paint = Paint()
        paint.color = Color.WHITE
        paint.alpha = 0
        canvas.drawRect(0f, 0f, (normalBitmap.width / 2).toFloat(), normalBitmap.height.toFloat(), paint)
        paint = Paint()
        canvas.drawBitmap(newBitmap2, 0f, 0f, paint)
        canvas.save(Canvas.ALL_SAVE_FLAG)
        // 存储新合成的图片
        canvas.restore()
    }

    private fun addChild(context: Context, position: Int) {
        if (useSymbolicTick) {
            addSymbolicChild(context, position)
        } else {
            addDrawableChild(context, position)
        }
    }

    private fun addSymbolicChild(context: Context, position: Int) {
        val tv = TextView(context)
        tv.text = symbolicTick
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, customTextSize.toFloat())
        if (customTextStyle != 0) {
            tv.setTypeface(Typeface.DEFAULT, customTextStyle)
        }
        if (mIsClickable) {
            tv.setTag(R.id.prb_child_tag_id, position)
            tv.setOnClickListener(mTickClickedListener)
        }
        this.addView(tv)
    }

    private fun addDrawableChild(context: Context, position: Int) {
        val iv = ImageView(context)
        if (tickDrawableWidth > 0) {
            val params = LinearLayout.LayoutParams(tickDrawableWidth, tickDrawableHeigt)
            iv.layoutParams = params
        }
        iv.setPadding(tickSpacing, tickSpacing, tickSpacing, tickSpacing)
        if (mIsClickable) {
            iv.setTag(R.id.prb_child_tag_id, position)
            iv.setOnClickListener(mTickClickedListener)
        }
        this.addView(iv)
    }

    private val mTickClickedListener = OnClickListener { v ->
        val temp = v.getTag(R.id.prb_child_tag_id) as Int
        lastSelectedTickIndex = if (temp == lastSelectedTickIndex) temp - 1 else temp
        rating = (lastSelectedTickIndex + 1).toFloat()
        redrawChildren()
        listener!!.onRatePicked(this@ProperRatingBar)
    }

    private fun redrawChildren() {
        for (i in 0..totalTicks - 1) {
            if (useSymbolicTick) {
                redrawChildSelection(this@ProperRatingBar.getChildAt(i) as TextView, i <= lastSelectedTickIndex)
            } else {
                val doubleNum = (rating * 2).toInt()
                redrawChildSelection(this@ProperRatingBar.getChildAt(i) as ImageView, i <= lastSelectedTickIndex, i == lastSelectedTickIndex && doubleNum % 2 > 0)
            }
        }
    }

    private fun redrawChildSelection(child: ImageView, isSelected: Boolean, hasHalf: Boolean) {
        if (isSelected) {
            if (isHalfstepSize && hasHalf) {//半星
                child.setImageBitmap(mHalfBitmap)
            } else
                child.setImageResource(tickSelectedDrawable)

        } else {
            child.setImageResource(tickNormalDrawable)
        }


    }

    private fun redrawChildSelection(child: TextView, isSelected: Boolean) {
        if (isSelected) {
            child.setTextColor(customTextSelectedColor)
        } else {
            child.setTextColor(customTextNormalColor)
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Saving and restoring state
    ///////////////////////////////////////////////////////////////////////////

    public override fun onSaveInstanceState(): Parcelable {
        val savedState = SavedState(super.onSaveInstanceState())
        savedState.rating = rating

        return savedState
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }

        super.onRestoreInstanceState(state.superState)
        rating=state.rating
    }

    internal class SavedState : View.BaseSavedState {
        var rating: Float = 0f
        constructor(superState: Parcelable) : super(superState)

        private constructor(`in`: Parcel) : super(`in`) {
            this.rating = `in`.readInt().toFloat()
        }
        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeFloat(this.rating)
        }
        companion object {

            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(`in`: Parcel): SavedState {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState> {
                    return arrayOfNulls<SavedState>(size) as Array<SavedState>
                }
            }
        }
    }

    interface RatingListener {
        fun onRatePicked(ratingBar: ProperRatingBar)
    }
    companion object {
        private val DF_TOTAL_TICKS = 5
        private val DF_DEFAULT_TICKS = 3
        private val DF_CLICKABLE = false
        private val DF_SYMBOLIC_TICK_RES = R.string.prb_default_symbolic_string
        private val DF_SYMBOLIC_TEXT_SIZE_RES = R.dimen.prb_symbolic_tick_default_text_size
        private val DF_SYMBOLIC_TEXT_STYLE = Typeface.NORMAL
        private val DF_SYMBOLIC_TEXT_NORMAL_COLOR = Color.BLACK
        private val DF_SYMBOLIC_TEXT_SELECTED_COLOR = Color.GRAY
        private val DF_TICK_SPACING_RES = R.dimen.prb_drawable_tick_default_spacing
    }
}
