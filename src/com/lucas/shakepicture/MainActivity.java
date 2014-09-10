package com.lucas.shakepicture;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import net.youmi.android.diy.DiyManager;
import net.youmi.android.spot.SpotManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lucas.shakepicture.picareaselector.PicAreaSelect;
import com.lucas.shakepicture.picareaselector.PicAreaSelect.OnSelectDoneListener;
import com.lucas.shakepicture.pictureselector.PicWallActivity;
import com.lucas.util.AndroidUtil;
import com.lucas.util.BitmapLib;
import com.lucas.util.BitmapLib.PicZoomOutType;
import com.lucas.util.AdHelper;
import com.lucas.util.PhoneLang;
import com.lucas.util.StartApp;
import com.lucas.util.YouMi;
import com.startapp.android.publish.Ad;
import com.startapp.android.publish.AdEventListener;
import com.startapp.android.publish.nativead.NativeAdDetails;
import com.startapp.android.publish.nativead.NativeAdPreferences;
import com.startapp.android.publish.nativead.NativeAdPreferences.NativeAdBitmapSize;
import com.startapp.android.publish.nativead.StartAppNativeAd;
import com.umeng.update.UmengUpdateAgent;

public class MainActivity extends Activity {
    
    private TextView localPicTv;
    private TextView appBuildInPicTv;
    private TextView photographTv;
    
    private static final int SELECT_PIC = 0; // 选择本地图片
    private static final int TAKE_PIC = 1; // 拍照
    private static final int SELECT_BUILD_IN_PIC = 2; // 选择应用自带图片
    
    private Uri uriTakePic;         // 拍照时，拍照后图片的Uri
    
    private WakeLock wakeLock;
    
    private ProgressDialog progressDialog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        YouMi.init(this);       
        StartApp.init(this);
        
        setContentView(R.layout.activity_main);
        
//        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024 / 1024);  
//        int w = AndroidUtil.getScreenWidth(this);
//        int h = AndroidUtil.getScreenHeight(this);
//        Toast.makeText(this, "" + maxMemory + " MB" + ", [" + w + ", " + h + "]", 1).show();

        SharedPreferences sp = getSharedPreferences(Common.SharedPreFileName, Context.MODE_PRIVATE);
        int bootCount = sp.getInt(Common.SPKeyBootCount, 0);
        
        Editor editor = sp.edit();
        editor.putInt(Common.SPKeyBootCount, ++bootCount);
        editor.commit();
        
        if(bootCount == 1) {
            /*
             *  首次启动，创建一下桌面快捷方式
             *  以后将不再做检查
             */
            addShortCut();
        }
        
        // 检查更新（使用友盟的接口）
        UmengUpdateAgent.setDeltaUpdate(false); // true增量更新，设为false则为全量更新
        UmengUpdateAgent.update(this);
                
        // 保持屏幕不灭
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);  
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");  
        wakeLock.acquire();  
        
        // 启动标题抖动效果
        TextView tv = (TextView) findViewById(R.id.title);
        Animation animation =  AnimationUtils.loadAnimation(this, R.anim.text_rotate);
        tv.startAnimation(animation);        
        
        // 启动小人动画
        ImageView iv = (ImageView) findViewById(R.id.shake_people);
        Animatable animatable = (Animatable) iv.getBackground();
        animatable.start();
        
        localPicTv = (TextView) findViewById(R.id.local_pic);
        appBuildInPicTv = (TextView) findViewById(R.id.app_built_in_pic);
        photographTv = (TextView) findViewById(R.id.photograph);
                
        // 本地图片
        localPicTv.setOnClickListener(new OnClickListener() {
            
            public void onClick(View v) {
             // 激活系统图库，选择一张图片
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_PIC);
            }
        });
        
        // app自带图片
        appBuildInPicTv.setOnClickListener(new OnClickListener() {
            
            public void onClick(View v) {
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage(MainActivity.this.getResources().getString(R.string.loading));
                progressDialog.setCancelable(false);
                progressDialog.show();
                
                PicWallActivity.startForResult(MainActivity.this, SELECT_BUILD_IN_PIC);
            }
        });
        
        // 拍照
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
        
        // 推荐墙广告
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
        
        if (requestCode == TAKE_PIC) { // 拍照
            PicAreaSelect.startSelect(this, uriTakePic, onSelectDonwListener);
        } else if (requestCode == SELECT_PIC && data != null) { // 选择一张本地图片
            // 得到图片的全路径
            Uri uri = data.getData();
            if(uri != null) {
                PicAreaSelect.startSelect(this, uri, onSelectDonwListener);
            }
        } else if(requestCode == SELECT_BUILD_IN_PIC && data != null) {
            String picPath = data.getExtras().getString("picPath");
            InputStream is = null;
            try {
                is = getAssets().open(picPath);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return;
            }
            BitmapReference.appBuildInSelectedBitmap = BitmapLib.decodeBitmap(
                                                    this, is, 100000, 100000, PicZoomOutType.ZOOM_OUT);
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
        // 注销有米插屏广告广播
        SpotManager.getInstance(this) .unregisterSceenReceiver();
        
        // 熄灭屏幕
        wakeLock.release(); 
        
        super.onDestroy();
    }

    private long exitTime = 0;

    // 再按一次退出程序的实现
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
    
    /**
     * 此函数无法正常工作
     * @return
     */
    private boolean isAddShortCut() {
        final ContentResolver resolver = this.getContentResolver();

        String AUTHORITY = "com.android.launcher2.settings";

        final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");
        Cursor c = resolver.query(CONTENT_URI,
                        new String[] { "title", "iconResource" }, "title=?",
                        new String[] { getString(R.string.app_name) }, null);

        if (c != null && c.getCount() > 0)
            return true;
        
        return false;
    }
    
    private void addShortCut(){        
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");

        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, getResources().getString(R.string.app_name));
 
        // 是否允许重复创建
        shortcut.putExtra("duplicate", false);
        
        //设置桌面快捷方式的图标
        Parcelable icon = Intent.ShortcutIconResource.fromContext(this, R.drawable.icon);        
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        
        //点击快捷方式的操作
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClass(this, MainActivity.class);
        
        // 设置启动程序
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        
        //广播通知桌面去创建
        this.sendBroadcast(shortcut);
    }

}
