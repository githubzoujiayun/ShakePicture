package com.lucas.shakepicture;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lucas.shakepicture.picareaselector.PicAreaSelect;
import com.lucas.shakepicture.picareaselector.PicAreaSelect.OnSelectDoneListener;
import com.lucas.shakepicture.pictureselector.PicWallActivity;
import com.lucas.util.AndroidUtil;
import com.lucas.util.BitmapLib;
import com.lucas.util.BitmapLib.PicZoomOutType;

public class MainActivity extends Activity {
    
    private TextView localPicTv;
    private TextView appBuildInPicTv;
    private TextView photographTv;
    
    private static final int SELECT_PIC = 0; // ѡ�񱾵�ͼƬ
    private static final int TAKE_PIC = 1; // ����
    private static final int SELECT_BUILD_IN_PIC = 2; // ѡ��Ӧ���Դ�ͼƬ
    
    private Uri uriTakePic;         // ����ʱ�����պ�ͼƬ��Uri
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024 / 1024);  
        int w = AndroidUtil.getScreenWidth(this);
        int h = AndroidUtil.getScreenHeight(this);
        Toast.makeText(this, "" + maxMemory + " MB" + ", [" + w + ", " + h + "]", 1).show();
        
        if(!isAddShortCut()) 
            addShortCut();
        
        // �������ⶶ��Ч��
        TextView tv = (TextView) findViewById(R.id.title);
        Animation animation =  AnimationUtils.loadAnimation(this, R.anim.text_rotate);
        tv.startAnimation(animation);        
        
        // ����С�˶���
        ImageView iv = (ImageView) findViewById(R.id.shake_people);
        Animatable animatable = (Animatable) iv.getBackground();
        animatable.start();
        
        localPicTv = (TextView) findViewById(R.id.local_pic);
        appBuildInPicTv = (TextView) findViewById(R.id.app_built_in_pic);
        photographTv = (TextView) findViewById(R.id.photograph);
                
        // ����ͼƬ
        localPicTv.setOnClickListener(new OnClickListener() {
            
            public void onClick(View v) {
             // ����ϵͳͼ�⣬ѡ��һ��ͼƬ
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_PIC);
            }
        });
        
        // app�Դ�ͼƬ
        appBuildInPicTv.setOnClickListener(new OnClickListener() {
            
            public void onClick(View v) {
                PicWallActivity.startForResult(MainActivity.this, SELECT_BUILD_IN_PIC);
            }
        });
        
        // ����
        photographTv.setOnClickListener(new OnClickListener() {
            
            public void onClick(View v) {
                uriTakePic = Uri.fromFile(Common.getFileInSdcardByName(MainActivity.this, "camera_raw.jpg", true));
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriTakePic);
                startActivityForResult(intent, TAKE_PIC);
            }
        });
    }
    
    private OnSelectDoneListener onSelectDonwListener = new OnSelectDoneListener() {

        @Override
        public boolean onSelectDone(Bitmap bitmap, Set<RectF> rectSet) {
            if(rectSet.size() == 0) {
                Toast.makeText(MainActivity.this, R.string.select_shake_area, Toast.LENGTH_SHORT).show();
                return false;
            }
            
            BitmapReference.willShakePicBitmap = bitmap;

            ArrayList<RectF> list = new ArrayList<RectF>();
            list.addAll(rectSet);

            ShakePicActivity.start(MainActivity.this, list);
            return false;
        }
        
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK) {
            return;
        }
        
        if (requestCode == TAKE_PIC) { // ����
            PicAreaSelect.startSelect(this, uriTakePic, onSelectDonwListener);
        } else if (requestCode == SELECT_PIC && data != null) { // ѡ��һ�ű���ͼƬ
            // �õ�ͼƬ��ȫ·��
            Uri uri = data.getData();
            PicAreaSelect.startSelect(this, uri, onSelectDonwListener);
        } else if(requestCode == SELECT_BUILD_IN_PIC && data != null) {
            String picPath = data.getExtras().getString("picPath");
            InputStream is = null;
            try {
                is = getAssets().open(picPath);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return;
            }
            BitmapReference.appBuildInSelectedBitmap = BitmapLib.decodeBitmap(
                                                    this, is, 100000, 100000, PicZoomOutType.ZOOM_OUT);
            PicAreaSelect.startSelect(this, onSelectDonwListener);
        }
        
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    private long exitTime = 0;

    // �ٰ�һ���˳������ʵ��
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), R.string.click_again_exit, Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    private boolean isAddShortCut() {
        final ContentResolver resolver = this.getContentResolver();

        String AUTHORITY = "com.android.launcher2.settings";

        final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");
        Cursor c = resolver.query(CONTENT_URI,
                        new String[] { "title", "iconResource" }, "title=?",
                        new String[] { getString(R.string.app_name) }, null);

        if (c != null && c.getCount() > 0)
            return true;
        
        return false;
    }
    
    private void addShortCut(){        
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        // ��������
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, getResources().getString(R.string.app_name));
        ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(this.getApplicationContext(), R.drawable.icon);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON,iconRes);
 
        // �Ƿ������ظ�����
        shortcut.putExtra("duplicate", false);
        
        //���������ݷ�ʽ��ͼ��
        Parcelable icon = Intent.ShortcutIconResource.fromContext(this,R.drawable.icon);        
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,icon);
        
        //�����ݷ�ʽ�Ĳ���
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClass(this, MainActivity.class);
        
        // ������������
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        
        //�㲥֪ͨ����ȥ����
        this.sendBroadcast(shortcut);
    }

}
