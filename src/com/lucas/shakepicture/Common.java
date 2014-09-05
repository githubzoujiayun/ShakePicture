package com.lucas.shakepicture;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class Common {
    
    private static final String FOLDER_NAME = "ShakePicture";

    public static File getFileInSdcardByName(Context context, String fileName, boolean createIfNoeExists) {
        String state = Environment.getExternalStorageState();
        if(!state.equals(Environment.MEDIA_MOUNTED)) {
            Log.e(FOLDER_NAME, "SD卡没有插入或没有写权限");
            Toast.makeText(context, "SD卡没有插入或没有写权限", Toast.LENGTH_LONG).show();
            return null;
        }

        File file = new File(Environment.getExternalStorageDirectory(), FOLDER_NAME);

        if(!file.exists()) {
            if(!file.mkdirs()) {
                Log.e(FOLDER_NAME, "在SD卡中创建文件夹失败");
                Toast.makeText(context, "在SD卡中创建文件夹失败", Toast.LENGTH_LONG).show();
                return null;
            }
        }
        
        File f =  new File(file, fileName);
        if(!f.exists()) {
            if(!createIfNoeExists) {
                return null;
            }

            try {
                f.createNewFile();
            } catch (IOException e) {
                Log.e("", "fffff   " + e.getMessage());
            }

        }
        
        return f;
    }
    
    public static void deleteFileInSdcardByName(Context context, String fileName) {
        File f = getFileInSdcardByName(context, fileName, false);
        if(f != null) {
            f.delete();
        }
    }   

}
