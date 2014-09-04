package com.lucas.shakepicture;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
   //     setContentView(new MeshBitmap(this));
        
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024 / 1024);  
        Toast.makeText(this, "Max memory is " + maxMemory + "MB", Toast.LENGTH_SHORT).show();
        Log.e("TAG", "Max memory is " + maxMemory + "MB");  
        
        FragmentManager mgr = getFragmentManager();
        FragmentTransaction trans = mgr.beginTransaction();
        trans.add(R.id.fragment_holder, new SelectPicFragment());
        trans.commit();
    }

}
