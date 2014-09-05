package com.lucas.shakepicture.picareaselector;

import java.util.Set;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;

/**
 * 供外部使用
 * @author Lucas
 *
 */

public class PicAreaSelect {
    
    public interface OnSelectDoneListener {
        /**
         * 
         * @param bitmap
         * @param rectSet
         * @return true: destroy this activity, else not
         */
        boolean onSelectDone(Bitmap bitmap, Set<RectF> rectSet);
    }
                            
    public static void startSelect(Context context, Uri bitmapUri, OnSelectDoneListener listener) {
        DigActivity.start(context, bitmapUri, listener);
    }
    
    public static void startSelect(Context context, OnSelectDoneListener listener) {
        DigActivity.start(context, listener);
    }
}
