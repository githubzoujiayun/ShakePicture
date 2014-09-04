package com.lucas.shakepicture.pictureselector;

import java.io.IOException;
import java.util.Arrays;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.lucas.shakepicture.R;
import com.lucas.shakepicture.pictureselector.PictureSelector.OnPicSelectedListener;

public class PicWallActivity extends Activity {

    private static OnPicSelectedListener listener;
    
    public static void start(Context context, OnPicSelectedListener listener) {
        if(listener == null)
            return;
        
        PicWallActivity.listener = listener;
        
        Intent intent = new Intent(context, PicWallActivity.class);
        context.startActivity(intent);
    }
    
    private  PicWallAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_wall);
        
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true); // 给左上角图标的左边加上一个返回的图标 。对应ActionBar.DISPLAY_HOME_AS_UP
        actionBar.setDisplayShowHomeEnabled(true);  //使左上角图标可点击，对应id为android.R.id.home，对应ActionBar.DISPLAY_SHOW_HOME
        
        final String[] picPathArr;
        try {
            picPathArr = PictureSelector.assetManager.list("belle");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("", e.getMessage());
            return ;
        }
        for(int i = 0; i < picPathArr.length; i++) {
            picPathArr[i] = "belle/" + picPathArr[i];
        }

        GridView picWall = (GridView) findViewById(R.id.pic_wall);        
        
        adapter = new PicWallAdapter(this, R.layout.pic_wall_item, Arrays.asList(picPathArr));        
        picWall.setAdapter(adapter);
        
        picWall.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GalleryActivity.start(PicWallActivity.this, picPathArr, position, PicWallActivity.listener);
                finish();
            }
        });
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onDestroy() {
        if(adapter != null)
            adapter.cancelAllTasks();
        super.onDestroy();
    }

}






