package com.example.androidble.adapters;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidble.R;

import java.util.ArrayList;



/*
 * Adapter for list which contains Characteristics
 */
public class RecyclerCharacteristicAdapter extends RecyclerView.Adapter<RecyclerCharacteristicAdapter.ViewHolder> {

    private static final String TAG_TX = "WRITE_CHS";
    private static final String TAG_RX = "READ_CHS";
    private static final String TEXT_TX = "WRITE";
    private static final String TEXT_RX = "READ";

    private ArrayList<BluetoothGattCharacteristic> CharacteristicList;
    private LayoutInflater Inflater;
    private int selected_pos;

    public RecyclerCharacteristicAdapter(LayoutInflater externalInflater) {
        CharacteristicList = new ArrayList<>();
        Inflater = externalInflater;
    }

    /*
     * Add le device to list
     * @param LeDevice  --  Bluetooth device
     */
    public void addChs(BluetoothGattCharacteristic Characteristic){
        if(!CharacteristicList.contains(Characteristic)){
            CharacteristicList.add(Characteristic);
        }
    }

    /*
     * Create new item in List
     * @param viewGroup     -- group for add
     * @param i             -- pos (unnecessary)
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_chs, viewGroup, false);
        return new ViewHolder(v);
    }

    /*
     * Bind view with item from List
     * @param viewHolder    -- View for binding
     * @param i             -- Position in list
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        String name = "Unknown";    // string for device without name
        BluetoothGattCharacteristic Characteristic = CharacteristicList.get(i);


        viewHolder.uuid.setText(Characteristic.getUuid().toString());

        ///mb refactor
        String btn_tag;
        String btn_text ;
        String property;
        switch (Characteristic.getProperties()){
            case BluetoothGattCharacteristic.PROPERTY_BROADCAST :
                property = "BROADCAST";
                btn_tag = TAG_RX;
                btn_text = TEXT_RX;
                break;
            case BluetoothGattCharacteristic.PROPERTY_READ :
                property = "READ";
                btn_tag = TAG_RX;
                btn_text = TEXT_RX;
                break;
            case BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE :
                property = "WRITE NO RESPONSE";
                btn_tag = TAG_TX;
                btn_text = TEXT_TX;
                break;
            case BluetoothGattCharacteristic.PROPERTY_NOTIFY :
                property = "NOTIFY";
                btn_tag = TAG_TX;
                btn_text = TEXT_TX;
                break;
            case BluetoothGattCharacteristic.PROPERTY_INDICATE :
                property = "INDICATE";
                btn_tag = TAG_RX;
                btn_text = TEXT_RX;
                break;
            case BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE :
                property = "SIGNED WRITE";
                btn_tag = TAG_TX;
                btn_text = TEXT_TX;
                break;
            case BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS :
                property = "EXTENDED PROPS";
                btn_tag = TAG_TX;
                btn_text = TEXT_TX;
                break;
            default:
                property = "UNKNOWN";
                btn_tag = TAG_TX;
                btn_text = TEXT_TX;
        }
        viewHolder.property.setText(property);

        String permisson;
        switch(Characteristic.getPermissions()){
            case BluetoothGattCharacteristic.PERMISSION_READ :
                permisson = "PERMISSION_READ";
                break;
            case BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED :
                permisson = "PERMISSION_READ_ENCRYPTED";
                break;
            case BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED_MITM :
                permisson = "PERMISSION_READ_ENCRYPTED_MITM";
                break;
            case BluetoothGattCharacteristic.PERMISSION_WRITE :
                permisson = "PERMISSION_WRITE";
                break;
            case BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED :
                permisson = "PERMISSION_WRITE_ENCRYPTED";
                break;
            case BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED_MITM :
                permisson = "PERMISSION_WRITE_ENCRYPTED_MITM";
                break;
            case BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED :
                permisson = "PERMISSION_WRITE_SIGNED";
                break;
            case BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED_MITM :
                permisson = "PERMISSION_WRITE_SIGNED_MITM";
                break;
            default:
                permisson = "UNKNOWN";
        }
        viewHolder.permission.setText(permisson);;
        viewHolder.btn_connect.setTag(btn_tag);
        viewHolder.btn_connect.setText(btn_text);
        viewHolder.btn_connect.setId(i);

    }

    @Override
    public int getItemCount() {
        return CharacteristicList.size();
    }

    /*
     * Class describe components of item in List
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView uuid;
        private TextView property;
        private TextView permission;
        private Button btn_connect;

        ViewHolder(final View itemView) {
            super(itemView);
            uuid =  itemView.findViewById(R.id.chs_uuid);
            property =  itemView.findViewById(R.id.chs_property);
            permission = itemView.findViewById(R.id.chs_permisson);
            btn_connect = itemView.findViewById(R.id.button);

        }
    }
}