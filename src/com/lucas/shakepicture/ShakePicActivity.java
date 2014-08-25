package com.lucas.shakepicture;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

public class ShakePicActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake_pic);
        
        Bitmap image = (Bitmap) getIntent().getExtras().get("image");
        
        ImageView iv = (ImageView) findViewById(R.id.image);
        iv.setImageBitmap(image);
    }

    public static void start(Context context, Bitmap image) {
        if(image == null) {
            return;
        }
        
        Intent intent = new Intent(context, ShakePicActivity.class);
        intent.putExtra("image", image);
        context.startActivity(intent);
    }
}
