package com.lucas.shakepicture.pictureselector;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.lucas.shakepicture.R;
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
    private Set<AsyncTask<Integer, Void, Bitmap>> allTasks = new HashSet<AsyncTask<Integer, Void, Bitmap>>();

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
                 return value.getByteCount();
            }

        };
    }

     int i = 0;
    
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if(convertView != null) {
            Log.e("", "QIJIQIJIQIJI");
        }
        
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.pic_wall_item, null);  
            
            Holder holder = new Holder();
            holder.iv = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } 
        
        final Holder holder = (Holder) convertView.getTag();
        final ImageView iv = holder.iv;    
        
        if(holder.task != null && !holder.task.isCancelled()) {
            holder.task.cancel(true);
        }
        
        allTasks.remove(holder.task);
       
        final String picPath = getItem(position);
        GridView gv = (GridView) parent;
        final int w = gv.getColumnWidth();
        
        Bitmap bitmap = picCache.get(position);
        if(bitmap != null) {
            iv.setImageBitmap(bitmap);            
            return convertView;
        }
        
        AsyncTask<Integer, Void, Bitmap> task = new AsyncTask<Integer, Void, Bitmap> () {

            @Override
            protected Bitmap doInBackground(Integer... params) {                   
                InputStream is = null;
                try {
                    is = PictureSelector.assetManager.open(picPath);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }

                Bitmap bt =  BitmapLib.decodeBitmap(context, is, w, w, PicZoomOutType.DIG_CENTER);
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return bt;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                if(result != null) {
                    picCache.put(position, result);
                    iv.setImageBitmap(result);
                }
            }
            
        };
        
        holder.task = task;
        allTasks.add(task);
        task.execute(position);
        
        return convertView;
    }

    public void cancelAllTasks() {
        Iterator<AsyncTask<Integer, Void, Bitmap>> iter = allTasks.iterator();

        while (iter.hasNext()) {
            AsyncTask<Integer, Void, Bitmap> task = iter.next();
            if (!task.isCancelled())
                task.cancel(true);
        }
    }

    private class Holder {
        public ImageView iv;
        public AsyncTask<Integer, Void, Bitmap> task;        
    }
}
