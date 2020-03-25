package com.example.androidble;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

/*
 * Adapter for list which contains Services
 */
public class RecyclerServiceAdapter extends RecyclerView.Adapter<RecyclerServiceAdapter.ViewHolder> {

    private ArrayList<BluetoothDevice> LeDeviceList;
    private LayoutInflater Inflater;
    private int selected_pos;

    public RecyclerServiceAdapter(LayoutInflater externalInflater) {
        LeDeviceList = new ArrayList<>();
        Inflater = externalInflater;
    }

    /*
     * Add le device to list
     * @param LeDevice  --  Bluetooth device
     */
    public void addDevice(BluetoothDevice LeDevice){
        if(!LeDeviceList.contains(LeDevice)){
            LeDeviceList.add(LeDevice);
        }
    }

    /*
     * @param pos   -- position in List
     * @return Le Device on position
     */
    public BluetoothDevice getDevice(int pos){
        return LeDeviceList.get(pos);
    }

    /*
     * Clear list
     */
    public void clear(){
        LeDeviceList.clear();
    }


    /*
     * Create new item in List
     * @param viewGroup     -- group for add
     * @param i             -- pos (unnecessary)
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_device, viewGroup, false);
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
        BluetoothDevice LeDevice = LeDeviceList.get(i);
        if(LeDevice.getName() != null) {
            viewHolder.name.setText(LeDevice.getName());
        }else {
            viewHolder.name.setText(name);
        }
        viewHolder.address.setText(LeDevice.getAddress());
        viewHolder.btn_connect.setId(i);

    }

        /*
     * @return size of List
     */
    @Override
    public int getItemCount() {
        return LeDeviceList.size();
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
        private TextView name;
        private TextView address;
        private Button btn_connect;

        ViewHolder(final View itemView) {
            super(itemView);
            name =  itemView.findViewById(R.id.device_name);
            address =  itemView.findViewById(R.id.device_address);
            btn_connect = itemView.findViewById(R.id.to_control);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            listener.onItemClick(itemView,pos);
                            selected_pos = pos;
                        }
                    }
                }
            });


        }
    }
}