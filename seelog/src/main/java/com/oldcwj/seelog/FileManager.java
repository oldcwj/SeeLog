package com.oldcwj.seelog;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.RandomAccessFile;

public class FileManager {
    Context context;
    
    public FileManager(Context context) {
        this.context = context;
    }
    
    private static boolean isSDExists() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }
    
    private String getRootPath() {
        String resultPath = null;
        try {
            if (isSDExists()) {
                File file = Environment.getExternalStorageDirectory();
                String dirPath = file.getAbsolutePath();
                resultPath = dirPath + "/" + context.getApplicationInfo().packageName;
            } else {
                File dir = context.getFilesDir();
                resultPath = dir.getAbsolutePath();
            }
        } catch (Exception e) {
            Log.e("path", e.getMessage());
        }
        return resultPath;
    }
    
    public String getLogPath() {
        return getRootPath() + "/log";
    }
    
    public void saveLog(String name, String log) {
        File dir = new File(getLogPath());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        if (TextUtils.isEmpty(name)) {
            return;
        }
        
        try {
            if (!name.endsWith(".txt")) {
                name = name + ".txt";
            }
            File file = new File(getLogPath() + "/" + name);
            
            // 以指定文件创建 RandomAccessFile对象
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            
            // 将文件记录指针移动到最后
            raf.seek(file.length());
            // 输出文件内容
            raf.write(log.getBytes());
            // 关闭RandomAccessFile               
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public File getLog(String name) {
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        
        if (!name.endsWith(".txt")) {
            name = name + ".txt";
        }
        
        File file = new File(getLogPath() + "/" + name);
        if (!file.exists()) {
            return null;
        }
        
        return file;
    }

    public void delete(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }
}