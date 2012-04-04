//
//
//  Copyright 2012 Kii Corporation
//  http://kii.com
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//  
//

package com.kii.demo.cloudstorage.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kii.cloud.storage.KiiFile;
import com.kii.demo.cloudstorage.R;
import com.kii.demo.cloudstorage.activities.ConstantValues;
import com.kii.demo.cloudstorage.activities.ShowInfo;
import com.kii.demo.cloudstorage.api.KiiFileOperation;

public class WorkingFilesListActivity extends Activity {
    private ListView mListView;
    String[] mTitles;

    private java.text.DateFormat mDateFormat;

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

        mDateFormat = DateFormat.getTimeFormat(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        rebuildTitle(KiiFileOperation.getWorkingFileTitles());
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
        menu.add(0, 0, 0, "Update Custom Field");
        menu.add(0, 1, 0, "Update Body");
        menu.add(0, 2, 0, "Download Body");
        menu.add(0, 3, 0, "Move to trash");
        menu.add(0, 4, 0, "Delete");
        menu.add(0, 5, 0, "Info");

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();
        final int position = info.position;

        int itemid = item.getItemId();
        if (itemid == 3) { // Move to trash

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setIcon(R.drawable.alert_dialog_icon)
                    .setTitle("Make sure to move this file to trash!")
                    .setPositiveButton(R.string.alert_dialog_ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int whichButton) {
                                    int token = KiiFileOperation
                                            .asyncMoveToTrash(
                                                    WorkingFilesListActivity.this,
                                                    position);
                                    ShowInfo.showProcessing(
                                            WorkingFilesListActivity.this,
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
        } else if (itemid == 4) { // delete
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setIcon(R.drawable.alert_dialog_icon)
                    .setTitle("Make sure to delete this file!")
                    .setPositiveButton(R.string.alert_dialog_ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int whichButton) {
                                    int token = KiiFileOperation
                                            .asyncMoveToTrash(
                                                    WorkingFilesListActivity.this,
                                                    position);
                                    ShowInfo.showProcessing(
                                            WorkingFilesListActivity.this,
                                            token, "Deleting file...");
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
        } else if (itemid == 2) { // download
            LayoutInflater factory = LayoutInflater.from(this);
            final View textEntryView = factory.inflate(
                    R.layout.alert_dialog_download, null);
            final TextView filenameView = (TextView) textEntryView
                    .findViewById(R.id.id_edit);
            filenameView.setText(ConstantValues.TARGET_FILE_NAME);
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setIcon(R.drawable.alert_dialog_icon)
                    .setTitle("Download")
                    .setView(textEntryView)
                    .setPositiveButton(R.string.alert_dialog_ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int whichButton) {
                                    String root = Environment
                                            .getExternalStorageDirectory()
                                            .toString();
                                    String filename = root + "/"
                                            + filenameView.getText();
                                    int token = KiiFileOperation
                                            .asyncDownloadFile(
                                                    WorkingFilesListActivity.this,
                                                    position, filename);
                                    ShowInfo.showProcessing(
                                            WorkingFilesListActivity.this,
                                            token, "Dowloading file...");

                                }
                            })
                    .setNegativeButton(R.string.alert_dialog_cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int whichButton) {

                                }
                            }).create();

            dialog.show();

        } else if (itemid == 5) {
            String title = "File Info";
            StringBuilder sb = new StringBuilder();
            KiiFile f = KiiFileOperation.mWorkingFiles.get(position);

            sb.append("Title: " + f.getTitle());
            sb.append("\n\n");

            sb.append("MimeType: " + f.getMimeType());
            sb.append("\n\n");

            sb.append("RemotePath: " + f.getRemotePath());
            sb.append("\n\n");

            sb.append("File size: " + f.getFileSize());
            sb.append("\n\n");

            sb.append("CustomField: " + f.getCustomField());
            sb.append("\n\n");

            long time = f.getModifedTime();
            if (time > 0) {
                Date d = new Date(time);
                sb.append("Modify Time: " + mDateFormat.format(d));
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
        } else if (itemid == 0) {
            LayoutInflater factory = LayoutInflater.from(this);
            final View textEntryView = factory.inflate(
                    R.layout.alert_dialog_file_info, null);
            final TextView customView = (TextView) textEntryView
                    .findViewById(R.id.id_edit);
            
            KiiFile f = KiiFileOperation.mWorkingFiles.get(position);
            final String custom = f.getCustomField();
            if(custom != null){
                customView.setText(custom);
            }
            
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setIcon(R.drawable.alert_dialog_icon)
                    .setTitle("Change custom field")
                    .setView(textEntryView)
                    .setPositiveButton(R.string.alert_dialog_ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int whichButton) {
                                    String newCustom = customView.getText().toString();
                                    if(!TextUtils.isEmpty(newCustom) && !newCustom.equals(custom)){
                                        int token = KiiFileOperation
                                                .asyncUpdateFileCustomField(
                                                        WorkingFilesListActivity.this,
                                                        position, newCustom);
                                        ShowInfo.showProcessing(
                                                WorkingFilesListActivity.this,
                                                token, "Updating file custom field...");
                                    }
                                    

                                }
                            })
                    .setNegativeButton(R.string.alert_dialog_cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int whichButton) {

                                }
                            }).create();

            dialog.show();
        } else if(itemid == 1){
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
                    .setTitle("Select a file to update")
                    .setItems(titles, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String filename = root_path + "/"
                                    + filenames.get(which);
                            int token = KiiFileOperation.asyncUpdateFile(
                                    WorkingFilesListActivity.this, position, filename);
                            ShowInfo.showProcessing(WorkingFilesListActivity.this,
                                    token, "Updating file...");
                        }
                    }).create();

            dialog.show();
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
