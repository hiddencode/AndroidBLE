package com.example.androidble.dialogs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import androidx.fragment.app.DialogFragment;
import com.example.androidble.R;


/*
 *   TODO:   Transmit (WriteMessageDialogFragment & ReadMessageDialogFragment) into MessageDialogInterface
 *   mb unused
 */


public class ReadMessageDialogFragment extends DialogFragment {

    private OnDismissListener listener;
    public interface OnDismissListener{
        void onDismiss(ReadMessageDialogFragment wmdf, byte[] value);
    }
    public void setDismissListener(OnDismissListener listener){
        this.listener = listener;
    }

    public byte[] Value;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.send_title)
                .setView(inflater.inflate(R.layout.dialog_send, null))

                .setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Dialog dialogView = getDialog();
                        EditText value = dialogView.findViewById(R.id.text_value);
                        Value = value.getText().toString().getBytes();

                        /* value message
                            uuid
                            descriptor
                            (what data needs for record?)
                        */
                    }
                })

                //  TODO:   Processing dialogs
                //          Read characteristics
                //          Reactive android
                //

                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        /*
                            Exit in prev state
                         */
                    }
                });

        return builder.create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(listener != null){
            listener.onDismiss(this,Value);
        }
        Log.i("BLE-demo", "Dialog has been closed");
    }
}
