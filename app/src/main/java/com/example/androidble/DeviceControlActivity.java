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
import androidx.appcompat.app.AppCompatActivity;


import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
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
            mBluetoothLeService.connect(mDeviceAddress);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result = " + result);
        }
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
            HashMap<String, String> currentServiceData = new HashMap<>();
            uuid = gattService.getUuid().toString();

            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
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

                //serviceActivity.putExtra(ServiceControlActivity.EXTRA_SERVICE_NAME,);
                serviceActivity.putExtra(ServiceControlActivity.EXTRA_SERVICE_UUID, uuid);
                serviceActivity.putExtra(ServiceControlActivity.EXTRA_SERVICE_TYPE, type);
                Bundle chsArgs;
                //chsArgs.s

                /* Working */
                BluetoothGattService service = new BluetoothGattService(UUID.fromString(uuid),type);
                BluetoothGatt gatt = mBluetoothLeService.getLeService();
                Log.i(LOG_TAG,"Service - " + service.getUuid() + " have characteristics: "+ gatt.getService(service.getUuid()).getCharacteristics());

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
        SendMessageDialogFragment dialogFragment= new SendMessageDialogFragment();
        dialogFragment.show(getSupportFragmentManager(),"TAG");
    }

    /*
     * Write value to selected characteristic with property - WRITE
     */
    public void CharacteristicWrite(View v){
        mBluetoothLeService.log_state_connection(); // output info about connection
        /* startActivity(DialogFragment.class), for sending message */
    }

    public void CharacteristicRead(View v) {
        // Transmit to WriteCharacteristic
        // Temperate variables
        UUID uuid = UUID.fromString("ef2a2826-6a74-11ea-bc55-0242ac130004");
        byte[] bytes = "0xAAA".getBytes();
        BluetoothGattCharacteristic chs = new BluetoothGattCharacteristic(uuid,BluetoothGattCharacteristic.PROPERTY_WRITE, BluetoothGattCharacteristic.PERMISSION_WRITE);


        // Need select characteristic
        //mBluetoothLeService.sendMessage(uuid, bytes, BluetoothGattCharacteristic.PROPERTY_WRITE, BluetoothGattCharacteristic.PERMISSION_WRITE);
        //mBluetoothLeService.sendMessage(chs);

    }

}
