package com.kii.demo.cloudstorage.file;

import com.kii.cloud.storage.KiiClient;
import com.kii.cloud.storage.KiiUser;
import com.kii.demo.cloudstorage.R;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TextView;

public class FileTabActivity extends TabActivity{
    TabHost mTabHost;
    private TextView mRightTitle;
    private TextView mLeftTitle;
    
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.tab_layout);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
        
        mTabHost = getTabHost();
        setupTabs();
        mTabHost.setCurrentTab(0);
        
        mLeftTitle = (TextView) findViewById(R.id.left_text);
        mRightTitle = (TextView) findViewById(R.id.right_text);
        updateUserStateTitle();
        
    }

    private void updateUserStateTitle() {
        mLeftTitle.setText("FileManager");

        if (KiiClient.getCurrentUser() == null) {
            mRightTitle.setText("No User Login");
        } else {
            KiiUser user = KiiClient.getCurrentUser();
            mRightTitle.setText("Login:" + user.getUsername());
        }
    }

    private void setupTabs() {
        Intent intent = new Intent(this, WorkingFilesListActivity.class);
        mTabHost.addTab(mTabHost.newTabSpec("working")
                .setIndicator(getString(R.string.tab_work))
                .setContent(intent));
        intent = new Intent(this, TrashFilesListActivity.class);
        mTabHost.addTab(mTabHost.newTabSpec("trash")
                .setIndicator(getString(R.string.tab_trash)).setContent(intent));
    }
    
}
