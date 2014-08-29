package com.lucas.util;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
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
     * �����ֻ��ķֱ��ʴ� dp �ĵ�λ ת��Ϊ px(����)
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
     * �����ֻ��ķֱ��ʴ� px(����) �ĵ�λ ת��Ϊ dp
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
    
    /**
     * Bitmapת��Ϊdrawable
     * 
     * @param bitmap
     * @return
     */
    public static Drawable bitmap2Drawable(Bitmap bitmap) {
        return new BitmapDrawable(bitmap);
    }

    /**
     * Drawable ת bitmap
     * 
     * @param drawable
     * @return
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap.createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }
    
    // ���������ε��ཻ����
    /*
     * �ٶ���������һ�Ե����(minx,miny)(maxx, maxy) ��ô��������rect1{(minx1,miny1)(maxx1,
     * maxy1)}, rect2{(minx2,miny2)(maxx2, maxy2)}
     * 
     * �ཻ�Ľ��һ���Ǹ����Σ���������ཻ����rect{(minx,miny)(maxx, maxy)}�ĵ�������ǣ� minx =
     * max(minx1, minx2) miny = max(miny1, miny2) maxx = min(maxx1, maxx2) maxy
     * = min(maxy1, maxy2)
     * 
     * ����������β��ཻ����ô����õ��ĵ�������Ȼ���� minx > maxx ���� miny > maxy
     */
    public static RectF rectFIntersection(RectF r1, RectF r2) {
        RectF r = new RectF();

        r.left = Math.max(r1.left, r2.left);
        r.top = Math.max(r1.top, r2.top);
        r.right = Math.min(r1.right, r2.right);
        r.bottom = Math.min(r1.bottom, r2.bottom);

        if (r.left > r.right || r.top > r.bottom)
            return null;  // ���ཻ

        return r;
    }
}
