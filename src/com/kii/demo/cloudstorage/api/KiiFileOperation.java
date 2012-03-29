package com.kii.demo.cloudstorage.api;

import java.io.File;
import java.util.List;

import com.kii.cloud.storage.KiiClient;
import com.kii.cloud.storage.KiiFile;
import com.kii.cloud.storage.callback.KiiFileCallBack;
import com.kii.demo.cloudstorage.activities.ShowInfo;
import com.kii.demo.cloudstorage.file.TrashFilesListActivity;
import com.kii.demo.cloudstorage.file.WorkingFilesListActivity;

public class KiiFileOperation {
    private static final String FILE_CONTAINER = "apidemo";

    static {
        KiiClient.initialize(AppInfo.APP_ID, AppInfo.APP_KEY);
    }

    private static List<KiiFile> mWorkingFiles;
    private static List<KiiFile> mTrashFiles;

    public static int asyncListWorkingFiles(
            final WorkingFilesListActivity activity) {
        int token = KiiFile.listWorkingFiles(new KiiFileCallBack() {

            @Override
            public void onListWorkingCompleted(int mToken, boolean success,
                    List<KiiFile> files, Exception exception) {
                ShowInfo.closeProgressDialog();
                if (success) {
                    mWorkingFiles = files;
                    activity.rebuildTitle(getWorkingFileTitles());
                } else {
                    ShowInfo.showException(activity, exception);
                }
            }

        }, FILE_CONTAINER);

        return token;
    }
    
    public static int asyncListTrashFiles(
            final TrashFilesListActivity activity) {
        int token = KiiFile.listTrashedFiles(new KiiFileCallBack() {

            @Override
            public void onListTrashCompleted(int mToken, boolean success,
                    List<KiiFile> files, Exception exception) {
                ShowInfo.closeProgressDialog();
                if (success) {
                    mTrashFiles = files;
                    activity.rebuildTitle(getTrashFileTitles());
                } else {
                    ShowInfo.showException(activity, exception);
                }
            }

        }, FILE_CONTAINER);

        return token;
    }
    
    public static int asyncUploadFile(final WorkingFilesListActivity activity, String filename){
        KiiFile f = new KiiFile(new File(filename), FILE_CONTAINER);
        int token = f.upload(new KiiFileCallBack(){
            @Override
            public void onUploadCompleted(int token, boolean success,
                    KiiFile file, Exception exception) {
                ShowInfo.closeProgressDialog();
                if(success){
                    mWorkingFiles.add(0, file);
                    activity.rebuildTitle(getWorkingFileTitles());
                } else{
                    ShowInfo.showException(activity, exception);
                }
            }
            
        });
        
        return token;
    }
    
    public static int asyncMoveToTrash(final WorkingFilesListActivity activity, final int position){
        KiiFile f = mWorkingFiles.get(position);
        int token = f.moveToTrash(new KiiFileCallBack(){

            @Override
            public void onMoveTrashCompleted(int token, boolean success,
                    KiiFile file, Exception exception) {
                ShowInfo.closeProgressDialog();
                if(success){
                    mWorkingFiles.remove(position);
                    activity.rebuildTitle(getWorkingFileTitles());
                } else {
                    ShowInfo.showException(activity, exception);
                }
            }
            
        });
        
        return token;
    }

    private static String[] getWorkingFileTitles() {
        int length = mWorkingFiles.size();
        String[] res = new String[length];
        for (int i = 0; i < length; i++) {
            res[i] = mWorkingFiles.get(i).getTitle();
        }

        return res;
    }
    
    private static String[] getTrashFileTitles() {
        int length = mTrashFiles.size();
        String[] res = new String[length];
        for (int i = 0; i < length; i++) {
            res[i] = mTrashFiles.get(i).getTitle();
        }

        return res;
    }
}
