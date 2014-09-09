package com.lucas.util;

import android.content.Context;

/** Phone language */
public class PhoneLang {
    
    public enum Language {
        CN,  // 中文
        TW,  // 繁体中文
        US,   // 英文(美式)
        
        UNKNOWN
    }
    
    private static Language currLang = Language.UNKNOWN;
    
    public static Language getCurrPhoneLang(Context context) {        
        if(!currLang.equals(Language.UNKNOWN))
            return currLang;
        
        String country = context.getResources().getConfiguration().locale.getCountry();
        
        if(country.equalsIgnoreCase("CN")) 
            return Language.CN;
        
        if(country.equalsIgnoreCase("TW")) 
            return Language.TW;

        return Language.US;
    }
}
