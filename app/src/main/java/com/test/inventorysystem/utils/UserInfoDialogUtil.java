package com.test.inventorysystem.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.test.inventorysystem.R;
import com.test.inventorysystem.models.UserModel;

/**
 * Created by youmengli on 6/7/16.
 */

public class UserInfoDialogUtil extends DialogFragment {

    private TextView userAccount;
    private TextView userName;
    private TextView userDepartmentId;

    public static UserInfoDialogUtil newInstance(UserModel userModel) {

        Bundle args = new Bundle();
        args.putString("account", userModel.getAccounts());
        args.putString("username", userModel.getUsername());
        args.putString("departmentName", userModel.getDepartmentName());
        UserInfoDialogUtil frag = new UserInfoDialogUtil();
        frag.setArguments(args);

        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String account = getArguments().getString("account");
        String username = getArguments().getString("username");
        String departmentName = getArguments().getString("departmentName");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_user_info, null);
        Initialization(view);

        userAccount.setText(account);
        userName.setText(username);
        if (departmentName != null) {
            userDepartmentId.setText(departmentName);
        }

        // Use the Builder class for convenient dialog construction
        builder.setView(view)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void Initialization (View view) {
        userAccount = (TextView) view.findViewById(R.id.textView_dialog_user_account);
        userName = (TextView) view.findViewById(R.id.textView_dialog_user_name);
        userDepartmentId = (TextView) view.findViewById(R.id.textView_dialog_user_departmentName);
    }
}
