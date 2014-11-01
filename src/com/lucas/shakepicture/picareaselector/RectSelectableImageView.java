package com.lucas.shakepicture.picareaselector;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.lucas.shakepicture_for_google_play.R;

public class RectSelectableImageView extends ImageView {
    
    private Set<RectF> selectedRects = new HashSet<RectF>();

    private Context context;
    public RectSelectableImageView(Context context) {
        super(context);
        this.context = context;
        init();
    }
    
    public RectSelectableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public RectSelectableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }
    
    private Paint rectPaint = new Paint();  
    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    
    private String doubleClickDelete;
    float textLen;
    float textHeight;
    
    private void init() {
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setColor(Color.BLUE);
        rectPaint.setStrokeWidth(3);
        PathEffect effects = new DashPathEffect(new float[] { 5, 5, 5, 5 }, 1);
        rectPaint.setPathEffect(effects);

        doubleClickDelete = context.getResources().getString(R.string.double_click_delete);

        textPaint.setTextSize(30);
        textPaint.setColor(Color.RED);
        textLen = textPaint.measureText(doubleClickDelete);
        FontMetrics  fm = textPaint.getFontMetrics();
        textHeight = fm.ascent + fm.descent;
    }
    
    public void clean() {
        selectedRects.clear();
    }
 
    public enum AddAreaResult {
        SUCCESS,                 // 成功
        FAIL_ILLEGAL_ARGU,       // 失败，参数不合法
        FAIL_INTERSECTION_EXIST, // 失败，与已有的区域相交
        FAIL_SAME_EXIST          // 失败，已经存在相同的区域
    }
    
    /**
     * 
     * @param rectF
     * @return true if this selectAreas is modified, false otherwise.
     */
    public AddAreaResult addSelectedRect(RectF rectF) {
        if(rectF == null)
            return AddAreaResult.FAIL_ILLEGAL_ARGU;
        
        Iterator<RectF> iter = selectedRects.iterator();
        while (iter.hasNext()) {       
            RectF currRectF = iter.next();
            
            if(currRectF.equals(rectF)) {
                return AddAreaResult.FAIL_SAME_EXIST;
            }
                        
            if(RectF.intersects(currRectF, rectF)) { // 两个矩形相交相交
                return AddAreaResult.FAIL_INTERSECTION_EXIST;
            }
        }
        
        boolean b =  selectedRects.add(new RectF(rectF));
        if(b) {
            invalidate();
            return AddAreaResult.SUCCESS;
        }
        
        return AddAreaResult.FAIL_SAME_EXIST;
    }
    
    /**
     * 
     * @param rectF
     * @return true if this selectAreas is modified, false otherwise.
     */
    public boolean deleteRect(RectF rectF) {
        if(rectF == null)
            return false;
        
        boolean b = selectedRects.remove(rectF);
        if(b) {
            invalidate();
        }
        
        return b;
    }
    
    public boolean deleteRectByPoint(float x, float y) {
        Iterator<RectF> iter = selectedRects.iterator();
        while (iter.hasNext()) {
            RectF rectF = iter.next();  
            if(rectF.contains(x, y)) 
                return deleteRect(rectF);
        }
        
        return false;
    }
    
    public Set<RectF> getAllSelectedRect() {
        return selectedRects;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);        
        
        canvas.save();

        Iterator<RectF> iter = selectedRects.iterator();
        while (iter.hasNext()) {
            RectF rectF = iter.next();            
            canvas.drawRect(rectF, rectPaint);
            
            float textLeft = rectF.left + (rectF.width() - textLen ) / 2;
            float textTop = rectF.top + (rectF.height() - textHeight ) / 2;
            canvas.drawText(doubleClickDelete, textLeft, textTop, textPaint);
        }
        
        canvas.restore();
    }
    
}
