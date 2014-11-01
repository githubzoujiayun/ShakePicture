package com.lucas.util;

import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;
import android.content.Context;
import android.view.View;

import com.lucas.util.PhoneLang.Language;

/**
 * π„∏Ê÷˙ ÷
 * 
 * @author Lucas
 * 
 */
public class AdHelper {

    private static String TAG = "AdHelper";

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
            // adView = new Banner(context); // startapp banner
            adView = new AdView(context, AdSize.FIT_SCREEN);
            break;
        }

        adView.setAlpha(alpha);
        return adView;
    }

    public static View getBanner(Context context, float alpha, Language lang) {
        View adView;

        switch (lang) {
        case CN:
            adView = new AdView(context, AdSize.FIT_SCREEN);
            break;
        case TW:
            adView = new AdView(context, AdSize.FIT_SCREEN);
            break;
        default:
            // adView = new Banner(context); // startapp banner
            adView = new AdView(context, AdSize.FIT_SCREEN);
            break;
        }

        adView.setAlpha(alpha);
        return adView;
    }

}
