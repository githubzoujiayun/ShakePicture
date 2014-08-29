package com.lucas.shakepicture;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.lucas.shakepicture.ui.SelectBox;
import com.lucas.shakepicture.ui.SelectBox.Position;
import com.lucas.util.AndroidUtil;

public class DigPicActivity extends Activity {
    
    private ImageView iv;
    private SelectBox sb; 
    private FrameLayout layout;
    
    private Bitmap bitmap;
    
    public static void start(Context context, Uri uri) {
        if(uri == null) {
            Toast.makeText(context, "000000000000000000000000", 1).show();
            Log.e("", "0000000000000000000000000000000000");
            return;
        }
        
        Intent intent = new Intent(context, DigPicActivity.class);
        intent.putExtra("uri", uri);
        context.startActivity(intent);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dig);

        WillShakePicBitmap.bitmap = bitmap = getSuitableBitmap((Uri) getIntent().getExtras().get("uri"));
        
        if(bitmap == null) {
            Toast.makeText(this, "1111111111111111111111111111", 1).show();
            Log.e("", "111111111111111111111111");
            return;
        }
        
        layout = (FrameLayout) findViewById(R.id.main_layout);
        iv = (ImageView) findViewById(R.id.image);
        iv.setImageBitmap(bitmap);
        
        sb = new SelectBox(this);
        sb.setLocal(50, 50, 200, 200);
        layout.addView(sb);
        
        layout.setOnTouchListener(listener);

        Button b = (Button) findViewById(R.id.dig);
        b.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
     //           Bitmap bitmap = AndroidUtil.drawable2Bitmap(iv.getDrawable());
                
                int[] layoutLoca = new int[2];
                layout.getLocationInWindow(layoutLoca);
                
                int[] loca = new int[2];
                iv.getLocationInWindow(loca);
        //        Log.e("", loca[0] + ", " + loca[1] + ", " + iv.getWidth() + ", " + iv.getHeight());
        //        Toast.makeText(DigActivity.this, "" + bitmap.getWidth() + ", " + bitmap.getHeight(), Toast.LENGTH_LONG).show();
                
                int ivX = loca[0] - layoutLoca[0];
                int ivY = loca[1] - layoutLoca[1];
                
                RectF ivRectF = new RectF(ivX, ivY, ivX + iv.getWidth(), ivY + iv.getHeight());
                
                RectF rectF = AndroidUtil.rectFIntersection(ivRectF, sb.getRectF());
                if(rectF == null) {
                    Toast.makeText(DigPicActivity.this, "222222222222222222222222222", 1).show();
                    Log.e("", "222222222222222222222222222");
                    return;  // 未选中图片，无交集
                }
                
                int x = (int) rectF.left;
                int y = (int) rectF.top; 
                int w = (int) (rectF.right - rectF.left);
                int h = (int) (rectF.bottom - rectF.top);
                
                // 上面的x, y, w, h是相对于中国Layout的坐标
                // 还需转为相对于ImageView的坐标
                x -= ivX;
                y -= ivY;
                
                // 参数的有效性判断
                if(w == 0 || h == 0
                        || x + w > bitmap.getWidth()
                        || y + h > bitmap.getHeight()) {
                    Toast.makeText(DigPicActivity.this, "333333333333333333333333", 1).show();
                    Log.e("", "333333333333333333333333");
                    return;
                }
                
          //      Bitmap sub = Bitmap.createBitmap(bitmap, x, y, w, h);
      //          Intent intent = new Intent(DigPicActivity.this, ShakePicActivity.class);
        //        intent.putExtra("bitmap", bitmap);
      //          startActivity(intent);
                
                ArrayList<RectF> rects = new ArrayList<RectF>();
                
                rects.add(new RectF(x, y, x + w, y + h));
                
                int newX = x + w + 20;
                rects.add(new RectF(newX, y, newX + w, y + h));
                
                ShakePicActivity.start(DigPicActivity.this, rects);
            }
        });
    }    
   
    private OnTouchListener listener = new OnTouchListener() {
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
 //           int[] loca = new int[2];
 //           iv.getLocationInWindow(loca);
 //           Log.e("", loca[0] + ", " + loca[1] + ", " + iv.getWidth() + ", " + iv.getHeight());
            
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                Position p = sb.getPosition(x, y);
                
                lastX = x;
                lastY = y;
                
                if(p.on()) {
                    Log.e("", "ZOOM");

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
                    Log.e("", "MOVE");
                    state = MOVE;
                } else {
                    Log.e("", "IDLE");
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
    
    private Bitmap getSuitableBitmap(Uri uri) {        
        FileDescriptor fd = null;
        try {
            fd = getContentResolver().openFileDescriptor(uri, "r").getFileDescriptor();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        
        Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fd);
        
        int maxW = AndroidUtil.getScreenWidth(this);
        int maxH = AndroidUtil.getScreenHeight(this);
        
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
            bitmap = Bitmap.createScaledBitmap(bitmap, (int)(bmW * ratio), (int)(bmH * ratio), false);
        }
        
        return bitmap;
    }
}
