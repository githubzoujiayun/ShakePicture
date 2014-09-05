package com.lucas.util;

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
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;


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
    
    /**
     * Bitmap转化为drawable
     * 
     * @param bitmap
     * @return
     */
    public static Drawable bitmap2Drawable(Bitmap bitmap) {
        return new BitmapDrawable(bitmap);
    }

    /**
     * Drawable 转 bitmap
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
    
    // 求两个矩形的相交矩形
    /*
     * 假定矩形是用一对点表达的(minx,miny)(maxx, maxy) 那么两个矩形rect1{(minx1,miny1)(maxx1,
     * maxy1)}, rect2{(minx2,miny2)(maxx2, maxy2)}
     * 
     * 相交的结果一定是个矩形，构成这个相交矩形rect{(minx,miny)(maxx, maxy)}的点对坐标是： minx =
     * max(minx1, minx2) miny = max(miny1, miny2) maxx = min(maxx1, maxx2) maxy
     * = min(maxy1, maxy2)
     * 
     * 如果两个矩形不相交，那么计算得到的点对坐标必然满足 minx > maxx 或者 miny > maxy
     */
    public static RectF rectFIntersection(RectF r1, RectF r2) {
        RectF r = new RectF();

        r.left = Math.max(r1.left, r2.left);
        r.top = Math.max(r1.top, r2.top);
        r.right = Math.min(r1.right, r2.right);
        r.bottom = Math.min(r1.bottom, r2.bottom);

        if (r.left > r.right || r.top > r.bottom)
            return null;  // 不相交

        return r;
    }
    

//    public static void saveAsGif(Context context, List<Bitmap> list) {
//     // change out directory if it is necessary
//        File outputDir = Environment.getExternalStorageDirectory();
//        if (!outputDir.exists()) {
//            // outputDir.mkdirs();
//            return;
//        }      
//
//        ImageIO.scanForPlugins();
//        try {
//            // read images. Here we read from files but it can be any source (internet, database etc.)
//            BufferedImage[] images = new BufferedImage[4];
//            for (int i=0;i< 120;i++) {
//                images[i-1] = ImageIO.read(JavaSEUtil.class.getResourceAsStream( i+".jpg"));
//            }
//            // create gif image
//            GifImage gifImage = new GifImage(160,120,GifImage.RESIZE_STRATEGY_SCALE_TO_FIT_IMAGE_SIZE);
//            // set indefinite looping
//            gifImage.setLoopNumber(0);
//
//            //create font to draw titles
//            Font font = new Font("Arial",Font.BOLD,15);
//            //create TextPainter instance
//            TextPainter painter = new TextPainter(font,true);
//            painter.setForegroundPaint(java.awt.Color.WHITE);
//            // create the first frame
//            GifFrame frame1 = new GifFrame(images[0]);
//            // Render title for this frame
//            painter.setOutlinePaint(java.awt.Color.RED);
//            BufferedImage titleForFrame1 = painter.renderString("House",true);
//            // wrap title with Watermark instance and add frame to gif image
//            gifImage.addGifFrame(frame1,new Watermark(titleForFrame1,Watermark.LAYOUT_TOP_CENTER,1.0f));
//            // create the second frame
//            GifFrame frame2 = new GifFrame(images[1]);
//            // Render title for this frame
//            painter.setOutlinePaint(java.awt.Color.BLUE);
//            BufferedImage titleForFrame2 = painter.renderString("Room",true);
//            // wrap title with Watermark instance and add frame to gif image
//            gifImage.addGifFrame(frame2,new Watermark(titleForFrame2,Watermark.LAYOUT_MIDDLE_CENTER,0.8f));
//            // create the third frame
//            GifFrame frame3 = new GifFrame(images[2]);
//            // Render title for this frame
//            painter.setOutlinePaint(new java.awt.Color(0,82,1));
//            BufferedImage titleForFrame3 = painter.renderString("Kitchen",true);
//            // wrap title with Watermark instance and add frame to gif image
//            gifImage.addGifFrame(frame3,new Watermark(titleForFrame3,Watermark.LAYOUT_BOTTOM_CENTER,0.6f));
//            // create the forth frame
//            GifFrame frame4 = new GifFrame(images[3]);
//            // Render title for this frame
//            painter.setOutlinePaint(java.awt.Color.BLACK);
//            BufferedImage titleForFrame4 = painter.renderString("Garden",true);
//            // wrap header with Watermark instance and add frame to gif image
//            gifImage.addGifFrame(frame4,new Watermark(titleForFrame4,Watermark.LAYOUT_COVER_CONSECUTIVELY,0.4f));
//            // encode gif image
//            GifEncoder.encode(gifImage,new File(outputDir,"ImageTourExample1.gif"),true);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
