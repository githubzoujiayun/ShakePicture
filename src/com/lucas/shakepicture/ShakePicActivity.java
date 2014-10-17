package com.lucas.shakepicture;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

import com.lucas.util.AdHelper;
import com.lucas.util.AndroidUtil;
import com.lucas.util.PhoneLang;
import com.lucas.util.PhoneLang.Language;

public class ShakePicActivity extends Activity {

    /**
     * 
     * @param context
     * @param bitmap
     * @param rects 需要摇晃的bitmap的区域，可以为多个
     */
    public static void start(Context context, ArrayList<RectF> rects) {
        if (BitmapReference.willShakePicBitmap == null) { 
            return;
        }

        Intent intent = new Intent(context, ShakePicActivity.class);
        intent.putParcelableArrayListExtra("rects", rects);
        context.startActivity(intent);
    }
    
    private VerticesView verticesView;    
    private SensorManager sensorManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake_pic);
        
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
 
        addContentView(AdHelper.getBanner(this, (float) 0.75), layoutParams);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // 加速度传感器
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        Bitmap bitmap = BitmapReference.willShakePicBitmap;
        ArrayList<RectF> rects = getIntent().getExtras().getParcelableArrayList("rects");        
        
        verticesView = (VerticesView) findViewById(R.id.verticesView);
        verticesView.init(bitmap, rects);     
     
        verticesView.start();
        
        // 保存为GIF
        findViewById(R.id.save_as_gif).setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                List<Bitmap> list = verticesView.getBitmapsOnPath();
                if(list == null) {
                    return;
                }
                                
                for(int i = 0; i < list.size(); i++) {
                    File file = Common.getFileInSdcardByName(ShakePicActivity.this, i + ".jpg", true);
                    FileOutputStream out;
                    try {
                        out = new FileOutputStream(file);
                        list.get(i).compress(Bitmap.CompressFormat.JPEG, 100, out);     
                        out.flush();
                        out.close();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }            
                }
            }
        });
        
        // 返回
        findViewById(R.id.back).setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /*
         * 点击改变震动方向
         * 坐标系：以此VerticesView的中心为原点，向右为x正轴，向上为y正轴
         */
        if((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            
            // 中心点（即原点）的坐标
            float originX = AndroidUtil.getScreenWidth(this) / (float) 2;
            float originY = AndroidUtil.getScreenHeight(this) / (float)2;

            // 点击点相对于左上角的坐标
            float x = event.getX();
            float y = event.getY();
            
            // 转换为相对于中心（即原点）的坐标
            x -= originX;
            y = originY - y;
            
            double angle = Math.atan2(y, x); // [-π, π]
            // 控制angle在 [0, PI] 内
           if(angle < 0) {
                angle = Math.PI + angle;
            }
            
            verticesView.setAngle(angle);
        }

        return true;
    }    
    
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        /*
         * 加速度的阈值，超过此值认为晃动了手机
         * 因为重力的存在，手机不动就有9.8m/s2的加速度，所以此值要大于9.8
         */
        private static final int THRESHOLD = 15; 
        
        private long lastShakeTime = 0;
        
        @Override
        public void onSensorChanged(SensorEvent event) {            
            float x = Math.abs(event.values[0]);
            float y = Math.abs(event.values[1]);
            float z = Math.abs(event.values[2]);
            
            if(x > THRESHOLD || y > THRESHOLD || z > THRESHOLD) {
                long currTime = new Date().getTime();
                if(currTime - lastShakeTime < 200) {
                    // 距上次晃动时间不足200ms，认为是一次晃动
                    return;
                }

                verticesView.speedUp();
                
                lastShakeTime = currTime;
            }
        }
        
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) { }
    };
    
    @Override
    protected void onDestroy() {
    //    quit = true;
        sensorManager.unregisterListener(sensorEventListener);
        verticesView.stopShake();
        super.onDestroy();
    }
    
}


