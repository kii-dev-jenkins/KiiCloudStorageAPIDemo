package com.kii.demo.cloudstorage.file;

import com.kii.demo.cloudstorage.R;
import com.kii.demo.cloudstorage.activities.ConstantValues;
import com.kii.demo.cloudstorage.activities.ShowInfo;
import com.kii.demo.cloudstorage.api.KiiFileOperation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class TrashFilesListActivity extends Activity {
    private ListView mListView;
    String[] mTitles;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trashfilelist);

        mListView = (ListView) findViewById(R.id.listView1);
        mListView.setOnCreateContextMenuListener(this);

        if (ConstantValues.mSyncMode) {
            // KiiNoteOperation.getAllNote(this);
        } else {
            int token = KiiFileOperation.asyncListTrashFiles(this);
            ShowInfo.showProcessing(this, token, "Getting trash files...");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        rebuildTitle(KiiFileOperation.getTrashFileTitles());
    }

    public void handleRefresh(View v) {
        if (ConstantValues.mSyncMode) {
            // KiiNoteOperation.getAllNote(this);
        } else {
            int token = KiiFileOperation.asyncListTrashFiles(this);
            ShowInfo.showProcessing(this, token, "Getting trash files...");
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        menu.add(0, 0, 0, "Restore");
        menu.add(0, 1, 0, "Delete");

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();
        final int position = info.position;

        if (item.getItemId() == 0) { // restore

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setIcon(R.drawable.alert_dialog_icon)
                    .setTitle("Make sure to move this file to trash!")
                    .setPositiveButton(R.string.alert_dialog_ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int whichButton) {
                                    int token = KiiFileOperation.asyncRestoreFile(TrashFilesListActivity.this, position);

                                    ShowInfo.showProcessing(
                                            TrashFilesListActivity.this,
                                            token, "Restoring file from trash...");
                                }
                            })
                    .setNegativeButton(R.string.alert_dialog_cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int whichButton) {
                                }
                            }).create();
            dialog.show();
            return true;
        } else if (item.getItemId() == 1) { // delete
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setIcon(R.drawable.alert_dialog_icon)
                    .setTitle("Make sure to delete this file!")
                    .setPositiveButton(R.string.alert_dialog_ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int whichButton) {
                                    int token = KiiFileOperation
                                            .asyncDeleteTrashFile(
                                                    TrashFilesListActivity.this,
                                                    position);

                                    ShowInfo.showProcessing(
                                            TrashFilesListActivity.this, token,
                                            "Deleting file...");
                                }
                            })
                    .setNegativeButton(R.string.alert_dialog_cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int whichButton) {
                                }
                            }).create();
            dialog.show();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    public void rebuildTitle(String[] newTitles) {
        if (newTitles == null)
            return;
        mTitles = newTitles;
        mListView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mTitles));
    }
}
