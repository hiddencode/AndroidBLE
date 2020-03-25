package com.example.androidble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_READ;


/*
 * Adapter for list which contains Characteristics
 */
public class RecyclerCharacteristicAdapter extends RecyclerView.Adapter<RecyclerCharacteristicAdapter.ViewHolder> {

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
     * @param pos   -- position in List
     * @return Le Device on position
     */
    public BluetoothGattCharacteristic getDevice(int pos){
        return CharacteristicList.get(pos);
    }

    /*
     * Clear list
     */
    public void clear(){
        CharacteristicList.clear();
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
        String property;
        switch (Characteristic.getProperties()){
            case BluetoothGattCharacteristic.PROPERTY_BROADCAST :
                property = "BROADCAST";
                break;
            case PROPERTY_READ :
                property = "READ";
                break;
            case BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE :
                property = "WRITE NO RESPONSE";
                break;
            case BluetoothGattCharacteristic.PROPERTY_NOTIFY :
                property = "NOTIFY";
                break;
            case BluetoothGattCharacteristic.PROPERTY_INDICATE :
                property = "INDICATE";
                break;
            case BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE :
                property = "SIGNED WRITE";
                break;
            case BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS :
                property = "EXTENDED PROPS";
                break;
            default:
                property = "UNKNOWN";
        }
        viewHolder.property.setText(property);
        viewHolder.btn_connect.setId(i);

    }

    /*
     * @return size of List
     */
    @Override
    public int getItemCount() {
        return CharacteristicList.size();
    }

    private OnItemClickListener listener;
    public interface OnItemClickListener{
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    /*
     * Class describe components of item in List
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView uuid;
        private TextView property;
        private Button btn_connect;

        ViewHolder(final View itemView) {
            super(itemView);
            uuid =  itemView.findViewById(R.id.chs_uuid);
            property =  itemView.findViewById(R.id.chs_property);
            btn_connect = itemView.findViewById(R.id.button);

//            itemView.setOnClickListener(new View.OnClickListener(){
//                @Override
//                public void onClick(View v) {
//                    if (listener != null) {
//                        int pos = getAdapterPosition();
//                        if(pos != RecyclerView.NO_POSITION){
//                            listener.onItemClick(itemView,pos);
//                            selected_pos = pos;
//                        }
//                    }
//                }
//            });


        }
    }
}