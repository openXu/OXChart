package com.openxu.utils;

import android.util.Log;

import com.openxu.cview.BuildConfig;


/**
 * autour : openXu
 * date : 2017/11/6 11:36
 * className : LogUtil
 * version : 1.0
 * description : log日志
 */
public class LogUtil {

    public static void i(String TAG, Object msg) {
        logLong("i", TAG, msg);
    }
    public static void d(String TAG, Object msg) {
        logLong("d", TAG, msg);
    }
    public static void v(String TAG, Object msg) {
        logLong("v", TAG, msg);
    }
    public static void w(String TAG, Object msg) {
        logLong("w", TAG, msg);
    }
    public static void e(String TAG, Object msg) {
        logLong("e", TAG, msg);
    }

    /**
     * 日志太长打印不全时，分段打印
     * @param type
     * @param tag
     * @param msg
     */
    private static void logLong(String type, String tag, Object msg) {
        String content = (null==msg)?"":msg.toString();
        int maxLength = 1000;
        long length = content.length();
        if (length <= maxLength) {
            logByType(type, tag, content);
        }else {
            while (content.length() > maxLength) {
                String logContent = content.substring(0, maxLength);
                content = content.replace(logContent, "");
                logByType(type, tag, logContent);
            }
            logByType(type, tag, content);
        }
    }
    /**打印不同颜色日志*/
    private static void logByType(String type, String tag, String content){
        if(BuildConfig.DEBUG) {
            switch (type){
                case "i":
                    Log.i(tag, ""+(null==content?"":content));
                    break;
                case "d":
                    Log.d(tag, ""+(null==content?"":content));
                    break;
                case "v":
                    Log.v(tag, ""+(null==content?"":content));
                    break;
                case "w":
                    Log.w(tag, ""+(null==content?"":content));
                    break;
                case "e":
                    Log.e(tag, ""+(null==content?"":content));
                    break;
            }
        }
    }

}
