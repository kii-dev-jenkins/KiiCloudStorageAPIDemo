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

package com.kii.demo.cloudstorage.api;

import java.io.File;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.kii.cloud.storage.KiiClient;
import com.kii.cloud.storage.KiiFile;
import com.kii.cloud.storage.callback.KiiFileCallBack;
import com.kii.cloud.storage.callback.KiiFileProgress;
import com.kii.demo.cloudstorage.activities.ShowInfo;
import com.kii.demo.cloudstorage.file.TrashFilesListActivity;
import com.kii.demo.cloudstorage.file.WorkingFilesListActivity;

public class KiiFileOperation {

    static {
    	// Initialize KiiClient SDK with Application ID and Key
        KiiClient.initialize(AppInfo.APP_ID, AppInfo.APP_KEY, AppInfo.BASE_URL);
    }
    
    // file container's name
    private static final String FILE_CONTAINER = "demo_file";

    // cache working files
    public static List<KiiFile> mWorkingFiles;

    // cache trashed files
    public static List<KiiFile> mTrashedFiles;

    /**
     * Get list of working files from Kii Cloud
     */
    public static int asyncListWorkingFiles(
            final WorkingFilesListActivity activity) {
        int token = KiiFile.listWorkingFiles(new KiiFileCallBack() {

        	// implement the call back on list working files completed
            @Override
            public void onListWorkingCompleted(int mToken, boolean success,
                    List<KiiFile> files, Exception exception) {
            	// close the progress dialog
                ShowInfo.closeProgressDialog();
                if (success) {
                	// update working files
                    mWorkingFiles = files;
                    // update the UI
                    activity.rebuildTitle(getWorkingFileTitles());
                } else {
                	// show error message
                    ShowInfo.showException(activity, exception);
                }
            }
        }, FILE_CONTAINER);

        return token;
    }

    /**
     * Get list of trashed files from Kii Cloud
     */
    public static int asyncListTrashFiles(final TrashFilesListActivity activity) {
        int token = KiiFile.listTrashedFiles(new KiiFileCallBack() {

        	// implement the call back on list trashed files completed
            @Override
            public void onListTrashCompleted(int mToken, boolean success,
                    List<KiiFile> files, Exception exception) {
            	// close progress dialog 
                ShowInfo.closeProgressDialog();
                if (success) {
                	// updated trashed files
                    mTrashedFiles = files;
                    // update the UI
                    activity.rebuildTitle(getTrashFileTitles());
                } else {
                	// show error message
                    ShowInfo.showException(activity, exception);
                }
            }

        }, FILE_CONTAINER);
        return token;
    }

    /**
     * Delete working file from Kii Cloud
     */
    public static int asyncDeleteWorkingFile(
            final WorkingFilesListActivity activity, final int position) {
        KiiFile file = mWorkingFiles.get(position);
        int token = file.delete(new KiiFileCallBack() {

        	// implement the call back on delete working file completed 
            @Override
            public void onDeleteCompleted(int token, boolean success,
                    Exception exception) {
            	// close progress dialog
                ShowInfo.closeProgressDialog();
                if (success) {
                	// remove the file from cache
                    mWorkingFiles.remove(position);
                    // update the UI
                    activity.rebuildTitle(getTrashFileTitles());
                } else {
                	// show error message                	
                    ShowInfo.showException(activity, exception);
                }
            }
        });
        return token;
    }

    /**
     * Delete trashed file from Kii Cloud
     */
    public static int asyncDeleteTrashedFile(
            final TrashFilesListActivity activity, final int position) {
        KiiFile file = mTrashedFiles.get(position);
        int token = file.delete(new KiiFileCallBack() {

        	// implement the call back on delete trashed file completed
            @Override
            public void onDeleteCompleted(int token, boolean success,
                    Exception exception) {
            	// close progress dialog
                ShowInfo.closeProgressDialog();
                if (success) {
                	// remove the trashed file from cache
                    mTrashedFiles.remove(position);
                    // update UI
                    activity.rebuildTitle(getWorkingFileTitles());
                } else {
                	// show error message
                    ShowInfo.showException(activity, exception);
                }
            }
        });
        return token;
    }

    /**
     * Upload a file to Kii Cloud 
     */
    public static int asyncUploadFile(final WorkingFilesListActivity activity,
            String filename) {
    	// Create KiiFile by local file
        KiiFile f = new KiiFile(new File(filename), FILE_CONTAINER);
        int token = f.upload(new KiiFileCallBack() {
        	// implement the call back on upload file completed
            @Override
            public void onUploadCompleted(int token, boolean success,
                    KiiFile file, Exception exception) {
            	// close progress dialog
                ShowInfo.closeProgressDialog();
                if (success) {
                	// add the file to cache
                    mWorkingFiles.add(0, file);
                    // update UI
                    activity.rebuildTitle(getWorkingFileTitles());
                } else {
                	// show error message
                    ShowInfo.showException(activity, exception);
                }
            }

            // implement the call back for updating upload progress
            @Override
            public void onProgressUpdate(int token, KiiFileProgress progress) {
                switch(progress.getStatus()){
                    case KiiFileProgress.STATUS_START_UPDATE_META:
                        ShowInfo.updateProgressText("Start to upload metadata...");
                        break;
                    case KiiFileProgress.STATUS_START_UPDATE_BODY:
                        ShowInfo.updateProgressText("Start to upload file body...");
                        break;
                    case KiiFileProgress.STATUS_UPDATING_BODY:
                        long totalsize = progress.getTotalSize();
                        long currentsize = progress.getCurrentSize();
                        String msg = "Uploading file body :" + currentsize +"/" + totalsize;
                        ShowInfo.updateProgressText(msg);
                        break;
                }
            }
        });
        return token;
    }

    /**
     * Move a working file to trash
     */
    public static int asyncMoveToTrash(final WorkingFilesListActivity activity,
            final int position) {
        KiiFile f = mWorkingFiles.get(position);
        int token = f.moveToTrash(new KiiFileCallBack() {

        	// implement the call back on move to trash completed
            @Override
            public void onMoveTrashCompleted(int token, boolean success,
                    KiiFile file, Exception exception) {
            	// close progress dialog
                ShowInfo.closeProgressDialog();
                if (success) {
                	// move the file from working file cache to trashed file cache
                    mWorkingFiles.remove(position);
                    mTrashedFiles.add(0, file);                    
                    // update the UI
                    activity.rebuildTitle(getWorkingFileTitles());
                } else {
                	// show error message
                    ShowInfo.showException(activity, exception);
                }
            }
        });
        return token;
    }

    /**
     * Restore a trashed file 
     */
    public static int asyncRestoreFile(final TrashFilesListActivity activity,
            final int position) {
        KiiFile f = mTrashedFiles.get(position);
        int token = f.restoreFromTrash(new KiiFileCallBack() {
        	// implement the call back on restore trashed file completed
            @Override
            public void onRestoreTrashCompleted(int token, boolean success,
                    KiiFile file, Exception exception) {
            	// close progress dialog
                ShowInfo.closeProgressDialog();
                if (success) {
                	// move the trashed file to working file cache
                    mTrashedFiles.remove(position);
                    mWorkingFiles.add(0, file);
                    // update the UI
                    activity.rebuildTitle(getTrashFileTitles());
                } else {
                	// show error message
                    ShowInfo.showException(activity, exception);
                }
            }
        });
        return token;
    }

    /**
     * Download working file Kii Cloud  
     */
    public static int asyncDownloadFile(
            final WorkingFilesListActivity activity, final int position,
            final String filename) {
        KiiFile f = mWorkingFiles.get(position);
        int token = f.downloadFileBody(new KiiFileCallBack() {

        	// implement the call back on download completed
            @Override
            public void onDownloadBodyCompleted(int token, boolean success,
                    Exception exception) {
            	// close progress dialog
                ShowInfo.closeProgressDialog();
                if (success) {
                	// show a dialog to indicate that download is successful
                    AlertDialog dialog = new AlertDialog.Builder(activity)
                            .setTitle(
                                    "The file have been downloaded successful!")
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int whichButton) {
                                            /* User clicked OK so do some stuff */
                                        }
                                    }).create();
                    dialog.show();
                } else {
                	// show error message
                    ShowInfo.showException(activity, exception);
                }
            }
            
            // implement the call back to publish the downnload progress
            @Override
            public void onProgressUpdate(int token, KiiFileProgress progress) {
                switch(progress.getStatus()){
                    case KiiFileProgress.STATUS_START_DOWNLOAD_BODY:
                        ShowInfo.updateProgressText("Start to download file...");
                        break;
                    case KiiFileProgress.STATUS_DOWNLOADING_BODY:
                        long totalsize = progress.getTotalSize();
                        long currentsize = progress.getCurrentSize();
                        String msg = "Downloading file body :" + currentsize +"/" + totalsize;
                        ShowInfo.updateProgressText(msg);
                        break;
                }
            }

        }, filename);

        return token;
    }

    /**
     * Update KiiFile custom field
     */
    public static int asyncUpdateFileCustomField(
            final WorkingFilesListActivity activity, final int position,
            final String custom) {
        KiiFile f = mWorkingFiles.get(position);
        // set value for custom field
        f.setCustomeField(custom);
        int token = f.updateMetaData(new KiiFileCallBack() {

        	// implement the call back on update completed
            @Override
            public void onUpdateCompleted(int token, boolean success,
                    KiiFile file, Exception exception) {
            	// close progress dialog
                ShowInfo.closeProgressDialog();
                if (success) {
                	// update cache
                    mWorkingFiles.set(position, file);
                    // update UI
                    activity.rebuildTitle(getWorkingFileTitles());
                } else {
                	// show error message
                    ShowInfo.showException(activity, exception);
                }
            }
        });
        return token;
    }

    /**
     * Update KiiFile content/body 
     */
    public static int asyncUpdateFile(final WorkingFilesListActivity activity,
            final int position, String filename) {
        KiiFile f = mWorkingFiles.get(position);
        File update = new File(filename);
        int token = f.update(new KiiFileCallBack(){
        	// implement the call back on update completed
            @Override
            public void onUpdateCompleted(int token, boolean success,
                    KiiFile file, Exception exception) {
                ShowInfo.closeProgressDialog();
                if (success) {
                	// update cache
                    mWorkingFiles.set(position, file);
                    // update UI
                    activity.rebuildTitle(getWorkingFileTitles());
                } else {
                	// show error message
                    ShowInfo.showException(activity, exception);
                }
            }
            
            // implement the call back to publish the download progress
            @Override
            public void onProgressUpdate(int token, KiiFileProgress progress) {
                switch(progress.getStatus()){
                    case KiiFileProgress.STATUS_START_UPDATE_META:
                        ShowInfo.updateProgressText("Start to update metadata...");
                        break;
                    case KiiFileProgress.STATUS_START_UPDATE_BODY:
                        ShowInfo.updateProgressText("Start to update file body...");
                        break;
                    case KiiFileProgress.STATUS_UPDATING_BODY:
                        long totalsize = progress.getTotalSize();
                        long currentsize = progress.getCurrentSize();
                        String msg = "Updating file body :" + currentsize +"/" + totalsize;
                        ShowInfo.updateProgressText(msg);
                        break;
                }
            }
        }, update);
        return token;
    }

    /**
     * get list of working files from cache
     */
    public static String[] getWorkingFileTitles() {
        if (mWorkingFiles == null) {
            return null;
        }
        int length = mWorkingFiles.size();
        String[] res = new String[length];
        for (int i = 0; i < length; i++) {
            res[i] = mWorkingFiles.get(i).getTitle();
        }

        return res;
    }

    /**
     * get list of trashed files from cache
     */
    public static String[] getTrashFileTitles() {
        if (mTrashedFiles == null) {
            return null;
        }
        int length = mTrashedFiles.size();
        String[] res = new String[length];
        for (int i = 0; i < length; i++) {
            res[i] = mTrashedFiles.get(i).getTitle();
        }

        return res;
    }

}
