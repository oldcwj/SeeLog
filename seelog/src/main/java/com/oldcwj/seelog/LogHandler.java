package com.oldcwj.seelog;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import java.io.File;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LogHandler extends Handler {
    private final static String TAG = LogHandler.class.getSimpleName();

    public static final int MESSAGE_SAVE_LOG = 1;
    public static final int MESSAGE_SEND_LOG = 2;

    private Context mContext;
    private FileManager fm;
    private File f;

    private static LogHandler mLogHandler;

    public LogHandler(Looper looper, Context c) {
        super(looper);
        mContext = c;
        fm = new FileManager(mContext);
    }

    public static LogHandler getInstance(Context context) {
        if (mLogHandler == null) {
            HandlerThread mLogThread = new HandlerThread("mLogThread");
            mLogThread.start();
            mLogHandler = new LogHandler(mLogThread.getLooper(), context.getApplicationContext());
        }

        return mLogHandler;
    }

    public synchronized void procMsg(int iMsgId, String msgContent) {
        Message msg = obtainMessage();
        msg.what = iMsgId;
        msg.obj = msgContent;
        sendMessage(msg);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_SAVE_LOG:
                //以日期来做文件名
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dateStr = sdf.format(date);
                fm.saveLog(dateStr, (String) msg.obj);

                //如果用户当前log前两天的log没上传 ，就把前两天之前的前两天的log删除掉
                String[] str1 = new String[2];

                for (int i = 0; i < str1.length; i++) {
                    try {
                        str1[i] = getStatetime(i - 3);
                        f = fm.getLog(str1[i]);
                        if (f == null) {
                            continue;
                        } else {
                            f.delete();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                break;
            case MESSAGE_SEND_LOG:
                // TODO send log to server
                break;
            default:
                break;
        }
    }

    /**参数 -2当前日期钱两天是几号   2后两天
     * @return 当前日期的前几天日期是多少号
     * @throws ParseException
     */
    public static String getStatetime(int day) throws ParseException{

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, day);
        Date monday = c.getTime();
        String preMonday = sdf.format(monday);
        return preMonday;
    }
}
