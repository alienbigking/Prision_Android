package com.starlight.mobile.android.lib.view


import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

import com.starlight.mobile.android.lib.R
//控件设置别名
import kotlinx.android.synthetic.main.common_head_layout.view.common_head_layout_tv_title as tvTitle
import kotlinx.android.synthetic.main.common_head_layout.view.common_head_layout_iv_left as ivLeft
import kotlinx.android.synthetic.main.common_head_layout.view.common_head_layout_iv_right as ivRight
import kotlinx.android.synthetic.main.common_head_layout.view.common_head_layout_tv_left as tvLeft
import kotlinx.android.synthetic.main.common_head_layout.view.common_head_layout_tv_title as tvTitle
import kotlinx.android.synthetic.main.common_head_layout.view.common_head_layout_tv_right as tvRight

/**
 * @author raleighluo
 * 自定义属性
 * <attr name="chHead_title" format="reference"></attr>
 * <attr name="chHead_title_padding_left" format="dimension"></attr>
 * <attr name="chHead_leftText" format="reference"></attr>
 *
 * <attr name="chHead_leftTextImg" format="reference"></attr>
 * <attr name="chHead_rightText" format="reference"></attr>
 *
 * <attr name="chHead_rightTextImg" format="reference"></attr>
 * <attr name="chHead_leftImg" format="reference"></attr>
 * <attr name="chHead_rightImg" format="reference"></attr>
 *
 * <attr name="chHead_imgClickEffect" format="reference"></attr>
 * <attr name="chHead_textClickEffect" format="reference"></attr>
 *
 * <attr name="chHead_titleColor" format="reference"></attr>
 *
 * <attr name="chHead_textColor" format="reference"></attr>
 * All view's onClick event android:onClick="onClickListener" ，否则需要重新设置监听器，方法setBtnClickListener
 */
class CusHeadView : RelativeLayout {
    private var textDrawableSize: Int = 0

    constructor(context: Context) : super(context, null)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        try {
            val inflater = context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.common_head_layout, this)
            textDrawableSize = resources.getDimensionPixelSize(R.dimen.chHead_text_drawable_size)

            //获取自定义属性
            val a = context.obtainStyledAttributes(attrs, R.styleable.CusHeadView_Attrs)


            val imgClickEffect = a.getResourceId(R.styleable.CusHeadView_Attrs_chHead_imgClickEffect, R.drawable.common_head_text_btn_selector)
            if (imgClickEffect != R.drawable.common_head_text_btn_selector) {//5.0以前取消左右两边的点击效果
                ivLeft.setBackgroundResource(imgClickEffect)
                ivRight.setBackgroundResource(imgClickEffect)
            }
            val textClickEffect = a.getResourceId(R.styleable.CusHeadView_Attrs_chHead_textClickEffect, R.drawable.common_head_text_btn_selector)
            if (textClickEffect != R.drawable.common_head_text_btn_selector) {//5.0以前取消左右两边的点击效果
                tvLeft.setBackgroundResource(imgClickEffect)
                tvRight.setBackgroundResource(imgClickEffect)
            }
            //文字颜色
            val titleColor = a.getResourceId(R.styleable.CusHeadView_Attrs_chHead_titleColor, android.R.color.white)
            val textColor = a.getResourceId(R.styleable.CusHeadView_Attrs_chHead_textColor, android.R.color.white)

            val titleSize = a.getDimensionPixelSize(R.styleable.CusHeadView_Attrs_chHead_title_size, 0)
            if (titleSize >= 0) tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize.toFloat())
            val titleLeftPadding = a.getDimensionPixelSize(R.styleable.CusHeadView_Attrs_chHead_title_padding_left, 0)
            if (titleLeftPadding >= 0) tvTitle.setPadding(titleLeftPadding, 0, 0, 0)
            //获取title属性值,默认为：空字符串
            tvTitle .setText(a.getResourceId(R.styleable.CusHeadView_Attrs_chHead_title, R.string.empty))
            tvTitle .setTextColor(resources.getColor(titleColor))
            //获取图标属性值,默认为：无色
            val leftImgRsc = a.getResourceId(R.styleable.CusHeadView_Attrs_chHead_leftImg, android.R.color.transparent)
            if (leftImgRsc != android.R.color.transparent) {
                val leftPadding = a.getDimensionPixelSize(R.styleable.CusHeadView_Attrs_chHead_leftImgPadding, -1)
                ivLeft .setImageResource(leftImgRsc)
                if (leftPadding >= 0) ivLeft .setPadding(leftPadding, leftPadding, leftPadding, leftPadding)
                ivLeft .visibility = View.VISIBLE
            }

            val rightImgRsc = a.getResourceId(R.styleable.CusHeadView_Attrs_chHead_rightImg, android.R.color.transparent)
            if (rightImgRsc != android.R.color.transparent) {
                val rightPadding = a.getDimensionPixelSize(R.styleable.CusHeadView_Attrs_chHead_rightImgPadding, -1)
                if (rightPadding >= 0) ivRight .setPadding(rightPadding, rightPadding, rightPadding, rightPadding)
                ivRight .setImageResource(rightImgRsc)
                ivRight .visibility = View.VISIBLE
            }

            val textSize = a.getDimensionPixelSize(R.styleable.CusHeadView_Attrs_chHead_text_size, 0)
            if (textSize >= 0) {
                tvLeft .setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
                tvRight .setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
            }

            val leftTextRsc = a.getResourceId(R.styleable.CusHeadView_Attrs_chHead_leftText, R.string.empty)
            if (leftTextRsc != R.string.empty) {
                tvLeft .setText(leftTextRsc)
                val csl = resources.getColorStateList(textColor)
                if (csl != null)
                    tvLeft.setTextColor(csl)
                else
                    tvLeft .setTextColor(resources.getColor(textColor))
                tvLeft .visibility = View.VISIBLE

                val leftTextImgRcs = a.getResourceId(R.styleable.CusHeadView_Attrs_chHead_leftTextImg, android.R.color.transparent)
                if (leftTextImgRcs != android.R.color.transparent) {
                    //左文字的左图标
                    val drawable = resources.getDrawable(leftTextImgRcs)
                    /// 这一步必须要做,否则不会显示.图片的大小
                    drawable.setBounds(0, 0, textDrawableSize, textDrawableSize)
                    tvLeft .setCompoundDrawables(drawable, null, null, null)
                }

            }

            val rightTextRsc = a.getResourceId(R.styleable.CusHeadView_Attrs_chHead_rightText, R.string.empty)
            if (rightTextRsc != R.string.empty) {
                tvRight .setText(rightTextRsc)
                val csl = resources.getColorStateList(textColor)
                if (csl != null)
                    tvRight .setTextColor(csl)
                else
                    tvRight .setTextColor(resources.getColor(textColor))
                tvRight .visibility = View.VISIBLE
                //右文字的右图标
                val rightTextImgRcs = a.getResourceId(R.styleable.CusHeadView_Attrs_chHead_rightTextImg, android.R.color.transparent)
                if (rightTextImgRcs != android.R.color.transparent) {
                    val drawable = resources.getDrawable(rightTextImgRcs)
                    /// 这一步必须要做,否则不会显示.图片的大小
                    drawable.setBounds(0, 0, textDrawableSize, textDrawableSize)
                    tvRight .setCompoundDrawables(null, null, drawable, null)
                }
            }
            a.recycle()
        } catch (e: Exception) {
        }

    }

    fun setRightBtnImage(resId: Int) {
        if (resId > 0) {
            this.ivRight .visibility = View.VISIBLE
            val drawable = resources.getDrawable(resId)
            //drawable.setBounds(0, 0, textDrawableSize, textDrawableSize);
            this.ivRight .setImageDrawable(drawable)
        } else {
            this.ivRight .visibility = View.GONE
        }
    }

    fun setRightBtnImageVisible(visibility: Boolean) {
        if (visibility) {
            this.ivRight .visibility = View.VISIBLE
        } else {
            this.ivRight .visibility = View.GONE
        }
    }


    /**设置左文字的左图标
     * @param resId
     */
    fun setLeftTextLeftDrawable(resId: Int) {
        if (resId > 0) {
            val drawable = resources.getDrawable(resId)
            /// 这一步必须要做,否则不会显示.图片的大小
            drawable.setBounds(0, 0, textDrawableSize, textDrawableSize)
            tvLeft .setCompoundDrawables(drawable, null, null, null)
        } else {
            tvLeft .setCompoundDrawables(null, null, null, null)
        }
    }

    /**设置右文字的右图标
     * @param resId
     */
    fun setRightTextRightDrawable(resId: Int) {
        if (resId > 0) {
            val drawable = resources.getDrawable(resId)
            /// 这一步必须要做,否则不会显示.图片的大小
            drawable.setBounds(0, 0, textDrawableSize, textDrawableSize)
            tvRight .setCompoundDrawables(null, null, drawable, null)
        } else {
            tvRight .setCompoundDrawables(null, null, null, null)
        }
    }

    fun setBtnClickListener(onClickListener: View.OnClickListener?) {
        onClickListener?.let{
            ivLeft .setOnClickListener(onClickListener)
            ivRight .setOnClickListener(onClickListener)
            tvLeft .setOnClickListener(onClickListener)
            tvRight .setOnClickListener(onClickListener)
        }
    }

    /**
     * 切换按钮
     *
     * @param btnLeftVisib
     * 值如View.gone
     * @param btnRightVisib
     * @param tvLeftVisib
     * @param tvRightVisib
     */
    private fun switchBtn(btnLeftVisib: Int, btnRightVisib: Int,
                          tvLeftVisib: Int, tvRightVisib: Int) {
        ivLeft .visibility = btnLeftVisib
        ivRight .visibility = btnRightVisib
        tvLeft .visibility = tvLeftVisib
        tvRight.visibility = tvRightVisib
    }

    fun getIvLeft(): ImageView {
        return ivLeft
    }

    fun getIvRight(): ImageView {
        return ivRight
    }

    fun getTvTitle(): TextView {
        return tvTitle
    }

    fun getTvLeft(): TextView {
        return tvLeft
    }

    fun getTvRight(): TextView {
        return tvRight
    }


}
