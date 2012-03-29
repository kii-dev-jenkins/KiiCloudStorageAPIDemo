package com.kii.demo.cloudstorage.activities;

public class ConstantValues {

    public static boolean mSyncMode = false;

    
    public static final String DEMO_FILE = "/mnt/sdcard/download/images.jpeg";
    public static final String VIRTUAL_ROOT = "myroot";
    public static final String TARGET_FILE_NAME = "/mnt/sdcard/download/downloaded_file";

    public static final String[] ITEMS = { "Create User", // 0
            "Login", // 1
            "Logout", // 2
            "User operations...", //3
            "Note operations...", // 4
            "File operations...", // 5

    };
    
    public static final String[] USER_ITEMS = {
        "Update User Phone", // 0
        "Set User Birthday", // 1
        "Show User Info", // 2
        "Forgot Password", // 3
        "Change Password", // 4
    };

    public static final String[] FILE_ITEMS = { "Upload a file", // 0
            "List files",// 1
            "Get File Info",// 2
            "Get File Body",// 3
            "Delete a file",// 4
            "Restore a file",// 5
            "Empty trashcan",// 6
            "List files in trashcan",// 7
            // "Search for a file",// 8
            "Update File Info"// 9

    };

    public static final int ITEM_CREATE_USER = 0;
    public static final int ITEM_LOGIN = 1;
    public static final int ITEM_LOGOUT = 2;
    public static final int ITEM_OPERATION_USER = 3;
    public static final int ITEM_OPERATION_NOTES = 4;
    public static final int ITEM_OPERATON_FILES = 5;
    
    public static final int ITEM_USER_SET_PHONE = 0;
    public static final int ITEM_USER_SET_BIRTHDAY = 1;
    public static final int ITEM_USER_SHOW_INFO = 2;
    public static final int ITEM_USER_FORGOT_PWD = 3;
    public static final int ITEM_USER_CHANGE_PWD = 4;
    

    public static final int ITEM_FILE_UPLOAD = 0;
    public static final int ITEM_FILE_LIST = 1;

    public static final int ITEM_FILE_INFO = 2;
    public static final int ITEM_FILE_BODY = 3;

    public static final int ITEM_FILE_DELETE = 4;
    public static final int ITEM_FILE_RESTORE = 5;

    public static final int ITEM_FILE_EMPTY_TRASH = 6;
    public static final int ITEM_FILE_LIST_TRASH = 7;
    // not in spec
    // public static final int ITEM_FILE_SEARCH = 8;
    public static final int ITEM_FILE_UPDATE = 8;

    public static final String FIELD_USER_PHONE = "phone";
    public static final String FIELD_USER_BOD = "birthday";

    public static final String DEFAULT_USER = "kiitest03";
    public static final String DEFAULT_EMAIL = "kiitest03@testkii.com";
    public static final String DEFAULT_PHONE = "18601183592";
    public static final String DEFAULT_PWD = "123456";

    public static final String DEFAULT_NOTE_TITLE = "My Note 01";
    public static final String DEFAULT_NOTE_CONTENT = "Hello! \nThis is a test note! ";

    public static final String DEFAULT_NOTE_TITLE_QUERY = "My Note 01";
    public static final String DEFAULT_NOTE_CONTENT_QUERY = "";
    
    public static final int DEFAULT_NOTE_QUERY_LIMIT = 0;

    public static final String EXTRA_NOTES = "notes";
    public static final String EXTRA_LIMIT = "limit";

    public static final String FIELDNAME_CREATOR = "creator";

    public static final String KII_OBJECT_NAME = "test_note2";

    public static final String KII_FILE_VIRTUAL_ROOT = "test";

}
