package com.lucas.shakepicture.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class VerticesView extends View {
    private final Paint mPaint = new Paint();

 //   private final float[] mVerts = new float[10];
  //  private final float[] mTexs = new float[10];
  //  private final short[] mIndices = { 0, 1, 2, 3, 4, 1 };
    
    private static class SubVerticesView {
 //       private final Paint paint = new Paint();
        
        public RectF rectF;

        public float[] verts = new float[10];
        public float[] texs = new float[10];
        public short[] indices = { 0, 1, 2, 3, 4, 1 };
        
        public boolean contains(float x, float y) {
            return rectF.contains(x, y);
        }
    }
    
    private List<SubVerticesView> allSubVVs = new ArrayList<SubVerticesView>();

 //   private final Matrix mMatrix = new Matrix();

 //   private final Matrix mInverse = new Matrix();

    // ImageView v;

    private void setXY(float[] array, int index, float x, float y) {
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

    public void init(Bitmap bitmap, ArrayList<RectF> rects) {
        this.bitmap = bitmap;

        setFocusable(true);
        
        Iterator<RectF> iter = rects.iterator();
        while(iter.hasNext()) {           
            RectF r = iter.next();
            SubVerticesView svv = new SubVerticesView();
            
            svv.rectF = r;
            
            setXY(svv.texs, 0, r.left + r.width()/2, r.top + r.height()/2);
            setXY(svv.texs, 1, r.left, r.top);
            setXY(svv.texs, 2, r.right, r.top);
            setXY(svv.texs, 3, r.right, r.bottom);
            setXY(svv.texs, 4, r.left, r.bottom);
            
            setXY(svv.verts, 0, r.left + r.width()/2, r.top + r.height()/2);
            setXY(svv.verts, 1, r.left, r.top);
            setXY(svv.verts, 2, r.right, r.top);
            setXY(svv.verts, 3, r.right, r.bottom);
            setXY(svv.verts, 4, r.left, r.bottom);
            
            allSubVVs.add(svv);
        }

        // Bitmap bm = BitmapFactory.decodeResource(getResources(),
        // R.drawable.beach);
        Shader s = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mPaint.setShader(s);

   //     float w = bitmap.getWidth();
   //     float h = bitmap.getHeight();
        // construct our mesh
        
//      setXY(mTexs, 0, w / 2, h / 2);
//      setXY(mTexs, 1, 0, 0);
//      setXY(mTexs, 2, w, 0);
//      setXY(mTexs, 3, w, h);
//      setXY(mTexs, 4, 0, h);
//
//      setXY(mVerts, 0, w / 2, h / 2);
//      setXY(mVerts, 1, 0, 0);
//      setXY(mVerts, 2, w, 0);
//      setXY(mVerts, 3, w, h);
//      setXY(mVerts, 4, 0, h);
        
    //    RectF r = rects.get(0);
        
        
//        setXY(mTexs, 0, r.left + r.width()/2, r.top + r.height()/2);
//        setXY(mTexs, 1, r.left, r.top);
//        setXY(mTexs, 2, r.right, r.top);
//        setXY(mTexs, 3, r.right, r.bottom);
//        setXY(mTexs, 4, r.left, r.bottom);
//        
//        setXY(mVerts, 0, r.left + r.width()/2, r.top + r.height()/2);
//        setXY(mVerts, 1, r.left, r.top);
//        setXY(mVerts, 2, r.right, r.top);
//        setXY(mVerts, 3, r.right, r.bottom);
//        setXY(mVerts, 4, r.left, r.bottom);

 //       mMatrix.setScale(0.8f, 0.8f);
 //       mMatrix.preTranslate(20, 20);
 //       mMatrix.invert(mInverse);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // canvas.drawColor(0xFFCCCCCC);
        canvas.save();
  //      canvas.concat(mMatrix);

        // canvas.drawVertices(Canvas.VertexMode.TRIANGLE_FAN, 10, mVerts, 0,
        // mTexs, 0, null, 0, null, 0, 0, mPaint);

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
  //      canvas.drawVertices(Canvas.VertexMode.TRIANGLE_FAN, 10, mVerts, 0,
  //              mTexs, 0, null, 0, mIndices, 0, 6, mPaint);
        
        Iterator<SubVerticesView> iter = allSubVVs.iterator();
        
        while (iter.hasNext()) {
            SubVerticesView svv = iter.next();
            canvas.drawVertices(Canvas.VertexMode.TRIANGLE_FAN, 10, svv.verts, 0,
                    svv.texs, 0, null, 0, svv.indices, 0, 6, mPaint);
        }   

        // canvas.drawBitmap(bitmap, 0, 0, mPaint);

        // canvas.translate(0, 240);
        // canvas.drawVertices(Canvas.VertexMode.TRIANGLE_FAN, 10, mVerts, 0,
        // mTexs, 0, null, 0, mIndices, 0, 6, mPaint);

        canvas.restore();
    }

    public void drag(int x, int y) {
    //    float[] pt = { x, y };
  //      mInverse.mapPoints(pt);
    //    setXY(mVerts, 0, pt[0], pt[1]);
        
        Iterator<SubVerticesView> iter = allSubVVs.iterator();
        
        while (iter.hasNext()) {
            SubVerticesView svv = iter.next();
            if(svv.contains(x, y)) {
                setXY(svv.verts, 0, x, y);
                break;  // 各个区域没有重叠，所以一个点只能在一个区域中
            }
        }        
        
        invalidate();
    }
        
    private Handler ChildThreadHandler = null;
    private boolean stop = false;
    
    public void autoShake() {        
        
        final Handler mainThreadHandler = new Handler(new Handler.Callback() {
            
            @Override
            public boolean handleMessage(Message msg) {
             
                Iterator<SubVerticesView> iter = allSubVVs.iterator();
                
                while (iter.hasNext()) {
                    SubVerticesView svv = iter.next();
                    setXY(svv.verts, 0, svv.rectF.left + svv.rectF.width()/2, msg.what);
                }  
                    
                invalidate();
                
                ChildThreadHandler.sendMessage(Message.obtain());
                return true;
            }
        });

        new Thread(new Runnable() {
            private int y = (int) allSubVVs.get(0).rectF.top;
            private int step = 10;   // 修改此值可以影响震动速度
            
            private void move() {
                    y += step;
                    Message msg = Message.obtain();
                    msg.what = y;
                    mainThreadHandler.sendMessage(msg);
                    if(y < allSubVVs.get(0).rectF.top || y > allSubVVs.get(0).rectF.bottom) {
                        step = -step;
                    }
            }
            
            @Override
            public void run() {
                Looper.prepare();
                
                ChildThreadHandler = new Handler(new Handler.Callback() {
                    
                    @Override
                    public boolean handleMessage(Message msg) {
                        if(stop) {
                            Looper.myLooper().quit();
                        } else {
                            move();
                        }
                        
                        return true;
                    }
                });
                
                move();

                /*
                 * 建立一个消息循环，该线程不会退出
                 * 下面这一句必须放在方法的最后，因为其后面的代码不会执行了
                 */
                Looper.loop(); 
            }
        }).start();
       
    }

    public void stopShake() {
        stop = true;
    }

}


//setXY(mTexs, 0, w / 2, h / 2);
//setXY(mTexs, 1, 0, 0);
//setXY(mTexs, 2, w, 0);
//setXY(mTexs, 3, w, h);
//setXY(mTexs, 4, 0, h);

//setXY(mVerts, 0, w / 2, h / 2);
//setXY(mVerts, 1, 0, 0);
//setXY(mVerts, 2, w, 0);
//setXY(mVerts, 3, w, h);
//setXY(mVerts, 4, 0, h);



