package com.example.androidble;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
 * TODO: Check Mesh principe with BLE,
 *       create group interaction with devices
 */

/*
 *  Using for group up devices,
 *  subscribing to notification
 *  のと:  Start this service in $GroupActivity,
 *         interacting with $BluetoothLeService
 *
 */
public class MeshLeService extends Service {

    BluetoothLeService bluetoothLeService;  // initial with helps intent from prev state
    // < Key(Tag), List<Values> >
    HashMap <Object, List<BluetoothDevice>> LeGroup;

    /*
     * Add to group with Tag,
     */
    public boolean addToGroup(Object Tag, BluetoothDevice device){
        List<BluetoothDevice> devices = LeGroup.get(Tag);
        assert devices != null;
        devices.add(device);

        LeGroup.put(Tag, devices);
        return true;

//      Check existing by group
//        if(!LeGroup.containsKey(Tag) && LeGroup.containsValue(device)) {
//            LeGroup.add(device);
//            return true;
//        }else{
//            return false;
//        }
    }

    public BluetoothDevice getItem(Object Tag, int index){
        return  LeGroup.get(Tag).get(index);
    }

    /*
     * Init group list at open group menu
     * after try to connect each device in group,
     * if anyone device don`t answer output err
     *
     */
    public void groupLeInit(Object Tag){
        /* mb unnecessary */
    }

    public void sendCommand(Object Tag){
        /* Steps:
         * - send request
         * - check result of request
         * - send command with relay and increase it by diff value
         */
    }
    //public void groupUpLeDevices(Object Tag){ }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
