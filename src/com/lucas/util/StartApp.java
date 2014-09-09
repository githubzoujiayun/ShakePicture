package com.lucas.util;

import com.startapp.android.publish.StartAppSDK;

import android.content.Context;

public class StartApp {
    public static final String DEVELOPER_ID = "109403462";
    public static final String APP_KEY = "209478027";
    
    public static void init(Context context) {
        /*
         * The last true parameter enables the "Return Ads" feature
         *  as explained in the next section. 
         *  If you want to disable this feature, simply pass false instead.
         */
        StartAppSDK.init(context, DEVELOPER_ID, APP_KEY, true);
    }
}
