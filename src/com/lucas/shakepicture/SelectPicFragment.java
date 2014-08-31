package com.lucas.shakepicture;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lucas.picareaselect.PicAreaSelect;
import com.lucas.picareaselect.PicAreaSelect.OnSelectDoneListener;

public class SelectPicFragment extends Fragment {
    
    TextView localPicTv;
    TextView appBuildInPicTv;
    TextView photographTv;
    
    private static final int SELECT_PIC = 0; // ѡ�񱾵�ͼƬ
    private static final int TAKE_PIC = 1; // ����
    private static final int DIG_PIC = 2; // ͼƬ�ü�
    
    private Uri uriTakePic;         // ����ʱ�����պ�ͼƬ��Uri
    
    private Activity activity;
    
    @Override
    public void onAttach(Activity activity) {
        this.activity = activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_select_pic, null);
        localPicTv = (TextView) v.findViewById(R.id.local_pic);
        appBuildInPicTv = (TextView) v.findViewById(R.id.app_built_in_pic);
        photographTv = (TextView) v.findViewById(R.id.photograph);
                
        localPicTv.setOnClickListener(new OnClickListener() {
            
            public void onClick(View v) {
             // ����ϵͳͼ�⣬ѡ��һ��ͼƬ
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_PIC);
                
       //             ShakePicActivity.start(activity);
                
     //           Intent intent = new Intent(activity, Vertices.class);
     //           startActivity(intent);
            }
        });
        
        appBuildInPicTv.setOnClickListener(new OnClickListener() {
            
            public void onClick(View v) {
      //          ShakePicActivity.start(activity, R.drawable.pa);
            }
        });
        
        photographTv.setOnClickListener(new OnClickListener() {
            
            public void onClick(View v) {
//                uriTakePic = Uri.fromFile(Common.getFileInSdcardByName(activity, "camera_raw.jpg", true));
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriTakePic);
//                startActivityForResult(intent, TAKE_PIC);
            }
        });
        
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PIC) {  // ����
          //  private Uri uriTakePic;         // ����ʱ�����պ�ͼƬ��Uri
        } else if (requestCode == SELECT_PIC && data != null) {   // ѡ��һ�ű���ͼƬ
            // �õ�ͼƬ��ȫ·��
            Uri uri = data.getData();
   //         Log.e("", activity.toString() + ", " + uri.toString());
            PicAreaSelect.startSelect(activity, uri, new OnSelectDoneListener() {
                
                @Override
                public void onSelectDone(Bitmap bitmap, Set<RectF> rectSet) {
                  WillShakePicBitmap.bitmap = bitmap;
                  
                  ArrayList<RectF> list = new ArrayList<RectF>();
                  list.addAll(rectSet);
                  
                  ShakePicActivity.start(activity, list);
                }
            });
        } 
//        else if(requestCode == DIG_PIC && resultCode == Activity.RESULT_OK) {            
//            WillShakePicBitmap.bitmap = PicAreaSelect.getBitmap();
//            
//            ArrayList<RectF> list = new ArrayList<RectF>();
//            list.addAll(PicAreaSelect.getSelectRect());
//            
//            ShakePicActivity.start(activity, list);
//        }
//        else if(requestCode == CUT_PIC && data != null) {
//            Bundle extras = data.getExtras();
//            if (extras != null) {
//                Bitmap image = extras.getParcelable("data");
//                ShakePicActivity.start(activity, image);
////                if (image != null) {
////                    imageView1.setImageBitmap(image);
////                    try {
////                        File myCaptureFile = new File("/data/data/com.test.cropphoto/files/icon.jpg");
////                        BufferedOutputStream bos = new BufferedOutputStream(
////                                new FileOutputStream(myCaptureFile));
////                        image.compress(Bitmap.CompressFormat.JPEG, 80, bos);
////                        bos.flush();
////                        bos.close();
////                    } catch (FileNotFoundException e) {
////                        e.printStackTrace();
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////                }
//            }
//        }
        
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    /** 
     * �ü�ͼƬ����ʵ�� 
     * @param uri 
     */ 
//     public void startPhotoCut(Uri uri) { 
//         Intent intent = new Intent("com.android.camera.action.CROP"); 
//         intent.setDataAndType(uri, "image/*"); 
//         //�������crop=true�������ڿ�����Intent��������ʾ��VIEW�ɲü� 
//         intent.putExtra("crop", "true"); 
//         // aspectX aspectY �ǿ�ߵı��� 
//         intent.putExtra("aspectX", 1); 
//         intent.putExtra("aspectY", 1); 
//         // outputX outputY �ǲü�ͼƬ��� 
//         intent.putExtra("outputX", 100); 
//         intent.putExtra("outputY", 100); 
//  //       intent.putExtra("output", Uri.fromFile(Common.getFileInSdcardByName(activity, "tmp.jpg", true)));
// //        intent.putExtra("outputFormat", "JPEG"); // ���ظ�ʽ
//         intent.putExtra("return-data", true); 
//         startActivityForResult(intent, CUT_PIC); 
//     } 

}
