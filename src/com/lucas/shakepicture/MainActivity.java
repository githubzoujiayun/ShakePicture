package com.lucas.shakepicture;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;

import net.youmi.android.AdManager;
import net.youmi.android.diy.DiyManager;
import net.youmi.android.spot.SpotManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lucas.shakepicture.picareaselector.PicAreaSelect;
import com.lucas.shakepicture.picareaselector.PicAreaSelect.OnSelectDoneListener;
import com.lucas.shakepicture.pictureselector.PicWallActivity;
import com.lucas.util.AndroidUtil;
import com.lucas.util.BitmapLib;
import com.lucas.util.BitmapLib.PicZoomOutType;
import com.lucas.util.PhoneLang;
import com.lucas.util.StartApp;
import com.lucas.util.YouMi;
import com.startapp.android.publish.StartAppAd;
import com.umeng.update.UmengUpdateAgent;

public class MainActivity extends Activity {
    
    private TextView localPicTv;
    private TextView appBuildInPicTv;
    private TextView photographTv;
    
    private static final int SELECT_PIC = 0; // ѡ�񱾵�ͼƬ
    private static final int TAKE_PIC = 1; // ����
    private static final int SELECT_BUILD_IN_PIC = 2; // ѡ��Ӧ���Դ�ͼƬ
    
    private Uri uriTakePic;         // ����ʱ�����պ�ͼƬ��Uri
    
    private WakeLock wakeLock;
    
    private ProgressDialog progressDialog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
//        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024 / 1024);  
//        int w = AndroidUtil.getScreenWidth(this);
//        int h = AndroidUtil.getScreenHeight(this);
//        Toast.makeText(this, "" + maxMemory + " MB" + ", [" + w + ", " + h + "]", 1).show();
        
        // �����û�����ͳ�Ʒ���,Ĭ�ϲ����������� false ֵҲ��������ֻ�д��� true �Ż����
//        AdManager.getInstance(this).setUserDataCollect(true);

        SharedPreferences sp = getSharedPreferences(Common.SharedPreFileName, Context.MODE_PRIVATE);
        int bootCount = sp.getInt(Common.SPKeyBootCount, 0);
        
        Editor editor = sp.edit();
        editor.putInt(Common.SPKeyBootCount, bootCount);
        editor.commit();
        
//        if(bootCount < 10) { 
//            /*
//             * ��Ϊ�ڵ�Ӧ���г�˵��Ӧ�����
//             * ����ǰ10������������ʾ��Ӧ������ͼƬ��ť��
//             * ע�⣺�������Google play�г�����ȡ�������ƣ�
//             */
//            findViewById(R.id.app_built_in_pic).setVisibility(View.GONE);
//        }
        
        // �����£�ʹ�����˵Ľӿڣ�
        UmengUpdateAgent.setDeltaUpdate(false); // true�������£���Ϊfalse��Ϊȫ������
        UmengUpdateAgent.update(this);
                
        // ������Ļ����
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);  
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");  
        wakeLock.acquire();  
        
        // �������ⶶ��Ч��
        TextView tv = (TextView) findViewById(R.id.title);
        Animation animation =  AnimationUtils.loadAnimation(this, R.anim.text_rotate);
        tv.startAnimation(animation);        
        
        // ����С�˶���
        ImageView iv = (ImageView) findViewById(R.id.shake_people);
        Animatable animatable = (Animatable) iv.getBackground();
        animatable.start();
        
        localPicTv = (TextView) findViewById(R.id.local_pic);
        appBuildInPicTv = (TextView) findViewById(R.id.app_built_in_pic);
        photographTv = (TextView) findViewById(R.id.photograph);
                
        // ����ͼƬ
        localPicTv.setOnClickListener(new OnClickListener() {
            
            public void onClick(View v) {
             // ����ϵͳͼ�⣬ѡ��һ��ͼƬ
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_PIC);
            }
        });
        
        // app�Դ�ͼƬ
        appBuildInPicTv.setOnClickListener(new OnClickListener() {
            
            public void onClick(View v) {
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage(MainActivity.this.getResources().getString(R.string.loading));
                progressDialog.setCancelable(false);
                progressDialog.show();
                
                PicWallActivity.startForResult(MainActivity.this, SELECT_BUILD_IN_PIC);
            }
        });
        
        // ����
        photographTv.setOnClickListener(new OnClickListener() {
            
            public void onClick(View v) {
                File f = Common.getFileInSdcardByName(MainActivity.this, "camera_raw.jpg", true);
                if(f == null) {
                    Toast.makeText(MainActivity.this, 
                            MainActivity.this.getResources().getString(R.string.not_find_sd_card), 
                            Toast.LENGTH_LONG).show();
                    return;
                }
                
                uriTakePic = Uri.fromFile(f);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriTakePic);
                startActivityForResult(intent, TAKE_PIC);
            }
        });
        
        // �Ƽ�ǽ���
        View appRecommend = findViewById(R.id.app_recommend);
        switch (PhoneLang.getCurrPhoneLang(this)) {
        case CN:
        case TW:
            break;

        default:
            appRecommend.setVisibility(View.INVISIBLE);
            break;
        }
        
        appRecommend.setOnClickListener(
            new OnClickListener() {

                @Override
                public void onClick(View v) {
                    DiyManager.showRecommendWall(MainActivity.this);
                }
       });
    }
    
    private OnSelectDoneListener onSelectDonwListener = new OnSelectDoneListener() {

        @Override
        public boolean onSelectDone(Bitmap bitmap, Set<RectF> rectSet) {
            if(rectSet.size() == 0) {
                Toast.makeText(MainActivity.this, R.string.select_shake_area, Toast.LENGTH_SHORT).show();
                return false;
            }
            
            BitmapReference.willShakePicBitmap = bitmap;

            ArrayList<RectF> list = new ArrayList<RectF>();
            list.addAll(rectSet);

            ShakePicActivity.start(MainActivity.this, list);
            return false;
        }
        
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK) {
            return;
        }
        
        if (requestCode == TAKE_PIC) { // ����
            PicAreaSelect.startSelect(this, uriTakePic, onSelectDonwListener);
        } else if (requestCode == SELECT_PIC && data != null) { // ѡ��һ�ű���ͼƬ
            // �õ�ͼƬ��ȫ·��
            Uri uri = data.getData();
            if(uri != null) {
                PicAreaSelect.startSelect(this, uri, onSelectDonwListener);
            }
        } else if(requestCode == SELECT_BUILD_IN_PIC && data != null) {  // ѡ��һ��Ӧ���Դ�ͼƬ
            String picPath = data.getExtras().getString("picPath");
//            InputStream is = null;
//            try {
//                is = getAssets().open(picPath);
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//                return;
//            }
       //     BitmapReference.appBuildInSelectedBitmap = BitmapLib.decodeBitmap(
      //                                              this, is, 100000, 100000, PicZoomOutType.ZOOM_OUT);
            
            BitmapReference.appBuildInSelectedBitmap = BitmapLib.decodeBitmap(
                                this, picPath, 100000, 100000, PicZoomOutType.ZOOM_OUT);
            PicAreaSelect.startSelect(this, onSelectDonwListener);
        }
        
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    @Override
    protected void onStop() {
        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // ע�����ײ������㲥
        SpotManager.getInstance(this) .unregisterSceenReceiver();
        
        // Ϩ����Ļ
        wakeLock.release(); 
        
        super.onDestroy();
    }

    private long exitTime = 0;

    /**
     * �ٰ�һ���˳������ʵ��
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), R.string.click_again_exit, Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
