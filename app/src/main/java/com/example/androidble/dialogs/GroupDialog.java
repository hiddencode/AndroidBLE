package com.example.androidble.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.androidble.R;

import java.util.ArrayList;
import java.util.Arrays;

public class GroupDialog extends DialogFragment {


    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        builder.setMessage(R.string.send_title)
                .setView(inflater.inflate(R.layout.dialog_group, null))

                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // start mesh service for adding device
                    }
                })


                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        Dialog  dialog = getDialog();
        Spinner  spinner = dialog.findViewById(R.id.spin_group);
        // Init adapter for spinner
        ArrayList <String> arrayList = new ArrayList<>();
        arrayList.add("TAG: K");
        arrayList.add("TAG: B");
        arrayList.add("TAG: H");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(dialog.getContext(), android.R.layout.simple_spinner_item, arrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }
}
