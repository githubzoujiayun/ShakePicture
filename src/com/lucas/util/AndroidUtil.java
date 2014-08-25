package com.lucas.util;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class AndroidUtil {   
        
    private static int screenWidth = -1;
    private static int screenHeight = -1;
    
    public static int getScreenWidth(Context context) {
        if(screenWidth != -1) {
            return screenWidth;
        }
        
        Point size = new Point();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(size);
        
        screenWidth = size.x;
        return screenWidth;
    }
    
    public static int getScreenHeight(Context context) {
        if(screenHeight != -1) {
            return screenHeight;
        }
        
        Point size = new Point();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(size);
        
        screenHeight = size.y;
        return screenHeight;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     * @param context
     * @param dpValue
     * @return
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
        
    //    Resources res = context.getResources();
    //    DisplayMetrics dm = res.getDisplayMetrics();
    //    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, dm);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    
    /*
     *  v: parent view of TextViews
     *  ids: TextView's id in v
     *  names: column names
     */
    public static void setTVTextByCursor(View v, int[] ids, Cursor c, String[] names) {
        for(int i = 0; i < ids.length; i++) {
            ((TextView)(v.findViewById(ids[i]))).setText(c.getString(c.getColumnIndex(names[i])));
        }
    }
    
    /*
     *  v: parent view of TextViews
     *  id: TextView's id in v
     */    
    public static String getTVText(View v, int id) {
        return ((TextView) v.findViewById(id)).getText().toString();
    }
    
    public static void setTVText(View v, int id, CharSequence text) {
        ((TextView) v.findViewById(id)).setText(text);
    }
    
    public static File getFileInSdcardByName(Context context, String fileName, boolean createIfNoeExists) {
        String state = Environment.getExternalStorageState();
        if(!state.equals(Environment.MEDIA_MOUNTED)) {
            Log.e("MBM", "SD卡没有插入或没有写权限");
            Toast.makeText(context, "SD卡没有插入或没有写权限", Toast.LENGTH_LONG).show();
            return null;
        }

        File file = new File(Environment.getExternalStorageDirectory(), "HappyMemo");

        if(!file.exists()) {
            if(!file.mkdirs()) {
                Log.e("MBM", "在SD卡中创建文件夹失败");
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
