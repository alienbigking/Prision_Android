package com.gkzxhn.prison.customview

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.media.*
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.gkzxhn.prison.R
import android.media.AudioManager




class AlarmClockPopWindow(val context: Context) : PopupWindow(context) {
    private val root: View
    private val ivClock: ImageView
    private var mMediaPlayer: MediaPlayer? = null
    //倒计时10分钟
    private var mTimer: CountDownTimer = object : CountDownTimer(10 * 60 * 1000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
        }

        override fun onFinish() {
            dismiss()
        }
    }

    init {
        root = LayoutInflater.from(context).inflate(R.layout.alarm_clock_layout, null);
        setContentView(root)

        val m = context
                .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val d = m.defaultDisplay
        width = d.width / 2
        height = LinearLayout.LayoutParams.WRAP_CONTENT
        setBackgroundDrawable(context.resources.getDrawable(R.drawable.alarm_clock_shape))
        //设置点击外部不消失
        isFocusable = false
        isOutsideTouchable = false
        ivClock = root.findViewById(R.id.alarm_clock_layout_iv_alarm) as ImageView
        //点击关闭按钮消失窗口
        var tvClose = root.findViewById(R.id.alarm_clock_layout_tv_close)
        tvClose.setOnClickListener {
            dismiss()
        }
        //铃声
        mMediaPlayer = MediaPlayer()

//        var  file = context.getResources().openRawResourceFd(R.raw.alarm);
//        try {
//            mMediaPlayer?.setDataSource(file.getFileDescriptor(), file.getStartOffset(),
//                    file.getLength());
//            mMediaPlayer?.prepare();
//            file.close();
//        } catch (e:Exception) {
//            e.printStackTrace();
//        }
    }

    override fun showAtLocation(parent: View?, gravity: Int, x: Int, y: Int) {
        super.showAtLocation(parent, gravity, x, y)
        //显示闹铃动画
        if (ivClock != null) {
            mTimer.cancel()
            mTimer.start()
            val drawable = ivClock.drawable as AnimationDrawable
            drawable.start()
            try{
                mMediaPlayer=MediaPlayer.create(context,R.raw.alarm)
                mMediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer?.prepare();
            }catch (e:Exception){
                mMediaPlayer=null
                e.printStackTrace()
            }

//            mMediaPlayer?.reset()
            mMediaPlayer?.setVolume(1f,1f)
            mMediaPlayer?.start();
            mMediaPlayer?.setLooping(true); //循环播放
        }

    }


    override fun dismiss() {
        super.dismiss()
        mMediaPlayer?.stop()
        mMediaPlayer?.release()
        mMediaPlayer=null
        //关闭闹铃动画
        if(ivClock!=null) {
            val drawable = ivClock.drawable as AnimationDrawable
            drawable.stop()
        }
    }
}