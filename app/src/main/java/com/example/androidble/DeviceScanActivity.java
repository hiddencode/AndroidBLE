package com.example.androidble;


import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanActivity extends AppCompatActivity {          //init class

    static final public String LOG_TAG = "BLE-demo";
     public Integer short_way = 0;
    //private LeDeviceListAdapter mLeDeviceListAdapter;              // create instance for list adapter -- replace to recycle view
    private RecyclerServiceAdapter recyclerServiceAdapter;
    private RecyclerView recyclerView;


    private BluetoothAdapter mBluetoothAdapter;
    // Contain true if scan, and false if not scan
    private boolean mScanning;
    private Handler mHandler;

    // Request on location
    private static final int LOCATE_PERMISSION_REQUEST = 888;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 456;
    // Request on enable bluetooth
    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    @SuppressLint("ResourceType")
    @Override
    public void onCreate(Bundle savedInstanceState) {   // <----- entry point
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_devices);
        Log.i(LOG_TAG, "DeviceScanActivity:onCreate");

        mHandler = new Handler();
        // Without it, ScanLeDevice not working
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
        }

        startService(new Intent(this, BluetoothLeService.class)); // start ble service

        // Init a Bluetooth adapter with bluetooth manager
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if(bluetoothManager != null) {
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }

        // Checks Bluetooth support
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Check ble support
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Init recycler view adapter for list of le devices
        recyclerView = findViewById(R.id.scan_view);
        LayoutInflater current_inflater = DeviceScanActivity.this.getLayoutInflater();
        recyclerServiceAdapter = new RecyclerServiceAdapter(current_inflater);
        recyclerView.setAdapter(recyclerServiceAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Log.i(LOG_TAG, ":onCreate, recycleView has been init");
    }

    // context menu in right top part
    // Change visible menu components
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    // Devices
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                recyclerServiceAdapter.clear();
                scanLeDevice(true);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
        }
        return true;
    }


    // next point after entry point
    @Override
    protected void onResume() {
        super.onResume();

        // Check bluetooth state (enable or not)
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        // Check locate permission
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATE_PERMISSION_REQUEST);
        }

        scanLeDevice(true);
    }


    // result intent of device control
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check request code
        if (requestCode == REQUEST_ENABLE_BT && resultCode == AppCompatActivity.RESULT_CANCELED) {
            finish();
            return;
        } else {
            startService(new Intent(this, BluetoothLeService.class));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // Stop scanning in pause event, and clear list
    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        recyclerServiceAdapter.clear();
        Intent service = new Intent(this,BluetoothLeService.class);
        stopService(service);
    }


    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
        new BluetoothAdapter.LeScanCallback() {

            @Override
            public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                runOnUiThread(new Thread() {
                    @Override
                    public void run() {
                        assert true;
                        recyclerServiceAdapter.addDevice(device);
                        recyclerServiceAdapter.notifyDataSetChanged();
                        //Log.i(LOG_TAG, "OnLeScan, device has been added:\n" + "Name: " + device.getName() + "\nAddress: " + device.getAddress());
                    }
                });
            }
        };

    /* Transition to DeviceControl*/
    public void onConnect(View view){

        // Describe click event
        /*recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                short_way = position;
                Log.i(LOG_TAG, "Device position:" + short_way);
            }
        });*/

        final int pos = view.getId();
        final BluetoothDevice device = recyclerServiceAdapter.getDevice(pos);
        Intent activity = new Intent(this, DeviceControlActivity.class);
        view.setEnabled(false);

        // Transmit info to DeviceControl
        activity.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
        activity.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
        activity.putExtra(DeviceControlActivity.EXTRAS_DEVICE_UUID, device.getUuids());

        Log.i(LOG_TAG,"Position:" + pos);
        Log.i(LOG_TAG,"Device name:" + device.getName());
        Log.i(LOG_TAG,"Address:" + device.getAddress());


        startActivity(activity);
    }
}
