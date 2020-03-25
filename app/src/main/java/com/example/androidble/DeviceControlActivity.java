package com.example.androidble;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.androidble.adapters.ExpandableListAdapter;
import com.example.androidble.dialogs.WriteMessageDialogFragment;
import com.example.androidble.ifaces.LeInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.lang.String.*;

/**
 *  Manage connection with device
 */
public class DeviceControlActivity extends AppCompatActivity {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String EXTRAS_DEVICE_UUID = "DEVICE_UUID";
    private static final String LIST_NAME = "SERVICE_NAME";
    private static final String LIST_UUID = "SERVICE_UUID";
    private static final String LOG_TAG = "BLE-demo";

    private TextView mDataField;
    private ExpandableListView ServicesView;

    private String mDeviceName;
    private String mDeviceAddress;

    private BluetoothLeService mBluetoothLeService;
    private static BluetoothGatt copyGatt;
    private ArrayList<BluetoothGattService> ArrayService = new ArrayList<>();

    private boolean mConnected = false;
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device
            if(!mBluetoothLeService.connect(mDeviceAddress)){
                //Toast.makeText(this, "Failed connection", Toast.LENGTH_SHORT).show();
                finish();;
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    /*
     * Receive message from broadcast
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // display services and characteristics
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };


    private void clearUI() {
        mDataField.setText(R.string.no_data);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.button_control);    // failed in start

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        mDataField = findViewById(R.id.data_value);
        ServicesView = findViewById(R.id.services_list);

        if(getActionBar()!= null) {
            getActionBar().setTitle(mDeviceName);
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        checkRequest();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        /*
        Check request code for activity result
           if request done :
                    processing $data
                    send message with $data
                    check result operation
                setResult(RESULT_OK, intent)
           else:
                setResult(RESULT_FALSE, intent)
                finish(); <- md unnecessary
        */
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Thread() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                mDataField.setText(format("%d/%s/%s", resourceId, mDeviceName, mDeviceAddress));
            }
        });
    }

    private void displayData(String data) {
        if (data != null) {
            mDataField.setText(data);
        }
    }
    // Demonstrates the supported GATT Services/Characteristics.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;

        ArrayList<ArrayList<String>> ServicesList = new ArrayList<ArrayList<String>>();
        ArrayList<String> ServicesData = new ArrayList<String>();

        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            String TextData;
            uuid = gattService.getUuid().toString();
            ArrayService.add(gattService);
            TextData = SampleGattAttributes.lookup(uuid, unknownServiceString) + "\n" + uuid;// + "\n" + gattService.getType();
            // output info in view
            ServicesData.add(TextData);

        }

        // Add values into groups
        ServicesList.add(ServicesData);
        // Init adapters for expand list
        ExpandableListAdapter ServiceAdapter = new ExpandableListAdapter(getApplicationContext(), ServicesList, "Services");
        // Set adapters for expand list
        ServicesView.setAdapter(ServiceAdapter);
        // Listener event
        ServicesView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Log.i(LOG_TAG,"Item " + childPosition + " has been clicked");

                // - Transition to Activity for processing characteristics
                // - Transmit info about service
                Intent serviceActivity = new Intent(DeviceControlActivity.this, ServiceControlActivity.class);
                int type = ArrayService.get(childPosition).getType();
                String uuid = ArrayService.get(childPosition).getUuid().toString();

                serviceActivity.putExtra(ServiceControlActivity.EXTRA_SERVICE_UUID, uuid);
                serviceActivity.putExtra(ServiceControlActivity.EXTRA_SERVICE_TYPE, type);

                /* Working */
                BluetoothGattService service = new BluetoothGattService(UUID.fromString(uuid),type);
                BluetoothGatt LeGatt = mBluetoothLeService.getLeService();
                List<BluetoothGattCharacteristic>  LeCHS = LeGatt.getService(service.getUuid()).getCharacteristics();
                Log.i(LOG_TAG,"Service - " + service.getUuid() + " have characteristics: " + LeGatt.getService(service.getUuid()).getCharacteristics());
                serviceActivity.putExtra(ServiceControlActivity.EXTRA_LE_INFO, new LeInfo(LeCHS));

                // for onActivityResult
                copyGatt = LeGatt;
                startActivity(serviceActivity);

                return true;
            }
        });
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    /* Call dialog for send message (notify || command)*/
    public void showDialog(View v) {
        WriteMessageDialogFragment dialogFragment= new WriteMessageDialogFragment();
        dialogFragment.show(getSupportFragmentManager(),"TAG");
    }

    public void CharacteristicRead(View v) {
        // Transmit to WriteCharacteristic
        // Temperate variables
        UUID uuid = UUID.fromString("ef2a2826-6a74-11ea-bc55-0242ac130004");
        byte[] bytes = "0xAAA".getBytes();
        BluetoothGattCharacteristic chs = new BluetoothGattCharacteristic(uuid,BluetoothGattCharacteristic.PROPERTY_WRITE, BluetoothGattCharacteristic.PERMISSION_WRITE);

    }

    public void checkRequest(){
        Intent intent = getIntent();
        int requestCode = intent.getIntExtra(ServiceControlActivity.EXTRA_REQUEST, 0);

        if(requestCode == ServiceControlActivity.REQUEST_CODE_MESSAGE){

            UUID serviceUUID = UUID.fromString(intent.getStringExtra(ServiceControlActivity.EXTRA_SEND_SERVICE_UUID));
            UUID chsUUID = UUID.fromString(intent.getStringExtra(ServiceControlActivity.EXTRA_SEND_CHS_UUID));
            byte[] bytes = intent.getByteArrayExtra(ServiceControlActivity.EXTRA_SEND_BYTES);

            copyGatt.getService(serviceUUID).getCharacteristic(chsUUID).setValue(bytes);
            if(copyGatt.writeCharacteristic(copyGatt.getService(serviceUUID).getCharacteristic(chsUUID))){
               setResult(RESULT_OK);
               finish();
            }else{
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }


}
