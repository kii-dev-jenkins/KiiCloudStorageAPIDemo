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

package com.kii.demo.cloudstorage.notepad;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.kii.cloud.storage.KiiClient;
import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.query.KQExp;
import com.kii.cloud.storage.query.KQWhere;
import com.kii.cloud.storage.query.KiiQuery;
import com.kii.cloud.storage.utils.Utils;
import com.kii.demo.cloudstorage.R;
import com.kii.demo.cloudstorage.activities.ConstantValues;
import com.kii.demo.cloudstorage.activities.ShowInfo;
import com.kii.demo.cloudstorage.api.KiiNoteOperation;

/**
 * 
 * Activity to handle notes operations
 *
 */
public class NoteItemListActivity extends Activity implements
        OnItemClickListener {
    //private static final String TAG = "NoteItemListActivity";
    private TextView mRightTitle;
    private TextView mLeftTitle;

    String[] mTitles;

    int mLimit;
    private ListView mListView;

    private String mOldContent;
    private int mChangePosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.notelist);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

        mListView = (ListView) findViewById(R.id.listView1);
        mListView.setOnCreateContextMenuListener(this);
        mListView.setOnItemClickListener(this);

        mLeftTitle = (TextView) findViewById(R.id.left_text);
        mRightTitle = (TextView) findViewById(R.id.right_text);
        
        updateUserStateTitle();
        
        if (ConstantValues.mSyncMode) {
            KiiNoteOperation.getAllNote(this);
        } else {
            int token = KiiNoteOperation.asyncGetAllNote(this);
            if (token > 0) {
                ShowInfo.showProcessing(this, token, "Getting note");
            }
        }
    }
    
    

    @Override
    protected void onResume() {
        super.onResume();
        

    }



    public void handleCreate(View v) {
        Intent intent = new Intent(this, NoteEditor.class);
        intent.setAction(Intent.ACTION_INSERT);
        this.startActivityForResult(intent, 1);
    }

    public void handleRefresh(View v) {
        int token = KiiNoteOperation.asyncGetAllNote(this);
        if (token > 0) {
            ShowInfo.showProcessing(this, token, "Getting note");
        }
    }

    public void handleSearch(View v) {
        LayoutInflater factory = LayoutInflater.from(this);

        final View textEntryView = factory.inflate(
                R.layout.alert_dialog_note_query, null);
        final TextView titleView = (TextView) textEntryView
                .findViewById(R.id.title_query_edit);
        titleView.setText(ConstantValues.DEFAULT_NOTE_TITLE_QUERY);

        final TextView contentView = (TextView) textEntryView
                .findViewById(R.id.content_query_edit);
        contentView.setText(ConstantValues.DEFAULT_NOTE_CONTENT_QUERY);
        
        final TextView limitView = (TextView) textEntryView
                .findViewById(R.id.limit_edit);
        limitView.setText("" + ConstantValues.DEFAULT_NOTE_QUERY_LIMIT);

        final CheckBox checkbox = (CheckBox) textEntryView
                .findViewById(R.id.checkBox1);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setIcon(R.drawable.alert_dialog_icon)
                .setTitle("Search")
                .setView(textEntryView)
                .setPositiveButton(R.string.alert_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {

                                KiiQuery query = new KiiQuery();
                                String title = titleView.getText().toString();
                                String content = contentView.getText().toString();
                                KQWhere where = new KQWhere(KQExp.contains(KiiNoteOperation.PROPERTY_TITLE, title));
                                if(!Utils.isEmpty(content)){
                                    where.and(KQExp.contains(KiiNoteOperation.PROPERTY_CONTENT, content));
                                }
                                
                                if (checkbox.isChecked()) {
                                    String creator = KiiClient.getCurrentUser().toUri().toString();
                                    where.and(KQExp.equals(ConstantValues.FIELDNAME_CREATOR, creator));
                                }

                                query.setWhere(where);
                                String limit_str = limitView.getText()
                                        .toString();
                                int limit = Integer.parseInt(limit_str);
                                query.setLimit(limit);

                                int token = KiiNoteOperation.asyncQueryNote(NoteItemListActivity.this, query);
                                if (token >= 0) {
                                    ShowInfo.showProcessing(NoteItemListActivity.this,token, "Querying note");
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
        
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            if (data.getAction().equals(Intent.ACTION_INSERT)) {
                String title = data.getStringExtra("title");
                String content = data.getStringExtra("body");
                int token = KiiNoteOperation.asyncCreateNote(this, title, content);
                if (token >= 0) {
                    ShowInfo.showProcessing(this, token, "Creating note");
                }
            } else if (data.getAction().equals(Intent.ACTION_EDIT)) {
                String content = data.getStringExtra("body");
                if (!content.equals(mOldContent)) {
                    int token = KiiNoteOperation.asyncUpdateNote(this, mChangePosition, content);
                    ShowInfo.showProcessing(this, token, "Updating note");
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void rebuildTitle(String [] newTitles) {
        mTitles = newTitles;
        mListView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mTitles));
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();

        if (item.getItemId() == 0) { // delete
            final int position = info.position;
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setIcon(R.drawable.alert_dialog_icon)
                    .setTitle("Make sure to delete this note!")
                    .setPositiveButton(R.string.alert_dialog_ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int whichButton) {

                                    int token = KiiNoteOperation.asyncDeleteNote(NoteItemListActivity.this, position);
                                    ShowInfo.showProcessing(NoteItemListActivity.this, token, "Deleting note");
                                }
                            })
                    .setNegativeButton(R.string.alert_dialog_cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int whichButton) {

                                    /* User clicked Cancel so do some stuff */
                                }
                            }).create();
            dialog.show();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private boolean isMorePosition(int position) {
        return KiiNoteOperation.isMore;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        if (isMorePosition(info.position)) {
            return;
        }
        menu.add(0, 0, 0, "Delete");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    private void updateUserStateTitle() {
        // if (ConstantValues.mSyncMode) {
        // mLeftTitle.setText(R.string.sync_title);
        // } else {
        // mLeftTitle.setText(R.string.async_title);
        // }

        mLeftTitle.setText("Notepad");

        if (KiiClient.getCurrentUser() == null) {
            mRightTitle.setText("No User Login");
        } else {
            KiiUser user = KiiClient.getCurrentUser();
            mRightTitle.setText("Login:" + user.getUsername());
        }
    }
    
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
            long arg3) {
        if (isMorePosition(position)) {
            getMoreData();
            return;
        }

        KiiObject note = KiiNoteOperation.mObjects.get(position);
        mChangePosition = position;
        mOldContent = note.getString(KiiNoteOperation.PROPERTY_CONTENT, "");
        Intent intent = new Intent(this, NoteEditor.class);
        intent.setAction(Intent.ACTION_EDIT);
        intent.putExtra("title", note.getString(KiiNoteOperation.PROPERTY_TITLE, ""));
        intent.putExtra("body", mOldContent);

        this.startActivityForResult(intent, 1);

    }

    private void getMoreData() {
        int token = KiiNoteOperation.asyncQueryMore(this);
        ShowInfo.showProcessing(this, token, "Getting more notes");

    }

    

}
