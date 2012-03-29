package com.kii.demo.cloudstorage.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kii.cloud.storage.KiiClient;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.callback.KiiUserCallBack;
import com.kii.demo.cloudstorage.R;
import com.kii.demo.cloudstorage.api.KiiUserOperation;
import com.kii.demo.cloudstorage.notepad.NoteItemListActivity;

/**
 * The purpose of this application is to provide to Android developers a simple
 * application that allows to learn the usage of the Cloud Storage API and
 * quickly test the operations provided. The API capabilities that can be tested
 * are: User operations {@see UserOperationActiviy)} Create a user Login Logout
 * Set user birthday Change password Forgot password Update user phone Show user
 * information Note operations {@see
 * com.kii.demo.cloudstorage.notepad.NoteItemListActivity} Create a note View a
 * note View note list Refresh Search for a note File operations {@see
 * FileOperationActivity} Upload a file List files View file information Update
 * file information Download the file (get the file content) Delete a file
 * Restore a file List files in trash Delete a file from trash Empty trashcan
 */
public class StartActivity extends Activity implements OnItemClickListener {

    private TextView mRightTitle;
    private TextView mLeftTitle;

    private static final int DIALOG_CREATE_USER = 0;
    private static final int DIALOG_LOGIN = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.apidemo);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

        ListView list = (ListView) findViewById(R.id.listView1);
        list.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, ConstantValues.ITEMS));
        list.setOnItemClickListener(this);

        mLeftTitle = (TextView) findViewById(R.id.left_text);
        mRightTitle = (TextView) findViewById(R.id.right_text);

        updateUserStateTitle();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
            long arg3) {
        switch (position) {
            case ConstantValues.ITEM_CREATE_USER:
                this.showDialog(DIALOG_CREATE_USER);
                break;
            case ConstantValues.ITEM_LOGIN:
                this.showDialog(DIALOG_LOGIN);
                break;
            case ConstantValues.ITEM_OPERATION_NOTES:
                if (alertNoUserLogin())
                    return;
                Intent intent3 = new Intent(this, NoteItemListActivity.class);
                startActivity(intent3);
                break;

            case ConstantValues.ITEM_OPERATION_USER:
                if (alertNoUserLogin())
                    return;
                Intent intent2 = new Intent(this, UserOperationActivity.class);
                startActivity(intent2);
                break;

            case ConstantValues.ITEM_LOGOUT:
                KiiClient.logOut();
                updateUserStateTitle();
                break;

            case ConstantValues.ITEM_OPERATON_FILES:
                Intent intent = new Intent(this, FileOperationActivity.class);
                startActivity(intent);
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Change Mode");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            ConstantValues.mSyncMode = !ConstantValues.mSyncMode;

            if (ConstantValues.mSyncMode) {
                Toast.makeText(this, "Change to sync mode", Toast.LENGTH_SHORT);
            } else {
                Toast.makeText(this, "Change to async mode", Toast.LENGTH_SHORT);
            }
            updateUserStateTitle();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        LayoutInflater factory = LayoutInflater.from(this);
        switch (id) {
            case DIALOG_CREATE_USER:
                return dialogCreateUser(factory);
            case DIALOG_LOGIN:
                return dialogLogin(factory);

        }
        return null;

    }

    private Dialog dialogCreateUser(LayoutInflater factory) {
        final View textEntryView = factory.inflate(
                R.layout.alert_dialog_user_entry, null);
        final TextView usernameView = (TextView) textEntryView
                .findViewById(R.id.username_edit);
        usernameView.setText(ConstantValues.DEFAULT_USER);

        final TextView pwdView = (TextView) textEntryView
                .findViewById(R.id.password_edit);
        pwdView.setText(ConstantValues.DEFAULT_PWD);

        final TextView emailView = (TextView) textEntryView
                .findViewById(R.id.email_edit);
        emailView.setText(ConstantValues.DEFAULT_EMAIL);

        final TextView phoneView = (TextView) textEntryView
                .findViewById(R.id.phone_edit);
        phoneView.setText(ConstantValues.DEFAULT_PHONE);

        return new AlertDialog.Builder(StartActivity.this)
                .setIcon(R.drawable.alert_dialog_icon)
                .setTitle(R.string.create_user)
                .setView(textEntryView)
                .setPositiveButton(R.string.alert_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                String username = usernameView.getText()
                                        .toString();
                                String pwd = pwdView.getText().toString();
                                String email = emailView.getText().toString();
                                String phone = phoneView.getText().toString();

                                KiiUser user = new KiiUser();
                                user.setEmail(email);
                                user.set(ConstantValues.FIELD_USER_PHONE, phone);

                                if (ConstantValues.mSyncMode) {
                                    KiiUserOperation.createUser(
                                            StartActivity.this, user, username,
                                            pwd);
                                } else {

                                    int token = KiiUserOperation
                                            .asyncCreateUser(
                                                    StartActivity.this, user,
                                                    username, pwd);
                                    ShowInfo.showProcessing(StartActivity.this,
                                            token, "Creating User");
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

    private Dialog dialogLogin(LayoutInflater factory) {
        final View textEntryView = factory.inflate(
                R.layout.alert_dialog_user_login, null);
        final TextView usernameView = (TextView) textEntryView
                .findViewById(R.id.username_edit);
        usernameView.setText(ConstantValues.DEFAULT_USER);

        final TextView pwdView = (TextView) textEntryView
                .findViewById(R.id.password_edit);
        pwdView.setText(ConstantValues.DEFAULT_PWD);

        return new AlertDialog.Builder(StartActivity.this)
                .setIcon(R.drawable.alert_dialog_icon)
                .setTitle(R.string.login)
                .setView(textEntryView)
                .setPositiveButton(R.string.alert_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                String username = usernameView.getText()
                                        .toString();
                                String pwd = pwdView.getText().toString();

                                if (ConstantValues.mSyncMode) {
                                    KiiUserOperation.userLogin(
                                            StartActivity.this, username, pwd);
                                } else {
                                    int token = KiiUserOperation
                                            .asyncUserLogin(StartActivity.this,
                                                    username, pwd);
                                    ShowInfo.showProcessing(StartActivity.this,
                                            token, "User Logining");
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

    private boolean alertNoUserLogin() {
        if (KiiClient.getCurrentUser() == null) {
            Toast.makeText(this, "Please Login first!", Toast.LENGTH_SHORT)
                    .show();
            return true;
        }
        return false;
    }
}