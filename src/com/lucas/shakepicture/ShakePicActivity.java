package com.lucas.shakepicture;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.lucas.util.AndroidUtil;

public class ShakePicActivity extends Activity {

    /**
     * 
     * @param context
     * @param bitmap
     * @param rects ��Ҫҡ�ε�bitmap�����򣬿���Ϊ���
     */
    public static void start(Context context, ArrayList<RectF> rects) {
        if (BitmapReference.willShakePicBitmap == null) { 
            Log.e("", "444444444444444444444444444444");
            return;
        }

        Intent intent = new Intent(context, ShakePicActivity.class);
    //    intent.putExtra("bitmap", bitmap);
        intent.putParcelableArrayListExtra("rects", rects);
        context.startActivity(intent);
    }
    
    private VerticesView verticesView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake_pic);
        
 //       Log.e("", "ShakePicActivity onCreate()");

        Bitmap bitmap = BitmapReference.willShakePicBitmap;
        ArrayList<RectF> rects = getIntent().getExtras().getParcelableArrayList("rects");        
        
        verticesView = (VerticesView) findViewById(R.id.verticesView);
        verticesView.init(bitmap, rects, 30);     
     
        verticesView.start();
        
        // ����ΪGIF
        findViewById(R.id.save_as_gif).setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
//                List<Bitmap> list = verticesView.getBitmapsOnPath();
  //              if(list == null) {
                   // new AlertDialog.Builder().setTitle("").show();
  //                  Log.e("", "Bitmap count: " + 0);
  //                  return;
   //             }
                
   //             AndroidUtil.saveAsGif(ShakePicActivity.this, list);
                
   //             Log.e("", "Bitmap count: " + list.size());
                
//                for(int i = 0; i < list.size(); i++) {
//                    File file = Common.getFileInSdcardByName(ShakePicActivity.this, i + ".jpg", true);
//                    FileOutputStream out;
//                    try {
//                        out = new FileOutputStream(file);
//                        list.get(i).compress(Bitmap.CompressFormat.JPEG, 100, out);     
//                        out.flush();
//                        out.close();
//                    } catch (FileNotFoundException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }            
//                }
            }
        });
        
        // ����
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
         * ����ı��𶯷���
         * ����ϵ���Դ�VerticesView������Ϊԭ�㣬����Ϊx���ᣬ����Ϊy����
         */
        if((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            
            // ���ĵ㣨��ԭ�㣩������
            float originX = AndroidUtil.getScreenWidth(this) / (float) 2;
            float originY = AndroidUtil.getScreenHeight(this) / (float)2;

            // �������������Ͻǵ�����
            float x = event.getX();
            float y = event.getY();
            
            // ת��Ϊ��������ģ���ԭ�㣩������
            x -= originX;
            y = originY - y;
            
            //  ����Ļ������в���⣬�ٿ�����������������������������������������������������������������������
            double angle = Math.atan2(y, x); // [-��, ��]
            // ����angle�� [0, PI] ��
           if(angle < 0) {
                angle = Math.PI + angle;
            }
            
            verticesView.setAngle(angle);
        }

        return true;
    }    
    
    @Override
    protected void onDestroy() {
    //    quit = true;
        
        verticesView.stopShake();
        super.onDestroy();
    }
    
}


