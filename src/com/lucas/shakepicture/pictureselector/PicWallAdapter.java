package com.lucas.shakepicture.pictureselector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.lucas.shakepicture_for_google_play.R;
import com.lucas.util.AndroidUtil;
import com.lucas.util.BitmapLib;
import com.lucas.util.BitmapLib.PicZoomOutType;

public class PicWallAdapter extends ArrayAdapter<String> {
    
    // Lru：Least Recently Used 近期最少使用算法
    private LruCache<Integer, Bitmap> picCache;
    
    private Context context;    

    /*
     * 当快速连续两次new此类对象时，会因为前一个对象的图片解码没有完成导致第二次打开activity明显黑屏，
     * 所以这里记录一下第一次的所有任务，在第二次打开之前需cancel掉第一个的所有任务
     */
    private Set<AsyncTask<Object, Void, Bitmap>> allTasks = new HashSet<AsyncTask<Object, Void, Bitmap>>();

    public PicWallAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        
        this.context = context;

        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        // 设置图片缓存大小为程序最大可用内存的1/8
        int cacheSize = maxMemory / 8;
        
        /*
         * maxSize for caches that do not override sizeOf, 
         * this is the maximum number of entries in the cache.
         *  
         * For all other caches, 
         * this is the maximum sum of the sizes of the entries in this cache.
         */
        picCache = new LruCache<Integer, Bitmap>(cacheSize) {

            @Override
            protected int sizeOf(Integer key, Bitmap value) {
                 return value.getByteCount() / 1024;
            }

        };
    }
        
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.pic_wall_item, null);  
        } 
        
        final ImageView iv = (ImageView) convertView.findViewById(R.id.image);  
        // 算每列的宽度，getColumnWidth函数式SDK16以上才有的，所以只好用下面的本办法算下
   //     final int w = gv.getColumnWidth();
        int screenW = AndroidUtil.getScreenWidth(context);
        // XML文件里面设的是列间距3dp
        final int w = (screenW - 2 * (AndroidUtil.dp2px(context, 3)))/3;
        
        
        Bitmap bitmap = picCache.get(position);
        if(bitmap != null) {
            iv.setImageBitmap(bitmap);   
            return convertView;
        } else {
            iv.setImageResource(R.drawable.empty_photo);
        }
                
        class DecodeBitmap {
            Bitmap bitmap = null;
        }
        
        final DecodeBitmap dbt = new DecodeBitmap();
        
        final Handler handler = new Handler(new Handler.Callback() {
            
            @Override
            public boolean handleMessage(Message msg) {
                if(dbt.bitmap != null) {
                    picCache.put(position, dbt.bitmap);
                    iv.setImageBitmap(dbt.bitmap);
                } else {
                    Toast.makeText(context, "内存不足，解码图片失败", Toast.LENGTH_SHORT).show();
                }
                
                return true;
            }
        });
        
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                InputStream is = null;
                try {
                    is = new FileInputStream(new File(getItem(position)));
                  //  is = context.openFileInput("half_star.png");
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.sendMessage(handler.obtainMessage());
                    return;
                }

          //      dbt.bitmap =  BitmapLib.decodeBitmap(context, is, w, w, PicZoomOutType.DIG_CENTER);
                dbt.bitmap =  BitmapLib.decodeBitmap(context, getItem(position), w, w, PicZoomOutType.DIG_CENTER);
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.sendMessage(handler.obtainMessage());
                    return;
                }

                handler.sendMessage(handler.obtainMessage());
            }
        }).start();
        
        return convertView;
    }
       
    public void cancelAllTasks() {
        Iterator<AsyncTask<Object, Void, Bitmap>> iter = allTasks.iterator();

        while (iter.hasNext()) {
            AsyncTask<Object, Void, Bitmap> task = iter.next();
            if (!task.isCancelled())
                task.cancel(true);
        }
    }
}
