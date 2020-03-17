package com.example.intro;
import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.UUID;

import static android.content.res.Resources.getSystem;


public class GattService extends Service {

    /* init TAG for Logs*/
    private final static String TAG = GattService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    /* constants for determine state*/
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    /* constants for determine actions of gatt */
    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    public final static UUID UUID_HEART_RATE_MEASUREMENT =
        UUID.fromString("00001110-0000-1000-8000-00805f9b34fb"); // tmp - STUB, old decision under (:52)
        // UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT); // heart rate identifier ; ? - mb similar by like a bleManager.getAddress(...)


    private final BluetoothGattCallback mGattCallback =
            new BluetoothGattCallback() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
                /* method for manage connection with Gatt service */
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    String intentAction;
                    if (newState == BluetoothProfile.STATE_CONNECTED) {             // Gatt successfully connected
                        intentAction = ACTION_GATT_CONNECTED;                       // Save
                        mConnectionState = STATE_CONNECTED;                         // states
                        broadcastUpdate(intentAction);                              // Update broadcast actions
                        Log.i(TAG, "Connected to GATT server.");               // Message to log
                        Log.i(TAG, "Attempting to start service discovery:" +  // Message to log
                                mBluetoothGatt.discoverServices());

                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {   // Gatt disconnected
                        intentAction = ACTION_GATT_DISCONNECTED;                    // Save
                        mConnectionState = STATE_DISCONNECTED;                      // states
                        Log.i(TAG, "Disconnected from GATT server.");
                        broadcastUpdate(intentAction);
                    }
                }

                /* found new service */
                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {             // Check gatt status
                        broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);   // for update broadcast
                    } else {
                        Log.w(TAG, "onServicesDiscovered received: " + status); // or out log message
                    }
                }

                /* update  broadcast with
                 * result of read characteristic
                 */
                @Override
                public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                    }
                }

            };

    /* update broadcast with only action*/
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);   // init Intent with @action
        sendBroadcast(intent);                      // send intent to broadcast
    }

    /* update broadcast with action and characteristic */
    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        // For heart rate
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {     // check uuid
            int flag = characteristic.getProperties();                          // save
            int format = -1;                                                    // data
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);    // init heart rate from characteristic
            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));                 // put extra data to intent
        } else {
            // For all other profiles, writes the data formatted in HEX.
            // Processing characteristic
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(EXTRA_DATA, new String(data) + "\n" +
                        stringBuilder.toString());
            }
        }
        sendBroadcast(intent);  // send intent to broadcast
    }

    /* code-generator*/
    /* Binder - API for system services*/
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
