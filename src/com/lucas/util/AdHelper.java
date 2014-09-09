package com.lucas.util;

import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;

import com.startapp.android.publish.banner.Banner;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

/**
 * π„∏Ê÷˙ ÷
 * @author Lucas
 *
 */
public class AdHelper {

    public static View getBanner(Context context, float alpha) {
        View adView;
        
        switch (PhoneLang.getCurrPhoneLang(context)) {
        case CN:
            adView = new AdView(context, AdSize.FIT_SCREEN);
            break;
        case TW:
            adView = new AdView(context, AdSize.FIT_SCREEN);
            break;
        default:
            adView = new Banner(context); // startapp banner
            break;
        }
        
        adView.setAlpha(alpha);
        return adView;
    }
    
}
