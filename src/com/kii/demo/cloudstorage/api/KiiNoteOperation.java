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

import java.util.List;

import android.widget.Toast;

import com.kii.cloud.storage.KiiClient;
import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.callback.KiiObjectCallBack;
import com.kii.cloud.storage.query.KiiQuery;
import com.kii.cloud.storage.query.KiiQueryResult;
import com.kii.demo.cloudstorage.activities.ConstantValues;
import com.kii.demo.cloudstorage.activities.ShowInfo;
import com.kii.demo.cloudstorage.notepad.NoteItemListActivity;

public class KiiNoteOperation {

	// Note object class name    
    public static final String KII_OBJECT_TYPE = "demo_note";

	// Key's name for note's title
    public final static String PROPERTY_TITLE = "title";
	// Key's name for note's content
    public final static String PROPERTY_CONTENT = "content";
   
    // cache note objects
    public static List<KiiObject> mObjects;
    // indicate if there is more result on Kii Cloud 
    public static boolean isMore;
    // KiiQuery for retrieving next batch of result on Kii Cloud
    private static KiiQuery mNextQuery;

    static {
    	// Initialize KiiClient SDK with Application ID and Key
        KiiClient.initialize(AppInfo.APP_ID, AppInfo.APP_KEY, AppInfo.BASE_URL);
    }
    
    /**
     * Get the list notes by title only 
     */
    private static String[] getTitles(){
        int length = mObjects.size();
        String[] res = new String[isMore?length + 1:length];
        for(int i = 0; i< length; i++){
            res[i] = mObjects.get(i).getString(PROPERTY_TITLE, "NO TITLE");
        }
        
        if(isMore){
            res[length] = "More...";
        }
        return res;
    }


    /**
     * List all the notes from Kii Cloud
     */
    public static int asyncGetAllNote(final NoteItemListActivity activity) {
    	// List all the notes by query with KiiQuery is null
        int token = KiiObject.query(new KiiObjectCallBack(){

        	// implement the call back on query completed
            @Override
            public void onQueryCompleted(int token, boolean success,
                    KiiQueryResult<KiiObject> objects, Exception exception) {
            	// close the progress dialog
                ShowInfo.closeProgressDialog();
                if(success){
                	// get the list of objects
                    mObjects = objects.getResult();
                    // update the UI with updated note's title
                    activity.rebuildTitle(getTitles());
                    // no more result on the Kii Cloud
                    isMore = false;
                } else{
                	// show error message
                    ShowInfo.showException(activity, exception);
                }
            }
            
        }, KII_OBJECT_TYPE, null);
                

        return token;
    }
    
    /**
     * Query Kii Cloud for the list of note that matches KiiQuery
     */
    public static int asyncQueryNote(final NoteItemListActivity activity, KiiQuery query) {
    	// query Kii Query
        int token = KiiObject.query(new KiiObjectCallBack(){

        	// implement the call back on query completed
        	@Override
            public void onQueryCompleted(int token, boolean success,
                    KiiQueryResult<KiiObject> objects, Exception exception) {
        		// close the progress dialog
                ShowInfo.closeProgressDialog();
                
                if(success){
                	// get the list of objects 
                    mObjects = objects.getResult();
                    // check if any more note on KiiCloud
                    isMore = objects.hasNext();
                    // get the KiiQuery for next batch
                    mNextQuery = objects.getNextKiiQuery();
                    // update the UI with updated note's title
                    activity.rebuildTitle(getTitles());

                    // show toast message if no result
                    if(mObjects.size() == 0){
                        Toast.makeText(activity,
                                "no result", Toast.LENGTH_SHORT).show();
                    }
                } else{
                	// show error message
                    ShowInfo.showException(activity, exception);
                }
            }
            
        }, KII_OBJECT_TYPE, query);

        return token;

    }
    
    /**
     * Query Kii Cloud for the list of note that matches previous KiiQuery
     */
    public static int asyncQueryMore(final NoteItemListActivity activity) {
        int token = KiiObject.query(new KiiObjectCallBack(){

            @Override
            public void onQueryCompleted(int token, boolean success,
                    KiiQueryResult<KiiObject> objects, Exception exception) {
        		// close the progress dialog
                ShowInfo.closeProgressDialog();
                
                if(success){
                	// add the objects to the existing list
                    mObjects.addAll(objects.getResult());
                    // check if any more note on KiiCloud
                    isMore = objects.hasNext();
                    // get the KiiQuery for next batch
                    mNextQuery = objects.getNextKiiQuery();
                    // update the UI with updated note's title
                    activity.rebuildTitle(getTitles());
                    // show toast message if no result
                    if(mObjects.size() == 0){
                        Toast.makeText(activity,
                                "no result", Toast.LENGTH_SHORT).show();
                    }
                } else{
                    ShowInfo.showException(activity, exception);
                }
            }
            
        }, KII_OBJECT_TYPE, mNextQuery);

        return token;
        
    }

    /**
     * Update the note's content
     */
    public static int asyncUpdateNote(final NoteItemListActivity activity, int postion, String content) {
        // get the note 
    	KiiObject object = mObjects.get(postion);
    	// set the content of the note
        object.set(PROPERTY_CONTENT, content);
        
        // save the updated note to Kii Cloud
        int token = object.save(new KiiObjectCallBack(){

        	// implement the call back on save completed
            @Override
            public void onSaveCompleted(int token, boolean success,
                    KiiObject object, Exception exception) {
            	// close progress dialog
                ShowInfo.closeProgressDialog();
                
                if(success){
                	// show operation is successful
                    Toast.makeText(activity,
                            "Note is saved.", Toast.LENGTH_SHORT).show();
                } else{
                	// show error message
                    ShowInfo.showException(activity, exception);
                }
            }
            
        });

        return token;
    }

    /**
     * Delete the note
     */
    public static int asyncDeleteNote(final NoteItemListActivity activity, final int position) {
        
        // get the note
    	KiiObject object = mObjects.get(position);
    	// delete the note from Kii Cloud
        int token = object.delete(new KiiObjectCallBack(){

        	// implement the call back on delete completed
            @Override
            public void onDeleteCompleted(int token, boolean success,
                    Exception exception) {
                if(success){
                    if(position > -1){
                    	// remove the note from the cache
                        mObjects.remove(position);
                        // update the UI with updated note's title
                        activity.rebuildTitle(getTitles());
                    }
                }else{
                	// show error message
                    ShowInfo.showException(activity, exception);
                }
            }
            
        });
                
        return token;
    }

    /**
     * Create a note
     */
    public static int asyncCreateNote(final NoteItemListActivity activity, String title, String content) {

        // Create an empty object
    	KiiObject newobject = new KiiObject(KII_OBJECT_TYPE);
    	// set the title of the note
    	newobject.set(PROPERTY_TITLE, title);
    	// set the content of the note
        newobject.set(PROPERTY_CONTENT, content);
        // set the creator reference
        newobject.set(ConstantValues.FIELDNAME_CREATOR, KiiClient.getCurrentUser().toUri().toString());

        // save the KiiObject Kii Cloud
        int token = newobject.save(new KiiObjectCallBack(){

        	// implement call back on save completed
            @Override
            public void onSaveCompleted(int token, boolean success,
                    KiiObject object, Exception exception) {
            	// cloe progress dialog
                ShowInfo.closeProgressDialog();
                
                if(success){
                	// add the newly created to the list of note
                    mObjects.add(0, object);
                    // update the UI with updated note's title
                    activity.rebuildTitle(getTitles());
                } else{
                	// show error message
                    ShowInfo.showException(activity, exception);
                }
            }
            
        });
        return token;
    }
    
}
