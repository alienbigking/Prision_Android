package com.gkzxhn.prison.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.gkzxhn.prison.R
import android.util.Log


/**
 * Created by Raleigh.Luo on 18/5/28.
 */
class MonthCirclerView:View {
    private val mCirclerPaint = Paint()
    private val mTextPaint = Paint()
    private lateinit var mMonthText:Array<String>

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    fun init(context: Context){
        mMonthText=resources.getStringArray(R.array.month_text)
        mCirclerPaint.setColor(resources.getColor(R.color.common_blue));// 设置红色
        mCirclerPaint.setAntiAlias(true);//取消锯齿
        mCirclerPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(resources.getColor(android.R.color.white));//
        mTextPaint.setStyle(Paint.Style.FILL)
        mTextPaint.textSize=18f

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            val dis=20
            val radius=Math.min(2*width.toFloat(),height/Math.sqrt(3.toDouble()).toFloat())
            val cx=-radius/2-dis
            val cy=(height-y)/2
            it.drawCircle(cx,cy,radius,mCirclerPaint)

            val textRadius=9*radius/10
            val number = floatArrayOf(0f,0.5f,(Math.sqrt(3.toDouble())/2).toFloat(),1f)
            var index=0
            var numberIndex=0
            try {
                for (text in mMonthText) {
                    var x = 0f
                    var y = 0f
                    if (index < 7) {
                        x = cx + number[numberIndex] * textRadius
                    } else {
                        x = cx - number[numberIndex] * textRadius

                    }
                    if(index<4||index>8){
                        y = cy - number[number.size - numberIndex - 1] * textRadius
                    }else{
                        y = cy + number[number.size - numberIndex - 1] * textRadius
                    }


                    canvas.drawText(mMonthText[index], x, y, mTextPaint)
                    val t = index % 6
//                    Log.e("raleigh_test","x="+x+",y="+y+",radius="+textRadius+",index="+index)
                    Log.e("raleigh_test","index="+index+",  x="+x+",y="+y+"numberIndex="+numberIndex+",yidex="+ (number.size - numberIndex - 1))
                    if (t >= 0 && t < 3) {
                        numberIndex++
                    } else {
                        numberIndex--
                    }
                    index++
                  }
            }catch (e:Exception){
                e.printStackTrace()
                Log.e("raleigh_test","Exception="+e.toString())
            }
        }


//        val wRadius=2*width.toFloat()
//        val hRadius=(Math.sqrt(3.toDouble())*height/4).toFloat()
//        val radius= min(wRadius,hRadius)
////        val radius=width.toFloat() -x
//        val mX=x-(radius+radius/2)
//        val mY=(height-y)/2-radius
//        val oval = RectF(mX, mY,
//                mX+2*radius , radius*2+mY )
//        Log.e("raleigh_test","mX="+mX+",mY="+mY+",radius="+radius)
//        //useCenter =false 不经过圆心
//        canvas?.drawArc(oval, -60f, 120f, false, mCirclerPaint)
//


//        val wRadius=width
//        val hRadius=height/2
//        val radius= min(wRadius,hRadius).toFloat()
////        val radius=width.toFloat() -x
//        val mX=-radius
//        val mY=(height-y)/2-radius
//        val oval = RectF(mX, mY,
//                mX+2*radius , radius*2+mY )
//        Log.e("raleigh_test","mX="+mX+",mY="+mY+",radius="+radius)
//        //useCenter =false 不经过圆心
//        canvas?.drawArc(oval, -90f, 180f, false, mCirclerPaint)
    }
}