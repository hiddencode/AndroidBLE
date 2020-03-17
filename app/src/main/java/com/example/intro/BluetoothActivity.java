package com.example.intro;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;


@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BluetoothActivity extends Activity {
    /* List of devices */
    private RecyclerView rView;
    private RecyclerView.Adapter rAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private TextView textView = findViewById(R.id.txt_view);

    static final int SCAN_REQUEST = 1; // The request code.
    final String LOG_TAG = getClass().getSimpleName();

    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    @SuppressLint("HardwareIds")

    @Override
    public void onCreate(Bundle stateBundle){
        super.onCreate(stateBundle);
        setContentView(R.layout.activity_bluetooth);


        rView = findViewById(R.id.rview_list);   // Area for output info
        rView.setHasFixedSize(true);
        // Use a liner layout manager
        layoutManager = new LinearLayoutManager(this);
        rView.setLayoutManager(layoutManager);
        String[] rDaftest = {"Sdf", "dfs"};
        rAdapter = new RViewAdapter(rDaftest);
        //rView.setAdapter(rAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent_scan = new Intent(this, ScanLeActivity.class);

        startActivityForResult(intent_scan, SCAN_REQUEST); // start activity for getting result of scan
    }



    // Return result of scan
    @Override
    protected void onActivityResult(int request_code, int result_code, Intent result_activity){
        super.onActivityResult(request_code, result_code, result_activity);
        if(request_code == SCAN_REQUEST){
            if(result_code == RESULT_OK) {
                Log.i(LOG_TAG,"Scan enabled");
            }
        }
    }

}


