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


    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.send_title)
                .setView(inflater.inflate(R.layout.dialog_send, null))

                .setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Dialog dialogView = getDialog();
                        assert dialogView != null;
                        EditText value = dialogView.findViewById(R.id.text_value);
                        Value = value.getText().toString().getBytes();
                    }
                })


                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        assert true;
                    }
                });

        return builder.create();
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
