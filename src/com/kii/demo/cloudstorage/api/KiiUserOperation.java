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

import android.app.Activity;

import com.kii.cloud.storage.KiiClient;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.callback.KiiUserCallBack;
import com.kii.demo.cloudstorage.activities.ConstantValues;
import com.kii.demo.cloudstorage.activities.ShowInfo;
import com.kii.demo.cloudstorage.activities.StartActivity;
import com.kii.demo.cloudstorage.activities.UserOperationActivity;

public class KiiUserOperation {
    static {
    	// Initialize KiiClient SDK with Application ID and Key
        KiiClient.initialize(AppInfo.APP_ID, AppInfo.APP_KEY,AppInfo.BASE_URL);
    }

    /**
     * Create Application User (Synchronous Method)
     */
    public static void createUser(StartActivity activity,
            String username, String pwd, String email, String phone) {
        try {
        	// create empty Kii User
            KiiUser user = new KiiUser();
            // Set email address and it must be unique
            user.setEmail(email);
            // Set phone number
            user.set(ConstantValues.FIELD_USER_PHONE, phone);
            // register user with Kii Cloud
            user.register(username, pwd);
            // show registered user 
            showUserResult(activity, user);
        } catch (Exception e) {
        	// show error message
            ShowInfo.showException(activity, e);
        }
    }

    /**
     * User LogIn (Synchronous Method)
     */
    public static void userLogin(StartActivity activity, String username,
            String pwd) {
        try {
        	// LogIn using given user credential  
            KiiUser.logIn(username, pwd);
            // upon successful user as will be cached
            showUserResult(activity, KiiClient.getCurrentUser());
        } catch (Exception e) {
        	// show error message
            ShowInfo.showException(activity, e);
        }
        activity.updateUserStateTitle();
    }

    /**
     * Get the URL which show web page for reset password
     */
    public static String getResetPasswordUrl() {
        return KiiUser.getResetPasswordUrl();
    }

    /**
     * Update user phone number only (Synchronous Method)
     */
    public static void updateUserPhone(UserOperationActivity activity,
            String phone) {
    	// get current user
        KiiUser user = KiiClient.getCurrentUser();

        try {
        	// set phone number
            user.set(ConstantValues.FIELD_USER_PHONE, phone);
            // update user info to Kii Cloud
            user.update();
            // show user profile 
            showUserResult(activity, user);
        } catch (Exception e) {
        	// show error message
            ShowInfo.showException(activity, e);
        }
    }
    
    /**
     * Update user date of birth only (Synchronous Method)
     */
    public static void updateUserBirthday(UserOperationActivity activity, long time) {
    	// get current user
        KiiUser user = KiiClient.getCurrentUser();

        try {
        	// set date of birth
            user.set(ConstantValues.FIELD_USER_BOD, time);
            // update user info to Kii Cloud
            user.update();
            // show user profile
            showUserResult(activity, user);
        } catch (Exception e) {
        	// show error message        	
            ShowInfo.showException(activity, e);
        }

    }
    
    /**
     * Change user password  (Synchronous Method)
     */
    public static void changePwd(UserOperationActivity activity, String old_pwd, String new_pwd) {

        try {
        	// Get current password
            KiiUser user = KiiClient.getCurrentUser();
            // change password
            user.changePassword(new_pwd, old_pwd);
            // show user profile
            showUserResult(activity, user);
        } catch (Exception e) {
        	// show error message
            ShowInfo.showException(activity, e);
        }

    }

    /**
     * Create Application User (Asynchronous Method)
     */
    public static int asyncCreateUser(final StartActivity activity,
    		String username, String pwd, String email, String phone) {
    	
    	// create empty Kii User
        KiiUser user = new KiiUser();
        // Set email address
        user.setEmail(email);
        // Set phone number
        user.set(ConstantValues.FIELD_USER_PHONE, phone);

    	// Register user with Kii Cloud using asychronous method
        int token = user.register(new KiiUserCallBack() {

        	// implement the callback on register completed
            @Override
            public void onRegisterCompleted(int token, boolean success,
                    KiiUser user, Exception exception) {
            	// close progress dialog
                ShowInfo.closeProgressDialog();
                if (success) {
                	// show registered user 
                    showUserResult(activity, user);
                } else {
                	// show error
                    ShowInfo.showException(activity, exception);
                }
            }

        }, username, pwd);

        // return the token for asynchronous task
        return token;
    }

    /**
     * User LogIn (Asynchronous Method)
     */
    public static int asyncUserLogin(final StartActivity activity,
            String username, String pwd) {
    	// LogIn using given user credential  
        int token = KiiUser.logIn(new KiiUserCallBack() {

            // implement the callback on logIn completed
        	@Override
            public void onLoginCompleted(int token, boolean success,
                    KiiUser user, Exception exception) {
        		// Close progress dialog
        		ShowInfo.closeProgressDialog();
                if (success) {
                	// show login user
                    showUserResult(activity, user);
                    activity.updateUserStateTitle();
                } else {
                	// show error
                    ShowInfo.showException(activity, exception);
                }
            }
        }, username, pwd);

        // return the token for asynchronous task
        return token;
    }

    /**
     * Update user phone number only  (Asynchronous Method)
     */
    public static int asyncUpdateUserPhone(final UserOperationActivity activity,
            String phone) {
    	// get current user
        KiiUser user = KiiClient.getCurrentUser();

        // set user phone number
        user.set(ConstantValues.FIELD_USER_PHONE, phone);

        // update user info to Kii Cloud
        int token = user.update(new KiiUserCallBack(){
        	// implement the call back on update completed
            @Override
            public void onUpdateCompleted(int token, boolean success, KiiUser user,
                    Exception exception) {
            	// Close progress dialog
                ShowInfo.closeProgressDialog();
                if (success) {
                	// show user profile
                    showUserResult(activity, user);
                    activity.updateUserStateTitle();
                } else {
                	// show error message
                    ShowInfo.showException(activity, exception);
                }
            }
        });
        
        // return the token for asynchronous task
        return token;
    }

    /**
     * Update user date of birth only (Asynchronous Method)
     */
    public static int asyncUpdateUserBirthday(final UserOperationActivity activity, long time) {
    	
    	// get current user
        KiiUser user = KiiClient.getCurrentUser();
        
        // set date of birth
        user.set(ConstantValues.FIELD_USER_BOD, time);

        // update user info to Kii Cloud
        int token = user.update(new KiiUserCallBack(){

        	// implement the call back on update completed 
            @Override
            public void onUpdateCompleted(int token, boolean success,
                    KiiUser user, Exception exception) {
            	// Close progress dialog
                ShowInfo.closeProgressDialog();
                if (success) {
                	// show user profile
                    showUserResult(activity,  user);
                    activity.updateUserStateTitle();
                } else {
                	// show error message
                    ShowInfo.showException(activity, exception);
                }
            }
            
        });
       
        // return the token for asynchronous task
        return token;
    }

    /**
     * Change password (Asynchronous Method)
     */
    public static int asyncChangePwd(final UserOperationActivity activity, String old_pwd, String new_pwd) {
        int token = KiiClient.getCurrentUser().changePassword(new KiiUserCallBack(){

        	// implement the call back on change password completed 
            @Override
            public void onChangePasswordCompleted(int token, boolean success,
                    Exception exception) {
            	// Close progress dialog
                ShowInfo.closeProgressDialog();
                if (success) {
                	// show user profile
                    showUserResult(activity, KiiClient.getCurrentUser());
                } else {
                	// show error message
                    ShowInfo.showException(activity, exception);
                }
            }
            
        }, new_pwd, old_pwd);

     // return the token for asynchronous task
        return token;
    }

    /**
     * display user profile
     */
    public static void showUserResult(Activity activity, KiiUser user) {
        StringBuilder show = new StringBuilder();
        show.append("Kii User Uri: " + user.toUri());
        show.append("\n");
        show.append("Details: " + user.toString());
        ShowInfo.showSuccess(activity, "user operation successful");
    }
}
