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

package com.kii.demo.cloudstorage.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kii.cloud.storage.KiiClient;
import com.kii.cloud.storage.exception.CloudExecutionException;
import com.kii.demo.cloudstorage.R;

public class ShowInfo {
    private static AlertDialog mProgressDialog;
    private static TextView mProgressTextView;
    
    public static  void showProcessing(final Activity activity, final int token, String process_title) {
        closeProgressDialog();

        LayoutInflater factory = LayoutInflater.from(activity);

        final View processView = factory.inflate(R.layout.processing, null);
        mProgressTextView = (TextView) processView
                .findViewById(R.id.process_text);
        mProgressTextView.setText(process_title);

        mProgressDialog = new AlertDialog.Builder(activity)
                .setIcon(R.drawable.alert_dialog_icon)
                .setTitle("Progressing")
                .setView(processView)
                .setPositiveButton(R.string.alert_dialog_background,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {

                            }
                        })
                .setNegativeButton(R.string.alert_dialog_cancel_task,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {

                                KiiClient.cancelTask(token);
                                Toast.makeText(activity,
                                        "Task has been canceled",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }).create();

        mProgressDialog.show();
    }

    
    public static void updateProgressText(String text){
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressTextView.setText(text);
        }
    }
    public static void closeProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public static void showException(Activity activity, Exception exception) {
        String title = "Exception";
        String msg = exception.getMessage();
        
        if(exception instanceof CloudExecutionException){
            CloudExecutionException cloudException = (CloudExecutionException)exception;
            StringBuilder sb = new StringBuilder();
            sb.append("Error:" + cloudException.getError());
            sb.append("\n\n");
            sb.append("Exception:" + cloudException.getException());
            sb.append("\n\n");
            sb.append("Error Details:" + cloudException.getErrorDetails());
            msg = sb.toString();
            
        }
        
        AlertDialog dialog = new AlertDialog.Builder(activity)
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

    public static void showSuccess(Activity activity, String msg) {

        String title = "Success";
//        AlertDialog dialog = new AlertDialog.Builder(activity)
//                .setIcon(R.drawable.alert_dialog_icon)
//                .setTitle(title)
//                .setMessage(msg)
//                .setPositiveButton(R.string.alert_dialog_ok,
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog,
//                                    int whichButton) {
//
//                                /* User clicked OK so do some stuff */
//                            }
//                        }).create();
//
//        dialog.show();
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();

    }

    public static void showError(Activity activity, String msg) {
        String title = "Error";
        AlertDialog dialog = new AlertDialog.Builder(activity)
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
