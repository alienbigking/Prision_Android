package com.gkzxhn.prison.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gkzxhn.prison.async.SingleRequestQueue;
import com.gkzxhn.prison.async.VolleyUtils;
import com.gkzxhn.prison.common.Constants;
import com.gkzxhn.prison.common.GKApplication;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 *
 * @author huangzhengneng
 * created on 2016/5/9
 *
 */
public class CrashHandler implements UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";
    //系统默认的UncaughtException处理类
    private UncaughtExceptionHandler mDefaultHandler;
    //CrashHandler实例
    private static CrashHandler INSTANCE = new CrashHandler();
    //程序的Context对象
    private Context mContext;
    //用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();

    //用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());
    private boolean flag=false;
    private VolleyUtils  volleyUtils = new VolleyUtils();
    /** 保证只有一个CrashHandler实例 */
    private CrashHandler() {
    }

    /** 获取CrashHandler实例 ,单例模式 */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : " + e.getMessage());
            }
            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
//        saveCrashInfo2File(ex);
        if(!flag){
            StringBuffer b = new StringBuffer();
            b.append(ex.toString());
            StackTraceElement[] stackTraceElements=ex.getStackTrace();
            int lenth=stackTraceElements.length>2?2:stackTraceElements.length;
            if(stackTraceElements!=null){
                for (int i=0;i<lenth;i++) {
                    StackTraceElement s=stackTraceElements[i];
                    if(s!=null&&!s.toString().isEmpty())b.append("\n"+s.toString() );
                }
            }
            uploadLog(b.toString());
        }
        if(Constants.IS_DEBUG_MODEL){//打印日志
            ex.printStackTrace();
        }
        return true;
    }

    /**上传奔溃日志
     * @param message
     */
    private void uploadLog(String message){
        JSONObject params=new JSONObject();
        try {
            SharedPreferences preferences= GKApplication.getInstance().
                    getSharedPreferences(Constants.USER_TABLE, Activity.MODE_PRIVATE);
            params.put("phone",preferences.getString(Constants.USER_ACCOUNT,""));
            params.put("contents",message);
            params.put("device_name",android.os.Build.MODEL);
            params.put("sys_version","Android");
            params.put("device_type",String.valueOf(Build.VERSION.SDK_INT));
            PackageManager pm = mContext.getPackageManager();
            PackageInfo packageInfo = null;
            packageInfo = pm.getPackageInfo(mContext.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            params.put("app_version",packageInfo.versionCode);
            volleyUtils.post( Constants.REQUEST_CRASH_LOG_URL,new JSONObject().put("logger", params),null,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return  返回文件名称,便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) {

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = "crash-" + time + "-" + timestamp + ".log";
            if (true) {
                String path =Constants.SD_ROOT_PATH + "/crashLog";
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(path + "/" +fileName);
                fos.write(sb.toString().getBytes());
                fos.close();
            }
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file..." + e.getMessage());
        }
        return null;
    }

}
