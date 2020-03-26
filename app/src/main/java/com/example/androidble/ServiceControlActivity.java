package com.example.androidble;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidble.adapters.RecyclerCharacteristicAdapter;
import com.example.androidble.dialogs.WriteMessageDialogFragment;
import com.example.androidble.ifaces.LeInfo;

import java.util.List;
import java.util.UUID;




public class ServiceControlActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerCharacteristicAdapter recyclerCharacteristicAdapter;

    public static final String EXTRA_SERVICE_UUID = "SERVICE_UUID";
    public static final String EXTRA_SERVICE_TYPE = "SERVICE TYPE"; //Primary || Secondary
    public static final String EXTRA_LE_INFO = "LeInfo";

    public static final int REQUEST_CODE_MESSAGE = 0xDEAD;
    public static final int REQUEST_CODE_WRITE = 0xAC00;
    public static final int REQUEST_CODE_READ = 0XCA00;

    public static final String EXTRA_REQUEST = "REQUEST";
    public static final String EXTRA_SEND_SERVICE_UUID = "SEND SERVICE UUID";
    public static final String EXTRA_SEND_CHS_UUID = "SEND CHS UUID";
    public static final String EXTRA_SEND_BYTES = "SEND BYTES";

    public static UUID serviceUUID;
    public static int serviceType;


    public BluetoothGattService currentService;
    public List<BluetoothGattCharacteristic> ArrayCharacteristic;

    public WriteMessageDialogFragment  dialogFragment;

    private String LOG_TAG = "BLE-demo";


    @Override
    public void onCreate(Bundle State){
        super.onCreate(State);
        setContentView(R.layout.scan_chs);

        Intent intent = getIntent();
        serviceUUID = UUID.fromString(intent.getStringExtra(EXTRA_SERVICE_UUID));
        serviceType = intent.getIntExtra(EXTRA_SERVICE_TYPE, BluetoothGattService.SERVICE_TYPE_PRIMARY);
        currentService = new BluetoothGattService(serviceUUID, serviceType);

        LeInfo leInfo = intent.getParcelableExtra(EXTRA_LE_INFO);
        if(leInfo != null) {
            ArrayCharacteristic = leInfo.getLeCHS();
        }

        if(ArrayCharacteristic.size() != 0){
            Log.i(LOG_TAG, "Receive data successful");
        }

        Log.i(LOG_TAG,"ServiceControlActivity: onCreate - closed");
    }

    @Override
    public void onResume(){
        super.onResume();

        recyclerView = findViewById(R.id.chs_view);
        LayoutInflater current_inflater = ServiceControlActivity.this.getLayoutInflater();
        recyclerCharacteristicAdapter = new RecyclerCharacteristicAdapter(current_inflater);
        recyclerView.setAdapter(recyclerCharacteristicAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        for(BluetoothGattCharacteristic CHS : ArrayCharacteristic){
            recyclerCharacteristicAdapter.addChs(CHS);
            recyclerCharacteristicAdapter.notifyDataSetChanged();
        }

    }


    /* Call dialog for set value for send message (notify || command) */
    public void showDialog(final View view) {
        String TAG_TX = "WRITE_CHS";
        String TAG_RX = "READ_CHS";
        final View iview =view;
        if(iview.getTag() == TAG_TX) {
            dialogFragment = new WriteMessageDialogFragment();
            dialogFragment.show(getSupportFragmentManager(), "WRITE");
            dialogFragment.setDismissListener(new WriteMessageDialogFragment.OnDismissListener() {
                @Override
                public void onDismiss(WriteMessageDialogFragment wmdf, byte[] Value) {
                    writeCHS(iview, Value);
                }
            });
        }else  if(iview.getTag() == TAG_RX){
            readCHS(iview);
        }
    }

    /*
     * Getting access to methods of ble service
     */
    public void writeCHS(View view, byte[] Value){
        Intent intent = new Intent(this, DeviceControlActivity.class);
        UUID CHS_UUID = ArrayCharacteristic.get(view.getId()).getUuid();
        byte[] bytes = Value;

        intent.putExtra(EXTRA_SEND_SERVICE_UUID, serviceUUID.toString());
        intent.putExtra(EXTRA_SEND_CHS_UUID, CHS_UUID.toString());
        intent.putExtra(EXTRA_SEND_BYTES, bytes);

        startActivityForResult(intent, REQUEST_CODE_WRITE);
    }

    public void readCHS(View view){
        Intent intent = new Intent(this, DeviceControlActivity.class);
        UUID CHS_UUID = ArrayCharacteristic.get(view.getId()).getUuid();

        intent.putExtra(EXTRA_SEND_SERVICE_UUID, serviceUUID.toString());
        intent.putExtra(EXTRA_SEND_CHS_UUID, CHS_UUID.toString());

        startActivityForResult(intent, REQUEST_CODE_READ);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode){
        intent.putExtra(EXTRA_REQUEST, requestCode);
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            Toast.makeText(this, "Operation success" ,Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Operation failed", Toast.LENGTH_SHORT).show();
        }
    }
}
