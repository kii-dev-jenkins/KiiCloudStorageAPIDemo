package com.kii.demo.cloudstorage.file;

import com.kii.demo.cloudstorage.R;
import com.kii.demo.cloudstorage.activities.ConstantValues;
import com.kii.demo.cloudstorage.activities.ShowInfo;
import com.kii.demo.cloudstorage.api.KiiFileOperation;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class TrashFilesListActivity extends Activity{
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
    
    public void rebuildTitle(String[] newTitles) {
        mTitles = newTitles;
        mListView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mTitles));
    }
}