package com.lucas.util;

import android.content.Context;
import net.youmi.android.AdManager;
import net.youmi.android.spot.SpotManager;

public class YouMi {

    public static final String APP_ID = "3b78fa1a56d13f02";
    public static final String APP_SECRET_KEY = "5cae3c5a7f73bb6a";
    
    public static void init(Context context) {
        // ��ʼ��Ӧ�õķ��� ID ����Կ���Լ����ò���ģʽ
        AdManager.getInstance(context).init(APP_ID, APP_SECRET_KEY, false);
        
        // ��ʼ���������ӿ�
        SpotManager.getInstance(context).loadSpotAds(); 
    }
}
