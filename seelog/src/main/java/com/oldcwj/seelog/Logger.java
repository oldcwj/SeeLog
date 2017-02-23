package com.oldcwj.seelog;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * 用于保存log到sd卡,然后上传到服务器
 */
public class Logger {

    private static int screenWidth = 0, screenHeight = 0;

    public synchronized static void saveMotionEvent(MotionEvent event, Object context) {
        try {
            String msg = getEventValue(event, context);
            if (msg != null && !msg.equals("")) {
                Context context1 = (Context)context;
                saveLog(context1, msg);
            }
        } catch (Exception e) {
            Log.i("Logger", "e saveMotionEvent");
        }
    }

    private static String getEventValue(MotionEvent event, Object context) {
        if (screenWidth == 0 || screenHeight == 0) {
            Context context1 = (Context)context;
            WindowManager wm = (WindowManager) context1
                    .getSystemService(Context.WINDOW_SERVICE);
            screenWidth = wm.getDefaultDisplay().getWidth();
            screenHeight = wm.getDefaultDisplay().getHeight();
        }

        String value = "";
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                String className = context.getClass().getName();
                value = className + "[" + screenWidth + "," + screenHeight + "]";
                float rawX = event.getRawX();
                float rawY = event.getRawY();
                value += "(" + rawX + "," + rawY + ")";
                String t = getDate(0, "yyyy-MM-dd HH:mm:ss:SSS");
                value = String.format("%s|%s\n", t, value);
                break;
            default:
                break;
        }

        return value;
    }

    public static String getDate(int type, String t) {
        String resultValue = "";
        try {
            Calendar ca = Calendar.getInstance();
            ca.add(Calendar.DATE, type);
            SimpleDateFormat df = (SimpleDateFormat) DateFormat.getInstance();
            df.applyPattern(t);
            resultValue = df.format(ca.getTime());
        } catch (Exception e) {
            Log.i("Logger", "e getDate");
        }

        return resultValue;
    }

    public static void saveLog(Context context, String log) {
        LogHandler logHandler = LogHandler.getInstance(context);
        logHandler.procMsg(1, log);
    }
}
