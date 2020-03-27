package com.example.androidble;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidble.adapters.RecyclerServiceAdapter;

import java.util.List;


public class GroupControlActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerServiceAdapter recyclerServiceAdapter;

    @Override
    public void onCreate(Bundle SavedState){
        super.onCreate(SavedState);
        setContentView(R.layout.group_devices);

        recyclerView = findViewById(R.id.chs_view);
        LayoutInflater current_inflater = GroupControlActivity.this.getLayoutInflater();
        recyclerServiceAdapter = new RecyclerServiceAdapter(current_inflater);
        recyclerView.setAdapter(recyclerServiceAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    public void onResume(){
        super.onResume();


    }

    /*  method for init group list
     *   @param Tag  -   Tag of group
     *   @return list of bluetooth devices
     *   fillGroupList(Object Tag)
     *
     *   #Steps:
     *   - Send request for existing devices in group
     *   -
     *   -
     *   -
     */
    private List<BluetoothDevice> devicesFromGroup(Object Tag){
        List<BluetoothDevice> tmp = null;
        return tmp;
    }

}
