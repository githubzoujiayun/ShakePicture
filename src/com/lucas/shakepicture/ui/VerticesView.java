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
    
    // ��BlockingQueueֻ������һ��Ԫ��
    private BlockingQueue<Object> blockingQueue = new ArrayBlockingQueue<Object>(1);
    
    private final Paint mPaint = new Paint();

 //   private final float[] mVerts = new float[10];
  //  private final float[] mTexs = new float[10];
  //  private final short[] mIndices = { 0, 1, 2, 3, 4, 1 };
    
    /*
     * �˶�������X��ļнǵ�tanֵ
     * ����ϵ���Դ�VerticesView������Ϊԭ�㣬����Ϊx���ᣬ����Ϊy����
     */
    private float tanOfAngle = 0; // Ĭ�Ϻ����𶯣��н�tanֵΪ0
    
    private class SubVerticesView {
 //       private final Paint paint = new Paint();
        
        private RectF rectF;
        
        // ÿ����һ���뾶���ܲ���
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
        
        // ����ͨ����С�𶯷�Χ���ﵽ������ֹͣ��Ч��
        public void setPath(float tanOfAngle) {
            
            // ÿ��ֻ�˶�85%�ľ��룬��ֹ���������Ӳ
            float shakeRadius = (rectF.height() / 2) * (85/(float)100);
            step = shakeRadius/stepCountForOneRadius; // ����
            
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
            if(path.size() < 2) {  // �����������ˣ�û��Ҫ����
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
    
    // ��ʼ��һ����Чֵ
    private int xTranslateOnDraw = -1;
    private int yTranslateOnDraw = -1;

    public void init(Bitmap bitmap, ArrayList<RectF> rects) {
        
        this.bitmap = bitmap;

        setFocusable(true);
        
        Iterator<RectF> iter = rects.iterator();
        while(iter.hasNext()) {     
            allSubVVs.add(new SubVerticesView(iter.next(), 30)); // һ���뾶����30��
        }

        Shader s = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mPaint.setShader(s);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /*
         * ����ı��𶯷���
         * ����ϵ���Դ�VerticesView������Ϊԭ�㣬����Ϊx���ᣬ����Ϊy����
         */
        if((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            
            // ���ĵ㣨��ԭ�㣩������
            float originX = getWidth() / (float) 2;
            float originY = getHeight() / (float)2;

            // �������������Ͻǵ�����
            float x = event.getX();
            float y = event.getY();
            
            // ת��Ϊ��������ģ���ԭ�㣩������
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
         * VertexMode �������� �������������Σ�����3�����㣩���� �ı��� ������4�����㣩�ȵ� vertexCount ������
         * �ܹ��ж��ٸ�������ơ� verts[] �������� [0,0,0,1,1,0,...] ǰ����xy 3�� �����������������
         * ���͹���һ�������εĻ��ƻ�Ԫ���������ơ� vertOffset �������� ��ʼλ�� ����ȫ�����ƣ�Ҳ����ֻ���Ʋ��ֶ��㡣��
         * vertexCount ����ʹ�� һ��Ϊ0 texs[] �������� ���Ƕ�ͼƬ�Ƚ��в�����Ȼ��ȥ��Ⱦ���㡣������Ƚϸ��ӣ���Ҫ�˽���
         * ����opengl��Ⱦԭ���� texOffset ͬ��offset ����ƫ���� colors[] ��ɫ���� ֱ������ɫ��Ⱦ����
         * colorOffset ͬ��offset ����ƫ���� indices[] �������� ����ֻ���Ʋ��ֶ��� ������Ǵ����Щ�����index
         * �� ��verts[index] indexOffset ͬ��offset ����ƫ���� indexCount ���ƶ��ٸ������㡣 paint
         * ���ֻ�� texs[] ���� �����ṩ�� �ṩͼƬ֮�ණ�� ������
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

