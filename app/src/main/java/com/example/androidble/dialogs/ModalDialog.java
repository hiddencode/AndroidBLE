package com.example.androidble.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.androidble.R;
import com.example.androidble.ifaces.OnDismissListener;

public class ModalDialog extends DialogFragment {

    private OnDismissListener dismissListener;
    private byte[] Value;

    public void setDismissListener(OnDismissListener dismissListener){
        this.dismissListener = dismissListener;
    }




    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(dismissListener != null){
            dismissListener.onDismiss(this, Value);
        }
        Log.i("BLE-demo", "Dialog has been closed");
    }

}
