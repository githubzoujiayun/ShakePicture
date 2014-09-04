package com.lucas.shakepicture.pictureselector;

import java.io.IOException;
import java.io.InputStream;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lucas.shakepicture.R;
import com.lucas.shakepicture.pictureselector.PictureSelector.OnPicSelectedListener;
import com.lucas.util.BitmapLib;
import com.lucas.util.BitmapLib.PicZoomOutType;

public class GalleryActivity extends Activity {

    private static OnPicSelectedListener listener;
    
    public static void start(Context context, String[] picPathArray, int beginPos, OnPicSelectedListener listener) {
        if(listener == null)
            return;
        
        GalleryActivity.listener = listener;
        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putExtra("picPathArray", picPathArray);
        intent.putExtra("beginPos", beginPos);
        context.startActivity(intent);
    }
    
    private int currentPage;
    private String[] picPathArray;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true); // 给左上角图标的左边加上一个返回的图标 。对应ActionBar.DISPLAY_HOME_AS_UP
        actionBar.setDisplayShowHomeEnabled(true);  //使左上角图标可点击，对应id为android.R.id.home，对应ActionBar.DISPLAY_SHOW_HOME
        
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
            is = PictureSelector.assetManager.open(picPathArray[pos]);
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
        menu.add("选定").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        
        if(item.getTitle() == "选定") {
            listener.onSelected(getBitmapByPos(currentPage));
            finish();
        }
        
        return super.onOptionsItemSelected(item);
    }
    
}
