package com.lucas.shakepicture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.lucas.shakepicture.SwingList.SwingListIterator;


public class VerticesView extends View {
    
    private Paint bitmapPaint = new Paint();
    private Paint verticesPaint = new Paint();    
    
    /*
     * 运动反向与X轴的夹角的tan值
     * 坐标系：以此VerticesView的中心为原点，向右为x正轴，向上为y正轴
     */
    private double angle = Math.PI / 2; // 震动夹角的弧度值，默认垂直震动
    
    // 最大振幅占总范围的百分比，如全范围震，则画面过于生硬
    private static final float MAX_RANGE_PERCENT = (70/(float)100);
    
    // 一个半径步数的最小值
    private static final int STEP_COUNT_FOR_ONE_REDIUS_MIN = 10;
    
    // 一个半径步数的初始值
    private static final int STEP_COUNT_FOR_ONE_REDIUS_ORIGINAL = 50;
    
    private int stepCountForOneRadius = STEP_COUNT_FOR_ONE_REDIUS_ORIGINAL;
    
    private boolean stop = false;
    
    /**
     * 设置震动的角度
     */
    public void setAngle(double angle) {
        this.angle = angle;

        // 如果已经停止了，就重启震动
        if (stop) {
            Iterator<SubVerticesView> iter = allSubVVs.iterator();

            while (iter.hasNext()) {
                SubVerticesView svv = iter.next();
                svv.setMyPath();
            }

            start();
            return;
        }

        Iterator<SubVerticesView> iter = allSubVVs.iterator();

        while (iter.hasNext()) {
            SubVerticesView svv = iter.next();
            svv.needResetPath = true;
        }
    }
    
    public void speedUp() {
        if(stepCountForOneRadius - 2 >= STEP_COUNT_FOR_ONE_REDIUS_MIN) {
            stepCountForOneRadius -= 2;
        }
        
        // 如果已经停止了，就重启震动
        if (stop) {
            Iterator<SubVerticesView> iter = allSubVVs.iterator();

            while (iter.hasNext()) {
                SubVerticesView svv = iter.next();
                svv.setMyPath();
            }

            start();
            return;
        }

        Iterator<SubVerticesView> iter = allSubVVs.iterator();

        while (iter.hasNext()) {
            SubVerticesView svv = iter.next();
            svv.needResetPath = true;
        }
    }
    
    private class SubVerticesView {
 //       private final Paint paint = new Paint();
        
        private RectF rectF;
        
        private boolean needResetPath = false;
        
        // 每运行一个半径的总步数
 //       private int stepCountForOneRadius;
  //      private double step;

        private float[] verts = new float[10];
        private float[] texs = new float[10];
        private short[] indices = { 0, 1, 2, 3, 4, 1 };
        
        private SwingList<PointF> path;
        private SwingListIterator<PointF> iter;
        
        private boolean loopOnePeriod = false;     
        
        public SubVerticesView(RectF rectF) {
            this.rectF = rectF;
      //      this.stepCountForOneRadius = stepCountForOneRadius;
            
            float left = rectF.left;
            float right = rectF.right;
            float top = rectF.top;
            float bottom = rectF.bottom;
            
            float centerX = rectF.centerX();
            float centerY = rectF.centerY();
            
            setXY(texs, 0, centerX, centerY);
            setXY(texs, 1, left, top);
            setXY(texs, 2, right, top);
            setXY(texs, 3, right, bottom);
            setXY(texs, 4, left, bottom);
            
            setXY(verts, 1, left, top);
            setXY(verts, 2, right, top);
            setXY(verts, 3, right, bottom);
            setXY(verts, 4, left, bottom);
            
            path = new SwingList<PointF>(new SwingList.OnOnePeriodListener() {

                @Override
                public void onOnePeriod() {
                    loopOnePeriod = true;
                }
            });
            
            setMyPath(); 
        }
        
        private void setMyPath() {
            setPath(rectF, path);
            setPathIterator();
        }

        public void setPathIterator() {
            iter = path.iterator();
        }
        
        public void act() {
            if(needResetPath) {
                setMyPath();
                needResetPath = false;
            }
            
            if(path.size() < 2) {  // 不足两个点了，没必要震了
                stop = true;
                return;
            }
            
            PointF p = iter.next();
            setXY(verts, 0, p.x, p.y);
            
            if(loopOnePeriod) {
                // 没过一个周期，path消去两端，使震动慢慢停止
                path.remove(path.size() - 1);
                path.remove(0);
                setPathIterator();
                loopOnePeriod = false;
            }
        }
    }
    
    private List<SubVerticesView> allSubVVs = new ArrayList<SubVerticesView>();

    private static void setXY(float[] array, int index, float x, float y) {
        array[index * 2 + 0] = x;
        array[index * 2 + 1] = y;
    }
    
    public void setPath(RectF rectF, ArrayList<PointF> path) {
        
        float halfWidth = rectF.width() / 2;
        float halfHeiht = rectF.height() / 2;
        
        // 矩形右上角和中心点的夹角的弧度值
        double rectAngle = Math.atan2(halfHeiht, halfWidth);
        
        double shakeRadius;  // 震动的半径
        
        // π rad == 180° ( rad: 弧度 )
        if(angle > rectAngle && angle < (Math.PI - rectAngle)) {
            // 相交于矩形的上，下边界
            shakeRadius = halfHeiht / Math.sin(angle);
        } else {
            // 相交于矩形的左，右边界
            shakeRadius = halfWidth / Math.cos(angle);
        }
        
        shakeRadius *= MAX_RANGE_PERCENT;
        
        double step = shakeRadius / stepCountForOneRadius; // 步长
        
        double xStep = step * Math.cos(angle);
        double yStep = step * Math.sin(angle);
        
        float centerX = rectF.centerX();
        float centerY = rectF.centerY();

        path.clear();
        path.add(new PointF(centerX, centerY));
        
        float x = centerX;
        float y = centerY;
        for(int i = 0; i < stepCountForOneRadius; i++) {
            x += xStep; // 向右（夹角小于90度）或向左（夹角大于90度）运动
            y -= yStep; // 向上运动
            path.add(new PointF(x, y));
           
        }
        
        x = centerX;
        y = centerY;
        for(int i = 0; i < stepCountForOneRadius; i++) {
            x -= xStep;
            y += yStep;
            path.add(new PointF(x, y));
           
        }

        /*
         * 优先已x轴方向为基准运动，如x相同则以y轴正方向为基准运动
         */
        Collections.sort(path, new Comparator<PointF>() {

            @Override
            public int compare(PointF lhs, PointF rhs) {
               if(lhs.x < rhs.x) 
                   return -1;
               else if(lhs.x > rhs.x) 
                   return 1;
               else if(lhs.y > rhs.y) 
                   return -1;
               else if(lhs.y < rhs.y) 
                   return 1;
               return 0;
            }
        });
    }

    private Bitmap bitmap;

    public VerticesView(Context context) {
        super(context);
    }

    public VerticesView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    
    // 初始化一个无效值
    private int xTranslateOnDraw = -1;
    private int yTranslateOnDraw = -1;

    public void init(Bitmap bitmap, ArrayList<RectF> rects) {
        
        this.bitmap = bitmap;

        setFocusable(true);
        
        allSubVVs.clear();
        
        Iterator<RectF> iter = rects.iterator();
        while(iter.hasNext()) {     
            allSubVVs.add(new SubVerticesView(iter.next())); 
        }

        Shader s = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        verticesPaint.setShader(s);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        if(xTranslateOnDraw == -1 || yTranslateOnDraw == -1) {
            xTranslateOnDraw = (getWidth() - bitmap.getWidth()) / 2;
            yTranslateOnDraw = (getHeight() - bitmap.getHeight()) / 2;
        }
        
        canvas.translate(xTranslateOnDraw, yTranslateOnDraw);
        canvas.drawBitmap(bitmap, 0, 0, bitmapPaint);
        
        /*
         * VertexMode 顶点类型 比如他是三角形（连续3个顶点）或者 四边形 （连续4个顶点）等等 vertexCount 顶点数
         * 总共有多少个顶点绘制。 verts[] 顶点数组 [0,0,0,1,1,0,...] 前面有xy 3组 如果是类型是三角形
         * 他就构成一个三角形的绘制基元，往后类推。 vertOffset 顶点数据 起始位置 可能全部绘制，也可能只绘制部分顶点。与
         * vertexCount 配置使用 一般为0 texs[] 纹理数组 就是对图片等进行采样，然后去渲染顶点。（这个比较复杂，需要了解下
         * 比如opengl渲染原理。） texOffset 同上offset 就是偏移量 colors[] 颜色数组 直接用颜色渲染顶点
         * colorOffset 同上offset 就是偏移量 indices[] 顶点索引 可能只绘制部分顶点 这个就是存放那些顶点的index
         * ， 即verts[index] indexOffset 同上offset 就是偏移量 indexCount 绘制多少个索引点。 paint
         * 这个只有 texs[] 不空 必须提供， 提供图片之类东西 采样。
         */
        
        Iterator<SubVerticesView> iter = allSubVVs.iterator();
        
        while (iter.hasNext()) {
            SubVerticesView svv = iter.next();
            canvas.drawVertices(Canvas.VertexMode.TRIANGLE_FAN, 
                            10, svv.verts, 0,    // vertex info
                            svv.texs, 0,         // tex info
                            null, 0,            // color info
                            svv.indices, 0, 6,  // indice info
                            verticesPaint);
        } 
    }
    
    public List<Bitmap> getBitmapsOnPath() {
        class ViewInfo {
            private SwingListIterator<PointF> iter;
            private float[] verts;
            private float[] texs;
            private short[] indices;
        }
        
        List<ViewInfo> viewInfoList = new ArrayList<ViewInfo>();
        
        Iterator<SubVerticesView> iter = allSubVVs.iterator();
        
        while (iter.hasNext()) {
            SubVerticesView svv = iter.next();
            
            ViewInfo viewInfo = new ViewInfo();
            
            SwingList<PointF> path = new SwingList<PointF>(null); 
            setPath(svv.rectF, path);    
            
            viewInfo.verts = svv.verts.clone();
            viewInfo.texs = svv.texs.clone();
            viewInfo.indices = svv.indices.clone();
            viewInfo.iter = path.iterator();
            
            viewInfoList.add(viewInfo);
        } 
        
        List<Bitmap> bitmapList = new ArrayList<Bitmap>();
        
        // stepCountForOneRadius
        for(int i = 0; i < stepCountForOneRadius * 4; i++) {
            Bitmap bt = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            if(bt == null) {
                return null;
            }
            
            Canvas canvas = new Canvas(bt);
            
            Iterator<ViewInfo> viIter = viewInfoList.iterator();

            while (viIter.hasNext()) {
                ViewInfo info = viIter.next();
                PointF p = info.iter.next();
                
                setXY(info.verts, 0, p.x, p.y);
                canvas.drawVertices(Canvas.VertexMode.TRIANGLE_FAN, 
                                10, info.verts, 0,    // vertex info
                                info.texs, 0,         // tex info
                                null, 0,            // color info
                                info.indices, 0, 6,  // indice info
                                verticesPaint);
            } 
            
            bitmapList.add(bt);
        }
        
        return bitmapList;
    }
    
    public void start() {            
        stop = false;
        
        final Handler mainThreadHandler = new Handler(new Handler.Callback() {
            
            @Override
            public boolean handleMessage(Message msg) {
                invalidate();
                return true;
            }
        });

        new Thread(new Runnable() {
            
            @Override
            public void run() {
                while (!stop) {
                    Iterator<SubVerticesView> iter = allSubVVs.iterator();
                    
                    while (iter.hasNext()) {
                        SubVerticesView svv = iter.next();
                        svv.act();
                    }  
                    
                    mainThreadHandler.sendMessage(Message.obtain());
                    
                    try {
                        Thread.sleep(10); 
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                 } 
            }
        }).start();
       
    }

    public void stopShake() {
        stop = true;
    }

}
