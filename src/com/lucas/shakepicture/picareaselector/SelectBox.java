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
                
    private static final int FRAME_WIDTH = 5;  // 边框线的宽度
    private static final int ERROR_RANGE = 10;   // 误差范围，因手指较粗不可能精确点击
        
    // 考虑了误差(Error Range)后的范围值
    private float leftER, topER, rightER, bottomER, frameWidthER;
    
    private Paint framePaint = new Paint();
    
 //   private Paint touchPointPaint = new Paint();    

    public SelectBox(Context context) {
        super(context);

        framePaint.setStrokeWidth(FRAME_WIDTH);
        framePaint.setAntiAlias(true);
        framePaint.setStyle(Paint.Style.STROKE);
        framePaint.setColor(0xFFB8860B); // 暗金黄色

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
     * 计算考虑了误差后的真实边界值
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
            IN, // 在框内

            ON_LEFT, // 在左边框上
            ON_TOP,// 在上边框上
            ON_RIGHT,// 在右边框上
            ON_BOTTOM,// 在下边框
            
            OUT, // 在框外
            
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

    // 判断一个点与此选择框的位置关系
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
     * 移动
     * @param distX 水平方向的移动距离，小于0表示向左移动，余下同理
     * @param distY
     */
    public void move(float distX, float distY) {        
        setLocal(left + distX, top + distY, right + distX, bottom + distY);
        invalidate();
    }
    
    /**
     * 按住左边框进行缩放
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
        // TODO: 坐标到像素的转换
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

