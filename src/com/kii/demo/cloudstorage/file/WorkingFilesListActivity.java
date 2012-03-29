package com.kii.demo.cloudstorage.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.kii.demo.cloudstorage.R;
import com.kii.demo.cloudstorage.activities.ConstantValues;
import com.kii.demo.cloudstorage.activities.ShowInfo;
import com.kii.demo.cloudstorage.api.KiiFileOperation;

public class WorkingFilesListActivity extends Activity {
    private ListView mListView;
    String[] mTitles;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workfilelist);

        mListView = (ListView) findViewById(R.id.listView1);
        mListView.setOnCreateContextMenuListener(this);

        if (ConstantValues.mSyncMode) {
            // KiiNoteOperation.getAllNote(this);
        } else {
            int token = KiiFileOperation.asyncListWorkingFiles(this);
            ShowInfo.showProcessing(this, token, "Getting working files...");
        }
    }

    public void handleRefresh(View v) {
        if (ConstantValues.mSyncMode) {
            // KiiNoteOperation.getAllNote(this);
        } else {
            int token = KiiFileOperation.asyncListWorkingFiles(this);
            ShowInfo.showProcessing(this, token, "Getting working files...");
        }
    }

    public void handleUpload(View v) {
        File external = Environment.getExternalStorageDirectory();
        final String root_path = external.getAbsolutePath();
        File[] files = external.listFiles();
        final List<String> filenames = new ArrayList<String>();

        for (File file : files) {
            if (file.isFile() && !file.isHidden()) {
                String path = file.getAbsolutePath();
                filenames.add(path.substring(path.lastIndexOf("/") + 1));
            }
        }

        String[] titles = new String[filenames.size()];
        titles = filenames.toArray(titles);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Select a file to upload")
                .setItems(titles, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String filename = root_path + "/"
                                + filenames.get(which);
                        int token = KiiFileOperation.asyncUploadFile(
                                WorkingFilesListActivity.this, filename);
                        ShowInfo.showProcessing(WorkingFilesListActivity.this,
                                token, "Uploading file...");
                    }
                }).create();

        dialog.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        menu.add(0, 0, 0, "Update Mime");
        menu.add(0, 1, 0, "Update Body");
        menu.add(0, 2, 0, "Download Body");
        menu.add(0, 3, 0, "Move to trash");
        menu.add(0, 4, 0, "Delete");
        
        super.onCreateContextMenu(menu, v, menuInfo);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();
        final int position = info.position;
        
        if (item.getItemId() == 3) { // Move to trash
            
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setIcon(R.drawable.alert_dialog_icon)
                    .setTitle("Make sure to move this note to trash!")
                    .setPositiveButton(R.string.alert_dialog_ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int whichButton) {
                                    int token = KiiFileOperation.asyncMoveToTrash(WorkingFilesListActivity.this, position);
                                    ShowInfo.showProcessing(WorkingFilesListActivity.this,
                                            token, "Moving file to trash...");
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
        mTitles = newTitles;
        mListView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mTitles));
    }
}
