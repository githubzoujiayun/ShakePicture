package com.lucas.shakepicture.picareaselector;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lucas.shakepicture.BitmapReference;
import com.lucas.shakepicture.picareaselector.PicAreaSelect.OnSelectDoneListener;
import com.lucas.shakepicture.picareaselector.RectSelectableImageView.AddAreaResult;
import com.lucas.shakepicture.picareaselector.SelectBox.Position;
import com.lucas.shakepicture_for_google_play.R;
import com.lucas.util.AdHelper;
import com.lucas.util.AndroidUtil;
import com.lucas.util.PhoneLang;
import com.lucas.util.PhoneLang.Language;

public class DigActivity extends Activity {
    
    private RectSelectableImageView iv;
    private SelectBox sb; 
    private FrameLayout layout;
        
    // 下面两个是存储返回结果的 
    private static Bitmap bitmap;       
    private static OnSelectDoneListener listener;
    
    public static void start(Context context, Uri bitmapUri, OnSelectDoneListener listener) {        
        DigActivity.listener = listener;
        
        Intent intent = new Intent(context, DigActivity.class);
        intent.putExtra("bitmapUri", bitmapUri);
        context.startActivity(intent);
    }
    
    public static void start(Context context, OnSelectDoneListener listener) {        
        DigActivity.listener = listener;
        
        Intent intent = new Intent(context, DigActivity.class);
        context.startActivity(intent);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.activity_dig); 
        
        // 设置悬浮广告条
        
        Language currLang = PhoneLang.getCurrPhoneLang(this);
        if(currLang == Language.CN || currLang == Language.TW) {
            FrameLayout.LayoutParams parms = new FrameLayout.LayoutParams(1, 1);
            parms.gravity = Gravity.LEFT; 
            View adView = AdHelper.getBanner(this, 1, Language.US);
            adView.setFocusable(false);
            adView.setClickable(false);
            addContentView(adView, parms);
        }
        
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams( 
                                    FrameLayout.LayoutParams.WRAP_CONTENT,
                                    FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL; 
        
        addContentView(AdHelper.getBanner(this, 1), layoutParams);
        
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            Uri bitmapUri = (Uri) bundle.get("bitmapUri");
            if (bitmapUri != null) {
                FileDescriptor fd = null;
                try {
                    fd = getContentResolver().openFileDescriptor(bitmapUri, "r").getFileDescriptor();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    // ///////////////////////////////////////////////////////////////////////
                    return;
                }

                try {
                    bitmap = bitmapAdaptScreen(BitmapFactory .decodeFileDescriptor(fd));
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }
                
                if (bitmap == null) {
                    Toast.makeText(this, getResources().getString(R.string.oom), Toast.LENGTH_LONG).show();
                    return;
                }
            } else {
                bitmap = BitmapReference.appBuildInSelectedBitmap;
            }
        } else {
            bitmap = BitmapReference.appBuildInSelectedBitmap;
        }

        layout = (FrameLayout) findViewById(R.id.main_layout);
        iv = (RectSelectableImageView) findViewById(R.id.image);
                
        iv.setImageBitmap(bitmap);        
        iv.setOnTouchListener(onImageViewTouchListener);
        
        sb = new SelectBox(this);
        
        // 显示在屏幕正中间
        int screenW = AndroidUtil.getScreenWidth(this);
        int screenH = AndroidUtil.getScreenHeight(this);
        int sbW = 250;
        int sbH = 150;
        int left = (screenW - sbW) / 2;
        int top = (screenH - sbH) / 2;
        sb.setLocal(left, top, left + sbW, top + sbH);
        layout.addView(sb);
        
        layout.setOnTouchListener(onLayoutTouchListener);
        
        TextView back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        TextView add = (TextView) findViewById(R.id.add);
        add.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                AddAreaResult result = iv.addSelectedRect(calSelectRect());
                if(result == AddAreaResult.FAIL_SAME_EXIST) {
                    Toast.makeText(DigActivity.this, R.string.area_exist, Toast.LENGTH_SHORT).show();
                } else if (result == AddAreaResult.FAIL_INTERSECTION_EXIST) {
                    Toast.makeText(DigActivity.this, R.string.area_not_coincide, Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView over = (TextView) findViewById(R.id.over);
        over.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) { 
                if(listener.onSelectDone(bitmap, iv.getAllSelectedRect())) 
                    finish();
            }
        });
    }    
    
    private Bitmap bitmapAdaptScreen(Bitmap bitmap) {
        if(bitmap == null)
            return null;
        
        // 因为此activity是全屏的，所以只需使用屏幕即可
        int maxW = AndroidUtil.getScreenWidth(this);   /////////////////////////  有问题  /
        int maxH = AndroidUtil.getScreenHeight(this); /////////////////////////   有问题 /
        
        int bmW = bitmap.getWidth();
        int bmH = bitmap.getHeight();
        
        float ratio = 1;
        if(bmW > maxW) {
            ratio = maxW/(float)bmW;
        }
        if(bmH > maxH) {
            ratio = Math.min(ratio, maxH/(float)bmH);
        }
        
        if(ratio < 1) {
            try {
                bitmap = Bitmap.createScaledBitmap(bitmap, (int)(bmW * ratio), (int)(bmH * ratio), false);
            } catch(OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        
        return bitmap;
    }

    private RectF calSelectRect() {
        int[] layoutLoca = new int[2];
        layout.getLocationInWindow(layoutLoca);
        
        int[] imageViewLoca = new int[2];
        iv.getLocationInWindow(imageViewLoca);
        
        // ImageView相对于FrameLayout的x, y
        int ivX = imageViewLoca[0] - layoutLoca[0];
        int ivY = imageViewLoca[1] - layoutLoca[1];
        
        RectF ivRectF = new RectF(ivX, ivY, ivX + iv.getWidth(), ivY + iv.getHeight());

        RectF rectF = new RectF(ivRectF); 
        /*
         * If the specified rectangle intersects this rectangle, return true and
         * set this rectangle to that intersection, otherwise return false and
         * do not change this rectangle
         */
        if (!rectF.intersect(sb.getRectF())) {
            return null; // 未选中图片，无交集
        }
        
        // 上面的均是相对于Layout的坐标
        // 还需转为相对于ImageView的坐标        
        float left = rectF.left - ivX; // 得到相对于ImageView的坐标
        float top = rectF.top - ivY;
        float right = rectF.width() + left;
        float bottom = rectF.height() + top;        
        
        return new RectF(left, top, right, bottom);
    }
    
    private OnTouchListener onImageViewTouchListener = new OnTouchListener() {

        private long lastClickTime = 0; // millisecond

        @Override
        public boolean onTouch(View v, MotionEvent e) {
            if ((e.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
                long time = new Date().getTime();

                if (lastClickTime == 0) {
                    lastClickTime = time;
                    return false;
                }

                if (time - lastClickTime < 500) { // 两次点击间隔小于500ms认为是双击
                    // 双击删除已有的区域
                    iv.deleteRectByPoint((int) e.getX(), (int) e.getY());
                    lastClickTime = 0;
                } else {
                    lastClickTime = time;
                }
            }
            
            return false;
        }
    };
   
    private OnTouchListener onLayoutTouchListener = new OnTouchListener() {
        private static final int IDLE = 1;   // 空闲
        private static final int MOVE = 2;   // 正在移动选择框
        private static final int ZOOM_LEFT = 3;   // 按住左边框缩放
        private static final int ZOOM_RIGHT = 4;   // 按住右边框缩放
        private static final int ZOOM_TOP = 5;   // 按住上边框缩放
        private static final int ZOOM_BOTTOM = 6;   // 按住下边框缩放
        
        private int state = IDLE;
        
        private float lastX, lastY;
        
        @Override
        public boolean onTouch(View v, MotionEvent event) {            
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                Position p = sb.getPosition(x, y);
                
                lastX = x;
                lastY = y;
                
                if(p.on()) {
                    Position.Dir dir = p.onWhich();
                    
                    if(dir == Position.Dir.ON_LEFT) {
                        state = ZOOM_LEFT;
                    } else if(dir == Position.Dir.ON_RIGHT) {
                        state = ZOOM_RIGHT;
                    }else if(dir == Position.Dir.ON_TOP) {
                        state = ZOOM_TOP;
                    }else if(dir == Position.Dir.ON_BOTTOM) {
                        state = ZOOM_BOTTOM;
                    } else {
                        state = IDLE;
                    }
                } else if(p.in()) {
                    state = MOVE;
                } else {
                    state = IDLE;
                }    
                break;
            case MotionEvent.ACTION_MOVE:
                if(state == ZOOM_LEFT) {
                    sb.zoomLeft(x - lastX);
                } else if(state == ZOOM_RIGHT) {
                    sb.zoomRight(x - lastX);
                } else if(state == ZOOM_TOP) {
                    sb.zoomTop(y - lastY);
                } else if(state == ZOOM_BOTTOM) {
                    sb.zoomBottom(y - lastY);
                } else if(state == MOVE) {
                    sb.move(x - lastX, y - lastY);
                }
                
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_UP:
                // 因为都选择框缩放的过程中，有可能交换了左右或上下边界，一次缩放完成后需要需要调整一下。
                sb.balance();
            default:
                state = IDLE;
                break;
            }
            
            return true;        
        }
    }; 
}
