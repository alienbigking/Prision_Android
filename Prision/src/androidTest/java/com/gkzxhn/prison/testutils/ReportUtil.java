package com.gkzxhn.prison.testutils;

import android.util.Log;

import com.gkzxhn.prison.uinttest.common.Constants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 *
 * @author huangzhengneng
 * created on 2016/5/9
 *
 */
public class ReportUtil {

    public static final String TAG = "CrashHandler";
    //系统默认的UncaughtException处理类
    //CrashHandler实例
    private static ReportUtil INSTANCE;
    //用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    /** 获取CrashHandler实例 ,单例模式 */
    public static ReportUtil getInstance() {
        if(INSTANCE==null)INSTANCE=new ReportUtil();
        return INSTANCE;
    }

    /**
     * 保存错误信息到文件中
     *
     * @return  返回文件名称,便于将文件传送到服务器
     */
    public String saveInfo2File(String message) {
        DateFormat longFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String time = longFormatter.format(new Date());
        StringBuffer sb =new StringBuffer();
        sb.append(time+"  "+message+"\n");
        try {
            String date = formatter.format(new Date());
            String fileName = "report-" + date + ".txt";

            BufferedWriter out = null;
            String path = Constants.SD_ROOT_PATH + "/unitReport";
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String filePath=path + "/" +fileName;
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(filePath, true)));
            out.write(sb.toString());
            out.close();
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file..." + e.getMessage());
        }
        return null;
    }
    /**
     * 保存错误信息到文件中
     *
     * @return  返回文件名称,便于将文件传送到服务器
     */
    public String saveInfo2File(Throwable error) {
        DateFormat longFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String time = longFormatter.format(new Date());
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        error.printStackTrace(printWriter);
        Throwable cause = error.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        StringBuffer sb =new StringBuffer();
        sb.append(time+"  "+writer.toString()+"\n");
        try {
            String date = formatter.format(new Date());
            String fileName = "report-" + date + ".txt";

            BufferedWriter out = null;
            String path = Constants.SD_ROOT_PATH + "/unitReport";
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String filePath=path + "/" +fileName;
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(filePath, true)));
            out.write(sb.toString());
            out.close();
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file..." + e.getMessage());
        }
        return null;
    }

}
