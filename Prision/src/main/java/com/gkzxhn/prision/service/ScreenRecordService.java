package com.gkzxhn.prision.service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.gkzxhn.prision.common.Constants;
import com.gkzxhn.prision.common.GKApplication;
import com.gkzxhn.prision.utils.Utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Raleigh.Luo on 17/5/16.
 */

public class ScreenRecordService extends Service {

    private static final String TAG = "ScreenRecordingService";

    private int mScreenWidth;
    private int mScreenHeight;
    private int mScreenDensity;
    private int mResultCode;
    private Intent mResultData;
    /** 是否为标清视频 */
    private boolean isVideoSd;
    /** 是否开启音频录制 */
    private boolean isAudio;

    private MediaProjection mMediaProjection;
    private MediaRecorder mMediaRecorder;
    private VirtualDisplay mVirtualDisplay;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.i(TAG, "Service onCreate() is called");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            String rootPath = Utils.getTFPath();
            if (rootPath != null) {
                // TODO Auto-generated method stub
                Log.i(TAG, "Service onStartCommand() is called");
                mResultCode = intent.getIntExtra("code", -1);
                mResultData = intent.getParcelableExtra("data");

                mScreenWidth = intent.getIntExtra("width", 720);
                mScreenHeight = intent.getIntExtra("height", 1280);
                mScreenDensity = intent.getIntExtra("density", 1);
                isVideoSd = intent.getBooleanExtra("quality", true);
                isAudio = intent.getBooleanExtra("audio", true);

                mMediaProjection = createMediaProjection();
//                            /*String*/ rootPath=Constants.SD_VIDEO_PATH;//存放目录

                mMediaRecorder = createMediaRecorder(rootPath);
                mVirtualDisplay = createVirtualDisplay(); // 必须在mediaRecorder.prepare() 之后调用，否则报错"fail to get surface"

                mMediaRecorder.start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        return Service.START_NOT_STICKY;
    }
    public List<Camera.Size> getSupportedVideoSizes(Camera camera) {
        if (camera.getParameters().getSupportedVideoSizes() != null) {
            return camera.getParameters().getSupportedVideoSizes();
        } else {
            // Video sizes may be null, which indicates that all the supported
            // preview sizes are supported for video recording.
            return camera.getParameters().getSupportedPreviewSizes();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private MediaProjection createMediaProjection() {
        Log.i(TAG, "Create MediaProjection");
        return ((MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE)).getMediaProjection(mResultCode, mResultData);
    }

    private MediaRecorder createMediaRecorder(String rootPath) {
        MediaRecorder mediaRecorder=null;
        try {
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
//            Date curDate = new Date(System.currentTimeMillis());
//            String curTime = formatter.format(curDate).replace(" ", "");
            String videoQuality = "HD";
            if (isVideoSd) videoQuality = "SD";

            Log.i(TAG, "Create MediaRecorder");
            mediaRecorder = new MediaRecorder();
            mediaRecorder.reset();
            if (isAudio) mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);

            /*File rootfile=new File(Constants.SD_ROOT_PATH);
            if(!rootfile.exists()){
                rootfile.mkdirs();
            }

            File file=new File(Constants.SD_VIDEO_PATH);
            if(!file.exists()){
                file.mkdirs();
            }*/
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            SharedPreferences preferences= GKApplication.getInstance().
                    getSharedPreferences(Constants.USER_TABLE, Activity.MODE_PRIVATE);

            String fileName=String.format("%s_%s", Utils.getDateFromTimeInMillis(System.currentTimeMillis(),new SimpleDateFormat("yyyyMMddHHmmss")),
                    preferences.getString(Constants.RECORD_VIDEO_NAME,""));

/*
            rootPath += "/video/";
            File file=new File(rootPath);
            if(!file.exists()){
                file.mkdirs();
            }*/
            mediaRecorder.setOutputFile(rootPath + "/" + fileName + ".mp4");

//            Socket receiver = new Socket("10.10.10.102", 8089);
//            ParcelFileDescriptor pfd = ParcelFileDescriptor.fromSocket(receiver);
//            mediaRecorder.setOutputFile(pfd.getFileDescriptor());

            mediaRecorder.setVideoSize(mScreenWidth, mScreenHeight);  //after setVideoSource(), setOutFormat()
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);  //after setOutputFormat()
            if (isAudio)
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);  //after setOutputFormat()
            int bitRate;
            if (isVideoSd) {
                mediaRecorder.setVideoEncodingBitRate(mScreenWidth * mScreenHeight);
                mediaRecorder.setVideoFrameRate(30);
                bitRate = mScreenWidth * mScreenHeight / 1000;
            } else {
                mediaRecorder.setVideoEncodingBitRate(5 * mScreenWidth * mScreenHeight);
                mediaRecorder.setVideoFrameRate(60); //after setVideoSource(), setOutFormat()
                bitRate = 5 * mScreenWidth * mScreenHeight / 1000;
            }
            try {
                mediaRecorder.prepare();
            } catch (IllegalStateException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Log.i(TAG, "Audio: " + isAudio + ", SD video: " + isVideoSd + ", BitRate: " + bitRate + "kbps");
        }catch (Exception e){
            e.printStackTrace();
        }
        return mediaRecorder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private VirtualDisplay createVirtualDisplay() {
        Log.i(TAG, "Create VirtualDisplay");
        return mMediaProjection.createVirtualDisplay(TAG, mScreenWidth, mScreenHeight, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mMediaRecorder.getSurface(), null, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.i(TAG, "Service onDestroy");
        if(mVirtualDisplay != null) {
            mVirtualDisplay.release();
            mVirtualDisplay = null;
        }
        if(mMediaRecorder != null) {
            mMediaRecorder.setOnErrorListener(null);
            mMediaProjection.stop();
            mMediaRecorder.reset();
        }
        if(mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
}
