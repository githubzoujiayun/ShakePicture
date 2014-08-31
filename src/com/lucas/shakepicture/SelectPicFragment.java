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
    
    private static final int SELECT_PIC = 0; // 选择本地图片
    private static final int TAKE_PIC = 1; // 拍照
    private static final int DIG_PIC = 2; // 图片裁剪
    
    private Uri uriTakePic;         // 拍照时，拍照后图片的Uri
    
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
             // 激活系统图库，选择一张图片
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
        if (requestCode == TAKE_PIC) {  // 拍照
          //  private Uri uriTakePic;         // 拍照时，拍照后图片的Uri
        } else if (requestCode == SELECT_PIC && data != null) {   // 选择一张本地图片
            // 得到图片的全路径
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
     * 裁剪图片方法实现 
     * @param uri 
     */ 
//     public void startPhotoCut(Uri uri) { 
//         Intent intent = new Intent("com.android.camera.action.CROP"); 
//         intent.setDataAndType(uri, "image/*"); 
//         //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪 
//         intent.putExtra("crop", "true"); 
//         // aspectX aspectY 是宽高的比例 
//         intent.putExtra("aspectX", 1); 
//         intent.putExtra("aspectY", 1); 
//         // outputX outputY 是裁剪图片宽高 
//         intent.putExtra("outputX", 100); 
//         intent.putExtra("outputY", 100); 
//  //       intent.putExtra("output", Uri.fromFile(Common.getFileInSdcardByName(activity, "tmp.jpg", true)));
// //        intent.putExtra("outputFormat", "JPEG"); // 返回格式
//         intent.putExtra("return-data", true); 
//         startActivityForResult(intent, CUT_PIC); 
//     } 

}
