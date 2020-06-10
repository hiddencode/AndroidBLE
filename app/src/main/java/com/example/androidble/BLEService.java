package com.example.androidble;


import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;


/*
 * In this class describe service, which helps interaction with BLE-devices
 * Sequence running tasks:
 * Start service : onCreate > onStartCommand
 * Detecting devices: scanLeDevice > onLeScan > rScanner.run
 * Search ESPM and interaction: connectESPM > onConnectionStateChanged > onServicesDiscovered
 * Send message to ESPM : outputMessagesReceiver.onReceive > sendMessagePart > onCharacteristicWrite
 */
public class BLEService extends Service {

    //  Exchange messages (WIP)
    private String inputMessage;
    private String[] outputMessage;
    private int partsCounter;
    private BroadcastReceiver outputMessagesReceiver;

    // For scanning BLE-devices
    static final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private boolean isScanning;
    private ArrayList<BluetoothDevice> bleDevices;
    private Handler handler;
    private Runnable rScanner;
    private final int SCAN_PERIOD = 10000;

    // Interaction with BLE-devices
    private int devNum;
    private BluetoothGatt ESPMGatt;
    private final UUID  serviceUuid = UUID.fromString("00000000-0000-0000-0000-0000E017C0FD"),
    //readChUuid  = UUID.fromString("00000000-0000-0000-0000-0001E017C0FD"),
    writeChUuid = UUID.fromString("00000000-0000-0000-0000-0002E017C0FD"),
            notifyChUuid = UUID.fromString("00000000-0000-0000-0000-0003E017C0FD"),
            descriptorUuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");


    private final static String LOG_TAG = "BLE-demo/BLEService";
    public final static String MESSAGE_TEXT = "Message text";


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LOG_TAG, "Service was create");
        //bluetoothAdapter.enable();

        // Init
        isScanning = false;
        devNum = 0;

        inputMessage = "";
        handler = new Handler();
        scanLeDevice(true);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOG_TAG, "Фоновая служба запущена");
        // Scanning devices
        if (!isScanning) {
            scanLeDevice(true);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    // ------------------------------------------------------------------
    // Search and create list of ble devices
    // ------------------------------------------------------------------

    // For scanning devices
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            handler.postDelayed(rScanner = new Runnable() {
                @Override
                public void run() {
                    isScanning = false;
                    bluetoothAdapter.stopLeScan(leScanCallback);
                    if (bleDevices.size() != 0) {
                        Log.i(LOG_TAG, "Found BLE-devices: " + bleDevices.toString());
                        // Try to find ESPM and connect to it
                        connectESPM();
                    } else {
                        Log.i(LOG_TAG, "Devices is not found");
                        sendBroadcast(new Intent("CHAT_STATE_CHANGED"));
                    }
                }
            }, SCAN_PERIOD);
            // Запускаем сканирование, его результаты будут приходить в метод onLeScan
            isScanning = true;
            bleDevices = new ArrayList<>();
            bluetoothAdapter.startLeScan(/*new UUID[]{serviceUuid}, */leScanCallback);
            Log.i(LOG_TAG, "Detecting active ");

        } else {
            bluetoothAdapter.stopLeScan(leScanCallback);
            Log.i(LOG_TAG, "stop le scan");
        }
    }


    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            // Save all devices
            if (isScanning && !bleDevices.contains(device)) {
                bleDevices.add(device);
                Log.i(LOG_TAG, "Devices has been added " + device.toString());
            }
            // Stop finding, if we get device
            if (isScanning
                    && device != null
                    && device.getName() != null
                    && device.getName().contains("IAp")) {
                Log.i(LOG_TAG, "добавлено " + device.getName());
                handler.removeCallbacks(rScanner);
                bleDevices.add(device);
                rScanner.run();
            }
        }
    };


    // ------------------------------------------------------------------
    // Finds ESPM and interaction
    // ------------------------------------------------------------------

    // Request for connect to first found devices
    void connectESPM() {
        if (devNum < (bleDevices.size())) {
            Log.i(LOG_TAG, "(" + devNum + ") " + bleDevices.get(devNum).toString() + " - connecting . . .");
            bleDevices.get(devNum).connectGatt(BLEService.this, false, gattCallback);
        } else {
            Log.i(LOG_TAG, "Среди найденных устройств нет тангенты");
            //ChatActivity.setChatState(ChatActivity.DISCONNECTED);
            //sendBroadcast(new Intent(CHAT_STATE_CHANGED));
        }
    }

    // WIP
    // GATT callbacks
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

        // Results of connect
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            //
            if (newState == BluetoothGatt.STATE_CONNECTED && EspmActivity.getEspState() != EspmActivity.CONNECTED) {
                Log.i(LOG_TAG, "(" + devNum + ") " + gatt.getDevice().toString() + " - request services . . .");
                gatt.discoverServices();
            }
            if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                if (EspmActivity.getEspState() != EspmActivity.DEFAULT) {
                    Log.i(LOG_TAG, "(" + devNum + ") " + gatt.getDevice().toString() + " - turn off");
                    if (EspmActivity.getEspState() == EspmActivity.CONNECTING) {
                        devNum++;
                        connectESPM();
                    } else {
                        handler.removeCallbacks(rScanner);
                        EspmActivity.setEspState(EspmActivity.DISCONNECTED);
                        sendBroadcast(new Intent("Changed")); // create action var PuFiSt String
                    }
                }
                // After turn off ESPM, close connection
                if (gatt == ESPMGatt) {
                    ESPMGatt.close();
                    ESPMGatt = null;
                }
            }
        }

        // Results of services
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.i(LOG_TAG, "(" + devNum + ") " + gatt.getDevice().toString() + " - check up services . . .");
            // Checkout service of device
            if (gatt.getService(serviceUuid) != null) {
                Log.i(LOG_TAG, "(" + devNum + ") " + gatt.getDevice().toString() + " - espm :)");
                EspmActivity.setEspState(EspmActivity.CONNECTED);
                sendBroadcast(new Intent("Changed")); // create action var PuFiSt String
                ESPMGatt = gatt;
                bleDevices = null;
            } else {
                Log.i(LOG_TAG, "(" + devNum + ") " + gatt.getDevice().toString() + " - isn`t espm T_T");
                devNum++;
                gatt.close();
                connectESPM();
            }
        }

        // Сюда приходят результаты запросов на чтение характеристики
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            assert  true;
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            assert true;
            //sendMessagePart();
        }


    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "Background service was destroyed");
        // If was detected, stop scanner
        if (handler != null) {
            handler.removeCallbacks(rScanner);
        }
        bleDevices = null;
        // If ESPM was found, disconnect of it
        if (ESPMGatt != null) {
            ESPMGatt.disconnect();
        }
        // Kill receiver
        unregisterReceiver(outputMessagesReceiver);
    }
}