package com.lucas.shakepicture.picareaselector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

public class SelectBox extends View {
       
    private float left, top, right, bottom;
                
    private static final int FRAME_WIDTH = 5;  // �߿��ߵĿ��
    private static final int ERROR_RANGE = 10;   // ��Χ������ָ�ϴֲ����ܾ�ȷ���
        
    // ���������(Error Range)��ķ�Χֵ
    private float leftER, topER, rightER, bottomER, frameWidthER;
    
    private Paint framePaint = new Paint();
    
 //   private Paint touchPointPaint = new Paint();    

    public SelectBox(Context context) {
        super(context);

        framePaint.setStrokeWidth(FRAME_WIDTH);
        framePaint.setAntiAlias(true);
        framePaint.setStyle(Paint.Style.STROKE);
        framePaint.setColor(0xFFB8860B); // �����ɫ

        setFocusable(false);
    }

    public void setLocal(float left, float top, float right, float bottom) {
 //       left = left < 0 ? 0 : left;
  //      top = top < 0 ? 0 : top;
        
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    /**
     * ���㿼�����������ʵ�߽�ֵ
     */
    private void calLocalWithErrorRange() {
        leftER = left - ERROR_RANGE;
        topER = top - ERROR_RANGE;
        rightER = right + ERROR_RANGE;
        bottomER = bottom + ERROR_RANGE;
        
        frameWidthER = FRAME_WIDTH + ERROR_RANGE;
    }
 
    public static class Position {
        public enum Dir {
            IN, // �ڿ���

            ON_LEFT, // ����߿���
            ON_TOP,// ���ϱ߿���
            ON_RIGHT,// ���ұ߿���
            ON_BOTTOM,// ���±߿�
            
            OUT, // �ڿ���
            
            UNKNOW
        }
        
        private Dir dir;
        
        public Position(Dir dir) {
            this.dir = dir;
        }
        
        public boolean in() {
            return dir == Dir.IN;
        }
        
        public boolean on() {
            return (dir == Dir.ON_LEFT || dir == Dir.ON_TOP || dir == Dir.ON_RIGHT || dir == Dir.ON_BOTTOM);
        }
        
        public Dir onWhich() {
            return on() ? dir : Dir.UNKNOW;
        }
        
        public boolean out() {
            return dir == Dir.OUT;
        }
        
    }

    // �ж�һ�������ѡ����λ�ù�ϵ
    public Position getPosition(float x, float y) {
        calLocalWithErrorRange();

  //       String s = String.format("%f, %f, %f, %f, %f, %f, %f", x, y, top, top + frameWidthER, rightER, topER, bottomER);
   //      Log.e("", s);

        if (x < leftER || x > rightER || y < topER || y > bottomER)
            return new Position(Position.Dir.OUT);

        if (x < left + frameWidthER)
            return new Position(Position.Dir.ON_LEFT);
        if (x > right - frameWidthER)
            return new Position(Position.Dir.ON_RIGHT);
        if (y < top + frameWidthER)
            return new Position(Position.Dir.ON_TOP);
        if (y > bottom - frameWidthER)
            return new Position(Position.Dir.ON_BOTTOM);

        return new Position(Position.Dir.IN);
    }
    
    /**
     * �ƶ�
     * @param distX ˮƽ������ƶ����룬С��0��ʾ�����ƶ�������ͬ��
     * @param distY
     */
    public void move(float distX, float distY) {        
        setLocal(left + distX, top + distY, right + distX, bottom + distY);
        invalidate();
    }
    
    /**
     * ��ס��߿��������
     * @param dist
     */
    public void zoomLeft(float dist) {
        left += dist;
        invalidate();
    }
    
    public void zoomRight(float dist) {
        right += dist;
        invalidate();
    }
    
    public void zoomTop(float dist) {
        top += dist;
        invalidate();
    }
    
    public void zoomBottom(float dist) {
        bottom += dist;
        invalidate();
    }
    
    public void balance() {
        if(left > right) {
            float t = right;
            right = left;
            left = t;
        }
        
        if(top > bottom) {
            float t = bottom;
            bottom = top;
            top = t;
        }
    }
    
    public RectF getRectF() {
        return new RectF(left, top, right, bottom);
    }
    
    public Rect getPixelRect() {
        // TODO: ���굽���ص�ת��
        return null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        
        /******************************************/
//        calLocalWithErrorRange();
//        Paint paint1 = new Paint();
//        paint1.setStrokeWidth(FRAME_WIDTH);  
//        paint1.setAntiAlias(true);   
//        paint1.setStyle(Paint.Style.STROKE); 
//        paint1.setColor(Color.RED);
//        canvas.drawRect(leftER, topER, rightER, bottomER, paint1);
//        canvas.drawRect(left + frameWidthER, top + frameWidthER, right - frameWidthER, bottom - frameWidthER, paint1);
        /******************************************/
        
        canvas.drawRect(left, top, right, bottom, framePaint);
        
  //      canvas.drawCircle(left + (right-left)/2, top, ERROR_RANGE, touchPointPaint);
        
        super.onDraw(canvas);
    }

}

