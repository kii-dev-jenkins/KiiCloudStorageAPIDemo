# Kii Cloud Storage API Demos #



A variety of small applications that demonstrate the use of Kii Cloud Storage SDK. It includes samples of:

- Application User Management
- Object Management
- File Management

![Screen shots](https://github.com/kii-dev-jenkins/KiiCloudStorageAPIDemo/raw/master/doc/screen_shots.jpg)

## Application User Management ##

- Create and update with additional fields (Date of Birth and mobile number)
- LogIn
- Change password
- Both Synchronous and Asynchronous Methods

*Refer to [KiiUserOperation class](https://github.com/kii-dev-jenkins/KiiCloudStorageAPIDemo/blob/master/src/com/kii/demo/cloudstorage/api/KiiUserOperation.java)*


## Object Management ##

**Note Sample Application** which demonstrates how to use Object Storage to store note.

- Object ClassName is *test_note2*
- Fields are title, content and creator
- Create, update, delete and view note
- List all notes
- Query notes by title, content and owner
- Retrieve the query results by batch
- Asynchronous Methods 

*Refer to [KiiUNoteOperation class](https://github.com/kii-dev-jenkins/KiiCloudStorageAPIDemo/blob/master/src/com/kii/demo/cloudstorage/api/KiiNoteOperation.java)*


## File Management ##

**Backup File Sample Application** which demonstrates how to backup local files and download files.

- Container name is *apidemo*
- Upload local file with customized metadata
- Download, update, delete and trash a file in KiiCloud
- List all working files in Kii Cloud
- List all trashed files in Kii Cloud
- Asynchronous Methods
- Showing upload and download progress

*Refer to [KiiFileOperation class](https://github.com/kii-dev-jenkins/KiiCloudStorageAPIDemo/blob/master/src/com/kii/demo/cloudstorage/api/KiiFileOperation.java)*

----------

**Additional Notes**

- Update the Sample app with your own application ID and application key at com.kii.demo.cloudstorage.api.AppInfo class. 

- The SDK javadoc is not automtatic visible in your eclipse project, you need to attach the JavaDoc(lcoated in folder doc) to the Kii Cloud SDK jar file(located in folder lib). 


- [Java API documenatation](http://static.kii.com/devportal/production/docs/storage/)
