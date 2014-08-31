package com.lucas.shakepicture.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.lucas.picareaselect.Util;
import com.lucas.shakepicture.datastructure.SwingList;
import com.lucas.shakepicture.datastructure.SwingList.SwingListIterator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class VerticesView extends View {
    
    // 此BlockingQueue只需容纳一个元素
    private BlockingQueue<Object> blockingQueue = new ArrayBlockingQueue<Object>(1);
    
    private final Paint mPaint = new Paint();

 //   private final float[] mVerts = new float[10];
  //  private final float[] mTexs = new float[10];
  //  private final short[] mIndices = { 0, 1, 2, 3, 4, 1 };
    
    /*
     * 运动反向与X轴的夹角的tan值
     * 坐标系：以此VerticesView的中心为原点，向右为x正轴，向上为y正轴
     */
    private float tanOfAngle = 0; // 默认横线震动，夹角tan值为0
    
    private class SubVerticesView {
 //       private final Paint paint = new Paint();
        
        private RectF rectF;
        
        // 每运行一个半径的总步数
        private int stepCountForOneRadius;
        private float step;

        private float[] verts = new float[10];
        private float[] texs = new float[10];
        private short[] indices = { 0, 1, 2, 3, 4, 1 };
        
        private SwingList<PointF> path;
        private SwingListIterator<PointF> iter;
        
        private boolean loopOnePeriod = false;
        
        public SubVerticesView(RectF rectF, int stepCountForOneRadius) {
            
            this.rectF = rectF;
            this.stepCountForOneRadius = stepCountForOneRadius;
            
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
            
            path = new SwingList<PointF>(new PointF(centerX, centerY), new SwingList.OnOnePeriodListener() {

                @Override
                public void onOnePeriod() {
                    loopOnePeriod = true;
                }
            });
            
            setPath(0);
            
            reset();
        }
        
        // 可以通过缩小震动范围来达到慢慢震动停止的效果
        public void setPath(float tanOfAngle) {
            
            // 每次只运动85%的距离，防止画面过于生硬
            float shakeRadius = (rectF.height() / 2) * (85/(float)100);
            step = shakeRadius/stepCountForOneRadius; // 步长
            
            float centerX = rectF.centerX();
            float centerY = rectF.centerY();

            path.add(new PointF(centerX, centerY));
            
            float y = centerY;
            for(int i = 0; i < stepCountForOneRadius; i++) {
                y -= step;
                path.add(new PointF(centerX, y));
               
            }
            
            y = centerY;
            for(int i = 0; i < stepCountForOneRadius; i++) {
                y += step; 
                path.add(0, new PointF(centerX, y));
               
            }
        }
        
        public void reset() {
            iter = path.iterator();
        }
        
        public void act() {
            if(path.size() < 2) {  // 不足两个点了，没必要震了
                stop = true;
                return;
            }
            
            PointF p = iter.next();
            setXY(verts, 0, p.x, p.y);
            
            if(loopOnePeriod) {
                path.remove(path.size() - 1);
                path.remove(0);
                reset();
                loopOnePeriod = false;
            }
        }
    }
    
    private List<SubVerticesView> allSubVVs = new ArrayList<SubVerticesView>();

    private static void setXY(float[] array, int index, float x, float y) {
        array[index * 2 + 0] = x;
        array[index * 2 + 1] = y;
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
        
        Iterator<RectF> iter = rects.iterator();
        while(iter.hasNext()) {     
            allSubVVs.add(new SubVerticesView(iter.next(), 30)); // 一个半径运行30步
        }

        Shader s = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mPaint.setShader(s);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /*
         * 点击改变震动方向
         * 坐标系：以此VerticesView的中心为原点，向右为x正轴，向上为y正轴
         */
        if((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            
            // 中心点（即原点）的坐标
            float originX = getWidth() / (float) 2;
            float originY = getHeight() / (float)2;

            // 点击点相对于左上角的坐标
            float x = event.getX();
            float y = event.getY();
            
            // 转换为相对于中心（即原点）的坐标
            x -= originX;
            y -= originY;
            
            tanOfAngle = y/x;
        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(xTranslateOnDraw == -1 || yTranslateOnDraw == -1) {
            xTranslateOnDraw = (getWidth() - bitmap.getWidth()) / 2;
            yTranslateOnDraw = (getHeight() - bitmap.getHeight()) / 2;
        }

        canvas.save();
        
        canvas.translate(xTranslateOnDraw, yTranslateOnDraw);
        canvas.drawBitmap(bitmap, 0, 0, new Paint());
        
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
                            mPaint);
        }   

        canvas.restore();
    }
        
    private boolean stop = false;
    
    public void autoShake() {            
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

