package com.kii.demo.cloudstorage.activities;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.kii.cloud.storage.KiiClient;
import com.kii.cloud.storage.KiiUser;
import com.kii.demo.cloudstorage.R;
import com.kii.demo.cloudstorage.api.KiiUserOperation;

/**
 * 
 * Activity to handle user related operations, including login/logout, create a user and set/get user information.
 *
 */
public class UserOperationActivity extends Activity implements
        OnItemClickListener {
    private TextView mRightTitle;
    private TextView mLeftTitle;


    private java.text.DateFormat mDateFormat;

    private static final int DIALOG_UPDATE_PHONE = 0;
    private static final int DIALOG_RESET_PWD = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.apidemo);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

        ListView list = (ListView) findViewById(R.id.listView1);
        list.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, ConstantValues.USER_ITEMS));
        list.setOnItemClickListener(this);

        mLeftTitle = (TextView) findViewById(R.id.left_text);
        mRightTitle = (TextView) findViewById(R.id.right_text);

        mDateFormat = DateFormat.getLongDateFormat(this);
        updateUserStateTitle();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int position,
            long id) {
        switch (position) {
            case ConstantValues.ITEM_USER_SHOW_INFO:
                showUserInfo();
                break;
            case ConstantValues.ITEM_USER_SET_PHONE:
                showDialog(DIALOG_UPDATE_PHONE);
                break;
            case ConstantValues.ITEM_USER_SET_BIRTHDAY:
                showBirthDayDialog();
                break;
            case ConstantValues.ITEM_USER_FORGOT_PWD:
                String uri = KiiUserOperation.getResetPasswordUrl();
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(uri));
                startActivity(webIntent);
                break;
            case ConstantValues.ITEM_USER_CHANGE_PWD:
                showDialog(DIALOG_RESET_PWD);
                break;
        }

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        LayoutInflater factory = LayoutInflater.from(this);
        switch (id) {
            case DIALOG_UPDATE_PHONE:
                return dialogUpdatePhone(factory);
            case DIALOG_RESET_PWD:
                return dialogChangePwd(factory);

        }
        return null;
    }

    private void showBirthDayDialog() {

        Calendar c = Calendar.getInstance();
        KiiUser user = KiiClient.getCurrentUser();
        long birthday = user.getLong(ConstantValues.FIELD_USER_BOD, 0);
        if (birthday > 0) {
            c.setTimeInMillis(birthday);
        }

        DatePickerDialog dialog = new DatePickerDialog(this,
                mBirthdayDateSetListener, c.get(Calendar.YEAR),
                c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private DatePickerDialog.OnDateSetListener mBirthdayDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                int dayOfMonth) {

            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, monthOfYear);
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            long time = c.getTimeInMillis();

            if (ConstantValues.mSyncMode) {
                KiiUserOperation.updateUserBirthday(UserOperationActivity.this, time);
            } else {
                int token = KiiUserOperation.asyncUpdateUserBirthday(UserOperationActivity.this, time);
                ShowInfo.showProcessing(UserOperationActivity.this, token, "Updating user's Birthday info");
            }
        }
    };

    public void updateUserStateTitle() {
        if (ConstantValues.mSyncMode) {
            mLeftTitle.setText(R.string.sync_title);
        } else {
            mLeftTitle.setText(R.string.async_title);
        }

        if (KiiClient.getCurrentUser() == null) {
            mRightTitle.setText("No User Login");
        } else {
            KiiUser user = KiiClient.getCurrentUser();
            mRightTitle.setText("Login:" + user.getUsername());
        }
    }

    private Dialog dialogChangePwd(LayoutInflater factory) {
        final View textEntryView = factory.inflate(
                R.layout.alert_dialog_user_change_pwd, null);
        final TextView oldPwdView = (TextView) textEntryView
                .findViewById(R.id.old_password_edit);

        final TextView newPwdView = (TextView) textEntryView
                .findViewById(R.id.new_password_edit);

        return new AlertDialog.Builder(this)
                .setIcon(R.drawable.alert_dialog_icon)
                .setTitle("Create")
                .setView(textEntryView)
                .setPositiveButton(R.string.alert_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                String old_pwd = oldPwdView.getText()
                                        .toString();
                                String new_pwd = newPwdView.getText()
                                        .toString();

                                if (ConstantValues.mSyncMode) {
                                    KiiUserOperation.changePwd(UserOperationActivity.this, old_pwd, new_pwd);
                                } else {
                                    int token = KiiUserOperation.asyncchangePwd(UserOperationActivity.this, old_pwd, new_pwd);
                                    if (token >= 0) {
                                        ShowInfo.showProcessing(UserOperationActivity.this, token,
                                                "Changing Password");
                                    }
                                }

                            }
                        })
                .setNegativeButton(R.string.alert_dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {

                                /* User clicked cancel so do some stuff */
                            }
                        }).create();
    }

    private Dialog dialogUpdatePhone(LayoutInflater factory) {

        final View textEntryView = factory.inflate(
                R.layout.alert_dialog_user_phone, null);
        final TextView phoneView = (TextView) textEntryView
                .findViewById(R.id.phone_edit);
        phoneView.setText(KiiClient.getCurrentUser().getString(
                ConstantValues.FIELD_USER_PHONE, null));

        return new AlertDialog.Builder(this)
                .setIcon(R.drawable.alert_dialog_icon)
                .setTitle("Update")
                .setView(textEntryView)
                .setPositiveButton(R.string.alert_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                String phone = phoneView.getText().toString();

                                if (ConstantValues.mSyncMode) {
                                    KiiUserOperation.updateUserPhone(UserOperationActivity.this, phone);
                                } else {
                                    int token = KiiUserOperation.asyncUpdateUserPhone(UserOperationActivity.this, phone);
                                    ShowInfo.showProcessing(UserOperationActivity.this, token,
                                            "Updating user's phone info");
                                }

                            }

                        })
                .setNegativeButton(R.string.alert_dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {

                                /* User clicked cancel so do some stuff */
                            }
                        }).create();
    }


    private void showUserInfo() {
        String title = "User Info";
        StringBuilder sb = new StringBuilder();
        KiiUser user = KiiClient.getCurrentUser();

        sb.append("User Name: " + user.getUsername());
        sb.append("\n\n");
        sb.append("Email: " + user.getEmail());
        sb.append("\n\n");
        String value = user.getString(ConstantValues.FIELD_USER_PHONE, "");
        if (value != null) {
            sb.append("Phone: " + value);

        }

        long time = user.getLong(ConstantValues.FIELD_USER_BOD, 0);
        if (time > 0) {
            sb.append("\n\n");
            Date d = new Date(time);
            sb.append("Birthday: " + mDateFormat.format(d));
        }
        String msg = sb.toString();
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setIcon(R.drawable.alert_dialog_icon)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(R.string.alert_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {

                                /* User clicked OK so do some stuff */
                            }
                        }).create();

        dialog.show();

    }
}
