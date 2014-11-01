package com.lucas.shakepicture;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.youmi.android.AdManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

import com.lucas.shakepicture_for_google_play.R;
import com.lucas.util.AndroidUtil;
import com.lucas.util.StartApp;
import com.lucas.util.YouMi;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        YouMi.init(this);       
        StartApp.init(this);
        
//        StartAppAd.showSplash(this, savedInstanceState, 
//                new SplashConfig()
//                       .setTheme(SplashConfig.Theme.USER_DEFINED)
//                       .setCustomScreen(R.layout.activity_splash)
//           );
        
        setContentView(R.layout.activity_splash);
        
        final Handler handler = new Handler(new Handler.Callback() {
            
            @Override
            public boolean handleMessage(Message msg) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                SplashActivity.this.startActivity(intent);
                finish();
                return true;
            }
        });
        
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                try {
                    // �����û�����ͳ�Ʒ���,Ĭ�ϲ����������� false ֵҲ��������ֻ�д��� true �Ż����
                    AdManager.getInstance(SplashActivity.this).setUserDataCollect(true);
                    
                    SharedPreferences sp = getSharedPreferences(Common.SharedPreFileName, Context.MODE_PRIVATE);
                    int lastBootVersionCode = sp.getInt(Common.SPKeyBootVersionCode, -1);
                    int bootCount = sp.getInt(Common.SPKeyBootCount, 0);
                    
                    Editor editor = sp.edit();
                    editor.putInt(Common.SPKeyBootCount, ++bootCount);
                    editor.commit();
                    
                    if(bootCount == 1) {
                        /*
                         *  �״�����������һ�������ݷ�ʽ
                         *  �Ժ󽫲��������
                         */
                        AndroidUtil.addShortCut(SplashActivity.this, SplashActivity.class);
                    }
                    
                    //��ʹ��asset�е��ļ�֮ǰ��Ҫ���俽�����ֻ����ڲ��洢ϵͳ�/data/data/ ����/filesĿ¼�£�
                    File path = new File(getFilesDir().getPath() + "/belle");
                    if(!path.exists()) { // �ļ��в����ڣ��追��
                        path.mkdir();
                        copyBellePicsToPhone(path);
                    } else {
                        if(path.list().length == 0) { // �ļ���Ϊ�գ��追��
                            copyBellePicsToPhone(path);
                        } else {
                            int currVersionCode;
                            try {
                                currVersionCode = AndroidUtil.getVersionCode(SplashActivity.this);
                                if(lastBootVersionCode != currVersionCode) {
                                    copyBellePicsToPhone(path);
                                    
                                    Editor e = sp.edit();
                                    e.putInt(Common.SPKeyBootVersionCode, currVersionCode);
                                    e.commit();
                                }
                            } catch (NameNotFoundException e1) {
                                e1.printStackTrace();
                            }                            
                        }
                    }
                    
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                handler.sendMessage(new Message());
            }
        }).start();
    }
    
    /**
     * 
     * @param path Phone�д��belle pics ��·��
     */
    private void copyBellePicsToPhone(File path) {
        
        // �����pathĿ¼
        if(!path.exists() || !path.isDirectory()) {
            return;
        }
        
        String[] arr = path.list();
        for(String s : arr) {
            File f = new File(path, s);
            f.delete();
        }
        
        // �����õ�asset��bell�ļ����µ��ļ�
        AssetManager mgr = getAssets();
        String[] pathArr = null;
        
        try {
            pathArr = mgr.list("belle");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        
        for(String p : pathArr) {
            try {
                InputStream is = mgr.open("belle/" + p);
                
                File f = new File(path, p);
                FileOutputStream os = new FileOutputStream(f);
                
                int len;
                byte[] buf = new byte[1024];
                while((len = is.read(buf)) != -1) {
                    os.write(buf, 0, len);
                }
                
                os.close();
                is.close();
                    
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * ���ΰ��������ܷ���
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }
}
