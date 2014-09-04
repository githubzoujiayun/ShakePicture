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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lucas.shakepicture.picareaselector.PicAreaSelect;
import com.lucas.shakepicture.picareaselector.PicAreaSelect.OnSelectDoneListener;
import com.lucas.shakepicture.pictureselector.PictureSelector;
import com.lucas.shakepicture.pictureselector.PictureSelector.OnPicSelectedListener;

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
                
        // ����ͼƬ
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
        
        // app�Դ�ͼƬ
        appBuildInPicTv.setOnClickListener(new OnClickListener() {
            
            public void onClick(View v) {
                PictureSelector.select(activity, new OnPicSelectedListener() {
                    
                    @Override
                    public void onSelected(Bitmap bitmap) {
                        BitmapReference.appBuildInSelectedBitmap = bitmap;
                        PicAreaSelect.startSelect(activity, onSelectDonwListener);
                    }
                });
            }
        });
        
        // ����
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
    
    private OnSelectDoneListener onSelectDonwListener = new OnSelectDoneListener() {

        @Override
        public boolean onSelectDone(Bitmap bitmap, Set<RectF> rectSet) {
            if(rectSet.size() == 0) {
                Toast.makeText(activity, R.string.select_shake_area, Toast.LENGTH_SHORT).show();
                return false;
            }
            
            BitmapReference.willShakePicBitmap = bitmap;

            ArrayList<RectF> list = new ArrayList<RectF>();
            list.addAll(rectSet);

            ShakePicActivity.start(activity, list);
            return false;
        }
        
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PIC) { // ����
            // private Uri uriTakePic; // ����ʱ�����պ�ͼƬ��Uri
        } else if (requestCode == SELECT_PIC && data != null) { // ѡ��һ�ű���ͼƬ
            // �õ�ͼƬ��ȫ·��
            Uri uri = data.getData();
            // Log.e("", activity.toString() + ", " + uri.toString());
            PicAreaSelect.startSelect(activity, uri, onSelectDonwListener);
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
    
}
