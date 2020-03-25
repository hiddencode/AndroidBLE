package com.example.androidble.ifaces;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class LeInfo implements Parcelable {

    private List<BluetoothGattCharacteristic> LeCHS;
    //private BluetoothGattService LeService;

    /* constructor for equal list */
    public LeInfo(List<BluetoothGattCharacteristic> LeCHS){
        this.LeCHS = LeCHS;
        Log.i("BLE-demo","LeInfo:  Internal -" + this.LeCHS);
        Log.i("BLE-demo","LeInfo:  External -" + LeCHS);
    }

    /* constructor for parcel */
    public LeInfo(Parcel in){

        List<BluetoothGattCharacteristic> listCHS = new ArrayList<>();
        in.readList(listCHS, List.class.getClassLoader());
        LeCHS = listCHS;
        //this.LeCHS.addAll(listCHS); //mb unsafe
    }

    public List<BluetoothGattCharacteristic> getLeCHS(){
        return LeCHS;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(LeCHS);
    }

    public static final Parcelable.Creator<LeInfo> CREATOR = new Parcelable.Creator<LeInfo>(){
        @Override
        public LeInfo createFromParcel(Parcel source){
            return new LeInfo(source);
        }

        @Override
        public LeInfo[] newArray(int size) {
            return new LeInfo[size];
        }


    };
}
