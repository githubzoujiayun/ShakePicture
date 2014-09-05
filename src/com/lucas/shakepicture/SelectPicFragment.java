//package com.lucas.shakepicture;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.Set;
//
//import android.app.Activity;
//import android.app.Fragment;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.RectF;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.lucas.shakepicture.picareaselector.PicAreaSelect;
//import com.lucas.shakepicture.picareaselector.PicAreaSelect.OnSelectDoneListener;
//import com.lucas.shakepicture.pictureselector.PicWallActivity;
//import com.lucas.util.BitmapLib;
//import com.lucas.util.BitmapLib.PicZoomOutType;
//
//public class SelectPicFragment extends Fragment {
//    
//    TextView localPicTv;
//    TextView appBuildInPicTv;
//    TextView photographTv;
//    
//    private static final int SELECT_PIC = 0; // 选择本地图片
//    private static final int TAKE_PIC = 1; // 拍照
//    private static final int DIG_PIC = 2; // 图片裁剪
//    
//    private static final int SELECT_BUILD_IN_PIC = 3; // 选择应用自带图片
//    
//    private Uri uriTakePic;         // 拍照时，拍照后图片的Uri
//    
//    private Activity activity;
//    
//    @Override
//    public void onAttach(Activity activity) {
//        this.activity = activity;
//        super.onAttach(activity);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//        View v = inflater.inflate(R.layout.fragment_select_pic, null);
//        localPicTv = (TextView) v.findViewById(R.id.local_pic);
//        appBuildInPicTv = (TextView) v.findViewById(R.id.app_built_in_pic);
//        photographTv = (TextView) v.findViewById(R.id.photograph);
//                
//        // 本地图片
//        localPicTv.setOnClickListener(new OnClickListener() {
//            
//            public void onClick(View v) {
//             // 激活系统图库，选择一张图片
//                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setType("image/*");
//                startActivityForResult(intent, SELECT_PIC);
//                
//       //             ShakePicActivity.start(activity);
//                
//     //           Intent intent = new Intent(activity, Vertices.class);
//     //           startActivity(intent);
//            }
//        });
//        
//        // app自带图片
//        appBuildInPicTv.setOnClickListener(new OnClickListener() {
//            
//            public void onClick(View v) {
////                PictureSelector.select(activity, new OnPicSelectedListener() {
////                    
////                    @Override
////                    public void onSelected(Bitmap bitmap) {
////                        BitmapReference.appBuildInSelectedBitmap = bitmap;
////                        PicAreaSelect.startSelect(activity, onSelectDonwListener);
////                    }
////                });
//                
//                PicWallActivity.startForResult(SelectPicFragment.this, SELECT_BUILD_IN_PIC);
//            }
//        });
//        
//        // 拍照
//        photographTv.setOnClickListener(new OnClickListener() {
//            
//            public void onClick(View v) {
////                uriTakePic = Uri.fromFile(Common.getFileInSdcardByName(activity, "camera_raw.jpg", true));
////                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriTakePic);
////                startActivityForResult(intent, TAKE_PIC);
//            }
//        });
//        
//        return v;
//    }
//    
//    private OnSelectDoneListener onSelectDonwListener = new OnSelectDoneListener() {
//
//        @Override
//        public boolean onSelectDone(Bitmap bitmap, Set<RectF> rectSet) {
//            if(rectSet.size() == 0) {
//                Toast.makeText(activity, R.string.select_shake_area, Toast.LENGTH_SHORT).show();
//                return false;
//            }
//            
//            BitmapReference.willShakePicBitmap = bitmap;
//
//            ArrayList<RectF> list = new ArrayList<RectF>();
//            list.addAll(rectSet);
//
//            ShakePicActivity.start(activity, list);
//            return false;
//        }
//        
//    };
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(resultCode != activity.RESULT_OK) {
//            return;
//        }
//        
//        if (requestCode == TAKE_PIC) { // 拍照
//            // private Uri uriTakePic; // 拍照时，拍照后图片的Uri
//        } else if (requestCode == SELECT_PIC && data != null) { // 选择一张本地图片
//            // 得到图片的全路径
//            Uri uri = data.getData();
//            // Log.e("", activity.toString() + ", " + uri.toString());
//            PicAreaSelect.startSelect(activity, uri, onSelectDonwListener);
//        } else if(requestCode == SELECT_BUILD_IN_PIC && data != null) {
//            String picPath = data.getExtras().getString("picPath");
//            InputStream is = null;
//            try {
//                is = activity.getAssets().open(picPath);
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//                return;
//            }
//            BitmapReference.appBuildInSelectedBitmap = BitmapLib.decodeBitmap(
//                                                    activity, is, 100000, 100000, PicZoomOutType.ZOOM_OUT);
//            PicAreaSelect.startSelect(activity, onSelectDonwListener);
//        }
////        else if(requestCode == DIG_PIC && resultCode == Activity.RESULT_OK) {            
////            WillShakePicBitmap.bitmap = PicAreaSelect.getBitmap();
////            
////            ArrayList<RectF> list = new ArrayList<RectF>();
////            list.addAll(PicAreaSelect.getSelectRect());
////            
////            ShakePicActivity.start(activity, list);
////        }
////        else if(requestCode == CUT_PIC && data != null) {
////            Bundle extras = data.getExtras();
////            if (extras != null) {
////                Bitmap image = extras.getParcelable("data");
////                ShakePicActivity.start(activity, image);
//////                if (image != null) {
//////                    imageView1.setImageBitmap(image);
//////                    try {
//////                        File myCaptureFile = new File("/data/data/com.test.cropphoto/files/icon.jpg");
//////                        BufferedOutputStream bos = new BufferedOutputStream(
//////                                new FileOutputStream(myCaptureFile));
//////                        image.compress(Bitmap.CompressFormat.JPEG, 80, bos);
//////                        bos.flush();
//////                        bos.close();
//////                    } catch (FileNotFoundException e) {
//////                        e.printStackTrace();
//////                    } catch (IOException e) {
//////                        e.printStackTrace();
//////                    }
//////                }
////            }
////        }
//        
//        super.onActivityResult(requestCode, resultCode, data);
//    }
//    
//}
