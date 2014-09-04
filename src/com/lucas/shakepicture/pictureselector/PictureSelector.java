package com.lucas.shakepicture.pictureselector;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;

public class PictureSelector {

    public interface OnPicSelectedListener {
        void onSelected(Bitmap bitmap);
    }
    
    // 默认的包访问权限
    static AssetManager assetManager;
    
    public static void select(Context context, OnPicSelectedListener listener) {
        assetManager = context.getAssets();
        PicWallActivity.start(context, listener);
    }
}
