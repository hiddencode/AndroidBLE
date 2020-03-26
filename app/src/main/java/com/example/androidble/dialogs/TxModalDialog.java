package com.example.androidble.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.example.androidble.R;

public class TxModalDialog extends ModalDialog {

    private byte[] Value;

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
                    }
                })


                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        assert true;
                    }
                });

        return builder.create();
    }
}
