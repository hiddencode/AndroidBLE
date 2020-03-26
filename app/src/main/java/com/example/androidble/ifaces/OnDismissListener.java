package com.example.androidble.ifaces;

import com.example.androidble.dialogs.ModalDialog;

public interface OnDismissListener {
    void onDismiss(ModalDialog dialog, byte[] Value);
}
