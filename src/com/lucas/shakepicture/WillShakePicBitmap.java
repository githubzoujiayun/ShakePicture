package com.lucas.shakepicture;

import android.graphics.Bitmap;

// 因为Activity之间用Intent不能传递过大的Bitmap
// 所以Activity之间通过访问这个静态变量来传递Bitmap
public class WillShakePicBitmap {

    public static Bitmap bitmap;
    
}
