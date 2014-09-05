package com.lucas.shakepicture.pictureselector;

import java.io.IOException;
import java.util.Arrays;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.lucas.shakepicture.R;

public class PicWallActivity extends Activity {
    
//    public static void start(Context context, OnPicSelectedListener listener) {
//        if(listener == null)
//            return;
//        
//        PicWallActivity.listener = listener;
//        
//        Intent intent = new Intent(context, PicWallActivity.class);
//        context.startActivity(intent);
//    }
    
    public static void startForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, PicWallActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }
    
    public static void startForResult(Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getActivity().getApplicationContext(), PicWallActivity.class);
        fragment.startActivityForResult(intent, requestCode);
    }
    
    private PicWallAdapter adapter;
    private String[] picPathArr;  // 保存相对于 assets 的路径
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_wall);
        
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true); // 给左上角图标的左边加上一个返回的图标 。对应ActionBar.DISPLAY_HOME_AS_UP
        actionBar.setDisplayShowHomeEnabled(true);  //使左上角图标可点击，对应id为android.R.id.home，对应ActionBar.DISPLAY_SHOW_HOME
                
        try {
            picPathArr = getAssets().list("belle");
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
                GalleryActivity.startForRequest(PicWallActivity.this, 0, picPathArr, position);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0 && resultCode == RESULT_OK) {
            int index = data.getIntExtra("selectIndex", -1);
            if(index != -1) {
                Intent intent = getIntent();
                intent.putExtra("picPath", picPathArr[index]);
                
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if(adapter != null)
            adapter.cancelAllTasks();
        super.onDestroy();
    }

}






