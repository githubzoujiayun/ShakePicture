package com.lucas.shakepicture;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.lucas.shakepicture.ui.VerticesView;

public class ShakePicActivity extends Activity {

  
 //   private boolean quit = false;
    
    /**
     * 
     * @param context
     * @param bitmap
     * @param rects 需要摇晃的bitmap的区域，可以为多个
     */
    public static void start(Context context, ArrayList<RectF> rects) {
        if (WillShakePicBitmap.bitmap == null) { 
            Log.e("", "444444444444444444444444444444");
            return;
        }

        Intent intent = new Intent(context, ShakePicActivity.class);
    //    intent.putExtra("bitmap", bitmap);
        intent.putParcelableArrayListExtra("rects", rects);
        context.startActivity(intent);
    }
    
    private VerticesView view;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake_pic);

        Bitmap bitmap = WillShakePicBitmap.bitmap;
        ArrayList<RectF> rects = getIntent().getExtras().getParcelableArrayList("rects");        
        
        view = (VerticesView) findViewById(R.id.verticesView);
        view.init(bitmap, rects);        
       
   //     for(int i = 0; i < rects.size(); i++) {
   //         shakeOneRect(view, rects.get(i));
   //     }
        
     
         view.autoShake();

    }
    
//    private void shakeOneRect(final VerticesView view, final RectF r) {        
//        class Task {
//            Handler mainThreadHandler;
//            Handler ChildThreadHandler;
//        }
//        
//        final Task task = new Task();
//        
//        task.mainThreadHandler = new Handler(new Handler.Callback() {
//            
//            @Override
//            public boolean handleMessage(Message msg) {
//                view.drag((int) (r.left + r.width()/2), msg.what);     
//                task.ChildThreadHandler.sendMessage(Message.obtain());
//                return true;
//            }
//        });
//        
//        new Thread(new Runnable() {
//            private int x = (int) r.top;
//            private int step = 1;
//            
//            private void move() {
//                    x += step;
//                    Message msg = Message.obtain();
//                    msg.what = x;
//                    task.mainThreadHandler.sendMessage(msg);
//                    if(x < r.top || x > r.bottom) {
//                        step = -step;
//                    }
//            }
//            
//            @Override
//            public void run() {
//                Looper.prepare();
//                
//                task.ChildThreadHandler = new Handler(new Handler.Callback() {
//                    
//                    @Override
//                    public boolean handleMessage(Message msg) {
//                        if(quit) {
//                            Looper.myLooper().quit();
//                        } else {
//                            move();
//                        }
//                        
//                        return true;
//                    }
//                });
//                
//                move();
//
//                /*
//                 * 建立一个消息循环，该线程不会退出
//                 * 下面这一句必须放在方法的最后，因为其后面的代码不会执行了
//                 */
//                Looper.loop(); 
//            }
//        }).start();
//    }

    @Override
    protected void onDestroy() {
    //    quit = true;
        
        view.stopShake();
        super.onDestroy();
    }
    
}


