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
     * �˶�������X��ļнǵ�tanֵ
     * ����ϵ���Դ�VerticesView������Ϊԭ�㣬����Ϊx���ᣬ����Ϊy����
     */
    private double angle = Math.PI / 2; // �𶯼нǵĻ���ֵ��Ĭ�ϴ�ֱ��
    
    // ������ռ�ܷ�Χ�İٷֱȣ���ȫ��Χ�����������Ӳ
    private static final float MAX_RANGE_PERCENT = (70/(float)100);
    
    // һ���뾶��������Сֵ
    private static final int STEP_COUNT_FOR_ONE_REDIUS_MIN = 10;
    
    // һ���뾶�����ĳ�ʼֵ
    private static final int STEP_COUNT_FOR_ONE_REDIUS_ORIGINAL = 50;
    
    private int stepCountForOneRadius = STEP_COUNT_FOR_ONE_REDIUS_ORIGINAL;
    
    private boolean stop = false;
    
    /**
     * �����𶯵ĽǶ�
     */
    public void setAngle(double angle) {
        this.angle = angle;

        // ����Ѿ�ֹͣ�ˣ���������
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
        
        // ����Ѿ�ֹͣ�ˣ���������
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
        
        // ÿ����һ���뾶���ܲ���
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
            
            if(path.size() < 2) {  // �����������ˣ�û��Ҫ����
                stop = true;
                return;
            }
            
            PointF p = iter.next();
            setXY(verts, 0, p.x, p.y);
            
            if(loopOnePeriod) {
                // û��һ�����ڣ�path��ȥ���ˣ�ʹ������ֹͣ
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
        
        // �������ϽǺ����ĵ�ļнǵĻ���ֵ
        double rectAngle = Math.atan2(halfHeiht, halfWidth);
        
        double shakeRadius;  // �𶯵İ뾶
        
        // �� rad == 180�� ( rad: ���� )
        if(angle > rectAngle && angle < (Math.PI - rectAngle)) {
            // �ཻ�ھ��ε��ϣ��±߽�
            shakeRadius = halfHeiht / Math.sin(angle);
        } else {
            // �ཻ�ھ��ε����ұ߽�
            shakeRadius = halfWidth / Math.cos(angle);
        }
        
        shakeRadius *= MAX_RANGE_PERCENT;
        
        double step = shakeRadius / stepCountForOneRadius; // ����
        
        double xStep = step * Math.cos(angle);
        double yStep = step * Math.sin(angle);
        
        float centerX = rectF.centerX();
        float centerY = rectF.centerY();

        path.clear();
        path.add(new PointF(centerX, centerY));
        
        float x = centerX;
        float y = centerY;
        for(int i = 0; i < stepCountForOneRadius; i++) {
            x += xStep; // ���ң��н�С��90�ȣ������󣨼нǴ���90�ȣ��˶�
            y -= yStep; // �����˶�
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
         * ������x�᷽��Ϊ��׼�˶�����x��ͬ����y��������Ϊ��׼�˶�
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
    
    // ��ʼ��һ����Чֵ
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
