package com.example.mackayirb.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.example.mackayirb.R;

public class PermissionAgreeFragment extends DialogFragment {

    String message = "";
    String[] permission_list;
    int requestCode = 0;
    boolean cancellable = true;

    /**
     * Use {@link DialogFragment}} to create request permission dialogs.
     * @param Message - AlertDialog Message
     * @param PermissionList - Requested Permissions
     * @param RequestCode - RequestCode
     */
    public PermissionAgreeFragment(@NonNull String Message, @NonNull String[] PermissionList, int RequestCode, boolean Cancellable) {
        message = Message;
        permission_list = PermissionList;
        requestCode = RequestCode;
        cancellable = Cancellable;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
            .setPositiveButton(
                getResources().getString(android.R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(
                            getActivity(),
                                permission_list,
                            requestCode
                        );
                        dismiss();
                    }
                }
            );
        if(cancellable) {
            builder.setNegativeButton(
                    getResources().getString(android.R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dismiss();
                        }
                    }
            );
        }

        return builder.create();
    }

}
