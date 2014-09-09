package com.lucas.shakepicture.pictureselector;

import java.io.IOException;
import java.io.InputStream;

import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;
import net.youmi.android.spot.SpotManager;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lucas.shakepicture.Common;
import com.lucas.shakepicture.R;
import com.lucas.util.AdHelper;
import com.lucas.util.BitmapLib;
import com.lucas.util.PhoneLang;
import com.lucas.util.BitmapLib.PicZoomOutType;
import com.lucas.util.PhoneLang.Language;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.banner.Banner;

public class GalleryActivity extends Activity {
    
//    public static void start(Context context, String[] picPathArray, int beginPos) {
//        Intent intent = new Intent(context, GalleryActivity.class);
//        intent.putExtra("picPathArray", picPathArray);
//        intent.putExtra("beginPos", beginPos);
//        context.startActivity(intent);
//    }
    
    public static void startForRequest(Activity activity, int requestCode, String[] picPathArray, int beginPos) {
        Intent intent = new Intent(activity, GalleryActivity.class);
        intent.putExtra("picPathArray", picPathArray);
        intent.putExtra("beginPos", beginPos);
        activity.startActivityForResult(intent, requestCode);
    }
    
    private int currentPage;
    private String[] picPathArray;
    
    private boolean exit = false;
    
    // startapp 插屏广告
    private StartAppAd startAppAd;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true); // 给左上角图标的左边加上一个返回的图标 。对应ActionBar.DISPLAY_HOME_AS_UP
        actionBar.setDisplayShowHomeEnabled(true);  //使左上角图标可点击，对应id为android.R.id.home，对应ActionBar.DISPLAY_SHOW_HOME
        
        startAppAd = new StartAppAd(this);
        
        // 加入广告条
        LinearLayout adLayout = (LinearLayout)findViewById(R.id.adLayout);
        adLayout.addView(AdHelper.getBanner(this, 1));

        SharedPreferences sp = getSharedPreferences(Common.SharedPreFileName, Context.MODE_PRIVATE);
        int bootCount = sp.getInt(Common.SPKeyBootCount, 0);
        /*
         * 前10次启动不展示插屏广告，原因：
         * 1. 在各个市场审核阶段，广告显示不出来（那些变态不会把应用连启10次吧？）
         * 2. 用户刚开始使用时有一个好的体验
         */
        if(bootCount > 10) { 
            // 展示插屏广告
            final SpotManager spotManager = SpotManager.getInstance(this);
            final Handler handler = new Handler(new Handler.Callback() {
                
                private Language lang = PhoneLang.getCurrPhoneLang(GalleryActivity.this);
                
                @Override
                public boolean handleMessage(Message msg) {
                    switch (lang) {
                    case CN:
                        spotManager.showSpotAds(GalleryActivity.this);
                        break;
                    case TW:
                        spotManager.showSpotAds(GalleryActivity.this);
                        break;
                    default:
                        startAppAd.showAd();
                        startAppAd.loadAd();
                        break;
                    }
                    return false;
                }
            });
    
            new Thread(new Runnable() {
    
                @Override
                public void run() {
                    while(true) {
                        try {
                            // 每30s展示一次插屏广告
                            Thread.sleep(30 * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        
                        if(exit) {
                            break;
                        }
                        
                        handler.sendMessage(new Message());
                    }
                }
            }).start();
        }
        
        picPathArray = getIntent().getExtras().getStringArray("picPathArray");
        int beginPos = getIntent().getExtras().getInt("beginPos");
        
        final TextView rateTv = (TextView) findViewById(R.id.rate);
        
        ViewPager vp = (ViewPager) findViewById(R.id.pic_pager);
        vp.setAdapter(new PagerAdapter() {

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                Context context = GalleryActivity.this;
                
                Bitmap bitmap = getBitmapByPos(position);
                
                View view = LayoutInflater.from(context).inflate(R.layout.gallery_item, null);  
                ImageView iv = (ImageView) view.findViewById(R.id.image);
                iv.setImageBitmap(bitmap);

                container.addView(view);
                return view;
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;  
            }
            
            @Override
            public int getCount() {
                return picPathArray.length;
            }
            
            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                View view = (View) object;  
                container.removeView(view);  
            }
        });
        
        vp.setOnPageChangeListener(new OnPageChangeListener() {
            
            @Override
            public void onPageSelected(int currentPage) {
                GalleryActivity.this.currentPage = currentPage;
                rateTv.setText((currentPage + 1) + "/" + picPathArray.length);  
            }
            
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) { }
            
            @Override
            public void onPageScrollStateChanged(int arg0) { }
        });
        
        currentPage = beginPos;
        rateTv.setText((beginPos + 1) + "/" + picPathArray.length);  
        vp.setCurrentItem(beginPos);
    }
    
    private Bitmap getBitmapByPos(int pos) {
        InputStream is = null;
        try {
            is = getAssets().open(picPathArray[pos]);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("", e.getMessage());
            return null;
        }

        
        Bitmap bitmap = BitmapLib.decodeBitmap(this, is, 10500, 15000, PicZoomOutType.FILL);
        
        try {
            is.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return bitmap;
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        menu.add(R.string.select_done).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        
        if(item.getTitle().equals(getResources().getString(R.string.select_done))) {
            Intent intent = getIntent();
            intent.putExtra("selectIndex",  currentPage);
            
            setResult(RESULT_OK, intent);
            finish();
        }
        
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startAppAd.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        startAppAd.onPause();
    }

    @Override
    protected void onDestroy() {
        exit = true;
        super.onDestroy();
    }

}
